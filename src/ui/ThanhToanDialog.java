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
    private JLabel lblTongTien;
    private double giaGio, tienBida, tienSanPham, tongBill;
    private long minutes;
    private BanBida ban;
    private PhienChoi phien;
    
    private JComboBox<String> cbKhachHang, cbNhanVien;

    public ThanhToanDialog(Frame owner, BanBida ban, PhienChoi phien) {
        super(owner, "Thanh Toán - " + ban.getTenBan(), true);
        this.ban = ban; this.phien = phien;
        
        setSize(500, 680); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- TÍNH TOÁN DỮ LIỆU ---
        LocalDateTime start = phien.getThoiGianBatDau();
        LocalDateTime end = LocalDateTime.now();
        minutes = Math.max(1, Duration.between(start, end).toMinutes()); 
        
        giaGio = ban.getLoaiBan().equals("VIP") ? 80000 : 50000;
        tienBida = (minutes / 60.0) * giaGio;

        ChiTietPhienDAO ctpDao = new ChiTietPhienDAO();
        tienSanPham = ctpDao.tinhTongTienTheoPhien(phien.getMaPhien());
        tongBill = tienBida + tienSanPham;

        // --- GIAO DIỆN ---
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

        loadDataToComboBox();

        lblTongTien = new JLabel("TỔNG CỘNG: " + String.format("%,.0f VNĐ", tongBill));
        lblTongTien.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lblTongTien.setForeground(LuxuryTheme.NAVY);
        content.add(lblTongTien);

        // --- NÚT XÁC NHẬN (1 Nút Duy Nhất) ---
        JButton btnThanhToan = LuxuryTheme.createButton("XÁC NHẬN THANH TOÁN", LuxuryTheme.TEAL, Color.WHITE);
        btnThanhToan.setPreferredSize(new Dimension(0, 50));
        btnThanhToan.addActionListener(e -> xuLyThanhToan(btnThanhToan));

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(btnThanhToan, BorderLayout.SOUTH);
    }

    private void loadDataToComboBox() {
        for (KhachHang kh : new KhachHangDAO().getAllKhachHang()) cbKhachHang.addItem(kh.getMaKH() + " - " + kh.getTenKH());
        for (NhanVien nv : new NhanVienDAO().layTatCaNhanVien()) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
        if(cbKhachHang.getItemCount()==0) cbKhachHang.addItem("--- Trống ---");
        if(cbNhanVien.getItemCount()==0) cbNhanVien.addItem("--- Trống ---");
    }

    private void xuLyThanhToan(JButton btn) {
        if (cbKhachHang.getSelectedItem().toString().contains("Trống") || cbNhanVien.getSelectedItem().toString().contains("Trống")) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm Khách/Nhân viên vào CSDL trước!", "Lỗi", 2); return;
        }

        btn.setEnabled(false); // Khóa nút chống click đúp
        String maKH = cbKhachHang.getSelectedItem().toString().split(" - ")[0];
        String maNV = cbNhanVien.getSelectedItem().toString().split(" - ")[0];

        try {
            // 1. Lưu xuống Database
            new PhienChoiDAO().ketThucPhien(phien.getMaPhien(), LocalDateTime.now());
            HoaDonBanDAO hdbDao = new HoaDonBanDAO();
            HoaDonBan hdb = hdbDao.taoTuPhien(phien.getMaPhien(), maKH, maNV, tienBida);
            hdbDao.them(hdb);

            // 2. Hỏi in PDF
            int confirm = JOptionPane.showConfirmDialog(this, "Thanh toán thành công! Bạn có muốn xuất hóa đơn PDF không?", "In Hóa Đơn", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                taoVaLuuPDF(hdb);
            }

            paid = true; dispose();
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
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

                // NẾU MÁY MAC, BẠN THAY ĐƯỜNG DẪN FONT NHÉ. Dưới đây là Font Windows chuẩn
                BaseFont bf = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font fontTitle = new Font(bf, 16, Font.BOLD);
                Font fontInfo = new Font(bf, 11, Font.NORMAL);
                Font fontNormal = new Font(bf, 12, Font.NORMAL);

                // --- DATA FAKE QUÁN ---
                Paragraph brand = new Paragraph("BILLIARDS CLUB CENTER - LUXURY EDITION", fontTitle);
                brand.setAlignment(Element.ALIGN_CENTER); document.add(brand);
                
                Paragraph address = new Paragraph("Đ/c: 123 Đường Cầu Giấy, Q. Cầu Giấy, Hà Nội", fontInfo);
                address.setAlignment(Element.ALIGN_CENTER); document.add(address);
                
                Paragraph phone = new Paragraph("Hotline: 0988.888.888", fontInfo);
                phone.setAlignment(Element.ALIGN_CENTER); document.add(phone);
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));

                // --- THÔNG TIN HÓA ĐƠN ---
                document.add(new Paragraph("Mã Hóa Đơn: " + hdb.getMaHDB(), fontNormal));
                document.add(new Paragraph("Bàn chơi: " + ban.getTenBan(), fontNormal));
                document.add(new Paragraph("Nhân viên: " + cbNhanVien.getSelectedItem().toString(), fontNormal));
                document.add(new Paragraph("Ngày: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                
                document.add(new Paragraph(String.format("Tiền Bida (%d phút): %,.0f đ", minutes, tienBida), fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));

                // --- BẢNG CHI TIẾT ---
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
                Paragraph tong = new Paragraph("TỔNG THANH TOÁN: " + String.format("%,.0f VNĐ", tongBill), new Font(bf, 14, Font.BOLD, BaseColor.RED));
                tong.setAlignment(Element.ALIGN_RIGHT); document.add(tong);
                
                Paragraph thanks = new Paragraph("\nXin cảm ơn quý khách và hẹn gặp lại!", fontInfo);
                thanks.setAlignment(Element.ALIGN_CENTER); document.add(thanks);

                document.close();
                Desktop.getDesktop().open(file); // Mở ngay file
                
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi in PDF: " + e.getMessage()); }
        }
    }

    public boolean isPaid() { return paid; }
}