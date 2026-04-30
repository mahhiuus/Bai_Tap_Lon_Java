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
    public String getmaSP(){ return maSP;}
    public String gettenSP(){ return tenSP;}
    public String getloaiSP(){ return loaiSP;}
    public double getgiaBan(){ return giaBan;}
    public int getsoLuongTon(){ return soLuongTon;}
    public String getmaNCC(){ return maNCC;}

    //Pthuc set
    public void setmaSP(String masp){ this.maSP = masp; }
    public void settenSP(String tensp){
        if (tenSP == null) {
            throw new IllegalArgumentException("Tên sản phẩm không được để trống");
        }
        this.tenSP = tensp;
    }
    public void setloaiSP(String loaisp){ 
        if (loaisp == null || loaisp.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại sản phẩm không được để trống!");
        }
        String l = loaisp.trim().toUpperCase(); // Chuyển về chữ hoa để so sánh cho chính xác
        if (l.equals("DO_AN") || l.equals("DO_UONG") || l.equals("DUNG_CU")) {
            loaisp = l;
        }else {
             throw new IllegalArgumentException("Loại sản phẩm phải là: DO_AN, DO_UONG hoặc DUNG_CU!");
        }
        this.loaiSP = loaisp; 
    }
    public void setgiaBan(double giaban){
        if (giaban < 0) {
        throw new IllegalArgumentException("Giá bán không được nhỏ hơn 0!");
    }
        this.giaBan = giaban;
    }
    public void setsoLuongTon(int soluongton){
        if (soluongton < 0) {
        throw new IllegalArgumentException("Số lượng tồn không được nhỏ hơn 0!");
        }
        this.soLuongTon =  soluongton;
    }
    public void setmaNCC(String mancc){ this.maNCC = mancc; }

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