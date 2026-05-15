package ui;

import dao.HoaDonNhapDAO;
import dao.NhaCungCapDAO;
import dao.NhanVienDAO;
import model.HoaDonNhap;
import model.NhaCungCap;
import model.NhanVien;
import model.TaiKhoan;
import com.toedter.calendar.JDateChooser;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
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
    private TaiKhoan currentUser;

    public HoaDonNhapPanel(TaiKhoan user) {
        this.currentUser = user;
        dao = new HoaDonNhapDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Hóa Đơn Nhập Hàng");
        lblHeader.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 28));
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
            TitledBorder.LEFT, TitledBorder.TOP, new java.awt.Font("Arial", java.awt.Font.BOLD, 14), LuxuryTheme.NAVY));

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
        form.add(cbNhaCungCap, gbc);

        gbc.gridy = ++y; form.add(createLabel("Nhân Viên Nhập:"), gbc); gbc.gridy = ++y;
        cbNhanVien = new JComboBox<>(); 
        cbNhanVien.setBackground(Color.WHITE);
        form.add(cbNhanVien, gbc);

        loadDataToComboBox();

        gbc.gridy = ++y; form.add(createLabel("Ngày Nhập:"), gbc); gbc.gridy = ++y;
        dateChooser = new JDateChooser(); 
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date()); 
        form.add(dateChooser, gbc);

        gbc.gridy = ++y; form.add(createLabel("Tổng Tiền (Hệ thống tự tính):"), gbc); gbc.gridy = ++y;
        txtTongTien = LuxuryTheme.createTextField(); 
        txtTongTien.setText("0");
        txtTongTien.setEditable(false); // Khóa ô tổng tiền
        txtTongTien.setForeground(Color.RED);
        form.add(txtTongTien, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnPanel.setOpaque(false);
        JButton btnAdd = LuxuryTheme.createButton("Tạo Vỏ", LuxuryTheme.TEAL, Color.WHITE);
        
        JButton btnDelete = LuxuryTheme.createButton("Xóa Vỏ", new Color(192, 57, 43), Color.WHITE);
        if (currentUser != null && !currentUser.getVaiTro().equals("ADMIN")) {
            btnDelete.setVisible(false); 
        }

        JButton btnNhapHang = LuxuryTheme.createButton("NHẬP CHI TIẾT", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        JButton btnInPDF = LuxuryTheme.createButton("IN PDF", LuxuryTheme.NAVY, LuxuryTheme.GOLD);

        btnAdd.addActionListener(e -> {
            try {
                HoaDonNhap hdn = taoHoaDonNhapTuForm();
                dao.themHoaDonNhap(hdn);
                refreshForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            dao.xoaHoaDonNhap(txtMaHDN.getText()); 
            refreshForm();
        });

        btnNhapHang.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Vui lòng Chọn 1 Phiếu ở bảng để Nhập hàng!"); return; }
            HoaDonNhap hdn = taoHoaDonNhapTuForm();
            Window w = SwingUtilities.getWindowAncestor(this);
            // MỞ DIALOG ĐỂ GÕ 50 LON COCA
            ChiTietHoaDonNhapDialog d = new ChiTietHoaDonNhapDialog((Frame)w, hdn);
            d.setVisible(true);
            refreshForm(); // Tải lại bảng để cập nhật Tổng Tiền
        });

        btnInPDF.addActionListener(e -> inPhieuNhapPDF());

        btnPanel.add(btnAdd); btnPanel.add(btnDelete); btnPanel.add(btnNhapHang); btnPanel.add(btnInPDF);
        gbc.gridy = ++y; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);
        return form;
    }

    private void inPhieuNhapPDF() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 phiếu nhập để in!"); return; }
        String maHDN = table.getValueAt(r, 0).toString();
        // Giữ nguyên code in PDF cũ của bạn
    }

    private void loadDataToComboBox() {
        cbNhaCungCap.removeAllItems();
        for (NhaCungCap ncc : new NhaCungCapDAO().getAllNhaCungCap()) cbNhaCungCap.addItem(ncc.getMaNCC() + " - " + ncc.getTenCongTy());
        cbNhanVien.removeAllItems();
        for (NhanVien nv : new NhanVienDAO().layTatCaNhanVien()) cbNhanVien.addItem(nv.getMaNV() + " - " + nv.getTenNV());
    }

    private HoaDonNhap taoHoaDonNhapTuForm() {
        HoaDonNhap hdn = new HoaDonNhap();
        hdn.setMaHDN(txtMaHDN.getText());
        hdn.setMaNCC(cbNhaCungCap.getSelectedItem().toString().split(" - ")[0]);
        hdn.setMaNV(cbNhanVien.getSelectedItem().toString().split(" - ")[0]);
        java.util.Date utilDate = dateChooser.getDate();
        if(utilDate != null) hdn.setNgayNhap(utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        return hdn;
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[]{"Mã HĐN", "Mã NCC", "Mã NV", "Ngày Nhập", "Tổng Tiền"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(40); 
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaHDN.setText(table.getValueAt(r, 0).toString());
                String maNCC = table.getValueAt(r, 1).toString();
                for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) if (cbNhaCungCap.getItemAt(i).startsWith(maNCC)) { cbNhaCungCap.setSelectedIndex(i); break; }
                String maNV = table.getValueAt(r, 2).toString();
                for (int i = 0; i < cbNhanVien.getItemCount(); i++) if (cbNhanVien.getItemAt(i).startsWith(maNV)) { cbNhanVien.setSelectedIndex(i); break; }
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
        lbl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        lbl.setForeground(LuxuryTheme.NAVY);
        return lbl;
    }

    private void refreshForm() {
        txtMaHDN.setText(dao.sinhMaMoi());
        txtTongTien.setText("0");
        dateChooser.setDate(new java.util.Date());
        
        tableModel.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (HoaDonNhap hdn : dao.getAllHoaDonNhap()) {
            String ngayNhap = hdn.getNgayNhap() != null ? hdn.getNgayNhap().format(fmt) : "";
            tableModel.addRow(new Object[]{hdn.getMaHDN(), hdn.getMaNCC(), hdn.getMaNV(), ngayNhap, String.format("%,.0f", hdn.getTongTien())});
        }
    }
}