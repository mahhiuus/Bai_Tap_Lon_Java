package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MainFrame extends JFrame {

    private JPanel contentPanel;

    public MainFrame() {

        // ================= FRAME =================
        setTitle("Billiards Club Management");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ================= LEFT MENU =================
        JPanel menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(250, 900));
        menuPanel.setBackground(new Color(61, 148, 178));
        menuPanel.setLayout(new BorderLayout());

        // ================= LOGO =================
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(new Color(61, 148, 178));
        logoPanel.setPreferredSize(new Dimension(250, 220));

        JPanel logoTextPanel = new JPanel();
        logoTextPanel.setBackground(new Color(61, 148, 178));
        logoTextPanel.setLayout(new GridLayout(2,1));

        JLabel lblLogo = new JLabel("BILLIARDS");
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setFont(new Font("Comfortaa", Font.BOLD, 34));
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblSubLogo = new JLabel("CLUB CENTER");
        lblSubLogo.setForeground(Color.WHITE);
        lblSubLogo.setFont(new Font("Lora", Font.BOLD, 18));
        lblSubLogo.setHorizontalAlignment(SwingConstants.CENTER);

        logoTextPanel.add(lblLogo);
        logoTextPanel.add(lblSubLogo);

        logoPanel.add(logoTextPanel);

        // ================= MENU BUTTON PANEL =================
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(61, 148, 178));
        buttonPanel.setLayout(new GridLayout(10,1,0,10));
        buttonPanel.setBorder(new EmptyBorder(10,0,0,0));

        // ================= BUTTONS =================
        JButton btnTrangChu = createMenuButton("Trang chủ");

        JButton btnBanBida =
                createMenuButton("Quản lý phòng/bàn");

        JButton btnSanPham =
                createMenuButton("Quản lý sản phẩm");

        JButton btnKhachHang =
                createMenuButton("Quản lý khách hàng");

        JButton btnNhaCungCap =
                createMenuButton("Quản lý nhà cung cấp");

        JButton btnNhanVien =
                createMenuButton("Quản lý nhân viên");

        JButton btnBaoCao =
                createMenuButton("Báo cáo");

        JButton btnTaiKhoan =
                createMenuButton("Tài khoản");

        // ================= ADD BUTTON =================
        buttonPanel.add(btnTrangChu);
        buttonPanel.add(btnBanBida);
        buttonPanel.add(btnSanPham);
        buttonPanel.add(btnKhachHang);
        buttonPanel.add(btnNhaCungCap);
        buttonPanel.add(btnNhanVien);
        buttonPanel.add(btnBaoCao);
        buttonPanel.add(btnTaiKhoan);

        menuPanel.add(logoPanel, BorderLayout.NORTH);
        menuPanel.add(buttonPanel, BorderLayout.CENTER);

        // ================= RIGHT PANEL =================
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // ================= HEADER =================
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(99,129,202));
        headerPanel.setPreferredSize(new Dimension(1300,110));

        JLabel lblTitle = new JLabel("Trang chủ");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Lora", Font.BOLD, 28));
        lblTitle.setBorder(new EmptyBorder(0,20,0,0));

        // ================= USER PANEL =================
        JPanel userPanel = new JPanel();
        userPanel.setBackground(new Color(99,129,202));

        JLabel lblHello = new JLabel("Xin chào admin");
        lblHello.setForeground(Color.WHITE);
        lblHello.setFont(new Font("Lora", Font.BOLD, 18));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(new Color(245,132,23));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setFont(new Font("Lora", Font.BOLD, 16));

        userPanel.add(lblHello);
        userPanel.add(btnLogout);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // ================= CONTENT PANEL =================
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(235,235,235));

        // Hiển thị trang chủ mặc định
        showHomePanel();

        rightPanel.add(headerPanel, BorderLayout.NORTH);
        rightPanel.add(contentPanel, BorderLayout.CENTER);

        // ================= ADD MAIN =================
        add(menuPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // =====================================================
        // ====================== EVENTS =======================
        // =====================================================

        // ================= TRANG CHỦ =================
        btnTrangChu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lblTitle.setText("Trang chủ");

                contentPanel.removeAll();

                showHomePanel();

                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        // ================= KHÁCH HÀNG =================
        btnKhachHang.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lblTitle.setText("Quản lý khách hàng");

                contentPanel.removeAll();

                contentPanel.add(new KhachHangPanel());

                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        // ================= NHÀ CUNG CẤP =================
        btnNhaCungCap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lblTitle.setText("Quản lý nhà cung cấp");

                contentPanel.removeAll();

                contentPanel.add(new NhaCungCapPanel());

                contentPanel.revalidate();
                contentPanel.repaint();
            }
        });

        // ================= SẢN PHẨM =================
        btnSanPham.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,
                        "Chưa code SanPhamPanel");
            }
        });

        // ================= NHÂN VIÊN =================
        btnNhanVien.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,
                        "Chưa code NhanVienPanel");
            }
        });

        // ================= PHÒNG/BÀN =================
        btnBanBida.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,
                        "Chưa code BanBidaPanel");
            }
        });

        // ================= BÁO CÁO =================
        btnBaoCao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,
                        "Chưa code ThongKePanel");
            }
        });

        // ================= TÀI KHOẢN =================
        btnTaiKhoan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JOptionPane.showMessageDialog(null,
                        "Chưa code TaiKhoanPanel");
            }
        });

        // ================= ĐĂNG XUẤT =================
        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int choose = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có muốn đăng xuất không?",
                        "Đăng xuất",
                        JOptionPane.YES_NO_OPTION
                );

                if(choose == JOptionPane.YES_OPTION) {

                    new LoginUI().setVisible(true);

                    dispose();
                }
            }
        });
    }

    // =====================================================
    // ================= HOME PANEL ========================
    // =====================================================

    private void showHomePanel() {

        JPanel homePanel = new JPanel();
        homePanel.setBackground(new Color(235,235,235));
        homePanel.setLayout(new BorderLayout());

        // ================= TOP TEXT =================
        JPanel topTextPanel = new JPanel(new GridLayout(2,1));
        topTextPanel.setBackground(new Color(235,235,235));
        topTextPanel.setBorder(new EmptyBorder(20,0,20,0));

        JLabel lblWelcome = new JLabel(
                "Chào mừng đến với hệ thống quản lý câu lạc bộ Billiards Club",
                SwingConstants.CENTER);

        lblWelcome.setFont(new Font("Lora", Font.BOLD, 28));

        JLabel lblGuide = new JLabel(
                "Chọn các chức năng ở Menu bên trái để bắt đầu",
                SwingConstants.CENTER);

        lblGuide.setFont(new Font("Lora", Font.BOLD, 24));

        topTextPanel.add(lblWelcome);
        topTextPanel.add(lblGuide);

        // ================= CARD PANEL =================
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(2,2,50,50));
        cardPanel.setBackground(new Color(235,235,235));
        cardPanel.setBorder(new EmptyBorder(30,100,50,100));

        JPanel card1 =
                createCard(new Color(92,169,235),
                        "Tổng số bàn");

        JPanel card2 =
                createCard(new Color(140,210,80),
                        "Khách hàng");

        JPanel card3 =
                createCard(new Color(198,154,224),
                        "Nhân viên");

        JPanel card4 =
                createCard(new Color(237,213,79),
                        "Tổng doanh thu");

        cardPanel.add(card1);
        cardPanel.add(card2);
        cardPanel.add(card3);
        cardPanel.add(card4);

        homePanel.add(topTextPanel, BorderLayout.NORTH);
        homePanel.add(cardPanel, BorderLayout.CENTER);

        contentPanel.add(homePanel, BorderLayout.CENTER);
    }

    // =====================================================
    // ================= CREATE CARD =======================
    // =====================================================

    private JPanel createCard(Color color, String text) {

        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setLayout(new GridBagLayout());

        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Comfortaa", Font.BOLD, 28));

        panel.add(label);

        return panel;
    }

    // =====================================================
    // ================= MENU BUTTON =======================
    // =====================================================

    private JButton createMenuButton(String text) {

        JButton btn = new JButton(text);

        btn.setBackground(new Color(61,148,178));
        btn.setForeground(Color.WHITE);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);

        btn.setFont(new Font("Comfortaa", Font.BOLD, 20));

        return btn;
    }

    // =====================================================
    // ================= MAIN ==============================
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                new MainFrame().setVisible(true);
            }
        });
    }
}