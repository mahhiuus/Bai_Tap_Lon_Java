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
import java.util.HashMap;
import java.util.Map;

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
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel mainGrid = new JPanel();
        mainGrid.setLayout(new BoxLayout(mainGrid, BoxLayout.Y_AXIS));
        mainGrid.setOpaque(false);

        pnlNormal = new JPanel(new GridLayout(0, 5, 20, 20));
        pnlNormal.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.NAVY, 2), " KHU VỰC BÀN THƯỜNG (50.000đ/h) ", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.NAVY));
        pnlNormal.setOpaque(false);

        pnlVip = new JPanel(new GridLayout(0, 5, 20, 20));
        pnlVip.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(LuxuryTheme.GOLD, 2), " KHU VỰC BÀN VIP (80.000đ/h) ", 
            TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14), LuxuryTheme.GOLD));
        pnlVip.setOpaque(false);

        mainGrid.add(pnlNormal);
        mainGrid.add(Box.createVerticalStrut(30));
        mainGrid.add(pnlVip);

        JScrollPane scrollPane = new JScrollPane(mainGrid);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
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
        String statusColor = isPlaying ? "#117E8D" : "#888888"; 

        String htmlNormal = "<html><center><b style='font-size:16px; color:#FAB95B;'>" + ban.getTenBan() + 
                          "</b><br><span style='font-size:12px; color:" + statusColor + ";'>" + statusText + "</span></center></html>";
        String htmlHover = "<html><center><b style='font-size:16px; color:#D68910;'>" + ban.getTenBan() + 
                          "</b><br><span style='font-size:12px; color:" + statusColor + ";'>" + statusText + "</span></center></html>";

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
                // 1. Lưu phiên mới vào DB
                PhienChoi pc = new PhienChoi();
                pc.setMaPhien(phienDao.sinhMaMoi());
                pc.setMaBan(ban.getMaBan());
                pc.setThoiGianBatDau(LocalDateTime.now());
                pc.setTrangThaiPhien("DANG_CHOI");
                phienDao.themPhien(pc);

                // 2. Cập nhật trạng thái bàn
                banDao.capNhatTrangThai(ban.getMaBan(), "DANG_CHOI");
                refreshMap(); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi kết nối Database mở bàn: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void stopTable(BanBida ban) {
        try {
            // 1. Tìm phiên đang chơi dưới DB
            PhienChoi pc = phienDao.timPhienDangChoiTheoBan(ban.getMaBan());
            
            if (pc != null) {
                // 2. Mở form thanh toán
                ThanhToanDialog dialog = new ThanhToanDialog(null, ban, pc);
                dialog.setVisible(true);
                
                // 3. Nếu thanh toán xong thì mới tắt đèn bàn
                if (dialog.isPaid()) {
                    banDao.capNhatTrangThai(ban.getMaBan(), "TRONG");
                    refreshMap(); 
                }
            } else {
                // XỬ LÝ LỖI DỮ LIỆU RÁC (Bàn đang sáng đèn nhưng không có dữ liệu giờ chơi)
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
}