package models;

import java.util.Date;
import java.util.Objects; // Import for Objects.hash and Objects.equals

public class HoaDon {
    private int maHoaDon;
    private String maKhachHang; // Foreign Key, can be null
    private String tenKhachHangVangLai; // Can be null
    private String sdtKhachHangVangLai; // Can be null
    private String maNhanVienLap; // Foreign Key
    private Date ngayBan;
    private String phuongThucThanhToan;

    public HoaDon() {
        // Default constructor
    }

    public HoaDon(int maHoaDon, String maKhachHang, String tenKhachHangVangLai, String sdtKhachHangVangLai, String maNhanVienLap, Date ngayBan, String phuongThucThanhToan) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.tenKhachHangVangLai = tenKhachHangVangLai;
        this.sdtKhachHangVangLai = sdtKhachHangVangLai;
        this.maNhanVienLap = maNhanVienLap;
        this.ngayBan = ngayBan;
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    // Constructor tiện lợi cho hóa đơn của khách hàng có MaKhachHang
    public HoaDon(String maKhachHang, String maNhanVienLap, Date ngayBan, String phuongThucThanhToan) {
        this(0, maKhachHang, null, null, maNhanVienLap, ngayBan, phuongThucThanhToan);
    }

    // Constructor tiện lợi cho hóa đơn của khách hàng vãng lai
    public HoaDon(String tenKhachHangVangLai, String sdtKhachHangVangLai, String maNhanVienLap, Date ngayBan, String phuongThucThanhToan) {
        this(0, null, tenKhachHangVangLai, sdtKhachHangVangLai, maNhanVienLap, ngayBan, phuongThucThanhToan);
    }


    // Getters and Setters
    public int getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHangVangLai() {
        return tenKhachHangVangLai;
    }

    public void setTenKhachHangVangLai(String tenKhachHangVangLai) {
        this.tenKhachHangVangLai = tenKhachHangVangLai;
    }

    public String getSdtKhachHangVangLai() {
        return sdtKhachHangVangLai;
    }

    public void setSdtKhachHangVangLai(String sdtKhachHangVangLai) {
        this.sdtKhachHangVangLai = sdtKhachHangVangLai;
    }

    public String getMaNhanVienLap() {
        return maNhanVienLap;
    }

    public void setMaNhanVienLap(String maNhanVienLap) {
        this.maNhanVienLap = maNhanVienLap;
    }

    public Date getNgayBan() {
        return ngayBan;
    }

    public void setNgayBan(Date ngayBan) {
        this.ngayBan = ngayBan;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    @Override
    public String toString() {
        return "HoaDon{" +
               "maHoaDon=" + maHoaDon +
               ", maKhachHang='" + maKhachHang + '\'' +
               ", tenKhachHangVangLai='" + tenKhachHangVangLai + '\'' +
               ", sdtKhachHangVangLai='" + sdtKhachHangVangLai + '\'' +
               ", maNhanVienLap='" + maNhanVienLap + '\'' +
               ", ngayBan=" + ngayBan +
               ", phuongThucThanhToan='" + phuongThucThanhToan + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return maHoaDon == hoaDon.maHoaDon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }
}