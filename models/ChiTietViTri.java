package models;

import java.util.Objects;

public class ChiTietViTri {
    private int maChiTietViTri;
    private String maNganDung;
    private String maSanPham;
    private int soLuong;

    public ChiTietViTri() {
        // Default constructor
    }

    public ChiTietViTri(int maChiTietViTri, String maNganDung, String maSanPham, int soLuong) {
        this.maChiTietViTri = maChiTietViTri;
        this.maNganDung = maNganDung;
        this.maSanPham = maSanPham;
        this.soLuong = soLuong;
    }

    // Constructor tiện lợi cho việc thêm chi tiết vị trí mới
    public ChiTietViTri(String maNganDung, String maSanPham, int soLuong) {
        this(0, maNganDung, maSanPham, soLuong);
    }

    // Getters and Setters
    public int getMaChiTietViTri() {
        return maChiTietViTri;
    }

    public void setMaChiTietViTri(int maChiTietViTri) {
        this.maChiTietViTri = maChiTietViTri;
    }

    public String getMaNganDung() {
        return maNganDung;
    }

    public void setMaNganDung(String maNganDung) {
        this.maNganDung = maNganDung;
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

    @Override
    public String toString() {
        return "ChiTietViTri{" +
               "maChiTietViTri=" + maChiTietViTri +
               ", maNganDung='" + maNganDung + '\'' +
               ", maSanPham='" + maSanPham + '\'' +
               ", soLuong=" + soLuong +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietViTri that = (ChiTietViTri) o;
        return maChiTietViTri == that.maChiTietViTri;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maChiTietViTri);
    }
}