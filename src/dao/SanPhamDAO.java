package dao;

import model.SanPham;
import java.sql.*;
import java.util.ArrayList; 
import java.util.List;

public class SanPhamDAO {
    // Chức năng Thêm sản phẩm
    public void themSanPham(SanPham sp) {
        if (sp == null || sp.getMaSP() == null ) {
            throw new IllegalArgumentException("Mã sản phẩm  không được để trống!");
        }
        String sql = "INSERT INTO san_pham (ma_sp, ten_sp, loai, gia_ban, so_luong_ton, ma_ncc) VALUES (?, ?, ?, ?, ?, ?)";
    
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
        
            stmt.setString(1, sp.getMaSP()); 
            stmt.setString(2, sp.getTenSP());
            stmt.setString(3, sp.getLoaiSP());
            stmt.setDouble(4, sp.getGiaBan());
            stmt.setInt(5, sp.getSoLuongTon());
            stmt.setString(6, sp.getMaNCC());
        
            stmt.executeUpdate();
            System.out.println("Thêm sản phẩm " + sp.getMaSP() + " thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm sản phẩm: " + e.getMessage(), e);
        }
    }

    //Chức năng XOA sản phẩm
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

    //Chuc nang CAP NHAT san pham
    public void capNhatSanPham(SanPham sp){
        if(sp == null || sp.getMaSP() == null ){
            throw new IllegalArgumentException("San pham hoac ma san pham khong duoc de trong!");
        }
        String sql = "UPDATE san_pham SET ten_sp = ?, loai = ?, gia_ban = ?, so_luong_ton = ?, ma_ncc = ? WHERE ma_sp = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, sp.getTenSP());
                stmt.setString(2, sp.getLoaiSP());
                stmt.setDouble(3, sp.getGiaBan());
                stmt.setInt(4, sp.getSoLuongTon());
                stmt.setString(5, sp.getMaNCC());
                stmt.setString(6, sp.getMaSP());

                stmt.executeUpdate();
                System.out.println("Cap nhat san pham thanh cong!");
            }catch(SQLException e){
                throw new RuntimeException("Loi khi cap nhat san pham" + e.getMessage(), e);
            }
    }

    public List<SanPham> timKiemTheoTen(String ten) {
        List<SanPham> ds = new ArrayList<>();
        // Câu lệnh SQL với toán tử LIKE để tìm kiếm gần đúng
        String sql = "SELECT * FROM san_pham WHERE ten_sp LIKE ?";
    
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
        
            //  %ten% là chứa chuỗi đó ở bất kỳ vị trí nào
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
                ds.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                ds.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    //Lay tat ca san pham
    public List<SanPham> getAllSanPham(){
        List<SanPham> List = new ArrayList<>();
        String sql = "SELECT * FROM san_pham";

        try ( Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setMaSP(rs.getString("ma_sp")); // Lấy dữ liệu dạng String
                    sp.setTenSP(rs.getString("ten_sp"));
                    sp.setLoaiSP(rs.getString("loai"));
                    sp.setGiaBan(rs.getDouble("gia_ban"));
                    sp.setSoLuongTon(rs.getInt("so_luong_ton"));
                    sp.setMaNCC(rs.getString("ma_ncc"));
                    List.add(sp);
                }
        }
            catch (SQLException e) {
                throw new RuntimeException("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage(), e);
            }
            return List;
    }

    // public static void main(String[] args) {
        // SanPhamDAO dao = new SanPhamDAO();

        //tao bang san pham
        // dao.createTable();

        // //khoi tao doi tuong san pham
        // SanPham sp1 = new SanPham("Sp01", "Coca", "DO_UONG", 15000, 50, 1);
        // SanPham sp2 = new SanPham("SP02", "Bim bim", "DO_AN", 5000, 35, 2);
        // SanPham sp3 = new SanPham("SP03", "Gay VIP", "DUNG_CU", 79000, 6, 3  );

        // //them san pham
        // dao.themSanPham(sp1);
        // dao.themSanPham(sp2);
        // dao.themSanPham(sp3);

        // //xoa san pham
        // dao.xoaSanPham("SP01");

        // //cap nhat san pham
        // sp2.settenSP("Bim Bim 5k");
        // dao.capNhatSanPham(sp2);

        // // tim kiem
        // System.out.println("--- Kết quả tìm kiếm tên có chữ 'bim' ---");
        // List<SanPham> tim = dao.timKiemTheoTen("bim");
        // for (SanPham sp : tim) {
        //     System.out.println("Ma SP: " + sp.getmaSP() + "| Ten SP: "+sp.gettenSP()+" | Gia ban: "+sp.getgiaBan()+ " | So luong ton: "+sp.getsoLuongTon()+" | Ma NCC: "+ sp.getmaNCC() );
        // }
        // System.out.println("--- Kết quả tìm kiếm mã sp SP02 ---");
        // List<SanPham> tim = dao.timKiemTheoMa("SP02");
        // for (SanPham sp : tim) {
        //     System.out.println("Ma SP: " + sp.getmaSP() + "| Ten SP: "+sp.gettenSP()+" | Gia ban: "+sp.getgiaBan()+ " | So luong ton: "+sp.getsoLuongTon()+" | Ma NCC: "+ sp.getmaNCC() );
        // }

        // //in danh sach san pham
        // System.out.println("Danh sach san pham: ");
        // List<SanPham> ds = dao.getAllSanPham();
        // for (SanPham sp : ds){
        //     System.out.println("Ma SP: " + sp.getmaSP() + "| Ten SP: "+sp.gettenSP()+" | Gia ban: "+sp.getgiaBan()+ " | So luong ton: "+sp.getsoLuongTon()+" | Ma NCC: "+ sp.getmaNCC() );
        // }
       
    // }
}