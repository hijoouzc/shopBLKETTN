package models;

import java.util.Date;
import java.util.Objects;

public class SaoLuu {
    private int maSaoLuu;
    private Date ngaySaoLuu;
    private String loaiSaoLuu;
    private String maNguoiDungThucHien; // Foreign Key
    private String viTriLuuTru;

    public SaoLuu() {
        // Default constructor
    }

    public SaoLuu(int maSaoLuu, Date ngaySaoLuu, String loaiSaoLuu, String maNguoiDungThucHien, String viTriLuuTru) {
        this.maSaoLuu = maSaoLuu;
        this.ngaySaoLuu = ngaySaoLuu;
        this.loaiSaoLuu = loaiSaoLuu;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.viTriLuuTru = viTriLuuTru;
    }

    // Constructor tiện lợi cho việc thêm bản sao lưu mới
    public SaoLuu(Date ngaySaoLuu, String loaiSaoLuu, String maNguoiDungThucHien, String viTriLuuTru) {
        this(0, ngaySaoLuu, loaiSaoLuu, maNguoiDungThucHien, viTriLuuTru);
    }

    // Getters and Setters
    public int getMaSaoLuu() {
        return maSaoLuu;
    }

    public void setMaSaoLuu(int maSaoLuu) {
        this.maSaoLuu = maSaoLuu;
    }

    public Date getNgaySaoLuu() {
        return ngaySaoLuu;
    }

    public void setNgaySaoLuu(Date ngaySaoLuu) {
        this.ngaySaoLuu = ngaySaoLuu;
    }

    public String getLoaiSaoLuu() {
        return loaiSaoLuu;
    }

    public void setLoaiSaoLuu(String loaiSaoLuu) {
        this.loaiSaoLuu = loaiSaoLuu;
    }

    public String getMaNguoiDungThucHien() {
        return maNguoiDungThucHien;
    }

    public void setMaNguoiDungThucHien(String maNguoiDungThucHien) {
        this.maNguoiDungThucHien = maNguoiDungThucHien;
    }

    public String getViTriLuuTru() {
        return viTriLuuTru;
    }

    public void setViTriLuuTru(String viTriLuuTru) {
        this.viTriLuuTru = viTriLuuTru;
    }

    @Override
    public String toString() {
        return "SaoLuu{" +
               "maSaoLuu=" + maSaoLuu +
               ", ngaySaoLuu=" + ngaySaoLuu +
               ", loaiSaoLuu='" + loaiSaoLuu + '\'' +
               ", maNguoiDungThucHien='" + maNguoiDungThucHien + '\'' +
               ", viTriLuuTru='" + viTriLuuTru + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaoLuu saoLuu = (SaoLuu) o;
        return maSaoLuu == saoLuu.maSaoLuu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSaoLuu);
    }
}