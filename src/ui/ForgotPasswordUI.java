package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordUI extends JFrame {

    public ForgotPasswordUI() {
        initComponents();
    }

    private void initComponents() {
        // Cài đặt cơ bản cho JFrame
        setTitle("Forgot Password - Billiard Management System");
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị ở giữa màn hình
        setLayout(new BorderLayout());

        // --- PANEL CHÍNH CHIA ĐÔI MÀN HÌNH ---
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // 1. PANEL TRÁI: Chứa hình ảnh
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.BLACK);
        // TODO: Đổi lại đường dẫn hình ảnh cho khớp với project của bạn (thư mục src/image/...)
        // Lấy cùng hình ảnh với LoginUI để đồng bộ
        try {
            ImageIcon icon = new ImageIcon("src/image/Login.jpg"); 
            // Scale hình ảnh cho vừa vặn
            Image img = icon.getImage().getScaledInstance(425, 550, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            leftPanel.add(imageLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel placeholder = new JLabel("Image Placeholder", SwingConstants.CENTER);
            placeholder.setForeground(Color.WHITE);
            leftPanel.add(placeholder, BorderLayout.CENTER);
        }

        // 2. PANEL PHẢI: Chứa Form Quên Mật Khẩu
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        rightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); // Khoảng cách giữa các phần tử
        gbc.weightx = 1.0;

        // Màu chủ đạo (Lấy từ màu nút Login của bạn)
        Color primaryColor = new Color(22, 129, 133); 

        // Tiêu đề
        JLabel lblTitle = new JLabel("Forgot Password?");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(lblTitle, gbc);

        // Subtitle
        JLabel lblSubtitle = new JLabel("Enter your username to reset your password.");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 25, 0);
        rightPanel.add(lblSubtitle, gbc);

        // Username Label & Textfield
        JLabel lblUsername = new JLabel("Username / Email");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 5, 0);
        rightPanel.add(lblUsername, gbc);

        JTextField txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 35));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 3;
        rightPanel.add(txtUsername, gbc);

        // New Password Label & Textfield
        JLabel lblNewPassword = new JLabel("New Password");
        lblNewPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridy = 4;
        gbc.insets = new Insets(15, 0, 5, 0);
        rightPanel.add(lblNewPassword, gbc);

        JPasswordField txtNewPassword = new JPasswordField();
        txtNewPassword.setPreferredSize(new Dimension(300, 35));
        txtNewPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 5, 0);
        rightPanel.add(txtNewPassword, gbc);

        // Nút Reset Password
        JButton btnReset = new JButton("Reset Password");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setBackground(primaryColor);
        btnReset.setForeground(Color.WHITE);
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setPreferredSize(new Dimension(300, 40));
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gbc.gridy = 6;
        gbc.insets = new Insets(25, 0, 15, 0);
        rightPanel.add(btnReset, gbc);

        // Quay lại màn hình Login
        JLabel lblBackToLogin = new JLabel("Back to Login");
        lblBackToLogin.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBackToLogin.setForeground(Color.GRAY);
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Căn giữa cho nút Back to Login
        lblBackToLogin.setHorizontalAlignment(SwingConstants.LEFT); 
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 0, 0);
        rightPanel.add(lblBackToLogin, gbc);

        // Thêm sự kiện Click cho nút Back to Login
        lblBackToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Đóng màn hình hiện tại và mở lại LoginUI
                dispose();
                new LoginUI().setVisible(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblBackToLogin.setForeground(primaryColor); // Đổi màu khi hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblBackToLogin.setForeground(Color.GRAY);
            }
        });

        // Ghép 2 panel vào mainPanel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        // Thêm mainPanel vào Frame
        add(mainPanel, BorderLayout.CENTER);
    }

    // Hàm main để test thử giao diện chạy độc lập
    public static void main(String[] args) {
        // Thiết lập giao diện nhìn giống hệ điều hành hơn (Flat design)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ForgotPasswordUI().setVisible(true);
        });
    }
}