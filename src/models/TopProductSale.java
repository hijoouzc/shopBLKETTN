package models;

/**
 * Lớp TopProductSale đại diện cho thông tin của một sản phẩm trong báo cáo sản phẩm bán chạy.
 * Chứa mã sản phẩm, tên sản phẩm và tổng số lượng đã bán.
 */
public class TopProductSale {
    private String maSanPham;
    private String tenSanPham;
    private int totalQuantitySold;

    public TopProductSale() {
    }

    public TopProductSale(String maSanPham, String tenSanPham, int totalQuantitySold) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.totalQuantitySold = totalQuantitySold;
    }

    // Getters and Setters
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

    public int getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public void setTotalQuantitySold(int totalQuantitySold) {
        this.totalQuantitySold = totalQuantitySold;
    }

    @Override
    public String toString() {
        return "TopProductSale{" +
               "maSanPham='" + maSanPham + '\'' +
               ", tenSanPham='" + tenSanPham + '\'' +
               ", totalQuantitySold=" + totalQuantitySold +
               '}';
    }
}
