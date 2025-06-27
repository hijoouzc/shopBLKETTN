package DAO;

import models.SanPham;
import utils.DatabaseConnection; // Updated to utils.DatabaseConnection

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO implements GenericDAO<SanPham, String> {

    private static SanPhamDAO instance;

    // Private constructor to prevent direct instantiation
    public SanPhamDAO() {
        // No explicit connection held here, as methods will receive it
    }

    // Static method to get the Singleton instance
    public static SanPhamDAO getIns() {
        if (instance == null) {
            instance = new SanPhamDAO();
        }
        return instance;
    }

    // Helper method to map ResultSet to SanPham object, including stock
    private SanPham mapResultSetToSanPham(Connection conn, ResultSet rs) throws SQLException {
        SanPham sanPham = new SanPham();
        sanPham.setInternalID(rs.getInt("InternalID"));
        sanPham.setMaSanPham(rs.getString("MaSanPham"));
        sanPham.setTenSanPham(rs.getString("TenSanPham"));
        sanPham.setDonGia(rs.getInt("DonGia"));
        sanPham.setNgaySanXuat(rs.getDate("NgaySanXuat"));
        sanPham.setThongSoKyThuat(rs.getString("ThongSoKyThuat"));
        sanPham.setMaLoaiSanPham(rs.getString("MaLoaiSanPham"));
        
        // Lấy số lượng tồn kho từ ChiTietViTri
        int stock = getProductTotalStock(conn, sanPham.getMaSanPham());
        sanPham.setSoLuongTon(stock);

        return sanPham;
    }

    /**
     * Retrieves the total stock quantity for a given product from ChiTietViTri.
     * This method is used internally by DAO read operations.
     * @param conn Database connection.
     * @param maSanPham The product ID.
     * @return The total stock quantity, or 0 if not found/no stock.
     * @throws SQLException If a database access error occurs.
     */
    public int getProductTotalStock(Connection conn, String maSanPham) throws SQLException {
        String SQL_SELECT_STOCK = "SELECT ISNULL(SUM(SoLuong), 0) FROM ChiTietViTri WHERE MaSanPham = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_STOCK)) {
            pstmt.setString(1, maSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // SUM(SoLuong)
                }
            }
        }
        return 0; // Should not reach here if SUM always returns a row, but as a safeguard
    }

    @Override
    public SanPham add(Connection conn, SanPham sanPham) throws SQLException {
        String SQL_INSERT = "INSERT INTO SanPham (TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham) VALUES (?, ?, ?, ?, ?)";
        SanPham newSanPham = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, sanPham.getTenSanPham());
            pstmt.setInt(2, sanPham.getDonGia());
            if (sanPham.getNgaySanXuat() != null) {
                pstmt.setDate(3, new java.sql.Date(sanPham.getNgaySanXuat().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            if (sanPham.getThongSoKyThuat() != null) {
                pstmt.setString(4, sanPham.getThongSoKyThuat());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }
            pstmt.setString(5, sanPham.getMaLoaiSanPham());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int internalId = generatedKeys.getInt(1);
                        String maSanPham = "SP" + String.format("%04d", internalId);
                        newSanPham = getById(conn, maSanPham); // Fetch complete SanPham including computed MaSanPham and stock
                        if (newSanPham != null) {
                            sanPham.setInternalID(newSanPham.getInternalID());
                            sanPham.setMaSanPham(newSanPham.getMaSanPham());
                            sanPham.setSoLuongTon(newSanPham.getSoLuongTon()); // Set stock from fetched object
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm sản phẩm: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return newSanPham;
    }

    @Override
    public SanPham getById(Connection conn, String maSanPham) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE MaSanPham = ?";
        SanPham sanPham = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, maSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    sanPham = mapResultSetToSanPham(conn, rs); // Use helper to map and get stock
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy sản phẩm theo mã: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return sanPham;
    }

    @Override
    public boolean update(Connection conn, SanPham sanPham) throws SQLException {
        // Lưu ý: SoLuongTon không được cập nhật trực tiếp qua đây, mà qua PhieuNhap/HoaDon
        String SQL_UPDATE = "UPDATE SanPham SET TenSanPham = ?, DonGia = ?, NgaySanXuat = ?, ThongSoKyThuat = ?, MaLoaiSanPham = ? WHERE MaSanPham = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, sanPham.getTenSanPham());
            pstmt.setInt(2, sanPham.getDonGia());
            if (sanPham.getNgaySanXuat() != null) {
                pstmt.setDate(3, new java.sql.Date(sanPham.getNgaySanXuat().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            if (sanPham.getThongSoKyThuat() != null) {
                pstmt.setString(4, sanPham.getThongSoKyThuat());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }
            pstmt.setString(5, sanPham.getMaLoaiSanPham());
            pstmt.setString(6, sanPham.getMaSanPham());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật sản phẩm: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, String maSanPham) throws SQLException {
        String SQL_DELETE = "DELETE FROM SanPham WHERE MaSanPham = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setString(1, maSanPham);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa sản phẩm: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return success;
    }

    @Override
    public List<SanPham> getAll() { // Changed to accept Connection parameter
        List<SanPham> sanPhamList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham";
        try (Connection conn = DatabaseConnection.getConnection();
        	Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                sanPhamList.add(mapResultSetToSanPham(conn, rs)); // Use helper to map and get stock
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả sản phẩm: " + ex.getMessage());
        }
        return sanPhamList;
    }

    /**
     * Checks if a product name already exists in the database.
     *
     * @param conn Connection object for the database.
     * @param tenSanPham The product name to check.
     * @return true if the product name exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean checkExistsByTen(Connection conn, String tenSanPham) throws SQLException {
        String SQL = "SELECT COUNT(*) FROM SanPham WHERE TenSanPham = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, tenSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi kiểm tra tên sản phẩm tồn tại: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return false;
    }

    /**
     * Checks if a product name already exists for a different product in the database.
     * Used for update operations to allow an item to keep its own name.
     *
     * @param conn Connection object for the database.
     * @param tenSanPham The product name to check.
     * @param excludeMaSanPham The MaSanPham to exclude from the check (i.e., the current product being updated).
     * @return true if the product name exists for a *different* product, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean checkExistsByTenForUpdate(Connection conn, String tenSanPham, String excludeMaSanPham) throws SQLException {
        String SQL = "SELECT COUNT(*) FROM SanPham WHERE TenSanPham = ? AND MaSanPham <> ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, tenSanPham);
            pstmt.setString(2, excludeMaSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi kiểm tra tên sản phẩm tồn tại để cập nhật: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return false;
    }

    /**
     * Retrieves a product by its exact name.
     *
     * @param conn Connection object for the database.
     * @param tenSanPham The exact product name to search for.
     * @return The SanPham object if found, null otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public SanPham getSanPhamByTen(Connection conn, String tenSanPham) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE TenSanPham = ?";
        SanPham sanPham = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, tenSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    sanPham = mapResultSetToSanPham(conn, rs); // Use helper to map and get stock
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy sản phẩm theo tên: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return sanPham;
    }
    
    public List<SanPham> searchByName(Connection conn, String keyword) throws SQLException {
        List<SanPham> sanPhamList = new ArrayList<>();
        String SQL_SEARCH = "SELECT InternalID, MaSanPham, TenSanPham, DonGia, NgaySanXuat, ThongSoKyThuat, MaLoaiSanPham FROM SanPham WHERE TenSanPham LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SEARCH)) {
            // Use %keyword% for partial, case-insensitive matching
            pstmt.setString(1, "%" + keyword + "%"); 
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sanPhamList.add(mapResultSetToSanPham(conn, rs)); // Use helper to map and get stock
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi tìm kiếm sản phẩm theo tên: " + ex.getMessage());
            throw ex; // Re-throw the exception
        }
        return sanPhamList;
    }
}
