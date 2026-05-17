package ui;

import dao.BanBidaDAO;
import dao.PhienChoiDAO;
import model.BanBida;
import model.PhienChoi;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;

public class SoDoBanPanel extends JPanel {
    private BanBidaDAO banDao = new BanBidaDAO();
    private PhienChoiDAO phienDao = new PhienChoiDAO(); 
    
    private JPanel pnlNormal, pnlVip;
    private Icon iconTrong;
    private Icon iconActive;

    // Class chống mờ ảnh cho màn hình độ phân giải cao
    private static class HiDPIIcon implements Icon {
        private Image img;
        private int logicalWidth, logicalHeight;

        public HiDPIIcon(Image img, int width, int height) {
            this.img = img;
            this.logicalWidth = width;
            this.logicalHeight = height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(img, x, y, logicalWidth, logicalHeight, null);
            g2.dispose();
        }

        @Override
        public int getIconWidth() { return logicalWidth; }
        @Override
        public int getIconHeight() { return logicalHeight; }
    }

    public SoDoBanPanel() {
        try {
            Image imgDisable = ImageIO.read(new File("src/image/BiaDisable.png"));
            Image imgActive = ImageIO.read(new File("src/image/BiaActive.png"));
            iconTrong = new HiDPIIcon(imgDisable, 180, 110);
            iconActive = new HiDPIIcon(imgActive, 180, 110);
        } catch (Exception e) {
            System.err.println("Lỗi tải ảnh: " + e.getMessage());
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(25, 30, 25, 30)); 

        JPanel mainGrid = new JPanel();
        mainGrid.setLayout(new BoxLayout(mainGrid, BoxLayout.Y_AXIS));
        mainGrid.setOpaque(false);
        mainGrid.setBorder(new EmptyBorder(0, 0, 0, 25));

        // =========================================================================
        // KHU VỰC BÀN THƯỜNG
        // =========================================================================
        JPanel sectionNormal = new JPanel(new BorderLayout());
        sectionNormal.setOpaque(false);

        JPanel headerNormal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LuxuryTheme.NAVY);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        headerNormal.setPreferredSize(new Dimension(0, 45));
        headerNormal.setOpaque(false);
        
        // --- TÁCH EMOJI VÀ TEXT RA 2 ELEMENT RIÊNG BIỆT ---
        JPanel pnlTitleNormal = new JPanel(new BorderLayout(10, 0));
        pnlTitleNormal.setOpaque(false);
        pnlTitleNormal.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel lblNormalIcon = new JLabel("🎱");
        lblNormalIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18)); // Font chuyên trị Emoji
        lblNormalIcon.setForeground(Color.WHITE);
        
        JLabel lblNormalText = new JLabel("KHU VỰC BÀN THƯỜNG (50.000đ/h)");
        lblNormalText.setFont(new Font("Arial", Font.BOLD, 15)); // Font chuẩn cho Text
        lblNormalText.setForeground(Color.WHITE);
        
        pnlTitleNormal.add(lblNormalIcon, BorderLayout.WEST);
        pnlTitleNormal.add(lblNormalText, BorderLayout.CENTER);
        
        headerNormal.add(pnlTitleNormal, BorderLayout.CENTER);
        sectionNormal.add(headerNormal, BorderLayout.NORTH);

        pnlNormal = new JPanel(new GridLayout(0, 4, 20, 25));
        pnlNormal.setOpaque(false);
        pnlNormal.setBorder(new EmptyBorder(20, 10, 20, 10)); 
        sectionNormal.add(pnlNormal, BorderLayout.CENTER);


        // =========================================================================
        // KHU VỰC BÀN VIP
        // =========================================================================
        JPanel sectionVip = new JPanel(new BorderLayout());
        sectionVip.setOpaque(false);

        JPanel headerVip = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(LuxuryTheme.GOLD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
            }
        };
        headerVip.setPreferredSize(new Dimension(0, 45));
        headerVip.setOpaque(false);
        
        // --- TÁCH EMOJI VÀ TEXT RA 2 ELEMENT RIÊNG BIỆT ---
        JPanel pnlTitleVip = new JPanel(new BorderLayout(10, 0));
        pnlTitleVip.setOpaque(false);
        pnlTitleVip.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        JLabel lblVipIcon = new JLabel("⭐");
        lblVipIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        lblVipIcon.setForeground(LuxuryTheme.NAVY);
        
        JLabel lblVipText = new JLabel("KHU VỰC BÀN VIP (80.000đ/h)");
        lblVipText.setFont(new Font("Arial", Font.BOLD, 15));
        lblVipText.setForeground(LuxuryTheme.NAVY);
        
        pnlTitleVip.add(lblVipIcon, BorderLayout.WEST);
        pnlTitleVip.add(lblVipText, BorderLayout.CENTER);
        
        headerVip.add(pnlTitleVip, BorderLayout.CENTER);
        sectionVip.add(headerVip, BorderLayout.NORTH);

        pnlVip = new JPanel(new GridLayout(0, 4, 20, 25));
        pnlVip.setOpaque(false);
        pnlVip.setBorder(new EmptyBorder(20, 10, 20, 10));
        sectionVip.add(pnlVip, BorderLayout.CENTER);


        mainGrid.add(sectionNormal);
        mainGrid.add(Box.createVerticalStrut(35)); 
        mainGrid.add(sectionVip);

        // =========================================================================
        // CẤU HÌNH THANH CUỘN
        // =========================================================================
        JScrollPane scrollPane = new JScrollPane(mainGrid);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        scrollPane.getVerticalScrollBar().setUI(new InvisibleModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

        add(scrollPane, BorderLayout.CENTER);
        
        refreshMap();
    }

    public void refreshMap() {
        pnlNormal.removeAll(); 
        pnlVip.removeAll();
        for (BanBida ban : banDao.layTatCaBan()) {
            JButton btnBan = createTableButton(ban);
            if (ban.getLoaiBan().equals("VIP")) pnlVip.add(btnBan);
            else pnlNormal.add(btnBan);
        }
        revalidate(); 
        repaint();
    }

    private JButton createTableButton(BanBida ban) {
        boolean isPlaying = ban.getTrangThaiBan().equals("DANG_CHOI");
        String statusText = isPlaying ? "Đang chơi" : "Trống";
        
        String statusColor = isPlaying ? "#117E8D" : "#7F8C8D"; 
        String nameColor = isPlaying ? "#1A3263" : "#D68910";   

        String htmlNormal = "<html><center><div style='font-family:sans-serif; margin-top:6px;'>"
                          + "<b style='font-size:15px; color:" + nameColor + ";'>" + ban.getTenBan() + "</b><br>"
                          + "<span style='font-size:12px; font-weight:600; color:" + statusColor + ";'>" + statusText + "</span>"
                          + "</div></center></html>";
                          
        String htmlHover = "<html><center><div style='font-family:sans-serif; margin-top:6px;'>"
                          + "<b style='font-size:15px; color:#2980B9;'>" + ban.getTenBan() + "</b><br>"
                          + "<span style='font-size:12px; font-weight:600; color:" + statusColor + ";'>" + statusText + "</span>"
                          + "</div></center></html>";

        JButton btn = new JButton(htmlNormal);
        btn.setIcon(isPlaying ? iconActive : iconTrong);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(8); 
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btn.setText(htmlHover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btn.setText(htmlNormal); }
        });

        btn.addActionListener(e -> {
            if (!isPlaying) startTable(ban);
            else stopTable(ban);
        });
        
        return btn;
    }

    private void startTable(BanBida ban) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bắt đầu tính giờ cho " + ban.getTenBan() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                PhienChoi pc = new PhienChoi();
                pc.setMaPhien(phienDao.sinhMaMoi());
                pc.setMaBan(ban.getMaBan());
                pc.setThoiGianBatDau(LocalDateTime.now());
                pc.setTrangThaiPhien("DANG_CHOI");
                phienDao.themPhien(pc);

                banDao.capNhatTrangThai(ban.getMaBan(), "DANG_CHOI");
                refreshMap(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi kết nối Database mở bàn: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stopTable(BanBida ban) {
        try {
            PhienChoi pc = phienDao.timPhienDangChoiTheoBan(ban.getMaBan());
            
            if (pc != null) {
                ThanhToanDialog dialog = new ThanhToanDialog(null, ban, pc);
                dialog.setVisible(true);
                
                if (dialog.isPaid()) {
                    banDao.capNhatTrangThai(ban.getMaBan(), "TRONG");
                    refreshMap(); 
                }
            } else {
                int fix = JOptionPane.showConfirmDialog(this, 
                    "Bàn này đang bị kẹt ở trạng thái 'Đang chơi' nhưng không tìm thấy dữ liệu tính giờ trong Database!\n\n" +
                    "Bạn có muốn ép buộc đặt lại bàn này thành 'Trống' để sử dụng lại không?", 
                    "Phát hiện lỗi dữ liệu", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (fix == JOptionPane.YES_OPTION) {
                    banDao.capNhatTrangThai(ban.getMaBan(), "TRONG");
                    refreshMap();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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