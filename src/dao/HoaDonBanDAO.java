package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import model.HoaDonBan;
import model.PhienChoi;

public class HoaDonBanDAO {
    
    public String sinhMaMoi() {
        String sql = "SELECT ma_hdb FROM hoa_don_ban";
        int maxId = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String ma = rs.getString("ma_hdb");
                if (ma != null && ma.startsWith("HDB")) {
                    try {
                        int id = Integer.parseInt(ma.replaceAll("[^0-9]", ""));
                        if (id > maxId) {
                            maxId = id;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return String.format("HDB%03d", maxId + 1);
    }

    public HoaDonBan taoTuPhien(String maPhien, String maKH, String maNV, double tienBida) {
        PhienChoi pc = new PhienChoiDAO().timTheoMaPhien(maPhien);
        if(pc == null) {return null;}
        double tienSanPham = new ChiTietPhienDAO().tinhTongTienTheoPhien(maPhien);
        double tongTien = tienBida + tienSanPham;
        return new HoaDonBan(sinhMaMoi(), maPhien, maKH, maNV, LocalDate.now(), tienBida, tienSanPham, tongTien, "");
    }

    public void them(HoaDonBan hdb) {
        String sql = " INSERT INTO hoa_don_ban (ma_hdb, ma_phien, ma_kh, ma_nv, ngay_ban, tien_bida, tien_san_pham, tong_tien, ghi_chu) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hdb.getMaHDB());
            stmt.setString(2, hdb.getMaPhien());
            stmt.setString(3, hdb.getMaKH());
            stmt.setString(4, hdb.getMaNV());
            stmt.setDate(5, Date.valueOf(hdb.getNgayBan()));
            stmt.setDouble(6, hdb.getTienBida());
            stmt.setDouble(7, hdb.getTienSanPham());
            stmt.setDouble(8, hdb.getTongTien());
            stmt.setString(9, hdb.getGhiChu());
            stmt.executeUpdate();
            System.out.println("Thêm hoá đơn bán thành công! Mã: " + hdb.getMaHDB());
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm hóa đơn bán: " + e.getMessage(), e);
        }
    }

    // --- HÀM BỔ SUNG ĐỂ SỬA LỖI DUPLICATE ---
    public void capNhat(HoaDonBan hdb) {
        String sql = "UPDATE hoa_don_ban SET tien_bida=?, tien_san_pham=?, tong_tien=?, ma_kh=?, ma_nv=? WHERE ma_hdb=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDouble(1, hdb.getTienBida());
            ps.setDouble(2, hdb.getTienSanPham());
            ps.setDouble(3, hdb.getTongTien());
            ps.setString(4, hdb.getMaKH());
            ps.setString(5, hdb.getMaNV());
            ps.setString(6, hdb.getMaHDB());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public HoaDonBan layTheoMaPhien(String maPhien) {
        String sql = "SELECT * FROM hoa_don_ban WHERE ma_phien=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maPhien);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                return hdb;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    // ----------------------------------------

    public void xoaHoaDonBan(String maHDB) {
        String sql = "DELETE FROM hoa_don_ban WHERE ma_hdb = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDB);
            stmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
    }

    public List<HoaDonBan> getAllHoaDonBan() {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban ORDER BY ma_hdb DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if(d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            } 
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public HoaDonBan layTheoId(String maHDB) {
        String sql = "SELECT * FROM hoa_don_ban WHERE ma_hdb=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maHDB);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                return hdb;
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return null;
    }

    public List<HoaDonBan> layTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban WHERE ngay_ban BETWEEN ? AND ? ORDER BY ma_hdb DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(tuNgay));
            ps.setDate(2, Date.valueOf(denNgay));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public List<HoaDonBan> layTheoKhachHang(String maKH) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban WHERE ma_kh=? ORDER BY ma_hdb DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public List<HoaDonBan> layTheoNhanVien(String maNV) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban WHERE ma_nv=? ORDER BY ma_hdb DESC";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, maNV);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public List<HoaDonBan> layTopHoaDon(int limit) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban ORDER BY tong_tien DESC LIMIT ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public List<HoaDonBan> layTopHoaDonTheoNgay(LocalDate ngay, int limit) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban WHERE ngay_ban=? ORDER BY tong_tien DESC LIMIT ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ngay));
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }

    public List<HoaDonBan> layTopHoaDonTheoThang(int thang, int nam, int limit) {
        List<HoaDonBan> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_ban WHERE MONTH(ngay_ban) = ? AND YEAR(ngay_ban) = ? ORDER BY tong_tien DESC LIMIT ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, thang);
            ps.setInt(2, nam);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonBan hdb = new HoaDonBan();
                hdb.setMaHDB(rs.getString("ma_hdb"));
                hdb.setMaPhien(rs.getString("ma_phien"));
                hdb.setMaKH(rs.getString("ma_kh"));
                hdb.setMaNV(rs.getString("ma_nv"));
                Date d = rs.getDate("ngay_ban");
                if (d != null) hdb.setNgayBan(d.toLocalDate());
                hdb.setTienBida(rs.getDouble("tien_bida"));
                hdb.setTienSanPham(rs.getDouble("tien_san_pham"));
                hdb.setTongTien(rs.getDouble("tong_tien"));
                hdb.setGhiChu(rs.getString("ghi_chu"));
                ds.add(hdb);
            }
        } catch (SQLException e) { throw new RuntimeException("Lỗi: " + e.getMessage(), e); }
        return ds;
    }
}