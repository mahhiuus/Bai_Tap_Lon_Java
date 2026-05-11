package ui;

import dao.BanBidaDAO;
import model.BanBida;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BanBidaPanel extends JPanel {
    private JTextField txtMaBan, txtTenBan;
    private JComboBox<String> cbLoaiBan;
    private DefaultTableModel tableModel;
    private JTable table;
    private BanBidaDAO dao;

    public BanBidaPanel() {
        dao = new BanBidaDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // HEADER
        JLabel lblHeader = new JLabel("Quản Lý Danh Mục Bàn Bida");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        // BỐ CỤC CHÍNH
        add(createFormPanel(), BorderLayout.WEST);
        add(createTablePanel(), BorderLayout.CENTER);

        // Tải dữ liệu và mã mới lần đầu
        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setPreferredSize(new Dimension(350, 0));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin bàn",
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weightx = 1.0;

        // Mã Bàn (Read-only vì tự động sinh)
        gbc.gridy = 0; form.add(new JLabel("Mã Bàn:"), gbc);
        txtMaBan = LuxuryTheme.createTextField();
        txtMaBan.setEditable(false);
        txtMaBan.setBackground(new Color(240, 240, 240));
        gbc.gridy = 1; form.add(txtMaBan, gbc);

        // Tên Bàn
        gbc.gridy = 2; form.add(new JLabel("Tên Bàn:"), gbc);
        txtTenBan = LuxuryTheme.createTextField();
        gbc.gridy = 3; form.add(txtTenBan, gbc);

        // Loại Bàn
        gbc.gridy = 4; form.add(new JLabel("Loại Bàn:"), gbc);
        cbLoaiBan = new JComboBox<>(new String[]{"THUONG", "VIP"});
        cbLoaiBan.setFont(new Font("Arial", Font.PLAIN, 15));
        cbLoaiBan.setBackground(Color.WHITE);
        gbc.gridy = 5; form.add(cbLoaiBan, gbc);

        // Khu vực Nút Bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setOpaque(false);
        
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);

        // --- XỬ LÝ SỰ KIỆN THÊM ---
        btnAdd.addActionListener(e -> {
            try {
                String ten = txtTenBan.getText().trim();
                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên bàn không được để trống!");
                    return;
                }
                BanBida ban = new BanBida(txtMaBan.getText(), ten, cbLoaiBan.getSelectedItem().toString(), "TRONG");
                dao.themBan(ban);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm bàn thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // --- XỬ LÝ SỰ KIỆN SỬA ---
        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần sửa!");
                return;
            }
            String trangThai = table.getValueAt(row, 3).toString();
            if (trangThai.equals("DANG_CHOI")) {
                JOptionPane.showMessageDialog(this, "Không thể sửa bàn đang hoạt động!");
                return;
            }
            
            BanBida ban = new BanBida(txtMaBan.getText(), txtTenBan.getText(), cbLoaiBan.getSelectedItem().toString(), "TRONG");
            dao.capNhatBan(ban);
            refreshForm();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        });

        // --- XỬ LÝ SỰ KIỆN XÓA ---
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            
            String ma = table.getValueAt(row, 0).toString();
            String trangThai = table.getValueAt(row, 3).toString();
            
            if (trangThai.equals("DANG_CHOI")) {
                JOptionPane.showMessageDialog(this, "Không thể xóa bàn đang hoạt động!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận xóa bàn " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.xoaBan(ma);
                refreshForm();
            }
        });

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
        panel.setOpaque(false);

        tableModel = new DefaultTableModel(new String[]{"Mã Bàn", "Tên Bàn", "Loại", "Trạng Thái"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(45); // Tăng padding dòng theo yêu cầu
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Sự kiện click vào bảng đổ lên form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtMaBan.setText(table.getValueAt(row, 0).toString());
                txtTenBan.setText(table.getValueAt(row, 1).toString());
                cbLoaiBan.setSelectedItem(table.getValueAt(row, 2).toString());
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }

    private void refreshForm() {
        // Tải lại danh sách từ DB
        tableModel.setRowCount(0);
        List<BanBida> list = dao.layTatCaBan();
        for (BanBida b : list) {
            tableModel.addRow(new Object[]{b.getMaBan(), b.getTenBan(), b.getLoaiBan(), b.getTrangThaiBan()});
        }
        
        // Làm trống form và sinh mã mới
        txtMaBan.setText(dao.sinhMaMoi());
        txtTenBan.setText("");
        cbLoaiBan.setSelectedIndex(0);
    }
}