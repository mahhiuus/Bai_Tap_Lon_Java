package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

// Import các class Backend
import dao.TaiKhoanDAO;
import model.TaiKhoan;

public class RegisterUI extends JFrame {

    public RegisterUI() {
        setTitle("Đăng Ký Tài Khoản - Hệ Thống Quản Lý Billiard");
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

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));

        JLabel lblTitle = new JLabel("Tạo tài khoản mới");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(mainColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Đăng ký tài khoản mới cho hệ thống.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- ĐOẠN CODE CẤY THÊM HIỂN THỊ CHỨC VỤ ---
        JLabel lblRole = new JLabel("Chức vụ cấp phép: NHÂN VIÊN (Mặc định)");
        lblRole.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 14));
        lblRole.setForeground(new Color(192, 57, 43));
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font("Arial", Font.BOLD, 18));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtUser = new JTextField();
        txtUser.setFont(new Font("Arial", Font.PLAIN, 18));
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUser.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(2,6,2,6)
        ));
        txtUser.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font("Arial", Font.BOLD, 18));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setFont(new Font("Arial", Font.PLAIN, 18));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtPass.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(2,6,2,6)
        ));
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblConfirm = new JLabel("Confirm password");
        lblConfirm.setFont(new Font("Arial", Font.BOLD, 18));
        lblConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField txtConfirm = new JPasswordField();
        txtConfirm.setFont(new Font("Arial", Font.PLAIN, 18));
        txtConfirm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtConfirm.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(2,6,2,6)
        ));
        txtConfirm.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnRegister = new JButton("Register");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 18));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setBackground(mainColor);
        btnRegister.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnRegister.setFocusPainted(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnRegister.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { btnRegister.setBackground(mainColor); }
            public void mousePressed(MouseEvent e) { btnRegister.setBackground(clickColor); }
            public void mouseReleased(MouseEvent e) { btnRegister.setBackground(hoverColor); }
        });

      
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword());
                String confirmPass = new String(txtConfirm.getPassword());

                // 1. Kiểm tra trống
                if (username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 2. Kiểm tra mật khẩu khớp nhau
                if (!password.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Mật khẩu xác nhận không khớp!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TaiKhoanDAO dao = new TaiKhoanDAO();

                // 3. Kiểm tra trùng lặp tên đăng nhập
                if (dao.kiemTraTenDangNhapTonTai(username)) {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    // 4. Tạo đối tượng TaiKhoan mới
                    // Sinh mã tự động
                    String maTKMoi = dao.sinhMaMoi();
                    // maNV để null tạm. Vai trò mặc định là "NHANVIEN" như code cũ của bạn
                    TaiKhoan tkMoi = new TaiKhoan(maTKMoi, username, password, "NHANVIEN", null);

                    // 5. Thêm vào database
                    dao.themTaiKhoan(tkMoi);

                    JOptionPane.showMessageDialog(RegisterUI.this, "Đăng ký thành công! Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    new LoginUI().setVisible(true);
                    dispose(); // Đóng form đăng ký

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RegisterUI.this, "Đăng ký thất bại: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel lblBack = new JLabel("Back to login");
        lblBack.setFont(new Font("Arial", Font.PLAIN, 13));
        lblBack.setForeground(normalLinkColor);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { lblBack.setForeground(mainColor); }
            public void mouseExited(MouseEvent e) { lblBack.setForeground(normalLinkColor); }
            public void mousePressed(MouseEvent e) { lblBack.setForeground(clickColor); }
            public void mouseReleased(MouseEvent e) { lblBack.setForeground(mainColor); }
            public void mouseClicked(MouseEvent e) {
                new LoginUI().setVisible(true);
                dispose(); 
            }
        });

        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblSub);
        leftPanel.add(Box.createVerticalStrut(20)); // Thu hẹp một chút để nhét dòng role vào
        
        // --- ADD DÒNG ROLE VÀO GIAO DIỆN ---
        leftPanel.add(lblRole); 
        leftPanel.add(Box.createVerticalStrut(10));
        // -----------------------------------

        leftPanel.add(lblUser);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(txtUser);
        leftPanel.add(Box.createVerticalStrut(15));
        
        leftPanel.add(lblPass);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(txtPass);
        leftPanel.add(Box.createVerticalStrut(15));
        
        leftPanel.add(lblConfirm);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(txtConfirm);
        leftPanel.add(Box.createVerticalStrut(25));
        
        leftPanel.add(btnRegister);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(lblBack);
        leftPanel.add(Box.createVerticalGlue());

        ImagePanel rightPanel = new ImagePanel("src/image/Login.jpg");
        rightPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        container.add(leftPanel);
        container.add(rightPanel);

        bgPanel.add(container, BorderLayout.CENTER);
        add(bgPanel);

        setVisible(true);
    }
}