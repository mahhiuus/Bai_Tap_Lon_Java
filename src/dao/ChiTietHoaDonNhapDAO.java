package dao;

import model.ChiTietHoaDonNhap;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonNhapDAO {

    // Chức năng sinh mã chi tiết mới (Ví dụ: CTN01, CTN02...)
    public String sinhMaMoi() {
        String sql = "SELECT ma_chi_tiet FROM chi_tiet_hoa_don_nhap ORDER BY ma_chi_tiet DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_chi_tiet");
                // Cắt chuỗi "CTN" và lấy số thứ tự phía sau
                int soThuTu = Integer.parseInt(maCuoi.substring(3)) + 1;
                return String.format("CTN%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới chi tiết hóa đơn nhập: " + e.getMessage(), e);
        }
        return "CTN01";
    }

    // THÊM chi tiết hóa đơn nhập
    public void themChiTiet(ChiTietHoaDonNhap ct) {
        if (ct == null || ct.getMaChiTiet() == null || ct.getMaChiTiet().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chi tiết không được để trống!");
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
            System.out.println("Thêm chi tiết " + ct.getMaChiTiet() + " thành công!");
            
            // Sau khi nhập hàng, số lượng tồn sẽ tăng
            SanPhamDAO spDAO = new SanPhamDAO();
            spDAO.tangTonKho(ct.getMaSP(), ct.getSoLuong());

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm chi tiết hóa đơn nhập: " + e.getMessage(), e);
        }
    }

    // Chức năng XÓA chi tiết hóa đơn
    public void xoaChiTiet(String maCT) {
        if (maCT == null || maCT.trim().isEmpty()) {
            throw new IllegalArgumentException("Bạn chưa nhập mã chi tiết cần xóa!");
        }
        String sql = "DELETE FROM chi_tiet_hoa_don_nhap WHERE ma_chi_tiet = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maCT);
            stmt.executeUpdate();
            System.out.println("Xóa chi tiết hóa đơn thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage(), e);
        }
    }

    // Lấy danh sách chi tiết theo MÃ HÓA ĐƠN (Để hiển thị lên bảng khi chọn 1 hóa đơn)
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
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách chi tiết theo mã HĐN: " + e.getMessage(), e);
        }
        return list;
    }
    
    // public static void main(String[] args) {
    //     ChiTietHoaDonNhapDAO dao = new ChiTietHoaDonNhapDAO();

        // 1. Test sinh mã tự động
        // String maMoi = dao.sinhMaMoi();
        // System.out.println("Mã chi tiết mới dự kiến: " + maMoi);

        // // 2. Test chức năng THÊM
        // ChiTietHoaDonNhap ctTest = new ChiTietHoaDonNhap(maMoi, "HDN99", "SP01", 10, 150000);
        //     dao.themChiTiet(ctTest);
        

        // // 3. LẤY DANH SÁCH theo mã hóa đơn
        // String maHDNCanTim = "HDN99";
        // System.out.println("\n--- Danh sách chi tiết của hóa đơn: " + maHDNCanTim + " ---");
        // List<ChiTietHoaDonNhap> ds = dao.getChiTietTheoMaHDN(maHDNCanTim);
        
        // if (ds.isEmpty()) {
        //     System.out.println("Không tìm thấy chi tiết nào cho mã hóa đơn này.");
        // } else {
        //     for (ChiTietHoaDonNhap item : ds) {
        //         System.out.println("Mã CT: " + item.getMaChiTiet() + 
        //                            " | SP: " + item.getMaSP() + 
        //                            " | SL: " + item.getSoLuong() + 
        //                            " | Giá: " + item.getDonGiaNhap());
        //     }
        // }

        // // 4. Test chức năng XÓA (Tùy chọn - Bỏ comment nếu muốn xóa thử)
        // dao.xoaChiTiet("CTN03");
    // }
    
}