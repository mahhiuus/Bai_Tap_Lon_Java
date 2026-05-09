package ui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
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

// Đổi từ JFrame sang JPanel
public class ThongKeUI extends JPanel {

    private ThongKeDao thongKeDao;
    private DecimalFormat currencyFormat;
    
    // Khai báo các container chứa chart để update khi lọc
    private JPanel chartContainerNgay;
    private JPanel chartContainerThang;
    
    private JTextField txtTuNgay;
    private JTextField txtDenNgay;
    private JComboBox<Integer> cbNam;

    public ThongKeUI() {
        thongKeDao = new ThongKeDao();
        currencyFormat = new DecimalFormat("#,### VNĐ");

        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM); // Nền màu Cream
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // HEADER
        JLabel lblHeader = new JLabel("Tổng quan kinh doanh của quán Billard");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 26));
        lblHeader.setForeground(LuxuryTheme.NAVY);
        add(lblHeader, BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(0, 25));
        centerArea.setBackground(LuxuryTheme.CREAM);

        // --- 4 THẺ SỐ LIỆU TỪ DATABASE THEO STYLE LUXURY ---
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setBackground(LuxuryTheme.CREAM);

        String dtThangNay = currencyFormat.format(thongKeDao.getDoanhThuThangHienTai());
        String hdThangNay = String.valueOf(thongKeDao.getSoHoaDonThangHienTai());
        String khThangNay = String.valueOf(thongKeDao.getKhachHangMoiThangHienTai());
        String banHoatDong = String.valueOf(thongKeDao.getSoBanDangHoatDong());

        // Sử dụng Icon Emoji đã được thu nhỏ
        cardsPanel.add(createStatCard("Doanh thu tháng", dtThangNay,"💰" ));
        cardsPanel.add(createStatCard("Số hóa đơn tháng", hdThangNay, "📄"));
        cardsPanel.add(createStatCard("Khách hàng mới", khThangNay, "👤"));
        cardsPanel.add(createStatCard("Bàn đang hoạt động", banHoatDong, "🎱"));
        
        centerArea.add(cardsPanel, BorderLayout.NORTH);

        // --- BIỂU ĐỒ VỚI 2 TABS ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(LuxuryTheme.NAVY);
        tabbedPane.setFocusable(false);
        
        tabbedPane.addTab("Doanh số theo ngày", createTabTheoNgay());
        tabbedPane.addTab("Doanh số theo tháng", createTabTheoThang());

        centerArea.add(tabbedPane, BorderLayout.CENTER);

        add(centerArea, BorderLayout.CENTER);
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

        JButton btnLoc = LuxuryTheme.createButton("Lọc", LuxuryTheme.TEAL, Color.WHITE);
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
        Integer[] years = {currentYear, currentYear - 1, currentYear - 2}; 
        cbNam = new JComboBox<>(years);
        cbNam.setFont(new Font("Arial", Font.PLAIN, 14));
        filterPanel.add(cbNam);

        JButton btnLoc = LuxuryTheme.createButton("Lọc", LuxuryTheme.TEAL, Color.WHITE);
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
        List<Map<String, Object>> data = thongKeDao.getDuLieuBieuDoTheoNgay(tuNgay, denNgay);
        List<String> xData = new ArrayList<>();
        List<Double> yTienBida = new ArrayList<>();
        List<Double> yTienSP = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yTienBida.add(0.0); yTienSP.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("ngay_ban_label"));
                yTienBida.add((Double) row.get("tien_bida"));
                yTienSP.add((Double) row.get("tien_sp"));
            }
        }

        CategoryChart chart = taoKhungBieuDo("Doanh thu từ " + tuNgay + " đến " + denNgay, "Ngày");
        chart.addSeries("Tiền Bida", xData, yTienBida);
        chart.addSeries("Tiền Sản Phẩm", xData, yTienSP);

        chartContainerNgay.removeAll();
        chartContainerNgay.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerNgay.revalidate();
        chartContainerNgay.repaint();
    }

    /**
     * VẼ BIỂU ĐỒ THEO THÁNG
     */
    private void loadChartDataThang(int nam) {
        List<Map<String, Object>> data = thongKeDao.getDuLieuBieuDoTheoThang(nam);
        List<String> xData = new ArrayList<>();
        List<Double> yTienBida = new ArrayList<>();
        List<Double> yTienSP = new ArrayList<>();

        if (data.isEmpty()) {
            xData.add("Không có DL"); yTienBida.add(0.0); yTienSP.add(0.0);
        } else {
            for (Map<String, Object> row : data) {
                xData.add((String) row.get("thang_label"));
                yTienBida.add((Double) row.get("tien_bida"));
                yTienSP.add((Double) row.get("tien_sp"));
            }
        }

        CategoryChart chart = taoKhungBieuDo("Doanh thu năm " + nam, "Tháng");
        chart.addSeries("Tiền Bida", xData, yTienBida);
        chart.addSeries("Tiền Sản Phẩm", xData, yTienSP);

        chartContainerThang.removeAll();
        chartContainerThang.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainerThang.revalidate();
        chartContainerThang.repaint();
    }

    /**
     * Cấu hình form chuẩn dùng chung cho XChart
     */
    private CategoryChart taoKhungBieuDo(String title, String xAxisTitle) {
        CategoryChart chart = new CategoryChartBuilder().width(800).height(350).title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle("Doanh thu (VNĐ)").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230));
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        // PHỐI MÀU BIỂU ĐỒ: Màu Navy (Tiền bida) và Vàng Gold (Sản phẩm)
        chart.getStyler().setSeriesColors(new Color[] { LuxuryTheme.NAVY, LuxuryTheme.GOLD }); 
        return chart;
    }

    /**
     * Tạo Thẻ thông số chuẩn Luxury
     */
    private JPanel createStatCard(String title, String value, String fontAwesomeIcon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Viền bóng mỏng, nền cùng màu tổng thể
        card.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Arial", Font.BOLD, 22));
        lblValue.setForeground(LuxuryTheme.NAVY); // Chữ màu NAVY

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(lblValue);

        // Icon Emoji được thu nhỏ xuống size 31
        JLabel lblIcon = new JLabel(fontAwesomeIcon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30)); 
        lblIcon.setForeground(LuxuryTheme.GOLD);

        card.add(textPanel, BorderLayout.WEST);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }
}