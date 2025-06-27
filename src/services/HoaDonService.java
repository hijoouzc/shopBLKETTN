package services;

import DAO.ChiTietHoaDonDAO;
import DAO.HoaDonDAO;
import DAO.KhachHangDAO;
import DAO.SanPhamDAO;
import models.ChiTietHoaDon;
import models.HoaDon;
import models.KhachHang;
import models.SanPham;
import utils.DatabaseConnection;

import java.sql.*; 
import java.util.List;

public class HoaDonService {

    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private SanPhamDAO sanPhamDAO;
    private KhachHangDAO khachHangDAO;

    // Mẫu Singleton cho dịch vụ
    private static HoaDonService instance;

    private HoaDonService() {
        this.hoaDonDAO = HoaDonDAO.getIns();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getIns();
        this.sanPhamDAO = SanPhamDAO.getIns();
        this.khachHangDAO = KhachHangDAO.getIns();
    }

    public static HoaDonService getIns() {
        if (instance == null) {
            instance = new HoaDonService();
        }
        return instance;
    }

    /**
     * Tạo một hóa đơn mới cùng với các chi tiết của nó.
     * Phương thức này xử lý quản lý giao dịch để đảm bảo tính nguyên tử.
     *
     * @param hoaDon Đối tượng HoaDon sẽ được thêm (không có maHoaDon, sẽ được tạo tự động).
     * @param chiTietList Một danh sách các ChiTietHoaDon cho hóa đơn này (không có maChiTietHoaDon).
     * @return Đối tượng HoaDon mới được tạo với maHoaDon đã tạo, hoặc null nếu tạo không thành công.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public HoaDon taoHoaDonMoi(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) throws SQLException {
        Connection conn = null;
        HoaDon newHoaDon = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Thêm HoaDon
            newHoaDon = hoaDonDAO.add(conn, hoaDon);
            if (newHoaDon == null || newHoaDon.getMaHoaDon() == 0) {
                conn.rollback();
                return null;
            }

            // 2. Thêm ChiTietHoaDon
            for (ChiTietHoaDon chiTiet : chiTietList) {
                // Đảm bảo maHoaDon được đặt từ ID hóa đơn mới được tạo
                chiTiet.setMaHoaDon(newHoaDon.getMaHoaDon());

                // Tính ThanhTien nếu chưa được đặt (hoặc tính lại để đảm bảo chính xác)
                SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
                if (sanPham == null) {
                    System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ". Đang quay lại giao dịch.");
                    conn.rollback();
                    return null;
                }
                int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
                chiTiet.setThanhTien(thanhTien);

                ChiTietHoaDon newChiTiet = chiTietHoaDonDAO.add(conn, chiTiet);
                if (newChiTiet == null || newChiTiet.getMaChiTietHoaDon() == 0) {
                    System.err.println("Không thể thêm ChiTietHoaDon. Đang quay lại giao dịch.");
                    conn.rollback();
                    return null;
                }
            }

            conn.commit(); // Cam kết giao dịch
            return newHoaDon;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Quay lại khi có lỗi
                    System.err.println("Giao dịch tạo hóa đơn đã được quay lại.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi quay lại giao dịch: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi tạo hóa đơn: " + e.getMessage());
            throw e; // Ném lại để các lớp cao hơn có thể xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Đặt lại auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Lấy thông tin một hóa đơn theo ID của nó, bao gồm chi tiết và thông tin sản phẩm/khách hàng liên quan.
     *
     * @param maHoaDon ID của hóa đơn cần lấy.
     * @return Đối tượng HoaDon với các chi tiết đã được điền đầy đủ, hoặc null nếu không tìm thấy.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public HoaDon layThongTinHoaDon(int maHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            HoaDon hoaDon = hoaDonDAO.getById(conn, maHoaDon); // Giả sử getById tồn tại trong HoaDonDAO

            if (hoaDon != null) {
                // Lấy thông tin khách hàng nếu có
                if (hoaDon.getMaKhachHang() != null) {
                    KhachHang khachHang = khachHangDAO.getById(conn, hoaDon.getMaKhachHang());
                    // Bạn có thể muốn lưu trữ đối tượng khách hàng này trong HoaDon hoặc truyền riêng
                    // Hiện tại, chúng ta chỉ lấy nó.
                    if(khachHang != null) {
                        // Tùy chọn, thêm đối tượng KhachHang vào HoaDon nếu model HoaDon của bạn hỗ trợ
                        // ví dụ: hoaDon.setKhachHang(khachHang);
                    }
                }

                // Lấy tất cả chi tiết cho hóa đơn này
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.getByMaHoaDon(conn, maHoaDon); // Cần phương thức mới trong ChiTietHoaDonDAO
                // Điền thông tin sản phẩm cho từng chi tiết
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
                    // Bạn có thể muốn lưu trữ đối tượng SanPham này trong ChiTietHoaDon
                    // ví dụ: chiTiet.setSanPham(sanPham);
                }
                // Tùy chọn, thêm danh sách ChiTietHoaDon vào HoaDon nếu model HoaDon của bạn hỗ trợ
                // ví dụ: hoaDon.setChiTietHoaDonList(chiTietList);
            }
            return hoaDon;
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thông tin hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Lấy danh sách tất cả các hóa đơn.
     *
     * @return Một danh sách các đối tượng HoaDon.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public List<HoaDon> layTatCaHoaDon() throws SQLException {
            return hoaDonDAO.getAll(); // Giả sử getAll tồn tại và nhận connection

    }

    /**
     * Lấy danh sách hóa đơn theo số điện thoại của khách hàng vãng lai.
     *
     * @param sdt Số điện thoại của khách hàng vãng lai.
     * @return Một danh sách các đối tượng HoaDon.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public List<HoaDon> layHoaDonTheoSdtKhachHangVangLai(String sdt) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return hoaDonDAO.getHoaDonBySdt(conn, sdt);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo số điện thoại khách hàng vãng lai: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Cập nhật thông tin chính của một hóa đơn hiện có (không phải các mục chi tiết).
     *
     * @param hoaDon Đối tượng HoaDon với thông tin đã cập nhật (maHoaDon phải được đặt).
     * @return true nếu cập nhật thành công, false nếu ngược lại.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public boolean capNhatHoaDon(HoaDon hoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return hoaDonDAO.update(conn, hoaDon); // Giả sử update tồn tại trong HoaDonDAO
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Xóa một hóa đơn và tất cả các chi tiết liên quan của nó.
     * Phương thức này xử lý quản lý giao dịch để đảm bảo tính nguyên tử.
     *
     * @param maHoaDon ID của hóa đơn cần xóa.
     * @return true nếu xóa thành công, false nếu ngược lại.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữm liệu.
     */
    public boolean xoaHoaDon(int maHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Xóa ChiTietHoaDon trước
            boolean detailsDeleted = chiTietHoaDonDAO.deleteByMaHoaDon(conn, maHoaDon); // Cần phương thức mới trong ChiTietHoaDonDAO
            if (!detailsDeleted) {
                System.err.println("Không thể xóa chi tiết hóa đơn. Đang quay lại giao dịch.");
                conn.rollback();
                return false;
            }

            // 2. Xóa HoaDon
            boolean hoaDonDeleted = hoaDonDAO.delete(conn, maHoaDon); // Giả sử delete tồn tại trong HoaDonDAO
            if (!hoaDonDeleted) {
                System.err.println("Không thể xóa bản ghi hóa đơn chính. Đang quay lại giao dịch.");
                conn.rollback();
                return false;
            }

            conn.commit(); // Cam kết giao dịch
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Quay lại khi có lỗi
                    System.err.println("Giao dịch xóa hóa đơn đã được quay lại.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi quay lại giao dịch: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Đặt lại auto-commit
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Thêm một chi tiết mới vào hóa đơn hiện có.
     *
     * @param maHoaDon ID của hóa đơn để thêm chi tiết.
     * @param chiTiet Đối tượng ChiTietHoaDon để thêm.
     * @return Đối tượng ChiTietHoaDon mới được thêm, hoặc null nếu không thành công.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public ChiTietHoaDon themChiTietHoaDon(int maHoaDon, ChiTietHoaDon chiTiet) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Đảm bảo maHoaDon được đặt đúng cho chi tiết
            chiTiet.setMaHoaDon(maHoaDon);

            // Tính ThanhTien
            SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
            if (sanPham == null) {
                System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ".");
                return null;
            }
            int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
            chiTiet.setThanhTien(thanhTien);

            return chiTietHoaDonDAO.add(conn, chiTiet);
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm chi tiết hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Cập nhật một chi tiết hóa đơn hiện có.
     *
     * @param chiTiet Đối tượng ChiTietHoaDon với thông tin đã cập nhật (maChiTietHoaDon phải được đặt).
     * @return true nếu cập nhật thành công, false nếu ngược lại.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public boolean capNhatChiTietHoaDon(ChiTietHoaDon chiTiet) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            // Tính lại ThanhTien trong trường hợp SoLuong hoặc DonGia (của sản phẩm) thay đổi
            SanPham sanPham = sanPhamDAO.getById(conn, chiTiet.getMaSanPham());
            if (sanPham == null) {
                System.err.println("Không tìm thấy sản phẩm với ID " + chiTiet.getMaSanPham() + ". Không thể cập nhật chi tiết.");
                return false;
            }
            int thanhTien = sanPham.getDonGia() * chiTiet.getSoLuong();
            chiTiet.setThanhTien(thanhTien);

            return chiTietHoaDonDAO.update(conn, chiTiet);
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chi tiết hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Xóa một chi tiết hóa đơn cụ thể.
     *
     * @param maChiTietHoaDon ID của chi tiết hóa đơn cần xóa.
     * @return true nếu xóa thành công, false nếu ngược lại.
     * @throws SQLException nếu xảy ra lỗi truy cập cơ sở dữ liệu.
     */
    public boolean xoaChiTietHoaDon(int maChiTietHoaDon) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            return chiTietHoaDonDAO.delete(conn, maChiTietHoaDon);
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi đóng kết nối: " + ex.getMessage());
                }
            }
        }
    }

    // --- Bạn cũng có thể xem xét các phương thức tìm kiếm/lọc hóa đơn theo khoảng thời gian, nhân viên, v.v. ---
    // public List<HoaDon> timKiemHoaDonTheoKhoangNgay(Date startDate, Date endDate) throws SQLException { ... }
    // public List<HoaDon> timKiemHoaDonTheoNhanVien(String maNhanVien) throws SQLException { ... }

}