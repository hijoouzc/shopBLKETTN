package services;

import DAO.LoaiSanPhamDAO; // Import the LoaiSanPhamDAO
import DAO.SanPhamDAO; // Import the SanPhamDAO

import models.SanPham;
import models.LoaiSanPham;

import utils.DatabaseConnection; // Utility for database connection

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class SanPhamService {
    private static SanPhamService instance; // Singleton instance
    private SanPhamDAO sanPhamDAO;
    private LoaiSanPhamDAO loaiSanPhamDAO;
    /**
     * Private constructor to implement the Singleton pattern.
     * Initializes the DAO instances.
     */
    private SanPhamService() {
        this.sanPhamDAO = new SanPhamDAO();
        this.loaiSanPhamDAO = new LoaiSanPhamDAO();
    }

    /**
     * Provides the singleton instance of SanPhamService.
     *
     * @return The single instance of SanPhamService.
     */
    public static SanPhamService getIns() {
        if (instance == null) {
            instance = new SanPhamService();
        }
        return instance;
    }

    // --- NGHIỆP VỤ SẢN PHẨM ---

    /**
     * Nghiệp vụ: Thêm sản phẩm mới.
     * Kiểm tra tên sản phẩm có trùng không và mã loại sản phẩm có tồn tại không.
     *
     * @param sanPham Đối tượng SanPham chứa thông tin sản phẩm.
     * @return SanPham nếu thêm thành công (đã có MaSanPham được gán), null nếu thất bại.
     */
    public SanPham themSanPham(SanPham sanPham) {
        Connection conn = null;
        SanPham newSanPham = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra mã loại sản phẩm có tồn tại không
            LoaiSanPham loaiSanPham = loaiSanPhamDAO.getById(conn, sanPham.getMaLoaiSanPham());
            if (loaiSanPham == null) {
                System.err.println("Lỗi: Mã loại sản phẩm '" + sanPham.getMaLoaiSanPham() + "' không tồn tại.");
                conn.rollback();
                return null;
            }

            // 2. Kiểm tra tên sản phẩm có trùng không
            // Giả định SanPhamDAO có phương thức findByTenSanPham(Connection conn, String tenSanPham)
            // Nếu không có, cần thêm vào SanPhamDAO hoặc duyệt getAll() (kém hiệu quả)
            // Hiện tại, SanPhamDAO không có getSanPhamByTen, nên cần bổ sung hoặc xử lý khác.
            // Để đơn giản hóa, tôi sẽ sử dụng getAll() để kiểm tra tên, nhưng khuyến nghị thêm phương thức findByTenSanPham vào DAO.
            List<SanPham> allSanPhams = sanPhamDAO.getAll(); // getAll() tự quản lý kết nối, nên không dùng conn của transaction
            for (SanPham sp : allSanPhams) {
                if (sp.getTenSanPham().equalsIgnoreCase(sanPham.getTenSanPham())) {
                    System.err.println("Lỗi: Tên sản phẩm '" + sanPham.getTenSanPham() + "' đã tồn tại.");
                    conn.rollback();
                    return null;
                }
            }


            // 3. Thêm sản phẩm mới
            // Phương thức add của DAO sẽ cập nhật MaSanPham vào đối tượng sanPham
            newSanPham = sanPhamDAO.add(conn, sanPham); 
            if (newSanPham != null) {
                conn.commit(); // Cam kết giao dịch
                return newSanPham;
            } else {
                conn.rollback(); // Hoàn tác giao dịch
                System.err.println("Lỗi: Thêm sản phẩm thất bại.");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch thêm sản phẩm.");
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
     * Nghiệp vụ: Cập nhật thông tin sản phẩm.
     * Không cho phép thay đổi MaSanPham.
     * Kiểm tra tên sản phẩm mới (nếu thay đổi) có trùng với sản phẩm khác không.
     * Kiểm tra mã loại sản phẩm mới có tồn tại không.
     *
     * @param sanPham Đối tượng SanPham với thông tin cập nhật (phải có MaSanPham).
     * @return true nếu cập nhật thành công, false nếu thất bại.
     */
    public boolean capNhatSanPham(SanPham sanPham) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Kiểm tra sản phẩm có tồn tại không
            SanPham existingSanPham = sanPhamDAO.getById(conn, sanPham.getMaSanPham());
            if (existingSanPham == null) {
                System.err.println("Lỗi: Không tìm thấy sản phẩm với mã: " + sanPham.getMaSanPham() + " để cập nhật.");
                conn.rollback();
                return false;
            }

            // 2. Kiểm tra mã loại sản phẩm mới có tồn tại không (nếu thay đổi)
            if (!existingSanPham.getMaLoaiSanPham().equals(sanPham.getMaLoaiSanPham())) {
                LoaiSanPham loaiSanPham = loaiSanPhamDAO.getById(conn, sanPham.getMaLoaiSanPham());
                if (loaiSanPham == null) {
                    System.err.println("Lỗi: Mã loại sản phẩm mới '" + sanPham.getMaLoaiSanPham() + "' không tồn tại.");
                    conn.rollback();
                    return false;
                }
            }

            // 3. Kiểm tra tên sản phẩm mới (nếu thay đổi) có trùng với sản phẩm khác không
            if (!existingSanPham.getTenSanPham().equalsIgnoreCase(sanPham.getTenSanPham())) {
                // Lại dùng getAll() tạm thời cho đến khi có findByTenSanPham(conn, tenSanPham) trong SanPhamDAO
                List<SanPham> allSanPhams = sanPhamDAO.getAll();
                for (SanPham sp : allSanPhams) {
                    if (sp.getTenSanPham().equalsIgnoreCase(sanPham.getTenSanPham()) && !sp.getMaSanPham().equals(sanPham.getMaSanPham())) {
                        System.err.println("Lỗi: Tên sản phẩm '" + sanPham.getTenSanPham() + "' đã được sử dụng bởi sản phẩm khác.");
                        conn.rollback();
                        return false;
                    }
                }
            }

            // 4. Cập nhật thông tin sản phẩm
            boolean updated = sanPhamDAO.update(conn, sanPham);
            conn.commit(); // Cam kết giao dịch
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác giao dịch
                    System.err.println("Đã rollback giao dịch cập nhật sản phẩm.");
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
     * Nghiệp vụ: Lấy thông tin sản phẩm theo mã.
     *
     * @param maSanPham Mã sản phẩm.
     * @return SanPham nếu tìm thấy, null nếu không.
     */
    public SanPham getSanPhamById(String maSanPham) {
        try (Connection conn = DatabaseConnection.getConnection()) { // Tạo kết nối mới cho thao tác đọc độc lập
            return sanPhamDAO.getById(conn, maSanPham);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy sản phẩm theo ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Nghiệp vụ: Lấy tất cả sản phẩm.
     *
     * @return Danh sách tất cả sản phẩm.
     */
    public List<SanPham> getAllSanPham() {
        return sanPhamDAO.getAll(); // DAO.getAll() tự quản lý kết nối
    }
    
    public List<SanPham> searchSanPhamByName(String name) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Gọi phương thức searchByName từ SanPhamDAO để tìm kiếm trực tiếp trong CSDL
            return sanPhamDAO.searchByName(conn, name); 
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tìm kiếm sản phẩm theo tên: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public SanPham getSanPhamByTen(String tenSanPham) {
        // Tương tự, có thể cần phương thức này trong SanPhamDAO
        try (Connection conn = DatabaseConnection.getConnection()) {
            return sanPhamDAO.getSanPhamByTen(conn, tenSanPham); // Assume sanPhamDAO has this method
        } catch (SQLException e) {
            System.err.println("Service: Lỗi SQL khi lấy sản phẩm theo tên: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

	public boolean xoaSanPham(String maSanPhamToDelete) {
		// TODO Auto-generated method stub
		return false;
	}
    
    
}
