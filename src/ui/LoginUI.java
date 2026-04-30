package ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import dao.TaiKhoanDAO;
import session.SessionManager;

public class LoginUI extends JFrame {

    public LoginUI() {
        setTitle("Login - Billard Management System");
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

        JLabel lblTitle = new JLabel("Login to Billard Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(mainColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Manage products, inventory and orders quickly.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUser = new JLabel("Username");
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

        JLabel lblPass = new JLabel("Password");
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

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(mainColor);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(mainColor);
            }

            public void mousePressed(MouseEvent e) {
                btnLogin.setBackground(clickColor);
            }

            public void mouseReleased(MouseEvent e) {
                btnLogin.setBackground(hoverColor);
            }
        });

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText().trim();
                String password = new String(txtPass.getPassword());

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginUI.this, "Vui lòng nhập tên đăng nhập và mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                TaiKhoanDAO dao = new TaiKhoanDAO();
                model.TaiKhoan tk = dao.dangNhap(username, password);

                if (tk != null) {
                    SessionManager.DangNhap(tk);
                    JOptionPane.showMessageDialog(LoginUI.this, "Đăng nhập thành công! Chào mừng " + tk.getTenDangNhap(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dispose(); // Đóng login window
                } else {
                    JOptionPane.showMessageDialog(LoginUI.this, "Tên đăng nhập hoặc mật khẩu không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JLabel lblForgot = new JLabel("Forgot password?");
        lblForgot.setFont(new Font("Arial", Font.PLAIN, 13));
        lblForgot.setForeground(normalLinkColor);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblRegister = new JLabel("Don't have an account? Register here.");
        lblRegister.setFont(new Font("Arial", Font.PLAIN, 13));
        lblRegister.setForeground(normalLinkColor);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRegister.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblRegister.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lblRegister.setForeground(mainColor);
            }

            public void mouseExited(MouseEvent e) {
                lblRegister.setForeground(normalLinkColor);
            }

            public void mousePressed(MouseEvent e) {
                lblRegister.setForeground(clickColor);
            }

            public void mouseReleased(MouseEvent e) {
                lblRegister.setForeground(mainColor);
            }
            public void mouseClicked(MouseEvent e) {
               new RegisterUI().setVisible(true);
               setVisible(false);
            }
            
        });

        lblForgot.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lblForgot.setForeground(mainColor);
            }

            public void mouseExited(MouseEvent e) {
                lblForgot.setForeground(normalLinkColor);
            }

            public void mousePressed(MouseEvent e) {
                lblForgot.setForeground(clickColor);
            }

            public void mouseReleased(MouseEvent e) {
                lblForgot.setForeground(mainColor);
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
        rightPanel.add(btnLogin);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(lblForgot);
        rightPanel.add(Box.createVerticalStrut(15));
        rightPanel.add(lblRegister);
        container.add(leftPanel);
        container.add(rightPanel);

        bgPanel.add(container, BorderLayout.CENTER);
        add(bgPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new LoginUI().setVisible(true);
    }
}

class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            image = icon.getImage();
        } else {
            image = null;
            System.err.println("Không tải được ảnh: " + path);
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        int imageWidth = image.getWidth(this);
        int imageHeight = image.getHeight(this);

        if (imageWidth <= 0 || imageHeight <= 0) {
            return;
        }

        double scale = Math.max((double) panelWidth / imageWidth, (double) panelHeight / imageHeight);

        int newWidth = (int) (imageWidth * scale);
        int newHeight = (int) (imageHeight * scale);

        int x = (panelWidth - newWidth) / 2;
        int y = (panelHeight - newHeight) / 2;

        g.drawImage(image, x, y, newWidth, newHeight, this);
    }
}
