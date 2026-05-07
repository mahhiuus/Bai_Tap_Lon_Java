package model;

public class ChiTietHoaDonNhap {
    private String maChiTiet;
    private String maHDN;
    private String maSP;
    private int soLuong;
    private double donGiaNhap;

    // ham khoi tao khong tham so
    public ChiTietHoaDonNhap() {
        maChiTiet = "";
        maHDN = "";
        maSP = "";
        soLuong = 0;
        donGiaNhap = 0;
    }

    // ham khoi tao co tham so
    public ChiTietHoaDonNhap(String maChiTiet, String maHDN, String maSP, int soLuong, double donGiaNhap) {
        this.maChiTiet = maChiTiet;
        this.maHDN = maHDN;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    // Getters and Setters
    public String getMaChiTiet() { return maChiTiet;}
    public void setMaChiTiet(String maChiTiet) { this.maChiTiet = maChiTiet;}

    public String getMaHDN() { return maHDN;}
    public void setMaHDN(String maHDN) { this.maHDN = maHDN;}

    public String getMaSP() { return maSP;}
    public void setMaSP(String maSP) { this.maSP = maSP;}

    public int getSoLuong() { return soLuong;}
    public void setSoLuong(int soLuong) { this.soLuong = soLuong;}

    public double getDonGiaNhap() { return donGiaNhap;}
    public void setDonGiaNhap(double donGiaNhap) { this.donGiaNhap = donGiaNhap;}

    @Override
    public String toString() {
        return "ChiTietHoaDonNhap{" +
               "maChiTiet='" + maChiTiet + '\'' +
               ", maHDN='" + maHDN + '\'' +
               ", maSP='" + maSP + '\'' +
               ", soLuong=" + soLuong +
               ", donGiaNhap=" + donGiaNhap +
               '}';
    }
}
