package ui;

import dao.HoaDonBanDAO;
import model.HoaDonBan;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HoaDonPanel extends JPanel {
    private DefaultTableModel tableModel;
    private JTable table;
    private HoaDonBanDAO dao;
    private JLabel lblTongDoanhThu;

    public HoaDonPanel() {
        dao = new HoaDonBanDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM); 
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // HEADER
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblHeader = new JLabel("Quản Lý Hóa Đơn Bán");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        pnlHeader.add(lblHeader, BorderLayout.WEST);

        JButton btnLamMoi = LuxuryTheme.createButton("Làm Mới Dữ Liệu", LuxuryTheme.TEAL, Color.WHITE);
        btnLamMoi.addActionListener(e -> loadData());
        pnlHeader.add(btnLamMoi, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // BẢNG DỮ LIỆU
        add(createTablePanel(), BorderLayout.CENTER);

        // BOTTOM (Doanh thu & Xóa)
        add(createBottomPanel(), BorderLayout.SOUTH);

        loadData();
    }

    private JPanel createTablePanel() {
        // Cấu trúc cột dựa theo model HoaDonBan
        tableModel = new DefaultTableModel(new String[]{
            "Mã HĐ", "Mã Phiên", "Mã KH", "Mã NV", "Ngày Bán", "Tiền Bida", "Tiền SP", "Tổng Tiền"
        }, 0);
        
        table = new JTable(tableModel);
        table.setRowHeight(40); // Padding cho dòng
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
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
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Nút Xóa
        JButton btnDelete = LuxuryTheme.createButton("Xóa Hóa Đơn", new Color(192, 57, 43), Color.WHITE);
        btnDelete.addActionListener(e -> xoaHoaDon());
        panel.add(btnDelete, BorderLayout.WEST);

        // Hiển thị tổng doanh thu
        lblTongDoanhThu = new JLabel("Tổng doanh thu: 0 VNĐ");
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 20));
        lblTongDoanhThu.setForeground(Color.RED);
        panel.add(lblTongDoanhThu, BorderLayout.EAST);

        return panel;
    }

    private void loadData() {
        tableModel.setRowCount(0);
        double tongDoanhThu = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<HoaDonBan> list = dao.getAllHoaDonBan(); // Gọi DAO
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
                dao.xoaHoaDonBan(maHDB); // Gọi DAO để xóa
                loadData(); // Tải lại bảng
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}