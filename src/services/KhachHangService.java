package services; // Giữ nguyên package theo file bạn cung cấp

import DAO.*;
import models.*;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class KhachHangService {
    private static KhachHangService instance;
    private KhachHangDAO khachHangDAO;
    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;
    private HoaDonDAO hoaDonDAO;

    private KhachHangService() {
        this.khachHangDAO = KhachHangDAO.getIns(); // Sử dụng Singleton instance
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns();
        this.hoaDonDAO = HoaDonDAO.getIns(); // Sử dụng Singleton instance
    }

    public static KhachHangService getIns() {
        if (instance == null) {
            instance = new KhachHangService();
        }
        return instance;
    }

    /**
     * Nghiệp vụ: 3.1 Thêm thông tin khách hàng mới.
     * Có thể tạo tài khoản người dùng liên kết nếu khách hàng muốn đăng ký tài khoản.
     * Nếu khách hàng không có tài khoản, việc kiểm tra trùng lặp chỉ dựa trên số điện thoại (SDT).
     *
     * @param khachHang Đối tượng KhachHang chứa thông tin. Cần có HoTen, SDT.
     * @param username Tài khoản đăng nhập (có thể null hoặc rỗng nếu không tạo tài khoản liên kết).
     * @param password Mật khẩu tài khoản (chỉ cần thiết nếu username không null).
     * @param emailForAccount Email cho tài khoản người dùng (chỉ cần thiết nếu username không null).
     * @return KhachHang nếu thêm thành công, null nếu thất bại.
     */
    public KhachHang addKhachHang(KhachHang khachHang, String username, String password, String emailForAccount) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra trùng SDT trước khi thêm khách hàng (luôn thực hiện)
            if (khachHangDAO.getBySdt(conn, khachHang.getSdt()) != null) {
                System.err.println("Lỗi: Số điện thoại khách hàng đã tồn tại.");
                conn.rollback();
                return null;
            }

            String maNguoiDung = null;
            // Chỉ xử lý tạo tài khoản người dùng nếu username được cung cấp
            if (username != null && !username.trim().isEmpty()) {
                // Kiểm tra trùng username hoặc email tài khoản người dùng
                if (taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username) != null) {
                    System.err.println("Lỗi: Tên đăng nhập tài khoản đã tồn tại.");
                    conn.rollback();
                    return null;
                }
                // EmailForAccount là bắt buộc nếu tạo tài khoản
                if (emailForAccount == null || emailForAccount.trim().isEmpty()) {
                    System.err.println("Lỗi: Email cho tài khoản người dùng không được để trống khi tạo tài khoản liên kết.");
                    conn.rollback();
                    return null;
                }
                // 1. Tạo tài khoản người dùng cho khách hàng
                // Đảm bảo LoaiNguoiDung là "Khách hàng" và TrangThaiTaiKhoan là "Hoạt động"
                TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung(username, password, emailForAccount, "Khách hàng", new Date(), "Hoạt động");
                TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, newAccount);
                if (createdAccount == null) {
                    throw new SQLException("Không thể tạo tài khoản người dùng cho khách hàng.");
                }
                maNguoiDung = createdAccount.getMaNguoiDung();
                khachHang.setMaNguoiDung(maNguoiDung); // Gán MaNguoiDung cho khách hàng
            } else {
                // Nếu không tạo tài khoản, đảm bảo MaNguoiDung của KhachHang là null
                khachHang.setMaNguoiDung(null);
            }

            // 2. Thêm thông tin khách hàng
            KhachHang newKhachHang = khachHangDAO.add(conn, khachHang);
            if (newKhachHang == null) {
                throw new SQLException("Không thể thêm thông tin khách hàng.");
            }

            conn.commit(); // Cam kết giao dịch
            return newKhachHang;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm khách hàng: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch thêm khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            e.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: 3.2 Cập nhật thông tin khách hàng.
     * Cập nhật thông tin cá nhân của khách hàng.
     *
     * @param khachHang Đối tượng KhachHang chứa thông tin cập nhật (MaKhachHang phải được set).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean updateKhachHang(KhachHang khachHang) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // Kiểm tra khách hàng có tồn tại không
            if (khachHangDAO.getById(conn, khachHang.getMaKhachHang()) == null) {
                System.err.println("Lỗi: Không tìm thấy khách hàng với mã: " + khachHang.getMaKhachHang());
                conn.rollback();
                return false;
            }
            // Kiểm tra trùng SDT nếu SDT thay đổi và không phải của chính khách hàng này
            KhachHang existingBySdt = khachHangDAO.getBySdt(conn, khachHang.getSdt());
            if (existingBySdt != null && !existingBySdt.getMaKhachHang().equals(khachHang.getMaKhachHang())) {
                System.err.println("Lỗi: Số điện thoại mới đã được sử dụng bởi khách hàng khác.");
                conn.rollback();
                return false;
            }

            boolean updated = khachHangDAO.update(conn, khachHang);
            conn.commit(); // Cam kết giao dịch
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật thông tin khách hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: 3.3 / 3.4
     * Lấy tất cả khách hàng.
     *
     * @return Danh sách tất cả khách hàng.
     */
    public List<KhachHang> getAllKhachHang() {
        return khachHangDAO.getAll(); // DAO này tự quản lý kết nối cho getAll()
    }

    /**
     * Nghiệp vụ: 3.4 Xem thông tin chi tiết khách hàng.
     * Lấy khách hàng theo mã.
     *
     * @param maKhachHang Mã khách hàng.
     * @return KhachHang nếu tìm thấy, null nếu không.
     */
    public KhachHang getKhachHangById(String maKhachHang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getById(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: Tra cứu khách hàng.
     * Lấy khách hàng theo số điện thoại.
     *
     * @param sdt Số điện thoại.
     * @return KhachHang nếu tìm thấy, null nếu không.
     */
    public KhachHang getKhachHangBySdt(String sdt) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return khachHangDAO.getBySdt(conn, sdt); // Sử dụng phương thức mới getBySdt
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy khách hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Nghiệp vụ: Tra cứu khách hàng.
     * Tìm kiếm khách hàng theo tên hoặc số điện thoại.
     * (Giả định KhachHangDAO có phương thức search)
     * @param keyword Từ khóa tìm kiếm (tên hoặc SĐT).
     * @return Danh sách khách hàng khớp với từ khóa.
     */
    public List<KhachHang> searchKhachHang(String keyword) {
        return khachHangDAO.search(keyword); // Giả định DAO có phương thức search
    }

    /**
     * Nghiệp vụ: 3.5 Xem lịch sử mua hàng của khách hàng.
     *
     * @param maKhachHang Mã khách hàng.
     * @return Danh sách các hóa đơn của khách hàng.
     */
    public List<HoaDon> getLichSuMuaHang(String maKhachHang) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Giả định HoaDonDAO có phương thức findByMaKhachHang để tối ưu
            return hoaDonDAO.getHoaDonByMaKhachHang(conn, maKhachHang);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public List<HoaDon> getLichSuMuaHangBySdt(String sdt) { // Đổi tên tham số từ maKhachHang thành sdt
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Nếu không, cần thêm vào HoaDonDAO.
            return hoaDonDAO.getHoaDonBySdt(conn, sdt);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy lịch sử mua hàng theo SĐT: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: Xóa khách hàng.
     * Xóa khách hàng, tài khoản liên kết (nếu có) và tất cả hóa đơn của khách hàng đó.
     *
     * @param maKhachHang Mã khách hàng cần xóa.
     * @return True nếu xóa thành công, false nếu thất bại.
     */
    public boolean deleteKhachHang(String maKhachHang) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Lấy thông tin khách hàng để biết MaNguoiDung (nếu có)
            KhachHang khachHangToDelete = khachHangDAO.getById(conn, maKhachHang);
            if (khachHangToDelete == null) {
                System.err.println("Lỗi: Không tìm thấy khách hàng để xóa với mã: " + maKhachHang);
                conn.rollback();
                return false;
            }
            String maNguoiDungLinked = khachHangToDelete.getMaNguoiDung();

            // 2. Xóa tất cả các hóa đơn liên quan đến khách hàng này trước
            // Đảm bảo HoaDonDAO có phương thức deleteByMaKhachHang(Connection conn, String maKhachHang)
            boolean hoaDonDeleted = hoaDonDAO.deleteByMaKhachHang(conn, maKhachHang);
            if (!hoaDonDeleted) {
                // Điều này có thể xảy ra nếu không có hóa đơn nào, hoặc lỗi xóa
                // Nếu không có hóa đơn, nó vẫn trả về false nhưng không phải lỗi
                // Cần kiểm tra kỹ hơn trong DAO nếu deleteByMaKhachHang có ý nghĩa là "tất cả"
                // Hoặc bạn có thể bỏ qua lỗi này nếu khách hàng không có hóa đơn nào
                System.out.println("Cảnh báo: Không có hóa đơn nào để xóa hoặc lỗi khi xóa hóa đơn cho khách hàng " + maKhachHang);
            }

            // 3. Xóa khách hàng
            boolean khachHangDeleted = khachHangDAO.delete(conn, maKhachHang);
            if (!khachHangDeleted) {
                System.err.println("Lỗi: Không thể xóa khách hàng với mã: " + maKhachHang);
                conn.rollback();
                return false;
            }

            // 4. Nếu có tài khoản người dùng liên kết, xóa tài khoản đó
            if (maNguoiDungLinked != null && !maNguoiDungLinked.trim().isEmpty() && !maNguoiDungLinked.equals("N/A")) {
                boolean taiKhoanDeleted = taiKhoanNguoiDungDAO.delete(conn, maNguoiDungLinked);
                if (!taiKhoanDeleted) {
                    System.err.println("Cảnh báo: Xóa khách hàng thành công nhưng không thể xóa tài khoản liên kết: " + maNguoiDungLinked);
                    // Quyết định: Rollback hay không? Nếu muốn đảm bảo tài khoản bị xóa, hãy rollback.
                    // Hiện tại, nếu lỗi xóa tài khoản, sẽ rollback toàn bộ.
                    conn.rollback();
                    return false;
                }
            }

            conn.commit(); // Cam kết giao dịch
            return true;

        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch xóa khách hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }
}