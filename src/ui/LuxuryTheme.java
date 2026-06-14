package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LuxuryTheme {
    public static final Color NAVY = new Color(26, 50, 99);
    public static final Color CREAM = new Color(248, 248, 248);
    public static final Color GOLD = new Color(250, 185, 91);
    public static final Color TEAL = new Color(17, 126, 141);

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

    public static JButton createButton(String text, Color bgColor, Color fgColor) {
        String displayText = getButtonDisplayText(text);
        JButton btn = new JButton(displayText);
        if (text != null && !displayText.equals(text)) {
            btn.setToolTipText(text);
        }
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setIcon(FontAwesomeIcon.forButton(text, fgColor, 16));
        btn.setIconTextGap(8);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setMargin(new Insets(8, 10, 8, 10));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 40), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgColor.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    private static String getButtonDisplayText(String text) {
        String value = normalize(text);
        if (value.equals("tao vo")) return "Tạo";
        if (value.equals("xoa vo")) return "Xóa";
        if (value.equals("nhap chi tiet")) return "Nhập";
        if (value.equals("in pdf") || value.equals("in lai hoa don pdf")) return "In PDF";
        if (value.equals("lam moi du lieu")) return "Làm mới";
        if (value.equals("tim theo ngay")) return "Tìm ngày";
        return text == null ? "" : text;
    }

    private static String normalize(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.replace('đ', 'd').replace('Đ', 'D').toLowerCase();
    }
}
