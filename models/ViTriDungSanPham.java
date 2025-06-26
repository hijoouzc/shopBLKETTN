package models;

import java.util.Objects;

public class ViTriDungSanPham {
    private int internalID;
    private String maNganDung;
    private String tenNganDung;

    public ViTriDungSanPham() {
        // Default constructor
    }

    public ViTriDungSanPham(int internalID, String maNganDung, String tenNganDung) {
        this.internalID = internalID;
        this.maNganDung = maNganDung;
        this.tenNganDung = tenNganDung;
    }

    public ViTriDungSanPham(String tenNganDung) {
        this(0, null, tenNganDung);
    }

    // Getters and Setters
    public int getInternalID() {
        return internalID;
    }

    public void setInternalID(int internalID) {
        this.internalID = internalID;
    }

    public String getMaNganDung() {
        return maNganDung;
    }

    public void setMaNganDung(String maNganDung) {
        this.maNganDung = maNganDung;
    }

    public String getTenNganDung() {
        return tenNganDung;
    }

    public void setTenNganDung(String tenNganDung) {
        this.tenNganDung = tenNganDung;
    }

    @Override
    public String toString() {
        return "ViTriDungSanPham{" +
               "internalID=" + internalID +
               ", maNganDung='" + maNganDung + '\'' +
               ", tenNganDung='" + tenNganDung + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViTriDungSanPham that = (ViTriDungSanPham) o;
        return internalID == that.internalID &&
               Objects.equals(maNganDung, that.maNganDung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalID, maNganDung);
    }
}
