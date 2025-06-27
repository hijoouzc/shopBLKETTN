package models;

import java.util.Objects; // Import for Objects.hash and Objects.equals

public class ChiTietHoaDon {
    private int maChiTietHoaDon;
    private int maHoaDon; // Foreign Key
    private String maSanPham; // Foreign Key
    private int soLuong;
    private int thanhTien;

    public ChiTietHoaDon() {
        // Default constructor
    }

    public ChiTietHoaDon(int maChiTietHoaDon, int maHoaDon, String maSanPham, int soLuong, int thanhTien) {
        this.maChiTietHoaDon = maChiTietHoaDon;
        this.maHoaDon = maHoaDon;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.thanhTien = thanhTien;
    }

    // Constructor tiện lợi cho việc thêm chi tiết hóa đơn mới
    public ChiTietHoaDon(int maHoaDon, String maSanPham, int soLuong, int thanhTien) {
        this(0, maHoaDon, maSanPham, soLuong, thanhTien);
    }

    // Getters and Setters
    public int getMaChiTietHoaDon() {
        return maChiTietHoaDon;
    }

    public void setMaChiTietHoaDon(int maChiTietHoaDon) {
        this.maChiTietHoaDon = maChiTietHoaDon;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public int getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(int thanhTien) {
        this.thanhTien = thanhTien;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
               "maChiTietHoaDon=" + maChiTietHoaDon +
               ", maHoaDon=" + maHoaDon +
               ", maSanPham='" + maSanPham + '\'' +
               ", soLuong=" + soLuong +
               ", thanhTien=" + thanhTien +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return maChiTietHoaDon == that.maChiTietHoaDon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietHoaDon);
    }
}