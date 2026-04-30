package session;

import model.TaiKhoan;

public class SessionManager {
    /*Khai báo thuộc tính của SessionManager
    Lớp SessionManager dùng để kiểm tra quyền ADMIN nên phải khai báo biến kiểu TaiKhoan*/
    private static TaiKhoan taiKhoanHienTai;

    /*Hàm DangNhap dùng để gán thông tin tài khoản khi người dùng đăng nhập
    Lưu thông tin người vừa đăng nhập vào Database*/
    public static void DangNhap(TaiKhoan tk) {
        taiKhoanHienTai = tk;
    }

    /*Hàm DangXuat dùng để gán thông tin tài khoản khi người dùng đăng xuất
    Xóa thông tin người đã đăng xuất*/
    public static void DangXuat() {
        taiKhoanHienTai = null;
    }

    /*Kiểm tra xem có người dùng nào đang đăng nhập không
    Nếu có trả về thông tin tài khoản*/
    public static TaiKhoan getTaiKhoan() {
        return taiKhoanHienTai;
    }

    /*Hàm kiểm tra quyền admin
    TRUE nếu thỏa mãn (Đang có người dùng đăng nhập) và (Giữ vai trò admin)*/
    public static boolean isAdmin() {
        return taiKhoanHienTai != null && "ADMIN".equals(taiKhoanHienTai.getVaiTro());
    }
}
