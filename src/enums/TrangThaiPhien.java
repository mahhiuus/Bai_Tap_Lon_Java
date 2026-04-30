package enums;

public enum TrangThaiPhien {
    DANG_CHOI("Đang chơi"),
    DA_KET_THUC("Đã kết thúc");

    private final String label;

    TrangThaiPhien(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}