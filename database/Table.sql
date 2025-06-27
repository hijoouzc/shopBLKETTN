CREATE TABLE TaiKhoanNguoiDung (
    -- Sử dụng computed column cho mã dạng TK0001
    InternalID INT IDENTITY(1,1) NOT NULL, 
    MaNguoiDung AS ('TK' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    Username NVARCHAR(50) NOT NULL UNIQUE ,
    Password NVARCHAR(255) NOT NULL,
    Email NVARCHAR(100) NOT NULL UNIQUE,
    LoaiNguoiDung NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_LoaiNguoiDung DEFAULT N'Khách hàng',
    NgayTao DATETIME NOT NULL CONSTRAINT DF_TaiKhoan_NgayTao DEFAULT CURRENT_TIMESTAMP,
    TrangThaiTaiKhoan NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT N'Hoạt động',
    
    -- Các ràng buộc
    CONSTRAINT CK_TaiKhoan_LoaiNguoiDung 
        CHECK (LoaiNguoiDung IN (N'Admin', N'Nhân viên', N'Khách hàng')),
    CONSTRAINT CK_TaiKhoan_TrangThai
        CHECK (TrangThaiTaiKhoan IN (N'Hoạt động', N'Bị khóa', N'Vô hiệu hóa'))
);

CREATE TABLE KhachHang (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaKhachHang AS ('KH' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    MaNguoiDung VARCHAR(5) NULL,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NULL,
    GioiTinh NVARCHAR(10) NULL,
    SDT NVARCHAR(15) NOT NULL UNIQUE,
    
    -- Ràng buộc khóa ngoại
    CONSTRAINT FK_KhachHang_TaiKhoan FOREIGN KEY (MaNguoiDung) 
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung),

    -- Ràng buộc kiểm tra
    CONSTRAINT CK_KhachHang_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CONSTRAINT CK_KhachHang_NgaySinh CHECK (NgaySinh <= GETDATE()),
    CONSTRAINT CK_KhachHang_SDT CHECK (LEN(SDT) >= 9 AND SDT NOT LIKE '%[^0-9]%'),
);
-- Ràng buộc duy nhất
CREATE UNIQUE INDEX UQ_KhachHang_MaNguoiDung
ON KhachHang(MaNguoiDung)
WHERE MaNguoiDung IS NOT NULL;

-- Tạo bảng Nhân viên
CREATE TABLE NhanVien (
    InternalID INT IDENTITY(1,1) NOT NULL,
    MaNhanVien AS ('NV' + RIGHT('000' + CAST(InternalID AS VARCHAR(10)), 3)) PERSISTED PRIMARY KEY,
    MaNguoiDung VARCHAR(5) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NOT NULL,
    GioiTinh NVARCHAR(10) NOT NULL,
    CCCD NVARCHAR(20) NOT NULL UNIQUE,
    SDT NVARCHAR(15) NOT NULL UNIQUE,
    Luong INT NOT NULL,
    TrangThaiLamViec NVARCHAR(20) NOT NULL CONSTRAINT DF_TaiKhoan_LamViec DEFAULT N'Hoạt động',

	CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (MaNguoiDung) 
		REFERENCES TaiKhoanNguoiDung(MaNguoiDung),
	-- Ràng buộc kiểm tra
    CONSTRAINT CK_NhanVien_GioiTinh CHECK (GioiTinh IN (N'Nam', N'Nữ', N'Khác')),
    CONSTRAINT CK_NhanVien_NgaySinh CHECK (NgaySinh <= GETDATE()),
    CONSTRAINT CK_NhanVien_SDT CHECK (LEN(SDT) >= 9 AND SDT NOT LIKE '%[^0-9]%'),

	CONSTRAINT CK_TaiKhoan_LamViec
		CHECK (TrangThaiLamViec IN (N'Hoạt động', N'NGhỉ phép', N'Đã nghỉ việc'))
);

CREATE UNIQUE INDEX UQ_NhanVien_MaNguoiDung 
ON NhanVien(MaNguoiDung) 
WHERE MaNguoiDung IS NOT NULL;

-- Tạo bảng Loại sản phẩm/linh kiện
CREATE TABLE LoaiSanPham (
	InternalID INT IDENTITY(1,1) NOT NULL, 
    MaLoaiSanPham AS ('LSP' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenLoaiSanPham NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(500) NULL
);

-- Tạo bảng Sản phẩm/linh kiện
CREATE TABLE SanPham (
	InternalID INT IDENTITY(1,1) NOT NULL, 
    MaSanPham AS ('SP' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenSanPham NVARCHAR(100) NOT NULL,
    DonGia INT NOT NULL,
    NgaySanXuat DATE NULL,
    ThongSoKyThuat NVARCHAR(MAX) NULL,
    MaLoaiSanPham VARCHAR(7) NOT NULL,
    FOREIGN KEY (MaLoaiSanPham) REFERENCES LoaiSanPham(MaLoaiSanPham)
);

-- Tạo bảng Vị trí đựng sản phẩm
CREATE TABLE ViTriDungSanPham (
	InternalID INT IDENTITY(1,1) NOT NULL, 
    MaNganDung AS ('N' + RIGHT('0000' + CAST(InternalID AS VARCHAR(10)), 4)) PERSISTED PRIMARY KEY,
    TenNganDung NVARCHAR(100) NOT NULL
);

-- Tạo bảng Chi tiết vị trí
CREATE TABLE ChiTietViTri (
    MaChiTietViTri INT PRIMARY KEY IDENTITY(1,1),
    MaNganDung VARCHAR(5) NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL DEFAULT 0,
    FOREIGN KEY (MaNganDung) REFERENCES ViTriDungSanPham(MaNganDung),
    FOREIGN KEY (MaSanPham) REFERENCES SanPham(MaSanPham),
    CONSTRAINT UC_ChiTietViTri UNIQUE (MaNganDung, MaSanPham)
);

-- Tạo bảng Hóa đơn
CREATE TABLE HoaDon (
    MaHoaDon INT PRIMARY KEY IDENTITY(1,1),
    MaKhachHang VARCHAR(5) NULL,
    TenKhachHangVangLai NVARCHAR(100) NULL,
    SDTKhachHangVangLai NVARCHAR(15) NULL,
    MaNhanVienLap VARCHAR(5) NOT NULL,
    NgayBan DATETIME NOT NULL CONSTRAINT DF_HoaDon_NgayBan DEFAULT CURRENT_TIMESTAMP,
    PhuongThucThanhToan NVARCHAR(50) NOT NULL,
    -- Ràng buộc khóa ngoại 
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (MaKhachHang) 
        REFERENCES KhachHang(MaKhachHang),   
    CONSTRAINT FK_HoaDon_NhanVienLap FOREIGN KEY (MaNhanVienLap) 
        REFERENCES NhanVien(MaNhanVien),
	-- Ràng buộc kiểm tra cho PhuongThucThanhToan
    CONSTRAINT CK_HoaDon_PhuongThucThanhToan 
        CHECK (PhuongThucThanhToan IN (N'Tiền mặt', N'Chuyển khoản', N'Thẻ tín dụng', N'Khác')),
	-- Ràng buộc để đảm bảo MaKhachHang hoặc TenKhachHangVangLai/SDTKhachHangVangLai phải có
	CONSTRAINT CK_HoaDon_KhachHangInfo CHECK (
		(MaKhachHang IS NOT NULL AND TenKhachHangVangLai IS NULL AND SDTKhachHangVangLai IS NULL) OR
		(MaKhachHang IS NULL AND TenKhachHangVangLai IS NOT NULL AND SDTKhachHangVangLai IS NOT NULL)
		)
);

-- Tạo bảng Chi tiết hóa đơn
CREATE TABLE ChiTietHoaDon (
    MaChiTietHoaDon INT PRIMARY KEY IDENTITY(1,1),
    MaHoaDon INT NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL CONSTRAINT CK_ChiTietHoaDon_SoLuong CHECK (SoLuong > 0),
	ThanhTien INT NOT NULL CONSTRAINT CK_ChiTietHoaDon_ThanhTien CHECK (ThanhTien >= 0),
	    -- Ràng buộc khóa ngoại
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (MaHoaDon) 
        REFERENCES HoaDon(MaHoaDon),
    CONSTRAINT FK_ChiTietHoaDon_SanPham FOREIGN KEY (MaSanPham) 
        REFERENCES SanPham(MaSanPham),
	-- Ràng buộc duy nhất cho cặp (MaHoaDon, MaSanPham)
	CONSTRAINT UQ_ChiTietHoaDon_HoaDon_SanPham UNIQUE (MaHoaDon, MaSanPham)
);

-- Tạo bảng Phiếu nhập hàng
CREATE TABLE PhieuNhapHang (
    MaPhieuNhap INT PRIMARY KEY IDENTITY(1,1),
    NgayNhap DATETIME NOT NULL CONSTRAINT DF_PhieuNHap_NgayNhap DEFAULT CURRENT_TIMESTAMP,
    MaNhanVienThucHien VARCHAR(5) NOT NULL,
	CONSTRAINT FK_PhieuNhapHang_NhanVien FOREIGN KEY (MaNhanVienThucHien)
		REFERENCES NhanVien(MaNhanVien)
);

-- Tạo bảng Chi tiết phiếu nhập
CREATE TABLE ChiTietPhieuNhap (
    MaChiTietPhieuNhap INT PRIMARY KEY IDENTITY(1,1),
    MaPhieuNhap INT NOT NULL,
    MaSanPham VARCHAR(6) NOT NULL,
    SoLuong INT NOT NULL CONSTRAINT CK_ChiTietPhieuNhap_SoLuong CHECK (SoLuong > 0),
    DonGiaNhap INT NOT NULL CONSTRAINT CK_ChiTietPhieuNhap_DonGiaNhap CHECK (DonGiaNhap >= 0),
    CONSTRAINT FK_ChiTietPhieuNhap_PhieuNhap FOREIGN KEY (MaPhieuNhap) 
        REFERENCES PhieuNhapHang(MaPhieuNhap),
    CONSTRAINT FK_ChiTietPhieuNhap_SanPham FOREIGN KEY (MaSanPham) 
        REFERENCES SanPham(MaSanPham),
    -- Ràng buộc duy nhất cho cặp (MaPhieuNhap, MaSanPham)
	CONSTRAINT UQ_ChiTietPhieuNhap_PhieuNhap_SanPham UNIQUE (MaPhieuNhap, MaSanPham)
);

-- Tạo bảng SaoLuu
CREATE TABLE SaoLuu (
    MaSaoLuu INT PRIMARY KEY IDENTITY(1,1),
    NgaySaoLuu DATETIME NOT NULL CONSTRAINT DF_SaoLuu_NgaySaoLuu DEFAULT CURRENT_TIMESTAMP,
    LoaiSaoLuu NVARCHAR(50) NOT NULL CONSTRAINT CK_SaoLuu_LoaiSaoLuu CHECK (LoaiSaoLuu IN (N'Full', N'Differential', N'Log', N'Incremental', N'Khác')),
    
    -- Đã sửa kiểu dữ liệu của MaNguoiDungThucHien thành VARCHAR(50)
    MaNguoiDungThucHien VARCHAR(5) NOT NULL, 
    ViTriLuuTru NVARCHAR(255) NOT NULL,
    
    CONSTRAINT FK_SaoLuu_TaiKhoanNguoiDung FOREIGN KEY (MaNguoiDungThucHien) 
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung)
 
);

-- Tạo bảng PhucHoi
CREATE TABLE PhucHoi (
    MaPhucHoi INT PRIMARY KEY IDENTITY(1,1),
    NgayPhucHoi DATETIME NOT NULL CONSTRAINT DF_PhucHoi_NgayPhucHoi DEFAULT CURRENT_TIMESTAMP,
    LoaiPhucHoi NVARCHAR(50) NOT NULL CONSTRAINT CK_PhucHoi_LoaiPhucHoi CHECK (LoaiPhucHoi IN (N'Full', N'Differential', N'Log', N'Khác')),
    
    -- Đã sửa kiểu dữ liệu của MaNguoiDungThucHien thành VARCHAR(50)
    MaNguoiDungThucHien VARCHAR(5) NOT NULL, 
    MaSaoLuu INT NOT NULL,
    
    CONSTRAINT FK_PhucHoi_TaiKhoanNguoiDung FOREIGN KEY (MaNguoiDungThucHien) 
        REFERENCES TaiKhoanNguoiDung(MaNguoiDung),
    
    CONSTRAINT FK_PhucHoi_SaoLuu FOREIGN KEY (MaSaoLuu) 
        REFERENCES SaoLuu(MaSaoLuu)
);

/*-- Xóa các bảng có khóa ngoại trước
DROP TABLE IF EXISTS ChiTietHoaDon;
DROP TABLE IF EXISTS HoaDon;
DROP TABLE IF EXISTS ChiTietPhieuNhap;
DROP TABLE IF EXISTS PhieuNhapHang;
DROP TABLE IF EXISTS ChiTietViTri;
DROP TABLE IF EXISTS PhucHoi;
DROP TABLE IF EXISTS SaoLuu;

-- Sau đó xóa các bảng được tham chiếu
DROP TABLE IF EXISTS SanPham;
DROP TABLE IF EXISTS ViTriDungSanPham;
DROP TABLE IF EXISTS LoaiSanPham;
DROP TABLE IF EXISTS NhanVien;
DROP TABLE IF EXISTS KhachHang;
DROP TABLE IF EXISTS TaiKhoanNguoiDung;*/