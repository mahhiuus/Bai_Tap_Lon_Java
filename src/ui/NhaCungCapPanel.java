package ui;

import dao.NhaCungCapDAO;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class NhaCungCapPanel extends JPanel {
    private JTextField txtMaNCC, txtTenCongTy, txtSDT, txtDiaChi, txtEmail, txtNguoiLienHe, txtSearch;
    private DefaultTableModel tableModel;
    private JTable table;
    private NhaCungCapDAO dao = new NhaCungCapDAO();
    
    // --- CẬP NHẬT: Thêm biến cho phân trang ---
    private List<NhaCungCap> allData;
    private PhanTrangPanel phanTrang;

    public NhaCungCapPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Nhà Cung Cấp");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        add(createFormPanel(), BorderLayout.WEST);
        add(createTableAndSearchPanel(), BorderLayout.CENTER);

        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        // GIỮ NGUYÊN CHIỀU RỘNG 350px CỦA BẠN
        form.setPreferredSize(new Dimension(350, 0));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Nhà Cung Cấp",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;

        int y = 0;
        form.add(createLabel("Mã NCC:"), gbc); gbc.gridy = ++y;
        txtMaNCC = LuxuryTheme.createTextField(); txtMaNCC.setEditable(false); txtMaNCC.setBackground(new Color(240, 240, 240));
        form.add(txtMaNCC, gbc);

        gbc.gridy = ++y; form.add(createLabel("Tên Công Ty:"), gbc); gbc.gridy = ++y;
        txtTenCongTy = LuxuryTheme.createTextField();
        // Chỉ cho phép chữ (bao gồm khoảng trắng và dấu tiếng Việt)
        txtTenCongTy.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Cho phép chữ cái, khoảng trắng, dấu tiếng Việt
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        form.add(txtTenCongTy, gbc);

        gbc.gridy = ++y; form.add(createLabel("Số Điện Thoại:"), gbc); gbc.gridy = ++y;
        txtSDT = LuxuryTheme.createTextField();
        // Chỉ cho phép chữ số, tối đa 10 kí tự
        txtSDT.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Chỉ cho phép chữ số
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
                // Giới hạn 10 chữ số
                if (txtSDT.getText().length() >= 10 && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        form.add(txtSDT, gbc);

        gbc.gridy = ++y; form.add(createLabel("Địa Chỉ:"), gbc); gbc.gridy = ++y;
        txtDiaChi = LuxuryTheme.createTextField(); form.add(txtDiaChi, gbc);

        gbc.gridy = ++y; form.add(createLabel("Email:"), gbc); gbc.gridy = ++y;
        txtEmail = LuxuryTheme.createTextField();
        form.add(txtEmail, gbc);

        gbc.gridy = ++y; form.add(createLabel("Người Liên Hệ:"), gbc); gbc.gridy = ++y;
        txtNguoiLienHe = LuxuryTheme.createTextField();
        // Chỉ cho phép chữ (bao gồm khoảng trắng và dấu tiếng Việt)
        txtNguoiLienHe.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Cho phép chữ cái, khoảng trắng, dấu tiếng Việt
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        form.add(txtNguoiLienHe, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                if (!validateForm()) return;
                NhaCungCap ncc = new NhaCungCap(txtMaNCC.getText(), txtTenCongTy.getText(), txtSDT.getText(), txtDiaChi.getText(), txtEmail.getText(), txtNguoiLienHe.getText());
                dao.themNhaCungCap(ncc);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                if (!validateForm()) return;
                NhaCungCap ncc = new NhaCungCap(txtMaNCC.getText(), txtTenCongTy.getText(), txtSDT.getText(), txtDiaChi.getText(), txtEmail.getText(), txtNguoiLienHe.getText());
                dao.capNhatNhaCungCap(ncc);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.xoaNhaCungCap(txtMaNCC.getText());
                refreshForm();
            }
        });

        // --- CẬP NHẬT LOGIC NÚT MỚI ---
        btnClear.addActionListener(e -> { 
            refreshForm(); 
            table.clearSelection(); 
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridy = ++y; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã/Tên): "));
        txtSearch = LuxuryTheme.createTextField();
        txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        
        btnSearch.addActionListener(e -> loadData(dao.timKiem(txtSearch.getText().trim())));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã NCC", "Tên Công Ty", "SĐT", "Địa Chỉ", "Email", "Người Liên Hệ"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); // ĐỒNG BỘ 40px
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaNCC.setText(table.getValueAt(r, 0).toString());
                txtTenCongTy.setText(table.getValueAt(r, 1).toString());
                txtSDT.setText(table.getValueAt(r, 2).toString());
                txtDiaChi.setText(table.getValueAt(r, 3).toString());
                txtEmail.setText(table.getValueAt(r, 4).toString());
                txtNguoiLienHe.setText(table.getValueAt(r, 5).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);
        
        // --- CẬP NHẬT: GỌI COMPONENT PHÂN TRANG ---
        phanTrang = new PhanTrangPanel(this::updateTableDisplay);
        panel.add(phanTrang, BorderLayout.SOUTH);
        
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    private void refreshForm() {
        txtMaNCC.setText(dao.sinhMaMoi());
        txtTenCongTy.setText(""); txtSDT.setText(""); txtDiaChi.setText(""); txtEmail.setText(""); txtNguoiLienHe.setText("");
        txtSearch.setText("");
        loadData(dao.getAllNhaCungCap());
    }

    // --- LOGIC PHÂN TRANG ---
    private void loadData(List<NhaCungCap> list) {
        allData = list;
        phanTrang.setTotalItems(allData.size());
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        if (allData == null || allData.isEmpty()) {
            return;
        }
        
        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();
        
        for (int i = start; i < end; i++) {
            NhaCungCap n = allData.get(i);
            tableModel.addRow(new Object[]{n.getMaNCC(), n.getTenCongTy(), n.getSdt(), n.getDiaChi(), n.getEmail(), n.getNguoiLienHe()});
        }
    }

    // Validation method
    private boolean validateForm() {
        String tenCongTy = txtTenCongTy.getText().trim();
        String sdt = txtSDT.getText().trim();
        String email = txtEmail.getText().trim();
        String nguoiLienHe = txtNguoiLienHe.getText().trim();
        
        if (tenCongTy.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên công ty!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check if company name contains only letters and spaces
        if (!tenCongTy.matches("[a-zA-Z\\s\\u0080-\\uFFFF]*")) {
            JOptionPane.showMessageDialog(this, "Tên công ty chỉ được phép chứa chữ cái!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (sdt.length() != 10) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải đúng 10 chữ số!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập email!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check email format: letters/numbers + @gmail.com
        if (!email.matches("[a-zA-Z0-9]+@gmail\\.com")) {
            JOptionPane.showMessageDialog(this, "Email phải có định dạng: kí tự/số@gmail.com", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (nguoiLienHe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên người liên hệ!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Check if contact name contains only letters and spaces
        if (!nguoiLienHe.matches("[a-zA-Z\\s\\u0080-\\uFFFF]*")) {
            JOptionPane.showMessageDialog(this, "Tên người liên hệ chỉ được phép chứa chữ cái!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        return true;
    }
}