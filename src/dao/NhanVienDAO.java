package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.NhanVien;

public class NhanVienDAO {
    //Phương thức tạo bảng nhân viên trong mysql
    public void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS nhan_vien (
                ma_nv       VARCHAR(10) PRIMARY KEY,
                ho_ten      VARCHAR(100) NOT NULL,
                sdt         VARCHAR(15),
                gioi_tinh   VARCHAR(10),
                chuc_vu     VARCHAR(50),
                ngay_sinh   DATE
            )
        """;
        try(Statement stmt = DBConnection.getConnection().createStatement()) {
            stmt.execute(sql);
            System.out.println("Tạo bảng nhân viên thành công!");
        }
        catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tạo bảng nhân viên " + e.getMessage(), e);
        }
    }

    //Phương thức thêm nhân viên vào bảng nhan_vien trong mysql
    public void themNhanVien(NhanVien nv) {
        if (nv == null || nv.getMaNV() == null || nv.getTenNV() == null) {
            throw new IllegalArgumentException("Nhân viên hoặc các trường bắt buộc không được để trống");
        }
        String sql = "INSERT INTO nhan_vien (ma_nv, ho_ten, sdt, gioi_tinh, chuc_vu, ngay_sinh) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getTenNV());
            stmt.setString(3, nv.getSoDienThoai());
            stmt.setString(4, nv.getGioiTinh());
            stmt.setString(5, nv.getChucVu());
            if (nv.getNgaySinh() != null) {
                stmt.setDate(6, Date.valueOf(nv.getNgaySinh()));
            } else {
                stmt.setNull(6, Types.DATE);
            }
            stmt.executeUpdate();

            System.out.println("Thêm nhân viên thành công!");
        } 
        catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm nhân viên: " + e.getMessage(), e);
        }
    }

    //Phương thức xóa nhân viên bằng mã nhân viên trong mysql
    public void xoaNhanVien(String maNV) {
        if(maNV == null || maNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống!");
        }
        String sql = "DELETE FROM nhan_vien WHERE ma_nv = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            stmt.executeUpdate();

            System.out.println("Xóa nhân viên thành công!");
        } 
        catch(Exception e) {
            throw new RuntimeException("Lỗi khi xóa nhân viên " + e.getMessage(), e);
        }
    }

    //Phương thức cập nhật nhân viên bằng mã nhân viên trong mysql
    public void capNhatNhanVien(NhanVien nv) {
        if(nv == null || nv.getMaNV() == null || nv.getTenNV() == null) {
            throw new IllegalArgumentException("Nhân viên hoặc các trường bắt buộc không được để trống");
        }
        String sql = "UPDATE nhan_vien SET ho_ten = ?, sdt = ?, gioi_tinh = ?, chuc_vu = ?, ngay_sinh = ? WHERE ma_nv = ?";

        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getTenNV());
            stmt.setString(2, nv.getSoDienThoai());
            stmt.setString(3, nv.getGioiTinh());
            stmt.setString(4, nv.getChucVu());
            if (nv.getNgaySinh() != null) {
                stmt.setDate(5, Date.valueOf(nv.getNgaySinh()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            stmt.setString(6, nv.getMaNV());
            stmt.executeUpdate();

            System.out.println("Cập nhật nhân viên thành công!");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật nhân viên " + e.getMessage(), e);
        }
    }
    
    //Phương thức lấy tất cả nhân viên
    public List<NhanVien> layTatCaNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhan_vien";

        try(Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("ma_nv"));
                nv.setTenNV(rs.getString("ho_ten"));
                nv.setSoDienThoai(rs.getString("sdt"));
                nv.setGioiTinh(rs.getString("gioi_tinh"));
                nv.setChucVu(rs.getString("chuc_vu"));
                nv.setNgaySinh(rs.getDate("ngay_sinh") != null ? rs.getDate("ngay_sinh").toLocalDate() : null);
                list.add(nv);
            }
        } 
        catch(SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách nhân viên: " + e.getMessage(), e);
        }
        return list;
    }

    //Phương thức tìm nhân viên theo mã nhân viên
    public NhanVien timTheoMaNhanVien(String maNV) {
        if(maNV == null || maNV.trim().isEmpty()) {
            throw new RuntimeException("Mã nhân viên không được để trống!");
        }
        String sql = "SELECT * FROM nhan_vien WHERE ma_nv = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   return new NhanVien(
                    rs.getString("ma_nv"),
                    rs.getString("ho_ten"),
                    rs.getString("sdt"),
                    rs.getString("gioi_tinh"),
                    rs.getString("chuc_vu"),
                    rs.getDate("ngay_sinh") != null ? rs.getDate("ngay_sinh").toLocalDate() : null
                   );
                }
            }
        } 
        catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm theo mã nhân viên " + e.getMessage(), e);
        }
        return null;
    }

    public List<NhanVien> timKiem(String keyword) {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM nhan_vien WHERE ma_nv LIKE ? OR ho_ten LIKE ?";

        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + (keyword == null ? "" : keyword) + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("ma_nv"));
                    nv.setTenNV(rs.getString("ho_ten"));
                    nv.setSoDienThoai(rs.getString("sdt"));
                    nv.setGioiTinh(rs.getString("gioi_tinh"));
                    nv.setChucVu(rs.getString("chuc_vu"));
                    nv.setNgaySinh(rs.getDate("ngay_sinh") != null ? rs.getDate("ngay_sinh").toLocalDate() : null);
                    list.add(nv);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm nhân viên: " + e.getMessage(), e);
        }
        return list;
    }
}