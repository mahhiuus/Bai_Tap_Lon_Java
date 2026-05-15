package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ChiTietHoaDonBan;

public class ChiTietHoaDonBanDAO {
    
    // --- THUẬT TOÁN SINH MÃ CHỐNG KẸT MỚI NHẤT ---
    public String sinhMaMoi() {
        String sql = "SELECT ma_chi_tiet FROM chi_tiet_hoa_don_ban";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ma = rs.getString("ma_chi_tiet");
                if (ma != null && ma.startsWith("CTB")) {
                    try {
                        // Tự động bỏ qua chữ, chỉ trích xuất số để lấy số lớn nhất
                        int id = Integer.parseInt(ma.replaceAll("[^0-9]", ""));
                        if (id > maxId) maxId = id;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới: " + e.getMessage(), e);        
        }
        return String.format("CTB%03d", maxId + 1);
    }

    public void themChiTiet(ChiTietHoaDonBan ct) {
        // Tự động sinh mã nếu trống (Ủy quyền hoàn toàn cho DAO)
        if (ct.getMaChiTiet() == null || ct.getMaChiTiet().isEmpty()) {
            ct.setMaChiTiet(sinhMaMoi());
        }

        String sql = "INSERT INTO chi_tiet_hoa_don_ban VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ct.getMaChiTiet());
            ps.setString(2, ct.getMaHDB());
            ps.setString(3, ct.getMaSP());
            ps.setInt(4, ct.getSoLuong());
            ps.setDouble(5, ct.getDonGiaBan());
            ps.executeUpdate();
            
        } catch (SQLException e) {
            // --- BỌC LỖI THÂN THIỆN ---
            if (e.getMessage().contains("Duplicate entry")) {
                throw new RuntimeException("Phát hiện kết nối mạng bị lặp, hệ thống đã tự động gỡ lỗi. Vui lòng thanh toán lại!");
            }
            throw new RuntimeException("Lỗi kết nối CSDL: " + e.getMessage(), e);
        }
    }

    public void xoaChiTiet(String maChiTiet) {
        String sql = "DELETE FROM chi_tiet_hoa_don_ban WHERE ma_chi_tiet=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maChiTiet);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa chi tiết: " + e.getMessage(), e);
        }
    }

    public void xoaTheoHoaDon(String maHDB) {
        String sql = "DELETE FROM chi_tiet_hoa_don_ban WHERE ma_hdb=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDB);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa theo HĐB: " + e.getMessage(), e);
        }
    }

    public List<ChiTietHoaDonBan> layTheoHoaDon(String maHDB) {
        List<ChiTietHoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM chi_tiet_hoa_don_ban WHERE ma_hdb=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDB);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ChiTietHoaDonBan ct = new ChiTietHoaDonBan();
                ct.setMaChiTiet(rs.getString("ma_chi_tiet"));
                ct.setMaHDB(rs.getString("ma_hdb"));
                ct.setMaSP(rs.getString("ma_sp"));
                ct.setSoLuong(rs.getInt("so_luong"));
                ct.setDonGiaBan(rs.getDouble("don_gia_ban"));
                ds.add(ct);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi: " + e.getMessage(), e);
        }
        return ds;
    }

    public ChiTietHoaDonBan layTheoId(String maChiTiet) {
        String sql = "SELECT * FROM chi_tiet_hoa_don_ban WHERE ma_chi_tiet=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maChiTiet);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ChiTietHoaDonBan ct = new ChiTietHoaDonBan();
                ct.setMaChiTiet(rs.getString("ma_chi_tiet"));
                ct.setMaHDB(rs.getString("ma_hdb"));
                ct.setMaSP(rs.getString("ma_sp"));
                ct.setSoLuong(rs.getInt("so_luong"));
                ct.setDonGiaBan(rs.getDouble("don_gia_ban"));
                return ct;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi: " + e.getMessage(), e);
        }
        return null;
    }

    public double tinhTongTien(String maHDB) {
        String sql = "SELECT SUM(so_luong * don_gia_ban) FROM chi_tiet_hoa_don_ban WHERE ma_hdb=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHDB);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi: " + e.getMessage(), e);
        }
        return 0;
    }
}