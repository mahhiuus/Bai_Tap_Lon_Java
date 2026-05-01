package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.NhaCungCap;

public class NhaCungCapDAO {
    public void themNhaCungCap(NhaCungCap ncc) {
        if (ncc == null || ncc.getMaNCC() == null) {
            throw new IllegalArgumentException("Dữ liệu không hợp lệ");
        }
        String sql = "INSERT INTO nha_cung_cap (ma_ncc, ten_cong_ty, sdt, dia_chi, email, nguoi_lien_he) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ncc.getMaNCC()); 
            stmt.setString(2, ncc.getTenCongTy());
            stmt.setString(3, ncc.getSdt());
            stmt.setString(4, ncc.getDiaChi());
            stmt.setString(5, ncc.getEmail());
            stmt.setString(6, ncc.getNguoiLienHe());

            stmt.executeUpdate();
            System.out.println("Thêm nhà cung cấp thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi thêm nhà cung cấp", e);
        }
    }

    public void capNhatNhaCungCap(NhaCungCap ncc) {
        String sql = "UPDATE nha_cung_cap SET ten_cong_ty = ?, sdt = ?, dia_chi = ?, email = ?, nguoi_lien_he = ? WHERE ma_ncc = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ncc.getTenCongTy());
            stmt.setString(2, ncc.getSdt());
            stmt.setString(3, ncc.getDiaChi());
            stmt.setString(4, ncc.getEmail());
            stmt.setString(5, ncc.getNguoiLienHe());
            stmt.setString(6, ncc.getMaNCC());
            stmt.executeUpdate();
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
    }

    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM nha_cung_cap";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap();
                ncc.setMaNCC(rs.getString("ma_ncc")); 
                ncc.setTenCongTy(rs.getString("ten_cong_ty"));
                ncc.setSdt(rs.getString("sdt"));
                ncc.setDiaChi(rs.getString("dia_chi"));
                ncc.setEmail(rs.getString("email"));
                ncc.setNguoiLienHe(rs.getString("nguoi_lien_he"));
                list.add(ncc);
                System.out.println(ncc);
            }
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
        return list;
    }

    public NhaCungCap timTheoMaNhaCungCap(String maNCC) {
        if (maNCC == null || maNCC.trim().isEmpty()) {
            throw new RuntimeException("Mã nhà cung cấp không được để trống!");
        }
        String sql = "SELECT * FROM nha_cung_cap WHERE ma_ncc = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNCC);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.setMaNCC(rs.getString("ma_ncc"));
                    ncc.setTenCongTy(rs.getString("ten_cong_ty"));
                    ncc.setSdt(rs.getString("sdt"));
                    ncc.setDiaChi(rs.getString("dia_chi"));
                    ncc.setEmail(rs.getString("email"));
                    ncc.setNguoiLienHe(rs.getString("nguoi_lien_he"));
                    return ncc;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm theo mã nhà cung cấp: " + e.getMessage(), e);
        }
        return null;
    }

    public List<NhaCungCap> timKiem(String keyword) {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT * FROM nha_cung_cap WHERE ma_ncc LIKE ? OR ten_cong_ty LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String likeKeyword = "%" + (keyword == null ? "" : keyword) + "%";
            stmt.setString(1, likeKeyword);
            stmt.setString(2, likeKeyword);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    NhaCungCap ncc = new NhaCungCap();
                    ncc.setMaNCC(rs.getString("ma_ncc"));
                    ncc.setTenCongTy(rs.getString("ten_cong_ty"));
                    ncc.setSdt(rs.getString("sdt"));
                    ncc.setDiaChi(rs.getString("dia_chi"));
                    ncc.setEmail(rs.getString("email"));
                    ncc.setNguoiLienHe(rs.getString("nguoi_lien_he"));
                    list.add(ncc);
                }
            }
        } catch (SQLException e) { 
            throw new RuntimeException("Lỗi khi tìm kiếm nhà cung cấp: " + e.getMessage(), e); 
        }
        return list;
    }

    public void xoaNhaCungCap(String maNCC) {
        String sql = "DELETE FROM nha_cung_cap WHERE ma_ncc = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNCC);
            stmt.executeUpdate();
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        }
    }
    // public static void main(String[] args) {
    //     NhaCungCapDAO dao = new NhaCungCapDAO();
    //     dao.createTable();
    // }
}