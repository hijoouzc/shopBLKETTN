package DAO;

import models.ChiTietViTri;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietViTriDAO implements GenericDAO<ChiTietViTri, Integer> {
    
    private static ChiTietViTriDAO instance;
    public static ChiTietViTriDAO getIns() {
        if (instance == null) {
            instance = new ChiTietViTriDAO();
        }
        return instance;
    }

    @Override
    public ChiTietViTri add(Connection conn, ChiTietViTri chiTiet) throws SQLException {
        String SQL_INSERT = "INSERT INTO ChiTietViTri (MaNganDung, MaSanPham, SoLuong) VALUES (?, ?, ?)";
        ChiTietViTri newChiTiet = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, chiTiet.getMaNganDung());
            pstmt.setString(2, chiTiet.getMaSanPham());
            pstmt.setInt(3, chiTiet.getSoLuong());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newChiTiet = getById(conn, id);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm chi tiết vị trí: " + ex.getMessage());
            throw ex;
        }
        return newChiTiet;
    }

    @Override
    public ChiTietViTri getById(Connection conn, Integer maChiTietViTri) throws SQLException {
        String SQL_SELECT = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaChiTietViTri = ?";
        ChiTietViTri chiTiet = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maChiTietViTri);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    chiTiet = new ChiTietViTri();
                    chiTiet.setMaChiTietViTri(rs.getInt("MaChiTietViTri"));
                    chiTiet.setMaNganDung(rs.getString("MaNganDung"));
                    chiTiet.setMaSanPham(rs.getString("MaSanPham"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết vị trí theo mã: " + ex.getMessage());
            throw ex;
        }
        return chiTiet;
    }

    /**
     * Cập nhật số lượng sản phẩm trong ChiTietViTri.
     * Đây là phương thức quan trọng để quản lý tồn kho.
     * @param conn Connection được cung cấp bởi tầng Service, không đóng ở đây.
     * @param chiTiet Đối tượng ChiTietViTri cần cập nhật (chứa số lượng mới).
     * @return boolean True nếu cập nhật thành công, False nếu thất bại.
     * @throws SQLException Nếu có lỗi SQL.
     */
    @Override
    public boolean update(Connection conn, ChiTietViTri chiTiet) throws SQLException {
        String SQL_UPDATE = "UPDATE ChiTietViTri SET MaNganDung = ?, MaSanPham = ?, SoLuong = ? WHERE MaChiTietViTri = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, chiTiet.getMaNganDung());
            pstmt.setString(2, chiTiet.getMaSanPham());
            pstmt.setInt(3, chiTiet.getSoLuong());
            pstmt.setInt(4, chiTiet.getMaChiTietViTri());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật chi tiết vị trí: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maChiTietViTri) throws SQLException {
        String SQL_DELETE = "DELETE FROM ChiTietViTri WHERE MaChiTietViTri = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maChiTietViTri);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa chi tiết vị trí: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<ChiTietViTri> getAll() { // Đã sửa để nhận Connection
        List<ChiTietViTri> chiTietList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri";
        try (Connection conn = DatabaseConnection.getConnection();
        	Statement stmt = conn.createStatement(); // Sử dụng Connection được truyền vào
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                ChiTietViTri chiTiet = new ChiTietViTri();
                chiTiet.setMaChiTietViTri(rs.getInt("MaChiTietViTri"));
                chiTiet.setMaNganDung(rs.getString("MaNganDung"));
                chiTiet.setMaSanPham(rs.getString("MaSanPham"));
                chiTiet.setSoLuong(rs.getInt("SoLuong"));
                chiTietList.add(chiTiet);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả chi tiết vị trí: " + ex.getMessage());
        }
        return chiTietList;
    }

    /**
     * Lấy chi tiết vị trí dựa trên MaNganDung và MaSanPham.
     * Hữu ích để kiểm tra tồn kho hoặc tìm kiếm một mục cụ thể.
     * @param conn Connection được cung cấp bởi tầng Service, không đóng ở đây.
     * @param maNganDung Mã ngăn đựng.
     * @param maSanPham Mã sản phẩm.
     * @return ChiTietViTri nếu tìm thấy, ngược lại là null.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public ChiTietViTri getByMaNganDungAndMaSanPham(Connection conn, String maNganDung, String maSanPham) throws SQLException {
        String SQL_SELECT = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaNganDung = ? AND MaSanPham = ?";
        ChiTietViTri chiTiet = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, maNganDung);
            pstmt.setString(2, maSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    chiTiet = new ChiTietViTri();
                    chiTiet.setMaChiTietViTri(rs.getInt("MaChiTietViTri"));
                    chiTiet.setMaNganDung(rs.getString("MaNganDung"));
                    chiTiet.setMaSanPham(rs.getString("MaSanPham"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết vị trí theo mã ngăn đựng và mã sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return chiTiet;
    }

    /**
     * Lấy danh sách ChiTietViTri theo MaNganDung.
     * @param conn Connection được cung cấp bởi tầng Service.
     * @param maNganDung Mã ngăn đựng.
     * @return Danh sách ChiTietViTri liên quan đến MaNganDung.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public List<ChiTietViTri> getByMaNganDung(Connection conn, String maNganDung) throws SQLException {
        List<ChiTietViTri> chiTietList = new ArrayList<>();
        String SQL_SELECT_BY_NGAN_DUNG = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaNganDung = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_NGAN_DUNG)) {
            pstmt.setString(1, maNganDung);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietViTri chiTiet = new ChiTietViTri();
                    chiTiet.setMaChiTietViTri(rs.getInt("MaChiTietViTri"));
                    chiTiet.setMaNganDung(rs.getString("MaNganDung"));
                    chiTiet.setMaSanPham(rs.getString("MaSanPham"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                    chiTietList.add(chiTiet);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết vị trí theo mã ngăn đựng: " + ex.getMessage());
            throw ex;
        }
        return chiTietList;
    }

    /**
     * Lấy một ChiTietViTri bất kỳ cho một sản phẩm.
     * Dùng khi bạn chỉ cần biết tổng tồn kho của sản phẩm mà không cần quan tâm vị trí cụ thể.
     * @param conn Connection được cung cấp bởi tầng Service.
     * @param maSanPham Mã sản phẩm.
     * @return Một ChiTietViTri (thường là cái đầu tiên tìm thấy), hoặc null nếu không có.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public ChiTietViTri getAnyByMaSanPham(Connection conn, String maSanPham) throws SQLException {
        String SQL_SELECT = "SELECT MaChiTietViTri, MaNganDung, MaSanPham, SoLuong FROM ChiTietViTri WHERE MaSanPham = ? ORDER BY MaChiTietViTri ASC";
        ChiTietViTri chiTiet = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, maSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    chiTiet = new ChiTietViTri();
                    chiTiet.setMaChiTietViTri(rs.getInt("MaChiTietViTri"));
                    chiTiet.setMaNganDung(rs.getString("MaNganDung"));
                    chiTiet.setMaSanPham(rs.getString("MaSanPham"));
                    chiTiet.setSoLuong(rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy bất kỳ chi tiết vị trí nào theo mã sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return chiTiet;
    }
}
