package models;

import java.util.Date;
import java.util.Objects;

public class PhucHoi {
    private int maPhucHoi;
    private Date ngayPhucHoi;
    private String loaiPhucHoi;
    private String maNguoiDungThucHien; // Foreign Key
    private int maSaoLuu; // Foreign Key

    public PhucHoi() {
        // Default constructor
    }

    public PhucHoi(int maPhucHoi, Date ngayPhucHoi, String loaiPhucHoi, String maNguoiDungThucHien, int maSaoLuu) {
        this.maPhucHoi = maPhucHoi;
        this.ngayPhucHoi = ngayPhucHoi;
        this.loaiPhucHoi = loaiPhucHoi;
        this.maNguoiDungThucHien = maNguoiDungThucHien;
        this.maSaoLuu = maSaoLuu;
    }

    // Constructor tiện lợi cho việc thêm bản phục hồi mới
    public PhucHoi(Date ngayPhucHoi, String loaiPhucHoi, String maNguoiDungThucHien, int maSaoLuu) {
        this(0, ngayPhucHoi, loaiPhucHoi, maNguoiDungThucHien, maSaoLuu);
    }

    // Getters and Setters
    public int getMaPhucHoi() {
        return maPhucHoi;
    }

    public void setMaPhucHoi(int maPhucHoi) {
        this.maPhucHoi = maPhucHoi;
    }

    public Date getNgayPhucHoi() {
        return ngayPhucHoi;
    }

    public void setNgayPhucHoi(Date ngayPhucHoi) {
        this.ngayPhucHoi = ngayPhucHoi;
    }

    public String getLoaiPhucHoi() {
        return loaiPhucHoi;
    }

    public void setLoaiPhucHoi(String loaiPhucHoi) {
        this.loaiPhucHoi = loaiPhucHoi;
    }

    public String getMaNguoiDungThucHien() {
        return maNguoiDungThucHien;
    }

    public void setMaNguoiDungThucHien(String maNguoiDungThucHien) {
        this.maNguoiDungThucHien = maNguoiDungThucHien;
    }

    public int getMaSaoLuu() {
        return maSaoLuu;
    }

    public void setMaSaoLuu(int maSaoLuu) {
        this.maSaoLuu = maSaoLuu;
    }

    @Override
    public String toString() {
        return "PhucHoi{" +
               "maPhucHoi=" + maPhucHoi +
               ", ngayPhucHoi=" + ngayPhucHoi +
               ", loaiPhucHoi='" + loaiPhucHoi + '\'' +
               ", maNguoiDungThucHien='" + maNguoiDungThucHien + '\'' +
               ", maSaoLuu=" + maSaoLuu +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhucHoi phucHoi = (PhucHoi) o;
        return maPhucHoi == phucHoi.maPhucHoi;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhucHoi);
    }
}
