package ui;

import dao.BanBidaDAO;
import dao.ChiTietPhienDAO;
import dao.PhienChoiDAO;
import dao.SanPhamDAO;
import model.BanBida;
import model.ChiTietPhien;
import model.PhienChoi;
import model.SanPham;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuBanHangPanel extends JPanel {
    private JPanel pnlProductsGrid;
    private JComboBox<String> cbPhienChoi;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblTongTien;
    private String currentCategory = "TẤT CẢ"; 

    private SanPhamDAO spDao = new SanPhamDAO();
    private PhienChoiDAO phienDao = new PhienChoiDAO();
    private ChiTietPhienDAO ctDao = new ChiTietPhienDAO();
    private BanBidaDAO banDao = new BanBidaDAO();
    
    private List<ChiTietPhien> currentOrderList; // Để lấy món ra thao tác khi click

    private static class HiDPIIcon implements Icon {
        private Image img;
        private int width, height;

        public HiDPIIcon(Image img, int width, int height) {
            this.img = img; this.width = width; this.height = height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, x, y, width, height, null);
            g2.dispose();
        }

        @Override public int getIconWidth() { return width; }
        @Override public int getIconHeight() { return height; }
    }

    public MenuBanHangPanel() {
        setLayout(new BorderLayout(10, 0));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        splitPane.setDividerLocation(880); 
        splitPane.setDividerSize(0);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        refreshPhienChoi();
        loadProducts(currentCategory);
    }

    private JPanel createLeftPanel() {
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 15));
        pnlLeft.setOpaque(false);

        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTabs.setOpaque(false);
        String[] displayNames = {"TẤT CẢ MÓN", "ĐỒ ĂN", "ĐỒ UỐNG", "DỊCH VỤ KHÁC"};
        String[] categories = {"TẤT CẢ", "DO_AN", "DO_UONG", "DUNG_CU"};

        for (int i = 0; i < categories.length; i++) {
            final String cat = categories[i];
            JButton btnTab = LuxuryTheme.createButton(displayNames[i], LuxuryTheme.NAVY, LuxuryTheme.GOLD);
            btnTab.setPreferredSize(new Dimension(160, 45));
            btnTab.addActionListener(e -> {
                currentCategory = cat;
                loadProducts(cat);
            });
            pnlTabs.add(btnTab);
        }
        pnlLeft.add(pnlTabs, BorderLayout.NORTH);

        pnlProductsGrid = new JPanel(new GridLayout(0, 4, 15, 15));
        pnlProductsGrid.setOpaque(false);

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(pnlProductsGrid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);

        pnlLeft.add(scroll, BorderLayout.CENTER);
        return pnlLeft;
    }

    // --- TÍNH TỒN KHO ẢO ĐỂ HIỂN THỊ "HẾT HÀNG" ---
    private int tinhTonKhoAo(SanPham sp) {
        int reserved = 0;
        List<PhienChoi> allPhien = phienDao.layTatCaPhien();
        List<ChiTietPhien> allCTP = ctDao.layTatCaChiTietPhien();
        for (ChiTietPhien ct : allCTP) {
            if (ct.getMaSanPham().equals(sp.getMaSP())) {
                for(PhienChoi p : allPhien) {
                    if(p.getMaPhien().equals(ct.getMaPhien()) && "DANG_CHOI".equals(p.getTrangThaiPhien())) {
                        reserved += ct.getSoLuong();
                        break;
                    }
                }
            }
        }
        return sp.getSoLuongTon() - reserved;
    }

    private void loadProducts(String category) {
        pnlProductsGrid.removeAll();
        List<SanPham> dsSP = spDao.getAllSanPham();
        for (SanPham sp : dsSP) {
            if (category.equals("TẤT CẢ") || sp.getLoaiSP().equalsIgnoreCase(category)) {
                int tonKhoAo = tinhTonKhoAo(sp);
                pnlProductsGrid.add(createProductCard(sp, tonKhoAo));
            }
        }
        pnlProductsGrid.revalidate();
        pnlProductsGrid.repaint();
    }

    private JPanel createProductCard(SanPham sp, int tonKhoAo) {
        boolean outOfStock = tonKhoAo <= 0;
        
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setPreferredSize(new Dimension(200, 280)); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JLabel lblImg = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (outOfStock) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(0, 0, 0, 120));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Arial", Font.BOLD, 18));
                    FontMetrics fm = g2.getFontMetrics();
                    String text = "HẾT HÀNG";
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = (getHeight() + fm.getAscent()) / 2 - 5;
                    g2.setColor(new Color(200, 0, 0));
                    g2.fillRect(0, y - 22, getWidth(), 34);
                    g2.setColor(Color.WHITE);
                    g2.drawString(text, x, y + 2);
                    g2.dispose();
                }
            }
        };
        lblImg.setPreferredSize(new Dimension(190, 190));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            String path = sp.getHinhAnh();
            if (path != null && new File(path).exists()) {
                BufferedImage img = ImageIO.read(new File(path));
                int w = img.getWidth(); int h = img.getHeight();
                int size = Math.min(w, h);
                BufferedImage squareImg = img.getSubimage((w - size) / 2, (h - size) / 2, size, size);
                lblImg.setIcon(new HiDPIIcon(squareImg, 190, 190));
            } else { lblImg.setText("CHƯA CÓ ẢNH"); lblImg.setForeground(Color.LIGHT_GRAY); }
        } catch (Exception e) { lblImg.setText("LỖI ẢNH"); }

        card.add(lblImg, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new GridLayout(3, 1, 0, 2));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(5, 10, 8, 10));

        JLabel name = new JLabel(sp.getTenSP());
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setForeground(LuxuryTheme.NAVY);

        JLabel priceStock = new JLabel(String.format("%,.0fđ  |  Tồn: %d", sp.getGiaBan(), tonKhoAo));
        priceStock.setFont(new Font("Arial", Font.PLAIN, 12));
        priceStock.setForeground(outOfStock ? Color.GRAY : new Color(180, 0, 0));

        JButton btnAdd = LuxuryTheme.createButton("+ THÊM NỮA", LuxuryTheme.TEAL, Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setFocusPainted(false);

        if (outOfStock) {
            btnAdd.setEnabled(false); btnAdd.setText("TẠM HẾT"); btnAdd.setBackground(Color.LIGHT_GRAY);
        } else {
            btnAdd.addActionListener(e -> themVaoBan(sp, 1));
        }

        pnlInfo.add(name); pnlInfo.add(priceStock); pnlInfo.add(btnAdd);
        card.add(pnlInfo, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!outOfStock) { card.setBorder(BorderFactory.createLineBorder(LuxuryTheme.GOLD, 2)); card.setCursor(new Cursor(Cursor.HAND_CURSOR)); }
            }
            public void mouseExited(MouseEvent e) { card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); }
        });

        return card;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(LuxuryTheme.GOLD, 2), new EmptyBorder(15, 15, 15, 15)));

        JPanel top = new JPanel(new BorderLayout(10, 5));
        top.setOpaque(false);
        JLabel lblTitle = new JLabel("BÀN ĐANG PHỤC VỤ:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(LuxuryTheme.NAVY);
        top.add(lblTitle, BorderLayout.NORTH);

        cbPhienChoi = new JComboBox<>();
        cbPhienChoi.setFont(new Font("Arial", Font.PLAIN, 16));
        cbPhienChoi.addActionListener(e -> loadOrderDetails());
        top.add(cbPhienChoi, BorderLayout.CENTER);

        JButton btnSync = LuxuryTheme.createButton("🔄", Color.GRAY, Color.WHITE);
        btnSync.addActionListener(e -> refreshPhienChoi());
        top.add(btnSync, BorderLayout.EAST);
        panel.add(top, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{"Món", "SL", "Tổng"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // --- BỘ NÚT ĐIỀU CHỈNH SỐ LƯỢNG MỚI ĐƯỢC THÊM VÀO ĐÂY ---
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlControls.setOpaque(false);
        JButton btnPlus1 = LuxuryTheme.createButton("+ 1", LuxuryTheme.TEAL, Color.WHITE);
        JButton btnMinus1 = LuxuryTheme.createButton("- 1", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        JButton btnMinus10 = LuxuryTheme.createButton("- 10", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        JButton btnDelete = LuxuryTheme.createButton("Xóa Món", new Color(192, 57, 43), Color.WHITE);

        btnPlus1.addActionListener(e -> thayDoiSoLuongChoMonDangChon(1));
        btnMinus1.addActionListener(e -> thayDoiSoLuongChoMonDangChon(-1));
        btnMinus10.addActionListener(e -> thayDoiSoLuongChoMonDangChon(-10));
        btnDelete.addActionListener(e -> xoaMonDangChon());

        pnlControls.add(btnPlus1); pnlControls.add(btnMinus1); pnlControls.add(btnMinus10); pnlControls.add(btnDelete);

        JPanel bottom = new JPanel(new BorderLayout(0, 10));
        bottom.setOpaque(false);
        
        lblTongTien = new JLabel("Tiền Đồ Ăn: 0 đ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 20));
        lblTongTien.setForeground(new Color(180, 0, 0));

        JButton btnPay = LuxuryTheme.createButton("THANH TOÁN HÓA ĐƠN", new Color(192, 57, 43), Color.WHITE);
        btnPay.setPreferredSize(new Dimension(0, 55));
        btnPay.setFont(new Font("Arial", Font.BOLD, 16));
        btnPay.addActionListener(e -> moThanhToan());

        bottom.add(pnlControls, BorderLayout.NORTH);
        bottom.add(lblTongTien, BorderLayout.CENTER);
        bottom.add(btnPay, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshPhienChoi() {
        cbPhienChoi.removeAllItems();
        List<PhienChoi> ds = phienDao.layTatCaPhien();
        for (PhienChoi p : ds) {
            if ("DANG_CHOI".equals(p.getTrangThaiPhien())) {
                cbPhienChoi.addItem(p.getMaPhien() + " - Bàn: " + p.getMaBan());
            }
        }
        if (cbPhienChoi.getItemCount() == 0) cbPhienChoi.addItem("--- Chưa mở bàn ---");
        loadOrderDetails();
    }

    private void themVaoBan(SanPham sp, int qty) {
        if (cbPhienChoi.getSelectedItem() == null || cbPhienChoi.getSelectedItem().toString().contains("---")) {
            JOptionPane.showMessageDialog(this, "Vui lòng mở một bàn trước!", "Lỗi", 2); return;
        }

        String maPhien = cbPhienChoi.getSelectedItem().toString().split(" - ")[0];
        currentOrderList = ctDao.timTheoMaPhien(maPhien);
        boolean duplicate = false;
        
        // CHỈ THÊM VÀO BẢNG ORDER TẠM CỦA BÀN. CHƯA TRỪ KHỎI DATABASE LÚC NÀY.
        for (ChiTietPhien ct : currentOrderList) {
            if (ct.getMaSanPham().equals(sp.getMaSP())) {
                ct.setSoLuong(ct.getSoLuong() + qty);
                ctDao.capNhatChiTietPhien(ct);
                duplicate = true; break;
            }
        }
        if (!duplicate) {
            ctDao.themChiTietPhien(new ChiTietPhien(ctDao.sinhMaMoi(), maPhien, sp.getMaSP(), qty, sp.getGiaBan()));
        }
        
        // Load lại để thấy kho ảo bị trừ
        loadOrderDetails();
        loadProducts(currentCategory); 
    }

    // --- CÁC HÀM NÚT ĐIỀU CHỈNH SỐ LƯỢNG ---
    private void thayDoiSoLuongChoMonDangChon(int thayDoi) {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món trong bảng Order!"); return; }

        ChiTietPhien ct = currentOrderList.get(r);
        SanPham sp = spDao.layTheoId(ct.getMaSanPham());

        if (thayDoi > 0) {
            int tonKhoAo = tinhTonKhoAo(sp);
            if (tonKhoAo < thayDoi) { JOptionPane.showMessageDialog(this, "Hết hàng ảo! Không thể gọi thêm món này."); return; }
        }

        int soLuongMoi = ct.getSoLuong() + thayDoi;
        if (soLuongMoi <= 0) {
            ctDao.xoaChiTietPhien(ct.getMaChiTiet());
        } else {
            ct.setSoLuong(soLuongMoi);
            ctDao.capNhatChiTietPhien(ct);
        }

        loadOrderDetails();
        loadProducts(currentCategory); 
    }

    private void xoaMonDangChon() {
        int r = table.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 món trong bảng Order!"); return; }
        
        ChiTietPhien ct = currentOrderList.get(r);
        ctDao.xoaChiTietPhien(ct.getMaChiTiet());
        
        loadOrderDetails();
        loadProducts(currentCategory); 
    }
    // ----------------------------------------

    private void loadOrderDetails() {
        tableModel.setRowCount(0);
        if (cbPhienChoi.getSelectedItem() == null || cbPhienChoi.getSelectedItem().toString().contains("---")) {
            lblTongTien.setText("Tiền Đồ Ăn: 0 đ"); return;
        }
        String maPhien = cbPhienChoi.getSelectedItem().toString().split(" - ")[0];
        currentOrderList = ctDao.timTheoMaPhien(maPhien);
        
        double tong = 0;
        for (ChiTietPhien ct : currentOrderList) {
            SanPham sp = spDao.layTheoId(ct.getMaSanPham());
            double total = ct.getSoLuong() * ct.getDonGia();
            tableModel.addRow(new Object[]{ (sp != null ? sp.getTenSP() : ct.getMaSanPham()), ct.getSoLuong(), String.format("%,.0f", total)});
            tong += total;
        }
        lblTongTien.setText("Tiền Đồ Ăn: " + String.format("%,.0f đ", tong));
    }

    private void moThanhToan() {
        if (cbPhienChoi.getSelectedItem().toString().contains("---")) return;
        String combo = cbPhienChoi.getSelectedItem().toString();
        String maPhien = combo.split(" - ")[0];
        String maBan = combo.split("Bàn: ")[1];
        
        PhienChoi p = phienDao.timTheoMaPhien(maPhien);
        BanBida b = banDao.timTheoMaBan(maBan);
        
        if (p != null && b != null) {
            Frame f = (Frame) SwingUtilities.getWindowAncestor(this);
            ThanhToanDialog d = new ThanhToanDialog(f, b, p);
            d.setVisible(true);
            if (d.isPaid()) {
                banDao.capNhatTrangThai(b.getMaBan(), "TRONG");
                refreshPhienChoi();
                loadProducts(currentCategory); 
            }
        }
    }
}