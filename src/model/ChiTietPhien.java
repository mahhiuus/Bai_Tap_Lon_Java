package model;

public class ChiTietPhien {
    // Khai báo thuộc tính của ChiTietPhien
    private String maChiTiet;
    private String maPhien;
    private String maSanPham;
    private int soLuong;
    private double donGia;

    // Hàm khởi tạo không tham số
    public ChiTietPhien() {
        maChiTiet = "";
        maPhien = "";
        maSanPham = "";
        soLuong = 0;
        donGia = 0;
    }

    // Hàm khởi tạo có tham số
    public ChiTietPhien(String maChiTiet, String maPhien, String maSanPham, int soLuong, double donGia) {
        this.maChiTiet = maChiTiet;
        this.maPhien = maPhien;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // Getter
    public String getMaChiTiet() {
        return maChiTiet;
    }

    public String getMaPhien() {
        return maPhien;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    // Setter
    public void setMaChiTiet(String maChiTiet) {
        if (maChiTiet == null || maChiTiet.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã chi tiết không được để trống");
        }
        this.maChiTiet = maChiTiet;
    }

    public void setMaPhien(String maPhien) {
        if (maPhien == null || maPhien.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã phiên không được để trống");
        }
        this.maPhien = maPhien;
    }

    public void setMaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống");
        }
        this.maSanPham = maSanPham;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        this.soLuong = soLuong;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException("Đơn giá không được nhỏ hơn 0");
        }
        this.donGia = donGia;
    }

    // Hàm tính thành tiền
    public double getThanhTien() {
        return soLuong * donGia;
    }

    // Hàm toString() của ChiTietPhien
    @Override
    public String toString() {
        return "ChiTietPhien {"
             + "maChiTiet = " + maChiTiet + " / "
             + "maPhien = " + maPhien + " / "
             + "maSanPham = " + maSanPham + " / "
             + "soLuong = " + soLuong + " / "
             + "donGia = " + donGia + " / "
             + "thanhTien = " + getThanhTien()
             + "}";
    }
}