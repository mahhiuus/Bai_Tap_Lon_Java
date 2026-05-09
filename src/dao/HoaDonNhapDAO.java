package dao;

import model.HoaDonNhap;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonNhapDAO {

    // Chức năng sinh mã hóa đơn mới (Ví dụ: HDN01, HDN02...)
    public String sinhMaMoi() {
        String sql = "SELECT ma_hdn FROM hoa_don_nhap ORDER BY ma_hdn DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_hdn");
                // Cắt chuỗi "HDN" và lấy số thứ tự phía sau
                int soThuTu = Integer.parseInt(maCuoi.substring(3)) + 1;
                return String.format("HDN%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới hóa đơn nhập: " + e.getMessage(), e);
        }
        return "HDN01";
    }

    // THÊM hóa đơn nhập
    public void themHoaDonNhap(HoaDonNhap hdn) {
        if (hdn == null || hdn.getMaHDN() == null || hdn.getMaHDN().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã hóa đơn không được để trống!");
        }
        String sql = "INSERT INTO hoa_don_nhap (ma_hdn, ma_ncc, ma_nv, ngay_nhap, tong_tien) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hdn.getMaHDN());
            stmt.setString(2, hdn.getMaNCC());
            stmt.setString(3, hdn.getMaNV());
            // Chuyển LocalDate sang java.sql.Date để khớp với kiểu DATE trong database
            stmt.setDate(4, Date.valueOf(hdn.getNgayNhap()));
            stmt.setDouble(5, hdn.getTongTien());

            stmt.executeUpdate();
            System.out.println("Thêm hóa đơn " + hdn.getMaHDN() + " thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm hóa đơn nhập: " + e.getMessage(), e);
        }
    }

    // XÓA hóa đơn nhập
    public void xoaHoaDonNhap(String maHDN) {
        if (maHDN == null || maHDN.trim().isEmpty()) {
            throw new IllegalArgumentException("Bạn chưa nhập mã hóa đơn nhập cần xóa!");
        }
        String sql = "DELETE FROM hoa_don_nhap WHERE ma_hdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDN);
            int kietQua = stmt.executeUpdate();
            if (kietQua > 0) {
                System.out.println("Xóa hóa đơn nhập thành công!");
            } else {
                System.out.println("Không tìm thấy mã hóa đơn để xóa.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa hóa đơn nhập: " + e.getMessage(), e);
        }
    }

    // CẬP NHẬT hóa đơn nhập
    public void capNhatHoaDonNhap(HoaDonNhap hdn) {
        if (hdn == null || hdn.getMaHDN() == null) {
            throw new IllegalArgumentException("Hóa đơn hoặc mã hóa đơn không được để trống!");
        }
        String sql = "UPDATE hoa_don_nhap SET ma_ncc = ?, ma_nv = ?, ngay_nhap = ?, tong_tien = ? WHERE ma_hdn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hdn.getMaNCC());
            stmt.setString(2, hdn.getMaNV());
            stmt.setDate(3, Date.valueOf(hdn.getNgayNhap()));
            stmt.setDouble(4, hdn.getTongTien());
            stmt.setString(5, hdn.getMaHDN());

            stmt.executeUpdate();
            System.out.println("Cập nhật hóa đơn nhập thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật hóa đơn nhập: " + e.getMessage(), e);
        }
    }

    // Lấy tất cả danh sách hóa đơn nhập
    public List<HoaDonNhap> getAllHoaDonNhap() {
        List<HoaDonNhap> list = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_nhap";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                HoaDonNhap hdn = new HoaDonNhap();
                hdn.setMaHDN(rs.getString("ma_hdn"));
                hdn.setMaNCC(rs.getString("ma_ncc"));
                hdn.setMaNV(rs.getString("ma_nv"));
                // Chuyển từ sql.Date về LocalDate của Java
                Date d = rs.getDate("ngay_nhap");
                if (d != null) {
                    hdn.setNgayNhap(d.toLocalDate());
                }
                hdn.setTongTien(rs.getDouble("tong_tien"));
                list.add(hdn);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách hóa đơn nhập: " + e.getMessage(), e);
        }
        return list;
    }

    // TÌM KIẾM hóa đơn theo mã
    public List<HoaDonNhap> timKiemTheoMa(String ma) {
        List<HoaDonNhap> ds = new ArrayList<>();
        String sql = "SELECT * FROM hoa_don_nhap WHERE ma_hdn LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + ma + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDonNhap hdn = new HoaDonNhap();
                hdn.setMaHDN(rs.getString("ma_hdn"));
                hdn.setMaNCC(rs.getString("ma_ncc"));
                hdn.setMaNV(rs.getString("ma_nv"));
                hdn.setNgayNhap(rs.getDate("ngay_nhap").toLocalDate());
                hdn.setTongTien(rs.getDouble("tong_tien"));
                ds.add(hdn);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage(), e);
        }
        return ds;
    }
    // public static void main(String[] args) {
    // HoaDonNhapDAO dao = new HoaDonNhapDAO();

    // // 1. Tạo dữ liệu mẫu để test
    // HoaDonNhap hdnTest = new HoaDonNhap("HDN99", "NCC01", "NV01", ...); // Thêm các tham số còn thiếu của constructor

    // // 2. Test chức năng Thêm
    // // dao.themHoaDonNhap(hdnTest);

    // // 3. Test chức năng Lấy tất cả danh sách
    // // testGetAll(dao);

    // // 4. Test chức năng Cập nhật
    // // hdnTest.setTongTien(888888);
    // // dao.capNhatHoaDonNhap(hdnTest);

    // // 5. Test chức năng Xóa
    // // dao.xoaHoaDonNhap("HDN99");

    // // 6.Test in danh sach
    //     List<HoaDonNhap> danhSach = dao.getAllHoaDonNhap();
    //     System.out.println("--- DANH SÁCH HÓA ĐƠN NHẬP ---");
    //     if (danhSach.isEmpty()) {
    //         System.out.println("Không có dữ liệu trong bảng hoa_don_nhap.");
    //     } else {
    //         for (HoaDonNhap hdn : danhSach) {
    //             System.out.println("Mã HD: " + hdn.getMaHDN() + 
    //                                " | NCC: " + hdn.getMaNCC() + 
    //                                " | Ngày: " + hdn.getNgayNhap() + 
    //                                " | Tổng tiền: " + hdn.getTongTien());
    //         }
    //     }
    // }
}
    
