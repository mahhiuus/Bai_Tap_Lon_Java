package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Import XChart
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler.LegendPosition;

// Import DAO
import dao.ThongKeDao;

public class ThongKeUI extends JPanel {

    private ThongKeDao thongKeDao;
    private DecimalFormat currencyFormat;
    
    private JPanel chartContainerNgay;
    private JPanel chartContainerThang;
    
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<Integer> cbNam;

    public ThongKeUI() {
        thongKeDao = new ThongKeDao();
        currencyFormat = new DecimalFormat("#,### VNĐ");

        setLayout(new BorderLayout(20, 25)); // Tăng khoảng cách chiều dọc cho thoáng
        setBackground(LuxuryTheme.CREAM);
        setBorder(new EmptyBorder(20, 30, 20, 30)); // Nới rộng không gian viền ngoài

        // HEADER
        JLabel lblHeader = new JLabel("Tổng quan kinh doanh của quán Billard");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 30)); // Tăng margin giữa card và chart
        centerArea.setBackground(LuxuryTheme.CREAM);

        // --- 4 THẺ SỐ LIỆU TỪ DATABASE THEO STYLE LUXURY ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 25, 15)); // Nới rộng khe hở giữa 4 thẻ
        cardsPanel.setBackground(LuxuryTheme.CREAM);

        // Tính toán các thông số
        double doanhThu = thongKeDao.getDoanhThuThangHienTai();
        double giaVon = thongKeDao.getGiaVonThangHienTai();
        double loiNhuan = doanhThu - giaVon; 

        String dtThangNay = currencyFormat.format(doanhThu);
        String hdThangNay = String.valueOf(thongKeDao.getSoHoaDonThangHienTai());
        String lnThangNay = currencyFormat.format(loiNhuan); 
        String banHoatDong = String.valueOf(thongKeDao.getSoBanDangHoatDong());

        cardsPanel.add(createStatCard("Doanh thu tháng", dtThangNay, "money"));
        cardsPanel.add(createStatCard("Lợi nhuận gộp", lnThangNay, "chart-line")); 
        cardsPanel.add(createStatCard("Số hóa đơn tháng", hdThangNay, "file-invoice"));
        cardsPanel.add(createStatCard("Bàn đang hoạt động", banHoatDong, "billiard"));
        cardsPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateCardsLayout(cardsPanel);
            }
        });
        updateCardsLayout(cardsPanel);
        
        centerArea.add(cardsPanel, BorderLayout.NORTH);

        // --- BIỂU ĐỒ VỚI 2 TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(LuxuryTheme.NAVY);
        tabbedPane.setFocusable(false);
        // Tắt viền xấu xí của JTabbedPane mặc định
        tabbedPane.setBorder(BorderFactory.createEmptyBorder()); 
        
        tabbedPane.addTab("  Doanh số theo ngày  ", createTabTheoNgay());
        tabbedPane.addTab("  Doanh số theo tháng  ", createTabTheoThang());

        centerArea.add(tabbedPane, BorderLayout.CENTER);

        add(centerArea, BorderLayout.CENTER);
    }

    private JPanel createTabTheoNgay() {
        // Áp dụng viền bo góc và bóng đổ cho Tab Biểu đồ
        ShadowRoundedPanel panel = new ShadowRoundedPanel(new BorderLayout(0, 15), 15);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        filterPanel.add(new JLabel("Từ ngày:"));
        txtTuNgay = LuxuryTheme.createTextField(); 
        txtTuNgay.setText(today.minusDays(6).format(dtf));
        filterPanel.add(txtTuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        txtDenNgay = LuxuryTheme.createTextField();
        txtDenNgay.setText(today.format(dtf));
        filterPanel.add(txtDenNgay);

        JButton btnLoc = LuxuryTheme.createButton("Lọc Dữ Liệu", LuxuryTheme.TEAL, Color.WHITE);
        btnLoc.addActionListener(e -> {
            try {
                LocalDate start = LocalDate.parse(txtTuNgay.getText().trim(), dtf);
                LocalDate end = LocalDate.parse(txtDenNgay.getText().trim(), dtf);
                if (start.isAfter(end)) {
                    JOptionPane.showMessageDialog(this, "Từ ngày không được sau đến ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                loadChartDataNgay(start, end);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày đúng định dạng dd/MM/yyyy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        filterPanel.add(btnLoc);
        panel.add(filterPanel, BorderLayout.NORTH);

        chartContainerNgay = new JPanel(new BorderLayout());
        chartContainerNgay.setOpaque(false);
        loadChartDataNgay(today.minusDays(6), today); 
        panel.add(chartContainerNgay, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabTheoThang() {
        // Áp dụng viền bo góc và bóng đổ cho Tab Biểu đồ
        ShadowRoundedPanel panel = new ShadowRoundedPanel(new BorderLayout(0, 15), 15);
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 20, 20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filterPanel.setOpaque(false);

        filterPanel.add(new JLabel("Chọn năm:"));
        int currentYear = LocalDate.now().getYear();
        Integer[] years = {currentYear, currentYear - 1, currentYear - 2}; 
        cbNam = new JComboBox<>(years);
        cbNam.setFont(new Font("Arial", Font.PLAIN, 15));
        cbNam.setBackground(Color.WHITE);
        filterPanel.add(cbNam);

        JButton btnLoc = LuxuryTheme.createButton("Lọc Dữ Liệu", LuxuryTheme.TEAL, Color.WHITE);
        btnLoc.addActionListener(e -> {
            int selectedYear = (Integer) cbNam.getSelectedItem();
            loadChartDataThang(selectedYear);
        });
        filterPanel.add(btnLoc);
        panel.add(filterPanel, BorderLayout.NORTH);

        chartContainerThang = new JPanel(new BorderLayout());
        chartContainerThang.setOpaque(false);
        loadChartDataThang(currentYear); 
        panel.add(chartContainerThang, BorderLayout.CENTER);

        return panel;
    }

    private void loadChartDataNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> data = thongKeDao.getDuLieuBieuDoTheoNgay(tuNgay, denNgay);
        List<String> xData = new ArrayList<>();
        List<Double> yDoanhThu = new ArrayList<>();
        List<Double> yLoiNhuan = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yDoanhThu.add(0.0); yLoiNhuan.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("ngay_ban_label"));
                double dt = (Double) row.get("doanh_thu");
                double ln = (Double) row.get("loi_nhuan");
                yDoanhThu.add(dt / 1000000.0);
                yLoiNhuan.add(ln / 1000000.0);
            }
        }

        CategoryChart chart = taoKhungBieuDo("Thống kê tài chính từ " + tuNgay + " đến " + denNgay, "Ngày");
        chart.addSeries("Doanh Thu", xData, yDoanhThu);
        chart.addSeries("Lợi Nhuận", xData, yLoiNhuan);

        chartContainerNgay.removeAll();
        chartContainerNgay.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerNgay.revalidate();
        chartContainerNgay.repaint();
    }

    private void loadChartDataThang(int nam) {
        List<Map<String, Object>> data = thongKeDao.getDuLieuBieuDoTheoThang(nam);
        List<String> xData = new ArrayList<>();
        List<Double> yDoanhThu = new ArrayList<>();
        List<Double> yLoiNhuan = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yDoanhThu.add(0.0); yLoiNhuan.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("thang_label"));
                double dt = (Double) row.get("doanh_thu");
                double ln = (Double) row.get("loi_nhuan");
                yDoanhThu.add(dt / 1000000.0);
                yLoiNhuan.add(ln / 1000000.0);
            }
        }

        CategoryChart chart = taoKhungBieuDo("Thống kê tài chính năm " + nam, "Tháng");
        chart.addSeries("Doanh Thu", xData, yDoanhThu);
        chart.addSeries("Lợi Nhuận", xData, yLoiNhuan);

        chartContainerThang.removeAll();
        chartContainerThang.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerThang.revalidate();
        chartContainerThang.repaint();
    }

    private CategoryChart taoKhungBieuDo(String title, String xAxisTitle) {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(350).title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle("Số tiền (Triệu VNĐ)").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(new Color(235, 235, 235)); // Line lưới nhạt hơn
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setChartTitleFont(new Font("Arial", Font.BOLD, 16));
        
        chart.getStyler().setYAxisDecimalPattern("#,##0.##");
        chart.getStyler().setSeriesColors(new Color[] { LuxuryTheme.NAVY, LuxuryTheme.GOLD }); 
        return chart;
    }

    private void updateCardsLayout(JPanel cardsPanel) {
        int columns = cardsPanel.getWidth() < 1050 ? 2 : 4;
        LayoutManager currentLayout = cardsPanel.getLayout();
        if (currentLayout instanceof GridLayout) {
            GridLayout grid = (GridLayout) currentLayout;
            if (grid.getColumns() == columns) return;
        }
        cardsPanel.setLayout(new GridLayout(0, columns, 25, 15));
        cardsPanel.revalidate();
    }

    private JPanel createStatCard(String title, String value, String fontAwesomeIcon) {
        // --- THAY VÌ JPANEL BÌNH THƯỜNG, DÙNG CLASS ĐỔ BÓNG BO GÓC ---
        ShadowRoundedPanel card = new ShadowRoundedPanel(new BorderLayout(14, 0), 15);
        card.setBackground(Color.WHITE);
        
        // Tăng padding để đẩy nội dung tách khỏi viền bóng
        card.setBorder(new EmptyBorder(24, 26, 24, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false); // Xuyên thấu để hiện nền Trắng của thẻ

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitle.setForeground(new Color(120, 120, 120)); // Xám thanh lịch

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, value.length() > 10 ? 23 : 26)); // To và rõ hơn
        lblValue.setForeground(LuxuryTheme.NAVY); 

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(8));
        textPanel.add(lblValue);

        JLabel lblIcon = new JLabel(FontAwesomeIcon.of(fontAwesomeIcon, LuxuryTheme.GOLD, 38));
        lblIcon.setPreferredSize(new Dimension(46, 52));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    // =========================================================================
    // INNER CLASS: PANEl TỰ ĐỘNG BO GÓC VÀ ĐỔ BÓNG MƯỢT MÀ
    // =========================================================================
    class ShadowRoundedPanel extends JPanel {
        private int cornerRadius;
        private Color shadowColor = new Color(0, 0, 0, 18); // Màu đen, độ mờ (Alpha) 18
        private int shadowSize = 5; // Độ rộng của bóng
        private int shadowOffset = 3; // Lệch bóng xuống dưới 3px

        public ShadowRoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false); // Phải set False để Swing vẽ được nền trong suốt chứa bóng
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth() - shadowSize - shadowOffset;
            int height = getHeight() - shadowSize - shadowOffset;

            // 1. Vẽ nhiều lớp bóng với màu nhạt dần để tạo hiệu ứng Blur mềm mại
            int shadowSteps = 4;
            for (int i = 0; i < shadowSteps; i++) {
                int alpha = (int) (shadowColor.getAlpha() * (1.0 - (double) i / shadowSteps));
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(), alpha));
                g2.fill(new RoundRectangle2D.Double(shadowOffset + i, shadowOffset + i, width, height, cornerRadius, cornerRadius));
            }

            // 2. Vẽ Nền chính (Màu trắng) của Panel
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, width, height, cornerRadius, cornerRadius));

            g2.dispose();
        }
    }
}
