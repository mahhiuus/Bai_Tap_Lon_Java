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
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class MenuBanHangPanel extends JPanel {
    private JPanel pnlProductsGrid;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JComboBox<String> cbPhienChoi;
    private DefaultTableModel tableModel;
    private JTable table;
    private JLabel lblTongTien;
    private String currentCategory = "TẤT CẢ"; 

    private SanPhamDAO spDao = new SanPhamDAO();
    private PhienChoiDAO phienDao = new PhienChoiDAO();
    private ChiTietPhienDAO ctDao = new ChiTietPhienDAO();
    private BanBidaDAO banDao = new BanBidaDAO();
    
    private List<ChiTietPhien> currentOrderList; 

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
        setLayout(new BorderLayout(15, 0)); 
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(15, 20, 15, 20)); 

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createLeftPanel());
        splitPane.setRightComponent(createRightPanel());
        
        splitPane.setDividerLocation(920); 
        splitPane.setDividerSize(0);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);

        refreshPhienChoi();
        loadProducts(currentCategory);
    }

    private JPanel createLeftPanel() {
        JPanel pnlLeft = new JPanel(new BorderLayout(0, 20));
        pnlLeft.setOpaque(false);

        JPanel pnlTabs = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        pnlTabs.setOpaque(false);
        String[] displayNames = {"TẤT CẢ MÓN", "ĐỒ ĂN", "ĐỒ UỐNG", "DỊCH VỤ KHÁC"};
        String[] categories = {"TẤT CẢ", "DO_AN", "DO_UONG", "DUNG_CU"};

        for (int i = 0; i < categories.length; i++) {
            final String cat = categories[i];
            GlowButton btnTab = new GlowButton(displayNames[i], LuxuryTheme.NAVY, LuxuryTheme.GOLD);
            btnTab.setPreferredSize(new Dimension(160, 45));
            btnTab.addActionListener(e -> {
                currentCategory = cat;
                loadProducts(cat, txtSearch != null ? txtSearch.getText().trim() : "");
            });
            pnlTabs.add(btnTab);
        }

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("Tìm kiếm (Mã/Tên):");
        lblSearch.setFont(new Font("Arial", Font.BOLD, 13));
        lblSearch.setForeground(LuxuryTheme.NAVY);
        pnlSearch.add(lblSearch);

        txtSearch = LuxuryTheme.createTextField();
        txtSearch.setPreferredSize(new Dimension(260, 42));
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSearch.addActionListener(e -> {
            if (btnSearch != null) btnSearch.doClick();
        });
        pnlSearch.add(txtSearch);

        btnSearch = LuxuryTheme.createButton("Tìm Kiếm", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        btnSearch.setPreferredSize(new Dimension(120, 42));
        btnSearch.setFont(new Font("Arial", Font.BOLD, 13));
        btnSearch.addActionListener(e -> loadProducts(currentCategory, txtSearch.getText().trim()));
        pnlSearch.add(btnSearch);

        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.add(pnlTabs);
        pnlHeader.add(Box.createVerticalStrut(6));
        pnlHeader.add(pnlSearch);

        pnlLeft.add(pnlHeader, BorderLayout.NORTH);

        pnlProductsGrid = new JPanel(new GridLayout(0, 4, 15, 20)); 
        pnlProductsGrid.setOpaque(false);
        pnlProductsGrid.setBorder(new EmptyBorder(5, 5, 20, 5)); 

        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setOpaque(false);
        gridWrapper.add(pnlProductsGrid, BorderLayout.NORTH);
        
        gridWrapper.setBorder(new EmptyBorder(0, 0, 0, 25)); 

        JScrollPane scroll = new JScrollPane(gridWrapper);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scroll.getVerticalScrollBar().setUI(new InvisibleModernScrollBarUI());
        scroll.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        pnlLeft.add(scroll, BorderLayout.CENTER);
        return pnlLeft;
    }

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
        loadProducts(category, txtSearch != null ? txtSearch.getText().trim() : "");
    }

    private void loadProducts(String category, String keyword) {
        pnlProductsGrid.removeAll();

        String key = keyword == null ? "" : keyword.trim();
        List<SanPham> dsSP;

        if (key.isEmpty() && "TẤT CẢ".equalsIgnoreCase(category)) {
            dsSP = spDao.getAllSanPham();
        } else {
            List<SanPham> source = spDao.getAllSanPham();
            if (!"TẤT CẢ".equalsIgnoreCase(category)) {
                List<SanPham> byCategory = new java.util.ArrayList<>();
                for (SanPham sp : source) {
                    if (category.equals(sp.getLoaiSP())) byCategory.add(sp);
                }
                source = byCategory;
            }
            dsSP = FuzzySearch.filter(source, key,
                SanPham::getMaSP,
                SanPham::getTenSP,
                SanPham::getLoaiSP,
                SanPham::getMaNCC
            );
        }

        for (SanPham sp : dsSP) {
            int tonKhoAo = tinhTonKhoAo(sp);
            pnlProductsGrid.add(createProductCard(sp, tonKhoAo));
        }

        pnlProductsGrid.revalidate();
        pnlProductsGrid.repaint();
    }

    private JPanel createProductCard(SanPham sp, int tonKhoAo) {
        boolean outOfStock = tonKhoAo <= 0;
        
        ShadowRoundedPanel card = new ShadowRoundedPanel(new BorderLayout(0, 5), 15);
        card.setPreferredSize(new Dimension(195, 305)); 
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(8, 8, 12, 8)); 

        JLabel lblImg = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Shape clipShape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight() + 15, 15, 15);
                g2.clip(clipShape);
                
                super.paintComponent(g2); 
                
                if (outOfStock) {
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
                }
                g2.dispose();
            }
        };
        lblImg.setPreferredSize(new Dimension(179, 175));
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);
        
        try {
            String path = sp.getHinhAnh();
            if (path != null && new File(path).exists()) {
                BufferedImage img = ImageIO.read(new File(path));
                int w = img.getWidth(); int h = img.getHeight();
                int size = Math.min(w, h);
                BufferedImage squareImg = img.getSubimage((w - size) / 2, (h - size) / 2, size, size);
                lblImg.setIcon(new HiDPIIcon(squareImg, 179, 175));
            } else { 
                lblImg.setText("CHƯA CÓ ẢNH"); lblImg.setForeground(Color.LIGHT_GRAY); 
            }
        } catch (Exception e) { lblImg.setText("LỖI ẢNH"); }

        card.add(lblImg, BorderLayout.NORTH);

        JPanel pnlInfo = new JPanel(new GridLayout(3, 1, 0, 4));
        pnlInfo.setOpaque(false); 
        pnlInfo.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel name = new JLabel(sp.getTenSP(), SwingConstants.CENTER); 
        name.setFont(new Font("Arial", Font.BOLD, 14));
        name.setForeground(LuxuryTheme.NAVY);

        JLabel priceStock = new JLabel(String.format("%,.0fđ  |  Tồn: %d", sp.getGiaBan(), tonKhoAo), SwingConstants.CENTER);
        priceStock.setFont(new Font("Arial", Font.PLAIN, 13));
        priceStock.setForeground(outOfStock ? Color.GRAY : new Color(180, 0, 0));

        GlowButton btnAdd = new GlowButton("+ THÊM (1)", LuxuryTheme.TEAL, Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));

        if (outOfStock) {
            btnAdd.setEnabled(false); btnAdd.setText("TẠM HẾT"); btnAdd.setBaseColor(Color.LIGHT_GRAY);
        } else {
            btnAdd.addActionListener(e -> themVaoBan(sp, 1));
        }

        pnlInfo.add(name); pnlInfo.add(priceStock); pnlInfo.add(btnAdd);
        card.add(pnlInfo, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!outOfStock) { 
                    card.setHoverGlow(LuxuryTheme.GOLD); 
                    card.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
                }
            }
            public void mouseExited(MouseEvent e) { 
                card.setHoverGlow(null); 
            }
        });

        return card;
    }

    private JPanel createRightPanel() {
        ShadowRoundedPanel panel = new ShadowRoundedPanel(new BorderLayout(0, 15), 15);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 25, 30, 25)); 

        JPanel top = new JPanel(new BorderLayout(15, 10));
        top.setOpaque(false);
        
        // --- CẬP NHẬT: TÁCH RIÊNG EMOJI VÀ TEXT CHO TIÊU ĐỀ BILL ---
        JPanel pnlTitleBill = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlTitleBill.setOpaque(false);

        JLabel lblBillIcon = new JLabel("🛒");
        lblBillIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); // Font chuyên trị Emoji
        lblBillIcon.setForeground(LuxuryTheme.NAVY);

        JLabel lblBillText = new JLabel("CHI TIẾT BÀN ĐANG CHỌN:");
        lblBillText.setFont(new Font("Arial", Font.BOLD, 16)); // Font chuẩn cho tiếng Việt
        lblBillText.setForeground(LuxuryTheme.NAVY);

        pnlTitleBill.add(lblBillIcon);
        pnlTitleBill.add(lblBillText);
        top.add(pnlTitleBill, BorderLayout.NORTH);

        cbPhienChoi = new JComboBox<>();
        cbPhienChoi.setFont(new Font("Arial", Font.PLAIN, 16));
        cbPhienChoi.setBackground(Color.WHITE);
        cbPhienChoi.addActionListener(e -> loadOrderDetails());
        top.add(cbPhienChoi, BorderLayout.CENTER);

        // --- CẬP NHẬT: ÉP FONT EMOJI ĐỘC LẬP CHO NÚT SYNC ---
        GlowButton btnSync = new GlowButton("🔄", Color.GRAY, Color.WHITE);
        btnSync.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14)); // Hiển thị icon mượt mà
        btnSync.addActionListener(e -> refreshPhienChoi());
        top.add(btnSync, BorderLayout.EAST);
        
        panel.add(top, BorderLayout.NORTH);

        // --- CẬP NHẬT: TABLE TRẢ VỀ FONT ARIAL NGUYÊN BẢN ---
        tableModel = new DefaultTableModel(new String[]{"Món", "SL", "Tổng"}, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.getTableHeader().setBackground(LuxuryTheme.NAVY);
        table.getTableHeader().setForeground(LuxuryTheme.GOLD);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14)); // Trả về font Arial
        
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollTable, BorderLayout.CENTER);

        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        pnlControls.setOpaque(false);
        GlowButton btnPlus1 = new GlowButton("+ 1", LuxuryTheme.TEAL, Color.WHITE);
        GlowButton btnMinus1 = new GlowButton("- 1", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        GlowButton btnMinus10 = new GlowButton("- 10", LuxuryTheme.GOLD, LuxuryTheme.NAVY);
        GlowButton btnDelete = new GlowButton("Xóa Món", new Color(192, 57, 43), Color.WHITE);

        btnPlus1.addActionListener(e -> thayDoiSoLuongChoMonDangChon(1));
        btnMinus1.addActionListener(e -> thayDoiSoLuongChoMonDangChon(-1));
        btnMinus10.addActionListener(e -> thayDoiSoLuongChoMonDangChon(-10));
        btnDelete.addActionListener(e -> xoaMonDangChon());

        pnlControls.add(btnPlus1); pnlControls.add(btnMinus1); pnlControls.add(btnMinus10); pnlControls.add(btnDelete);

        JPanel bottom = new JPanel(new BorderLayout(0, 15));
        bottom.setOpaque(false);
        
        lblTongTien = new JLabel("Tiền Đồ Ăn: 0 đ", SwingConstants.RIGHT);
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 24)); 
        lblTongTien.setForeground(new Color(180, 0, 0));

        GlowButton btnPay = new GlowButton("THANH TOÁN BÀN NÀY", new Color(192, 57, 43), Color.WHITE);
        btnPay.setPreferredSize(new Dimension(0, 60)); 
        btnPay.setFont(new Font("Arial", Font.BOLD, 18));
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
        
        loadOrderDetails();
        loadProducts(currentCategory); 
    }

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

    // =========================================================================
    // CUSTOM CLASS: GLOW BUTTON ĐÃ ĐƯỢC TĂNG PADDING (10, 20, 10, 20)
    // =========================================================================
    class GlowButton extends JButton {
        private Color baseColor;
        private boolean isHovered = false;
        private int radius = 15;

        public GlowButton(String text, Color bg, Color fg) {
            super(text);
            this.baseColor = bg;
            setForeground(fg);
            setFont(new Font("Arial", Font.BOLD, 14));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            setBorder(new EmptyBorder(10, 20, 10, 20)); 

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (isEnabled()) { isHovered = true; repaint(); } }
                public void mouseExited(MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        public void setBaseColor(Color c) {
            this.baseColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(); int h = getHeight();
            int glowSize = 4; 

            int steps = isHovered ? 5 : 3; 
            int maxAlpha = isHovered ? 70 : 40; 
            for (int i = 0; i < steps; i++) {
                int alpha = maxAlpha - (maxAlpha / steps) * i;
                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha));
                g2.fill(new RoundRectangle2D.Double(glowSize - i, glowSize - i, w - glowSize*2 + i*2, h - glowSize*2 + i*2, radius + i*2, radius + i*2));
            }

            g2.setColor(isHovered ? baseColor.brighter() : baseColor);
            g2.fill(new RoundRectangle2D.Double(glowSize, glowSize, w - glowSize*2, h - glowSize*2, radius, radius));

            g2.dispose();
            super.paintComponent(g); 
        }
    }

    // =========================================================================
    // CUSTOM CLASS: PANEL ĐÃ ĐƯỢC GIẢM OPACITY BÓNG XUỐNG MỨC MỜ NHẸ (ALPHA = 60)
    // =========================================================================
    class ShadowRoundedPanel extends JPanel {
        private int cornerRadius;
        private Color glowColor = new Color(215, 218, 222, 60); 
        private Color hoverGlowColor = null; 
        private int glowSize = 8; 

        public ShadowRoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false); 
        }

        public void setHoverGlow(Color c) {
            this.hoverGlowColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - glowSize * 2;
            int height = getHeight() - glowSize * 2;

            Color drawColor = (hoverGlowColor != null) ? hoverGlowColor : glowColor;
            int steps = (hoverGlowColor != null) ? 6 : 5;
            int maxAlpha = (hoverGlowColor != null) ? 100 : glowColor.getAlpha();

            for (int i = 0; i < steps; i++) {
                int alpha = maxAlpha - (maxAlpha / steps) * i;
                g2.setColor(new Color(drawColor.getRed(), drawColor.getGreen(), drawColor.getBlue(), alpha));
                g2.fill(new RoundRectangle2D.Double(glowSize - i, glowSize - i, width + i*2, height + i*2, cornerRadius + i, cornerRadius + i));
            }

            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(glowSize, glowSize, width, height, cornerRadius, cornerRadius));

            if (hoverGlowColor != null) {
                g2.setColor(hoverGlowColor);
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(glowSize + 1, glowSize + 1, width - 2, height - 2, cornerRadius, cornerRadius));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class InvisibleModernScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            this.trackColor = new Color(0, 0, 0, 0); 
        }

        @Override protected JButton createDecreaseButton(int orientation) { return createZeroButton(); }
        @Override protected JButton createIncreaseButton(int orientation) { return createZeroButton(); }

        private JButton createZeroButton() {
            JButton button = new JButton();
            Dimension zeroDim = new Dimension(0, 0);
            button.setPreferredSize(zeroDim);
            button.setMinimumSize(zeroDim);
            button.setMaximumSize(zeroDim);
            return button;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            boolean isHovered = isThumbRollover();
            Color thumbColor = isHovered ? new Color(0, 0, 0, 50) : new Color(0, 0, 0, 18);
            
            g2.setPaint(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y, thumbBounds.width - 2, thumbBounds.height, 8, 8);
            g2.dispose();
        }
    }
}
