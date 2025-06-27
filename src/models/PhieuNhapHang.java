package models; // Updated package

import java.util.Date;
import java.util.Objects;

public class PhieuNhapHang {
    private int maPhieuNhap;
    private Date ngayNhap;
    private String maNhanVienThucHien; // Foreign Key

    public PhieuNhapHang() {
        // Default constructor
    }

    public PhieuNhapHang(int maPhieuNhap, Date ngayNhap, String maNhanVienThucHien) {
        this.maPhieuNhap = maPhieuNhap;
        this.ngayNhap = ngayNhap;
        this.maNhanVienThucHien = maNhanVienThucHien;
    }

    // Constructor tiện lợi cho việc thêm phiếu nhập hàng mới
    public PhieuNhapHang(Date ngayNhap, String maNhanVienThucHien) {
        this(0, ngayNhap, maNhanVienThucHien);
    }

    // Getters and Setters
    public int getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(int maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public String getMaNhanVienThucHien() {
        return maNhanVienThucHien;
    }

    public void setMaNhanVienThucHien(String maNhanVienThucHien) {
        this.maNhanVienThucHien = maNhanVienThucHien;
    }

    @Override
    public String toString() {
        return "PhieuNhapHang{" +
               "maPhieuNhap=" + maPhieuNhap +
               ", ngayNhap=" + ngayNhap +
               ", maNhanVienThucHien='" + maNhanVienThucHien + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhieuNhapHang that = (PhieuNhapHang) o;
        return maPhieuNhap == that.maPhieuNhap;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }
}