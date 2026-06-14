package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Locale;

public class FontAwesomeIcon implements Icon {
    private final String name;
    private final Color color;
    private final int size;

    public FontAwesomeIcon(String name, Color color, int size) {
        this.name = name == null ? "circle" : name;
        this.color = color == null ? Color.BLACK : color;
        this.size = Math.max(12, size);
    }

    public static Icon of(String name, Color color, int size) {
        return new FontAwesomeIcon(name, color, size);
    }

    public static Icon forButton(String text, Color color, int size) {
        return of(iconNameForButton(text), color, size);
    }

    public static Icon forMenu(String text, Color color, int size) {
        String value = normalize(text);
        if (value.contains("tong quan")) return of("dashboard", color, size);
        if (value.contains("so do")) return of("table", color, size);
        if (value.contains("ban hang")) return of("cart", color, size);
        if (value.contains("ban bida")) return of("billiard", color, size);
        if (value.contains("san pham")) return of("box", color, size);
        if (value.contains("khach hang")) return of("customers", color, size);
        if (value.contains("nha cung cap")) return of("supplier", color, size);
        if (value.contains("nhan vien")) return of("users", color, size);
        if (value.contains("tai khoan")) return of("account", color, size);
        if (value.contains("hoa don ban")) return of("receipt", color, size);
        if (value.contains("hoa don nhap")) return of("file-import", color, size);
        if (value.contains("dang xuat")) return of("logout", color, size);
        return of("circle", color, size);
    }

    public static String iconNameForButton(String text) {
        String value = normalize(text);
        if (value.contains("them") || value.contains("tao") || value.contains("+")) return "plus";
        if (value.contains("sua") || value.contains("cap nhat")) return "edit";
        if (value.contains("xoa")) return "trash";
        if (value.contains("moi") || value.contains("lam moi")) return "refresh";
        if (value.contains("tim") || value.contains("loc")) return "search";
        if (value.contains("pdf") || value.contains("in ")) return "print";
        if (value.contains("anh") || value.contains("file")) return "image";
        if (value.contains("nhap chi tiet")) return "file-import";
        if (value.contains("thanh toan") || value.contains("xac nhan")) return "check";
        if (value.contains("truoc")) return "arrow-left";
        if (value.contains("sau")) return "arrow-right";
        return "circle";
    }

    private static String normalize(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.replace('đ', 'd').replace('Đ', 'D').toLowerCase(Locale.ROOT);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.scale(size / 24.0, size / 24.0);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (name) {
            case "plus": drawPlus(g2); break;
            case "edit": drawEdit(g2); break;
            case "trash": drawTrash(g2); break;
            case "refresh": drawRefresh(g2); break;
            case "search": drawSearch(g2); break;
            case "print": drawPrint(g2); break;
            case "image": drawImage(g2); break;
            case "check": drawCheck(g2); break;
            case "arrow-left": drawArrow(g2, true); break;
            case "arrow-right": drawArrow(g2, false); break;
            case "dashboard": drawDashboard(g2); break;
            case "table": drawTable(g2); break;
            case "cart": drawCart(g2); break;
            case "billiard": drawBilliard(g2); break;
            case "box": drawBox(g2); break;
            case "customers": drawCustomers(g2); break;
            case "supplier": drawSupplier(g2); break;
            case "users": drawUsers(g2); break;
            case "account": drawAccount(g2); break;
            case "receipt": drawReceipt(g2); break;
            case "file-import": drawFileImport(g2); break;
            case "logout": drawLogout(g2); break;
            case "login": drawLogin(g2); break;
            case "money": drawMoney(g2); break;
            case "chart-line": drawChartLine(g2); break;
            case "file-invoice": drawFileInvoice(g2); break;
            case "eye": drawEye(g2, false); break;
            case "eye-slash": drawEye(g2, true); break;
            default: drawCircle(g2); break;
        }

        g2.dispose();
    }

    private void drawPlus(Graphics2D g) { g.drawLine(12, 5, 12, 19); g.drawLine(5, 12, 19, 12); }
    private void drawEdit(Graphics2D g) { g.draw(new Rectangle2D.Double(4, 16, 4, 4)); g.drawLine(8, 18, 18, 8); g.drawLine(16, 6, 20, 10); g.drawLine(15, 7, 17, 5); }
    private void drawTrash(Graphics2D g) { g.drawLine(5, 7, 19, 7); g.draw(new Rectangle2D.Double(7, 8, 10, 12)); g.drawLine(9, 7, 10, 4); g.drawLine(14, 4, 15, 7); g.drawLine(10, 11, 10, 17); g.drawLine(14, 11, 14, 17); }
    private void drawRefresh(Graphics2D g) { g.draw(new Arc2D.Double(5, 5, 14, 14, 30, 280, Arc2D.OPEN)); g.drawLine(18, 4, 18, 9); g.drawLine(18, 4, 13, 4); }
    private void drawSearch(Graphics2D g) { g.draw(new Ellipse2D.Double(4, 4, 11, 11)); g.drawLine(14, 14, 20, 20); }
    private void drawPrint(Graphics2D g) { g.draw(new Rectangle2D.Double(7, 3, 10, 6)); g.draw(new Rectangle2D.Double(5, 9, 14, 8)); g.draw(new Rectangle2D.Double(7, 15, 10, 6)); }
    private void drawImage(Graphics2D g) { g.draw(new Rectangle2D.Double(4, 5, 16, 14)); g.draw(new Ellipse2D.Double(7, 8, 3, 3)); g.drawLine(5, 18, 11, 12); g.drawLine(11, 12, 15, 16); g.drawLine(15, 16, 18, 13); }
    private void drawCheck(Graphics2D g) { g.drawLine(5, 12, 10, 17); g.drawLine(10, 17, 20, 6); }
    private void drawArrow(Graphics2D g, boolean left) { if (left) { g.drawLine(19, 12, 6, 12); g.drawLine(6, 12, 12, 6); g.drawLine(6, 12, 12, 18); } else { g.drawLine(5, 12, 18, 12); g.drawLine(18, 12, 12, 6); g.drawLine(18, 12, 12, 18); } }
    private void drawDashboard(Graphics2D g) { g.draw(new Arc2D.Double(4, 5, 16, 16, 180, -180, Arc2D.OPEN)); g.drawLine(12, 13, 17, 9); g.drawLine(6, 20, 18, 20); }
    private void drawTable(Graphics2D g) { g.draw(new Rectangle2D.Double(4, 5, 16, 14)); g.drawLine(4, 10, 20, 10); g.drawLine(9, 5, 9, 19); g.drawLine(15, 5, 15, 19); }
    private void drawCart(Graphics2D g) { g.drawPolyline(new int[]{3, 6, 8, 18, 20}, new int[]{5, 5, 15, 15, 8}, 5); g.draw(new Ellipse2D.Double(8, 17, 3, 3)); g.draw(new Ellipse2D.Double(16, 17, 3, 3)); }
    private void drawBilliard(Graphics2D g) { g.fill(new Ellipse2D.Double(5, 5, 14, 14)); g.setColor(Color.WHITE); g.fill(new Ellipse2D.Double(9, 7, 6, 6)); g.setColor(color); g.drawString("8", 10, 13); }
    private void drawBox(Graphics2D g) { g.draw(new Rectangle2D.Double(5, 8, 14, 11)); g.drawLine(5, 8, 12, 4); g.drawLine(12, 4, 19, 8); g.drawLine(12, 4, 12, 15); }
    private void drawCustomers(Graphics2D g) { drawUserAt(g, 7, 5); drawUserAt(g, 15, 7); g.draw(new Arc2D.Double(3, 13, 11, 8, 0, 180, Arc2D.OPEN)); g.draw(new Arc2D.Double(11, 14, 10, 7, 0, 180, Arc2D.OPEN)); }
    private void drawSupplier(Graphics2D g) { g.draw(new Rectangle2D.Double(3, 9, 18, 8)); g.draw(new Rectangle2D.Double(5, 5, 9, 4)); g.draw(new Ellipse2D.Double(6, 16, 3, 3)); g.draw(new Ellipse2D.Double(16, 16, 3, 3)); }
    private void drawUsers(Graphics2D g) { drawCustomers(g); }
    private void drawAccount(Graphics2D g) { g.draw(new Ellipse2D.Double(8, 4, 8, 8)); g.draw(new Arc2D.Double(5, 13, 14, 8, 0, 180, Arc2D.OPEN)); }
    private void drawReceipt(Graphics2D g) { g.draw(new Rectangle2D.Double(6, 4, 12, 16)); g.drawLine(9, 8, 15, 8); g.drawLine(9, 12, 15, 12); g.drawLine(9, 16, 13, 16); }
    private void drawFileImport(Graphics2D g) { g.draw(new Rectangle2D.Double(7, 3, 10, 18)); g.drawLine(10, 12, 19, 12); g.drawLine(15, 8, 19, 12); g.drawLine(15, 16, 19, 12); }
    private void drawLogout(Graphics2D g) { g.draw(new Rectangle2D.Double(4, 5, 9, 14)); g.drawLine(12, 12, 21, 12); g.drawLine(17, 8, 21, 12); g.drawLine(17, 16, 21, 12); }
    private void drawLogin(Graphics2D g) { g.draw(new Rectangle2D.Double(11, 5, 9, 14)); g.drawLine(3, 12, 14, 12); g.drawLine(10, 8, 14, 12); g.drawLine(10, 16, 14, 12); }
    private void drawMoney(Graphics2D g) { g.draw(new RoundRectangle2D.Double(3, 6, 18, 12, 2, 2)); g.draw(new Ellipse2D.Double(9, 8, 6, 8)); g.drawLine(12, 5, 12, 19); }
    private void drawChartLine(Graphics2D g) { g.drawLine(4, 19, 20, 19); g.drawLine(4, 19, 4, 5); g.drawPolyline(new int[]{6, 10, 13, 18}, new int[]{16, 12, 14, 8}, 4); }
    private void drawFileInvoice(Graphics2D g) { drawReceipt(g); g.drawLine(16, 4, 19, 7); }
    private void drawCircle(Graphics2D g) { g.draw(new Ellipse2D.Double(6, 6, 12, 12)); }

    private void drawEye(Graphics2D g, boolean slash) {
        g.draw(new Arc2D.Double(3, 7, 18, 10, 0, 180, Arc2D.OPEN));
        g.draw(new Arc2D.Double(3, 7, 18, 10, 0, -180, Arc2D.OPEN));
        g.draw(new Ellipse2D.Double(9, 9, 6, 6));
        if (slash) g.drawLine(4, 20, 20, 4);
    }

    private void drawUserAt(Graphics2D g, int x, int y) {
        g.draw(new Ellipse2D.Double(x - 3, y, 6, 6));
    }
}
