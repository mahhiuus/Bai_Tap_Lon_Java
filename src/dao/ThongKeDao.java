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

    // 3. Lấy Số khách hàng mới của THÁNG HIỆN TẠI
    public int getKhachHangMoiThangHienTai() {
        String sql = "SELECT COUNT(*) FROM khach_hang WHERE MONTH(ngay_dang_ky) = MONTH(CURRENT_DATE()) AND YEAR(ngay_dang_ky) = YEAR(CURRENT_DATE())";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 4. Lấy số Bàn đang hoạt động (Trạng thái KHÁC 'DA_KET_THUC')
    public int getSoBanDangHoatDong() {
        String sql = "SELECT COUNT(*) FROM phien_choi WHERE trang_thai != 'DA_KET_THUC'";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 5. Lấy dữ liệu Biểu đồ (Tách Tiền Bida và Tiền Sản phẩm) THEO NGÀY
    public List<Map<String, Object>> getDuLieuBieuDoTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT ngay_ban, SUM(tien_bida) as tong_tien_bida, SUM(tien_san_pham) as tong_tien_sp " +
                     "FROM hoa_don_ban " +
                     "WHERE ngay_ban BETWEEN ? AND ? " +
                     "GROUP BY ngay_ban " +
                     "ORDER BY ngay_ban ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                LocalDate date = rs.getDate("ngay_ban").toLocalDate();
                String labelNgay = String.format("%02d/%02d", date.getDayOfMonth(), date.getMonthValue());
                
                row.put("ngay_ban_label", labelNgay);
                row.put("tien_bida", rs.getDouble("tong_tien_bida"));
                row.put("tien_sp", rs.getDouble("tong_tien_sp"));
                ketQua.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }

    // 6. Lấy dữ liệu Biểu đồ (Tách Tiền Bida và Tiền Sản phẩm) THEO THÁNG TRONG NĂM
    public List<Map<String, Object>> getDuLieuBieuDoTheoThang(int nam) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT MONTH(ngay_ban) as thang, " +
                     "SUM(tien_bida) as tong_tien_bida, SUM(tien_san_pham) as tong_tien_sp " +
                     "FROM hoa_don_ban " +
                     "WHERE YEAR(ngay_ban) = ? " +
                     "GROUP BY thang " +
                     "ORDER BY thang ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nam);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                String labelThang = "Tháng " + rs.getInt("thang");
                
                row.put("thang_label", labelThang);
                row.put("tien_bida", rs.getDouble("tong_tien_bida"));
                row.put("tien_sp", rs.getDouble("tong_tien_sp"));
                ketQua.add(row);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ketQua;
    }
}