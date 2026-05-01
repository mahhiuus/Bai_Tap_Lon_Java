package model;

import java.time.LocalDateTime;
import java.time.Duration;

public class PhienChoi {
    // Khai báo thuộc tính của PhienChoi
    private String maPhien;
    private String maBan;
    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;
    private String trangThaiPhien;

    // Hàm khởi tạo không tham số
    public PhienChoi() {
        maPhien = "";
        maBan = "";
        thoiGianBatDau = LocalDateTime.now();
        thoiGianKetThuc = null;
        trangThaiPhien = "";
    }

    // Hàm khởi tạo có tham số
    public PhienChoi(String maPhien, String maBan, LocalDateTime thoiGianBatDau,
                     LocalDateTime thoiGianKetThuc, String trangThaiPhien) {
        this.maPhien = maPhien;
        this.maBan = maBan;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.trangThaiPhien = trangThaiPhien;
    }

    // Getter
    public String getMaPhien() {
        return maPhien;
    }

    public String getMaBan() {
        return maBan;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public String getTrangThaiPhien() {
        return trangThaiPhien;
    }

    // Setter
    public void setMaPhien(String maPhien) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống");
        }
        this.maPhien = maPhien;
    }

    public void setMaBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống");
        }
        this.maBan = maBan;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        if (thoiGianBatDau == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống");
        }

        if (thoiGianKetThuc != null && thoiGianKetThuc.isBefore(thoiGianBatDau)) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được sau thời gian kết thúc");
        }

        this.thoiGianBatDau = thoiGianBatDau;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        if (thoiGianKetThuc != null && thoiGianBatDau != null 
                && thoiGianKetThuc.isBefore(thoiGianBatDau)) {
            throw new IllegalArgumentException("Thời gian kết thúc không được trước thời gian bắt đầu");
        }

        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public void setTrangThaiPhien(String trangThaiPhien) {
        if (trangThaiPhien == null || trangThaiPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái phiên không được để trống");
        }
        this.trangThaiPhien = trangThaiPhien;
    }

    // Hàm tính số phút chơi
    public long getSoPhutChoi() {
        if (thoiGianBatDau == null || thoiGianKetThuc == null) {
            return 0;
        }
        return Duration.between(thoiGianBatDau, thoiGianKetThuc).toMinutes();
    }

    // Hàm tính số giờ chơi
    public double getSoGioChoi() {
        return getSoPhutChoi() / 60.0;
    }

    // Hàm toString() của PhienChoi
    @Override
    public String toString() {
        return "PhienChoi {"
             + "maPhien = " + maPhien + " / "
             + "maBan = " + maBan + " / "
             + "thoiGianBatDau = " + thoiGianBatDau + " / "
             + "thoiGianKetThuc = " + thoiGianKetThuc + " / "
             + "trangThaiPhien = " + trangThaiPhien + " / "
             + "soPhutChoi = " + getSoPhutChoi()
             + "}";
    }
}