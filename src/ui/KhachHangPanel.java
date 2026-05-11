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

        // Khung Nút Bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(LuxuryTheme.CREAM);
        
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
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

        // Sự kiện Làm Mới form (Bỏ chọn bảng, sinh mã mới)
        btnClear.addActionListener(e -> refreshForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
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

        // --- THANH PHÂN TRANG ---
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

    // --- HÀM LÀM MỚI FORM VÀ SINH MÃ ---
    private void refreshForm() {
        txtMaKH.setText(dao.sinhMaMoi()); // Gọi lệnh tự sinh KH01, KH02...
        txtTenKH.setText("");
        txtSdt.setText("");
        loadDataToTable(); // Cập nhật lại bảng
    }

    // --- LOGIC DATA & PHÂN TRANG ---
    private void loadDataToTable() {
        allKhachHang = dao.getAllKhachHang();
        currentPage = 1; 
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0); 
        if (allKhachHang == null || allKhachHang.isEmpty()) {
            lblPageInfo.setText("Trang 1 / 1");
            return;
        }

        int maxPage = (int) Math.ceil((double) allKhachHang.size() / ITEMS_PER_PAGE);
        lblPageInfo.setText("Trang " + currentPage + " / " + maxPage);

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