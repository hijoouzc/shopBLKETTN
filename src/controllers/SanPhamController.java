package controllers; // Đã đổi package cho Controller

import models.SanPham;
import models.LoaiSanPham;
import services.SanPhamService; // Import từ package Service
import services.LoaiSanPhamService; // Import từ package Service
import view.SanPhamView; // Import từ package ui.views

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors; // Để lọc danh sách sản phẩm

/**
 * Lớp SanPhamController xử lý logic nghiệp vụ cho màn hình quản lý sản phẩm.
 * Nó lắng nghe các sự kiện từ SanPhamView, tương tác với SanPhamService và LoaiSanPhamService,
 * và cập nhật SanPhamView dựa trên kết quả.
 */
public class SanPhamController {
    private SanPhamView view;
    private SanPhamService sanPhamService;
    private LoaiSanPhamService loaiSanPhamService;

    public SanPhamController(SanPhamView view, SanPhamService sanPhamService, LoaiSanPhamService loaiSanPhamService) {
        this.view = view;
        this.sanPhamService = sanPhamService;
        this.loaiSanPhamService = loaiSanPhamService;

        // Đăng ký các listeners
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener()); // Đã bỏ comment để kích hoạt lại
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        
        // Listener cho việc chọn hàng trên bảng
        this.view.getProductTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi Controller được tạo
        loadInitialData();
    }

    /**
     * Tải dữ liệu ban đầu cho bảng sản phẩm và ComboBox loại sản phẩm.
     */
    private void loadInitialData() {
        // Tải danh sách loại sản phẩm cho ComboBox
        List<LoaiSanPham> loaiSanPhamList = loaiSanPhamService.getAllLoaiSanPham();
        if (loaiSanPhamList != null) {
            view.populateLoaiSanPhamComboBox(loaiSanPhamList);
        } else {
            view.displayMessage("Không thể tải danh sách loại sản phẩm.", true);
        }

        // Tải tất cả sản phẩm vào bảng
        loadAllProductsToTable();
    }

    /**
     * Tải tất cả sản phẩm từ service và điền vào bảng.
     */
    private void loadAllProductsToTable() {
        List<SanPham> sanPhamList = sanPhamService.getAllSanPham();
        if (sanPhamList != null) {
            view.populateTable(sanPhamList);
        } else {
            view.displayMessage("Không thể tải danh sách sản phẩm.", true);
        }
    }

    // --- Inner Listener Classes ---

    /**
     * Xử lý sự kiện khi nút "Thêm" được nhấn.
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SanPham sanPham = view.getSanPhamFromInput();
            if (sanPham == null) {
                return; // Lỗi đã được hiển thị từ view.getSanPhamFromInput()
            }
            // Mã sản phẩm rỗng khi thêm mới (DB sẽ tự tạo)
            if (!sanPham.getMaSanPham().isEmpty()) { 
                view.displayMessage("Bạn không thể thêm sản phẩm với Mã SP đã có. Vui lòng làm mới để thêm mới.", true);
                return;
            }

            // Gọi service để thêm sản phẩm
            SanPham newSanPham = sanPhamService.themSanPham(sanPham);

            if (newSanPham != null) {
                view.displayMessage("Thêm sản phẩm thành công: " + newSanPham.getTenSanPham(), false);
                loadAllProductsToTable(); // Tải lại bảng để hiển thị sản phẩm mới
                view.clearInputFields(); // Xóa trắng các trường nhập liệu
            } else {
                // Lỗi đã được in ra từ SanPhamService
                view.displayMessage("Thêm sản phẩm thất bại. Vui lòng kiểm tra console.", true);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Sửa" được nhấn.
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            SanPham sanPham = view.getSanPhamFromInput();
            if (sanPham == null) {
                return;
            }
            if (sanPham.getMaSanPham().isEmpty()) {
                view.displayMessage("Vui lòng chọn sản phẩm cần sửa từ bảng.", true);
                return;
            }

            // Gọi service để cập nhật sản phẩm
            boolean updated = sanPhamService.capNhatSanPham(sanPham);

            if (updated) {
                view.displayMessage("Cập nhật sản phẩm thành công: " + sanPham.getTenSanPham(), false);
                loadAllProductsToTable(); // Tải lại bảng để hiển thị thay đổi
                view.clearInputFields(); // Xóa trắng các trường nhập liệu
            } else {
                view.displayMessage("Cập nhật sản phẩm thất bại. Vui lòng kiểm tra console.", true);
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Xóa" được nhấn.
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String maSanPhamToDelete = view.getTxtMaSanPham().getText(); // Lấy mã từ trường text
            if (maSanPhamToDelete.isEmpty()) {
                view.displayMessage("Vui lòng chọn sản phẩm cần xóa từ bảng.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa sản phẩm " + maSanPhamToDelete + " không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = sanPhamService.xoaSanPham(maSanPhamToDelete);
                if (deleted) {
                    view.displayMessage("Xóa sản phẩm thành công: " + maSanPhamToDelete, false);
                    loadAllProductsToTable(); // Tải lại bảng
                    view.clearInputFields(); // Xóa trắng các trường
                } else {
                    view.displayMessage("Xóa sản phẩm thất bại. Có thể có ràng buộc dữ liệu hoặc lỗi khác. Vui lòng kiểm tra console.", true);
                }
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Làm mới" được nhấn.
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            loadAllProductsToTable(); // Tải lại tất cả sản phẩm
            view.displayMessage("Form đã được làm mới.", false);
        }
    }

    /**
     * Xử lý sự kiện khi nút "Tìm kiếm" được nhấn.
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchText().trim();
            if (searchText.isEmpty()) {
                loadAllProductsToTable(); // Nếu rỗng, hiển thị tất cả
                view.displayMessage("Hiển thị tất cả sản phẩm.", false);
                return;
            }

            // Sử dụng phương thức searchByName từ SanPhamService
            List<SanPham> filteredProducts = sanPhamService.searchSanPhamByName(searchText);
            
            view.populateTable(filteredProducts);
            if (filteredProducts == null || filteredProducts.isEmpty()) {
                view.displayMessage("Không tìm thấy sản phẩm nào khớp với từ khóa '" + searchText + "'.", true);
            } else {
                view.displayMessage("Đã tìm thấy " + filteredProducts.size() + " sản phẩm.", false);
            }
        }
    }

    /**
     * Xử lý sự kiện khi một hàng trên bảng được chọn.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Đảm bảo chỉ xử lý khi lựa chọn cuối cùng được đưa ra
                int selectedRow = view.getProductTable().getSelectedRow();
                if (selectedRow != -1) { // Có hàng được chọn
                    String maSanPham = (String) view.getProductTable().getValueAt(selectedRow, 0);
                    // Lấy chi tiết sản phẩm từ service
                    SanPham selectedSanPham = sanPhamService.getSanPhamById(maSanPham);
                    if (selectedSanPham != null) {
                        view.displaySanPhamDetails(selectedSanPham);
                        view.displayMessage("Đã tải thông tin sản phẩm: " + selectedSanPham.getMaSanPham(), false);
                    } else {
                        view.displayMessage("Không thể tải chi tiết sản phẩm đã chọn.", true);
                    }
                } else {
                    // Nếu không có hàng nào được chọn, xóa trắng các trường
                    view.clearInputFields();
                    view.displayMessage("Sẵn sàng.", false);
                }
            }
        }
    }

    /**
     * Hiển thị SanPhamView.
     */
    public void showSanPhamView() {
        // Có thể gọi loadInitialData() ở đây nếu SanPhamView được ẩn/hiện thường xuyên
        // Hiện tại, nó được gọi trong constructor
        // view.setVisible(true); // SanPhamView là JPanel, không phải JFrame
    }
}
