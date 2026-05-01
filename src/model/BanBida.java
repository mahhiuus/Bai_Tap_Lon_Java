package model;

public class BanBida {
    // Khai báo thuộc tính của BanBida
    private String maBan;
    private String tenBan;
    private String loaiBan;
    private String trangThaiBan;

    // Hàm khởi tạo không tham số
    public BanBida() {
        maBan = "";
        tenBan = "";
        loaiBan = "";
        trangThaiBan = "";
    }

    // Hàm khởi tạo có tham số
    public BanBida(String maBan, String tenBan, String loaiBan, String trangThaiBan) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.loaiBan = loaiBan;
        this.trangThaiBan = trangThaiBan;
    }

    // Getter
    public String getMaBan() {
        return maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public String getLoaiBan() {
        return loaiBan;
    }

    public String getTrangThaiBan() {
        return trangThaiBan;
    }

    // Setter
    public void setMaBan(String maBan) {
        if (maBan == null || maBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Mã bàn không được để trống");
        }
        this.maBan = maBan;
    }

    public void setTenBan(String tenBan) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bàn không được để trống");
        }
        this.tenBan = tenBan;
    }

    public void setLoaiBan(String loaiBan) {
        if (loaiBan == null || loaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại bàn không được để trống");
        }
        this.loaiBan = loaiBan;
    }

    public void setTrangThaiBan(String trangThaiBan) {
        if (trangThaiBan == null || trangThaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống");
        }
        this.trangThaiBan = trangThaiBan;
    }

    // Hàm toString() của BanBida
    @Override
    public String toString() {
        return "BanBida {"
             + "maBan = " + maBan + " / "
             + "tenBan = " + tenBan + " / "
             + "loaiBan = " + loaiBan + " / "
             + "trangThaiBan = " + trangThaiBan
             + "}";
    }
}