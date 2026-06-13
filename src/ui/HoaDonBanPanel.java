package ui;

import dao.HoaDonBanDAO;
import dao.DBConnection;
import model.HoaDonBan;
import model.TaiKhoan; 

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
import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

public class HoaDonBanPanel extends JPanel {
    private JTextField txtSearch;
    private DefaultTableModel tableModel;
    private JTable table;
    private HoaDonBanDAO dao;
    private JLabel lblTongDoanhThu;
    private TaiKhoan currentUser; 

    // --- CẬP NHẬT: Biến phân trang ---
    private List<HoaDonBan> allData;
    private PhanTrangPanel phanTrang;
        private JDateChooser dateChooserFrom, dateChooserTo;

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
        btnLamMoi.addActionListener(e -> { loadData(); table.clearSelection(); });
        pnlHeader.add(btnLamMoi, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // BẢNG DỮ LIỆU
        add(createTablePanel(), BorderLayout.CENTER);

        // BOTTOM
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
        
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
            searchPanel.add(createLabel("Từ ngày: "));
            dateChooserFrom = new JDateChooser();
            dateChooserFrom.setDateFormatString("dd/MM/yyyy");
            dateChooserFrom.setPreferredSize(new Dimension(120, 35));
            searchPanel.add(dateChooserFrom);
        
            searchPanel.add(createLabel("Đến ngày: "));
            dateChooserTo = new JDateChooser();
            dateChooserTo.setDateFormatString("dd/MM/yyyy");
            dateChooserTo.setPreferredSize(new Dimension(120, 35));
            searchPanel.add(dateChooserTo);
        
            JButton btnSearchDate = LuxuryTheme.createButton("Tìm Theo Ngày", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
            btnSearchDate.addActionListener(e -> searchByDate());
            searchPanel.add(btnSearchDate);
        
        searchPanel.add(createLabel("Tìm kiếm (Mã HĐ/Mã Phiên/Mã KH/Mã NV): "));
        txtSearch = LuxuryTheme.createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> loadData(dao.timKiem(txtSearch.getText().trim())));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        panel.add(scroll, BorderLayout.CENTER);

        // --- CẬP NHẬT: THÊM COMPONENT PHÂN TRANG ---
        phanTrang = new PhanTrangPanel(this::updateTableDisplay);
        panel.add(phanTrang, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnDelete = LuxuryTheme.createButton("Xóa Hóa Đơn", new Color(192, 57, 43), Color.WHITE);
        
        if (currentUser != null && !currentUser.getVaiTro().equals("ADMIN")) {
            btnDelete.setVisible(false); 
        }
        btnDelete.addActionListener(e -> xoaHoaDon());

        JButton btnInPDF = LuxuryTheme.createButton("IN LẠI HÓA ĐƠN PDF", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnInPDF.addActionListener(e -> inLaiHoaDonPDF());

        // --- CẬP NHẬT: NÚT MỚI (Bỏ chọn bảng) ---
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);
        btnClear.addActionListener(e -> { loadData(); table.clearSelection(); });

        btnPanel.add(btnDelete);
        btnPanel.add(btnInPDF);
        btnPanel.add(btnClear);

        panel.add(btnPanel, BorderLayout.WEST);

        lblTongDoanhThu = new JLabel("Tổng (Trang 1): 0 VNĐ");
        lblTongDoanhThu.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22));
        lblTongDoanhThu.setForeground(Color.RED);
        panel.add(lblTongDoanhThu, BorderLayout.EAST);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    // --- CẬP NHẬT: LOGIC PHÂN TRANG ---
    private void loadData() {
        if (txtSearch != null) txtSearch.setText("");
        loadData(dao.getAllHoaDonBan());
    }

    private void loadData(List<HoaDonBan> data) {
        allData = data;
        phanTrang.setTotalItems(allData.size());
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        double tongDoanhThuTrang = 0;

        if (allData == null || allData.isEmpty()) {
            lblTongDoanhThu.setText("Tổng (Trang 1): 0 VNĐ");
            return;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();

        for (int i = start; i < end; i++) {
            HoaDonBan hdb = allData.get(i);
            String ngayBanStr = hdb.getNgayBan() != null ? hdb.getNgayBan().format(formatter) : "";
            
            tableModel.addRow(new Object[]{
                hdb.getMaHDB(), hdb.getMaPhien(), hdb.getMaKH(), hdb.getMaNV(), ngayBanStr,
                String.format("%,.0f", hdb.getTienBida()), String.format("%,.0f", hdb.getTienSanPham()), String.format("%,.0f", hdb.getTongTien())
            });
            tongDoanhThuTrang += hdb.getTongTien();
        }

        lblTongDoanhThu.setText("Tổng (Trang " + phanTrang.getCurrentPage() + "): " + String.format("%,.0f VNĐ", tongDoanhThuTrang));
    }
    
    private void searchByDate() {
        java.util.Date dateFrom = dateChooserFrom.getDate();
        java.util.Date dateTo = dateChooserTo.getDate();
    
        if (dateFrom == null || dateTo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        LocalDate ldFrom = dateFrom.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate ldTo = dateTo.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        
        List<HoaDonBan> filtered = dao.getAllHoaDonBan();
        List<HoaDonBan> result = new ArrayList<>();
        
        for (HoaDonBan hdb : filtered) {
            if (hdb.getNgayBan() != null && !hdb.getNgayBan().isBefore(ldFrom) && !hdb.getNgayBan().isAfter(ldTo)) {
                result.add(hdb);
            }
        }        
        loadData(result);
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
    // HÀM IN LẠI HÓA ĐƠN BÁN (GIỮ NGUYÊN)
    // =========================================================================
    private void inLaiHoaDonPDF() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn một Hóa Đơn trong bảng để in!"); return; }

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

                document.add(new Paragraph("HÓA ĐƠN BÁN HÀNG (BẢN SAO)", new Font(bf, 14, Font.BOLD)));
                document.add(new Paragraph("Mã Hóa Đơn: " + maHDB, fontNormal));
                document.add(new Paragraph("Khách hàng: " + maKH, fontNormal));
                document.add(new Paragraph("Nhân viên: " + maNV, fontNormal));
                document.add(new Paragraph("Ngày in: " + ngayBan, fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                document.add(new Paragraph("Tiền Bida: " + tienBidaStr + " đ", fontNormal));
                document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                document.add(new Paragraph("Chi tiết gọi đồ:", fontNormal));
                document.add(new Paragraph(" "));
                PdfPTable pdfTable = new PdfPTable(3); 
                pdfTable.setWidthPercentage(100); pdfTable.setWidths(new float[]{4f, 1f, 3f});
                pdfTable.addCell(new Phrase("Tên món", fontNormal)); pdfTable.addCell(new Phrase("SL", fontNormal)); pdfTable.addCell(new Phrase("Thành tiền", fontNormal));

                String sql = "SELECT c.so_luong, c.don_gia_ban, s.ten_sp FROM chi_tiet_hoa_don_ban c JOIN san_pham s ON c.ma_sp = s.ma_sp WHERE c.ma_hdb = ?";
                try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, maHDB); ResultSet rs = ps.executeQuery();
                    while(rs.next()) {
                        pdfTable.addCell(new Phrase(rs.getString("ten_sp"), fontNormal));
                        pdfTable.addCell(new Phrase(String.valueOf(rs.getInt("so_luong")), fontNormal));
                        pdfTable.addCell(new Phrase(String.format("%,.0f", rs.getInt("so_luong") * rs.getDouble("don_gia_ban")), fontNormal));
                    }
                }
                document.add(pdfTable); document.add(new Paragraph("----------------------------------------------------------------", fontNormal));
                Paragraph tong = new Paragraph("TỔNG THANH TOÁN: " + tongTienStr + " VNĐ", new Font(bf, 14, Font.BOLD, BaseColor.RED));
                tong.setAlignment(Element.ALIGN_RIGHT); document.add(tong);
                Paragraph thanks = new Paragraph("\nXin cảm ơn quý khách và hẹn gặp lại!", fontInfo);
                thanks.setAlignment(Element.ALIGN_CENTER); document.add(thanks);

                document.close(); Desktop.getDesktop().open(file); 
            } catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi in PDF: " + e.getMessage()); }
        }
    }
}