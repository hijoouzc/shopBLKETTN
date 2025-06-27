package DAO;

import models.TaiKhoanNguoiDung;
import utils.DatabaseConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TaiKhoanNguoiDungDAO implements GenericDAO<TaiKhoanNguoiDung, String> {

    private static TaiKhoanNguoiDungDAO instance;

    // Private constructor to enforce Singleton pattern
    private TaiKhoanNguoiDungDAO() {
    }

	    // Static method to get the Singleton instance
	    public static TaiKhoanNguoiDungDAO getIns() {
	        if (instance == null) {
	            instance = new TaiKhoanNguoiDungDAO();
	        }
	        return instance;
	    }

    /**
     * Thêm một tài khoản người dùng mới vào cơ sở dữ liệu.
     * Vì MaNguoiDung và InternalID được tạo tự động bởi CSDL,
     * chúng ta sẽ chèn các trường còn lại và sau đó truy vấn lại để lấy đầy đủ thông tin tài khoản.
     *
     * @param conn Connection đến cơ sở dữ liệu.
     * @param account Đối tượng TaiKhoanNguoiDung cần thêm.
     * @return TaiKhoanNguoiDung Đối tượng TaiKhoanNguoiDung đã được thêm (bao gồm MaNguoiDung được CSDL tạo),
     * hoặc null nếu thêm thất bại.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    @Override
    public TaiKhoanNguoiDung add(Connection conn, TaiKhoanNguoiDung account) throws SQLException {
        String SQL_INSERT = "INSERT INTO TaiKhoanNguoiDung (Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan) VALUES (?, ?, ?, ?, ?, ?)";
        TaiKhoanNguoiDung newAccount = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getEmail());
            pstmt.setString(4, account.getLoaiNguoiDung());
            // Sử dụng java.sql.Timestamp cho cột DATETIME trong CSDL
            pstmt.setTimestamp(5, new Timestamp(account.getNgayTao().getTime()));
            pstmt.setString(6, account.getTrangThaiTaiKhoan());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Lấy InternalID được tạo tự động (là cột IDENTITY)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Do MaNguoiDung là computed column dựa trên InternalID,
                        // cách tốt nhất là truy vấn lại tài khoản dựa trên Username
                        // (vì Username là UNIQUE) để lấy MaNguoiDung đã được CSDL tạo.
                        // Truyền cùng một đối tượng connection để đảm bảo giao dịch
                        newAccount = getTaiKhoanByUsername(conn, account.getUsername());
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm tài khoản: " + ex.getMessage());
            throw ex; // Re-throw exception for Service layer to handle transaction rollback
        }
        return newAccount;
    }

    /**
     * Lấy thông tin tài khoản người dùng dựa trên MaNguoiDung.
     *
     * @param conn Connection đến cơ sở dữ liệu.
     * @param maNguoiDung Mã người dùng cần tìm (là ID).
     * @return TaiKhoanNguoiDung Đối tượng TaiKhoanNguoiDung nếu tìm thấy, ngược lại là null.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    @Override
    public TaiKhoanNguoiDung getById(Connection conn, String maNguoiDung) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE MaNguoiDung = ?";
        TaiKhoanNguoiDung account = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, maNguoiDung);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    account = new TaiKhoanNguoiDung();
                    account.setInternalID(rs.getInt("InternalID"));
                    account.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    account.setUsername(rs.getString("Username"));
                    account.setPassword(rs.getString("Password"));
                    account.setEmail(rs.getString("Email"));
                    account.setLoaiNguoiDung(rs.getString("LoaiNguoiDung"));
                    account.setNgayTao(new Date(rs.getTimestamp("NgayTao").getTime()));
                    account.setTrangThaiTaiKhoan(rs.getString("TrangThaiTaiKhoan"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tài khoản theo mã: " + ex.getMessage());
            throw ex; // Re-throw exception for Service layer to handle
        }
        return account;
    }

    /**
     * Cập nhật thông tin của một tài khoản người dùng hiện có.
     * Không cho phép cập nhật MaNguoiDung và InternalID vì chúng được CSDL quản lý.
     *
     * @param conn Connection đến cơ sở dữ liệu.
     * @param account Đối tượng TaiKhoanNguoiDung chứa thông tin cập nhật.
     * @return boolean True nếu cập nhật thành công, False nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    @Override
    public boolean update(Connection conn, TaiKhoanNguoiDung account) throws SQLException {
        String SQL_UPDATE = "UPDATE TaiKhoanNguoiDung SET Username = ?, Password = ?, Email = ?, LoaiNguoiDung = ?, TrangThaiTaiKhoan = ? WHERE MaNguoiDung = ?";
        boolean success = false;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, account.getUsername());
            pstmt.setString(2, account.getPassword());
            pstmt.setString(3, account.getEmail());
            pstmt.setString(4, account.getLoaiNguoiDung());
            pstmt.setString(5, account.getTrangThaiTaiKhoan());
            pstmt.setString(6, account.getMaNguoiDung()); // Dùng MaNguoiDung để xác định bản ghi

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật tài khoản: " + ex.getMessage());
            throw ex; // Re-throw exception for Service layer to handle
        }
        return success;
    }

    /**
     * Xóa một tài khoản người dùng khỏi cơ sở dữ liệu dựa trên MaNguoiDung.
     *
     * @param conn Connection đến cơ sở dữ liệu.
     * @param maNguoiDung Mã người dùng của tài khoản cần xóa.
     * @return boolean True nếu xóa thành công, False nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    @Override
    public boolean delete(Connection conn, String maNguoiDung) throws SQLException {
        String SQL_DELETE = "DELETE FROM TaiKhoanNguoiDung WHERE MaNguoiDung = ?";
        boolean success = false;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setString(1, maNguoiDung);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa tài khoản: " + ex.getMessage());
            throw ex; // Re-throw exception for Service layer to handle
        }
        return success;
    }

    /**
     * Lấy tất cả các tài khoản người dùng từ cơ sở dữ liệu.
     * Phương thức này tự quản lý Connection vì thường dùng cho các thao tác đọc độc lập.
     *
     * @return List<TaiKhoanNguoiDung> Danh sách các đối tượng TaiKhoanNguoiDung.
     */
    @Override
    public List<TaiKhoanNguoiDung> getAll() {
        List<TaiKhoanNguoiDung> taiKhoanList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung";

        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                TaiKhoanNguoiDung account = new TaiKhoanNguoiDung();
                account.setInternalID(rs.getInt("InternalID"));
                account.setMaNguoiDung(rs.getString("MaNguoiDung"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
                account.setEmail(rs.getString("Email"));
                account.setLoaiNguoiDung(rs.getString("LoaiNguoiDung"));
                account.setNgayTao(new Date(rs.getTimestamp("NgayTao").getTime()));
                account.setTrangThaiTaiKhoan(rs.getString("TrangThaiTaiKhoan"));
                taiKhoanList.add(account);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả tài khoản: " + ex.getMessage());
            ex.printStackTrace();
        }
        return taiKhoanList;
    }

    /**
     * Lấy thông tin tài khoản người dùng dựa trên Username.
     * Thường dùng cho chức năng đăng nhập hoặc kiểm tra trùng lặp username.
     * (Đây là một phương thức cụ thể, không nằm trong GenericDAO)
     *
     * @param conn Connection đến cơ sở dữ liệu.
     * @param username Tên đăng nhập cần tìm.
     * @return TaiKhoanNguoiDung Đối tượng TaiKhoanNguoiDung nếu tìm thấy, ngược lại là null.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    public TaiKhoanNguoiDung getTaiKhoanByUsername(Connection conn, String username) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNguoiDung, Username, Password, Email, LoaiNguoiDung, NgayTao, TrangThaiTaiKhoan FROM TaiKhoanNguoiDung WHERE Username = ?";
        TaiKhoanNguoiDung account = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    account = new TaiKhoanNguoiDung();
                    account.setInternalID(rs.getInt("InternalID"));
                    account.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    account.setUsername(rs.getString("Username"));
                    account.setPassword(rs.getString("Password"));
                    account.setEmail(rs.getString("Email"));
                    account.setLoaiNguoiDung(rs.getString("LoaiNguoiDung"));
                    account.setNgayTao(new Date(rs.getTimestamp("NgayTao").getTime()));
                    account.setTrangThaiTaiKhoan(rs.getString("TrangThaiTaiKhoan"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tài khoản theo username: " + ex.getMessage());
            throw ex; // Re-throw exception for Service layer to handle
        }
        return account;
    }
}
