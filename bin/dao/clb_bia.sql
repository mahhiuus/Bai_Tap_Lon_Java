-- Tạo database
CREATE DATABASE IF NOT EXISTS CLB_bia
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE CLB_bia;
-- =========================
-- BẢNG NHÂN VIÊN
-- =========================
CREATE TABLE IF NOT EXISTS nhan_vien (
    ma_nv       VARCHAR(10) PRIMARY KEY,
    ho_ten      VARCHAR(100) NOT NULL,
    sdt         VARCHAR(15),
    gioi_tinh   ENUM('Nam', 'Nu', 'Khac') NOT NULL,
    chuc_vu     VARCHAR(50),
    ngay_sinh   DATE
);

-- =========================
-- BẢNG TÀI KHOẢN
-- =========================
CREATE TABLE IF NOT EXISTS tai_khoan (
    ma_tk           VARCHAR(10) PRIMARY KEY,
    ten_dang_nhap   VARCHAR(50) NOT NULL UNIQUE,
    mat_khau        VARCHAR(100) NOT NULL,
    vai_tro         ENUM('ADMIN', 'NHANVIEN') NOT NULL,
    ma_nv           VARCHAR(10),
    FOREIGN KEY (ma_nv) REFERENCES nhan_vien(ma_nv)
);

-- =========================
-- BẢNG KHÁCH HÀNG
-- =========================
CREATE TABLE IF NOT EXISTS khach_hang (
    ma_kh         VARCHAR(10) PRIMARY KEY,
    ho_ten        VARCHAR(50) NOT NULL,
    sdt           VARCHAR(10),
    dia_chi       VARCHAR(100),
    diem_tich_luy INT DEFAULT 0 CHECK (diem_tich_luy >= 0),
    ngay_dang_ky  DATE
);

-- =========================
-- BẢNG NHÀ CUNG CẤP
-- =========================
CREATE TABLE IF NOT EXISTS nha_cung_cap (
    ma_ncc        VARCHAR(10) PRIMARY KEY,
    ten_cong_ty   VARCHAR(100) NOT NULL,
    sdt           VARCHAR(10),
    dia_chi       VARCHAR(255),
    email         VARCHAR(100),
    nguoi_lien_he VARCHAR(100)
);

-- =========================
-- BẢNG SẢN PHẨM
-- =========================
CREATE TABLE IF NOT EXISTS san_pham (
    ma_sp        VARCHAR(10) PRIMARY KEY,
    ten_sp       VARCHAR(100) NOT NULL,
    loai         ENUM('DO_AN', 'DO_UONG', 'DUNG_CU') NOT NULL,
    gia_ban      DECIMAL(12,2) NOT NULL CHECK (gia_ban >= 0),
    so_luong_ton INT DEFAULT 0 CHECK (so_luong_ton >= 0),
    ma_ncc       VARCHAR(10),
    FOREIGN KEY (ma_ncc) REFERENCES nha_cung_cap(ma_ncc)
);

-- =========================
-- BẢNG BÀN BIDA
-- =========================
CREATE TABLE IF NOT EXISTS ban_bida (
    ma_ban     VARCHAR(10) PRIMARY KEY,
    ten_ban    VARCHAR(50) NOT NULL,
    loai_ban   ENUM('THUONG', 'VIP') NOT NULL,
    trang_thai ENUM('TRONG', 'DANG_CHOI', 'DA_DAT', 'BAO_TRI') NOT NULL
);

-- =========================
-- BẢNG PHIÊN CHƠI
-- =========================
CREATE TABLE IF NOT EXISTS phien_choi (
    ma_phien           VARCHAR(10) PRIMARY KEY,
    ma_ban             VARCHAR(10) NOT NULL,
    thoi_gian_bat_dau  DATETIME NOT NULL,
    thoi_gian_ket_thuc DATETIME,
    trang_thai         ENUM('DANG_CHOI', 'DA_KET_THUC', 'DA_HUY') NOT NULL,
    FOREIGN KEY (ma_ban) REFERENCES ban_bida(ma_ban),
    CHECK (thoi_gian_ket_thuc IS NULL OR thoi_gian_ket_thuc >= thoi_gian_bat_dau)
);

-- =========================
-- BẢNG CHI TIẾT PHIÊN
-- =========================
CREATE TABLE IF NOT EXISTS chi_tiet_phien (
    ma_chi_tiet VARCHAR(10) PRIMARY KEY,
    ma_phien    VARCHAR(10) NOT NULL,
    ma_san_pham VARCHAR(10) NOT NULL,
    so_luong    INT NOT NULL CHECK (so_luong > 0),
    don_gia     DECIMAL(12,2) NOT NULL CHECK (don_gia >= 0),
    FOREIGN KEY (ma_phien) REFERENCES phien_choi(ma_phien),
    FOREIGN KEY (ma_san_pham) REFERENCES san_pham(ma_sp)
);

-- =========================
-- BẢNG HÓA ĐƠN NHẬP
-- =========================
CREATE TABLE IF NOT EXISTS hoa_don_nhap (
    ma_hdn    VARCHAR(10) PRIMARY KEY,
    ma_ncc    VARCHAR(10),
    ma_nv     VARCHAR(10),
    ngay_nhap DATE NOT NULL,
    tong_tien DECIMAL(12,2) DEFAULT 0 CHECK (tong_tien >= 0),
    ghi_chu   VARCHAR(255),
    FOREIGN KEY (ma_ncc) REFERENCES nha_cung_cap(ma_ncc),
    FOREIGN KEY (ma_nv) REFERENCES nhan_vien(ma_nv)
);

-- =========================
-- BẢNG CHI TIẾT HÓA ĐƠN NHẬP
-- =========================
CREATE TABLE IF NOT EXISTS chi_tiet_hoa_don_nhap (
    ma_chi_tiet  VARCHAR(10) PRIMARY KEY,
    ma_hdn       VARCHAR(10) NOT NULL,
    ma_sp        VARCHAR(10) NOT NULL,
    so_luong     INT NOT NULL CHECK (so_luong > 0),
    don_gia_nhap DECIMAL(12,2) NOT NULL CHECK (don_gia_nhap >= 0),
    FOREIGN KEY (ma_hdn) REFERENCES hoa_don_nhap(ma_hdn),
    FOREIGN KEY (ma_sp) REFERENCES san_pham(ma_sp)
);

-- =========================
-- BẢNG HÓA ĐƠN BÁN
-- =========================
CREATE TABLE IF NOT EXISTS hoa_don_ban (
    ma_hdb        VARCHAR(10) PRIMARY KEY,
    ma_phien      VARCHAR(10) UNIQUE,
    ma_kh         VARCHAR(10),
    ma_nv         VARCHAR(10),
    ngay_ban      DATE NOT NULL,
    tien_bida     DECIMAL(12,2) DEFAULT 0 CHECK (tien_bida >= 0),
    tien_san_pham DECIMAL(12,2) DEFAULT 0 CHECK (tien_san_pham >= 0),
    tong_tien     DECIMAL(12,2) DEFAULT 0 CHECK (tong_tien >= 0),
    ghi_chu       VARCHAR(255),
    FOREIGN KEY (ma_phien) REFERENCES phien_choi(ma_phien),
    FOREIGN KEY (ma_kh) REFERENCES khach_hang(ma_kh),
    FOREIGN KEY (ma_nv) REFERENCES nhan_vien(ma_nv)
);

-- =========================
-- BẢNG CHI TIẾT HÓA ĐƠN BÁN
-- =========================
CREATE TABLE IF NOT EXISTS chi_tiet_hoa_don_ban (
    ma_chi_tiet VARCHAR(10) PRIMARY KEY,
    ma_hdb      VARCHAR(10) NOT NULL,
    ma_sp       VARCHAR(10) NOT NULL,
    so_luong    INT NOT NULL CHECK (so_luong > 0),
    don_gia_ban DECIMAL(12,2) NOT NULL CHECK (don_gia_ban >= 0),
    FOREIGN KEY (ma_hdb) REFERENCES hoa_don_ban(ma_hdb),
    FOREIGN KEY (ma_sp) REFERENCES san_pham(ma_sp)
);