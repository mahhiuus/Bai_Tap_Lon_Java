package model;

import java.time.LocalDate;

public class KhachHang {
    private String maKH; 
    private String tenKH;
    private String sdt;
    private String diaChi;
    private int diemTichLuy;
    private LocalDate ngayDangKy;

    public KhachHang() {
        this.maKH = " ";
        this.tenKH = "";
        this.sdt = "";
        this.diaChi = "";
        this.diemTichLuy = 0;
        this.ngayDangKy = LocalDate.now();
    }

    public KhachHang(String maKH, String tenKH, String sdt, String diaChi, int diemTichLuy, LocalDate ngayDangKy) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.diemTichLuy = diemTichLuy;
        this.ngayDangKy = ngayDangKy;
    }

    // Getter & Setter
    public String getMaKH() { 
        return maKH;
    }

    public void setMaKH(String maKH) { 
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        if (tenKH == null || tenKH.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten khach hang khong duoc de trong!");
        }
        this.tenKH = tenKH;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public int getDiemTichLuy() { 
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public LocalDate getNgayDangKy() { 
        return ngayDangKy;
    }

    public void setNgayDangKy(LocalDate ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    @Override
    public String toString() {
        return "KhachHang {" + "Ma KH: " + maKH + " / Ten KH: " + tenKH + " / SDT: " + sdt 
                + " / Dia chi: " + diaChi + " / Diem: " + diemTichLuy + " / Ngay: " + ngayDangKy + "}";
    }
}