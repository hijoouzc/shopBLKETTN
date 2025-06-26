package controllers;

import view.NhanVienView;
import services.NhanVienService; // Đổi từ Service.NhanVienService sang services.NhanVienService
import services.TaiKhoanNguoiDungService; // Đổi từ Service.TaiKhoanNguoiDungService sang services.TaiKhoanNguoiDungService
import models.NhanVien;
import models.TaiKhoanNguoiDung;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException; // Import SQLException
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Lớp NhanVienController xử lý logic nghiệp vụ cho giao diện quản lý nhân viên (NhanVienView).
 * Nó tương tác với NhanVienService và TaiKhoanNguoiDungService.
 */
public class NhanVienController {
    private NhanVienView view;
    private NhanVienService nhanVienService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService; // Để quản lý tài khoản người dùng

    // SimpleDateFormat cho định dạng ngày.
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Constructor khởi tạo NhanVienController.
     *
     * @param view Instance của NhanVienView.
     * @param nhanVienService Instance của NhanVienService.
     * @param taiKhoanNguoiDungService Instance của TaiKhoanNguoiDungService.
     */
    public NhanVienController(NhanVienView view, NhanVienService nhanVienService, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.view = view;
        this.nhanVienService = nhanVienService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;

        // Đăng ký các listener cho các thành phần UI
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener()); // Đã bỏ comment
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        this.view.getEmployeeTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi view được tải
        loadNhanVienData();
    }

    /**
     * Tải dữ liệu nhân viên từ service và hiển thị lên bảng.
     */
    private void loadNhanVienData() {
        try {
            List<NhanVien> nhanVienList = nhanVienService.getAllNhanViens(); // Gọi phương thức đúng tên
            view.populateTable(nhanVienList);
            view.clearInputFields(); // Xóa các trường input và đặt lại trạng thái nút
        } catch (Exception ex) { // Bắt Exception chung để xử lý lỗi từ Service
            view.displayMessage("Lỗi khi tải dữ liệu nhân viên: " + ex.getMessage(), true);
            ex.printStackTrace();
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Thêm".
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Lấy thông tin tài khoản từ View
            // (Hiện tại, theo yêu cầu, tạo tài khoản là bắt buộc khi thêm nhân viên)
            TaiKhoanNguoiDung taiKhoan = view.getTaiKhoanNguoiDungFromInput();
            if (taiKhoan == null) {
                // Lỗi validation đã được hiển thị từ View
                return;
            }

            try {
                // 2. Kiểm tra tên đăng nhập đã tồn tại chưa
                if (taiKhoanNguoiDungService.getTaiKhoanByUsername(taiKhoan.getUsername()) != null) {
                    view.displayMessage("Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", true);
                    return;
                }

                // 3. Thêm tài khoản người dùng vào DB và lấy MaNguoiDung
                // Phương thức trong service là addTaiKhoan, không phải themtaikhoan
                String maNguoiDungMoi = taiKhoanNguoiDungService.addTaiKhoan(taiKhoan);
                if (maNguoiDungMoi == null) {
                    view.displayMessage("Lỗi khi thêm tài khoản người dùng. Vui lòng thử lại.", true);
                    return;
                }

                // 4. Lấy thông tin nhân viên từ View và gán MaNguoiDung vừa tạo
                NhanVien nhanVien = view.getNhanVienFromInput();
                if (nhanVien == null) {
                    // Nếu thông tin nhân viên không hợp lệ, cần rollback tài khoản đã tạo
                    taiKhoanNguoiDungService.deleteTaiKhoan(maNguoiDungMoi); // Giả định có phương thức này
                    view.displayMessage("Thông tin nhân viên không hợp lệ. Đã hủy tạo tài khoản.", true);
                    return;
                }
                nhanVien.setMaNguoiDung(maNguoiDungMoi); // Gán mã người dùng đã tạo

                // 5. Thêm nhân viên vào DB
                NhanVien addedNhanVien = nhanVienService.addNhanVien(nhanVien);

                if (addedNhanVien != null) {
                    view.displayMessage("Thêm nhân viên và tài khoản thành công!", false);
                    loadNhanVienData(); // Tải lại dữ liệu sau khi thêm
                } else {
                    // Nếu thêm nhân viên thất bại (mặc dù tài khoản đã tạo), cần rollback tài khoản
                    taiKhoanNguoiDungService.deleteTaiKhoan(maNguoiDungMoi); // Giả định có phương thức này
                    view.displayMessage("Thêm nhân viên thất bại. Đã hủy tạo tài khoản.", true);
                }

            } catch (SQLException ex) { // Bắt lỗi SQL riêng biệt
                view.displayMessage("Lỗi CSDL khi thêm nhân viên/tài khoản: " + ex.getMessage(), true);
                ex.printStackTrace();
            } catch (Exception ex) { // Bắt các lỗi khác
                view.displayMessage("Đã xảy ra lỗi khi thêm nhân viên: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Sửa".
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            NhanVien nhanVien = view.getNhanVienFromInput();
            if (nhanVien == null) {
                return;
            }

            // Mã nhân viên phải có để cập nhật
            if (nhanVien.getMaNhanVien() == null || nhanVien.getMaNhanVien().isEmpty()) {
                view.displayMessage("Vui lòng chọn nhân viên cần sửa từ bảng.", true);
                return;
            }
            
            // Đảm bảo không cho phép cập nhật thông tin tài khoản (username/password) từ đây
            // vì các trường này trong View sẽ bị disabled khi chọn một nhân viên.
            // Logic này đã được xử lý trong NhanVienView.displayNhanVienDetails,
            // nên không cần kiểm tra isLinkAccountSelected() ở đây nữa.

            try {
                boolean updated = nhanVienService.updateNhanVien(nhanVien);
                if (updated) {
                    view.displayMessage("Cập nhật nhân viên thành công!", false);
                    loadNhanVienData(); // Tải lại dữ liệu sau khi cập nhật
                } else {
                    view.displayMessage("Cập nhật nhân viên thất bại. Vui lòng kiểm tra lại thông tin.", true);
                }
            } catch (Exception ex) {
                view.displayMessage("Lỗi khi cập nhật nhân viên: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi nhấn nút "Xóa".
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getEmployeeTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn nhân viên để xóa.", true);
                return;
            }

            // Lấy mã nhân viên từ cột đầu tiên của hàng được chọn
            String maNhanVien = (String) view.getEmployeeTable().getValueAt(selectedRow, 0);
            // Lưu ý: Không cần lấy MaNguoiDung trực tiếp từ bảng ở đây nữa,
            // vì logic đó sẽ được xử lý trong Service.

            int confirm = JOptionPane.showConfirmDialog(view,
                "Bạn có chắc chắn muốn xóa nhân viên này và tài khoản liên kết của họ không?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // GỌI PHƯƠNG THỨC MỚI TRONG SERVICE
                    boolean deleted = nhanVienService.deleteNhanVienAndAccount(maNhanVien);

                    if (deleted) {
                        view.displayMessage("Xóa nhân viên và tài khoản thành công!", false);
                        loadNhanVienData(); // Tải lại dữ liệu sau khi xóa
                    } else {
                        view.displayMessage("Xóa nhân viên và tài khoản thất bại. Vui lòng thử lại.", true);
                    }
                } catch (SQLException ex) { // Bắt SQLException từ Service
                    view.displayMessage("Lỗi CSDL khi xóa nhân viên: " + ex.getMessage(), true);
                    ex.printStackTrace();
                } catch (Exception ex) { // Bắt các lỗi khác
                    view.displayMessage("Đã xảy ra lỗi khi xóa nhân viên: " + ex.getMessage(), true);
                    ex.printStackTrace();
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
            view.getEmployeeTable().clearSelection(); // Bỏ chọn hàng trên bảng
            loadNhanVienData(); // Tải lại toàn bộ dữ liệu (để bỏ các bộ lọc tìm kiếm)
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
                view.displayMessage("Vui lòng nhập từ khóa tìm kiếm (Tên, SĐT, hoặc CCCD).", true);
                loadNhanVienData(); // Hiển thị lại tất cả nếu không có từ khóa
                return;
            }
            
            try {
                // Sử dụng phương thức searchNhanVien từ Service
                // Phương thức này cần được triển khai trong NhanVienService và NhanVienDAO
                List<NhanVien> searchResults = nhanVienService.searchNhanVien(searchText); 

                if (searchResults != null && !searchResults.isEmpty()) {
                    view.displayMessage("Tìm thấy " + searchResults.size() + " kết quả.", false);
                    view.populateTable(searchResults); // Hiển thị kết quả tìm kiếm
                } else {
                    view.displayMessage("Không tìm thấy nhân viên nào với từ khóa '" + searchText + "'.", false);
                    view.populateTable(new java.util.ArrayList<>()); // Xóa bảng nếu không tìm thấy
                }
            } catch (Exception ex) {
                view.displayMessage("Lỗi khi tìm kiếm nhân viên: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Lớp nội bộ xử lý sự kiện khi chọn một hàng trên bảng.
     */
    class TableSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) { // Đảm bảo chỉ xử lý khi người dùng ngừng chọn
                int selectedRow = view.getEmployeeTable().getSelectedRow();
                if (selectedRow != -1) { // Một hàng đã được chọn
                    // Lấy mã nhân viên từ cột đầu tiên của hàng được chọn
                    String maNhanVien = (String) view.getEmployeeTable().getValueAt(selectedRow, 0); 

                    try {
                        // Lấy đối tượng NhanVien đầy đủ từ service bằng ID
                        NhanVien selectedNhanVien = nhanVienService.getNhanVienById(maNhanVien); 
                        if (selectedNhanVien != null) {
                            view.displayNhanVienDetails(selectedNhanVien);
                            view.displayMessage("Đã chọn nhân viên: " + selectedNhanVien.getHoTen(), false);
                        } else {
                            view.clearInputFields();
                            view.displayMessage("Không thể tải chi tiết nhân viên.", true);
                        }
                    } catch (Exception ex) {
                        view.displayMessage("Lỗi khi tải chi tiết nhân viên: " + ex.getMessage(), true);
                        ex.printStackTrace();
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