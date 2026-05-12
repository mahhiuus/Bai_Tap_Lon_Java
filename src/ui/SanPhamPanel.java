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

    // Các biến cho tính năng Hình Ảnh
    private JLabel lblHinhAnh;
    private String currentImagePath = ""; // Đường dẫn hiện tại trong DB
    private File selectedFileToCopy = null; // File mới người dùng vừa chọn

    // Biến cho phân trang
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

        add(createFormPanel(), BorderLayout.WEST);
        add(createTableAndSearchPanel(), BorderLayout.CENTER);

        refreshForm();
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(LuxuryTheme.CREAM);
        form.setPreferredSize(new Dimension(380, 0));
        form.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY), "Thông tin Sản Phẩm", 
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(12, 10, 12, 10); gbc.weightx = 1.0;

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

        // --- KHU VỰC HÌNH ẢNH ---
        gbc.gridy = y++; form.add(createLabel("Hình Ảnh:"), gbc);
        
        JPanel pnlAnh = new JPanel(new BorderLayout(15, 0));
        pnlAnh.setBackground(LuxuryTheme.CREAM);
        
        lblHinhAnh = new JLabel("CHƯA CÓ ẢNH", SwingConstants.CENTER);
        lblHinhAnh.setPreferredSize(new Dimension(120, 120));
        lblHinhAnh.setBorder(BorderFactory.createDashedBorder(Color.GRAY, 2, 2));
        lblHinhAnh.setForeground(Color.GRAY);
        lblHinhAnh.setFont(new Font("Arial", Font.BOLD, 12));
        
        JButton btnChonAnh = LuxuryTheme.createButton("Chọn File Ảnh", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnChonAnh.setPreferredSize(new Dimension(120, 40));
        btnChonAnh.addActionListener(e -> chonAnhTuMayTinh());
        
        JPanel pnlBtnAnh = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBtnAnh.setOpaque(false);
        pnlBtnAnh.add(btnChonAnh);

        pnlAnh.add(lblHinhAnh, BorderLayout.WEST);
        pnlAnh.add(pnlBtnAnh, BorderLayout.CENTER);
        
        gbc.gridy = y++; form.add(pnlAnh, gbc);

        // --- NÚT CHỨC NĂNG ---
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnPanel.setBackground(LuxuryTheme.CREAM);
        JButton btnAdd = LuxuryTheme.createButton("Thêm", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnEdit = LuxuryTheme.createButton("Sửa", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnDelete = LuxuryTheme.createButton("Xóa", new Color(192, 57, 43), Color.WHITE);
        JButton btnClear = LuxuryTheme.createButton("Mới", Color.GRAY, Color.WHITE);

        btnAdd.addActionListener(e -> {
            try {
                if (cbNhaCungCap.getSelectedItem() == null || cbNhaCungCap.getSelectedItem().toString().contains("Chưa có")) {
                    JOptionPane.showMessageDialog(this, "Vui lòng thêm Nhà Cung Cấp trước!"); return;
                }
                SanPham sp = taoSanPhamTuForm();
                dao.themSanPham(sp);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Thêm thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnEdit.addActionListener(e -> {
            try {
                if (cbNhaCungCap.getSelectedItem() == null) return;
                SanPham sp = taoSanPhamTuForm();
                dao.capNhatSanPham(sp);
                refreshForm();
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); }
        });

        btnDelete.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Xác nhận xóa?", "Xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dao.xoaSanPham(txtMaSP.getText());
                refreshForm();
            }
        });

        btnClear.addActionListener(e -> refreshForm());

        btnPanel.add(btnAdd); btnPanel.add(btnEdit); btnPanel.add(btnDelete); btnPanel.add(btnClear);
        gbc.gridy = y++; gbc.insets = new Insets(20, 10, 10, 10); form.add(btnPanel, gbc);

        return form;
    }

    // Hàm gọi hộp thoại chọn ảnh
    private void chonAnhTuMayTinh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (JPG, PNG)", "jpg", "png", "jpeg"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFileToCopy = fileChooser.getSelectedFile();
            hienThiAnh(selectedFileToCopy.getAbsolutePath()); // Show preview tạm thời
        }
    }

    // Hàm copy ảnh vào source code khi Thêm/Sửa
    private String luuAnhVaoProject(File sourceFile) {
        if (sourceFile == null) return currentImagePath; // Nếu không chọn file mới, giữ nguyên đường dẫn cũ
        
        try {
            // Tạo thư mục nếu chưa có
            File dir = new File("src/image/products");
            if (!dir.exists()) dir.mkdirs();

            // Lấy đuôi file (.jpg, .png)
            String fileName = sourceFile.getName();
            String extension = "";
            int i = fileName.lastIndexOf('.');
            if (i > 0) extension = fileName.substring(i);

            // Đặt tên mới để không trùng (Ví dụ: SP_163456789.jpg)
            String newFileName = "SP_" + System.currentTimeMillis() + extension;
            File destFile = new File(dir, newFileName);

            // Copy file
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return destFile.getPath().replace("\\", "/"); // Chuẩn hóa đường dẫn
        } catch (Exception e) {
            e.printStackTrace();
            return currentImagePath; // Trả về ảnh cũ nếu copy lỗi
        }
    }

    // Hàm hiển thị hình ảnh lên khung 120x120
    private void hienThiAnh(String path) {
        if (path != null && !path.trim().isEmpty()) {
            File f = new File(path);
            if (f.exists() && !f.isDirectory()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                lblHinhAnh.setIcon(icon);
                lblHinhAnh.setText("");
                return;
            }
        }
        lblHinhAnh.setIcon(null);
        lblHinhAnh.setText("CHƯA CÓ ẢNH");
    }

    private JPanel createTableAndSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(LuxuryTheme.CREAM);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.add(createLabel("Tìm kiếm (Tên SP): "));
        txtSearch = LuxuryTheme.createTextField(); txtSearch.setPreferredSize(new Dimension(250, 35));
        searchPanel.add(txtSearch);
        JButton btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        
        btnSearch.addActionListener(e -> {
            allSanPham = dao.timKiemTheoTen(txtSearch.getText().trim());
            currentPage = 1; updateTableDisplay();
        });
        searchPanel.add(btnSearch); panel.add(searchPanel, BorderLayout.NORTH);

        // BẢNG (Chỉ giữ 6 cột gọn gàng)
        tableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Loại", "Giá Bán", "Tồn Kho", "Mã NCC"}, 0);
        table = new JTable(tableModel); table.setRowHeight(40);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY); table.getTableHeader().setForeground(LuxuryTheme.GOLD); table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionBackground(new Color(17, 126, 141, 50));
        
        // Sự kiện click vào bảng -> Hiển thị ảnh
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                String ma = table.getValueAt(r, 0).toString();
                
                // Load dữ liệu đầy đủ từ DB để lấy được hình ảnh
                SanPham sp = dao.layTheoId(ma);
                if (sp != null) {
                    txtMaSP.setText(sp.getMaSP());
                    txtTenSP.setText(sp.getTenSP());
                    cbLoai.setSelectedItem(sp.getLoaiSP());
                    txtGiaBan.setText(String.format("%.0f", sp.getGiaBan()));
                    txtSoLuong.setText(String.valueOf(sp.getSoLuongTon()));
                    
                    // Xử lý Nhà cung cấp
                    for (int i = 0; i < cbNhaCungCap.getItemCount(); i++) {
                        if (cbNhaCungCap.getItemAt(i).startsWith(sp.getMaNCC())) {
                            cbNhaCungCap.setSelectedIndex(i); break;
                        }
                    }

                    // Xử lý Ảnh
                    selectedFileToCopy = null; // Reset biến cờ copy
                    currentImagePath = sp.getHinhAnh();
                    hienThiAnh(currentImagePath);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(LuxuryTheme.NAVY, 1));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel pagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); pagPanel.setBackground(LuxuryTheme.CREAM);
        JButton btnPrev = LuxuryTheme.createButton("< Trước", LuxuryTheme.NAVY, Color.WHITE);
        JButton btnNext = LuxuryTheme.createButton("Sau >", LuxuryTheme.NAVY, Color.WHITE);
        lblPageInfo = new JLabel("Trang 1 / 1"); lblPageInfo.setFont(new Font("Arial", Font.BOLD, 14)); lblPageInfo.setForeground(LuxuryTheme.NAVY);

        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; updateTableDisplay(); } });
        btnNext.addActionListener(e -> { if (currentPage < Math.ceil((double)allSanPham.size() / ITEMS_PER_PAGE)) { currentPage++; updateTableDisplay(); } });

        pagPanel.add(btnPrev); pagPanel.add(lblPageInfo); pagPanel.add(btnNext);
        panel.add(pagPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadNhaCungCapToComboBox() {
        cbNhaCungCap.removeAllItems();
        List<NhaCungCap> list = new NhaCungCapDAO().getAllNhaCungCap();
        if (list.isEmpty()) { cbNhaCungCap.addItem("--- Chưa có NCC ---"); return; }
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
        
        // Gọi hàm copy ảnh và lấy đường dẫn lưu vào DB
        String finalPath = luuAnhVaoProject(selectedFileToCopy);
        sp.setHinhAnh(finalPath);
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
        txtTenSP.setText(""); txtGiaBan.setText("0"); txtSoLuong.setText("0"); txtSearch.setText("");
        cbLoai.setSelectedIndex(0);
        
        // Làm mới cờ và khung ảnh
        selectedFileToCopy = null;
        currentImagePath = "";
        hienThiAnh(""); 

        loadNhaCungCapToComboBox();
        
        allSanPham = dao.getAllSanPham();
        currentPage = 1;
        updateTableDisplay();
    }

    private void updateTableDisplay() {
        tableModel.setRowCount(0);
        if (allSanPham == null || allSanPham.isEmpty()) { lblPageInfo.setText("Trang 1 / 1"); return; }
        int maxPage = (int) Math.ceil((double) allSanPham.size() / ITEMS_PER_PAGE);
        lblPageInfo.setText("Trang " + currentPage + " / " + maxPage);

        int start = (currentPage - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allSanPham.size());

        for (int i = start; i < end; i++) {
            SanPham sp = allSanPham.get(i);
            tableModel.addRow(new Object[]{
                sp.getMaSP(), sp.getTenSP(), sp.getLoaiSP(), 
                String.format("%,.0f", sp.getGiaBan()), sp.getSoLuongTon(), sp.getMaNCC()
            });
        }
    }
}