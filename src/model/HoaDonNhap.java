package model;

import java.time.LocalDate;

public class HoaDonNhap {
    private String maHDN;
    private String maNCC;
    private String maNV;
    private LocalDate ngayNhap;
    private double tongTien;

    //ham khoi tao khong tham so
    public HoaDonNhap(){
        maHDN = "";
        maNCC = "";
        maNV = "";
        ngayNhap = LocalDate.now();
        tongTien = 0;
    }

    //ham khoi tao co tham so
    public HoaDonNhap(String maHDN, String maNCC, String maNV, LocalDate ngayNhap, double tongTien){
        this.maHDN = maHDN;
        this.maNCC = maNCC;
        this.maNV = maNV;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
    }

    //Phuong thuc get
    public String getMaHDN(){ return this.maHDN;}
    public String getMaNCC(){ return this.maNCC;}
    public String getMaNV(){ return this.maNV;}
    public LocalDate getNgayNhap(){ return this.ngayNhap;}
    public double getTongTien(){ return this.tongTien;}

    //Phuong thuc set
    public void setMaHDN(String maHDN){ this.maHDN = maHDN;}
    public void setMaNCC(String maNCC){ this.maNCC = maNCC;}
    public void setMaNV(String maNV){ this.maNV = maNV;}
    public void setNgayNhap(LocalDate ngayNhap){ this.ngayNhap = ngayNhap;}
    public void setTongTien(double tongTien){ 
        if (tongTien < 0) {
            throw new IllegalArgumentException("Tổng tiền không được âm!");
        }
        this.tongTien = tongTien;
    }

    @Override
    public String toString() {
        return "HoaDonNhap{" +
               "maHDN='" + maHDN + '\'' +
               ", maNCC='" + maNCC + '\'' +
               ", maNV='" + maNV + '\'' +
               ", ngayNhap=" + ngayNhap +
               ", tongTien=" + tongTien +
               '}';
    }

}