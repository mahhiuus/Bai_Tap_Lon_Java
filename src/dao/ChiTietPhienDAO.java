package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.ChiTietPhien;

public class ChiTietPhienDAO {
    // Phương thức thêm chi tiết phiên
    public void themChiTietPhien(ChiTietPhien ct) {
        if (ct == null || ct.getMaChiTiet() == null || ct.getMaChiTiet().trim().isEmpty()
                || ct.getMaPhien() == null || ct.getMaPhien().trim().isEmpty()
                || ct.getMaSanPham() == null || ct.getMaSanPham().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin chi tiết phiên không hợp lệ!");
        }

        if (ct.getSoLuong() <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0!");
        }

        if (ct.getDonGia() < 0) {
            throw new IllegalArgumentException("Đơn giá không được nhỏ hơn 0!");
        }

        String sql = """
            INSERT INTO chi_tiet_phien 
            (ma_chi_tiet, ma_phien, ma_san_pham, so_luong, don_gia)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ct.getMaChiTiet());
            stmt.setString(2, ct.getMaPhien());
            stmt.setString(3, ct.getMaSanPham());
            stmt.setInt(4, ct.getSoLuong());
            stmt.setDouble(5, ct.getDonGia());

            stmt.executeUpdate();
            System.out.println("Thêm chi tiết phiên thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm chi tiết phiên: " + e.getMessage(), e);
        }
    }

    // Phương thức cập nhật chi tiết phiên
    public void capNhatChiTietPhien(ChiTietPhien ct) {
        if (ct == null || ct.getMaChiTiet() == null || ct.getMaChiTiet().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chi tiết không được để trống!");
        }

        String sql = """
            UPDATE chi_tiet_phien
            SET ma_phien = ?, ma_san_pham = ?, so_luong = ?, don_gia = ?
            WHERE ma_chi_tiet = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ct.getMaPhien());
            stmt.setString(2, ct.getMaSanPham());
            stmt.setInt(3, ct.getSoLuong());
            stmt.setDouble(4, ct.getDonGia());
            stmt.setString(5, ct.getMaChiTiet());

            stmt.executeUpdate();
            System.out.println("Cập nhật chi tiết phiên thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật chi tiết phiên: " + e.getMessage(), e);
        }
    }

    // Phương thức xóa chi tiết phiên
    public void xoaChiTietPhien(String maChiTiet) {
        if (maChiTiet == null || maChiTiet.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chi tiết không được để trống!");
        }

        String sql = "DELETE FROM chi_tiet_phien WHERE ma_chi_tiet = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maChiTiet);
            stmt.executeUpdate();

            System.out.println("Xóa chi tiết phiên thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa chi tiết phiên: " + e.getMessage(), e);
        }
    }

    // Phương thức lấy tất cả chi tiết phiên
    public List<ChiTietPhien> layTatCaChiTietPhien() {
        List<ChiTietPhien> list = new ArrayList<>();

        String sql = "SELECT * FROM chi_tiet_phien";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ChiTietPhien ct = taoChiTietPhienTuResultSet(rs);
                list.add(ct);
                System.out.println(ct);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách chi tiết phiên: " + e.getMessage(), e);
        }

        return list;
    }

    // Phương thức tìm chi tiết phiên theo mã phiên
    public List<ChiTietPhien> timTheoMaPhien(String maPhien) {
        List<ChiTietPhien> list = new ArrayList<>();

        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        String sql = "SELECT * FROM chi_tiet_phien WHERE ma_phien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maPhien);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhien ct = taoChiTietPhienTuResultSet(rs);
                    list.add(ct);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm chi tiết phiên theo mã phiên: " + e.getMessage(), e);
        }

        return list;
    }

    // Phương thức tính tổng tiền sản phẩm trong một phiên
    public double tinhTongTienTheoPhien(String maPhien) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        String sql = """
            SELECT COALESCE(SUM(so_luong * don_gia), 0) AS tong_tien
            FROM chi_tiet_phien
            WHERE ma_phien = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maPhien);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("tong_tien");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tính tổng tiền theo phiên: " + e.getMessage(), e);
        }

        return 0;
    }

    // Hàm hỗ trợ tạo đối tượng ChiTietPhien từ ResultSet
    private ChiTietPhien taoChiTietPhienTuResultSet(ResultSet rs) throws SQLException {
        ChiTietPhien ct = new ChiTietPhien();

        ct.setMaChiTiet(rs.getString("ma_chi_tiet"));
        ct.setMaPhien(rs.getString("ma_phien"));
        ct.setMaSanPham(rs.getString("ma_san_pham"));
        ct.setSoLuong(rs.getInt("so_luong"));
        ct.setDonGia(rs.getDouble("don_gia"));

        return ct;
    }
}