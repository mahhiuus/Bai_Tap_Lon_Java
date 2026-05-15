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

        setLayout(new BorderLayout(20, 20));
        setBackground(LuxuryTheme.CREAM);
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

        // Tính toán các thông số
        double doanhThu = thongKeDao.getDoanhThuThangHienTai();
        double giaVon = thongKeDao.getGiaVonThangHienTai();
        double loiNhuan = doanhThu - giaVon; // Lãi gộp = Doanh Thu - Tiền vốn nhập hàng

        String dtThangNay = currencyFormat.format(doanhThu);
        String hdThangNay = String.valueOf(thongKeDao.getSoHoaDonThangHienTai());
        String lnThangNay = currencyFormat.format(loiNhuan); // Thay thế Khách Hàng bằng LỢI NHUẬN
        String banHoatDong = String.valueOf(thongKeDao.getSoBanDangHoatDong());

        cardsPanel.add(createStatCard("Doanh thu tháng", dtThangNay,"💰" ));
        cardsPanel.add(createStatCard("Lợi nhuận gộp", lnThangNay, "📈")); // Thẻ Lợi Nhuận
        cardsPanel.add(createStatCard("Số hóa đơn tháng", hdThangNay, "📄"));
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

    private JPanel createTabTheoNgay() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        chartContainerNgay = new JPanel(new BorderLayout());
        chartContainerNgay.setBackground(Color.WHITE);
        loadChartDataNgay(today.minusDays(6), today); 
        panel.add(chartContainerNgay, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTabTheoThang() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

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

        chartContainerThang = new JPanel(new BorderLayout());
        chartContainerThang.setBackground(Color.WHITE);
        loadChartDataThang(currentYear); 
        panel.add(chartContainerThang, BorderLayout.CENTER);

        return panel;
    }

    // --- BIỂU ĐỒ KÉP: DOANH THU & LỢI NHUẬN (NGÀY) ---
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
                
                // --- CHIA CHO 1 TRIỆU ĐỂ HIỂN THỊ ĐƠN VỊ TRIỆU VNĐ ---
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

    // --- BIỂU ĐỒ KÉP: DOANH THU & LỢI NHUẬN (THÁNG) ---
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
                
                // --- CHIA CHO 1 TRIỆU ĐỂ HIỂN THỊ ĐƠN VỊ TRIỆU VNĐ ---
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
        // Đổi yAxisTitle thành "Triệu VNĐ"
        CategoryChart chart = new CategoryChartBuilder().width(800).height(350).title(title)
                .xAxisTitle(xAxisTitle).yAxisTitle("Số tiền (Triệu VNĐ)").build();

        chart.getStyler().setLegendPosition(LegendPosition.InsideNW);
        chart.getStyler().setPlotGridLinesVisible(true);
        chart.getStyler().setPlotGridLinesColor(new Color(230, 230, 230));
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(Color.WHITE);
        
        // --- ÉP ĐỊNH DẠNG SỐ BÌNH THƯỜNG (KHÔNG DÙNG 1E4, 1E5) ---
        chart.getStyler().setYAxisDecimalPattern("#,##0.##");
        
        // Màu Navy (Doanh Thu) & Vàng Gold (Lợi Nhuận) tạo sự tương phản chuẩn Luxury
        chart.getStyler().setSeriesColors(new Color[] { LuxuryTheme.NAVY, LuxuryTheme.GOLD }); 
        return chart;
    }

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
        lblValue.setForeground(LuxuryTheme.NAVY); 

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(10));
        textPanel.add(lblValue);

        JLabel lblIcon = new JLabel(fontAwesomeIcon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30)); 
        lblIcon.setForeground(LuxuryTheme.GOLD);

        card.add(textPanel, BorderLayout.WEST);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }
}