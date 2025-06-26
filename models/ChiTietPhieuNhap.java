package models;

import java.util.Objects;

public class ChiTietPhieuNhap {
    private int maChiTietPhieuNhap;
    private int maPhieuNhap; // Foreign Key
    private String maSanPham; // Foreign Key
    private int soLuong;
    private int donGiaNhap;

    public ChiTietPhieuNhap() {
        // Default constructor
    }

    public ChiTietPhieuNhap(int maChiTietPhieuNhap, int maPhieuNhap, String maSanPham, int soLuong, int donGiaNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
        this.maPhieuNhap = maPhieuNhap;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    // Constructor tiện lợi cho việc thêm chi tiết phiếu nhập mới
    public ChiTietPhieuNhap(int maPhieuNhap, String maSanPham, int soLuong, int donGiaNhap) {
        this(0, maPhieuNhap, maSanPham, soLuong, donGiaNhap);
    }

    // Getters and Setters
    public int getMaChiTietPhieuNhap() {
        return maChiTietPhieuNhap;
    }

    public void setMaChiTietPhieuNhap(int maChiTietPhieuNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
    }

    public int getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(int maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
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

    public int getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(int donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }
    
    public int getThanhTien() {
        return this.soLuong * this.donGiaNhap;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{" +
               "maChiTietPhieuNhap=" + maChiTietPhieuNhap +
               ", maPhieuNhap=" + maPhieuNhap +
               ", maSanPham='" + maSanPham + '\'' +
               ", soLuong=" + soLuong +
               ", donGiaNhap=" + donGiaNhap +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietPhieuNhap that = (ChiTietPhieuNhap) o;
        return maChiTietPhieuNhap == that.maChiTietPhieuNhap;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietPhieuNhap);
    }
    
    
}
