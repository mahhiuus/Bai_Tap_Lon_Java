package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.KhachHang;

public class KhachHangDAO {

    public void createTable (){
        String sql = """
                CREATE TABLE IF NOT EXISTS khach_hang (
                ma_kh VARCHAR(10) PRIMARY KEY,
                ho_ten VARCHAR(50) NOT NULL,
                sdt VARCHAR(10),
                dia_chi VARCHAR(100),
                diem_tich_luy INT,
                ngay_dang_ky DATE
                )
                """;
        try(Statement stmt = DBConnection.getConnection().createStatement()) {
            stmt.execute(sql);
            System.out.println("Tạo bảng khách hàng thành công!");
        }
        catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo bảng khách hàng ", e);
        }
    }

    public void themKhachHang(KhachHang kh) {
        if (kh == null || kh.getMaKH() == null) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ");
        }
        String sql = "INSERT INTO khach_hang (ma_kh, ho_ten, sdt, dia_chi, diem_tich_luy, ngay_dang_ky) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKH()); 
            stmt.setString(2, kh.getTenKH());
            stmt.setString(3, kh.getSdt());
            stmt.setString(4, kh.getDiaChi());
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setDate(6, Date.valueOf(kh.getNgayDangKy()));

            stmt.executeUpdate();
            System.out.println("Thêm khách hàng thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm khách hàng: " + e.getMessage(), e);
        }
    }
    
    public void xoaKhachHang(String maKH){
        String sql = "DELETE FROM khach_hang WHERE ma_kh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH); 
            stmt.executeUpdate();
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
    }
    }

    public void capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE khach_hang SET ho_ten = ?, sdt = ?, dia_chi = ?, diem_tich_luy = ?, ngay_dang_ky = ? WHERE ma_kh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kh.getTenKH());
            stmt.setString(2, kh.getSdt());
            stmt.setString(3, kh.getDiaChi());
            stmt.setInt(4, kh.getDiemTichLuy());
            stmt.setDate(5, Date.valueOf(kh.getNgayDangKy()));
            stmt.setString(6, kh.getMaKH()); 
            stmt.executeUpdate();
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
    }

    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khach_hang";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKH(rs.getString("ma_kh")); 
                kh.setTenKH(rs.getString("ho_ten"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("dia_chi"));
                kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                kh.setNgayDangKy(rs.getDate("ngay_dang_ky").toLocalDate());
                list.add(kh);
            }
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
        return list;
    }
    // public static void main(String[] args) {
    //     KhachHangDAO db = new KhachHangDAO();
    //     db.createTable();
    // }
}