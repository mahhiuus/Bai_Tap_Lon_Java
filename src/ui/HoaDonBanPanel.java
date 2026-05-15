package ui;

import dao.HoaDonBanDAO;
import dao.DBConnection;
import model.HoaDonBan;
import model.TaiKhoan; // Đã thêm thư viện TaiKhoan

// Thư viện in PDF của iText 5
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HoaDonBanPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private HoaDonBanDAO dao;
    private JLabel lblTongDoanhThu;
    private TaiKhoan currentUser; // Thêm biến lưu quyền tài khoản

    // --- CẬP NHẬT CONSTRUCTOR NHẬN TÀI KHOẢN ---
    public HoaDonBanPanel(TaiKhoan user) {
        this.currentUser = user;
        dao = new HoaDonBanDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM); 
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblHeader = new JLabel("Quản Lý Hóa Đơn Bán & Xuất PDF");
        lblHeader.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        pnlHeader.add(lblHeader, BorderLayout.WEST);

        JButton btnLamMoi = LuxuryTheme.createButton("Làm Mới Dữ Liệu", LuxuryTheme.TEAL, Color.WHITE);
        btnLamMoi.addActionListener(e -> loadData());
        pnlHeader.add(btnLamMoi, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // BẢNG DỮ LIỆU
        add(createTablePanel(), BorderLayout.CENTER);

        // BOTTOM (Nút Xóa, Nút In PDF & Doanh thu)
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadData();
    }

    private JPanel createTablePanel() {
        tableModel = new DefaultTableModel(new String[]{
            "Mã HĐ", "Mã Phiên", "Mã KH", "Mã NV", "Ngày Bán", "Tiền Bida", "Tiền SP", "Tổng Tiền"
        }, 0);
        
        table = new JTable(tableModel);
        table.setRowHeight(40); 
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnDelete = LuxuryTheme.createButton("Xóa Hóa Đơn", new Color(192, 57, 43), Color.WHITE);
        
        // --- LOGIC PHÂN QUYỀN ẨN NÚT XÓA ---
        if (currentUser != null && !currentUser.getVaiTro().equals("ADMIN")) {
            btnDelete.setVisible(false); // Nếu là nhân viên thì ẩn nút Xóa đi
        }
        btnDelete.addActionListener(e -> xoaHoaDon());

        JButton btnInPDF = LuxuryTheme.createButton("IN LẠI HÓA ĐƠN PDF", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnInPDF.addActionListener(e -> inLaiHoaDonPDF());

        btnPanel.add(btnDelete);
        btnPanel.add(btnInPDF);

        panel.add(btnPanel, BorderLayout.WEST);

        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongDoanhThu.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lblTongDoanhThu.setForeground(Color.RED);
        panel.add(lblTongDoanhThu, BorderLayout.EAST);

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        double tongDoanhThu = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<HoaDonBan> list = dao.getAllHoaDonBan(); 
        for (HoaDonBan hdb : list) {
            String ngayBanStr = hdb.getNgayBan() != null ? hdb.getNgayBan().format(formatter) : "";
            
            tableModel.addRow(new Object[]{
                hdb.getMaHDB(),
                hdb.getMaPhien(),
                hdb.getMaKH(),
                hdb.getMaNV(),
                ngayBanStr,
                String.format("%,.0f", hdb.getTienBida()),
                String.format("%,.0f", hdb.getTienSanPham()),
                String.format("%,.0f", hdb.getTongTien())
            });
            tongDoanhThu += hdb.getTongTien();
        }

        lblTongDoanhThu.setText("Tổng doanh thu: " + String.format("%,.0f VNĐ", tongDoanhThu));
    }

    private void xoaHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xóa!");
            return;
        }

        String maHDB = table.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa hóa đơn " + maHDB + "?\nLưu ý: Dữ liệu bị xóa không thể khôi phục!", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dao.xoaHoaDonBan(maHDB);
                loadData(); 
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // HÀM IN LẠI HÓA ĐƠN BÁN (Quét lấy chi tiết sản phẩm từ CSDL để xuất PDF)
    // =========================================================================
    private void inLaiHoaDonPDF() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hóa Đơn trong bảng để in!");
            return;
        }

        String maHDB = table.getValueAt(row, 0).toString();
        String maKH = table.getValueAt(row, 2).toString();
        String maNV = table.getValueAt(row, 3).toString();
        String ngayBan = table.getValueAt(row, 4).toString();
        String tienBidaStr = table.getValueAt(row, 5).toString();
        String tongTienStr = table.getValueAt(row, 7).toString();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn nơi lưu Hóa Đơn PDF");
        fileChooser.setSelectedFile(new File(maHDB + "_Bill.pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getParentFile(), file.getName() + ".pdf");
            }

            try {
                Document document = new Document(PageSize.A5);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

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
                document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG (BẢN SAO)", new Font(bf, 14, Font.BOLD)));
                document.add(new Paragraph("Mã Hóa Đơn: " + maHDB, fontNormal));
                document.add(new Paragraph("Khách hàng: " + maKH, fontNormal));
                document.add(new Paragraph("Nhân viên: " + maNV, fontNormal));
                document.add(new Paragraph("Ngày in: " + ngayBan, fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                
                document.add(new Paragraph("Tiền Bida: " + tienBidaStr + " đ", fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));

                // --- BẢNG CHI TIẾT SẢN PHẨM DỊCH VỤ ---
                document.add(new Paragraph("Chi tiết gọi đồ:", fontNormal));
                document.add(new Paragraph(" "));
                PdfPTable pdfTable = new PdfPTable(3); 
                pdfTable.setWidthPercentage(100);
                pdfTable.setWidths(new float[]{4f, 1f, 3f});
                pdfTable.addCell(new Phrase("Tên món", fontNormal));
                pdfTable.addCell(new Phrase("SL", fontNormal));
                pdfTable.addCell(new Phrase("Thành tiền", fontNormal));

                // SQL lấy chi tiết từ CSDL
                String sql = "SELECT c.so_luong, c.don_gia_ban, s.ten_sp FROM chi_tiet_hoa_don_ban c JOIN san_pham s ON c.ma_sp = s.ma_sp WHERE c.ma_hdb = ?";
                try (Connection conn = DBConnection.getConnection(); 
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, maHDB);
                    ResultSet rs = ps.executeQuery();
                    while(rs.next()) {
                        String tenSP = rs.getString("ten_sp");
                        int sl = rs.getInt("so_luong");
                        double gia = rs.getDouble("don_gia_ban");
                        pdfTable.addCell(new Phrase(tenSP, fontNormal));
                        pdfTable.addCell(new Phrase(String.valueOf(sl), fontNormal));
                        pdfTable.addCell(new Phrase(String.format("%,.0f", sl * gia), fontNormal));
                    }
                }

                document.add(pdfTable);
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                
                Paragraph tong = new Paragraph("TỔNG THANH TOÁN: " + tongTienStr + " VNĐ", new Font(bf, 14, Font.BOLD, BaseColor.RED));
                tong.setAlignment(Element.ALIGN_RIGHT); 
                document.add(tong);
                
                Paragraph thanks = new Paragraph("\nXin cảm ơn quý khách và hẹn gặp lại!", fontInfo);
                thanks.setAlignment(Element.ALIGN_CENTER); 
                document.add(thanks);

                document.close();
                Desktop.getDesktop().open(file); // Tự động mở file
                
            } catch (Exception e) { 
                JOptionPane.showMessageDialog(this, "Lỗi in PDF: " + e.getMessage()); 
            }
        }
    }
}