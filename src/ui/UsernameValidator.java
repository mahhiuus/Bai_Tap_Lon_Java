package ui;

public final class UsernameValidator {
    private UsernameValidator() {
    }

    public static boolean isValid(String username) {
        return username != null && username.matches("[A-Za-z0-9_]+");
    }

    public static String message() {
        return "Tên đăng nhập chỉ được dùng chữ không dấu, số và dấu gạch dưới (_).";
    }
}
