package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDao {

    // 1. Lấy Doanh thu của THÁNG HIỆN TẠI
    public double getDoanhThuThangHienTai() {
        String sql = "SELECT SUM(tong_tien) FROM hoa_don_ban WHERE MONTH(ngay_ban) = MONTH(CURRENT_DATE()) AND YEAR(ngay_ban) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 2. Lấy Tổng số hóa đơn của THÁNG HIỆN TẠI
    public int getSoHoaDonThangHienTai() {
        String sql = "SELECT COUNT(*) FROM hoa_don_ban WHERE MONTH(ngay_ban) = MONTH(CURRENT_DATE()) AND YEAR(ngay_ban) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 3. Lấy Số khách hàng mới của THÁNG HIỆN TẠI (Giữ nguyên code cũ dự phòng)
    public int getKhachHangMoiThangHienTai() {
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE MONTH(ngay_dang_ky) = MONTH(CURRENT_DATE()) AND YEAR(ngay_dang_ky) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 4. Lấy số Bàn đang hoạt động
    public int getSoBanDangHoatDong() {
        String sql = "SELECT COUNT(*) FROM phien_choi WHERE trang_thai != 'DA_KET_THUC'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // ==============================================================================
    // CÁC HÀM MỚI ĐỂ TÍNH TIỀN VỐN (Dùng giá nhập trung bình) VÀ LỢI NHUẬN
    // ==============================================================================
    
    // Tính tổng Tiền Vốn của các món hàng đã bán trong THÁNG NÀY
    public double getGiaVonThangHienTai() {
        String sql = "SELECT SUM(c.so_luong * COALESCE((SELECT SUM(n.so_luong * n.don_gia_nhap)/NULLIF(SUM(n.so_luong), 0) FROM chi_tiet_hoa_don_nhap n WHERE n.ma_sp = c.ma_sp), 0)) " +
                     "FROM chi_tiet_hoa_don_ban c JOIN hoa_don_ban h ON c.ma_hdb = h.ma_hdb " +
                     "WHERE MONTH(h.ngay_ban) = MONTH(CURRENT_DATE()) AND YEAR(h.ngay_ban) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Lấy tiền vốn đã bán theo TỪNG NGÀY
    private double getGiaVonTheoNgay(LocalDate ngay) {
        String sql = "SELECT SUM(c.so_luong * COALESCE((SELECT SUM(n.so_luong * n.don_gia_nhap)/NULLIF(SUM(n.so_luong), 0) FROM chi_tiet_hoa_don_nhap n WHERE n.ma_sp = c.ma_sp), 0)) " +
                     "FROM chi_tiet_hoa_don_ban c JOIN hoa_don_ban h ON c.ma_hdb = h.ma_hdb WHERE h.ngay_ban = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ngay));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {}
        return 0;
    }

    // Lấy tiền vốn đã bán theo TỪNG THÁNG
    private double getGiaVonTheoThang(int thang, int nam) {
        String sql = "SELECT SUM(c.so_luong * COALESCE((SELECT SUM(n.so_luong * n.don_gia_nhap)/NULLIF(SUM(n.so_luong), 0) FROM chi_tiet_hoa_don_nhap n WHERE n.ma_sp = c.ma_sp), 0)) " +
                     "FROM chi_tiet_hoa_don_ban c JOIN hoa_don_ban h ON c.ma_hdb = h.ma_hdb WHERE MONTH(h.ngay_ban) = ? AND YEAR(h.ngay_ban) = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang); ps.setInt(2, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (Exception e) {}
        return 0;
    }

    // 5. CẬP NHẬT LẠI: Lấy dữ liệu Biểu đồ (Doanh Thu vs Lợi Nhuận) THEO NGÀY
    public List<Map<String, Object>> getDuLieuBieuDoTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT ngay_ban, SUM(tong_tien) as doanh_thu " +
                     "FROM hoa_don_ban WHERE ngay_ban BETWEEN ? AND ? " +
                     "GROUP BY ngay_ban ORDER BY ngay_ban ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                LocalDate date = rs.getDate("ngay_ban").toLocalDate();
                String labelNgay = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
                
                double doanhThu = rs.getDouble("doanh_thu");
                double giaVonSP = getGiaVonTheoNgay(date); // Gọi hàm phụ để chốt Giá Vốn
                double loiNhuan = doanhThu - giaVonSP;     // Lợi nhuận = Tổng thu - Vốn SP

                row.put("ngay_ban_label", labelNgay);
                row.put("doanh_thu", doanhThu);
                row.put("loi_nhuan", loiNhuan);
                ketQua.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }

    // 6. CẬP NHẬT LẠI: Lấy dữ liệu Biểu đồ (Doanh Thu vs Lợi Nhuận) THEO THÁNG
    public List<Map<String, Object>> getDuLieuBieuDoTheoThang(int nam) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT MONTH(ngay_ban) as thang, SUM(tong_tien) as doanh_thu " +
                     "FROM hoa_don_ban WHERE YEAR(ngay_ban) = ? " +
                     "GROUP BY thang ORDER BY thang ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                int thang = rs.getInt("thang");
                String labelThang = "Tháng " + thang;
                
                double doanhThu = rs.getDouble("doanh_thu");
                double giaVonSP = getGiaVonTheoThang(thang, nam); // Gọi hàm phụ lấy Giá Vốn
                double loiNhuan = doanhThu - giaVonSP;            // Lợi nhuận
                
                row.put("thang_label", labelThang);
                row.put("doanh_thu", doanhThu);
                row.put("loi_nhuan", loiNhuan);
                ketQua.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }
}