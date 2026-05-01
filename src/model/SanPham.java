package model;

public class SanPham {
    private String maSP;
    private String tenSP;
    private String loaiSP; //Do an, Do uong, Dung cu
    private double giaBan;
    private int soLuongTon;
    private String maNCC;

    //Ham khoi tao khong tham so
    public SanPham(){
        maSP = "";
        tenSP = "";
        loaiSP = "";
        giaBan = 0;
        soLuongTon = 0;
        maNCC = "";
    }

    //Ham khoi tao co tham so
    public SanPham(String maSP, String tenSP, String loaiSP, double giaBan, int soLuongTon, String maNCC){
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.loaiSP = loaiSP;
        this.giaBan = giaBan;
        this.soLuongTon = soLuongTon;
        this.maNCC = maNCC;
    }

    //Pthuc Get
    public String getMaSP(){ return maSP;}
    public String getTenSP(){ return tenSP;}
    public String getLoaiSP(){ return loaiSP;}
    public double getGiaBan(){ return giaBan;}
    public int getSoLuongTon(){ return soLuongTon;}
    public String getMaNCC(){ return maNCC;}

    //Pthuc set
    public void setMaSP(String maSP){ this.maSP = maSP; }
    public void setTenSP(String tenSP){
        if (tenSP == null) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        this.tenSP = tenSP.trim();
    }
    public void setLoaiSP(String loaiSP){ 
        if (loaiSP == null || loaiSP.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại sản phẩm không được để trống!");
        }
        String l = loaiSP.trim().toUpperCase(); // Chuyển về chữ hoa để so sánh cho chính xác
        if (l.equals("DO_AN") || l.equals("DO_UONG") || l.equals("DUNG_CU")) {
            loaiSP = l;
        }else {
             throw new IllegalArgumentException("Loại sản phẩm phải là: DO_AN, DO_UONG hoặc DUNG_CU!");
        }
        this.loaiSP = loaiSP; 
    }
    public void setGiaBan(double giaBan){
        if (giaBan < 0) {
        throw new IllegalArgumentException("Giá bán không được nhỏ hơn 0!");
    }
        this.giaBan = giaBan;
    }
    public void setSoLuongTon(int soLuongTon){
        if (soLuongTon < 0) {
        throw new IllegalArgumentException("Số lượng tồn không được nhỏ hơn 0!");
        }
        this.soLuongTon =  soLuongTon;
    }
    public void setMaNCC(String maNCC){ this.maNCC = maNCC; }

    @Override
    public String toString() {
        return "SanPham {"
             + "maSP = " + maSP + " / "
             + "tenSP = " + tenSP + " / "
             + "loaiSP = " + loaiSP + " / "
             + "giaBan = " + giaBan + " / "
             + "soLuongton = " + soLuongTon + " / "
             + "maNCC = " + maNCC
             + "}";
    }
}