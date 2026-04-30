package model;

public class TaiKhoan {
    //Khai báo thuộc tính của TaiKhoan
    private String maTK;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private String maNV;

    //Hàm khởi tạo không tham số
    public TaiKhoan() {
        maTK = "";
        tenDangNhap = "unknown";
        matKhau = "unknown";
        vaiTro = "NHANVIEN";
        maNV = "";
    }

    //Hàm khởi tạo có tham số
    public TaiKhoan(String maTK, String tenDangNhap, String matKhau, String vaiTro, String maNV) {
        this.maTK = maTK;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.maNV = maNV;
    }

    //Getter
    public String getMaTK() {return maTK;}
    public String getTenDangNhap() {return tenDangNhap;}
    public String getMatKhau() {return matKhau;}
    public String getVaiTro() {return vaiTro;}
    public String getMaNV() {return maNV;}

    //Setter
    public void setMaTK(String maTK) {this.maTK = maTK;}
    public void setTenDangNhap(String tenDangNhap) {
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }
        this.tenDangNhap = tenDangNhap;
    }
    public void setMatKhau(String matKhau) {
        if (matKhau == null) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        this.matKhau = matKhau;}
    public void setVaiTro(String vaiTro) {
        if(vaiTro != null && !vaiTro.equals("ADMIN") && !vaiTro.equals("NHANVIEN")) {
            throw new IllegalArgumentException("Vai trò phải là ADMIN hoặc NHANVIEN");
        }
        this.vaiTro = vaiTro;}
    public void setMaNV(String maNV) {this.maNV = maNV;}

    //hàm toString() của TaiKhoan
    @Override
    public String toString() {
        return "TaiKhoan {"
             + "maTK = " + maTK + " / "
             + "tenDangNhap = " + tenDangNhap + " / "
             + "matKhau = " + matKhau + " / "
             + "vaiTro = " + vaiTro + " / "
             + "maNV = " + maNV
             + "}";
    }

    //Hàm main để test TaiKhoan (8/3/2026)
    // public static void main(String[] args) {
    //     TaiKhoan tk = new TaiKhoan();
    //     tk.setVaiTro("helo");
    //     tk.getVaiTro();
    // }
}
