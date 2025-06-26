package DAO;

import models.ChiTietHoaDon;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO implements GenericDAO<ChiTietHoaDon, Integer> {

    private static ChiTietHoaDonDAO instance; // Singleton instance

    // Private constructor to enforce Singleton pattern
    private ChiTietHoaDonDAO() {
    }

    // Static method to get the Singleton instance
    public static ChiTietHoaDonDAO getIns() {
        if (instance == null) {
            instance = new ChiTietHoaDonDAO();
        }
        return instance;
    }

    @Override
    public ChiTietHoaDon add(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        String SQL_INSERT = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, ThanhTien) VALUES (?, ?, ?, ?)";
        ChiTietHoaDon newChiTietHoaDon = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, chiTietHoaDon.getMaHoaDon());
            pstmt.setString(2, chiTietHoaDon.getMaSanPham());
            pstmt.setInt(3, chiTietHoaDon.getSoLuong());
            pstmt.setInt(4, chiTietHoaDon.getThanhTien());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newChiTietHoaDon = getById(conn, id);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm chi tiết hóa đơn: " + ex.getMessage());
            throw ex; // Ném lại ngoại lệ để Service có thể xử lý rollback
        }
        return newChiTietHoaDon;
    }

    @Override
    public ChiTietHoaDon getById(Connection conn, Integer id) throws SQLException {
        String SQL_SELECT = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, ThanhTien FROM ChiTietHoaDon WHERE MaChiTietHoaDon = ?";
        ChiTietHoaDon chiTietHoaDon = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    chiTietHoaDon = new ChiTietHoaDon();
                    chiTietHoaDon.setMaChiTietHoaDon(rs.getInt("MaChiTietHoaDon"));
                    chiTietHoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                    chiTietHoaDon.setMaSanPham(rs.getString("MaSanPham"));
                    chiTietHoaDon.setSoLuong(rs.getInt("SoLuong"));
                    chiTietHoaDon.setThanhTien(rs.getInt("ThanhTien"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn theo ID: " + ex.getMessage());
            throw ex;
        }
        return chiTietHoaDon;
    }

    @Override
    public boolean update(Connection conn, ChiTietHoaDon chiTietHoaDon) throws SQLException {
        String SQL_UPDATE = "UPDATE ChiTietHoaDon SET MaHoaDon = ?, MaSanPham = ?, SoLuong = ?, ThanhTien = ? WHERE MaChiTietHoaDon = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            pstmt.setInt(1, chiTietHoaDon.getMaHoaDon());
            pstmt.setString(2, chiTietHoaDon.getMaSanPham());
            pstmt.setInt(3, chiTietHoaDon.getSoLuong());
            pstmt.setInt(4, chiTietHoaDon.getThanhTien());
            pstmt.setInt(5, chiTietHoaDon.getMaChiTietHoaDon());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật chi tiết hóa đơn: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer id) throws SQLException {
        String SQL_DELETE = "DELETE FROM ChiTietHoaDon WHERE MaChiTietHoaDon = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    /**
     * Lấy tất cả các chi tiết hóa đơn liên quan đến một hóa đơn cụ thể.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param maHoaDon Mã hóa đơn cần tìm.
     * @return Danh sách ChiTietHoaDon.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public List<ChiTietHoaDon> getByMaHoaDon(Connection conn, int maHoaDon) throws SQLException {
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        String SQL_SELECT = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, ThanhTien FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
                    chiTietHoaDon.setMaChiTietHoaDon(rs.getInt("MaChiTietHoaDon"));
                    chiTietHoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                    chiTietHoaDon.setMaSanPham(rs.getString("MaSanPham"));
                    chiTietHoaDon.setSoLuong(rs.getInt("SoLuong"));
                    chiTietHoaDon.setThanhTien(rs.getInt("ThanhTien"));
                    chiTietList.add(chiTietHoaDon);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn theo MaHoaDon: " + ex.getMessage());
            throw ex;
        }
        return chiTietList;
    }

    /**
     * Xóa tất cả các chi tiết hóa đơn liên quan đến một hóa đơn cụ thể.
     * @param conn Connection đến cơ sở dữ liệu.
     * @param maHoaDon Mã hóa đơn cần xóa chi tiết.
     * @return true nếu xóa thành công, false nếu ngược lại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public boolean deleteByMaHoaDon(Connection conn, int maHoaDon) throws SQLException {
        String SQL_DELETE_BY_HOADON = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE_BY_HOADON)) {
            pstmt.setInt(1, maHoaDon);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa chi tiết hóa đơn theo MaHoaDon: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<ChiTietHoaDon> getAll() {
        // Phương thức này thường tự quản lý Connection, nhưng trong context giao dịch,
        // bạn nên cân nhắc tạo một phiên bản get All có tham số Connection nếu cần trong giao dịch
        List<ChiTietHoaDon> chiTietHoaDonList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaChiTietHoaDon, MaHoaDon, MaSanPham, SoLuong, ThanhTien FROM ChiTietHoaDon";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                ChiTietHoaDon chiTietHoaDon = new ChiTietHoaDon();
                chiTietHoaDon.setMaChiTietHoaDon(rs.getInt("MaChiTietHoaDon"));
                chiTietHoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                chiTietHoaDon.setMaSanPham(rs.getString("MaSanPham"));
                chiTietHoaDon.setSoLuong(rs.getInt("SoLuong"));
                chiTietHoaDon.setThanhTien(rs.getInt("ThanhTien"));
                chiTietHoaDonList.add(chiTietHoaDon);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả chi tiết hóa đơn: " + ex.getMessage());
            // Không throw ex vì đây là getAll tự quản lý connection
        }
        return chiTietHoaDonList;
    }
}
