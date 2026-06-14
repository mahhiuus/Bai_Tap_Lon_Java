package ui;

import dao.BanBidaDAO;
import dao.PhienChoiDAO;
import model.BanBida;
import model.PhienChoi;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PhienChoiPanel extends JPanel {
    private JTextField txtMaPhien, txtSearch;
    private JComboBox<String> cbBan, cbTrangThai;
    private DefaultTableModel tableModel;
    private JTable table;
    private PhienChoiDAO dao;

    private List<Object[]> allData = new ArrayList<>();
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 10;
    private JLabel lblPageInfo;

    public PhienChoiPanel() {
        dao = new PhienChoiDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Lịch Sử Quản Lý Phiên Chơi");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);
        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setPreferredSize(new Dimension(380, 0));
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), 
            "Kiểm Tra Hệ Thống", 0, 0, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 10, 15, 10); gbc.weightx = 1.0;

        int y = 0;
        gbc.gridy = y++; form.add(createLabel("Mã Phiên:"), gbc);
        txtMaPhien = LuxuryTheme.createTextField(); txtMaPhien.setEditable(false); txtMaPhien.setBackground(new Color(240, 240, 240));
        gbc.gridy = y++; form.add(txtMaPhien, gbc);

        gbc.gridy = y++; form.add(createLabel("Thuộc Bàn:"), gbc);
        cbBan = new JComboBox<>(); cbBan.setBackground(Color.WHITE); cbBan.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridy = y++; form.add(cbBan, gbc);

        gbc.gridy = y++; form.add(createLabel("Trạng Thái:"), gbc);
        cbTrangThai = new JComboBox<>(new String[]{"DANG_CHOI", "DA_KET_THUC"}); cbTrangThai.setBackground(Color.WHITE); cbTrangThai.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridy = y++; form.add(cbTrangThai, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); btnPanel.setBackground(LuxuryTheme.CREAM);
        JButton btnDelete = LuxuryTheme.createButton("Xóa Bỏ Rác", new Color(192, 57, 43), Color.WHITE);
        JButton btnClear = LuxuryTheme.createButton("Làm Mới", Color.GRAY, Color.WHITE);

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (JOptionPane.showConfirmDialog(this, "Chỉ xóa khi dữ liệu bị lỗi rác! Xác nhận?", "Xóa", 0) == 0) {
                dao.xoaPhien(txtMaPhien.getText()); refreshForm();
            }
        });

        btnClear.addActionListener(e -> refreshForm());

        btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridy = y++; gbc.insets = new Insets(30, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(LuxuryTheme.CREAM);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã Phiên): "));
        txtSearch = LuxuryTheme.createTextField(); txtSearch.setPreferredSize(new Dimension(250, 35)); searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        searchPanel.add(btnSearch); panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã Phiên", "Bàn", "Giờ Bắt Đầu", "Giờ Kết Thúc", "Trạng Thái", "Số Phút"}, 0);
        table = new JTable(tableModel); table.setRowHeight(40);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD); table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaPhien.setText(table.getValueAt(r, 0).toString());
                cbTrangThai.setSelectedItem(table.getValueAt(r, 4).toString());
                String maBan = table.getValueAt(r, 1).toString();
                for (int i = 0; i < cbBan.getItemCount(); i++) {
                    if (cbBan.getItemAt(i).startsWith(maBan)) { cbBan.setSelectedIndex(i); break; }
                }
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel pagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); pagPanel.setBackground(LuxuryTheme.CREAM);
        JButton btnPrev = LuxuryTheme.createButton("< Trước", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnNext = LuxuryTheme.createButton("Sau >", LuxuryTheme.NAVY, Color.WHITE);
        lblPageInfo = new JLabel("Trang 1 / 1"); lblPageInfo.setFont(new Font("Arial", Font.BOLD, 14)); lblPageInfo.setForeground(LuxuryTheme.NAVY);
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; updateTableDisplay(); } });
        btnNext.addActionListener(e -> { if (currentPage < Math.ceil((double)allData.size() / ITEMS_PER_PAGE)) { currentPage++; updateTableDisplay(); } });
        pagPanel.add(btnPrev); pagPanel.add(lblPageInfo); pagPanel.add(btnNext); panel.add(pagPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshForm() {
        cbBan.removeAllItems();
        for (BanBida b : new BanBidaDAO().layTatCaBan()) cbBan.addItem(b.getMaBan() + " - " + b.getTenBan());
        loadData("");
    }

    private void loadData(String keyword) {
        allData.clear();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        for (PhienChoi p : dao.layTatCaPhien()) {
            String haystack = FuzzySearch.normalize(p.getMaPhien() + " " + p.getMaBan() + " " + p.getTrangThaiPhien());
            boolean matched = true;
            for (String term : FuzzySearch.normalize(keyword).trim().split("\\s+")) {
                if (!term.isEmpty() && !haystack.contains(term)) {
                    matched = false;
                    break;
                }
            }
            if (matched) {
                String bd = p.getThoiGianBatDau() != null ? p.getThoiGianBatDau().format(fmt) : "";
                String kt = p.getThoiGianKetThuc() != null ? p.getThoiGianKetThuc().format(fmt) : "Chưa xong";
                allData.add(new Object[]{ p.getMaPhien(), p.getMaBan(), bd, kt, p.getTrangThaiPhien(), p.getSoPhutChoi() });
            }
        }
        currentPage = 1; updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0); int max = Math.max(1, (int) Math.ceil((double) allData.size() / ITEMS_PER_PAGE));
        lblPageInfo.setText("Trang " + currentPage + " / " + max);
        int start = (currentPage - 1) * ITEMS_PER_PAGE; int end = Math.min(start + ITEMS_PER_PAGE, allData.size());
        for (int i = start; i < end; i++) tableModel.addRow(allData.get(i));
    }
    private JLabel createLabel(String text) { JLabel lbl = new JLabel(text); lbl.setFont(new Font("Arial", Font.BOLD, 14)); lbl.setForeground(LuxuryTheme.NAVY); return lbl; }
}
