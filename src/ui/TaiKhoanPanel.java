package ui;

import dao.TaiKhoanDAO;
import dao.NhanVienDAO;
import model.TaiKhoan;
import model.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class TaiKhoanPanel extends JPanel {
    private JTextField txtMaTK, txtUsername, txtPassword, txtSearch;
    private JComboBox<String> cbVaiTro, cbNhanVien;
    private DefaultTableModel tableModel;
    private JTable table;
    private TaiKhoanDAO dao = new TaiKhoanDAO();
    
    // --- CẬP NHẬT: Thêm biến cho phân trang ---
    private List<TaiKhoan> allData;
    private PhanTrangPanel phanTrang;

    public TaiKhoanPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Hệ Thống Tài Khoản");
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
        form.setPreferredSize(new Dimension(380, 0)); // GIỮ NGUYÊN UI GỐC
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Tài khoản"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(15, 10, 15, 10); gbc.weightx = 1.0;

        int y = 0;
        gbc.gridy = y++; form.add(createLabel("Mã Tài Khoản:"), gbc);
        txtMaTK = LuxuryTheme.createTextField(); txtMaTK.setEditable(false); txtMaTK.setBackground(new Color(240, 240, 240));
        gbc.gridy = y++; form.add(txtMaTK, gbc);

        gbc.gridy = y++; form.add(createLabel("Tên đăng nhập:"), gbc);
        txtUsername = LuxuryTheme.createTextField(); gbc.gridy = y++; form.add(txtUsername, gbc);

        gbc.gridy = y++; form.add(createLabel("Mật khẩu:"), gbc);
        txtPassword = LuxuryTheme.createTextField(); gbc.gridy = y++; form.add(txtPassword, gbc);

        gbc.gridy = y++; form.add(createLabel("Vai trò:"), gbc);
        cbVaiTro = new JComboBox<>(new String[]{"NHANVIEN", "ADMIN"}); 
        cbVaiTro.setBackground(Color.WHITE); gbc.gridy = y++; form.add(cbVaiTro, gbc);

        gbc.gridy = y++; form.add(createLabel("Sở hữu bởi (Nhân viên):"), gbc);
        cbNhanVien = new JComboBox<>(); cbNhanVien.setBackground(Color.WHITE); 
        gbc.gridy = y++; form.add(cbNhanVien, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> {
            if (!validateForm()) return;
            try {
                dao.themTaiKhoan(taoDataTuForm()); refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEdit.addActionListener(e -> {
            if (!validateForm()) return;
            try {
                dao.capNhatToanBoTaiKhoan(taoDataTuForm()); refreshForm();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", 0) == 0) {
                dao.xoaTaiKhoan(txtMaTK.getText()); refreshForm();
            }
        });

        // --- CẬP NHẬT: LOGIC NÚT MỚI ---
        btnClear.addActionListener(e -> {
            refreshForm();
            table.clearSelection();
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridy = y++; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã/Username/Mã NV): "));
        txtSearch = LuxuryTheme.createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> searchAccounts());
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã TK", "Username", "Password", "Vai Trò", "Mã NV"}, 0);
        table = new JTable(tableModel); table.setRowHeight(35);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaTK.setText(table.getValueAt(r, 0).toString());
                txtUsername.setText(table.getValueAt(r, 1).toString());
                txtPassword.setText(table.getValueAt(r, 2).toString());
                cbVaiTro.setSelectedItem(table.getValueAt(r, 3).toString());
                
                String maNV = table.getValueAt(r, 4) != null ? table.getValueAt(r, 4).toString() : "";
                for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
                    if (cbNhanVien.getItemAt(i).startsWith(maNV)) { cbNhanVien.setSelectedIndex(i); break; }
                }
            }
        });
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // --- CẬP NHẬT: THÊM COMPONENT PHÂN TRANG ---
        phanTrang = new PhanTrangPanel(this::updateTableDisplay);
        panel.add(phanTrang, BorderLayout.SOUTH);
        
        return panel;
    }

    private TaiKhoan taoDataTuForm() {
        String maNV = null;
        if(cbNhanVien.getSelectedItem() != null && !cbNhanVien.getSelectedItem().toString().contains("Trống")) {
             maNV = cbNhanVien.getSelectedItem().toString().split(" - ")[0];
        }
        return new TaiKhoan(txtMaTK.getText(), txtUsername.getText().trim(), txtPassword.getText(), 
                            cbVaiTro.getSelectedItem().toString(), maNV);
    }

    private boolean validateForm() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!UsernameValidator.isValid(username)) {
            JOptionPane.showMessageDialog(this, UsernameValidator.message(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, ValidationUtils.passwordMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        for (TaiKhoan tk : dao.getAllTaiKhoan()) {
            if (tk.getTenDangNhap().equalsIgnoreCase(username) && !tk.getMaTK().equals(txtMaTK.getText())) {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private void refreshForm() {
        txtMaTK.setText(dao.sinhMaMoi()); txtUsername.setText(""); txtPassword.setText("");
        if (txtSearch != null) txtSearch.setText("");
        cbNhanVien.removeAllItems();
        cbNhanVien.addItem("--- Trống (Không có NV) ---");
        for(NhanVien nv : new NhanVienDAO().layTatCaNhanVien()) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
        
        loadData(dao.getAllTaiKhoan());
    }

    private void searchAccounts() {
        String keyword = txtSearch.getText().trim();
        phanTrang.setCurrentPage(1);
        loadData(FuzzySearch.filter(dao.getAllTaiKhoan(), keyword,
            TaiKhoan::getMaTK,
            TaiKhoan::getTenDangNhap,
            TaiKhoan::getMaNV,
            TaiKhoan::getVaiTro
        ));
    }

    private void loadData(List<TaiKhoan> data) {
        allData = data;
        phanTrang.setTotalItems(allData.size());
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        if (allData == null || allData.isEmpty()) return;
        
        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();
        
        for (int i = start; i < end; i++) {
            TaiKhoan tk = allData.get(i);
            tableModel.addRow(new Object[]{tk.getMaTK(), tk.getTenDangNhap(), tk.getMatKhau(), tk.getVaiTro(), tk.getMaNV()});
        }
    }

    private JLabel createLabel(String txt) { JLabel l = new JLabel(txt); l.setFont(new Font("Arial", Font.BOLD, 14)); return l; }
}
