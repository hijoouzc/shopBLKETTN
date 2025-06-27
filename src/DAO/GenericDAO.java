package DAO;

import java.sql.*;
import java.util.List; 

/**
 * Interface DAO chung cho các thao tác CRUD cơ bản.
 * T: Kiểu của đối tượng Entity (POJO).
 * ID: Kiểu của ID (khóa chính) của Entity.
 *
 * CHÚ Ý: Các phương thức CRUD (add, getById, update, delete) giờ đây nhận một tham số Connection.
 * Điều này cho phép quản lý giao dịch ở tầng Service. Các phương thức này sẽ không tự động đóng Connection.
 * Phương thức getAll() vẫn tự quản lý Connection vì thường dùng cho các thao tác đọc độc lập.
 */
public interface GenericDAO<T, ID> {
    /**
     * Thêm một đối tượng entity mới vào cơ sở dữ liệu.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param entity Đối tượng entity cần thêm.
     * @return T Đối tượng entity đã được thêm (bao gồm ID được CSDL tạo),
     * hoặc null nếu thêm thất bại.
     */
    T add(Connection conn, T entity) throws SQLException;

    /**
     * Lấy một đối tượng entity từ cơ sở dữ liệu dựa trên ID của nó.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param id ID của entity cần tìm.
     * @return T Đối tượng entity nếu tìm thấy, ngược lại là null.
     */
    T getById(Connection conn, ID id) throws SQLException;

    /**
     * Cập nhật thông tin của một đối tượng entity hiện có trong cơ sở dữ liệu.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param entity Đối tượng entity chứa thông tin cập nhật.
     * @return boolean True nếu cập nhật thành công, False nếu thất bại.
     */
    boolean update(Connection conn, T entity) throws SQLException;

    /**
     * Xóa một đối tượng entity khỏi cơ sở dữ liệu dựa trên ID của nó.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param id ID của entity cần xóa.
     * @return boolean True nếu xóa thành công, False nếu thất bại.
     */
    boolean delete(Connection conn, ID id) throws SQLException;

    /**
     * Lấy tất cả các đối tượng entity thuộc loại này từ cơ sở dữ liệu.
     * Phương thức này tự quản lý Connection.
     * @return List<T> Danh sách các đối tượng entity.
     */
    List<T> getAll();
}