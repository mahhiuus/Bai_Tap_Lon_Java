package dao;

import model.SanPham;
import java.sql.*;
import java.util.ArrayList; 
import java.util.List;

public class SanPhamDAO {
    public String sinhMaMoi() {
        String sql = "SELECT ma_sp FROM san_pham ORDER BY ma_sp DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_sp");
                int soThuTu = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("SP%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới sản phẩm: " + e.getMessage(), e);
        }
        return "SP01";
    }

    public void themSanPham(SanPham sp) {
        if (sp == null || sp.getMaSP() == null ) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        }
        String sql = "INSERT INTO san_pham (ma_sp, ten_sp, loai, gia_ban, so_luong_ton, ma_ncc, hinh_anh) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        
            stmt.setString(1, sp.getMaSP()); 
            stmt.setString(2, sp.getTenSP());
            stmt.setString(3, sp.getLoaiSP());
            stmt.setDouble(4, sp.getGiaBan());
            stmt.setInt(5, sp.getSoLuongTon());
            stmt.setString(6, sp.getMaNCC());
            stmt.setString(7, sp.getHinhAnh()); 
        
            stmt.executeUpdate();
            System.out.println("Thêm sản phẩm " + sp.getMaSP() + " thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm: " + e.getMessage(), e);
        }
    }

    public void xoaSanPham(String maSP){
        if( maSP == null || maSP.trim().isEmpty()){
            throw new IllegalArgumentException("Ban chua nhap ma san pham can xoa");
        }
        String sql = "DELETE FROM san_pham WHERE ma_sp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1,maSP);
                stmt.executeUpdate();
                System.out.println("XOA san pham thanh cong!");
        }catch (SQLException e){
            throw new RuntimeException("Loi khi xoa san pham" + e.getMessage(),e);
        }
    }

    public void capNhatSanPham(SanPham sp){
        if(sp == null || sp.getMaSP() == null ){
            throw new IllegalArgumentException("San pham hoac ma san pham khong duoc de trong!");
        }
        String sql = "UPDATE san_pham SET ten_sp = ?, loai = ?, gia_ban = ?, so_luong_ton = ?, ma_ncc = ?, hinh_anh = ? WHERE ma_sp = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, sp.getTenSP());
                stmt.setString(2, sp.getLoaiSP());
                stmt.setDouble(3, sp.getGiaBan());
                stmt.setInt(4, sp.getSoLuongTon());
                stmt.setString(5, sp.getMaNCC());
                stmt.setString(6, sp.getHinhAnh());
                stmt.setString(7, sp.getMaSP());

                stmt.executeUpdate();
                System.out.println("Cap nhat san pham thanh cong!");
            }catch(SQLException e){
                throw new RuntimeException("Loi khi cap nhat san pham" + e.getMessage(), e);
            }
    }

    public List<SanPham> timKiemTheoTen(String ten) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM san_pham WHERE ten_sp LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + ten + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setLoaiSP(rs.getString("loai"));
                sp.setGiaBan(rs.getDouble("gia_ban"));
                sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                sp.setMaNCC(rs.getString("ma_ncc"));
                sp.setHinhAnh(rs.getString("hinh_anh"));
                ds.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public List<SanPham> timKiemTheoMa(String ma) {
        List<SanPham> ds = new ArrayList<>();
        String sql = "SELECT * FROM san_pham WHERE ma_sp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma); 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setLoaiSP(rs.getString("loai"));
                sp.setGiaBan(rs.getDouble("gia_ban"));
                sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                sp.setMaNCC(rs.getString("ma_ncc"));
                sp.setHinhAnh(rs.getString("hinh_anh"));
                ds.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public SanPham layTheoId(String ma) {
        if (ma == null || ma.trim().isEmpty()) return null;
        String sql = "SELECT * FROM san_pham WHERE ma_sp = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ma);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSP(rs.getString("ma_sp"));
                sp.setTenSP(rs.getString("ten_sp"));
                sp.setLoaiSP(rs.getString("loai"));
                sp.setGiaBan(rs.getDouble("gia_ban"));
                sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                sp.setMaNCC(rs.getString("ma_ncc"));
                sp.setHinhAnh(rs.getString("hinh_anh"));
                return sp;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<SanPham> getAllSanPham(){
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM san_pham";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp")); 
                    sp.setTenSP(rs.getString("ten_sp"));
                    sp.setLoaiSP(rs.getString("loai"));
                    sp.setGiaBan(rs.getDouble("gia_ban"));
                    sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                    sp.setMaNCC(rs.getString("ma_ncc"));
                    sp.setHinhAnh(rs.getString("hinh_anh"));
                    list.add(sp);
                }
        }catch (SQLException e) {
           throw new RuntimeException("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage(), e);
        }
        return list;
    }

    public void tangTonKho(String maSP, int soLuong) {
        String sql = "UPDATE san_pham SET so_luong_ton = so_luong_ton + ? WHERE ma_sp=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, soLuong); ps.setString(2, maSP); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void giamTonKho(String maSP, int soLuong) {
        String sql = "UPDATE san_pham SET so_luong_ton = so_luong_ton - ? WHERE ma_sp=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, soLuong); ps.setString(2, maSP); ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- MỚI THÊM: Tính giá nhập trung bình tự động ---
    public double tinhGiaNhapTrungBinh(String maSP) {
        String sql = "SELECT SUM(so_luong * don_gia_nhap) / SUM(so_luong) FROM chi_tiet_hoa_don_nhap WHERE ma_sp=?";
        try(PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)){
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) return rs.getDouble(1);
        }catch(Exception e){}
        return 0;
    }
}