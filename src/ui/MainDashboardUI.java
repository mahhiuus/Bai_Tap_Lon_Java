package ui;

import model.TaiKhoan;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainDashboardUI extends JFrame {

    private JPanel centerContentPanel;
    private List<JButton> menuButtons; 
    private TaiKhoan currentUser; // Biến lưu tài khoản đang đăng nhập

    // --- CẬP NHẬT: Nhận Tài khoản từ form Login truyền sang ---
    public MainDashboardUI(TaiKhoan tk) {
        this.currentUser = tk;
        
        setTitle("Billiard Management System - Xin chào: " + currentUser.getTenDangNhap() + " (" + currentUser.getVaiTro() + ")");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        menuButtons = new ArrayList<>();

        add(createNavBar(), BorderLayout.WEST);

        centerContentPanel = new JPanel(new BorderLayout());
        centerContentPanel.setBackground(LuxuryTheme.CREAM);
        
        centerContentPanel.add(new MenuBanHangPanel(), BorderLayout.CENTER);
        
        add(centerContentPanel, BorderLayout.CENTER);
    }

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(LuxuryTheme.NAVY);
        navBar.setPreferredSize(new Dimension(260, getHeight()));

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

        // --- CẬP NHẬT: CHIA ROLE ---
        String[] menuItems;
        if (currentUser.getVaiTro().equals("ADMIN")) {
            menuItems = new String[]{
                "Bán Hàng", "Sơ đồ Bàn", "Tổng quan", "Quản Lý Bàn Bida", "Sản phẩm", 
                "Khách hàng", "Nhà cung cấp", "Nhân viên", "Hóa đơn Bán", "Hóa đơn Nhập", "Quản lý tài khoản", "Đăng xuất"
            };
        } else {
            // Role NHANVIEN chỉ thấy bấy nhiêu đây
            menuItems = new String[]{
                "Bán Hàng", "Sơ đồ Bàn", "Tổng quan", "Hóa đơn Bán", "Hóa đơn Nhập", "Đăng xuất"
            };
        }
        
        for (String item : menuItems) {
            JButton btnMenu = new JButton(item);
            btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
            btnMenu.setForeground(LuxuryTheme.CREAM);
            btnMenu.setBackground(LuxuryTheme.NAVY);
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnMenu.setMaximumSize(new Dimension(260, 65));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMenu.setBorder(new EmptyBorder(15, 45, 15, 0));

            if (item.equals("Bán Hàng")) {
                setActiveTab(btnMenu);
            }

            menuButtons.add(btnMenu);

            btnMenu.addActionListener(e -> {
                if (item.equals("Đăng xuất")) {
                    if (JOptionPane.showConfirmDialog(this, "Đăng xuất khỏi hệ thống?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        new LoginUI().setVisible(true);
                        dispose(); 
                    }
                    return;
                }

                resetAllTabs();
                setActiveTab(btnMenu);

                centerContentPanel.removeAll();
                switch (item) {
                    case "Bán Hàng": centerContentPanel.add(new MenuBanHangPanel(), BorderLayout.CENTER); break;
                    case "Tổng quan": centerContentPanel.add(new ThongKeUI(), BorderLayout.CENTER); break; 
                    case "Sơ đồ Bàn": centerContentPanel.add(new SoDoBanPanel(), BorderLayout.CENTER); break;
                    
                    // --- CẬP NHẬT: Truyền Tài khoản vào để check quyền Xóa bên trong ---
                    case "Hóa đơn Bán": centerContentPanel.add(new HoaDonBanPanel(currentUser), BorderLayout.CENTER); break;
                    case "Hóa đơn Nhập": centerContentPanel.add(new HoaDonNhapPanel(currentUser), BorderLayout.CENTER); break;
                    
                    // Các form của ADMIN
                    case "Khách hàng": centerContentPanel.add(new KhachHangPanel(), BorderLayout.CENTER); break;
                    case "Nhà cung cấp": centerContentPanel.add(new NhaCungCapPanel(), BorderLayout.CENTER); break;
                    case "Quản Lý Bàn Bida": centerContentPanel.add(new BanBidaPanel(), BorderLayout.CENTER); break;
                    case "Sản phẩm": centerContentPanel.add(new SanPhamPanel(), BorderLayout.CENTER); break;
                    case "Nhân viên": centerContentPanel.add(new NhanVienPanel(), BorderLayout.CENTER); break;
                    case "Quản Lý Tài khoản": centerContentPanel.add(new TaiKhoanPanel(), BorderLayout.CENTER); break;
                    
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
  
        // Còn nếu muốn chạy thật với LoginUI thì dùng dòng này:
        // new LoginUI().setVisible(true);
}