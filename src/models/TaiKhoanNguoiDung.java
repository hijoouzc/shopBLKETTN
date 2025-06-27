package models; // Updated package

import java.util.Date;
import java.util.Objects;

public class TaiKhoanNguoiDung {
    private int internalID;
    private String maNguoiDung;
    private String username;
    private String password;
    private String email;
    private String loaiNguoiDung;
    private Date ngayTao;
    private String trangThaiTaiKhoan;

    public TaiKhoanNguoiDung() {
        // Default constructor
    }

    public TaiKhoanNguoiDung(int internalID, String maNguoiDung, String username, String password, String email, String loaiNguoiDung, Date ngayTao, String trangThaiTaiKhoan) {
        this.internalID = internalID;
        this.maNguoiDung = maNguoiDung;
        this.username = username;
        this.password = password;
        this.email = email;
        this.loaiNguoiDung = loaiNguoiDung;
        this.ngayTao = ngayTao;
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    public TaiKhoanNguoiDung(String username, String password, String email, String loaiNguoiDung, Date ngayTao, String trangThaiTaiKhoan) {
        this(0, null, username, password, email, loaiNguoiDung, ngayTao, trangThaiTaiKhoan);
    }

    // Getters and Setters
    public int getInternalID() {
        return internalID;
    }

    public void setInternalID(int internalID) {
        this.internalID = internalID;
    }

    public String getMaNguoiDung() {
        return maNguoiDung;
    }

    public void setMaNguoiDung(String maNguoiDung) {
        this.maNguoiDung = maNguoiDung;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoaiNguoiDung() {
        return loaiNguoiDung;
    }

    public void setLoaiNguoiDung(String loaiNguoiDung) {
        this.loaiNguoiDung = loaiNguoiDung;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public String getTrangThaiTaiKhoan() {
        return trangThaiTaiKhoan;
    }

    public void setTrangThaiTaiKhoan(String trangThaiTaiKhoan) {
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    @Override
    public String toString() {
        return "TaiKhoanNguoiDung{" +
               "internalID=" + internalID +
               ", maNguoiDung='" + maNguoiDung + '\'' +
               ", username='" + username + '\'' +
               ", email='" + email + '\'' +
               ", loaiNguoiDung='" + loaiNguoiDung + '\'' +
               ", ngayTao=" + ngayTao +
               ", trangThaiTaiKhoan='" + trangThaiTaiKhoan + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoanNguoiDung that = (TaiKhoanNguoiDung) o;
        return internalID == that.internalID &&
               Objects.equals(maNguoiDung, that.maNguoiDung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalID, maNguoiDung);
    }
}