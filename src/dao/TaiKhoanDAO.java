package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.TaiKhoan;

public class TaiKhoanDAO {
    public String sinhMaMoi() {
        String sql = "SELECT ma_tk FROM tai_khoan ORDER BY ma_tk DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_tk");
                int soThuTu = Integer.parseInt(maCuoi.substring(2)) + 1;
                return String.format("TK%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới tài khoản: " + e.getMessage(), e);
        }
        return "TK01";
    }

    //Tạo admin mặc định
    public void taoAdminMacDinh() {
        String kiemtra = "SELECT COUNT(*) FROM tai_khoan WHERE vai_tro = 'ADMIN'";
        try(Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(kiemtra)) {
            if(rs.next() && rs.getInt(1) == 0) {
                String sql = "INSERT INTO tai_khoan (ma_tk, ten_dang_nhap, mat_khau, vai_tro, ma_nv) VALUES ('TK001', 'admin', 'admin123', 'ADMIN', 'NV001')";
                stmt.executeUpdate(sql);
                System.out.println("Tạo thành công ADMIN mặc định (Tên đăng nhập: admin / Password: admin123)!");
                System.out.println(" Vui lòng đổi mật khẩu ADMIN ngay sau khi đăng nhập lần đầu!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo tài khoản ADMIN mặc định " + e.getMessage(), e);
        }
    }

    //Phương thức thêm tài khoản vào bảng tai_khoan trong mysql
    public void themTaiKhoan(TaiKhoan tk) {
        if (tk == null || tk.getTenDangNhap() == null || tk.getTenDangNhap().trim().isEmpty() 
                || tk.getMatKhau() == null || tk.getMatKhau().trim().isEmpty()) {
            throw new IllegalArgumentException("Tài khoản hoặc các trường bắt buộc không được để trống");
        }
        String sql = "INSERT INTO tai_khoan (ma_tk, ten_dang_nhap, mat_khau, vai_tro, ma_nv) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tk.getMaTK());
            stmt.setString(2, tk.getTenDangNhap());
            stmt.setString(3, tk.getMatKhau());
            stmt.setString(4, tk.getVaiTro());
            stmt.setString(5, tk.getMaNV());
            stmt.executeUpdate();

            System.out.println("Thêm tài khoản thành công!");
        } 
        catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm tài khoản: " + e.getMessage(), e);
        }
    }

    //Phương thức xóa nhân viên bằng mã nhân viên trong mysql
    public void xoaTaiKhoanTheoMaTK(String maTK) {
        if(maTK == null || maTK.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống!");
        }
        String sql = "DELETE FROM tai_khoan WHERE ma_tk = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maTK);
            stmt.executeUpdate();

            System.out.println("Xóa tài khoản thành công!");
        } 
        catch(Exception e) {
            throw new RuntimeException("Lỗi khi xóa tài khoản " + e.getMessage(), e);
        }
    }

    //Phương thức đổi mật khẩu
    public void doiMatKhau(String maTK, String matKhauMoi) {
        String sql = "UPDATE tai_khoan SET mat_khau = ? WHERE ma_tk = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, matKhauMoi);
            stmt.setString(2, maTK);
            stmt.executeUpdate();
            System.out.println("Đổi mật khẩu thành công!");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đổi mật khẩu " + e.getMessage(), e);
        }
    }

    //Phương thức đổi tên đăng nhập
    public void doiTenDangNhap(String maTK, String tenDangNhapMoi) {
        String sql = "UPDATE tai_khoan SET ten_dang_nhap = ? WHERE ma_tk = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhapMoi);
            stmt.setString(2, maTK);
            stmt.executeUpdate();
            System.out.println("Đổi tên đăng nhập thành công!");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đổi tên đăng nhập " + e.getMessage(), e);
        }
    }

    //Phương thức cập nhật vai trò của tài khoản
    public void capNhatVaiTro(String maTK, String vaiTro) {
        String sql = "UPDATE tai_khoan SET vai_tro = ? WHERE ma_tk = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, vaiTro);
            stmt.setString(2, maTK);
            stmt.executeUpdate();
            System.out.println("Cập nhật vai trò thành công!");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật vai trò " + e.getMessage(), e);
        }
    }

    //Phương thức đăng nhập
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ? AND mat_khau = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new TaiKhoan(
                    rs.getString("ma_tk"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro"),
                    rs.getString("ma_nv")
                );
            }
        } catch (Exception e) {
           throw new RuntimeException("Lỗi khi chương trình thực hiện đăng nhập " + e.getMessage(), e);
        }
        return null;
    }

    //Phương thức lấy tất cả các tài khoản
    public List<TaiKhoan> layTatCaTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM tai_khoan";

        try(Connection conn = DBConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setMaTK(rs.getString("ma_tk"));
                tk.setTenDangNhap(rs.getString("ten_dang_nhap"));
                tk.setMatKhau(rs.getString("mat_khau"));
                tk.setVaiTro(rs.getString("vai_tro"));
                tk.setMaNV(rs.getString("ma_nv"));
                list.add(tk);
                System.out.println(tk);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách tài khoản: " + e.getMessage(), e);
        }
        return list;
    }

    public TaiKhoan layTheoMaTK(String maTK) {
        String sql = "SELECT * FROM tai_khoan";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maTK);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new TaiKhoan(
                    rs.getString("ma_tk"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro"),
                    rs.getString("ma-nv")
                ); 
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chương trình lấy theo mã tài khoản " + e.getMessage(), e);
        }
        return null;
    }

    public TaiKhoan layTheoTenDangNhap(String tenDangNhap) {
        String sql = "SELECT * FROM tai_khoan WHERE ten_dang_nhap = ?";
        try(Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tenDangNhap);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                return new TaiKhoan(
                    rs.getString("ma_tk"),
                    rs.getString("ten_dang_nhap"),
                    rs.getString("mat_khau"),
                    rs.getString("vai_tro"),
                    rs.getString("ma_nv")
                ); 
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi chương trình lấy theo tên đăng nhập " + e.getMessage(), e);
        }
        return null;
    }

    // public static void main(String[] args) {
    //     TaiKhoanDAO dao = new TaiKhoanDAO();
    // //     TaiKhoan tk = new TaiKhoan();
    // //     tk.setMaTK("TK003");
    // //     tk.setTenDangNhap("maheu");
    // //     tk.setMatKhau("maheu2707");
    // //     tk.setVaiTro("ADMIN");
    // //     tk.setMaNV("NV003");

    // //     dao.themTaiKhoan(tk);
    // //     dao.taoAdminMacDinh();
    //     dao.layTatCaTaiKhoan();
    // //     // dao.doiMatKhau("TK002", "manhhieu27");
        
    // }
}
