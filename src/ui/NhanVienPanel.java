package ui;

import dao.NhanVienDAO;
import model.NhanVien;
import com.toedter.calendar.JDateChooser; 

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NhanVienPanel extends JPanel {
    private JTextField txtMaNV, txtTenNV, txtSDT, txtSearch;
    private JComboBox<String> cbGioiTinh, cbChucVu;
    private JDateChooser dateChooser;
    private DefaultTableModel tableModel;
    private JTable table;
    private NhanVienDAO dao;
    
    // --- CẬP NHẬT: Thêm biến cho phân trang ---
    private List<NhanVien> allData;
    private PhanTrangPanel phanTrang;

    public NhanVienPanel() {
        dao = new NhanVienDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Nhân Viên");
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
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Nhân Viên",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;

        int y = 0;
        form.add(createLabel("Mã NV:"), gbc); gbc.gridy = ++y;
        txtMaNV = LuxuryTheme.createTextField(); txtMaNV.setEditable(false); txtMaNV.setBackground(new Color(240, 240, 240));
        form.add(txtMaNV, gbc);

        gbc.gridy = ++y; form.add(createLabel("Họ Tên:"), gbc); gbc.gridy = ++y;
        txtTenNV = LuxuryTheme.createTextField();
        // Chỉ cho phép chữ (bao gồm khoảng trắng và dấu tiếng Việt)
        txtTenNV.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Cho phép chữ cái, khoảng trắng, dấu tiếng Việt
                if (!Character.isLetter(c) && c != ' ' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });
        form.add(txtTenNV, gbc);

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

        gbc.gridy = ++y; form.add(createLabel("Giới Tính:"), gbc); gbc.gridy = ++y;
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nu"}); 
        cbGioiTinh.setBackground(Color.WHITE);
        cbGioiTinh.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(cbGioiTinh, gbc);

        gbc.gridy = ++y; form.add(createLabel("Chức Vụ:"), gbc); gbc.gridy = ++y;
        cbChucVu = new JComboBox<>(new String[]{"Lễ tân", "Quản lý", "Phục vụ"}); 
        cbChucVu.setBackground(Color.WHITE);
        cbChucVu.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(cbChucVu, gbc);

        gbc.gridy = ++y; form.add(createLabel("Ngày Sinh:"), gbc); gbc.gridy = ++y;
        dateChooser = new JDateChooser(); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(dateChooser, gbc);

        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);
        // --- CẬP NHẬT: Thêm nút Mới ---
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                if (!validateForm()) return;
                NhanVien nv = taoNhanVienTuForm();
                dao.themNhanVien(nv);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                if (!validateForm()) return;
                NhanVien nv = taoNhanVienTuForm();
                dao.capNhatNhanVien(nv);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.xoaNhanVien(txtMaNV.getText()); refreshForm();
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

    private NhanVien taoNhanVienTuForm() {
        NhanVien nv = new NhanVien();
        nv.setMaNV(txtMaNV.getText());
        nv.setTenNV(txtTenNV.getText());
        nv.setSoDienThoai(txtSDT.getText());
        nv.setGioiTinh(cbGioiTinh.getSelectedItem().toString());
        nv.setChucVu(cbChucVu.getSelectedItem().toString());
        
        java.util.Date utilDate = dateChooser.getDate();
        if(utilDate != null) {
            LocalDate ld = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            nv.setNgaySinh(ld);
        }
        return nv;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã/Tên): "));
        txtSearch = LuxuryTheme.createTextField(); txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> searchNhanVien());
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã NV", "Họ Tên", "SĐT", "Giới Tính", "Chức Vụ", "Ngày Sinh"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); // ĐỒNG BỘ 40px
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaNV.setText(table.getValueAt(r, 0).toString());
                txtTenNV.setText(table.getValueAt(r, 1).toString());
                txtSDT.setText(table.getValueAt(r, 2).toString());
                cbGioiTinh.setSelectedItem(table.getValueAt(r, 3).toString());
                cbChucVu.setSelectedItem(table.getValueAt(r, 4).toString());
                
                String dateStr = table.getValueAt(r, 5).toString();
                if(!dateStr.isEmpty()) {
                    LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    dateChooser.setDate(java.sql.Date.valueOf(ld));
                } else {
                    dateChooser.setDate(null);
                }
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
        txtMaNV.setText(dao.sinhMaMoi());
        txtTenNV.setText(""); txtSDT.setText(""); dateChooser.setDate(null); txtSearch.setText("");
        cbGioiTinh.setSelectedIndex(0); cbChucVu.setSelectedIndex(0);
        loadData(dao.layTatCaNhanVien());
    }

    // --- LOGIC PHÂN TRANG ---
    private void loadData(List<NhanVien> list) {
        allData = list;
        phanTrang.setTotalItems(allData.size());
        updateTableDisplay();
    }

    private void searchNhanVien() {
        phanTrang.setCurrentPage(1);
        loadData(FuzzySearch.filter(dao.layTatCaNhanVien(), txtSearch.getText().trim(),
            NhanVien::getMaNV,
            NhanVien::getTenNV,
            NhanVien::getSoDienThoai,
            NhanVien::getGioiTinh,
            NhanVien::getChucVu
        ));
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        if (allData == null || allData.isEmpty()) {
            return;
        }
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();
        
        for (int i = start; i < end; i++) {
            NhanVien nv = allData.get(i);
            String ngaySinh = nv.getNgaySinh() != null ? nv.getNgaySinh().format(fmt) : "";
            tableModel.addRow(new Object[]{nv.getMaNV(), nv.getTenNV(), nv.getSoDienThoai(), nv.getGioiTinh(), nv.getChucVu(), ngaySinh});
        }
    }

    // Validation method
    private boolean validateForm() {
        String tenNV = txtTenNV.getText().trim();
        String sdt = txtSDT.getText().trim();
        
        if (tenNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhân viên!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!ValidationUtils.isPersonName(tenNV)) {
            JOptionPane.showMessageDialog(this, "Tên chỉ được phép chứa chữ cái!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!ValidationUtils.isPhone(sdt)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải đúng 10 chữ số!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (dateChooser.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày sinh!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        LocalDate ngaySinh = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (!ngaySinh.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this, "Ngày sinh phải nhỏ hơn ngày hiện tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        for (NhanVien nv : dao.layTatCaNhanVien()) {
            if (nv.getSoDienThoai() != null && nv.getSoDienThoai().equals(sdt) && !nv.getMaNV().equals(txtMaNV.getText())) {
                JOptionPane.showMessageDialog(this, "Số điện thoại nhân viên đã tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        
        return true;
    }
}
