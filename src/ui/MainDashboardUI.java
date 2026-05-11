package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardUI extends JFrame {

    private JPanel centerContentPanel;
    private List<JButton> menuButtons; 

    public MainDashboardUI() {
        setTitle("Billiard Management System - Luxury Edition");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menuButtons = new ArrayList<>();

        // 1. THANH NAV BAR BÊN TRÁI
        add(createNavBar(), BorderLayout.WEST);

        // 2. KHU VỰC CHÍNH
        centerContentPanel = new JPanel(new BorderLayout());
        centerContentPanel.setBackground(LuxuryTheme.CREAM);
        
        // Mặc định mở Tổng quan
        centerContentPanel.add(new ThongKeUI(), BorderLayout.CENTER);
        
        add(centerContentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(LuxuryTheme.NAVY);
        navBar.setPreferredSize(new Dimension(260, getHeight()));

        // LOGO
        JLabel lblLogo = new JLabel("BILLIARDS", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 28));
        lblLogo.setForeground(LuxuryTheme.GOLD); 
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(50, 0, 10, 0));
        
        JLabel lblSubLogo = new JLabel("CLUB CENTER", SwingConstants.CENTER);
        lblSubLogo.setFont(new Font("Arial", Font.BOLD, 14));
        lblSubLogo.setForeground(LuxuryTheme.CREAM);
        lblSubLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubLogo.setBorder(new EmptyBorder(0, 0, 50, 0));

        navBar.add(lblLogo);
        navBar.add(lblSubLogo);

        String[] menuItems = {
            "Tổng quan", "Sơ đồ Bàn", "Quản Lý Bàn Bida", "Sản phẩm", "Khách hàng", "Nhà cung cấp", 
            "Nhân viên", "Hóa đơn", "Tài khoản", "Đăng xuất"
        };
        
        for (String item : menuItems) {
            JButton btnMenu = new JButton(item);
            btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
            btnMenu.setForeground(LuxuryTheme.CREAM);
            btnMenu.setBackground(LuxuryTheme.NAVY);
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // --- TĂNG CHIỀU CAO MENU TỪ 50 LÊN 65 ---
            btnMenu.setMaximumSize(new Dimension(260, 65));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // --- TĂNG PADDING CHO NÚT (TRÊN 15, DƯỚI 15) ---
            btnMenu.setBorder(new EmptyBorder(15, 45, 15, 0));

            if (item.equals("Tổng quan")) {
                setActiveTab(btnMenu);
            }

            menuButtons.add(btnMenu);

            btnMenu.addActionListener(e -> {
                if (item.equals("Đăng xuất")) {
                    if (JOptionPane.showConfirmDialog(this, "Đăng xuất?", "Xác nhận", 0) == 0) {
                        new LoginUI().setVisible(true);
                        dispose();
                    }
                    return;
                }

                resetAllTabs();
                setActiveTab(btnMenu);

                centerContentPanel.removeAll();
                switch (item) {
                    case "Tổng quan": centerContentPanel.add(new ThongKeUI(), BorderLayout.CENTER); break;
                    case "Khách hàng": centerContentPanel.add(new KhachHangPanel(), BorderLayout.CENTER); break;
                    case "Nhà cung cấp": centerContentPanel.add(new NhaCungCapPanel(), BorderLayout.CENTER); break;
                    case "Quản Lý Bàn Bida": centerContentPanel.add(new BanBidaPanel(), BorderLayout.CENTER); break;
                    case "Sơ đồ Bàn": centerContentPanel.add(new SoDoBanPanel(), BorderLayout.CENTER); break;
                    case "Hóa đơn": centerContentPanel.add(new HoaDonPanel(), BorderLayout.CENTER); break;
                    // Các case khác bạn nhét Panel vào đây
                    default: centerContentPanel.add(new JPanel(), BorderLayout.CENTER); break;
                }
                centerContentPanel.revalidate();
                centerContentPanel.repaint();
            });

            navBar.add(btnMenu);
            navBar.add(Box.createVerticalStrut(5));
        }
        
        navBar.add(Box.createVerticalGlue());
        return navBar;
    }

    private void setActiveTab(JButton btn) {
        btn.setForeground(LuxuryTheme.GOLD);
        btn.setBackground(new Color(20, 40, 80));
        // --- GIỮ PADDING KHI ACTIVE ---
        btn.setBorder(new CompoundBorder(
                new MatteBorder(0, 6, 0, 0, LuxuryTheme.GOLD), 
                new EmptyBorder(15, 39, 15, 0)
        ));
    }

    private void resetAllTabs() {
        for (JButton btn : menuButtons) {
            btn.setForeground(LuxuryTheme.CREAM);
            btn.setBackground(LuxuryTheme.NAVY);
            btn.setBorder(new EmptyBorder(15, 45, 15, 0));
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> new MainDashboardUI().setVisible(true));
    }
}