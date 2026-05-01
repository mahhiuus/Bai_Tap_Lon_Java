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
-- ==========================================
-- 1. DỮ LIỆU NHÂN VIÊN & TÀI KHOẢN
-- ==========================================
INSERT INTO nhan_vien (ma_nv, ho_ten, sdt, gioi_tinh, chuc_vu, ngay_sinh) VALUES
('NV01', 'Nguyen Van A', '0123456789', 'Nam', 'Quan ly', '2000-01-01'),
('NV02', 'Tran Thi B', '0987654321', 'Nu', 'Nhan vien', '2002-05-10');

INSERT INTO tai_khoan (ma_tk, ten_dang_nhap, mat_khau, vai_tro, ma_nv) VALUES
('TK01', 'admin', 'admin123', 'ADMIN', 'NV01'),
('TK02', 'nhanvien', '123456', 'NHANVIEN', 'NV02');

-- ==========================================
-- 2. KHÁCH HÀNG & NHÀ CUNG CẤP
-- ==========================================
INSERT INTO khach_hang (ma_kh, ho_ten, sdt, dia_chi, diem_tich_luy, ngay_dang_ky) VALUES
('KH01', 'Le Van C', '0911111111', 'Ha Noi', 0, '2026-05-01'),
('KH02', 'Hoang Minh D', '0922222222', 'Ha Noi', 50, '2026-05-01');

INSERT INTO nha_cung_cap (ma_ncc, ten_cong_ty, sdt, dia_chi, email, nguoi_lien_he) VALUES
('NCC01', 'Cong ty Nuoc Giai Khat ABC', '0900000000', 'Ha Noi', 'abc@gmail.com', 'Anh Nam'),
('NCC02', 'Phu Kien Bida Thinh', '0933333333', 'TP HCM', 'thinhbida@gmail.com', 'Chi Thoa');

-- ==========================================
-- 3. SẢN PHẨM (ĐỒ ĂN, ĐỒ UỐNG, DỤNG CỤ)
-- ==========================================
INSERT INTO san_pham (ma_sp, ten_sp, loai, gia_ban, so_luong_ton, ma_ncc) VALUES
('SP01', 'Nuoc suoi', 'DO_UONG', 10000, 50, 'NCC01'),
('SP02', 'Coca Cola', 'DO_UONG', 15000, 40, 'NCC01'),
('SP03', 'Bim bim', 'DO_AN', 12000, 30, 'NCC01'),
('SP04', 'Bao tay bida', 'DUNG_CU', 20000, 15, 'NCC02');

-- ==========================================
-- 4. QUẢN LÝ NHẬP KHO (HÓA ĐƠN NHẬP)
-- ==========================================
INSERT INTO hoa_don_nhap (ma_hdn, ma_ncc, ma_nv, ngay_nhap, tong_tien, ghi_chu) VALUES 
('HDN01', 'NCC01', 'NV01', '2026-05-01', 500000, 'Nhap hang dau thang 5');

INSERT INTO chi_tiet_hoa_don_nhap (ma_chi_tiet, ma_hdn, ma_sp, so_luong, don_gia_nhap) VALUES 
('CTN01', 'HDN01', 'SP01', 30, 7000),
('CTN02', 'HDN01', 'SP02', 20, 10000);

-- ==========================================
-- 5. TRẠNG THÁI BÀN & PHIÊN CHƠI
-- ==========================================
INSERT INTO ban_bida (ma_ban, ten_ban, loai_ban, trang_thai) VALUES
('B01', 'Ban 1', 'THUONG', 'DANG_CHOI'),
('B02', 'Ban 2', 'VIP', 'TRONG'),
('B03', 'Ban 3', 'THUONG', 'BAO_TRI');

-- Phiên 1: Đang diễn ra
INSERT INTO phien_choi (ma_phien, ma_ban, thoi_gian_bat_dau, thoi_gian_ket_thuc, trang_thai) VALUES
('P01', 'B01', '2026-05-01 09:00:00', NULL, 'DANG_CHOI');

-- Đồ khách gọi thêm trong phiên P01
INSERT INTO chi_tiet_phien (ma_chi_tiet, ma_phien, ma_san_pham, so_luong, don_gia) VALUES
('CT01', 'P01', 'SP01', 2, 10000),
('CT02', 'P01', 'SP02', 1, 15000);

-- ==========================================
-- 6. THANH TOÁN (HÓA ĐƠN BÁN)
-- ==========================================
-- Giả sử phiên P01 kết thúc lúc 11:00 (chơi 2 tiếng)
UPDATE phien_choi 
SET thoi_gian_ket_thuc = '2026-05-01 11:00:00', trang_thai = 'DA_KET_THUC' 
WHERE ma_phien = 'P01';

-- Cập nhật lại trạng thái bàn sau khi khách về
UPDATE ban_bida SET trang_thai = 'TRONG' WHERE ma_ban = 'B01';

-- Xuất hóa đơn bán cho khách KH01
INSERT INTO hoa_don_ban (ma_hdb, ma_phien, ma_kh, ma_nv, ngay_ban, tien_bida, tien_san_pham, tong_tien, ghi_chu) VALUES 
('HDB01', 'P01', 'KH01', 'NV02', '2026-05-01', 100000, 35000, 135000, 'Khach thanh toan du');

INSERT INTO chi_tiet_hoa_don_ban (ma_chi_tiet, ma_hdb, ma_sp, so_luong, don_gia_ban) VALUES 
('CTB01', 'HDB01', 'SP01', 2, 10000),
('CTB02', 'HDB01', 'SP02', 1, 15000);