package model;

import java.time.LocalDate;

public class NhanVien {
    //Khai báo thuộc tính của NhanVien
    private String maNV;
    private String tenNV;
    private String soDienThoai;
    private String gioiTinh;
    private String chucVu;
    private LocalDate ngaySinh;

    //Hàm khởi tạo không tham số
    public NhanVien() {
        maNV = "";
        tenNV = "";
        soDienThoai = "";
        gioiTinh = "";
        chucVu = "";
        ngaySinh = LocalDate.now();
    }

    //Hàm khởi tạo có tham số
    public NhanVien(String maNV, String tenNV, String soDienThoai, String gioiTinh, String chucVu, LocalDate ngaySinh) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.soDienThoai = soDienThoai;
        this.gioiTinh = gioiTinh;
        this.chucVu = chucVu;
        this.ngaySinh = ngaySinh;
    }

    //Getter
    public String getMaNV() {return maNV;}
    public String getTenNV() {return tenNV;}
    public String getSoDienThoai() {return soDienThoai;}
    public String getGioiTinh() {return gioiTinh;}
    public String getChucVu() {return chucVu;}
    public LocalDate getNgaySinh() {return ngaySinh;}

    //Setter
    public void setMaNV(String maNV) {this.maNV = maNV;}
    public void setTenNV(String tenNV) {
        if (tenNV == null || tenNV.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống");
        }
        this.tenNV = tenNV;
    }
    public void setSoDienThoai(String soDienThoai) {this.soDienThoai = soDienThoai;}
    public void setGioiTinh(String gioiTinh) {
        if (gioiTinh != null && !gioiTinh.equals("Nam") && !gioiTinh.equals("Nữ") && !gioiTinh.equals("Khác")) {
            throw new IllegalArgumentException("Giới tính phải là 'Nam', 'Nữ' hoặc 'Khác'");
        }
        this.gioiTinh = gioiTinh;
    }
    public void setChucVu(String chucvu) {this.chucVu = chucvu;}
    public void setNgaySinh(LocalDate ngaySinh) {this.ngaySinh = ngaySinh;}

    //hàm toString() của NhanVien
    @Override
    public String toString() {
        return "NhanVien {"
             + "maNV = " + maNV + " / "
             + "tenNV = " + tenNV + " / "
             + "soDienThoai = " + soDienThoai + " / "
             + "gioiTinh = " + gioiTinh + " / "
             + "chucVu = " + chucVu + " / "
             + "ngaySinh = " + ngaySinh
             + "}";
    }
}