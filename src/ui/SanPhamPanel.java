package ui;

import dao.SanPhamDAO;
import dao.NhaCungCapDAO;
import model.SanPham;
import model.NhaCungCap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SanPhamPanel extends JPanel {
    private JTextField txtMaSP, txtTenSP, txtGiaBan, txtSoLuong, txtSearch;
    private JComboBox<String> cbLoai, cbNhaCungCap;
    private DefaultTableModel tableModel;
    private JTable table;
    private SanPhamDAO dao;
    private JLabel lblHinhAnh;
    private JLabel lblGiaNhapTB; 
    private String currentImagePath = ""; 
    private File selectedFileToCopy = null; 
    private List<SanPham> allSanPham;
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 10;
    private JLabel lblPageInfo;

    public SanPhamPanel() {
        dao = new SanPhamDAO();
        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JLabel lblHeader = new JLabel("Quản Lý Sản Phẩm Dịch Vụ");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 28));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        // Đã cập nhật Form bên trái với JScrollPane và Docked Buttons
        add(createFormPanel(), BorderLayout.WEST);
        add(createTableAndSearchPanel(), BorderLayout.CENTER);

        refreshForm();
    }

    private JPanel createFormPanel() {
        // 1. WRAPPER PANEL: Chứa ScrollPane (Ở giữa) và Panel Nút (Ở dưới cùng)
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setBackground(LuxuryTheme.CREAM);
        // Tăng chiều rộng lên 420px theo yêu cầu
        wrapper.setPreferredSize(new Dimension(420, 0));
        wrapper.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Sản Phẩm", 
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        
        // 2. FORM PANEL: Nơi chứa toàn bộ các ô nhập liệu sẽ được cuộn
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        // Tăng thêm 10px Padding (khoảng cách) giữa các dòng cho thoáng
        gbc.insets = new Insets(17, 10, 17, 10); 
        gbc.weightx = 1.0;

        int y = 0;
        gbc.gridy = y++; form.add(createLabel("Mã SP:"), gbc);
        txtMaSP = LuxuryTheme.createTextField(); 
        txtMaSP.setEditable(false); 
        txtMaSP.setBackground(new Color(240, 240, 240));
        gbc.gridy = y++; form.add(txtMaSP, gbc);

        gbc.gridy = y++; form.add(createLabel("Tên Sản Phẩm:"), gbc);
        txtTenSP = LuxuryTheme.createTextField(); gbc.gridy = y++; form.add(txtTenSP, gbc);

        gbc.gridy = y++; form.add(createLabel("Phân Loại:"), gbc);
        cbLoai = new JComboBox<>(new String[]{"DO_AN", "DO_UONG", "DUNG_CU"}); 
        cbLoai.setBackground(Color.WHITE); cbLoai.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridy = y++; form.add(cbLoai, gbc);

        gbc.gridy = y++; form.add(createLabel("Giá Bán (VNĐ):"), gbc);
        txtGiaBan = LuxuryTheme.createTextField(); txtGiaBan.setText("0"); gbc.gridy = y++; form.add(txtGiaBan, gbc);

        gbc.gridy = y++; form.add(createLabel("Số Lượng Tồn:"), gbc);
        txtSoLuong = LuxuryTheme.createTextField(); txtSoLuong.setText("0"); gbc.gridy = y++; form.add(txtSoLuong, gbc);

        gbc.gridy = y++; form.add(createLabel("Nhà Cung Cấp:"), gbc);
        cbNhaCungCap = new JComboBox<>(); 
        cbNhaCungCap.setBackground(Color.WHITE); cbNhaCungCap.setFont(new Font("Arial", Font.PLAIN, 15));
        gbc.gridy = y++; form.add(cbNhaCungCap, gbc);

        // --- CẬP NHẬT: THIẾT KẾ LẠI KHU VỰC HÌNH ẢNH DỌC (Nút nằm dưới ảnh) ---
        gbc.gridy = y++; form.add(createLabel("Hình Ảnh:"), gbc);
        JPanel pnlAnh = new JPanel();
        pnlAnh.setLayout(new BoxLayout(pnlAnh, BoxLayout.Y_AXIS)); // Sắp xếp dọc
        pnlAnh.setBackground(LuxuryTheme.CREAM);
        
        lblHinhAnh = new JLabel("CHƯA CÓ ẢNH", SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(140, 140)); // Tăng size ảnh lên 1 chút cho đẹp
        lblHinhAnh.setMaximumSize(new Dimension(140, 140));
        lblHinhAnh.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 2));
        lblHinhAnh.setForeground(Color.GRAY);
        lblHinhAnh.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        
        JButton btnChonAnh = LuxuryTheme.createButton("Chọn File Ảnh", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnChonAnh.setPreferredSize(new Dimension(140, 40));
        btnChonAnh.setMaximumSize(new Dimension(140, 40));
        btnChonAnh.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        btnChonAnh.addActionListener(e -> chonAnhTuMayTinh());
        
        pnlAnh.add(lblHinhAnh);
        pnlAnh.add(Box.createVerticalStrut(15)); // Tạo khoảng cách 15px giữa Ảnh và Nút
        pnlAnh.add(btnChonAnh);
        
        gbc.gridy = y++; form.add(pnlAnh, gbc);

        lblGiaNhapTB = new JLabel("Giá nhập gốc (TB): 0 đ");
        lblGiaNhapTB.setFont(new Font("Arial", Font.ITALIC, 14));
        lblGiaNhapTB.setForeground(Color.RED);
        gbc.gridy = y++; form.add(lblGiaNhapTB, gbc);

        // Thêm khoảng đệm dưới cùng cho form
        gbc.gridy = y++; form.add(Box.createVerticalStrut(20), gbc);

        // 3. JSCROLLPANE: Bọc cái form lại, thiết lập thanh cuộn Luxury
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setBorder(null); // Xóa viền của ScrollPane
        scrollPane.getViewport().setBackground(LuxuryTheme.CREAM);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Luôn luôn hiện thanh cuộn
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Lăn chuột mượt hơn
        
        // Gọi class LuxuryScrollBarUI (Được định nghĩa ở cuối file)
        scrollPane.getVerticalScrollBar().setUI(new LuxuryScrollBarUI());

        // 4. BUTTON PANEL: (Sẽ được Neo dính chặt ở phía dưới Wrapper)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        btnPanel.setBackground(LuxuryTheme.CREAM);
        btnPanel.setBorder(new EmptyBorder(10, 0, 15, 0)); // Padding cho khu vực nút

        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                SanPham sp = taoSanPhamTuForm();
                dao.themSanPham(sp);
                refreshForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                SanPham sp = taoSanPhamTuForm();
                dao.capNhatSanPham(sp);
                refreshForm();
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            dao.xoaSanPham(txtMaSP.getText());
            refreshForm();
        });

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete);

        // LẮP RÁP: ScrollPane ở Giữa, ButtonPanel ở Dưới (Neo cố định)
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(btnPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private void chonAnhTuMayTinh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFileToCopy = fileChooser.getSelectedFile();
            hienThiAnh(selectedFileToCopy.getAbsolutePath()); 
        }
    }

    private String luuAnhVaoProject(File sourceFile) {
        if (sourceFile == null) return currentImagePath; 
        try {
            File dir = new File("src/image/products");
            if (!dir.exists()) dir.mkdirs();
            String extension = sourceFile.getName().substring(sourceFile.getName().lastIndexOf('.'));
            String newFileName = "SP_" + System.currentTimeMillis() + extension;
            File destFile = new File(dir, newFileName);
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destFile.getPath().replace("\\", "/"); 
        } catch (Exception e) { return currentImagePath; }
    }

    private void hienThiAnh(String path) {
        if (path != null && !path.trim().isEmpty()) {
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                lblHinhAnh.setIcon(new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH)));
                lblHinhAnh.setText(""); return;
            }
        }
        lblHinhAnh.setIcon(null); lblHinhAnh.setText("CHƯA CÓ ẢNH");
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(LuxuryTheme.CREAM);

        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Loại", "Giá Bán", "Tồn Kho", "Mã NCC"}, 0);
        table = new JTable(tableModel); table.setRowHeight(40);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD); table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                String ma = table.getValueAt(r, 0).toString();
                SanPham sp = dao.layTheoId(ma);
                if (sp != null) {
                    txtMaSP.setText(sp.getMaSP());
                    txtTenSP.setText(sp.getTenSP());
                    cbLoai.setSelectedItem(sp.getLoaiSP());
                    txtGiaBan.setText(String.format("%.0f", sp.getGiaBan()));
                    txtSoLuong.setText(String.valueOf(sp.getSoLuongTon()));
                    for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) {
                        if (cbNhaCungCap.getItemAt(i).startsWith(sp.getMaNCC())) { cbNhaCungCap.setSelectedIndex(i); break; }
                    }
                    selectedFileToCopy = null; 
                    currentImagePath = sp.getHinhAnh();
                    hienThiAnh(currentImagePath);
                    
                    double giaTB = dao.tinhGiaNhapTrungBinh(ma);
                    lblGiaNhapTB.setText("Giá nhập gốc (TB): " + String.format("%,.0f đ", giaTB));
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void loadNhaCungCapToComboBox() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCap> list = new NhaCungCapDAO().getAllNhaCungCap();
        for (NhaCungCap ncc : list) cbNhaCungCap.addItem(ncc.getMaNCC() + " - " + ncc.getTenCongTy());
    }

    private SanPham taoSanPhamTuForm() {
        SanPham sp = new SanPham();
        sp.setMaSP(txtMaSP.getText());
        sp.setTenSP(txtTenSP.getText());
        sp.setLoaiSP(cbLoai.getSelectedItem().toString());
        sp.setGiaBan(Double.parseDouble(txtGiaBan.getText().trim()));
        sp.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim()));
        sp.setMaNCC(cbNhaCungCap.getSelectedItem().toString().split(" - ")[0]);
        sp.setHinhAnh(luuAnhVaoProject(selectedFileToCopy));
        return sp;
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
        selectedFileToCopy = null; currentImagePath = ""; hienThiAnh(""); 
        lblGiaNhapTB.setText("Giá nhập gốc (TB): 0 đ");

        loadNhaCungCapToComboBox();
        allSanPham = dao.getAllSanPham();
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        if (allSanPham == null) return;
        for (SanPham sp : allSanPham) {
            tableModel.addRow(new Object[]{ sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), String.format("%,.0f", sp.getGiaBan()), sp.getSoLuongTon(), sp.getMaNCC() });
        }
    }

  // =====================================================================================
    // CLASS CUSTOM SCROLLBAR LUXURY: Đã đổi sang màu VÀNG GOLD
    // =====================================================================================
    private class LuxuryScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        
        @Override
        protected void configureScrollBarColors() {
            this.trackColor = LuxuryTheme.CREAM; // Nền thanh cuộn giữ nguyên màu kem
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }

        @Override
        protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0, 0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }

        // --- ĐÂY LÀ NƠI CHỈNH MÀU THANH CUỘN (THUMB) ---
        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) { return; }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // CHỈNH TẠI ĐÂY: Sử dụng màu GOLD từ Theme của bạn
            // (180 là độ đậm nhạt của màu, bạn có thể chỉnh từ 0 - 255)
            Color goldColor = new Color(LuxuryTheme.GOLD.getRed(), LuxuryTheme.GOLD.getGreen(), LuxuryTheme.GOLD.getBlue(), 180);
            g2.setPaint(goldColor); 
            
            // Vẽ thanh cuộn bo góc
            g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 2, thumbBounds.width - 6, thumbBounds.height - 4, 10, 10);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new Color(235, 232, 245)); 
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}