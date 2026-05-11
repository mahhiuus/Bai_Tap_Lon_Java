package ui;

import dao.HoaDonNhapDAO;
import dao.NhaCungCapDAO;
import dao.NhanVienDAO;
import model.HoaDonNhap;
import model.NhaCungCap;
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

public class HoaDonNhapPanel extends JPanel {
    private JTextField txtMaHDN, txtTongTien, txtSearch;
    private JComboBox<String> cbNhaCungCap, cbNhanVien;
    private JDateChooser dateChooser;
    private DefaultTableModel tableModel;
    private JTable table;
    private HoaDonNhapDAO dao;

    public HoaDonNhapPanel() {
        dao = new HoaDonNhapDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Hóa Đơn Nhập Hàng");
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
        form.setPreferredSize(new Dimension(380, 0));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Phiếu Nhập",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(10, 10, 10, 10); gbc.weightx = 1.0;

        int y = 0;
        form.add(createLabel("Mã Hóa Đơn Nhập:"), gbc); gbc.gridy = ++y;
        txtMaHDN = LuxuryTheme.createTextField(); 
        txtMaHDN.setEditable(false); 
        txtMaHDN.setBackground(new Color(240, 240, 240));
        form.add(txtMaHDN, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhà Cung Cấp:"), gbc); gbc.gridy = ++y;
        cbNhaCungCap = new JComboBox<>(); 
        cbNhaCungCap.setBackground(Color.WHITE);
        cbNhaCungCap.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(cbNhaCungCap, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhân Viên Nhập:"), gbc); gbc.gridy = ++y;
        cbNhanVien = new JComboBox<>(); 
        cbNhanVien.setBackground(Color.WHITE);
        cbNhanVien.setFont(new Font("Arial", Font.PLAIN, 15));
        form.add(cbNhanVien, gbc);

        loadDataToComboBox();

        gbc.gridy = ++y; form.add(createLabel("Ngày Nhập:"), gbc); gbc.gridy = ++y;
        dateChooser = new JDateChooser(); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Arial", Font.PLAIN, 15));
        dateChooser.setDate(new java.util.Date()); 
        form.add(dateChooser, gbc);

        gbc.gridy = ++y; form.add(createLabel("Tổng Tiền (VNĐ):"), gbc); gbc.gridy = ++y;
        txtTongTien = LuxuryTheme.createTextField(); 
        txtTongTien.setText("0");
        form.add(txtTongTien, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                if (checkComboBoxEmpty()) return;
                HoaDonNhap hdn = taoHoaDonNhapTuForm();
                dao.themHoaDonNhap(hdn);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn nhập thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                if (checkComboBoxEmpty()) return;
                HoaDonNhap hdn = taoHoaDonNhapTuForm();
                dao.capNhatHoaDonNhap(hdn);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa hóa đơn này?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.xoaHoaDonNhap(txtMaHDN.getText()); 
                refreshForm();
            }
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);
        gbc.gridy = ++y; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private void loadDataToComboBox() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCap> dsNCC = new NhaCungCapDAO().getAllNhaCungCap();
        if (dsNCC != null && !dsNCC.isEmpty()) {
            for (NhaCungCap ncc : dsNCC) cbNhaCungCap.addItem(ncc.getMaNCC() + " - " + ncc.getTenCongTy());
        } else {
            cbNhaCungCap.addItem("--- Chưa có dữ liệu NCC ---");
        }

        cbNhanVien.removeAllItems();
        List<NhanVien> dsNV = new NhanVienDAO().layTatCaNhanVien();
        if (dsNV != null && !dsNV.isEmpty()) {
            for (NhanVien nv : dsNV) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
        } else {
            cbNhanVien.addItem("--- Chưa có dữ liệu NV ---");
        }
    }

    private boolean checkComboBoxEmpty() {
        if (cbNhaCungCap.getSelectedItem().toString().contains("Chưa có dữ liệu") || 
            cbNhanVien.getSelectedItem().toString().contains("Chưa có dữ liệu")) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm Nhà Cung Cấp và Nhân Viên vào Database trước!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return true;
        }
        return false;
    }

    private HoaDonNhap taoHoaDonNhapTuForm() {
        HoaDonNhap hdn = new HoaDonNhap();
        hdn.setMaHDN(txtMaHDN.getText());
        
        hdn.setMaNCC(cbNhaCungCap.getSelectedItem().toString().split(" - ")[0]);
        hdn.setMaNV(cbNhanVien.getSelectedItem().toString().split(" - ")[0]);
        
        java.util.Date utilDate = dateChooser.getDate();
        if(utilDate != null) {
            LocalDate ld = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            hdn.setNgayNhap(ld);
        }

        double tongTien = 0;
        try {
            tongTien = Double.parseDouble(txtTongTien.getText().replace(",", "").replace(".", ""));
        } catch (Exception e) {}
        hdn.setTongTien(tongTien);

        return hdn;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Mã Hóa Đơn): "));
        txtSearch = LuxuryTheme.createTextField(); txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.addActionListener(e -> loadData(dao.timKiemTheoMa(txtSearch.getText().trim())));
        searchPanel.add(btnSearch);
        panel.add(searchPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Mã HĐN", "Mã NCC", "Mã NV", "Ngày Nhập", "Tổng Tiền"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); // ĐỒNG BỘ 40px
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaHDN.setText(table.getValueAt(r, 0).toString());
                
                String maNCC = table.getValueAt(r, 1).toString();
                for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) {
                    if (cbNhaCungCap.getItemAt(i).startsWith(maNCC)) { cbNhaCungCap.setSelectedIndex(i); break; }
                }

                String maNV = table.getValueAt(r, 2).toString();
                for (int i = 0; i < cbNhanVien.getItemCount(); i++) {
                    if (cbNhanVien.getItemAt(i).startsWith(maNV)) { cbNhanVien.setSelectedIndex(i); break; }
                }

                String dateStr = table.getValueAt(r, 3).toString();
                if(!dateStr.isEmpty()) {
                    LocalDate ld = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    dateChooser.setDate(java.sql.Date.valueOf(ld));
                }

                txtTongTien.setText(table.getValueAt(r, 4).toString().replace(",", ""));
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
        txtMaHDN.setText(dao.sinhMaMoi());
        txtTongTien.setText("0");
        dateChooser.setDate(new java.util.Date());
        if (cbNhaCungCap.getItemCount() > 0) cbNhaCungCap.setSelectedIndex(0);
        if (cbNhanVien.getItemCount() > 0) cbNhanVien.setSelectedIndex(0);
        loadData(dao.getAllHoaDonNhap());
    }

    private void loadData(List<HoaDonNhap> list) {
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (HoaDonNhap hdn : list) {
            String ngayNhap = hdn.getNgayNhap() != null ? hdn.getNgayNhap().format(fmt) : "";
            tableModel.addRow(new Object[]{
                hdn.getMaHDN(), 
                hdn.getMaNCC(), 
                hdn.getMaNV(), 
                ngayNhap, 
                String.format("%,.0f", hdn.getTongTien())
            });
        }
    }
}