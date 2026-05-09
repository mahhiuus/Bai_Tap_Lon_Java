package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class MainDashboardUI extends JFrame {

    private JPanel centerContentPanel;

    public MainDashboardUI() {
        setTitle("Luxury Billiard Management");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. THANH NAV BAR BÊN TRÁI (Màu NAVY)
        add(createNavBar(), BorderLayout.WEST);

        // 2. KHU VỰC CHÍNH BÊN PHẢI (Màu CREAM)
        centerContentPanel = new JPanel(new BorderLayout());
        centerContentPanel.setBackground(LuxuryTheme.CREAM);
        
        // ---- TÍCH HỢP THỐNG KÊ VÀO LÀM MẶC ĐỊNH LÚC ĐẦU ----
        centerContentPanel.add(new ThongKeUI(), BorderLayout.CENTER);
        
        add(centerContentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(LuxuryTheme.NAVY);
        navBar.setPreferredSize(new Dimension(240, getHeight()));

        JLabel lblLogo = new JLabel("BILLIARDS", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Arial", Font.BOLD, 24));
        lblLogo.setForeground(LuxuryTheme.GOLD); // Nhấn nhá màu Vàng
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(40, 0, 50, 0));
        navBar.add(lblLogo);

        // Khai báo menu
        String[] menuItems = {"Tổng quan", "Khách hàng", "Nhà cung cấp", "Hóa đơn", "Đăng xuất"};
        
        for (String item : menuItems) {
            JButton btnMenu = new JButton(item);
            btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
            btnMenu.setForeground(LuxuryTheme.CREAM);
            btnMenu.setBackground(LuxuryTheme.NAVY);
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnMenu.setMaximumSize(new Dimension(240, 50));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setBorder(new EmptyBorder(0, 40, 0, 0));
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Đánh dấu dòng 'Tổng quan' được chọn mặc định
            if (item.equals("Tổng quan")) {
                btnMenu.setForeground(LuxuryTheme.GOLD);
                btnMenu.setBorder(new CompoundBorder(
                        new MatteBorder(0, 5, 0, 0, LuxuryTheme.GOLD), 
                        new EmptyBorder(0, 35, 0, 0)
                ));
            }

            // Xử lý chuyển trang
            btnMenu.addActionListener(e -> {
                // Reset hiệu ứng cho các nút (nếu có logic nâng cao)
                // Đổi form
                centerContentPanel.removeAll();
                
                if (item.equals("Tổng quan")) {
                    centerContentPanel.add(new ThongKeUI(), BorderLayout.CENTER);
                } 
                else if (item.equals("Khách hàng")) {
                    centerContentPanel.add(new KhachHangPanel(), BorderLayout.CENTER);
                } 
                else if(item.equals("Nhà cung cấp")) {
                    centerContentPanel.add(new NhaCungCapPanel(), BorderLayout.CENTER);
                }  
                else if (item.equals("Đăng xuất")) {
                    new LoginUI().setVisible(true);
                    dispose();
                    return;
                }
                
                // Cập nhật giao diện
                centerContentPanel.revalidate();
                centerContentPanel.repaint();
            });

            navBar.add(btnMenu);
            navBar.add(Box.createVerticalStrut(10));
        }
        return navBar;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainDashboardUI().setVisible(true));
    }
}