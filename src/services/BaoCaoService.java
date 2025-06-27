package services; // Đã đổi package từ Service sang services cho nhất quán

import DAO.HoaDonDAO;
import DAO.ChiTietHoaDonDAO;
import DAO.SanPhamDAO;
import DAO.PhieuNhapHangDAO; // Thêm import cho PhieuNhapHangDAO
import DAO.ChiTietPhieuNhapDAO; // Thêm import cho ChiTietPhieuNhapDAO

import models.HoaDon;
import models.PhieuNhapHang;
import models.ChiTietHoaDon;
import models.ChiTietPhieuNhap;
import models.SanPham;
import models.TopProductSale;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Calendar;

/**
 * Lớp ReportService cung cấp các phương thức để tạo các báo cáo và thống kê khác nhau.
 * Được triển khai theo mẫu Singleton.
 */
public class BaoCaoService {
    private static BaoCaoService instance;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private SanPhamDAO sanPhamDAO;
    private PhieuNhapHangDAO phieuNhapHangDAO; // Thêm DAO cho Phiếu nhập hàng
    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO; // Thêm DAO cho Chi tiết Phiếu nhập

    // Private constructor để triển khai Singleton
    private BaoCaoService() {
        this.hoaDonDAO = HoaDonDAO.getIns();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getIns();
        this.sanPhamDAO = SanPhamDAO.getIns();
        this.phieuNhapHangDAO = PhieuNhapHangDAO.getIns(); // Khởi tạo
        this.chiTietPhieuNhapDAO = ChiTietPhieuNhapDAO.getIns(); // Khởi tạo
    }

    // Static method để lấy thể hiện Singleton
    public static BaoCaoService getIns() {
        if (instance == null) {
            instance = new BaoCaoService();
        }
        return instance;
    }

    /**
     * Lấy tổng doanh thu theo từng tháng trong một năm cụ thể.
     *
     * @param year Năm cần báo cáo.
     * @return Map với key là tháng (1-12) và value là tổng doanh thu của tháng đó.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public Map<Integer, Double> getMonthlyRevenue(int year) throws SQLException {
        Map<Integer, Double> monthlyRevenue = new HashMap<>();
        // Khởi tạo tất cả các tháng với doanh thu 0
        for (int i = 1; i <= 12; i++) {
            monthlyRevenue.put(i, 0.0);
        }

        List<HoaDon> allHoaDon;
        List<ChiTietHoaDon> allChiTietHoaDon;

        allHoaDon = hoaDonDAO.getAll(); // Truyền connection
        allChiTietHoaDon = chiTietHoaDonDAO.getAll(); // Truyền connection


        // Tạo một map để dễ dàng tra cứu chi tiết hóa đơn theo MaHoaDon
        Map<Integer, List<ChiTietHoaDon>> chiTietByHoaDon = allChiTietHoaDon.stream()
            .collect(Collectors.groupingBy(ChiTietHoaDon::getMaHoaDon));

        Calendar cal = Calendar.getInstance();

        for (HoaDon hd : allHoaDon) {
            cal.setTime(hd.getNgayBan());
            int hdYear = cal.get(Calendar.YEAR);

            if (hdYear == year) {
                int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH starts from 0

                List<ChiTietHoaDon> details = chiTietByHoaDon.get(hd.getMaHoaDon());
                if (details != null) {
                    double totalInvoiceAmount = details.stream()
                        .mapToDouble(ChiTietHoaDon::getThanhTien) // ThanhTien của ChiTietHoaDon là int, cần chuyển sang Double
                        .sum();
                    monthlyRevenue.put(month, monthlyRevenue.getOrDefault(month, 0.0) + totalInvoiceAmount);
                }
            }
        }
        return monthlyRevenue;
    }

    /**
     * Lấy tổng doanh thu theo từng năm.
     *
     * @return Map với key là năm và value là tổng doanh thu của năm đó.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public Map<Integer, Double> getYearlyRevenue() throws SQLException {
        Map<Integer, Double> yearlyRevenue = new HashMap<>();

        List<HoaDon> allHoaDon;
        List<ChiTietHoaDon> allChiTietHoaDon;

        allHoaDon = hoaDonDAO.getAll();
        allChiTietHoaDon = chiTietHoaDonDAO.getAll();
     

        Map<Integer, List<ChiTietHoaDon>> chiTietByHoaDon = allChiTietHoaDon.stream()
            .collect(Collectors.groupingBy(ChiTietHoaDon::getMaHoaDon));

        Calendar cal = Calendar.getInstance();

        for (HoaDon hd : allHoaDon) {
            cal.setTime(hd.getNgayBan());
            int year = cal.get(Calendar.YEAR);

            List<ChiTietHoaDon> details = chiTietByHoaDon.get(hd.getMaHoaDon());
            if (details != null) {
                double totalInvoiceAmount = details.stream()
                    .mapToDouble(ChiTietHoaDon::getThanhTien)
                    .sum();
                yearlyRevenue.put(year, yearlyRevenue.getOrDefault(year, 0.0) + totalInvoiceAmount);
            }
        }
        return yearlyRevenue;
    }

    /**
     * Lấy danh sách các sản phẩm bán chạy nhất.
     *
     * @param limit Số lượng sản phẩm bán chạy hàng đầu muốn lấy.
     * @return Danh sách TopProductSale.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public List<TopProductSale> getTopSellingProducts(int limit) throws SQLException {
        Map<String, Integer> productSales = new HashMap<>(); // MaSanPham -> TotalQuantitySold

        List<ChiTietHoaDon> allChiTietHoaDon;

        allChiTietHoaDon = chiTietHoaDonDAO.getAll();
        
        for (ChiTietHoaDon cthd : allChiTietHoaDon) {
            productSales.put(cthd.getMaSanPham(), productSales.getOrDefault(cthd.getMaSanPham(), 0) + cthd.getSoLuong());
        }

        // Chuyển đổi sang danh sách TopProductSale và sắp xếp
        List<TopProductSale> topProducts = new ArrayList<>();
        Connection conn = null; // Cần một connection để lấy tên sản phẩm
        try {
            conn = DatabaseConnection.getConnection();
            for (Map.Entry<String, Integer> entry : productSales.entrySet()) {
                // SanPhamDAO.getById nhận String (MaSanPham), không phải int
                SanPham sanPham = sanPhamDAO.getById(conn, entry.getKey()); // Lấy thông tin sản phẩm
                if (sanPham != null) {
                    topProducts.add(new TopProductSale(entry.getKey(), sanPham.getTenSanPham(), entry.getValue()));
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }

        // Sắp xếp giảm dần theo số lượng bán và giới hạn kết quả
        return topProducts.stream()
                .sorted(Comparator.comparingInt(TopProductSale::getTotalQuantitySold).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Lấy mức tồn kho hiện tại của tất cả các sản phẩm.
     *
     * @return Danh sách các đối tượng SanPham (chứa Mã SP, Tên SP, Số lượng tồn).
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public List<SanPham> getProductStockLevels() throws SQLException {

    	return sanPhamDAO.getAll(); // Truyền connection

    }

    /**
     * Lấy tổng số lượng sản phẩm nhập về theo từng tháng trong một năm cụ thể.
     *
     * @param year Năm cần báo cáo.
     * @return Map với key là tháng (1-12) và value là tổng số lượng sản phẩm nhập.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public Map<Integer, Integer> getMonthlyImportQuantity(int year) throws SQLException {
        Map<Integer, Integer> monthlyImportQuantity = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            monthlyImportQuantity.put(i, 0);
        }

        List<PhieuNhapHang> allPhieuNhap;
        List<ChiTietPhieuNhap> allChiTietPhieuNhap;


             // Sử dụng PhieuNhapHangDAO và ChiTietPhieuNhapDAO để lấy dữ liệu nhập hàng
        allPhieuNhap = phieuNhapHangDAO.getAll();
        allChiTietPhieuNhap = chiTietPhieuNhapDAO.getAll();
 

        Map<Integer, List<ChiTietPhieuNhap>> chiTietNhapByPhieuNhap = allChiTietPhieuNhap.stream()
            .collect(Collectors.groupingBy(ChiTietPhieuNhap::getMaPhieuNhap));

        Calendar cal = Calendar.getInstance();

        for (PhieuNhapHang pnh : allPhieuNhap) { // Iterate through PhieuNhapHang objects
            cal.setTime(pnh.getNgayNhap()); // Use getNgayNhap() from PhieuNhapHang
            int pnhYear = cal.get(Calendar.YEAR);

            if (pnhYear == year) {
                int month = cal.get(Calendar.MONTH) + 1;

                List<ChiTietPhieuNhap> details = chiTietNhapByPhieuNhap.get(pnh.getMaPhieuNhap()); // Use getMaPhieuNhap()
                if (details != null) {
                    int totalImportedQuantity = details.stream()
                        .mapToInt(ChiTietPhieuNhap::getSoLuong)
                        .sum();
                    monthlyImportQuantity.put(month, monthlyImportQuantity.getOrDefault(month, 0) + totalImportedQuantity);
                }
            }
        }
        return monthlyImportQuantity;
    }
}
