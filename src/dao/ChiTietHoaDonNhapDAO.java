package dao;

import model.ChiTietHoaDonNhap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonNhapDAO {

    // --- THUẬT TOÁN SINH MÃ THÔNG MINH (CHỐNG KẸT) ---
    public String sinhMaMoi() {
        String sql = "SELECT ma_chi_tiet FROM chi_tiet_hoa_don_nhap";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ma = rs.getString("ma_chi_tiet");
                if (ma != null && ma.startsWith("CTN")) {
                    try {
                        // Tự động bỏ qua chữ, chỉ lấy số để tìm số lớn nhất
                        int id = Integer.parseInt(ma.replaceAll("[^0-9]", ""));
                        if (id > maxId) maxId = id;
                    } catch (Exception ignored) {}
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới: " + e.getMessage(), e);
        }
        return String.format("CTN%03d", maxId + 1); // Trả về CTN001, CTN002...
    }

    public void themChiTiet(ChiTietHoaDonNhap ct) {
        // Tự động cấp mã nếu chưa có
        if (ct.getMaChiTiet() == null || ct.getMaChiTiet().trim().isEmpty()) {
            ct.setMaChiTiet(sinhMaMoi());
        }
        String sql = "INSERT INTO chi_tiet_hoa_don_nhap (ma_chi_tiet, ma_hdn, ma_sp, so_luong, don_gia_nhap) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ct.getMaChiTiet());
            stmt.setString(2, ct.getMaHDN());
            stmt.setString(3, ct.getMaSP());
            stmt.setInt(4, ct.getSoLuong());
            stmt.setDouble(5, ct.getDonGiaNhap());
            stmt.executeUpdate();
            
            // --- TỰ ĐỘNG CỘNG TỒN KHO THẬT ---
            new SanPhamDAO().tangTonKho(ct.getMaSP(), ct.getSoLuong());
            // --- TỰ ĐỘNG CỘNG DỒN TỔNG TIỀN CHO PHIẾU ---
            capNhatTongTienHoaDon(ct.getMaHDN());

        } catch (SQLException e) { 
            throw new RuntimeException("Lỗi SQL: " + e.getMessage(), e); 
        }
    }

    public void xoaChiTiet(String maCT) {
        String maHDN = ""; String maSP = ""; int soLuong = 0;
        String getSql = "SELECT ma_hdn, ma_sp, so_luong FROM chi_tiet_hoa_don_nhap WHERE ma_chi_tiet = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getSql)) {
            stmt.setString(1, maCT);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                maHDN = rs.getString("ma_hdn");
                maSP = rs.getString("ma_sp");
                soLuong = rs.getInt("so_luong");
            }
        } catch(Exception e){}
        
        String sql = "DELETE FROM chi_tiet_hoa_don_nhap WHERE ma_chi_tiet = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maCT);
            stmt.executeUpdate();
            
            // --- THU HỒI LẠI SỐ LƯỢNG KHI XÓA SAI ---
            new SanPhamDAO().giamTonKho(maSP, soLuong); 
            // --- CẬP NHẬT LẠI TỔNG TIỀN PHIẾU ---
            if (!maHDN.isEmpty()) capNhatTongTienHoaDon(maHDN);
            
        } catch (SQLException e) { throw new RuntimeException("Lỗi khi xóa chi tiết: " + e.getMessage(), e); }
    }
    
    private void capNhatTongTienHoaDon(String maHDN) {
        String sql = "UPDATE hoa_don_nhap SET tong_tien = (SELECT COALESCE(SUM(so_luong * don_gia_nhap), 0) FROM chi_tiet_hoa_don_nhap WHERE ma_hdn = ?) WHERE ma_hdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDN);
            stmt.setString(2, maHDN);
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<ChiTietHoaDonNhap> getChiTietTheoMaHDN(String maHDN) {
        List<ChiTietHoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM chi_tiet_hoa_don_nhap WHERE ma_hdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHDN);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ChiTietHoaDonNhap ct = new ChiTietHoaDonNhap();
                ct.setMaChiTiet(rs.getString("ma_chi_tiet"));
                ct.setMaHDN(rs.getString("ma_hdn"));
                ct.setMaSP(rs.getString("ma_sp"));
                ct.setSoLuong(rs.getInt("so_luong"));
                ct.setDonGiaNhap(rs.getDouble("don_gia_nhap"));
                list.add(ct);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return list;
    }
}