package controllers;

import view.HoaDonView;
import view.CustomerSelectionDialog; // Dialog to select registered customer
import services.HoaDonService; // Đã đổi thành Service (chữ hoa)
import services.SanPhamService; // Đã đổi thành Service (chữ hoa)
import services.KhachHangService; // Đã đổi thành Service (chữ hoa)
import models.HoaDon;
import models.ChiTietHoaDon;
import models.SanPham;
import models.KhachHang;
import models.NhanVien;
import services.NhanVienService; // Đã đổi thành Service (chữ hoa)

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException; // Import SQLException
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp SalesController điều khiển logic nghiệp vụ cho giao diện bán hàng (SalesView).
 * Nó tương tác với HoaDonService, SanPhamService, KhachHangService và NhanVienService.
 */
public class HoaDonController {
    private HoaDonView view;
    private HoaDonService hoaDonService;
    private SanPhamService sanPhamService;
    private KhachHangService khachHangService;
    private NhanVienService nhanVienService; // To get current logged-in employee

    private String currentLoggedInEmployeeMaNhanVien; // Mã nhân viên đang đăng nhập

    /**
     * Constructor khởi tạo SalesController.
     *
     * @param view Instance của SalesView.
     * @param hoaDonService Instance của HoaDonService.
     * @param sanPhamService Instance của SanPhamService.
     * @param khachHangService Instance của KhachHangService.
     * @param nhanVienService Instance của NhanVienService.
     * @param maNhanVienLapHienTai Mã nhân viên hiện đang đăng nhập (người tạo hóa đơn).
     */
    public HoaDonController(HoaDonView view, HoaDonService hoaDonService, SanPhamService sanPhamService,
                           KhachHangService khachHangService, NhanVienService nhanVienService, String maNhanVienLapHienTai) {
        this.view = view;
        this.hoaDonService = hoaDonService;
        this.sanPhamService = sanPhamService;
        this.khachHangService = khachHangService;
        this.nhanVienService = nhanVienService;
        this.currentLoggedInEmployeeMaNhanVien = maNhanVienLapHienTai; // Lấy từ thông tin người dùng đăng nhập

        // Đăng ký các listener cho các thành phần UI
        this.view.addSelectCustomerButtonListener(new SelectCustomerButtonListener());
        this.view.addSearchProductButtonListener(new SearchProductButtonListener());
        this.view.addAddProductToCartButtonListener(new AddProductToCartButtonListener());
        this.view.addRemoveProductFromCartButtonListener(new RemoveProductFromCartButtonListener());
        this.view.addUpdateQuantityInCartButtonListener(new UpdateQuantityInCartButtonListener());
        this.view.addCreateInvoiceButtonListener(new CreateInvoiceButtonListener());
        this.view.addClearFormButtonListener(new ClearFormButtonListener());
        this.view.addRegisteredCustomerRadioButtonListener(new CustomerTypeChangeListener());
        this.view.addWalkInCustomerRadioButtonListener(new CustomerTypeChangeListener());
        this.view.getCbProductList().addItemListener(new ProductComboBoxListener());
        this.view.getCartTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                // Handle selection change in cart table if needed (e.g., enable/disable update/remove buttons)
            }
        });
    }

    /**
     * Xử lý sự kiện thay đổi loại khách hàng (đã đăng ký/vãng lai).
     */
    class CustomerTypeChangeListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (view.getRbRegisteredCustomer().isSelected()) {
                view.toggleCustomerFields(true);
            } else {
                view.toggleCustomerFields(false);
            }
            view.clearCustomerFields(); // Xóa thông tin cũ khi đổi loại
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Chọn khách hàng".
     * Mở dialog cho phép chọn khách hàng đã đăng ký.
     */
    class SelectCustomerButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            List<KhachHang> allCustomers;
            allCustomers = khachHangService.getAllKhachHang();
            
            CustomerSelectionDialog dialog = new CustomerSelectionDialog((JFrame) SwingUtilities.getWindowAncestor(view), allCustomers);
            dialog.setVisible(true);

            KhachHang selectedCustomer = dialog.getSelectedCustomer();
            if (selectedCustomer != null) {
                view.displaySelectedCustomer(selectedCustomer);
                view.displayMessage("Đã chọn khách hàng: " + selectedCustomer.getHoTen(), false);
            } else {
                view.displayMessage("Không có khách hàng nào được chọn.", false);
            }
        }
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
                // Populate the combo box with product names
                List<String> productNames = foundProducts.stream()
                        .map(SanPham::getTenSanPham)
                        .collect(Collectors.toList());
                view.populateProductComboBox(productNames);
                view.displayMessage("Tìm thấy " + foundProducts.size() + " sản phẩm.", false);
                // The first item will be selected by default, trigger its selection to show details
                if (!productNames.isEmpty()) {
                    view.getCbProductList().setSelectedIndex(0);
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
                    SanPham product = sanPhamService.getSanPhamByTen(selectedProductName); // Lấy sản phẩm theo tên chính xác
                    view.displaySelectedProductDetails(product);
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Thêm vào hóa đơn".
     * Thêm sản phẩm được chọn vào giỏ hàng.
     */
    class AddProductToCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SanPham selectedProduct = view.getSelectedProductInComboBox();
            if (selectedProduct == null) {
                view.displayMessage("Vui lòng chọn một sản phẩm để thêm vào hóa đơn.", true);
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(view.getTxtQuantity().getText().trim());
                if (quantity <= 0) {
                    view.displayMessage("Số lượng phải lớn hơn 0.", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                view.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên.", true);
                return;
            }

            // Check if product is already in cart, if so, get its current quantity in cart
            int currentQuantityInCart = 0;
            for (ChiTietHoaDon item : view.getCurrentCartItems()) {
                if (item.getMaSanPham().equals(selectedProduct.getMaSanPham())) {
                    currentQuantityInCart = item.getSoLuong();
                    break;
                }
            }
            
            // Check total quantity needed against stock
            if (selectedProduct.getSoLuongTon() < (quantity + currentQuantityInCart)) {
                view.displayMessage("Số lượng tồn kho không đủ cho sản phẩm này (" + selectedProduct.getSoLuongTon() + ").", true);
                return;
            }

            // Create ChiTietHoaDon object
            ChiTietHoaDon cthd = new ChiTietHoaDon();
            cthd.setMaSanPham(selectedProduct.getMaSanPham());
            cthd.setSoLuong(quantity);
            cthd.setThanhTien(quantity * selectedProduct.getDonGia()); // Calculate subtotal

            view.addProductToCart(cthd, selectedProduct); // Pass SanPham to get its name for display
            view.displayMessage("Đã thêm '" + selectedProduct.getTenSanPham() + "' vào hóa đơn.", false);
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Xóa SP khỏi hóa đơn".
     * Xóa sản phẩm được chọn khỏi giỏ hàng.
     */
    class RemoveProductFromCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getCartTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để xóa.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa sản phẩm này khỏi hóa đơn?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                view.removeProductFromCart(selectedRow);
                view.displayMessage("Đã xóa sản phẩm khỏi hóa đơn.", false);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Sửa số lượng SP".
     * Cho phép người dùng sửa số lượng của một sản phẩm trong giỏ hàng.
     */
    class UpdateQuantityInCartButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getCartTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn một sản phẩm trong giỏ hàng để sửa số lượng.", true);
                return;
            }
    
            List<ChiTietHoaDon> cartItems = view.getCurrentCartItems();
            ChiTietHoaDon selectedCartItem = cartItems.get(selectedRow);
            
            // Get current product details to check stock
            SanPham productInStock = sanPhamService.getSanPhamById(selectedCartItem.getMaSanPham());
            if (productInStock == null) {
                view.displayMessage("Không tìm thấy thông tin sản phẩm trong kho. Không thể cập nhật.", true);
                return;
            }
    
            String input = JOptionPane.showInputDialog(view, 
                "Nhập số lượng mới cho sản phẩm '" + view.getCartTable().getValueAt(selectedRow, 1) + "' (Tồn kho: " + productInStock.getSoLuongTon() + "):", 
                selectedCartItem.getSoLuong());
    
            if (input != null && !input.isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(input.trim());
                    if (newQuantity <= 0) {
                        view.displayMessage("Số lượng phải lớn hơn 0.", true);
                        return;
                    }
                    
                    // Calculate available stock considering the current item's quantity in cart
                    // New stock = (Original stock + Quantity of this item already in cart) - New requested quantity
                    // This logic assumes SanPham.getSoLuongTon() returns the actual stock from DB.
                    // The 'selectedCartItem.getSoLuong()' is the quantity already reserved for this specific item in the cart.
                    // So, the available stock for *new* quantity is (productInStock.getSoLuongTon() + old quantity of this item in cart).
                    int currentItemOldQuantity = selectedCartItem.getSoLuong();
                    int effectiveStockForThisUpdate = productInStock.getSoLuongTon() + currentItemOldQuantity;

                    if (newQuantity > effectiveStockForThisUpdate) {
                         view.displayMessage("Số lượng tồn kho không đủ. Chỉ còn " + productInStock.getSoLuongTon() + " sản phẩm trong kho. Số lượng hiện tại trong hóa đơn là " + currentItemOldQuantity + ". Bạn yêu cầu tổng cộng " + newQuantity + " sản phẩm.", true);
                         return;
                    }
                    
                    view.updateProductQuantityInCart(selectedRow, newQuantity, productInStock); // Pass productInStock to update total amount
                    view.displayMessage("Đã cập nhật số lượng sản phẩm.", false);
    
                } catch (NumberFormatException ex) {
                    view.displayMessage("Số lượng không hợp lệ. Vui lòng nhập số nguyên.", true);
                }
            }
        }
    }


    /**
     * Xử lý sự kiện khi nhấn nút "Tạo hóa đơn".
     * Tạo hóa đơn và lưu vào cơ sở dữ liệu, cập nhật tồn kho.
     */
    class CreateInvoiceButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get customer info
            String maKhachHang = null;
            String tenKhachHangVangLai = null;
            String sdtKhachHangVangLai = null;

            if (view.getRbRegisteredCustomer().isSelected()) {
                maKhachHang = view.getTxtMaKhachHang();
                if (maKhachHang.isEmpty()) {
                    view.displayMessage("Vui lòng chọn khách hàng đã đăng ký.", true);
                    return;
                }
            } else { // Walk-in customer
                tenKhachHangVangLai = view.getTxtTenKhachHang();
                sdtKhachHangVangLai = view.getTxtSdtKhachHang();

                if (tenKhachHangVangLai.isEmpty()) {
                    view.displayMessage("Vui lòng nhập tên khách hàng vãng lai.", true);
                    return;
                }
                // SDT is optional for walk-in customer but good practice to have validation
            }

            // Get cart items
            List<ChiTietHoaDon> cartItems = view.getCurrentCartItems();
            if (cartItems.isEmpty()) {
                view.displayMessage("Vui lòng thêm sản phẩm vào hóa đơn.", true);
                return;
            }

            // Create HoaDon object
            HoaDon hoaDon = new HoaDon();
            hoaDon.setMaKhachHang(maKhachHang);
            hoaDon.setTenKhachHangVangLai(tenKhachHangVangLai);
            hoaDon.setSdtKhachHangVangLai(sdtKhachHangVangLai);
            hoaDon.setMaNhanVienLap(currentLoggedInEmployeeMaNhanVien); // Nhân viên đang đăng nhập
            hoaDon.setNgayBan(new Date()); // Ngày hiện tại
            hoaDon.setPhuongThucThanhToan(view.getSelectedPaymentMethod());

            // Attempt to add invoice and its details
            try {
                HoaDon createdHoaDon = hoaDonService.taoHoaDonMoi(hoaDon, cartItems); // Gọi phương thức mới

                if (createdHoaDon != null) {
                    view.displayMessage("Tạo hóa đơn thành công! Mã hóa đơn: " + createdHoaDon.getMaHoaDon(), false);
                    view.clearInvoiceForm(); // Clear form after successful creation
                } else {
                    view.displayMessage("Tạo hóa đơn thất bại. Vui lòng kiểm tra lại thông tin.", true);
                }
            } catch (SQLException ex) {
                view.displayMessage("Lỗi cơ sở dữ liệu khi tạo hóa đơn: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Làm mới hóa đơn".
     * Xóa trắng toàn bộ form.
     */
    class ClearFormButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInvoiceForm();
            view.displayMessage("Form đã được làm mới.", false);
        }
    }
}

