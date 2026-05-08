package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.ChiTietHoaDonBan;
import model.SanPham;

public class ChiTietHoaDonBanDAO {
    public String sinhMaMoi() {
        String sql = "SELECT ma_chi_tiet FROM chi_tiet_hoa_don_ban ORDER BY ma_chi_tiet DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_chi_tiet");
                int soThuTu = Integer.parseInt(maCuoi.substring(3)) + 1;
                return String.format("CTB%03d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới chi tiết hóa đơn bán: " + e.getMessage(), e);        }
        return "CTB01";
    }

    public void themChiTiet(ChiTietHoaDonBan ct) {
        SanPhamDAO sanPhamDAO = new SanPhamDAO();
        SanPham sp = sanPhamDAO.layTheoId(ct.getMaSP());

        if (sp == null) {
            JOptionPane.showMessageDialog(null, "Sản phẩm không tồn tại!");
            return;
        }

        int tonKho = sp.getSoLuongTon();

        if (tonKho == 0) {
            JOptionPane.showMessageDialog(null,
                "Sản phẩm đã hết hàng!",
                "Hết hàng", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (ct.getSoLuong() > tonKho) {
            int confirm = JOptionPane.showConfirmDialog(null,
                "Kho chỉ còn " + tonKho + " sản phẩm!\n" +
                "Bạn có muốn mua " + tonKho + " không?",
                "Không đủ hàng", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ct.setSoLuong(tonKho);
            } else {
                return;
            }
        }

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

            // Giảm tồn kho trong SanPhamDAO
            sanPhamDAO.giamTonKho(ct.getMaSP(), ct.getSoLuong());
            System.out.println("Thêm chi tiết hoá đơn bán thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm chi tiết hóa đơn bán: " + e.getMessage(), e);
        }
    }

    public void xoaChiTiet(String maChiTiet) {
        // Hoàn tồn kho trước khi xoá
        ChiTietHoaDonBan ct = layTheoId(maChiTiet);
        if (ct != null) {
            new SanPhamDAO().tangTonKho(ct.getMaSP(), ct.getSoLuong());
        }
        String sql = "DELETE FROM chi_tiet_hoa_don_ban WHERE ma_chi_tiet=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maChiTiet);
            stmt.executeUpdate();
            System.out.println("Xoá chi tiết hoá đơn bán thành công!");
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa chi tiết hóa đơn bán: " + e.getMessage(), e);
        }
    }

    public void xoaTheoHoaDon(String maHDB) {
        // Hoàn tồn kho từng dòng trước khi xoá
        List<ChiTietHoaDonBan> ds = layTheoHoaDon(maHDB);
        SanPhamDAO sanPhamDAO = new SanPhamDAO();
        for (ChiTietHoaDonBan ct : ds) {
            sanPhamDAO.tangTonKho(ct.getMaSP(), ct.getSoLuong());
        }
        String sql = "DELETE FROM chi_tiet_hoa_don_ban WHERE ma_hdb=?";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maHDB);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa chi tiết theo mã hóa đơn bán: " + e.getMessage(), e);
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
            throw new RuntimeException("Lỗi khi lấy chi tiết theo hóa đơn bán: " + e.getMessage(), e);
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
            throw new RuntimeException("Lỗi khi lấy chi tiết: " + e.getMessage(), e);
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
            throw new RuntimeException("Lỗi khi tính tổng tiền theo mã hóa đơn bán: " + e.getMessage(), e);
        }
        return 0;
    }
}