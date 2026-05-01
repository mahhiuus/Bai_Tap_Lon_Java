package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.KhachHang;

public class KhachHangDAO {
    public String sinhMaMoi() {
        String sql = "SELECT ma_kh FROM khach_hang ORDER BY ma_kh DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_kh");
                int soThuTu = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("KH%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới khách hàng: " + e.getMessage(), e);
        }
        return "KH01";
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
                System.out.println(kh);
            }
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
        return list;
    }

    public KhachHang timTheoMaKhachHang(String maKH) {
        if (maKH == null || maKH.trim().isEmpty()) {
            throw new RuntimeException("Mã khách hàng không được để trống!");
        }
        String sql = "SELECT * FROM khach_hang WHERE ma_kh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getString("ma_kh"));
                    kh.setTenKH(rs.getString("ho_ten"));
                    kh.setSdt(rs.getString("sdt"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                    Date ngayDangKy = rs.getDate("ngay_dang_ky");
                    kh.setNgayDangKy(ngayDangKy != null ? ngayDangKy.toLocalDate() : null);
                    return kh;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm theo mã khách hàng: " + e.getMessage(), e);
        }
        return null;
    }

    public List<KhachHang> timKiem(String keyword) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM khach_hang WHERE ma_kh LIKE ? OR ho_ten LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + (keyword == null ? "" : keyword) + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKH(rs.getString("ma_kh"));
                    kh.setTenKH(rs.getString("ho_ten"));
                    kh.setSdt(rs.getString("sdt"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    kh.setDiemTichLuy(rs.getInt("diem_tich_luy"));
                    Date ngayDangKy = rs.getDate("ngay_dang_ky");
                    kh.setNgayDangKy(ngayDangKy != null ? ngayDangKy.toLocalDate() : null);
                    list.add(kh);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), e);
        }
        return list;
    }
    public static void main(String[] args) {
        KhachHangDAO db = new KhachHangDAO();
        db.getAllKhachHang();
        NhanVienDAO dao = new NhanVienDAO();
        dao.layTatCaNhanVien();
        SanPhamDAO sp = new SanPhamDAO();
        sp.getAllSanPham();
        NhaCungCapDAO ncc = new NhaCungCapDAO();
        ncc.getAllNhaCungCap();
        TaiKhoanDAO tk = new TaiKhoanDAO();
        tk.layTatCaTaiKhoan();
        PhienChoiDAO pc = new PhienChoiDAO();
        pc.layTatCaPhien();
        ChiTietPhienDAO ctp = new ChiTietPhienDAO();
        ctp.layTatCaChiTietPhien();
        BanBidaDAO bd = new BanBidaDAO();
        bd.layTatCaBan();
    }
}