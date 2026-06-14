package ui;

import model.*;
import dao.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThanhToanDialog extends JDialog {
    private boolean paid = false;
    private JTextField txtTienDoAn;
    private JLabel lblTongTruocGiam, lblGiamGia, lblTongTien;
    private double giaGio, tienBida, tienSanPham, tongBill, tongTruocGiam, tienGiam;
    private int phanTramGiam;
    private long minutes;
    private BanBida ban;
    private PhienChoi phien;
    
    private JComboBox<String> cbKhachHang, cbNhanVien, cbGiamGia;

    public ThanhToanDialog(Frame owner, BanBida ban, PhienChoi phien) {
        super(owner, "Thanh Toán - " + ban.getTenBan(), true);
        this.ban = ban; this.phien = phien;
        
        setSize(500, 680); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        LocalDateTime start = phien.getThoiGianBatDau();
        LocalDateTime end = LocalDateTime.now();
        minutes = Math.max(1, Duration.between(start, end).toMinutes()); 
        
        giaGio = ban.getLoaiBan().equals("VIP") ? 80000 : 50000;
        tienBida = (minutes / 60.0) * giaGio;

        ChiTietPhienDAO ctpDao = new ChiTietPhienDAO();
        tienSanPham = ctpDao.tinhTongTienTheoPhien(phien.getMaPhien());
        tongTruocGiam = tienBida + tienSanPham;
        tongBill = tongTruocGiam;

        JPanel content = new JPanel(new GridLayout(0, 1, 10, 15));
        content.setBorder(new EmptyBorder(25, 40, 25, 40));
        content.setBackground(Color.WHITE);

        JLabel lblHeader = new JLabel("Chi tiết sử dụng dịch vụ");
        lblHeader.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        content.add(lblHeader);

        content.add(new JLabel("Giờ bắt đầu: " + start.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
        content.add(new JLabel("Giờ kết thúc: " + end.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
        content.add(new JLabel("Tổng thời gian: " + minutes + " phút"));
        content.add(new JLabel("Thành tiền Bida: " + String.format("%,.0f VNĐ", tienBida)));

        content.add(new JLabel("Tiền Đồ ăn / Thức uống:"));
        txtTienDoAn = LuxuryTheme.createTextField();
        txtTienDoAn.setText(String.format("%,.0f VNĐ", tienSanPham));
        txtTienDoAn.setEditable(false); txtTienDoAn.setForeground(Color.RED);
        content.add(txtTienDoAn);

        content.add(new JLabel("Khách Hàng:"));
        cbKhachHang = new JComboBox<>(); cbKhachHang.setBackground(Color.WHITE); content.add(cbKhachHang);

        content.add(new JLabel("Nhân Viên:"));
        cbNhanVien = new JComboBox<>(); cbNhanVien.setBackground(Color.WHITE); content.add(cbNhanVien);

        content.add(new JLabel("Giảm giá:"));
        cbGiamGia = new JComboBox<>();
        cbGiamGia.setBackground(Color.WHITE);
        for (int i = 0; i <= 100; i += 10) cbGiamGia.addItem(i + "%");
        cbGiamGia.addActionListener(e -> capNhatTongTien());
        content.add(cbGiamGia);

        loadDataToComboBox();
        cbKhachHang.addActionListener(e -> tuDongChonGiamGiaTheoKhachHang());
        tuDongChonGiamGiaTheoKhachHang();

        lblTongTruocGiam = new JLabel();
        lblTongTruocGiam.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        lblTongTruocGiam.setForeground(Color.DARK_GRAY);
        content.add(lblTongTruocGiam);

        lblGiamGia = new JLabel();
        lblGiamGia.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
        lblGiamGia.setForeground(new Color(192, 57, 43));
        content.add(lblGiamGia);

        lblTongTien = new JLabel();
        lblTongTien.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lblTongTien.setForeground(LuxuryTheme.NAVY);
        content.add(lblTongTien);
        capNhatTongTien();

        JButton btnThanhToan = LuxuryTheme.createButton("XÁC NHẬN THANH TOÁN", LuxuryTheme.TEAL, Color.WHITE);
        btnThanhToan.setPreferredSize(new Dimension(0, 50));
        btnThanhToan.addActionListener(e -> xuLyThanhToan(btnThanhToan));

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(btnThanhToan, BorderLayout.SOUTH);
    }

    private void loadDataToComboBox() {
        cbKhachHang.addItem("--- Khách vãng lai ---");
        for (KhachHang kh : new KhachHangDAO().getAllKhachHang()) cbKhachHang.addItem(kh.getMaKH() + " - " + kh.getTenKH());
        for (NhanVien nv : new NhanVienDAO().layTatCaNhanVien()) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
        if(cbNhanVien.getItemCount()==0) cbNhanVien.addItem("--- Trống ---");
    }

    private void tuDongChonGiamGiaTheoKhachHang() {
        int autoDiscount = 0;
        KhachHang kh = getKhachHangDangChon();
        if (kh != null) {
            if (kh.getDiemTichLuy() >= 100) {
                autoDiscount = 20;
            } else if (kh.getDiemTichLuy() >= 50) {
                autoDiscount = 10;
            }
        }
        cbGiamGia.setSelectedItem(autoDiscount + "%");
        capNhatTongTien();
    }

    private KhachHang getKhachHangDangChon() {
        if (cbKhachHang.getSelectedItem() == null || cbKhachHang.getSelectedItem().toString().contains("---")) {
            return null;
        }
        String maKH = cbKhachHang.getSelectedItem().toString().split(" - ")[0];
        return new KhachHangDAO().timTheoMaKhachHang(maKH);
    }

    private void capNhatTongTien() {
        if (cbGiamGia == null || lblTongTien == null) return;
        String value = cbGiamGia.getSelectedItem() == null ? "0%" : cbGiamGia.getSelectedItem().toString();
        phanTramGiam = Integer.parseInt(value.replace("%", ""));
        tienGiam = tongTruocGiam * phanTramGiam / 100.0;
        tongBill = Math.max(0, tongTruocGiam - tienGiam);

        if (lblTongTruocGiam != null) {
            lblTongTruocGiam.setText("Tổng trước giảm: " + String.format("%,.0f VNĐ", tongTruocGiam));
        }
        if (lblGiamGia != null) {
            lblGiamGia.setText("Giảm giá: " + phanTramGiam + "% (-" + String.format("%,.0f VNĐ", tienGiam) + ")");
        }
        lblTongTien.setText("TỔNG THANH TOÁN: " + String.format("%,.0f VNĐ", tongBill));
    }

    private void xuLyThanhToan(JButton btn) {
        if (cbNhanVien.getSelectedItem().toString().contains("Trống")) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm nhân viên vào CSDL trước!", "Lỗi", 2); return;
        }

        btn.setEnabled(false); 
        boolean khachVangLai = cbKhachHang.getSelectedItem().toString().contains("---");
        String maKH = khachVangLai ? null : cbKhachHang.getSelectedItem().toString().split(" - ")[0];
        String maNV = cbNhanVien.getSelectedItem().toString().split(" - ")[0];

        try {
            new PhienChoiDAO().ketThucPhien(phien.getMaPhien(), LocalDateTime.now());
            HoaDonBanDAO hdbDao = new HoaDonBanDAO();
            
            HoaDonBan hdb = hdbDao.layTheoMaPhien(phien.getMaPhien());
            boolean hoaDonMoi = hdb == null;
            if (hdb == null) {
                hdb = hdbDao.taoTuPhien(phien.getMaPhien(), maKH, maNV, tienBida);
                hdb.setTongTien(tongBill);
                hdbDao.them(hdb);
            } else {
                hdb.setTienBida(tienBida);
                hdb.setTienSanPham(tienSanPham);
                hdb.setTongTien(tongBill);
                hdb.setMaKH(maKH);
                hdb.setMaNV(maNV);
                hdbDao.capNhat(hdb);
            }

            ChiTietHoaDonBanDAO ctHdbDao = new ChiTietHoaDonBanDAO();
            ctHdbDao.xoaTheoHoaDon(hdb.getMaHDB()); 
            List<ChiTietPhien> dsCT = new ChiTietPhienDAO().timTheoMaPhien(phien.getMaPhien());
            
            SanPhamDAO spDao = new SanPhamDAO(); // Dùng để trừ kho thật

            for (ChiTietPhien ctPhien : dsCT) {
                ChiTietHoaDonBan ctBan = new ChiTietHoaDonBan();
                ctBan.setMaChiTiet(""); 
                ctBan.setMaHDB(hdb.getMaHDB());
                ctBan.setMaSP(ctPhien.getMaSanPham());
                ctBan.setSoLuong(ctPhien.getSoLuong());
                ctBan.setDonGiaBan(ctPhien.getDonGia());
                
                ctHdbDao.themChiTiet(ctBan); 

                // --- TÍNH TIỀN XONG MỚI TRỪ TỒN KHO THỰC TẾ TRONG DATABASE ---
                spDao.giamTonKho(ctPhien.getMaSanPham(), ctPhien.getSoLuong());
            }

            if (hoaDonMoi && maKH != null) {
                new KhachHangDAO().congDiemTichLuy(maKH, 1);
            }

            String message = "Thanh toán thành công!";
            if (hoaDonMoi && maKH != null) {
                message += "\nKhách hàng được cộng 1 điểm tích lũy.";
            }
            message += "\nBạn có muốn xuất hóa đơn PDF không?";
            int confirm = JOptionPane.showConfirmDialog(this, message, "In Hóa Đơn", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                taoVaLuuPDF(hdb);
            }

            paid = true; dispose();
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            btn.setEnabled(true);
        }
    }

    private void taoVaLuuPDF(HoaDonBan hdb) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu Hóa Đơn PDF");
        fileChooser.setSelectedFile(new File(hdb.getMaHDB() + "_Bill.pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) file = new File(file.getParentFile(), file.getName() + ".pdf");

            try {
                Document document = new Document(PageSize.A5);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font fontTitle = new Font(bf, 16, Font.BOLD);
                Font fontInfo = new Font(bf, 11, Font.NORMAL);
                Font fontNormal = new Font(bf, 12, Font.NORMAL);

                Paragraph brand = new Paragraph("BILLIARDS CLUB CENTER - LUXURY EDITION", fontTitle);
                brand.setAlignment(Element.ALIGN_CENTER); document.add(brand);
                
                Paragraph address = new Paragraph("Đ/c: 123 Đường Cầu Giấy, Q. Cầu Giấy, Hà Nội", fontInfo);
                address.setAlignment(Element.ALIGN_CENTER); document.add(address);
                
                Paragraph phone = new Paragraph("Hotline: 0988.888.888", fontInfo);
                phone.setAlignment(Element.ALIGN_CENTER); document.add(phone);
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));

                document.add(new Paragraph("Mã Hóa Đơn: " + hdb.getMaHDB(), fontNormal));
                document.add(new Paragraph("Bàn chơi: " + ban.getTenBan(), fontNormal));
                document.add(new Paragraph("Nhân viên: " + cbNhanVien.getSelectedItem().toString(), fontNormal));
                document.add(new Paragraph("Ngày: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                
                document.add(new Paragraph(String.format("Tiền Bida (%d phút): %,.0f đ", minutes, tienBida), fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));

                document.add(new Paragraph("Chi tiết gọi đồ:", fontNormal));
                document.add(new Paragraph(" "));
                PdfPTable table = new PdfPTable(3); 
                table.setWidthPercentage(100);
                table.setWidths(new float[]{4f, 1f, 3f});
                table.addCell(new Phrase("Tên món", fontNormal));
                table.addCell(new Phrase("SL", fontNormal));
                table.addCell(new Phrase("Thành tiền", fontNormal));

                List<ChiTietPhien> dsCT = new ChiTietPhienDAO().timTheoMaPhien(phien.getMaPhien());
                SanPhamDAO spDao = new SanPhamDAO();
                for (ChiTietPhien ct : dsCT) {
                    SanPham sp = spDao.layTheoId(ct.getMaSanPham());
                    table.addCell(new Phrase(sp != null ? sp.getTenSP() : ct.getMaSanPham(), fontNormal));
                    table.addCell(new Phrase(String.valueOf(ct.getSoLuong()), fontNormal));
                    table.addCell(new Phrase(String.format("%,.0f", ct.getDonGia() * ct.getSoLuong()), fontNormal));
                }
                document.add(table);
                
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                document.add(new Paragraph("Giảm giá: " + phanTramGiam + "% (-" + String.format("%,.0f VNĐ", tienGiam) + ")", fontNormal));
                Paragraph tong = new Paragraph("Tổng thanh toán: " + String.format("%,.0f VNĐ", tongBill), new Font(bf, 14, Font.BOLD, BaseColor.RED));
                tong.setAlignment(Element.ALIGN_RIGHT); document.add(tong);
                
                Paragraph thanks = new Paragraph("\nXin cảm ơn quý khách và hẹn gặp lại!", fontInfo);
                thanks.setAlignment(Element.ALIGN_CENTER); document.add(thanks);

                document.close();
                Desktop.getDesktop().open(file); 
                
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi in PDF: " + e.getMessage()); }
        }
    }

    public boolean isPaid() { return paid; }
}
