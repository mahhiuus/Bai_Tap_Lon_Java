package ui;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class RegisterUI extends JFrame {

    public RegisterUI() {
        setTitle("Register - Billard Management System");
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

        JLabel lblTitle = new JLabel("Create your account");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitle.setForeground(mainColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Register a new account for the system.");
        lblSub.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        

        JLabel lblName = new JLabel("Full name");
        lblName.setFont(new Font("Arial", Font.BOLD, 18));
        lblName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField txtName = new JTextField();
        txtName.setFont(new Font("Arial", Font.PLAIN, 18));
        txtName.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtName.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(2,6,2,6)
        ));
        txtName.setAlignmentX(Component.LEFT_ALIGNMENT);

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
            public void mouseEntered(MouseEvent e) {
                btnRegister.setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent e) {
                btnRegister.setBackground(mainColor);
            }

            public void mousePressed(MouseEvent e) {
                btnRegister.setBackground(clickColor);
            }

            public void mouseReleased(MouseEvent e) {
                btnRegister.setBackground(hoverColor);
            }
        });

        JLabel lblBack = new JLabel("Back to login");
        lblBack.setFont(new Font("Arial", Font.PLAIN, 13));
        lblBack.setForeground(normalLinkColor);
        lblBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblBack.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblBack.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                lblBack.setForeground(mainColor);
            }

            public void mouseExited(MouseEvent e) {
                lblBack.setForeground(normalLinkColor);
            }

            public void mousePressed(MouseEvent e) {
                lblBack.setForeground(clickColor);
            }

            public void mouseReleased(MouseEvent e) {
                lblBack.setForeground(mainColor);
            }

            public void mouseClicked(MouseEvent e) {
               new LoginUI();
            setVisible(false);
            }
        });

        leftPanel.add(lblTitle);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(lblSub);
        leftPanel.add(Box.createVerticalStrut(25));
        // leftPanel.add(lblName);
        // leftPanel.add(Box.createVerticalStrut(8));
        // leftPanel.add(txtName);
        // leftPanel.add(Box.createVerticalStrut(15));
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

    // public static void main(String[] args) {
    //     new RegisterUI();
    // }
}


