package services;

import DAO.PhieuNhapHangDAO;
import DAO.ChiTietPhieuNhapDAO;
import DAO.SanPhamDAO;
import models.PhieuNhapHang;
import models.ChiTietPhieuNhap;
import models.SanPham;
import utils.DatabaseConnection;
import services.ViTriDungSanPhamService; // Đảm bảo import đúng Service này

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

/**
 * Lớp dịch vụ nghiệp vụ cho PhieuNhapHang.
 * Quản lý các nghiệp vụ liên quan đến phiếu nhập hàng, bao gồm lập, cập nhật, xóa,
 * và điều phối các thao tác với các DAO và Service khác trong một giao dịch.
 * Được triển khai theo mẫu Singleton.
 */
public class PhieuNhapHangService {
    private static PhieuNhapHangService instance;
    private PhieuNhapHangDAO phieuNhapHangDAO;
    private ChiTietPhieuNhapDAO chiTietPhieuNhapDAO;
    private SanPhamDAO sanPhamDAO;
    private ViTriDungSanPhamService viTriDungSanPhamService; // Service để cập nhật tồn kho

    // Private constructor để triển khai Singleton
    private PhieuNhapHangService() {
        this.phieuNhapHangDAO = PhieuNhapHangDAO.getIns(); // Khởi tạo DAO qua Singleton
        this.chiTietPhieuNhapDAO = ChiTietPhieuNhapDAO.getIns(); // Khởi tạo DAO qua Singleton
        this.sanPhamDAO = SanPhamDAO.getIns(); // Khởi tạo DAO qua Singleton
        this.viTriDungSanPhamService = ViTriDungSanPhamService.getIns(); // ViTriDungSanPhamService là Singleton
    }

    /**
     * Trả về thể hiện duy nhất của PhieuNhapHangService.
     * @return Thể hiện duy nhất của PhieuNhapHangService.
     */
    public static PhieuNhapHangService getIns() {
        if (instance == null) {
            instance = new PhieuNhapHangService();
        }
        return instance;
    }

    /**
     * Nghiệp vụ: Lập phiếu nhập hàng.
     * Tạo phiếu nhập và các chi tiết, đồng thời cập nhật số lượng tồn của sản phẩm.
     * Toàn bộ quy trình được bọc trong một giao dịch.
     *
     * @param phieuNhapHang Đối tượng PhieuNhapHang (chưa có MaPhieuNhap).
     * @param chiTietList Danh sách ChiTietPhieuNhap.
     * @return PhieuNhapHang nếu lập thành công (đối tượng trả về sẽ có MaPhieuNhap được sinh ra), null nếu thất bại.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu hoặc lỗi nghiệp vụ.
     */
    public PhieuNhapHang lapPhieuNhapHang(PhieuNhapHang phieuNhapHang, List<ChiTietPhieuNhap> chiTietList) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection(); // Lấy một kết nối cho giao dịch
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra dữ liệu đầu vào cơ bản
            if (phieuNhapHang == null || phieuNhapHang.getMaNhanVienThucHien() == null ||
                phieuNhapHang.getMaNhanVienThucHien().trim().isEmpty() ||
                chiTietList == null || chiTietList.isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu phiếu nhập hoặc chi tiết phiếu nhập không hợp lệ.");
                conn.rollback();
                return null;
            }

            // 2. Thêm phiếu nhập hàng vào CSDL
            PhieuNhapHang createdPhieuNhapHang = phieuNhapHangDAO.add(conn, phieuNhapHang);
            if (createdPhieuNhapHang == null || createdPhieuNhapHang.getMaPhieuNhap() == 0) {
                System.err.println("Lỗi: Thêm phiếu nhập hàng thất bại. Không nhận được mã phiếu nhập.");
                conn.rollback();
                return null;
            }
            // Gán lại mã phiếu nhập đã sinh ra vào đối tượng gốc được truyền vào
            phieuNhapHang.setMaPhieuNhap(createdPhieuNhapHang.getMaPhieuNhap());

            // 3. Thêm từng chi tiết phiếu nhập và cập nhật tồn kho sản phẩm
            for (ChiTietPhieuNhap ctpn : chiTietList) {
                ctpn.setMaPhieuNhap(phieuNhapHang.getMaPhieuNhap()); // Gán mã phiếu nhập cho chi tiết

                // Kiểm tra sản phẩm có tồn tại không trước khi thêm chi tiết và cập nhật tồn kho
                SanPham sanPham = sanPhamDAO.getById(conn, ctpn.getMaSanPham());
                if (sanPham == null) {
                    System.err.println("Lỗi nghiệp vụ: Sản phẩm với mã '" + ctpn.getMaSanPham() + "' không tồn tại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }

                // Thêm chi tiết phiếu nhập
                ChiTietPhieuNhap addedCtpn = chiTietPhieuNhapDAO.add(conn, ctpn);
                if (addedCtpn == null) {
                    System.err.println("Lỗi: Thêm chi tiết phiếu nhập cho SP '" + ctpn.getMaSanPham() + "' thất bại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }

                // Cập nhật số lượng tồn kho của sản phẩm bằng SanPhamDAO (tăng tồn kho)
                sanPham.setSoLuongTon(sanPham.getSoLuongTon() + ctpn.getSoLuong());
                boolean stockUpdated = sanPhamDAO.update(conn, sanPham);
                // ViTriDungSanPhamService.tangSoLuongTon(conn, ctpn.getMaSanPham(), ctpn.getSoLuong()); // Nếu sử dụng ViTriDungSanPhamService
                if (!stockUpdated) {
                    System.err.println("Lỗi: Cập nhật tồn kho cho SP '" + ctpn.getMaSanPham() + "' thất bại. Rollback giao dịch.");
                    conn.rollback();
                    return null;
                }
            }

            conn.commit(); // Cam kết giao dịch
            System.out.println("Phiếu nhập hàng và chi tiết đã được tạo thành công.");
            return phieuNhapHang; // Trả về đối tượng phiếu nhập đã được gán ID
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lập phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace(); // In stack trace để debug
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch lập phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Ném lại SQLException để Controller có thể bắt và hiển thị
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Đặt lại autocommit về true
                    conn.close(); // Đóng kết nối
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: Cập nhật phiếu nhập hàng.
     * Cập nhật thông tin phiếu nhập hàng và chi tiết, đồng thời điều chỉnh tồn kho.
     * Toàn bộ quy trình được bọc trong một giao dịch.
     *
     * @param phieuNhapHang Đối tượng PhieuNhapHang đã cập nhật.
     * @param newChiTietList Danh sách chi tiết mới (sẽ thay thế cái cũ, hoặc so sánh để cập nhật).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public boolean capNhatPhieuNhapHang(PhieuNhapHang phieuNhapHang, List<ChiTietPhieuNhap> newChiTietList) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Kiểm tra dữ liệu đầu vào cơ bản
            if (phieuNhapHang == null || phieuNhapHang.getMaPhieuNhap() == 0 ||
                phieuNhapHang.getMaNhanVienThucHien() == null || phieuNhapHang.getMaNhanVienThucHien().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu phiếu nhập không hợp lệ để cập nhật.");
                conn.rollback();
                return false;
            }

            // 1. Cập nhật thông tin chính của PhieuNhapHang
            boolean pnhUpdated = phieuNhapHangDAO.update(conn, phieuNhapHang);
            if (!pnhUpdated) {
                System.err.println("Lỗi: Không thể cập nhật thông tin phiếu nhập hàng chính.");
                conn.rollback();
                return false;
            }

            // 2. Xử lý các chi tiết phiếu nhập:
            // Lấy các chi tiết cũ để so sánh và hoàn tác tồn kho trước khi xóa
            List<ChiTietPhieuNhap> oldChiTietList = chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, phieuNhapHang.getMaPhieuNhap());

            // Hoàn tác tồn kho cho các mục cũ và xóa chúng khỏi DB
            if (oldChiTietList != null) {
                for (ChiTietPhieuNhap oldCtpn : oldChiTietList) {
                    // Giảm số lượng đã nhập khỏi tồn kho (hoàn tác)
                    SanPham oldSanPham = sanPhamDAO.getById(conn, oldCtpn.getMaSanPham());
                    if (oldSanPham == null) {
                        System.err.println("Lỗi: Sản phẩm cũ không tồn tại khi hoàn tác tồn kho: " + oldCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    oldSanPham.setSoLuongTon(oldSanPham.getSoLuongTon() - oldCtpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, oldSanPham);
                    // boolean updatedTon = viTriDungSanPhamService.giamSoLuongTon(conn, oldCtpn.getMaSanPham(), oldCtpn.getSoLuong()); // Nếu sử dụng ViTriDungSanPhamService
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể hoàn tác tồn kho khi cập nhật chi tiết phiếu nhập cho sản phẩm: " + oldCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Xóa chi tiết cũ khỏi DB
                    boolean ctpnDeleted = chiTietPhieuNhapDAO.delete(conn, oldCtpn.getMaChiTietPhieuNhap());
                    if (!ctpnDeleted) {
                        System.err.println("Lỗi: Không thể xóa chi tiết phiếu nhập cũ: " + oldCtpn.getMaChiTietPhieuNhap());
                        conn.rollback();
                        return false;
                    }
                }
            }


            // Thêm các chi tiết mới và cập nhật tồn kho
            if (newChiTietList != null) {
                for (ChiTietPhieuNhap newCtpn : newChiTietList) {
                    newCtpn.setMaPhieuNhap(phieuNhapHang.getMaPhieuNhap()); // Gán MaPhieuNhap

                    // Kiểm tra sản phẩm có tồn tại không
                    SanPham sanPham = sanPhamDAO.getById(conn, newCtpn.getMaSanPham());
                    if (sanPham == null) {
                        System.err.println("Lỗi nghiệp vụ: Sản phẩm với mã '" + newCtpn.getMaSanPham() + "' không tồn tại. Rollback giao dịch.");
                        conn.rollback();
                        return false;
                    }

                    ChiTietPhieuNhap addedCtpn = chiTietPhieuNhapDAO.add(conn, newCtpn);
                    if (addedCtpn == null) {
                        System.err.println("Lỗi: Không thể thêm chi tiết phiếu nhập mới cho sản phẩm: " + newCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Cập nhật số lượng tồn của sản phẩm (cộng thêm số lượng mới)
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() + newCtpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, sanPham);
                    // boolean updatedTon = viTriDungSanPhamService.tangSoLuongTon(conn, newCtpn.getMaSanPham(), newCtpn.getSoLuong()); // Nếu sử dụng ViTriDungSanPhamService
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể cập nhật tồn kho cho sản phẩm: " + newCtpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                }
            }


            conn.commit();
            System.out.println("Phiếu nhập hàng và chi tiết đã được cập nhật thành công.");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Ném lại SQLException
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
     * Nghiệp vụ: Xóa phiếu nhập hàng.
     * Xóa phiếu nhập và các chi tiết, đồng thời hoàn tác số lượng tồn của sản phẩm.
     * Toàn bộ quy trình được bọc trong một giao dịch.
     *
     * @param maPhieuNhap Mã phiếu nhập hàng cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public boolean xoaPhieuNhapHang(int maPhieuNhap) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Lấy tất cả chi tiết của phiếu nhập để hoàn tác tồn kho
            List<ChiTietPhieuNhap> chiTietList = chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, maPhieuNhap);
            // Xử lý trường hợp không tìm thấy chi tiết - nếu phiếu nhập không có chi tiết nào hoặc không tồn tại
            if (chiTietList == null || chiTietList.isEmpty()) {
                System.out.println("Không tìm thấy chi tiết phiếu nhập cho mã phiếu: " + maPhieuNhap + ". Tiếp tục xóa phiếu chính nếu tồn tại.");
                // Không rollback ở đây, tiếp tục xóa phiếu chính
            }

            // 2. Hoàn tác tồn kho và xóa chi tiết (chỉ nếu có chi tiết)
            if (chiTietList != null && !chiTietList.isEmpty()) {
                for (ChiTietPhieuNhap ctpn : chiTietList) {
                    // Trừ lại số lượng đã nhập từ tồn kho (hoàn tác)
                    SanPham sanPham = sanPhamDAO.getById(conn, ctpn.getMaSanPham());
                    if (sanPham == null) {
                        System.err.println("Lỗi: Sản phẩm liên quan đến chi tiết phiếu nhập không tồn tại khi xóa phiếu nhập: " + ctpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    sanPham.setSoLuongTon(sanPham.getSoLuongTon() - ctpn.getSoLuong());
                    boolean updatedTon = sanPhamDAO.update(conn, sanPham);
                    // boolean updatedTon = viTriDungSanPhamService.giamSoLuongTon(conn, ctpn.getMaSanPham(), ctpn.getSoLuong()); // Nếu sử dụng ViTriDungSanPhamService
                    if (!updatedTon) {
                        System.err.println("Lỗi: Không thể hoàn tác tồn kho khi xóa chi tiết phiếu nhập cho sản phẩm: " + ctpn.getMaSanPham());
                        conn.rollback();
                        return false;
                    }
                    // Xóa chi tiết phiếu nhập
                    boolean ctpnDeleted = chiTietPhieuNhapDAO.delete(conn, ctpn.getMaChiTietPhieuNhap());
                    if (!ctpnDeleted) {
                        System.err.println("Lỗi: Không thể xóa chi tiết phiếu nhập: " + ctpn.getMaChiTietPhieuNhap());
                        conn.rollback();
                        return false;
                    }
                }
            }


            // 3. Xóa phiếu nhập hàng chính
            boolean pnhDeleted = phieuNhapHangDAO.delete(conn, maPhieuNhap);
            if (!pnhDeleted) {
                System.err.println("Lỗi: Không thể xóa phiếu nhập hàng chính: " + maPhieuNhap + ". Có thể phiếu nhập không tồn tại hoặc có ràng buộc khóa ngoại khác.");
                conn.rollback();
                return false;
            }

            conn.commit(); // Cam kết giao dịch
            System.out.println("Đã xóa phiếu nhập hàng " + maPhieuNhap + " và hoàn tác tồn kho thành công.");
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi xóa phiếu nhập hàng: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch xóa phiếu nhập hàng.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e; // Ném lại SQLException
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
     * Nghiệp vụ: Tra cứu phiếu nhập hàng theo ID.
     * Lấy kết nối mới cho thao tác đọc đơn lẻ.
     *
     * @param maPhieuNhap Mã phiếu nhập hàng.
     * @return Đối tượng PhieuNhapHang nếu tìm thấy, null nếu không.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public PhieuNhapHang getPhieuNhapHangById(int maPhieuNhap) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Lấy kết nối mới
            return phieuNhapHangDAO.getById(conn, maPhieuNhap); // Truyền connection
        }
    }

    /**
     * Nghiệp vụ: Lấy tất cả phiếu nhập hàng.
     * Lấy kết nối mới cho thao tác đọc đơn lẻ.
     *
     * @return Danh sách tất cả phiếu nhập hàng.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public List<PhieuNhapHang> getAllPhieuNhapHang() throws SQLException {
        // getAll trong DAO đã tự quản lý kết nối.
        return phieuNhapHangDAO.getAll();
    }

    /**
     * Nghiệp vụ: Lấy danh sách chi tiết phiếu nhập theo mã phiếu nhập.
     * Lấy kết nối mới cho thao tác đọc đơn lẻ.
     *
     * @param maPhieuNhap Mã phiếu nhập.
     * @return Danh sách ChiTietPhieuNhap.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public List<ChiTietPhieuNhap> getChiTietPhieuNhap(int maPhieuNhap) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) { // Lấy kết nối mới
            return chiTietPhieuNhapDAO.getByMaPhieuNhap(conn, maPhieuNhap); // Truyền connection
        }
    }

    // Phương thức này hiện không cần thiết vì đã sử dụng SanPhamDAO trực tiếp để cập nhật tồn kho.
    // Nếu bạn muốn giữ lại lớp ViTriDungSanPhamService, cần đảm bảo nó có các phương thức tangSoLuongTon và giamSoLuongTon.
    // public boolean duyetPhieuNhapHang(int maPhieuNhap) throws SQLException { ... } // Không còn cần thiết nếu logic duyệt đã nằm trong lập/cập nhật
}
