package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// Import DAO để thao tác Database
import dao.TaiKhoanDAO;

public class ForgotPasswordUI extends JFrame {

    public ForgotPasswordUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Quên mật khẩu - Hệ Thống Quản Lý Billiard");
        setSize(1000, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color mainColor = new Color(17, 126, 141);
        Color hoverColor = new Color(14, 100, 112);
        Color clickColor = new Color(10, 80, 90);
        Color normalLinkColor = new Color(120, 120, 120);

        JPanel bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(new Color(235, 232, 245));
        bgPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setBackground(Color.WHITE);
        container.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        ImagePanel leftPanel = new ImagePanel("src/image/Login.jpg");
        leftPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 25, 40, 25));

        JLabel lblTitle = new JLabel("Forgot Password?");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(mainColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Enter your username to reset your password.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username / Email");
        lblUser.setFont(new Font("Arial", Font.BOLD, 18));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 18));
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        txtUser.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("New Password");
        lblPass.setFont(new Font("Arial", Font.BOLD, 18));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 18));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        txtPass.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(12, 16, 12, 16)
        ));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnReset = new JButton("Reset Password");
        btnReset.setFont(new Font("Arial", Font.BOLD, 18));
        btnReset.setForeground(Color.WHITE);
        btnReset.setBackground(mainColor);
        btnReset.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnReset.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnReset.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { btnReset.setBackground(mainColor); }
            public void mousePressed(MouseEvent e) { btnReset.setBackground(clickColor); }
            public void mouseReleased(MouseEvent e) { btnReset.setBackground(hoverColor); }
        });

        // --- KẾT NỐI BACKEND Ở ĐÂY ---
        btnReset.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String newPassword = new String(txtPass.getPassword());

            if (username.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Username và Mật khẩu mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!UsernameValidator.isValid(username)) {
                JOptionPane.showMessageDialog(this, UsernameValidator.message(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                JOptionPane.showMessageDialog(this, ValidationUtils.passwordMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            TaiKhoanDAO dao = new TaiKhoanDAO();
            
            // Gọi hàm datLaiMatKhau vừa thêm
            boolean isSuccess;
            try {
                isSuccess = dao.datLaiMatKhau(username, newPassword);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isSuccess) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                new LoginUI().setVisible(true);
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Tên đăng nhập không tồn tại hoặc có lỗi xảy ra!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JLabel lblBackToLogin = new JLabel("Back to Login");
        lblBackToLogin.setFont(new Font("Arial", Font.PLAIN, 13));
        lblBackToLogin.setForeground(normalLinkColor);
        lblBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBackToLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBackToLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblBackToLogin.setForeground(mainColor); }
            public void mouseExited(MouseEvent e) { lblBackToLogin.setForeground(normalLinkColor); }
            public void mousePressed(MouseEvent e) { lblBackToLogin.setForeground(clickColor); }
            public void mouseReleased(MouseEvent e) { lblBackToLogin.setForeground(mainColor); }
            public void mouseClicked(MouseEvent e) {
                new LoginUI().setVisible(true);
                dispose(); 
            }
        });

        rightPanel.add(lblTitle);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(lblSub);
        rightPanel.add(Box.createVerticalStrut(35));
        
        rightPanel.add(lblUser);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtUser);
        rightPanel.add(Box.createVerticalStrut(20));
        
        rightPanel.add(lblPass);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(txtPass);
        rightPanel.add(Box.createVerticalStrut(30));
        
        rightPanel.add(btnReset);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(lblBackToLogin);

        container.add(leftPanel);
        container.add(rightPanel);

        bgPanel.add(container, BorderLayout.CENTER);
        add(bgPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ForgotPasswordUI().setVisible(true);
        });
    }
}
