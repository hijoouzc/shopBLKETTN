package controllers;

import view.PhieuNhapView;
import services.PhieuNhapHangService; 
import services.SanPhamService; 
import services.NhanVienService; 
import models.PhieuNhapHang;
import models.ChiTietPhieuNhap;
import models.SanPham;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp ImportController điều khiển logic nghiệp vụ cho giao diện nhập hàng (ImportView).
 * Nó tương tác với PhieuNhapHangService, SanPhamService và NhanVienService.
 */
public class PhieuNhapController {
    private PhieuNhapView view;
    private PhieuNhapHangService phieuNhapHangService;
    private SanPhamService sanPhamService;
    private NhanVienService nhanVienService;

    private String currentLoggedInEmployeeMaNhanVien; // Mã nhân viên đang đăng nhập

    /**
     * Constructor khởi tạo ImportController.
     *
     * @param view Instance của ImportView.
     * @param phieuNhapHangService Instance của PhieuNhapHangService.
     * @param sanPhamService Instance của SanPhamService.
     * @param nhanVienService Instance của NhanVienService.
     * @param maNhanVienLapHienTai Mã nhân viên hiện đang đăng nhập (người tạo phiếu nhập).
     */
    public PhieuNhapController(PhieuNhapView view, PhieuNhapHangService phieuNhapHangService,
                            SanPhamService sanPhamService, NhanVienService nhanVienService,
                            String maNhanVienLapHienTai) {
        this.view = view;
        this.phieuNhapHangService = phieuNhapHangService;
        this.sanPhamService = sanPhamService;
        this.nhanVienService = nhanVienService;
        this.currentLoggedInEmployeeMaNhanVien = maNhanVienLapHienTai;

        // Đặt mã nhân viên lập phiếu mặc định
        this.view.setMaNhanVienLap(currentLoggedInEmployeeMaNhanVien);

        // Đăng ký các listener cho các thành phần UI
        this.view.addSearchProductButtonListener(new SearchProductButtonListener());
        this.view.addAddProductToImportCartButtonListener(new AddProductToImportCartButtonListener());
        this.view.addRemoveProductFromCartButtonListener(new RemoveProductFromCartButtonListener());
        this.view.addUpdateQuantityInCartButtonListener(new UpdateQuantityInCartButtonListener());
        this.view.addUpdatePriceInCartButtonListener(new UpdatePriceInCartButtonListener());
        this.view.addCreateImportInvoiceButtonListener(new CreateImportInvoiceButtonListener());
        this.view.addClearFormButtonListener(new ClearFormButtonListener());
        this.view.getCbProductList().addItemListener(new ProductComboBoxListener());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tìm SP".
     * Tìm kiếm sản phẩm theo tên và hiển thị kết quả lên ComboBox.
     */
    class SearchProductButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchTerm = view.getTxtSearchProduct().getText().trim();
            if (searchTerm.isEmpty()) {
                view.displayMessage("Vui lòng nhập tên sản phẩm để tìm kiếm.", true);
                view.populateProductComboBox(List.of()); // Clear previous results
                return;
            }

            List<SanPham> foundProducts = sanPhamService.searchSanPhamByName(searchTerm);
            if (foundProducts.isEmpty()) {
                view.displayMessage("Không tìm thấy sản phẩm nào với từ khóa '" + searchTerm + "'.", false);
                view.populateProductComboBox(List.of());
            } else {
                List<String> productNames = foundProducts.stream()
                        .map(SanPham::getTenSanPham)
                        .collect(Collectors.toList());
                view.populateProductComboBox(productNames);
                view.displayMessage("Tìm thấy " + foundProducts.size() + " sản phẩm.", false);
                if (!productNames.isEmpty()) {
                    view.getCbProductList().setSelectedIndex(0); // Tự động chọn sản phẩm đầu tiên
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi chọn một sản phẩm từ ComboBox.
     * Hiển thị chi tiết sản phẩm đã chọn.
     */
    class ProductComboBoxListener implements ItemListener {
        @Override
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String selectedProductName = (String) view.getCbProductList().getSelectedItem();
                if (selectedProductName != null) {
                    SanPham product = sanPhamService.getSanPhamByTen(selectedProductName);
                    view.displaySelectedProductDetails(product);
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Thêm vào phiếu nhập".
     * Thêm sản phẩm được chọn vào giỏ hàng nhập.
     */
    class AddProductToImportCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SanPham selectedProduct = view.getSelectedProductInComboBox();
            if (selectedProduct == null) {
                view.displayMessage("Vui lòng chọn một sản phẩm để thêm vào phiếu nhập.", true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(view.getTxtQuantity().getText().trim());
                if (quantity <= 0) {
                    view.displayMessage("Số lượng nhập phải lớn hơn 0.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                view.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên.", true);
                return;
            }

            // Lấy đơn giá nhập. Có thể lấy từ ô input riêng hoặc mặc định là GiaBan của sản phẩm
            // Để đơn giản, ở đây chúng ta sẽ giả định dùng GiaBan làm đơn giá nhập ban đầu
            // Trong thực tế, bạn có thể thêm một JTextField cho DonGiaNhap
            String donGiaNhapStr = JOptionPane.showInputDialog(view, "Nhập đơn giá nhập cho sản phẩm '" + selectedProduct.getTenSanPham() + "':", String.valueOf(selectedProduct.getDonGia()));
            int donGiaNhap;
            if (donGiaNhapStr == null || donGiaNhapStr.trim().isEmpty()) {
                view.displayMessage("Vui lòng nhập đơn giá nhập.", true);
                return;
            }
            try {
                donGiaNhap = Integer.parseInt(donGiaNhapStr.trim());
                if (donGiaNhap < 0) {
                    view.displayMessage("Đơn giá nhập không được âm.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                view.displayMessage("Đơn giá nhập không hợp lệ. Vui lòng nhập số nguyên.", true);
                return;
            }


            // Create ChiTietPhieuNhap object
            ChiTietPhieuNhap ctpn = new ChiTietPhieuNhap();
            ctpn.setMaSanPham(selectedProduct.getMaSanPham());
            ctpn.setSoLuong(quantity);
            ctpn.setDonGiaNhap(donGiaNhap);
//            ctpn.setThanhTien(quantity * donGiaNhap); // Tính ThanhTien

            view.addProductToImportCart(ctpn, selectedProduct); // Pass SanPham to get its name for display
            view.displayMessage("Đã thêm '" + selectedProduct.getTenSanPham() + "' vào phiếu nhập.", false);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Xóa SP khỏi phiếu".
     * Xóa sản phẩm được chọn khỏi giỏ hàng nhập.
     */
    class RemoveProductFromCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để xóa.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa sản phẩm này khỏi phiếu nhập?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.removeProductFromImportCart(selectedRow);
                view.displayMessage("Đã xóa sản phẩm khỏi phiếu nhập.", false);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Sửa số lượng SP".
     * Cho phép người dùng sửa số lượng của một sản phẩm trong giỏ hàng nhập.
     */
    class UpdateQuantityInCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để sửa số lượng.", true);
                return;
            }

            List<ChiTietPhieuNhap> cartItems = view.getCurrentImportCartItems();
            ChiTietPhieuNhap selectedCartItem = cartItems.get(selectedRow);

            String input = JOptionPane.showInputDialog(view,
                "Nhập số lượng mới cho sản phẩm '" + view.getImportCartTable().getValueAt(selectedRow, 1) + "':",
                String.valueOf(selectedCartItem.getSoLuong()));

            if (input != null && !input.isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(input.trim());
                    if (newQuantity <= 0) {
                        view.displayMessage("Số lượng phải lớn hơn 0.", true);
                        return;
                    }
                    view.updateProductQuantityInImportCart(selectedRow, newQuantity);
                    view.displayMessage("Đã cập nhật số lượng sản phẩm.", false);

                } catch (NumberFormatException ex) {
                    view.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên.", true);
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Sửa đơn giá nhập".
     * Cho phép người dùng sửa đơn giá nhập của một sản phẩm trong giỏ hàng nhập.
     */
    class UpdatePriceInCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getImportCartTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để sửa đơn giá nhập.", true);
                return;
            }

            List<ChiTietPhieuNhap> cartItems = view.getCurrentImportCartItems();
            ChiTietPhieuNhap selectedCartItem = cartItems.get(selectedRow);

            String input = JOptionPane.showInputDialog(view,
                "Nhập đơn giá nhập mới cho sản phẩm '" + view.getImportCartTable().getValueAt(selectedRow, 1) + "':",
                String.valueOf(selectedCartItem.getDonGiaNhap()));

            if (input != null && !input.isEmpty()) {
                try {
                    int newPrice = Integer.parseInt(input.trim());
                    if (newPrice < 0) {
                        view.displayMessage("Đơn giá nhập không được âm.", true);
                        return;
                    }
                    view.updateProductPriceInImportCart(selectedRow, newPrice);
                    view.displayMessage("Đã cập nhật đơn giá nhập sản phẩm.", false);

                } catch (NumberFormatException ex) {
                    view.displayMessage("Đơn giá nhập không hợp lệ. Vui lòng nhập số nguyên.", true);
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Tạo Phiếu Nhập".
     * Tạo phiếu nhập và lưu vào cơ sở dữ liệu, cập nhật tồn kho.
     */
    class CreateImportInvoiceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get employee info (already set by controller)
            String maNhanVienThucHien = view.getTxtMaNhanVienLap();
            if (maNhanVienThucHien.isEmpty()) {
                view.displayMessage("Thông tin nhân viên lập phiếu không có. Vui lòng thử lại sau.", true);
                return;
            }

            // Get cart items
            List<ChiTietPhieuNhap> importCartItems = view.getCurrentImportCartItems();
            if (importCartItems.isEmpty()) {
                view.displayMessage("Vui lòng thêm sản phẩm vào phiếu nhập.", true);
                return;
            }

            // Create PhieuNhapHang object
            PhieuNhapHang phieuNhapHang = new PhieuNhapHang();
            phieuNhapHang.setNgayNhap(new Date()); // Ngày hiện tại
            phieuNhapHang.setMaNhanVienThucHien(maNhanVienThucHien);

            // Attempt to add import invoice and its details
            try {
                PhieuNhapHang createdPhieuNhapHang = phieuNhapHangService.lapPhieuNhapHang(phieuNhapHang, importCartItems);

                if (createdPhieuNhapHang != null) {
                    view.displayMessage("Tạo phiếu nhập thành công! Mã phiếu nhập: " + createdPhieuNhapHang.getMaPhieuNhap(), false);
                    view.clearImportForm(); // Clear form after successful creation
                } else {
                    view.displayMessage("Tạo phiếu nhập thất bại. Vui lòng kiểm tra lại thông tin.", true);
                }
            } catch (SQLException ex) {
                view.displayMessage("Lỗi cơ sở dữ liệu khi tạo phiếu nhập: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Làm mới Phiếu".
     * Xóa trắng toàn bộ form.
     */
    class ClearFormButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearImportForm();
            view.displayMessage("Form đã được làm mới.", false);
        }
    }
}
