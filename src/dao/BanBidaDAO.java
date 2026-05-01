package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.BanBida;

public class BanBidaDAO {
    // Phương thức thêm bàn bida
    public void themBan(BanBida ban) {
        if (ban == null || ban.getMaBan() == null || ban.getMaBan().trim().isEmpty()
                || ban.getTenBan() == null || ban.getTenBan().trim().isEmpty()
                || ban.getLoaiBan() == null || ban.getLoaiBan().trim().isEmpty()
                || ban.getTrangThaiBan() == null || ban.getTrangThaiBan().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin bàn bida không hợp lệ!");
        }

        String loaiBan = ban.getLoaiBan().trim().toUpperCase();

        if (!loaiBan.equals("THUONG") && !loaiBan.equals("VIP")) {
            throw new IllegalArgumentException("Loại bàn chỉ được là THUONG hoặc VIP!");
        }

        String sql = "INSERT INTO ban_bida (ma_ban, ten_ban, loai_ban, trang_thai) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ban.getMaBan().trim());
            stmt.setString(2, ban.getTenBan().trim());
            stmt.setString(3, loaiBan);
            stmt.setString(4, ban.getTrangThaiBan().trim());

            stmt.executeUpdate();
            System.out.println("Thêm bàn bida thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm bàn bida: " + e.getMessage(), e);
        }
    }

    // Phương thức cập nhật bàn bida
    public void capNhatBan(BanBida ban) {
        if (ban == null || ban.getMaBan() == null || ban.getMaBan().trim().isEmpty()
                || ban.getTenBan() == null || ban.getTenBan().trim().isEmpty()
                || ban.getLoaiBan() == null || ban.getLoaiBan().trim().isEmpty()
                || ban.getTrangThaiBan() == null || ban.getTrangThaiBan().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin bàn bida không hợp lệ!");
        }

        String loaiBan = ban.getLoaiBan().trim().toUpperCase();

        if (!loaiBan.equals("THUONG") && !loaiBan.equals("VIP")) {
            throw new IllegalArgumentException("Loại bàn chỉ được là THUONG hoặc VIP!");
        }

        String sql = "UPDATE ban_bida SET ten_ban = ?, loai_ban = ?, trang_thai = ? WHERE ma_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ban.getTenBan().trim());
            stmt.setString(2, loaiBan);
            stmt.setString(3, ban.getTrangThaiBan().trim());
            stmt.setString(4, ban.getMaBan().trim());

            stmt.executeUpdate();
            System.out.println("Cập nhật bàn bida thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật bàn bida: " + e.getMessage(), e);
        }
    }

    // Phương thức cập nhật trạng thái bàn
    public void capNhatTrangThai(String maBan, String trangThaiBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống!");
        }

        if (trangThaiBan == null || trangThaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống!");
        }

        String sql = "UPDATE ban_bida SET trang_thai = ? WHERE ma_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThaiBan.trim());
            stmt.setString(2, maBan.trim());

            stmt.executeUpdate();
            System.out.println("Cập nhật trạng thái bàn thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật trạng thái bàn: " + e.getMessage(), e);
        }
    }

    // Phương thức xóa bàn bida
    public void xoaBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống!");
        }

        String sql = "DELETE FROM ban_bida WHERE ma_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBan.trim());
            stmt.executeUpdate();

            System.out.println("Xóa bàn bida thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa bàn bida: " + e.getMessage(), e);
        }
    }

    // Phương thức lấy tất cả bàn bida
    public List<BanBida> layTatCaBan() {
        List<BanBida> list = new ArrayList<>();

        String sql = "SELECT * FROM ban_bida";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BanBida ban = new BanBida();

                ban.setMaBan(rs.getString("ma_ban"));
                ban.setTenBan(rs.getString("ten_ban"));
                ban.setLoaiBan(rs.getString("loai_ban"));
                ban.setTrangThaiBan(rs.getString("trang_thai"));

                list.add(ban);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách bàn bida: " + e.getMessage(), e);
        }

        return list;
    }

    // Phương thức tìm bàn theo mã bàn
    public BanBida timTheoMaBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống!");
        }

        String sql = "SELECT * FROM ban_bida WHERE ma_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBan.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BanBida ban = new BanBida();

                    ban.setMaBan(rs.getString("ma_ban"));
                    ban.setTenBan(rs.getString("ten_ban"));
                    ban.setLoaiBan(rs.getString("loai_ban"));
                    ban.setTrangThaiBan(rs.getString("trang_thai"));

                    return ban;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bàn bida: " + e.getMessage(), e);
        }

        return null;
    }

    // Phương thức tìm bàn theo trạng thái
    public List<BanBida> timTheoTrangThai(String trangThaiBan) {
        List<BanBida> list = new ArrayList<>();

        if (trangThaiBan == null || trangThaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống!");
        }

        String sql = "SELECT * FROM ban_bida WHERE trang_thai = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, trangThaiBan.trim());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BanBida ban = new BanBida();

                    ban.setMaBan(rs.getString("ma_ban"));
                    ban.setTenBan(rs.getString("ten_ban"));
                    ban.setLoaiBan(rs.getString("loai_ban"));
                    ban.setTrangThaiBan(rs.getString("trang_thai"));

                    list.add(ban);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bàn theo trạng thái: " + e.getMessage(), e);
        }

        return list;
    }

    // Phương thức tìm bàn theo loại bàn
    public List<BanBida> timTheoLoaiBan(String loaiBan) {
        List<BanBida> list = new ArrayList<>();

        if (loaiBan == null || loaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại bàn không được để trống!");
        }

        loaiBan = loaiBan.trim().toUpperCase();

        if (!loaiBan.equals("THUONG") && !loaiBan.equals("VIP")) {
            throw new IllegalArgumentException("Loại bàn chỉ được là THUONG hoặc VIP!");
        }

        String sql = "SELECT * FROM ban_bida WHERE loai_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loaiBan);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BanBida ban = new BanBida();

                    ban.setMaBan(rs.getString("ma_ban"));
                    ban.setTenBan(rs.getString("ten_ban"));
                    ban.setLoaiBan(rs.getString("loai_ban"));
                    ban.setTrangThaiBan(rs.getString("trang_thai"));

                    list.add(ban);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm bàn theo loại bàn: " + e.getMessage(), e);
        }

        return list;
    }
}