package model;

public class NhaCungCap {
    private String maNCC;
    private String tenCongTy;
    private String sdt;
    private String diaChi;
    private String email;
    private String nguoiLienHe;

    public NhaCungCap() {}

    // Getter và Setter
    public String getMaNCC() { 
        return maNCC; 
    }
    public void setMaNCC(String maNCC) { 
        this.maNCC = maNCC; 
    }

    public String getTenCongTy() { 
        return tenCongTy; 
    }
    public void setTenCongTy(String tenCongTy) { 
        this.tenCongTy = tenCongTy; 
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

    public String getEmail() { 
        return email; 
    }
    public void setEmail(String email) { 
        this.email = email; 
    }

    public String getNguoiLienHe() { 
        return nguoiLienHe; 
    }
    public void setNguoiLienHe(String nguoiLienHe) { 
        this.nguoiLienHe = nguoiLienHe; 
    }

    @Override
    public String toString() {
        return "NhaCungCap {" + "Ma NCC: " + maNCC + " / Ten NCC: " + tenCongTy + " / SDT: " + sdt 
                + " / Dia chi: " + diaChi + " / Email: " + email + " / NguoiLienHe: " + nguoiLienHe + "}";
    }
}