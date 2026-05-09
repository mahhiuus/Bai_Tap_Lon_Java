package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class LuxuryTheme {
    // 1. BẢNG MÀU CHỦ ĐẠO
    public static final Color NAVY = new Color(26, 50, 99);     // #1A3263
    public static final Color CREAM = new Color(232, 226, 219);   // #E8E2DB
    public static final Color GOLD = new Color(250, 185, 91);     // #FAB95B
    public static final Color TEAL = new Color(17, 126, 141);     // #117E8D
    
    // 2. COMPONENT: Ô Input có viền Bottom
    public static JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Arial", Font.PLAIN, 15));
        txt.setBackground(CREAM);
        txt.setForeground(NAVY);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, NAVY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        txt.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, GOLD),
                    BorderFactory.createEmptyBorder(5, 5, 4, 5)
                ));
            }
            public void focusLost(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, NAVY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
            }
        });
        return txt;
    }

    // 3. COMPONENT: Nút bấm có chiều sâu
    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false); // Dùng để hiển thị màu nền phẳng trên Windows
        
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 40), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }
}