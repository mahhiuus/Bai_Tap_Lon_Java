package model;

import java.time.LocalDate;

public class HoaDonBan {
    private String maHDB;
    private String maPhien;
    private String maKH;
    private String maNV;
    private LocalDate ngayBan;
    private double tienBida;
    private double tienSanPham;
    private double tongTien;
    private String ghiChu;

    public HoaDonBan() {
        maHDB = "";
        maPhien = "";
        maKH = "";
        maNV = "";
        ngayBan = LocalDate.now();
        tienBida = 0;
        tienSanPham = 0;
        tongTien = 0;
        ghiChu = "";
    }

    public HoaDonBan(String maHDB, String maPhien, String maKH, String maNV, LocalDate ngayBan, double tienBida, double tienSanPham, double tongTien, String ghiChu) {
        this.maHDB = maHDB;
        this.maPhien = maPhien;
        this.maKH = maKH;
        this.maNV = maNV;
        this.ngayBan = ngayBan;
        this.tienBida = tienBida;
        this.tienSanPham = tienSanPham;
        this.tongTien = tongTien;
        this.ghiChu = ghiChu;
    }

    public String getMaHDB() { return maHDB; }
    public String getMaPhien() { return maPhien; }
    public String getMaKH() { return maKH; }
    public String getMaNV() { return maNV; }
    public LocalDate getNgayBan() { return ngayBan; }
    public double getTienBida() { return tienBida; }
    public double getTienSanPham() { return tienSanPham; }
    public double getTongTien() { return tongTien; }
    public String getGhiChu() { return ghiChu; }

    public void setMaHDB(String maHDB) { this.maHDB = maHDB; }
    public void setMaPhien(String maPhien) { this.maPhien = maPhien; }
    public void setMaKH(String maKH) { this.maKH = maKH; }
    public void setMaNV(String maNV) { this.maNV = maNV; }
    public void setNgayBan(LocalDate ngayBan) { this.ngayBan = ngayBan; }
    public void setTienBida(double tienBida) { this.tienBida = tienBida; }
    public void setTienSanPham(double tienSanPham) { this.tienSanPham = tienSanPham; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String toString() {
        return "HĐB " + maHDB + " - " + ngayBan.toString();
    }
}