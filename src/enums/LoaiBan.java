package enums;

public enum LoaiBan {
    LO("Bàn lỗ"),
    A("Bàn A"),
    VIP("Bàn VIP");

    private final String label;

    LoaiBan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}