package models;

import java.util.Date;
import java.util.Objects;

public class KhachHang {
    private int internalID;
    private String maKhachHang;
    private String maNguoiDung; // Foreign Key, can be null
    private String hoTen;
    private Date ngaySinh; // Can be null
    private String gioiTinh; // Can be null
    private String sdt;

    public KhachHang() {
        // Default constructor
    }

    public KhachHang(int internalID, String maKhachHang, String maNguoiDung, String hoTen, Date ngaySinh, String gioiTinh, String sdt) {
        this.internalID = internalID;
        this.maKhachHang = maKhachHang;
        this.maNguoiDung = maNguoiDung;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
    }

    // Constructor tiện lợi cho việc thêm khách hàng mới (không có MaNguoiDung ban đầu)
    public KhachHang(String hoTen, Date ngaySinh, String gioiTinh, String sdt) {
        this(0, null, null, hoTen, ngaySinh, gioiTinh, sdt);
    }

    // Constructor tiện lợi cho việc thêm khách hàng mới (có liên kết MaNguoiDung)
    public KhachHang(String maNguoiDung, String hoTen, Date ngaySinh, String gioiTinh, String sdt) {
        this(0, null, maNguoiDung, hoTen, ngaySinh, gioiTinh, sdt);
    }

    // Getters and Setters
    public int getInternalID() {
        return internalID;
    }

    public void setInternalID(int internalID) {
        this.internalID = internalID;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public Date getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(Date ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    @Override
    public String toString() {
        return "KhachHang{" +
               "internalID=" + internalID +
               ", maKhachHang='" + maKhachHang + '\'' +
               ", maNguoiDung='" + maNguoiDung + '\'' +
               ", hoTen='" + hoTen + '\'' +
               ", ngaySinh=" + ngaySinh +
               ", gioiTinh='" + gioiTinh + '\'' +
               ", sdt='" + sdt + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return internalID == khachHang.internalID &&
               Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalID, maKhachHang);
    }
}