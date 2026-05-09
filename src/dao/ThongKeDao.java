package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKeDao {

    /**
     * Lấy tổng doanh thu của toàn bộ cửa hàng từ trước đến nay
     * @return Tổng tiền (double)
     */
    public double tinhTongDoanhThu() {
        String sql = "SELECT SUM(tong_tien) FROM hoa_don_ban";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    /**
     * Tính tổng doanh thu trong một ngày cụ thể
     * @param ngay Ngày cần thống kê
     * @return Tổng tiền (double)
     */
    public double tinhDoanhThuTheoNgay(LocalDate ngay) {
        String sql = "SELECT SUM(tong_tien) FROM hoa_don_ban WHERE ngay_ban = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ngay));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    /**
     * Tính tổng doanh thu trong một tháng/năm cụ thể
     * @param thang Tháng cần thống kê
     * @param nam Năm cần thống kê
     * @return Tổng tiền (double)
     */
    public double tinhDoanhThuTheoThang(int thang, int nam) {
        String sql = "SELECT SUM(tong_tien) FROM hoa_don_ban WHERE MONTH(ngay_ban) = ? AND YEAR(ngay_ban) = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    /**
     * Thống kê chi tiết doanh thu và số lượng hóa đơn theo từng ngày trong một khoảng thời gian
     * @param tuNgay Ngày bắt đầu
     * @param denNgay Ngày kết thúc
     * @return Danh sách các Map chứa (ngay_ban, so_hoa_don, doanh_thu)
     */
    public List<Map<String, Object>> doanhThuTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT ngay_ban, COUNT(*) as so_hoa_don, SUM(tong_tien) as doanh_thu " +
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
                row.put("ngay_ban", rs.getDate("ngay_ban").toLocalDate());
                row.put("so_hoa_don", rs.getInt("so_hoa_don"));
                row.put("doanh_thu", rs.getDouble("doanh_thu"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Thống kê tổng doanh thu theo từng tháng trong một năm cụ thể
     * @param nam Năm cần thống kê
     * @return Danh sách các Map chứa (thang, so_hoa_don, doanh_thu)
     */
    public List<Map<String, Object>> doanhThuTheoThang(int nam) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT MONTH(ngay_ban) as thang, COUNT(*) as so_hoa_don, SUM(tong_tien) as doanh_thu " +
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
                row.put("thang", rs.getInt("thang"));
                row.put("so_hoa_don", rs.getInt("so_hoa_don"));
                row.put("doanh_thu", rs.getDouble("doanh_thu"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Lấy Top N các sản phẩm/dịch vụ bán chạy nhất trong khoảng thời gian
     * @return Danh sách các Map chứa (ten_sp, loai, tong_so_luong, doanh_thu)
     */
    public List<Map<String, Object>> sanPhamBanChay(LocalDate tuNgay, LocalDate denNgay, int limit) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT sp.ten_sp, sp.loai, SUM(ct.so_luong) as tong_so_luong, SUM(ct.so_luong * ct.don_gia_ban) as doanh_thu " +
                     "FROM chi_tiet_hoa_don_ban ct " +
                     "JOIN san_pham sp ON ct.ma_sp = sp.ma_sp " +
                     "JOIN hoa_don_ban hdb ON ct.ma_hdb = hdb.ma_hdb " +
                     "WHERE hdb.ngay_ban BETWEEN ? AND ? " +
                     "GROUP BY sp.ma_sp, sp.ten_sp, sp.loai " +
                     "ORDER BY tong_so_luong DESC " +
                     "LIMIT ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ten_sp", rs.getString("ten_sp"));
                row.put("loai", rs.getString("loai"));
                row.put("tong_so_luong", rs.getInt("tong_so_luong"));
                row.put("doanh_thu", rs.getDouble("doanh_thu"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Thống kê chi tiết doanh thu tiền bàn bida và tiền sản phẩm riêng biệt theo ngày
     */
    public List<Map<String, Object>> phienChoiTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT ngay_ban, COUNT(*) as so_phien, SUM(tien_bida) as tong_tien_bida, " +
                     "SUM(tien_san_pham) as tong_tien_sp, SUM(tong_tien) as tong_tien " +
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
                row.put("ngay_ban", rs.getDate("ngay_ban").toLocalDate());
                row.put("so_phien", rs.getInt("so_phien"));
                row.put("tong_tien_bida", rs.getDouble("tong_tien_bida"));
                row.put("tong_tien_sp", rs.getDouble("tong_tien_sp"));
                row.put("tong_tien", rs.getDouble("tong_tien"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Xem danh sách hàng hóa/sản phẩm còn tồn kho và thông tin nhà cung cấp
     */
    public List<Map<String, Object>> tonKhoSanPham() {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT sp.ten_sp, sp.loai, sp.gia_ban, sp.so_luong_ton, ncc.ten_cong_ty " +
                     "FROM san_pham sp " +
                     "LEFT JOIN nha_cung_cap ncc ON sp.ma_ncc = ncc.ma_ncc " +
                     "ORDER BY sp.so_luong_ton ASC";
                     
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ten_sp", rs.getString("ten_sp"));
                row.put("loai", rs.getString("loai"));
                row.put("gia_ban", rs.getDouble("gia_ban"));
                row.put("so_luong_ton", rs.getInt("so_luong_ton"));
                row.put("ten_cong_ty", rs.getString("ten_cong_ty"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Thống kê năng suất nhân viên (Nhân viên nào mang lại nhiều doanh thu nhất)
     */
    public List<Map<String, Object>> nhanVienTheoDoanhThu(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT nv.ho_ten, COUNT(*) as so_hoa_don, SUM(hdb.tong_tien) as doanh_thu " +
                     "FROM hoa_don_ban hdb " +
                     "JOIN nhan_vien nv ON hdb.ma_nv = nv.ma_nv " +
                     "WHERE hdb.ngay_ban BETWEEN ? AND ? " +
                     "GROUP BY nv.ma_nv, nv.ho_ten " +
                     "ORDER BY doanh_thu DESC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ho_ten", rs.getString("ho_ten"));
                row.put("so_hoa_don", rs.getInt("so_hoa_don"));
                row.put("doanh_thu", rs.getDouble("doanh_thu"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Phân tích xem bàn bida nào được sử dụng tần suất nhiều nhất
     */
    public List<Map<String, Object>> banBidaThongKe(LocalDate tuNgay, LocalDate denNgay) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT bb.so_ban, bb.loai, COUNT(*) as so_phien, SUM(hdb.tien_bida) as tong_tien_bida " +
                     "FROM hoa_don_ban hdb " +
                     "JOIN phien_choi pc ON hdb.ma_phien = pc.ma_phien " +
                     "JOIN ban_bida bb ON pc.ma_ban = bb.ma_ban " +
                     "WHERE hdb.ngay_ban BETWEEN ? AND ? " +
                     "GROUP BY bb.ma_ban, bb.so_ban, bb.loai " +
                     "ORDER BY so_phien DESC";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("so_ban", rs.getInt("so_ban"));
                row.put("loai", rs.getString("loai"));
                row.put("so_phien", rs.getInt("so_phien"));
                row.put("tong_tien_bida", rs.getDouble("tong_tien_bida"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }

    /**
     * Top N Khách hàng chi tiêu nhiều / chơi nhiều nhất
     */
    public List<Map<String, Object>> khachHangThanThiet(int limit) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String sql = "SELECT kh.ho_ten, kh.sdt, COUNT(*) as so_lan_choi, SUM(hdb.tong_tien) as tong_chi_tieu, kh.diem_tich_luy " +
                     "FROM hoa_don_ban hdb " +
                     "JOIN khach_hang kh ON hdb.ma_kh = kh.ma_kh " +
                     "GROUP BY kh.ma_kh, kh.ho_ten, kh.sdt, kh.diem_tich_luy " +
                     "ORDER BY so_lan_choi DESC " +
                     "LIMIT ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("ho_ten", rs.getString("ho_ten"));
                row.put("sdt", rs.getString("sdt"));
                row.put("so_lan_choi", rs.getInt("so_lan_choi"));
                row.put("tong_chi_tieu", rs.getDouble("tong_chi_tieu"));
                row.put("diem_tich_luy", rs.getInt("diem_tich_luy"));
                ketQua.add(row);
            }
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return ketQua;
    }
}