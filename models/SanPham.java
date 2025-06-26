package models;

import java.util.Date;
import java.util.Objects;

public class SanPham {
    private int internalID;
    private String maSanPham;
    private String tenSanPham;
    private int donGia;
    private Date ngaySanXuat; // Can be null
    private String thongSoKyThuat; // Can be null (NVARCHAR(MAX) implies potential large string)
    private String maLoaiSanPham; // Foreign Key
    private int soLuongTon; // Thêm thuộc tính này

    public SanPham() {
        // Default constructor
    }

    public SanPham(int internalID, String maSanPham, String tenSanPham, int donGia, Date ngaySanXuat, String thongSoKyThuat, String maLoaiSanPham, int soLuongTon) {
        this.internalID = internalID;
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.donGia = donGia;
        this.ngaySanXuat = ngaySanXuat;
        this.thongSoKyThuat = thongSoKyThuat;
        this.maLoaiSanPham = maLoaiSanPham;
        this.soLuongTon = soLuongTon;
    }

    // Constructor tiện lợi cho việc thêm sản phẩm mới
    public SanPham(String tenSanPham, int donGia, Date ngaySanXuat, String thongSoKyThuat, String maLoaiSanPham, int soLuongTon) {
        this(0, null, tenSanPham, donGia, ngaySanXuat, thongSoKyThuat, maLoaiSanPham, soLuongTon);
    }

    // Getters and Setters
    public int getInternalID() {
        return internalID;
    }

    public void setInternalID(int internalID) {
        this.internalID = internalID;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public int getDonGia() {
        return donGia;
    }

    public void setDonGia(int donGia) {
        this.donGia = donGia;
    }

    public Date getNgaySanXuat() {
        return ngaySanXuat;
    }

    public void setNgaySanXuat(Date ngaySanXuat) {
        this.ngaySanXuat = ngaySanXuat;
    }

    public String getThongSoKyThuat() {
        return thongSoKyThuat;
    }

    public void setThongSoKyThuat(String thongSoKyThuat) {
        this.thongSoKyThuat = thongSoKyThuat;
    }

    public String getMaLoaiSanPham() {
        return maLoaiSanPham;
    }

    public void setMaLoaiSanPham(String maLoaiSanPham) {
        this.maLoaiSanPham = maLoaiSanPham;
    }
    
    public int getSoLuongTon() { 
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) { 
        this.soLuongTon = soLuongTon;
    }
    
    

    @Override
    public String toString() {
        return "SanPham{" +
               "internalID=" + internalID +
               ", maSanPham='" + maSanPham + '\'' +
               ", tenSanPham='" + tenSanPham + '\'' +
               ", donGia=" + donGia +
               ", ngaySanXuat=" + ngaySanXuat +
               ", thongSoKyThuat='" + thongSoKyThuat + '\'' +
               ", maLoaiSanPham='" + maLoaiSanPham + '\'' +
               ", soLuongTon=" + soLuongTon + // Thêm soLuongTon vào toString
               '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return internalID == sanPham.internalID &&
               Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalID, maSanPham);
    }
}