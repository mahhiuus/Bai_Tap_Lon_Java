package ui;

import dao.SanPhamDAO;
import dao.NhaCungCapDAO;
import model.SanPham;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SanPhamPanel extends JPanel {
    private JTextField txtMaSP, txtTenSP, txtGiaBan, txtSoLuong, txtSearch;
    private JComboBox<String> cbLoai, cbNhaCungCap;
    private DefaultTableModel tableModel;
    private JTable table;
    private SanPhamDAO dao;

    public SanPhamPanel() {
        dao = new SanPhamDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Sản Phẩm Dịch Vụ");
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
        form.setPreferredSize(new Dimension(350, 0));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Sản Phẩm",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;

        int y = 0;
        form.add(createLabel("Mã SP:"), gbc); gbc.gridy = ++y;
        txtMaSP = LuxuryTheme.createTextField(); txtMaSP.setEditable(false); txtMaSP.setBackground(new Color(240, 240, 240));
        form.add(txtMaSP, gbc);

        gbc.gridy = ++y; form.add(createLabel("Tên Sản Phẩm:"), gbc); gbc.gridy = ++y;
        txtTenSP = LuxuryTheme.createTextField(); form.add(txtTenSP, gbc);

        gbc.gridy = ++y; form.add(createLabel("Phân Loại:"), gbc); gbc.gridy = ++y;
        cbLoai = new JComboBox<>(new String[]{"DO_AN", "DO_UONG", "DUNG_CU"}); 
        cbLoai.setBackground(Color.WHITE);
        cbLoai.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(cbLoai, gbc);

        gbc.gridy = ++y; form.add(createLabel("Giá Bán:"), gbc); gbc.gridy = ++y;
        txtGiaBan = LuxuryTheme.createTextField(); form.add(txtGiaBan, gbc);

        gbc.gridy = ++y; form.add(createLabel("Số Lượng Tồn:"), gbc); gbc.gridy = ++y;
        txtSoLuong = LuxuryTheme.createTextField(); form.add(txtSoLuong, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhà Cung Cấp:"), gbc); gbc.gridy = ++y;
        cbNhaCungCap = new JComboBox<>(); cbNhaCungCap.setBackground(Color.WHITE);
        cbNhaCungCap.setFont(new Font("Arial", Font.PLAIN, 15));
        loadNhaCungCapToComboBox();
        form.add(cbNhaCungCap, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                SanPham sp = taoSanPhamTuForm();
                dao.themSanPham(sp); refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                SanPham sp = taoSanPhamTuForm();
                dao.capNhatSanPham(sp); refreshForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.xoaSanPham(txtMaSP.getText()); refreshForm();
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        gbc.gridy = ++y; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private void loadNhaCungCapToComboBox() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCap> list = new NhaCungCapDAO().getAllNhaCungCap();
        if (list.isEmpty()) { cbNhaCungCap.addItem("--- Chưa có nhà cung cấp ---"); return; }
        for (NhaCungCap ncc : list) {
            cbNhaCungCap.addItem(ncc.getMaNCC() + " - " + ncc.getTenCongTy());
        }
    }

    private SanPham taoSanPhamTuForm() {
        SanPham sp = new SanPham();
        sp.setMaSP(txtMaSP.getText());
        sp.setTenSP(txtTenSP.getText());
        sp.setLoaiSP(cbLoai.getSelectedItem().toString());
        sp.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim()));
        sp.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim()));
        
        String maNCC = cbNhaCungCap.getSelectedItem().toString().split(" - ")[0];
        sp.setMaNCC(maNCC);
        return sp;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Tên SP): "));
        txtSearch = LuxuryTheme.createTextField(); txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> loadData(dao.timKiemTheoTen(txtSearch.getText().trim())));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Phân Loại", "Giá Bán", "Tồn Kho", "Mã NCC"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); // ĐỒNG BỘ 40px
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaSP.setText(table.getValueAt(r, 0).toString());
                txtTenSP.setText(table.getValueAt(r, 1).toString());
                cbLoai.setSelectedItem(table.getValueAt(r, 2).toString());
                txtGiaBan.setText(table.getValueAt(r, 3).toString().replace(",", ""));
                txtSoLuong.setText(table.getValueAt(r, 4).toString());
                
                String maNCC = table.getValueAt(r, 5).toString();
                for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) {
                    if (cbNhaCungCap.getItemAt(i).startsWith(maNCC)) {
                        cbNhaCungCap.setSelectedIndex(i); break;
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    private void refreshForm() {
        txtMaSP.setText(dao.sinhMaMoi());
        txtTenSP.setText(""); txtGiaBan.setText("0"); txtSoLuong.setText("0");
        cbLoai.setSelectedIndex(0);
        if (cbNhaCungCap.getItemCount() > 0) cbNhaCungCap.setSelectedIndex(0);
        loadData(dao.getAllSanPham());
    }

    private void loadData(List<SanPham> list) {
        tableModel.setRowCount(0);
        for (SanPham sp : list) {
            tableModel.addRow(new Object[]{sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), String.format("%,.0f", sp.getGiaBan()), sp.getSoLuongTon(), sp.getMaNCC()});
        }
    }
}