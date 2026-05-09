package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;

// Import thư viện XChart
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

// Import DAO (Khi nào bạn nối DB thật thì mở comment ra dùng)
// import dao.ThongKeDAO;

public class ThongKeUI extends JFrame {

    // Bảng màu chuẩn từ LoginUI của bạn
    private Color mainColor = new Color(17, 126, 141);
    private Color hoverColor = new Color(14, 100, 112);
    private Color bgColor = new Color(240, 242, 245); // Màu xám nhạt nền Dashboard
    private Color cardColor = Color.WHITE;

    public ThongKeUI() {
        setTitle("Dashboard Analytics - Billard Management System");
        setSize(1200, 750); // Màn hình thống kê cần rộng
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. LEFT PANEL: NAVIGATION BAR ---
        JPanel navBar = createNavBar();
        add(navBar, BorderLayout.WEST);

        // --- 2. CENTER PANEL: MAIN CONTENT ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout(20, 20));
        mainContent.setBackground(bgColor);
        mainContent.setBorder(new EmptyBorder(25, 30, 25, 30));

        // 2.1 HEADER: Tiêu đề Dashboard
        JLabel lblHeader = new JLabel("Tổng quan Analytics");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
        lblHeader.setForeground(new Color(40, 40, 40));
        mainContent.add(lblHeader, BorderLayout.NORTH);

        // 2.2 CENTER AREA: Chứa Cards và Chart
        JPanel centerArea = new JPanel();
        centerArea.setLayout(new BorderLayout(0, 25));
        centerArea.setBackground(bgColor);

        // -- TOP: 4 THẺ SỐ LIỆU (CARDS) --
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(bgColor);

        // TODO: Thay các con số này bằng thongKeDAO.tinhTongDoanhThu()...
        cardsPanel.add(createStatCard("Doanh thu tháng này", "25,410,000 VNĐ", "Tăng 8.2% so với tháng trước", new Color(46, 204, 113)));
        cardsPanel.add(createStatCard("Tổng số hóa đơn", "201", "36 hóa đơn chờ xử lý", new Color(52, 152, 219)));
        cardsPanel.add(createStatCard("Khách hàng mới", "48", "Tổng: 4,890 KH", new Color(155, 89, 182)));
        cardsPanel.add(createStatCard("Bàn đang hoạt động", "12 / 20", "Công suất 60%", new Color(230, 126, 34)));
        
        centerArea.add(cardsPanel, BorderLayout.NORTH);

        // -- BOTTOM: BIỂU ĐỒ DOANH THU (XCHART) --
        JPanel chartContainer = createChartPanel();
        centerArea.add(chartContainer, BorderLayout.CENTER);

        mainContent.add(centerArea, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * Hàm tạo thanh Menu Nav Bar bên trái
     */
    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(220, getHeight()));
        navBar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(220, 220, 220))); // Viền phải xám mỏng

        // Logo hoặc Tiêu đề trên cùng
        JLabel lblLogo = new JLabel("BIDA SYSTEM");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
        lblLogo.setForeground(mainColor);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(30, 0, 40, 0));
        navBar.add(lblLogo);

        // Các nút Menu
        String[] menuItems = {"Tổng quan", "Sản phẩm", "Hóa đơn", "Khách hàng", "Cài đặt", "Đăng xuất"};
        
        for (String item : menuItems) {
            JButton btnMenu = new JButton(item);
            btnMenu.setFont(new Font("Arial", Font.BOLD, 15));
            btnMenu.setForeground(new Color(80, 80, 80));
            btnMenu.setBackground(Color.WHITE);
            btnMenu.setFocusPainted(false);
            btnMenu.setBorderPainted(false);
            btnMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
            btnMenu.setMaximumSize(new Dimension(220, 50));
            btnMenu.setHorizontalAlignment(SwingConstants.LEFT);
            btnMenu.setBorder(new EmptyBorder(0, 30, 0, 0)); // Căn chữ lệch phải một chút
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Đánh dấu menu hiện tại (Tổng quan)
            if (item.equals("Tổng quan")) {
                btnMenu.setForeground(mainColor);
                btnMenu.setBackground(new Color(235, 245, 245));
                btnMenu.setBorder(new CompoundBorder(
                        new MatteBorder(0, 4, 0, 0, mainColor), // Vạch viền dọc báo hiệu đang chọn
                        new EmptyBorder(0, 26, 0, 0)
                ));
            }

            // Xử lý Hover
            btnMenu.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!item.equals("Tổng quan")) {
                        btnMenu.setBackground(new Color(245, 245, 245));
                        btnMenu.setForeground(mainColor);
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (!item.equals("Tổng quan")) {
                        btnMenu.setBackground(Color.WHITE);
                        btnMenu.setForeground(new Color(80, 80, 80));
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    if (item.equals("Đăng xuất")) {
                        int confirm = JOptionPane.showConfirmDialog(ThongKeUI.this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            new LoginUI().setVisible(true);
                            dispose();
                        }
                    } else if (item.equals("Tổng quan")) {
                        // Đang ở trang Tổng quan rồi, không làm gì
                    } else {
                        JOptionPane.showMessageDialog(ThongKeUI.this, "Chuyển sang trang: " + item);
                    }
                }
            });
            navBar.add(btnMenu);
            navBar.add(Box.createVerticalStrut(5));
        }

        navBar.add(Box.createVerticalGlue()); // Đẩy các menu lên trên
        return navBar;
    }

    /**
     * Hàm tạo một Thẻ thông số (Card) vuông vức phong cách Flat Design
     */
    private JPanel createStatCard(String title, String value, String subText, Color trendColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        // Thiết kế phẳng: Viền xám nhạt, không bo góc
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 24));
        lblValue.setForeground(new Color(40, 40, 40));

        JLabel lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Arial", Font.PLAIN, 12));
        lblSub.setForeground(trendColor);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);
        card.add(Box.createVerticalStrut(15));
        card.add(lblSub);

        return card;
    }

    /**
     * Hàm tạo Biểu đồ Doanh thu bằng thư viện XChart
     */
    private JPanel createChartPanel() {
        // Tạo biểu đồ dạng Cột (Bar Chart)
        CategoryChart chart = new CategoryChartBuilder()
                .width(800)
                .height(400)
                .title("Biểu đồ Doanh thu 7 ngày qua")
                .xAxisTitle("Ngày")
                .yAxisTitle("Doanh thu (VNĐ)")
                .build();

        // --- Custom Giao diện Biểu đồ chuẩn Flat Design ---
        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);

        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230)); // Lưới màu xám nhạt
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setChartTitleFont(new Font("Arial", Font.BOLD, 18));
        chart.getStyler().setAxisTickLabelsFont(new Font("Arial", Font.PLAIN, 12));
        chart.getStyler().setSeriesColors(new Color[] { mainColor }); // Cột màu Xanh của Login

        // DỮ LIỆU MẪU (Dummy Data) 
        // Sau này bạn gọi List<Map> từ ThongKeDAO, tách ra làm 2 mảng xData và yData rồi nạp vào đây
        List<String> xData = Arrays.asList("01/05", "02/05", "03/05", "04/05", "05/05", "06/05", "07/05");
        List<Double> yData = Arrays.asList(2100000.0, 1850000.0, 3200000.0, 1500000.0, 4100000.0, 2800000.0, 3500000.0);

        chart.addSeries("Doanh thu", xData, yData);

        // Bọc XChart vào một JPanel của Swing
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(new LineBorder(new Color(220, 220, 220), 1)); // Viền cho khối biểu đồ
        
        // XChartPanel chính là Component để nhúng vào Swing
        XChartPanel<CategoryChart> xChartPanel = new XChartPanel<>(chart);
        chartContainer.add(xChartPanel, BorderLayout.CENTER);

        return chartContainer;
    }

    public static void main(String[] args) {
        // Chỉnh UI theo chuẩn hệ điều hành trước khi load
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ThongKeUI().setVisible(true);
        });
    }
}