package controllers;

import models.KhachHang;
import services.KhachHangService; // Đã giữ nguyên Service.KhachHangService theo file bạn cung cấp
import services.TaiKhoanNguoiDungService; // Đã giữ nguyên Service.TaiKhoanNguoiDungService theo file bạn cung cấp
import models.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung model
import view.KhachHangView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException; // Import SQLException
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp CustomerController xử lý logic nghiệp vụ cho màn hình quản lý khách hàng.
 * Nó lắng nghe các sự kiện từ CustomerView, tương tác với KhachHangService và TaiKhoanNguoiDungService,
 * và cập nhật CustomerView dựa trên kết quả.
 */
public class KhachHangController {
    private KhachHangView view;
    private KhachHangService khachHangService;
    private TaiKhoanNguoiDungService taiKhoanNguoiDungService; // Vẫn cần TaiKhoanNguoiDungService cho một số validation và delete account

    public KhachHangController(KhachHangView view, KhachHangService khachHangService, TaiKhoanNguoiDungService taiKhoanNguoiDungService) {
        this.view = view;
        this.khachHangService = khachHangService;
        this.taiKhoanNguoiDungService = taiKhoanNguoiDungService;

        // Đăng ký các listeners
        this.view.addAddButtonListener(new AddButtonListener());
        this.view.addUpdateButtonListener(new UpdateButtonListener());
        this.view.addDeleteButtonListener(new DeleteButtonListener());
        this.view.addClearButtonListener(new ClearButtonListener());
        this.view.addSearchButtonListener(new SearchButtonListener());
        
        // Listener cho việc chọn hàng trên bảng
        this.view.getCustomerTable().getSelectionModel().addListSelectionListener(new TableSelectionListener());

        // Khởi tạo dữ liệu khi Controller được tạo
        loadAllCustomersToTable();
    }

    /**
     * Tải tất cả khách hàng từ service và điền vào bảng.
     */
    private void loadAllCustomersToTable() {
        List<KhachHang> khachHangList = khachHangService.getAllKhachHang();
        if (khachHangList != null) {
            view.populateTable(khachHangList);
        } else {
            view.displayMessage("Không thể tải danh sách khách hàng.", true);
        }
        view.clearInputFields(); // Đảm bảo các trường input được reset và nút được đặt lại
    }

    // --- Inner Listener Classes ---

    /**
     * Xử lý sự kiện khi nút "Thêm" được nhấn.
     */
    class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KhachHang khachHang = view.getKhachHangFromInput();
            if (khachHang == null) {
                return; // Lỗi đã được hiển thị từ view.getKhachHangFromInput()
            }
            if (!khachHang.getMaKhachHang().isEmpty()) {
                view.displayMessage("Bạn không thể thêm khách hàng với Mã KH đã có. Vui lòng làm mới để thêm mới.", true);
                return;
            }

            String username = null;
            String password = null;
            String emailForAccount = null;

            if (view.isLinkAccountSelected()) {
                TaiKhoanNguoiDung taiKhoanInput = view.getTaiKhoanNguoiDungFromInput();
                if (taiKhoanInput == null) {
                    return; // Lỗi validation đã được hiển thị từ view.getTaiKhoanNguoiDungFromInput()
                }
                username = taiKhoanInput.getUsername();
                password = taiKhoanInput.getPassword();
                emailForAccount = taiKhoanInput.getEmail();
            }

            // Gọi service để thêm khách hàng, truyền cả thông tin tài khoản (có thể null)
            try {
                KhachHang newKhachHang = khachHangService.addKhachHang(khachHang, username, password, emailForAccount); 

                if (newKhachHang != null) {
                    view.displayMessage("Thêm khách hàng thành công: " + newKhachHang.getHoTen(), false);
                    loadAllCustomersToTable(); // Tải lại bảng để hiển thị khách hàng mới
                } else {
                    // Lỗi đã được in ra từ Service (ví dụ: trùng SĐT, trùng username)
                    // Hoặc message đã được hiển thị từ view.get...Input()
                    // Không cần hiển thị message chung "Thêm khách hàng thất bại." ở đây
                    // vì Service đã thông báo lỗi cụ thể hơn.
                }
            } catch (Exception ex) { // Bắt Exception chung để xử lý các lỗi không phải SQL
                view.displayMessage("Đã xảy ra lỗi khi thêm khách hàng: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Sửa" được nhấn.
     */
    class UpdateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            KhachHang khachHang = view.getKhachHangFromInput();
            if (khachHang == null) {
                return;
            }
            if (khachHang.getMaKhachHang().isEmpty()) {
                view.displayMessage("Vui lòng chọn khách hàng cần sửa từ bảng.", true);
                return;
            }

            // Đảm bảo không thể cập nhật thông tin tài khoản liên kết từ màn hình này
            // vì logic liên kết tài khoản chỉ dành cho khi thêm mới
            if (view.isLinkAccountSelected() && !view.txtMaNguoiDung.getText().isEmpty()) {
                 view.displayMessage("Không thể thay đổi liên kết tài khoản cho khách hàng hiện có từ chức năng này. " +
                                     "Vui lòng quản lý tài khoản riêng biệt.", true);
                 return;
            } else if (view.isLinkAccountSelected() && view.txtMaNguoiDung.getText().isEmpty()) {
                 view.displayMessage("Bạn không thể tạo tài khoản mới khi cập nhật khách hàng. " +
                                     "Vui lòng bỏ chọn 'Liên kết tài khoản mới' hoặc làm mới để thêm mới.", true);
                 return;
            }


            try {
                // Gọi service để cập nhật khách hàng
                // Phương thức updateKhachHang trong service của bạn không nhận thông tin tài khoản
                boolean updated = khachHangService.updateKhachHang(khachHang); 
                if (updated) {
                    view.displayMessage("Cập nhật khách hàng thành công: " + khachHang.getHoTen(), false);
                    loadAllCustomersToTable(); // Tải lại bảng để hiển thị thay đổi
                } else {
                    // Service sẽ in lỗi cụ thể hơn nếu cập nhật thất bại (ví dụ: trùng SDT)
                    view.displayMessage("Cập nhật khách hàng thất bại. Vui lòng kiểm tra console/thông tin.", true);
                }
            } catch (Exception ex) {
                view.displayMessage("Lỗi khi cập nhật khách hàng: " + ex.getMessage(), true);
                ex.printStackTrace();
            }
        }
    }

    /**
     * Xử lý sự kiện khi nút "Xóa" được nhấn.
     */
    class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = view.getCustomerTable().getSelectedRow();
            if (selectedRow == -1) {
                view.displayMessage("Vui lòng chọn khách hàng cần xóa từ bảng.", true);
                return;
            }

            String maKhachHangToDelete = (String) view.getCustomerTable().getValueAt(selectedRow, 0);
            // Lấy MaNguoiDung từ bảng để hiển thị trong thông báo xác nhận
            String maNguoiDungLinked = (String) view.getCustomerTable().getValueAt(selectedRow, 5); // Cột MaNguoiDung

            int confirm = JOptionPane.showConfirmDialog(view, 
                "Bạn có chắc chắn muốn xóa khách hàng " + maKhachHangToDelete + " không? " +
                "Nếu khách hàng có tài khoản liên kết (" + (maNguoiDungLinked.isEmpty() ? "Không có" : maNguoiDungLinked) + "), tài khoản đó và các hóa đơn liên quan cũng sẽ bị xóa.",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    // Gọi service để xóa khách hàng. 
                    // Service sẽ tự xử lý việc xóa tài khoản liên kết (nếu có) và hóa đơn.
                    boolean deleted = khachHangService.deleteKhachHang(maKhachHangToDelete);

                    if (deleted) {
                        view.displayMessage("Xóa khách hàng thành công: " + maKhachHangToDelete, false);
                        loadAllCustomersToTable(); // Tải lại bảng
                    } else {
                        // Lỗi đã được in ra từ Service (ví dụ: khách hàng có hóa đơn)
                        view.displayMessage("Xóa khách hàng thất bại. Vui lòng kiểm tra console/thông tin.", true);
                    }
                } catch (Exception ex) {
                    view.displayMessage("Lỗi khi xóa khách hàng: " + ex.getMessage(), true);
                    ex.printStackTrace();
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
            view.getCustomerTable().clearSelection(); // Bỏ chọn hàng trên bảng
            loadAllCustomersToTable(); // Tải lại tất cả khách hàng
            view.displayMessage("Sẵn sàng.", false);
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
                loadAllCustomersToTable(); // Nếu rỗng, hiển thị tất cả
                view.displayMessage("Sẵn sàng.", false);
                return;
            }

            // Gọi phương thức searchKhachHang từ service
            List<KhachHang> filteredCustomers = khachHangService.searchKhachHang(searchText);
            
            if (filteredCustomers != null && !filteredCustomers.isEmpty()) {
                view.populateTable(filteredCustomers);
                view.displayMessage("Đã tìm thấy " + filteredCustomers.size() + " khách hàng.", false);
            } else {
                view.populateTable(new java.util.ArrayList<>()); // Xóa bảng nếu không tìm thấy
                view.displayMessage("Không tìm thấy khách hàng nào khớp với từ khóa.", true);
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
                int selectedRow = view.getCustomerTable().getSelectedRow();
                if (selectedRow != -1) { // Có hàng được chọn
                    String maKhachHang = (String) view.getCustomerTable().getValueAt(selectedRow, 0);
                    // Lấy chi tiết khách hàng từ service
                    KhachHang selectedKhachHang = khachHangService.getKhachHangById(maKhachHang);
                    if (selectedKhachHang != null) {
                        view.displayKhachHangDetails(selectedKhachHang);
                        view.displayMessage("Đã tải thông tin khách hàng.", false);
                    } else {
                        view.clearInputFields();
                        view.displayMessage("Không thể tải chi tiết khách hàng đã chọn.", true);
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