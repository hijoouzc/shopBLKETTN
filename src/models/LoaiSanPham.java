package models;

import java.util.Objects; // Import for Objects.hash and Objects.equals

//Lớp LoaiSanPham (ProductCategory)
public class LoaiSanPham {
 private int internalID;
 private String maLoaiSanPham;
 private String tenLoaiSanPham;
 private String moTa; // Can be null

 public LoaiSanPham() {
     // Default constructor
 }

 public LoaiSanPham(int internalID, String maLoaiSanPham, String tenLoaiSanPham, String moTa) {
     this.internalID = internalID;
     this.maLoaiSanPham = maLoaiSanPham;
     this.tenLoaiSanPham = tenLoaiSanPham;
     this.moTa = moTa;
 }

 public LoaiSanPham(String tenLoaiSanPham, String moTa) {
     this(0, null, tenLoaiSanPham, moTa);
 }

 // Getters and Setters
 public int getInternalID() {
     return internalID;
 }

 public void setInternalID(int internalID) {
     this.internalID = internalID;
 }

 public String getMaLoaiSanPham() {
     return maLoaiSanPham;
 }

 public void setMaLoaiSanPham(String maLoaiSanPham) {
     this.maLoaiSanPham = maLoaiSanPham;
 }

 public String getTenLoaiSanPham() {
     return tenLoaiSanPham;
 }

 public void setTenLoaiSanPham(String tenLoaiSanPham) {
     this.tenLoaiSanPham = tenLoaiSanPham;
 }

 public String getMoTa() {
     return moTa;
 }

 public void setMoTa(String moTa) {
     this.moTa = moTa;
 }

 @Override
 public String toString() {
     return "LoaiSanPham{" +
            "internalID=" + internalID +
            ", maLoaiSanPham='" + maLoaiSanPham + '\'' +
            ", tenLoaiSanPham='" + tenLoaiSanPham + '\'' +
            ", moTa='" + moTa + '\'' +
            '}';
 }

 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     LoaiSanPham that = (LoaiSanPham) o;
     return internalID == that.internalID &&
            Objects.equals(maLoaiSanPham, that.maLoaiSanPham);
 }

 @Override
 public int hashCode() {
     return Objects.hash(internalID, maLoaiSanPham);
 }
}