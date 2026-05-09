package ui;

import dao.KhachHangDAO;
import model.KhachHang;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KhachHangPanel extends JPanel {
    
    private JTextField txtMaKH, txtTenKH, txtSdt;
    private DefaultTableModel tableModel;
    private JTable table;
    private KhachHangDAO dao;
    
    // Biến cho phân trang
    private List<KhachHang> allKhachHang;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 10;
    private JLabel lblPageInfo;

    public KhachHangPanel() {
        dao = new KhachHangDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // 1. TIÊU ĐỀ
        JLabel lblHeader = new JLabel("Quản Lý Khách Hàng");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        // 2. KHU VỰC FORM (Ô Input xịn & Nút bấm)
        add(createFormPanel(), BorderLayout.WEST);

        // 3. KHU VỰC TABLE & PHÂN TRANG
        add(createTablePanel(), BorderLayout.CENTER);

        // Tải dữ liệu ban đầu
        loadDataToTable();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin chi tiết"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weightx = 1.0;

        // Mã KH
        gbc.gridy = 0; form.add(createLabel("Mã Khách Hàng:"), gbc);
        txtMaKH = LuxuryTheme.createTextField();
        gbc.gridy = 1; form.add(txtMaKH, gbc);

        // Tên KH
        gbc.gridy = 2; form.add(createLabel("Tên Khách Hàng:"), gbc);
        txtTenKH = LuxuryTheme.createTextField();
        gbc.gridy = 3; form.add(txtTenKH, gbc);

        // SĐT
        gbc.gridy = 4; form.add(createLabel("Số Điện Thoại:"), gbc);
        txtSdt = LuxuryTheme.createTextField();
        gbc.gridy = 5; form.add(txtSdt, gbc);

        // Khung Nút Bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(LuxuryTheme.CREAM);
        
        // Màu TEAL cho nút chính (Thêm), NAVY cho nút phụ
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);

        gbc.gridy = 6;
        gbc.insets = new Insets(30, 10, 10, 10);
        form.add(btnPanel, gbc);

        return form;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LuxuryTheme.CREAM);

        // Table Model
        String[] cols = {"Mã KH", "Họ Tên", "SĐT", "Điểm", "Ngày ĐK"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        
        // Custom Table Header (Màu Navy, chữ Vàng)
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(35);
        table.setSelectionBackground(new Color(17, 126, 141, 50)); // Teal nhạt khi select

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);

        // --- THANH PHÂN TRANG TỰ ĐỘNG BÊN DƯỚI ---
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        paginationPanel.setBackground(LuxuryTheme.CREAM);

        JButton btnPrev = LuxuryTheme.createButton("< Trước", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnNext = LuxuryTheme.createButton("Sau >", LuxuryTheme.NAVY, Color.WHITE);
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Arial", Font.BOLD, 14));
        lblPageInfo.setForeground(LuxuryTheme.NAVY);

        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; updateTableDisplay(); } });
        btnNext.addActionListener(e -> { 
            int maxPage = (int) Math.ceil((double)allKhachHang.size() / ITEMS_PER_PAGE);
            if (currentPage < maxPage) { currentPage++; updateTableDisplay(); } 
        });

        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);

        panel.add(paginationPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    // --- LOGIC DATA & PHÂN TRANG ---
    private void loadDataToTable() {
        // Lấy tất cả dữ liệu từ Database 1 lần
        allKhachHang = dao.getAllKhachHang();
        currentPage = 1; // Reset về trang 1
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0); // Xóa bảng hiện tại
        if (allKhachHang == null || allKhachHang.isEmpty()) {
            lblPageInfo.setText("Trang 1 / 1");
            return;
        }

        int maxPage = (int) Math.ceil((double) allKhachHang.size() / ITEMS_PER_PAGE);
        lblPageInfo.setText("Trang " + currentPage + " / " + maxPage);

        // Cắt sub-list để hiển thị cho trang hiện tại
        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allKhachHang.size());

        for (int i = start; i < end; i++) {
            KhachHang kh = allKhachHang.get(i);
            tableModel.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getSdt(), 
                kh.getDiemTichLuy(), kh.getNgayDangKy()
            });
        }
    }
}