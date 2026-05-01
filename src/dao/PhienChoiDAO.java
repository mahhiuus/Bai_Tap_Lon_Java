package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import model.PhienChoi;

public class PhienChoiDAO {
    public String sinhMaMoi() {
        String sql = "SELECT ma_phien FROM phien_choi ORDER BY ma_phien DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                String maCuoi = rs.getString("ma_phien");
                int soThuTu = Integer.parseInt(maCuoi.substring(1)) + 1;
                return String.format("P%02d", soThuTu);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi sinh mã mới phiên chơi: " + e.getMessage(), e);
        }
        return "P01";
    }

    // Phương thức thêm phiên chơi
    public void themPhien(PhienChoi phien) {
        if (phien == null || phien.getMaPhien() == null || phien.getMaPhien().trim().isEmpty()
                || phien.getMaBan() == null || phien.getMaBan().trim().isEmpty()
                || phien.getThoiGianBatDau() == null
                || phien.getTrangThaiPhien() == null || phien.getTrangThaiPhien().trim().isEmpty()) {
            throw new IllegalArgumentException("Thông tin phiên chơi không hợp lệ!");
        }

        String sql = """
            INSERT INTO phien_choi 
            (ma_phien, ma_ban, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phien.getMaPhien());
            stmt.setString(2, phien.getMaBan());
            stmt.setTimestamp(3, Timestamp.valueOf(phien.getThoiGianBatDau()));

            if (phien.getThoiGianKetThuc() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(phien.getThoiGianKetThuc()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }

            stmt.setString(5, phien.getTrangThaiPhien());

            stmt.executeUpdate();
            System.out.println("Thêm phiên chơi thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm phiên chơi: " + e.getMessage(), e);
        }
    }

    // Phương thức cập nhật phiên chơi
    public void capNhatPhien(PhienChoi phien) {
        if (phien == null || phien.getMaPhien() == null || phien.getMaPhien().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        String sql = """
            UPDATE phien_choi 
            SET ma_ban = ?, thoi_gian_bat_dau = ?, thoi_gian_ket_thuc = ?, trang_thai = ?
            WHERE ma_phien = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, phien.getMaBan());
            stmt.setTimestamp(2, Timestamp.valueOf(phien.getThoiGianBatDau()));

            if (phien.getThoiGianKetThuc() != null) {
                stmt.setTimestamp(3, Timestamp.valueOf(phien.getThoiGianKetThuc()));
            } else {
                stmt.setNull(3, Types.TIMESTAMP);
            }

            stmt.setString(4, phien.getTrangThaiPhien());
            stmt.setString(5, phien.getMaPhien());

            stmt.executeUpdate();
            System.out.println("Cập nhật phiên chơi thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật phiên chơi: " + e.getMessage(), e);
        }
    }

    // Phương thức kết thúc phiên chơi
    public void ketThucPhien(String maPhien, LocalDateTime thoiGianKetThuc) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        if (thoiGianKetThuc == null) {
            throw new IllegalArgumentException("Thời gian kết thúc không được để trống!");
        }

        String sql = """
            UPDATE phien_choi 
            SET thoi_gian_ket_thuc = ?, trang_thai = ?
            WHERE ma_phien = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(thoiGianKetThuc));
            stmt.setString(2, "DA_KET_THUC");
            stmt.setString(3, maPhien);

            stmt.executeUpdate();
            System.out.println("Kết thúc phiên chơi thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kết thúc phiên chơi: " + e.getMessage(), e);
        }
    }

    // Phương thức xóa phiên chơi
    public void xoaPhien(String maPhien) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        String sql = "DELETE FROM phien_choi WHERE ma_phien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maPhien);
            stmt.executeUpdate();

            System.out.println("Xóa phiên chơi thành công!");

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa phiên chơi: " + e.getMessage(), e);
        }
    }

    // Phương thức lấy tất cả phiên chơi
    public List<PhienChoi> layTatCaPhien() {
        List<PhienChoi> list = new ArrayList<>();

        String sql = "SELECT * FROM phien_choi";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PhienChoi phien = taoPhienChoiTuResultSet(rs);
                list.add(phien);
                System.out.println(phien);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách phiên chơi: " + e.getMessage(), e);
        }

        return list;
    }

    // Phương thức tìm phiên theo mã phiên
    public PhienChoi timTheoMaPhien(String maPhien) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống!");
        }

        String sql = "SELECT * FROM phien_choi WHERE ma_phien = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maPhien);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return taoPhienChoiTuResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm phiên chơi: " + e.getMessage(), e);
        }

        return null;
    }

    // Phương thức tìm phiên đang chơi theo mã bàn
    public PhienChoi timPhienDangChoiTheoBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống!");
        }

        String sql = """
            SELECT * FROM phien_choi
            WHERE ma_ban = ? AND trang_thai = 'DANG_CHOI'
            ORDER BY thoi_gian_bat_dau DESC
            LIMIT 1
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBan);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return taoPhienChoiTuResultSet(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm phiên đang chơi theo bàn: " + e.getMessage(), e);
        }

        return null;
    }

    // Phương thức lấy danh sách phiên theo mã bàn
    public List<PhienChoi> layPhienTheoBan(String maBan) {
        List<PhienChoi> list = new ArrayList<>();

        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống!");
        }

        String sql = "SELECT * FROM phien_choi WHERE ma_ban = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, maBan);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PhienChoi phien = taoPhienChoiTuResultSet(rs);
                    list.add(phien);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy phiên theo bàn: " + e.getMessage(), e);
        }

        return list;
    }

    // Hàm hỗ trợ tạo đối tượng PhienChoi từ ResultSet
    private PhienChoi taoPhienChoiTuResultSet(ResultSet rs) throws SQLException {
        PhienChoi phien = new PhienChoi();

        phien.setMaPhien(rs.getString("ma_phien"));
        phien.setMaBan(rs.getString("ma_ban"));

        Timestamp batDau = rs.getTimestamp("thoi_gian_bat_dau");
        Timestamp ketThuc = rs.getTimestamp("thoi_gian_ket_thuc");

        if (batDau != null) {
            phien.setThoiGianBatDau(batDau.toLocalDateTime());
        }

        if (ketThuc != null) {
            phien.setThoiGianKetThuc(ketThuc.toLocalDateTime());
        }

        phien.setTrangThaiPhien(rs.getString("trang_thai"));

        return phien;
    }
}