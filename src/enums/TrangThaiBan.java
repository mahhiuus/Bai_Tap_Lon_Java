package enums;

public enum TrangThaiBan {
    TRONG("Trống"),
    DANG_SU_DUNG("Đang sử dụng"),
    BAO_TRI("Bảo trì");

    private final String label;

    TrangThaiBan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}