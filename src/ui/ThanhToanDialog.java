package ui;

import model.BanBida;
import model.PhienChoi;
import model.HoaDonBan;
import model.NhanVien;  
import model.KhachHang;

import dao.PhienChoiDAO;
import dao.ChiTietPhienDAO;
import dao.HoaDonBanDAO;
import dao.NhanVienDAO;
import dao.KhachHangDAO;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ThanhToanDialog extends JDialog {
    private boolean paid = false;
    private JTextField txtTienDoAn;
    private JLabel lblTongTien;
    private double giaGio, tienBida, tienSanPham, tongBill;
    
    private JComboBox<String> cbKhachHang;
    private JComboBox<String> cbNhanVien;

    public ThanhToanDialog(Frame owner, BanBida ban, PhienChoi phien) {
        super(owner, "Thanh Toán Hóa Đơn - " + ban.getTenBan(), true);
        setSize(500, 650); 
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // --- 1. TÍNH TOÁN DỮ LIỆU ---
        LocalDateTime start = phien.getThoiGianBatDau();
        LocalDateTime end = LocalDateTime.now();
        long minutes = Math.max(1, Duration.between(start, end).toMinutes()); 
        
        giaGio = ban.getLoaiBan().equals("VIP") ? 80000 : 50000;
        tienBida = (minutes / 60.0) * giaGio;

        ChiTietPhienDAO ctpDao = new ChiTietPhienDAO();
        tienSanPham = ctpDao.tinhTongTienTheoPhien(phien.getMaPhien());
        
        tongBill = tienBida + tienSanPham;

        // --- 2. CẤU HÌNH GIAO DIỆN ---
        JPanel content = new JPanel(new GridLayout(0, 1, 10, 15));
        content.setBorder(new EmptyBorder(25, 40, 25, 40));
        content.setBackground(Color.WHITE);

        JLabel lblHeader = new JLabel("Chi tiết sử dụng dịch vụ");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 20));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        content.add(lblHeader);

        content.add(new JLabel("Giờ bắt đầu: " + start.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
        content.add(new JLabel("Giờ kết thúc: " + end.format(DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy"))));
        content.add(new JLabel("Tổng thời gian chơi: " + minutes + " phút"));
        content.add(new JLabel("Thành tiền Bida: " + String.format("%,.0f VNĐ", tienBida)));

        content.add(new JLabel("Tiền Đồ ăn / Thức uống:"));
        txtTienDoAn = LuxuryTheme.createTextField();
        txtTienDoAn.setText(String.format("%,.0f VNĐ", tienSanPham));
        txtTienDoAn.setEditable(false);
        txtTienDoAn.setBackground(new Color(245, 245, 245));
        txtTienDoAn.setForeground(Color.RED);
        content.add(txtTienDoAn);

        // --- COMBOBOX NHÂN VIÊN & KHÁCH HÀNG ---
        content.add(new JLabel("Nhân viên thực hiện:"));
        cbNhanVien = new JComboBox<>();
        cbNhanVien.setBackground(Color.WHITE);
        content.add(cbNhanVien);

        content.add(new JLabel("Khách hàng thanh toán:"));
        cbKhachHang = new JComboBox<>();
        cbKhachHang.setBackground(Color.WHITE);
        content.add(cbKhachHang);

        // GỌI HÀM ĐỔ DỮ LIỆU TỪ DATABASE
        loadDataToComboBox();

        lblTongTien = new JLabel("TỔNG THANH TOÁN: " + String.format("%,.0f VNĐ", tongBill));
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 22));
        lblTongTien.setForeground(LuxuryTheme.NAVY);
        lblTongTien.setBorder(new EmptyBorder(15, 0, 0, 0));
        content.add(lblTongTien);

        // --- 3. NÚT XÁC NHẬN ---
        JButton btnPay = LuxuryTheme.createButton("XUẤT HÓA ĐƠN", LuxuryTheme.TEAL, Color.WHITE);
        btnPay.setPreferredSize(new Dimension(0, 60));
        
        btnPay.addActionListener(e -> {
            // KHÓA NÚT BẤM ĐỂ CHỐNG SPAM CLICK LÀM TẠO 2 HÓA ĐƠN
            btnPay.setEnabled(false); 
            
            try {
                if (cbKhachHang.getSelectedItem() == null || cbNhanVien.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn Khách hàng và Nhân viên!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    btnPay.setEnabled(true);
                    return;
                }
                
                if (cbKhachHang.getSelectedItem().toString().contains("Chưa có dữ liệu") || cbNhanVien.getSelectedItem().toString().contains("Chưa có dữ liệu")) {
                    JOptionPane.showMessageDialog(this, "Trong Database chưa có dữ liệu Khách Hàng hoặc Nhân Viên. Vui lòng thêm trước!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    btnPay.setEnabled(true);
                    return;
                }

                String maKH = cbKhachHang.getSelectedItem().toString().split(" - ")[0];
                String maNV = cbNhanVien.getSelectedItem().toString().split(" - ")[0];

                PhienChoiDAO pcDao = new PhienChoiDAO();
                pcDao.ketThucPhien(phien.getMaPhien(), end);

                HoaDonBanDAO hdbDao = new HoaDonBanDAO();
                HoaDonBan hdb = hdbDao.taoTuPhien(phien.getMaPhien(), maKH, maNV, tienBida);
                
                if (hdb != null) {
                    hdbDao.them(hdb);
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công! Mã HĐ: " + hdb.getMaHDB(), "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                    paid = true;
                    dispose();
                } else {
                    btnPay.setEnabled(true);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lưu DB: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                btnPay.setEnabled(true); // Lỗi thì mở lại nút để thử lại
            }
        });

        add(new JScrollPane(content), BorderLayout.CENTER);
        add(btnPay, BorderLayout.SOUTH);
    }

    // =====================================================================
    // HÀM XỬ LÝ ĐỔ DỮ LIỆU TỪ DAO LÊN GIAO DIỆN COMBOBOX
    // =====================================================================
    private void loadDataToComboBox() {
        try {
            NhanVienDAO nvDao = new NhanVienDAO();
            List<NhanVien> dsNV = nvDao.layTatCaNhanVien(); 
            cbNhanVien.removeAllItems();
            if (dsNV != null && !dsNV.isEmpty()) {
                for (NhanVien nv : dsNV) {
                    cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV()); 
                }
            } else {
                cbNhanVien.addItem("--- Chưa có dữ liệu NV ---");
            }

            KhachHangDAO khDao = new KhachHangDAO();
            List<KhachHang> dsKH = khDao.getAllKhachHang();
            cbKhachHang.removeAllItems();
            if (dsKH != null && !dsKH.isEmpty()) {
                for (KhachHang kh : dsKH) {
                    cbKhachHang.addItem(kh.getMaKH() + " - " + kh.getTenKH());
                }
            } else {
                cbKhachHang.addItem("--- Chưa có dữ liệu KH ---");
            }
        } catch (Exception e) {
            System.err.println("Chưa thể load ComboBox: " + e.getMessage());
        }
    }

    public boolean isPaid() { return paid; }
}