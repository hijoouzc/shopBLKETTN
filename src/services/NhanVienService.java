package services;

import DAO.NhanVienDAO;
import DAO.TaiKhoanNguoiDungDAO;
import models.NhanVien;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NhanVienService {
	private NhanVienDAO nhanVienDAO;
    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO; // Thêm DAO cho TaiKhoanNguoiDung
    private static NhanVienService instance;

    private NhanVienService() {
        this.nhanVienDAO = NhanVienDAO.getIns();
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns(); // Khởi tạo
    }

    public static NhanVienService getIns() {
        if (instance == null) {
            instance = new NhanVienService();
        }
        return instance;
    }

    /**
     * Thêm một nhân viên mới vào cơ sở dữ liệu.
     * @param nhanVien Đối tượng NhanVien cần thêm.
     * @return Đối tượng NhanVien đã được thêm (có thể có ID mới được sinh ra), hoặc null nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    public NhanVien addNhanVien(NhanVien nhanVien) throws SQLException {
        Connection conn = null;
        NhanVien addedNhanVien = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            addedNhanVien = nhanVienDAO.add(conn, nhanVien);
            conn.commit(); // Commit transaction nếu thành công
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi thêm nhân viên: " + e.getMessage());
            throw e; // Re-throw exception để controller xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return addedNhanVien;
    }

    /**
     * Lấy thông tin nhân viên theo mã nhân viên.
     * @param maNhanVien Mã nhân viên.
     * @return Đối tượng NhanVien, hoặc null nếu không tìm thấy.
     */
    public NhanVien getNhanVienById(String maNhanVien) {
        Connection conn = null;
        NhanVien nhanVien = null;
        try {
            conn = DatabaseConnection.getConnection();
            nhanVien = nhanVienDAO.getById(conn, maNhanVien);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhân viên theo ID: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return nhanVien;
    }

    /**
     * Lấy thông tin nhân viên theo mã người dùng liên kết.
     * @param maNguoiDung Mã người dùng.
     * @return Đối tượng NhanVien, hoặc null nếu không tìm thấy.
     */
    public NhanVien getNhanVienByMaNguoiDung(String maNguoiDung) {
        Connection conn = null;
        NhanVien nhanVien = null;
        try {
            conn = DatabaseConnection.getConnection();
            nhanVien = nhanVienDAO.getNhanVienByMaNguoiDung(conn, maNguoiDung);
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhân viên theo MaNguoiDung: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return nhanVien;
    }

    /**
     * Cập nhật thông tin nhân viên.
     * @param nhanVien Đối tượng NhanVien cần cập nhật.
     * @return True nếu cập nhật thành công, False nếu thất bại.
     */
    public boolean updateNhanVien(NhanVien nhanVien) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            success = nhanVienDAO.update(conn, nhanVien);
            conn.commit(); // Commit transaction nếu thành công
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi cập nhật nhân viên: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * Phương thức xóa nhân viên và tài khoản người dùng liên kết trong một giao dịch.
     * @param maNhanVien Mã nhân viên cần xóa.
     * @return True nếu xóa thành công cả nhân viên và tài khoản, False nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL xảy ra trong quá trình giao dịch.
     */
    public boolean deleteNhanVienAndAccount(String maNhanVien) throws SQLException {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            // 1. Lấy MaNguoiDung của nhân viên này trước khi xóa nhân viên
            NhanVien nhanVienToDelete = nhanVienDAO.getById(conn, maNhanVien);
            String maNguoiDung = null;
            if (nhanVienToDelete != null) {
                maNguoiDung = nhanVienToDelete.getMaNguoiDung();
            }

            // 2. Xóa nhân viên
            boolean nhanVienDeleted = nhanVienDAO.delete(conn, maNhanVien);

            if (nhanVienDeleted) {
                // 3. Nếu nhân viên đã được xóa, tiến hành xóa tài khoản liên kết (nếu có)
                if (maNguoiDung != null && !maNguoiDung.isEmpty() && !maNguoiDung.equals("N/A")) {
                    boolean taiKhoanDeleted = taiKhoanNguoiDungDAO.delete(conn, maNguoiDung);
                    if (taiKhoanDeleted) {
                        success = true;
                    } else {
                        System.err.println("Xóa nhân viên thành công nhưng xóa tài khoản thất bại cho MaNguoiDung: " + maNguoiDung);
                        conn.rollback(); // Hoàn tác nếu xóa tài khoản thất bại
                        success = false;
                    }
                } else {
                    // Nếu không có tài khoản liên kết, vẫn coi là thành công sau khi xóa nhân viên
                    success = true;
                }
                
                if (success) {
                    conn.commit(); // Commit giao dịch nếu tất cả thành công
                }

            } else {
                System.err.println("Không thể xóa nhân viên với MaNhanVien: " + maNhanVien);
                conn.rollback(); // Hoàn tác nếu xóa nhân viên thất bại
                success = false;
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Hoàn tác nếu có bất kỳ lỗi SQL nào
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi khi xóa nhân viên và tài khoản: " + e.getMessage());
            throw e; // Ném lỗi để tầng Controller xử lý
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return success;
    }


    /**
     * Lấy tất cả danh sách nhân viên từ cơ sở dữ liệu.
     * @return Danh sách các đối tượng NhanVien.
     */
    public List<NhanVien> getAllNhanViens() {
        List<NhanVien> nhanViens = null;
        nhanViens = nhanVienDAO.getAll();
  
        return nhanViens;
    }

    /**
     * Tìm kiếm nhân viên theo tên, CCCD hoặc SĐT.
     * Đây là một phương thức giả định, bạn cần triển khai DAO method tương ứng.
     * @param searchText Từ khóa tìm kiếm.
     * @return Danh sách các nhân viên khớp với từ khóa tìm kiếm.
     */
    public List<NhanVien> searchNhanVien(String searchText) {
        List<NhanVien> searchResults = null;
        searchResults = nhanVienDAO.getAll(); // Placeholder: returns all if search is not implemented

        return searchResults;
    }
}