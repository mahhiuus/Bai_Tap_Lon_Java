package ui;

import dao.KhachHangDAO;
import model.KhachHang;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

public class KhachHangPanel extends JPanel {
    
    private JTextField txtMaKH, txtTenKH, txtSdt;
    private DefaultTableModel tableModel;
    private JTable table;
    private KhachHangDAO dao;
    
    // Biến cho phân trang
    private List<KhachHang> allKhachHang;
    private PhanTrangPanel phanTrang; // SỬ DỤNG COMPONENT PHÂN TRANG MỚI

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

        // 2. KHU VỰC FORM
        add(createFormPanel(), BorderLayout.WEST);

        // 3. KHU VỰC TABLE & PHÂN TRANG
        add(createTablePanel(), BorderLayout.CENTER);

        // Tải dữ liệu ban đầu và sinh mã mới
        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setPreferredSize(new Dimension(350, 0)); // Chốt cứng chiều ngang form cho cân đối
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin chi tiết", 
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 10, 15, 10);
        gbc.weightx = 1.0;

        // Mã KH (Khóa cứng để không cho sửa, tự động sinh mã)
        gbc.gridy = 0; form.add(createLabel("Mã Khách Hàng:"), gbc);
        txtMaKH = LuxuryTheme.createTextField();
        txtMaKH.setEditable(false); 
        txtMaKH.setBackground(new Color(240, 240, 240));
        gbc.gridy = 1; form.add(txtMaKH, gbc);

        // Tên KH
        gbc.gridy = 2; form.add(createLabel("Tên Khách Hàng:"), gbc);
        txtTenKH = LuxuryTheme.createTextField();
        gbc.gridy = 3; form.add(txtTenKH, gbc);

        // SĐT
        gbc.gridy = 4; form.add(createLabel("Số Điện Thoại:"), gbc);
        txtSdt = LuxuryTheme.createTextField();
        gbc.gridy = 5; form.add(txtSdt, gbc);

        // --- CẬP NHẬT: Khung Nút Bấm đổi sang GridLayout 2x2 để chứa 4 nút không bị che ---
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        btnPanel.setBackground(LuxuryTheme.CREAM);
        
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE); // --- THÊM NÚT XÓA ---
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        // Sự kiện Thêm
        btnAdd.addActionListener(e -> {
            try {
                String ten = txtTenKH.getText().trim();
                if (ten.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống!");
                    return;
                }
                
                KhachHang kh = new KhachHang();
                kh.setMaKH(txtMaKH.getText()); // Lấy mã đã sinh tự động trên form
                kh.setTenKH(ten);
                kh.setSdt(txtSdt.getText().trim());
                kh.setNgayDangKy(LocalDate.now());
                
                dao.themKhachHang(kh);
                refreshForm(); // Làm mới lại form và sinh mã KH tiếp theo
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        // Sự kiện Xóa
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!");
                return;
            }
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    dao.xoaKhachHang(txtMaKH.getText());
                    refreshForm();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                }
            }
        });

        // CẬP NHẬT SỰ KIỆN NÚT MỚI: Làm mới dữ liệu và bỏ chọn bảng
        btnClear.addActionListener(e -> {
            refreshForm();
            table.clearSelection();
        });

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete); // Đưa nút xóa vào giao diện
        btnPanel.add(btnClear);

        gbc.gridy = 6;
        gbc.insets = new Insets(30, 10, 10, 10);
        form.add(btnPanel, gbc);

        return form;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(LuxuryTheme.CREAM);

        String[] cols = {"Mã KH", "Họ Tên", "SĐT", "Điểm", "Ngày ĐK"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setRowHeight(40); // ĐỒNG BỘ 40px
        table.setSelectionBackground(new Color(17, 126, 141, 50)); 

        // Sự kiện click vào dòng trong bảng
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaKH.setText(table.getValueAt(r, 0).toString());
                txtTenKH.setText(table.getValueAt(r, 1).toString());
                txtSdt.setText(table.getValueAt(r, 2).toString());
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

    // --- HÀM LÀM MỚI FORM VÀ SINH MÃ ---
    private void refreshForm() {
        txtMaKH.setText(dao.sinhMaMoi()); // Gọi lệnh tự sinh KH01, KH02...
        txtTenKH.setText("");
        txtSdt.setText("");
        loadDataToTable(); // Cập nhật lại bảng
    }

    // --- LOGIC DATA & PHÂN TRANG MỚI ---
    private void loadDataToTable() {
        allKhachHang = dao.getAllKhachHang();
        phanTrang.setTotalItems(allKhachHang.size());
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0); 
        if (allKhachHang == null || allKhachHang.isEmpty()) {
            return;
        }

        int start = phanTrang.getStartIndex();
        int end = phanTrang.getEndIndex();

        for (int i = start; i < end; i++) {
            KhachHang kh = allKhachHang.get(i);
            tableModel.addRow(new Object[]{
                kh.getMaKH(), kh.getTenKH(), kh.getSdt(), 
                kh.getDiemTichLuy(), kh.getNgayDangKy()
            });
        }
    }
}