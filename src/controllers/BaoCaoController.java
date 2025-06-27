package controllers;

import view.BaoCaoView;
import services.BaoCaoService;
import models.SanPham;
import models.TopProductSale;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Lớp ReportController điều khiển logic nghiệp vụ cho giao diện báo cáo (ReportView).
 * Nó tương tác với ReportService để lấy dữ liệu báo cáo và cập nhật ReportView.
 */
public class BaoCaoController {
    private BaoCaoView view;
    private BaoCaoService baoCaoService;

    public BaoCaoController(BaoCaoView view, BaoCaoService baoCaoService) {
        this.view = view;
        this.baoCaoService = baoCaoService;

        // Đăng ký listener cho nút "Tạo Báo cáo"
        this.view.addGenerateReportButtonListener(new GenerateBaoCaoButtonListener());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tạo Báo cáo".
     * Lấy loại báo cáo và các tham số, gọi Service và hiển thị kết quả.
     */
    class GenerateBaoCaoButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String reportType = view.getSelectedReportType();
            String yearInput = view.getYearInput();
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = currentYear; // Default to current year

            // Validate year input if required by report type
            if (reportType.equals(BaoCaoView.REPORT_TYPE_MONTHLY_REVENUE) || reportType.equals(BaoCaoView.REPORT_TYPE_MONTHLY_IMPORT_QUANTITY)) {
                if (!yearInput.isEmpty()) {
                    try {
                        year = Integer.parseInt(yearInput);
                        if (year <= 1900 || year > currentYear + 1) { // Basic validation
                            view.displayMessage("Năm không hợp lệ. Vui lòng nhập năm trong khoảng hợp lý.", true);
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        view.displayMessage("Năm không hợp lệ. Vui lòng nhập số nguyên.", true);
                        return;
                    }
                } else {
                     view.displayMessage("Vui lòng nhập năm cho báo cáo theo tháng.", true);
                     return;
                }
            }


            try {
                switch (reportType) {
                    case BaoCaoView.REPORT_TYPE_MONTHLY_REVENUE:
                        generateMonthlyRevenueReport(year);
                        break;
                    case BaoCaoView.REPORT_TYPE_YEARLY_REVENUE:
                        generateYearlyRevenueReport();
                        break;
                    case BaoCaoView.REPORT_TYPE_TOP_SELLING_PRODUCTS:
                        generateTopSellingProductsReport();
                        break;
                    case BaoCaoView.REPORT_TYPE_PRODUCT_STOCK_LEVELS:
                        generateProductStockLevelsReport();
                        break;
                    case BaoCaoView.REPORT_TYPE_MONTHLY_IMPORT_QUANTITY:
                        generateMonthlyImportQuantityReport(year);
                        break;
                    default:
                        view.displayMessage("Loại báo cáo không hợp lệ.", true);
                        break;
                }
            } catch (SQLException ex) {
                view.displayMessage("Lỗi cơ sở dữ liệu: " + ex.getMessage(), true);
                ex.printStackTrace();
            } catch (Exception ex) {
                view.displayMessage("Đã xảy ra lỗi: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Tạo báo cáo doanh thu theo tháng cho một năm cụ thể.
     * @param year Năm cần báo cáo.
     * @throws SQLException
     */
    private void generateMonthlyRevenueReport(int year) throws SQLException {
        Map<Integer, Double> monthlyRevenue = baoCaoService.getMonthlyRevenue(year);

        String[] columnNames = {"Tháng", "Doanh thu"};
        Vector<Vector<Object>> data = new Vector<>();

        for (int month = 1; month <= 12; month++) {
            Vector<Object> row = new Vector<>();
            row.add("Tháng " + month);
            row.add(view.formatCurrency(monthlyRevenue.getOrDefault(month, 0.0)));
            data.add(row);
        }

        view.updateReportTable(columnNames, data);
        view.displayMessage("Báo cáo doanh thu theo tháng năm " + year + " đã được tạo.", false);
    }

    /**
     * Tạo báo cáo doanh thu theo năm.
     * @throws SQLException
     */
    private void generateYearlyRevenueReport() throws SQLException {
        Map<Integer, Double> yearlyRevenue = baoCaoService.getYearlyRevenue();

        String[] columnNames = {"Năm", "Doanh thu"};
        Vector<Vector<Object>> data = new Vector<>();

        List<Integer> years = yearlyRevenue.keySet().stream().sorted().collect(Collectors.toList());
        for (int year : years) {
            Vector<Object> row = new Vector<>();
            row.add(year);
            row.add(view.formatCurrency(yearlyRevenue.getOrDefault(year, 0.0)));
            data.add(row);
        }

        view.updateReportTable(columnNames, data);
        view.displayMessage("Báo cáo doanh thu theo năm đã được tạo.", false);
    }

    /**
     * Tạo báo cáo sản phẩm bán chạy nhất (Top 10).
     * @throws SQLException
     */
    private void generateTopSellingProductsReport() throws SQLException {
        int limit = 10; // Có thể cho phép người dùng nhập limit
        List<TopProductSale> topProducts = baoCaoService.getTopSellingProducts(limit);

        String[] columnNames = {"Mã SP", "Tên SP", "Tổng số lượng đã bán"};
        Vector<Vector<Object>> data = new Vector<>();

        int rank = 1;
        for (TopProductSale product : topProducts) {
            Vector<Object> row = new Vector<>();
            row.add(product.getMaSanPham());
            row.add(product.getTenSanPham());
            row.add(view.formatNumber(product.getTotalQuantitySold()));
            data.add(row);
            rank++;
        }

        view.updateReportTable(columnNames, data);
        view.displayMessage("Báo cáo " + limit + " sản phẩm bán chạy nhất đã được tạo.", false);
    }

    /**
     * Tạo báo cáo mức tồn kho sản phẩm.
     * @throws SQLException
     */
    private void generateProductStockLevelsReport() throws SQLException {
        List<SanPham> stockLevels = baoCaoService.getProductStockLevels();

        String[] columnNames = {"Mã SP", "Tên SP", "Số lượng tồn"};
        Vector<Vector<Object>> data = new Vector<>();

        for (SanPham sanPham : stockLevels) {
            Vector<Object> row = new Vector<>();
            row.add(sanPham.getMaSanPham());
            row.add(sanPham.getTenSanPham());
            row.add(view.formatNumber(sanPham.getSoLuongTon()));
            data.add(row);
        }

        view.updateReportTable(columnNames, data);
        view.displayMessage("Báo cáo mức tồn kho sản phẩm đã được tạo.", false);
    }

    /**
     * Tạo báo cáo tổng số lượng nhập về theo tháng trong một năm cụ thể.
     * @param year Năm cần báo cáo.
     * @throws SQLException
     */
    private void generateMonthlyImportQuantityReport(int year) throws SQLException {
        Map<Integer, Integer> monthlyImportQuantity = baoCaoService.getMonthlyImportQuantity(year);

        String[] columnNames = {"Tháng", "Tổng số lượng nhập"};
        Vector<Vector<Object>> data = new Vector<>();

        for (int month = 1; month <= 12; month++) {
            Vector<Object> row = new Vector<>();
            row.add("Tháng " + month);
            row.add(view.formatNumber(monthlyImportQuantity.getOrDefault(month, 0)));
            data.add(row);
        }

        view.updateReportTable(columnNames, data);
        view.displayMessage("Báo cáo số lượng nhập theo tháng năm " + year + " đã được tạo.", false);
    }
}
