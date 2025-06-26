package models;

import java.util.Date;
import java.util.Objects;

//Lớp NhanVien (Employee)
public class NhanVien {
 private int internalID;
 private String maNhanVien;
 private String maNguoiDung;
 private String hoTen;
 private Date ngaySinh;
 private String gioiTinh;
 private String cccd;
 private String sdt;
 private int luong;
 private String trangThaiLamViec;

 public NhanVien() {
     // Default constructor
 }

 public NhanVien(int internalID, String maNhanVien, String maNguoiDung, String hoTen, Date ngaySinh, String gioiTinh, String cccd, String sdt, int luong, String trangThaiLamViec) {
     this.internalID = internalID;
     this.maNhanVien = maNhanVien;
     this.maNguoiDung = maNguoiDung;
     this.hoTen = hoTen;
     this.ngaySinh = ngaySinh;
     this.gioiTinh = gioiTinh;
     this.cccd = cccd;
     this.sdt = sdt;
     this.luong = luong;
     this.trangThaiLamViec = trangThaiLamViec;
 }

 // Constructor tiện lợi cho việc thêm nhân viên mới
 public NhanVien(String maNguoiDung, String hoTen, Date ngaySinh, String gioiTinh, String cccd, String sdt, int luong, String trangThaiLamViec) {
     this(0, null, maNguoiDung, hoTen, ngaySinh, gioiTinh, cccd, sdt, luong, trangThaiLamViec);
 }

 // Getters and Setters
 public int getInternalID() {
     return internalID;
 }

 public void setInternalID(int internalID) {
     this.internalID = internalID;
 }

 public String getMaNhanVien() {
     return maNhanVien;
 }

 public void setMaNhanVien(String maNhanVien) {
     this.maNhanVien = maNhanVien;
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

 public String getCccd() {
     return cccd;
 }

 public void setCccd(String cccd) {
     this.cccd = cccd;
 }

 public String getSdt() {
     return sdt;
 }

 public void setSdt(String sdt) {
     this.sdt = sdt;
 }

 public int getLuong() {
     return luong;
 }

 public void setLuong(int luong) {
     this.luong = luong;
 }

 public String getTrangThaiLamViec() {
     return trangThaiLamViec;
 }

 public void setTrangThaiLamViec(String trangThaiLamViec) {
     this.trangThaiLamViec = trangThaiLamViec;
 }

 @Override
 public String toString() {
     return "NhanVien{" +
            "internalID=" + internalID +
            ", maNhanVien='" + maNhanVien + '\'' +
            ", maNguoiDung='" + maNguoiDung + '\'' +
            ", hoTen='" + hoTen + '\'' +
            ", ngaySinh=" + ngaySinh + '\'' +
            ", gioiTinh='" + gioiTinh + '\'' +
            ", cccd='" + cccd + '\'' +
            ", sdt='" + sdt + '\'' +
            ", luong=" + luong +
            ", trangThaiLamViec='" + trangThaiLamViec + '\'' +
            '}';
 }

 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     NhanVien nhanVien = (NhanVien) o;
     return internalID == nhanVien.internalID &&
            Objects.equals(maNhanVien, nhanVien.maNhanVien);
 }

 @Override
 public int hashCode() {
     return Objects.hash(internalID, maNhanVien);
 }

public String getEmail() {
	// TODO Auto-generated method stub
	return null;
}
}