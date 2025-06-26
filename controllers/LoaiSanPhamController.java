package controllers;

import view.LoaiSanPhamView;
import services.LoaiSanPhamService;
import models.LoaiSanPham;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors; // For Java 8+ stream API

/**
 * Lớp LoaiSanPhamController xử lý logic nghiệp vụ cho giao diện quản lý loại sản phẩm (LoaiSanPhamView).
 * Nó tương tác với LoaiSanPhamService.
 */
public class LoaiSanPhamController {
    private LoaiSanPhamView view;
    private LoaiSanPhamService loaiSanPhamService;

    /**
     * Constructor khởi tạo LoaiSanPhamController.
     *
     * @param view Instance của LoaiSanPhamView.
     * @param loaiSanPhamService Instance của LoaiSanPhamService.
     */
    public LoaiSanPhamController(LoaiSanPhamView view, LoaiSanPhamService loaiSanPhamService) {
        this.view = view;
        this.loaiSanPhamService = loaiSanPhamService;

        // Đăng ký các listener cho các thành phần UI
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        this.view.getLoaiSanPhamTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi view được tải
        loadLoaiSanPhamData();
    }

    /**
     * Tải dữ liệu loại sản phẩm từ service và hiển thị lên bảng.
     */
    private void loadLoaiSanPhamData() {
        List<LoaiSanPham> loaiSanPhamList = loaiSanPhamService.getAllLoaiSanPham();
        view.populateTable(loaiSanPhamList);
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Thêm".
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LoaiSanPham loaiSanPham = view.getLoaiSanPhamFromInput();
            if (loaiSanPham == null) {
                return; // Lỗi validation đã được hiển thị trong view
            }

            LoaiSanPham addedLoaiSanPham = loaiSanPhamService.themLoaiSanPham(loaiSanPham);
            if (addedLoaiSanPham != null) {
                view.displayMessage("Thêm loại sản phẩm thành công!", false);
                loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi thêm
                view.clearInputFields();
            } else {
                view.displayMessage("Thêm loại sản phẩm thất bại. Tên loại sản phẩm có thể đã tồn tại.", true);
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Sửa".
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LoaiSanPham loaiSanPham = view.getLoaiSanPhamFromInput();
            if (loaiSanPham == null) {
                return;
            }

            // Mã loại sản phẩm phải có để cập nhật
            if (loaiSanPham.getMaLoaiSanPham() == null || loaiSanPham.getMaLoaiSanPham().isEmpty()) {
                view.displayMessage("Vui lòng chọn loại sản phẩm cần sửa từ bảng.", true);
                return;
            }

            boolean updated = loaiSanPhamService.capNhatLoaiSanPham(loaiSanPham);
            if (updated) {
                view.displayMessage("Cập nhật loại sản phẩm thành công!", false);
                loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi cập nhật
                view.clearInputFields();
            } else {
                view.displayMessage("Cập nhật loại sản phẩm thất bại. Tên loại sản phẩm có thể đã tồn tại hoặc mã không hợp lệ.", true);
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Xóa".
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String maLoaiSanPham = view.txtMaLoaiSanPham.getText(); // Lấy mã loại sản phẩm từ trường nhập liệu
            if (maLoaiSanPham.isEmpty()) {
                view.displayMessage("Vui lòng chọn loại sản phẩm cần xóa từ bảng.", true);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc chắn muốn xóa loại sản phẩm này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = loaiSanPhamService.xoaLoaiSanPham(maLoaiSanPham);
                if (deleted) {
                    view.displayMessage("Xóa loại sản phẩm thành công!", false);
                    loadLoaiSanPhamData(); // Tải lại dữ liệu sau khi xóa
                    view.clearInputFields();
                } else {
                    view.displayMessage("Xóa loại sản phẩm thất bại. Có thể có sản phẩm đang thuộc loại này.", true);
                }
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Làm mới".
     */
    class ClearButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.clearInputFields();
            view.getLoaiSanPhamTable().clearSelection(); // Bỏ chọn hàng trên bảng
            loadLoaiSanPhamData(); // Tải lại toàn bộ dữ liệu (để bỏ các bộ lọc tìm kiếm)
            view.displayMessage("Sẵn sàng.", false);
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Tìm kiếm".
     */
    class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchText = view.getSearchText().trim();
            if (searchText.isEmpty()) {
                view.displayMessage("Vui lòng nhập từ khóa tìm kiếm (Tên loại SP).", true);
                loadLoaiSanPhamData(); // Hiển thị lại tất cả nếu không có từ khóa
                return;
            }
            
            List<LoaiSanPham> allLoaiSanPhams = loaiSanPhamService.getAllLoaiSanPham();
            List<LoaiSanPham> searchResults = allLoaiSanPhams.stream()
                .filter(lsp -> lsp.getTenLoaiSanPham().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());

            if (searchResults.isEmpty()) {
                view.displayMessage("Không tìm thấy loại sản phẩm nào với từ khóa '" + searchText + "'.", false);
            } else {
                view.displayMessage("Tìm thấy " + searchResults.size() + " kết quả.", false);
            }
            view.populateTable(searchResults); // Hiển thị kết quả tìm kiếm
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi chọn một hàng trên bảng.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Đảm bảo chỉ xử lý khi người dùng ngừng chọn
                int selectedRow = view.getLoaiSanPhamTable().getSelectedRow();
                if (selectedRow != -1) { // Một hàng đã được chọn
                    String maLoaiSanPham = (String) view.getLoaiSanPhamTable().getValueAt(selectedRow, 0); // Cột Mã Loại SP

                    LoaiSanPham selectedLoaiSanPham = loaiSanPhamService.getLoaiSanPhamById(maLoaiSanPham);
                    if (selectedLoaiSanPham != null) {
                        view.displayLoaiSanPhamDetails(selectedLoaiSanPham);
                        view.displayMessage("Đã chọn loại sản phẩm: " + selectedLoaiSanPham.getTenLoaiSanPham(), false);
                    } else {
                        view.clearInputFields();
                        view.displayMessage("Không thể tải chi tiết loại sản phẩm.", true);
                    }
                } else {
                    // Không có hàng nào được chọn, hoặc selection đã bị xóa
                    view.clearInputFields();
                    view.displayMessage("Sẵn sàng.", false);
                }
            }
        }
    }
}

