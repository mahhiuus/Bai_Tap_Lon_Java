package ui;

public final class ValidationUtils {
    public static final int MIN_PASSWORD_LENGTH = 8;

    private ValidationUtils() {
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= MIN_PASSWORD_LENGTH;
    }

    public static String passwordMessage() {
        return "Mật khẩu phải có ít nhất 8 ký tự. Vui lòng đổi mật khẩu.";
    }

    public static boolean isPhone(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    public static boolean isEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isPersonName(String name) {
        return name != null && name.trim().matches("[\\p{L} ]{2,}");
    }

    public static int parsePositiveInt(String text, String fieldName) {
        try {
            int value = Integer.parseInt(cleanNumber(text));
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " phải lớn hơn 0!");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên hợp lệ!");
        }
    }

    public static int parseNonNegativeInt(String text, String fieldName) {
        try {
            int value = Integer.parseInt(cleanNumber(text));
            if (value < 0) {
                throw new IllegalArgumentException(fieldName + " không được âm!");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên hợp lệ!");
        }
    }

    public static double parsePositiveMoney(String text, String fieldName) {
        try {
            double value = Double.parseDouble(cleanNumber(text));
            if (value <= 0) {
                throw new IllegalArgumentException(fieldName + " phải lớn hơn 0!");
            }
            if (value > 1_000_000_000D) {
                throw new IllegalArgumentException(fieldName + " không được vượt quá 1,000,000,000!");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " phải là số tiền hợp lệ!");
        }
    }

    public static String cleanNumber(String text) {
        return text == null ? "" : text.replace(",", "").replace(".", "").trim();
    }
}
