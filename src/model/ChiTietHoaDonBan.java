package model;

public class ChiTietHoaDonBan {
    private String maChiTiet;
    private String maHDB;
    private String maSP;
    private int soLuong;
    private double donGiaBan;

    public ChiTietHoaDonBan() {
        maChiTiet = "";
        maHDB = "";
        maSP = "";
        soLuong = 0;
        donGiaBan = 0;
    }

    public ChiTietHoaDonBan(String maChiTiet, String maHDB, String maSP, int soLuong, double donGiaBan) {
        this.maChiTiet = maChiTiet;
        this.maHDB = maHDB;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGiaBan = donGiaBan;
    }

    public String getMaChiTiet() { return maChiTiet; }
    public String getMaHDB() { return maHDB; }
    public String getMaSP() { return maSP; }
    public int getSoLuong() { return soLuong; }
    public double getDonGiaBan() { return donGiaBan; }

    public void setMaChiTiet(String maChiTiet) { this.maChiTiet = maChiTiet; }
    public void setMaHDB(String maHDB) { this.maHDB = maHDB; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public void setDonGiaBan(double donGiaBan) { this.donGiaBan = donGiaBan; }

    public String toString() {
        return "ChiTiet HDB " + maHDB + " - SP " + maSP + " - SL " + soLuong;
    }
}