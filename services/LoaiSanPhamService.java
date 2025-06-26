package services; 

import DAO.LoaiSanPhamDAO;
import DAO.SanPhamDAO;
import models.LoaiSanPham;
import models.SanPham;     
import utils.DatabaseConnection; 

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * Lớp LoaiSanPhamService xử lý các logic nghiệp vụ liên quan đến LoaiSanPham.
 * Nó tương tác với LoaiSanPhamDAO và SanPhamDAO để đảm bảo tính toàn vẹn dữ liệu.
 */
public class LoaiSanPhamService {
    private static LoaiSanPhamService instance; // Singleton instance
    private LoaiSanPhamDAO loaiSanPhamDAO;
    private SanPhamDAO sanPhamDAO; // Dùng để kiểm tra ràng buộc khóa ngoại

    /**
     * Private constructor để triển khai mẫu Singleton.
     * Khởi tạo các instance của DAO.
     */
    private LoaiSanPhamService() {
        this.loaiSanPhamDAO = new LoaiSanPhamDAO();
        this.sanPhamDAO = new SanPhamDAO();
    }

    /**
     * Cung cấp instance Singleton của LoaiSanPhamService.
     *
     * @return Instance duy nhất của LoaiSanPhamService.
     */
    public static LoaiSanPhamService getIns() {
        if (instance == null) {
            instance = new LoaiSanPhamService();
        }
        return instance;
    }

    // --- NGHIỆP VỤ LOẠI SẢN PHẨM ---

    /**
     * Nghiệp vụ: Thêm loại sản phẩm mới.
     * Kiểm tra tên loại sản phẩm có trùng không.
     *
     * @param loaiSanPham Đối tượng LoaiSanPham chứa thông tin loại sản phẩm.
     * @return LoaiSanPham nếu thêm thành công (đã có MaLoaiSanPham được gán), null nếu thất bại.
     */
    public LoaiSanPham themLoaiSanPham(LoaiSanPham loaiSanPham) {
        Connection conn = null;
        LoaiSanPham newLoaiSanPham = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra tên loại sản phẩm có trùng không
            LoaiSanPham existingByName = loaiSanPhamDAO.getLoaiSanPhamByTen(conn, loaiSanPham.getTenLoaiSanPham());
            if (existingByName != null) {
                System.err.println("Lỗi: Tên loại sản phẩm '" + loaiSanPham.getTenLoaiSanPham() + "' đã tồn tại.");
                conn.rollback();
                return null;
            }

            // 2. Thêm loại sản phẩm mới
            newLoaiSanPham = loaiSanPhamDAO.add(conn, loaiSanPham);
            if (newLoaiSanPham != null) {
                conn.commit(); // Cam kết giao dịch
                System.out.println("Service: Thêm loại sản phẩm thành công: " + newLoaiSanPham.getTenLoaiSanPham());
                return newLoaiSanPham;
            } else {
                conn.rollback(); // Hoàn tác giao dịch
                System.err.println("Service: Thêm loại sản phẩm thất bại.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi thêm loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch thêm loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return null;
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
     * Nghiệp vụ: Cập nhật thông tin loại sản phẩm.
     * Không cho phép thay đổi MaLoaiSanPham.
     * Kiểm tra tên loại sản phẩm mới (nếu thay đổi) có trùng với loại sản phẩm khác không.
     *
     * @param loaiSanPham Đối tượng LoaiSanPham với thông tin cập nhật (phải có MaLoaiSanPham).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean capNhatLoaiSanPham(LoaiSanPham loaiSanPham) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra loại sản phẩm có tồn tại không
            LoaiSanPham existingLoaiSanPham = loaiSanPhamDAO.getById(conn, loaiSanPham.getMaLoaiSanPham());
            if (existingLoaiSanPham == null) {
                System.err.println("Lỗi: Không tìm thấy loại sản phẩm với mã: " + loaiSanPham.getMaLoaiSanPham() + " để cập nhật.");
                conn.rollback();
                return false;
            }

            // 2. Kiểm tra tên loại sản phẩm mới (nếu thay đổi) có trùng với loại sản phẩm khác không
            if (!existingLoaiSanPham.getTenLoaiSanPham().equalsIgnoreCase(loaiSanPham.getTenLoaiSanPham())) {
                LoaiSanPham loaiSanPhamWithSameName = loaiSanPhamDAO.getLoaiSanPhamByTen(conn, loaiSanPham.getTenLoaiSanPham());
                if (loaiSanPhamWithSameName != null && !loaiSanPhamWithSameName.getMaLoaiSanPham().equals(loaiSanPham.getMaLoaiSanPham())) {
                    System.err.println("Lỗi: Tên loại sản phẩm '" + loaiSanPham.getTenLoaiSanPham() + "' đã được sử dụng bởi loại sản phẩm khác.");
                    conn.rollback();
                    return false;
                }
            }

            // 3. Cập nhật thông tin loại sản phẩm
            boolean updated = loaiSanPhamDAO.update(conn, loaiSanPham);
            if (updated) {
                conn.commit(); // Cam kết giao dịch
                System.out.println("Service: Cập nhật loại sản phẩm thành công: " + loaiSanPham.getTenLoaiSanPham());
                return true;
            } else {
                conn.rollback(); // Hoàn tác giao dịch
                System.err.println("Service: Cập nhật loại sản phẩm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi cập nhật loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch cập nhật loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
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
     * Nghiệp vụ: Xóa loại sản phẩm.
     * Cần kiểm tra xem có sản phẩm nào thuộc loại này không.
     * Nếu có, không cho phép xóa.
     *
     * @param maLoaiSanPham Mã loại sản phẩm cần xóa.
     * @return true nếu xóa thành công, false nếu thất bại.
     */
    public boolean xoaLoaiSanPham(String maLoaiSanPham) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra xem có sản phẩm nào thuộc loại này không
            // Giả định SanPhamDAO có phương thức getSanPhamByMaLoaiSanPham(Connection conn, String maLoaiSanPham)
            // Nếu không có, cần thêm vào SanPhamDAO hoặc duyệt getAll() (kém hiệu quả)
            List<SanPham> sanPhamList = sanPhamDAO.getAll(); // getAll() tự quản lý kết nối, nên không dùng conn của transaction
            for (SanPham sp : sanPhamList) {
                if (sp.getMaLoaiSanPham() != null && sp.getMaLoaiSanPham().equals(maLoaiSanPham)) {
                    System.err.println("Lỗi: Không thể xóa loại sản phẩm '" + maLoaiSanPham + "'. Có sản phẩm đang thuộc loại này.");
                    conn.rollback();
                    return false;
                }
            }

            // 2. Xóa loại sản phẩm
            boolean deleted = loaiSanPhamDAO.delete(conn, maLoaiSanPham);
            if (deleted) {
                conn.commit(); // Cam kết giao dịch
                System.out.println("Service: Xóa loại sản phẩm thành công: " + maLoaiSanPham);
                return true;
            } else {
                conn.rollback(); // Hoàn tác giao dịch
                System.err.println("Service: Xóa loại sản phẩm thất bại.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi xóa loại sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch xóa loại sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
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
     * Nghiệp vụ: Lấy thông tin loại sản phẩm theo mã.
     *
     * @param maLoaiSanPham Mã loại sản phẩm.
     * @return LoaiSanPham nếu tìm thấy, null nếu không.
     */
    public LoaiSanPham getLoaiSanPhamById(String maLoaiSanPham) {
        try (Connection conn = DatabaseConnection.getConnection()) { // Tạo kết nối mới cho thao tác đọc độc lập
            return loaiSanPhamDAO.getById(conn, maLoaiSanPham);
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy loại sản phẩm theo ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: Lấy tất cả loại sản phẩm.
     *
     * @return Danh sách tất cả loại sản phẩm.
     */
    public List<LoaiSanPham> getAllLoaiSanPham() {
        return loaiSanPhamDAO.getAll(); // DAO.getAll() tự quản lý kết nối
    }
}
