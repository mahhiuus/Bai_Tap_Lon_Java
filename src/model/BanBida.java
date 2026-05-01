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
        setMaBan(maBan);
        setTenBan(tenBan);
        setLoaiBan(loaiBan);
        setTrangThaiBan(trangThaiBan);
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
        this.maBan = maBan.trim();
    }

    public void setTenBan(String tenBan) {
        if (tenBan == null || tenBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên bàn không được để trống");
        }
        this.tenBan = tenBan.trim();
    }

    public void setLoaiBan(String loaiBan) {
        if (loaiBan == null || loaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Loại bàn không được để trống");
        }

        loaiBan = loaiBan.trim().toUpperCase();

        if (!loaiBan.equals("THUONG") && !loaiBan.equals("VIP")) {
            throw new IllegalArgumentException("Loại bàn chỉ được là THUONG hoặc VIP");
        }

        this.loaiBan = loaiBan;
    }

    public void setTrangThaiBan(String trangThaiBan) {
        if (trangThaiBan == null || trangThaiBan.trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái bàn không được để trống");
        }
        this.trangThaiBan = trangThaiBan.trim();
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