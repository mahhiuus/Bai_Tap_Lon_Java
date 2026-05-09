package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
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

public class ThongKeUI extends JFrame {

    private Color mainColor = new Color(17, 126, 141);
    private Color bgColor = new Color(240, 242, 245); 
    private Color cardColor = Color.WHITE;

    private ThongKeDao ThongKeDao;
    private DecimalFormat currencyFormat;
    
    // Khai báo các container chứa chart để update khi lọc
    private JPanel chartContainerNgay;
    private JPanel chartContainerThang;
    
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<Integer> cbNam;

    public ThongKeUI() {
        ThongKeDao = new ThongKeDao();
        currencyFormat = new DecimalFormat("#,### VNĐ");

        setTitle("Dashboard Analytics - Billard Management System");
        setSize(1200, 750); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        add(createNavBar(), BorderLayout.WEST);

        JPanel mainContent = new JPanel(new BorderLayout(20, 20));
        mainContent.setBackground(bgColor);
        mainContent.setBorder(new EmptyBorder(25, 30, 25, 30));

        // HEADER
        JLabel lblHeader = new JLabel("Tổng quan kinh doanh của quán Billard");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
        lblHeader.setForeground(new Color(40, 40, 40));
        mainContent.add(lblHeader, BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 25));
        centerArea.setBackground(bgColor);

        // --- 4 THẺ SỐ LIỆU TỪ DATABASE ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(bgColor);

        String dtThangNay = currencyFormat.format(ThongKeDao.getDoanhThuThangHienTai());
        String hdThangNay = String.valueOf(ThongKeDao.getSoHoaDonThangHienTai());
        String khThangNay = String.valueOf(ThongKeDao.getKhachHangMoiThangHienTai());
        String banHoatDong = String.valueOf(ThongKeDao.getSoBanDangHoatDong());

        cardsPanel.add(createStatCard("Doanh thu tháng này", dtThangNay, "Tính đến hôm nay", new Color(46, 204, 113)));
        cardsPanel.add(createStatCard("Số hóa đơn tháng này", hdThangNay, "Đã thanh toán", new Color(52, 152, 219)));
        cardsPanel.add(createStatCard("Khách hàng mới (Tháng)", khThangNay, "Tài khoản đăng ký mới", new Color(155, 89, 182)));
        cardsPanel.add(createStatCard("Bàn đang hoạt động", banHoatDong, "Trạng thái DANG_CHOI", new Color(230, 126, 34)));
        
        centerArea.add(cardsPanel, BorderLayout.NORTH);

        // --- BIỂU ĐỒ VỚI 2 TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setFocusable(false);
        
        tabbedPane.addTab("Doanh số theo ngày", createTabTheoNgay());
        tabbedPane.addTab("Doanh số theo tháng", createTabTheoThang());

        centerArea.add(tabbedPane, BorderLayout.CENTER);

        mainContent.add(centerArea, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }

    /**
     * TẠO TAB 1: DOANH SỐ THEO NGÀY
     */
    private JPanel createTabTheoNgay() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bộ lọc Từ ngày - Đến ngày
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate today = LocalDate.now();

        filterPanel.add(new JLabel("Từ ngày:"));
        txtTuNgay = new JTextField(today.minusDays(6).format(dtf), 8);
        txtTuNgay.setFont(new Font("Arial", Font.PLAIN, 14));
        filterPanel.add(txtTuNgay);

        filterPanel.add(new JLabel("Đến ngày:"));
        txtDenNgay = new JTextField(today.format(dtf), 8);
        txtDenNgay.setFont(new Font("Arial", Font.PLAIN, 14));
        filterPanel.add(txtDenNgay);

      JButton btnLoc = new JButton("Lọc");
btnLoc.setBackground(mainColor);
btnLoc.setForeground(Color.WHITE);
btnLoc.setFocusPainted(false);
btnLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
btnLoc.setOpaque(true); 
btnLoc.setBorderPainted(false);
        btnLoc.addActionListener(e -> {
            try {
                LocalDate start = LocalDate.parse(txtTuNgay.getText().trim(), dtf);
                LocalDate end = LocalDate.parse(txtDenNgay.getText().trim(), dtf);
                loadChartDataNgay(start, end);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày đúng định dạng dd/MM/yyyy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        filterPanel.add(btnLoc);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Chứa biểu đồ
        chartContainerNgay = new JPanel(new BorderLayout());
        chartContainerNgay.setBackground(Color.WHITE);
        loadChartDataNgay(today.minusDays(6), today); // Load mặc định 7 ngày
        panel.add(chartContainerNgay, BorderLayout.CENTER);

        return panel;
    }

    /**
     * TẠO TAB 2: DOANH SỐ THEO THÁNG
     */
    private JPanel createTabTheoThang() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bộ lọc theo Năm
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterPanel.setBackground(Color.WHITE);

        filterPanel.add(new JLabel("Chọn năm:"));
        int currentYear = LocalDate.now().getYear();
        Integer[] years = {currentYear, currentYear - 1, currentYear - 2}; // Cho chọn 3 năm gần nhất
        cbNam = new JComboBox<>(years);
        cbNam.setFont(new Font("Arial", Font.PLAIN, 14));
        filterPanel.add(cbNam);

        JButton btnLoc = new JButton("Lọc");
        btnLoc.setBackground(mainColor);
        btnLoc.setForeground(Color.WHITE);
        btnLoc.setFocusPainted(false);
        btnLoc.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnLoc.addActionListener(e -> {
            int selectedYear = (Integer) cbNam.getSelectedItem();
            loadChartDataThang(selectedYear);
        });
        filterPanel.add(btnLoc);
        panel.add(filterPanel, BorderLayout.NORTH);

        // Chứa biểu đồ
        chartContainerThang = new JPanel(new BorderLayout());
        chartContainerThang.setBackground(Color.WHITE);
        loadChartDataThang(currentYear); // Load mặc định năm hiện tại
        panel.add(chartContainerThang, BorderLayout.CENTER);

        return panel;
    }

    /**
     * VẼ BIỂU ĐỒ THEO NGÀY
     */
    private void loadChartDataNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> data = ThongKeDao.getDuLieuBieuDoTheoNgay(tuNgay, denNgay);
        List<String> xData = new ArrayList<>();
        List<Double> yTiềnBida = new ArrayList<>();
        List<Double> yTiềnSP = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yTiềnBida.add(0.0); yTiềnSP.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("ngay_ban_label"));
                yTiềnBida.add((Double) row.get("tien_bida"));
                yTiềnSP.add((Double) row.get("tien_sp"));
            }
        }

        CategoryChart chart = taoKhungBieuDo("Doanh thu từ " + tuNgay + " đến " + denNgay, "Ngày");
        chart.addSeries("Tiền Bida", xData, yTiềnBida);
        chart.addSeries("Tiền Sản Phẩm", xData, yTiềnSP);

        chartContainerNgay.removeAll();
        chartContainerNgay.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerNgay.revalidate();
        chartContainerNgay.repaint();
    }

    /**
     * VẼ BIỂU ĐỒ THEO THÁNG
     */
    private void loadChartDataThang(int nam) {
        List<Map<String, Object>> data = ThongKeDao.getDuLieuBieuDoTheoThang(nam);
        List<String> xData = new ArrayList<>();
        List<Double> yTiềnBida = new ArrayList<>();
        List<Double> yTiềnSP = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yTiềnBida.add(0.0); yTiềnSP.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("thang_label"));
                yTiềnBida.add((Double) row.get("tien_bida"));
                yTiềnSP.add((Double) row.get("tien_sp"));
            }
        }

        CategoryChart chart = taoKhungBieuDo("Doanh thu năm " + nam, "Tháng");
        chart.addSeries("Tiền Bida", xData, yTiềnBida);
        chart.addSeries("Tiền Sản Phẩm", xData, yTiềnSP);

        chartContainerThang.removeAll();
        chartContainerThang.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerThang.revalidate();
        chartContainerThang.repaint();
    }

    /**
     * Cấu hình form chuẩn dùng chung cho 2 loại biểu đồ (Giúp code đỡ bị lặp)
     */
    private CategoryChart taoKhungBieuDo(String title, String xAxisTitle) {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(350).title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle("Doanh thu (VNĐ)").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230));
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        chart.getStyler().setSeriesColors(new Color[] { mainColor, new Color(241, 196, 15) }); 
        return chart;
    }

    // ==============================================================
    // PHẦN BÊN DƯỚI GIỮ NGUYÊN (Thanh Nav Bar, Card Thống Kê, Main)
    // ==============================================================

    private JPanel createNavBar() {
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(Color.WHITE);
        navBar.setPreferredSize(new Dimension(220, getHeight()));
        navBar.setBorder(new MatteBorder(0, 0, 0, 1, new Color(220, 220, 220))); 

        JLabel lblLogo = new JLabel("BIDA SYSTEM");
        lblLogo.setFont(new Font("Arial", Font.BOLD, 20));
        lblLogo.setForeground(mainColor);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(new EmptyBorder(30, 0, 40, 0));
        navBar.add(lblLogo);

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
            btnMenu.setBorder(new EmptyBorder(0, 30, 0, 0));
            btnMenu.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (item.equals("Tổng quan")) {
                btnMenu.setForeground(mainColor);
                btnMenu.setBackground(new Color(235, 245, 245));
                btnMenu.setBorder(new CompoundBorder(
                        new MatteBorder(0, 4, 0, 0, mainColor), 
                        new EmptyBorder(0, 26, 0, 0)
                ));
            }

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
                    }
                }
            });
            navBar.add(btnMenu);
            navBar.add(Box.createVerticalStrut(5));
        }
        navBar.add(Box.createVerticalGlue()); 
        return navBar;
    }

    private JPanel createStatCard(String title, String value, String subText, Color trendColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardColor);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 22));
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

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> {
            new ThongKeUI().setVisible(true);
        });
    }
}