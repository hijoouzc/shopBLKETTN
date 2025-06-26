package DAO; 

import models.ChiTietPhieuNhap;
import models.ChiTietViTri;
import utils.DatabaseConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietPhieuNhapDAO implements GenericDAO<ChiTietPhieuNhap, Integer> { 

    private static ChiTietPhieuNhapDAO instance;

    // Private constructor for Singleton pattern
    public ChiTietPhieuNhapDAO() {
    }

    // Static method to get the Singleton instance
    public static ChiTietPhieuNhapDAO getIns() {
        if (instance == null) {
            instance = new ChiTietPhieuNhapDAO();
        }
        return instance;
    }

    @Override
    public ChiTietPhieuNhap add(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        String SQL_INSERT = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap) VALUES (?, ?, ?, ?)";
        ChiTietPhieuNhap newChiTietPhieuNhap = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, chiTietPhieuNhap.getMaPhieuNhap());
            pstmt.setString(2, chiTietPhieuNhap.getMaSanPham());
            pstmt.setInt(3, chiTietPhieuNhap.getSoLuong());
            pstmt.setInt(4, chiTietPhieuNhap.getDonGiaNhap());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newChiTietPhieuNhap = getById(conn, id); // Fetch complete ChiTietPhieuNhap
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm chi tiết phiếu nhập: " + ex.getMessage());
            throw ex; // Re-throw exception
        }
        return newChiTietPhieuNhap;
    }

    @Override
    public ChiTietPhieuNhap getById(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        String SQL_SELECT = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap WHERE MaChiTietPhieuNhap = ?";
        ChiTietViTri chiTiet = null; // Changed to ChiTietViTri initially, corrected to ChiTietPhieuNhap
        ChiTietPhieuNhap chiTietPhieuNhap = null; // Corrected variable name

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maChiTietPhieuNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    chiTietPhieuNhap = new ChiTietPhieuNhap();
                    chiTietPhieuNhap.setMaChiTietPhieuNhap(rs.getInt("MaChiTietPhieuNhap"));
                    chiTietPhieuNhap.setMaPhieuNhap(rs.getInt("MaPhieuNhap"));
                    chiTietPhieuNhap.setMaSanPham(rs.getString("MaSanPham"));
                    chiTietPhieuNhap.setSoLuong(rs.getInt("SoLuong"));
                    chiTietPhieuNhap.setDonGiaNhap(rs.getInt("DonGiaNhap"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết phiếu nhập theo mã: " + ex.getMessage());
            throw ex; // Re-throw exception
        }
        return chiTietPhieuNhap;
    }

    @Override
    public boolean update(Connection conn, ChiTietPhieuNhap chiTietPhieuNhap) throws SQLException {
        String SQL_UPDATE = "UPDATE ChiTietPhieuNhap SET MaPhieuNhap = ?, MaSanPham = ?, SoLuong = ?, DonGiaNhap = ? WHERE MaChiTietPhieuNhap = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setInt(1, chiTietPhieuNhap.getMaPhieuNhap());
            pstmt.setString(2, chiTietPhieuNhap.getMaSanPham());
            pstmt.setInt(3, chiTietPhieuNhap.getSoLuong());
            pstmt.setInt(4, chiTietPhieuNhap.getDonGiaNhap());
            pstmt.setInt(5, chiTietPhieuNhap.getMaChiTietPhieuNhap());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật chi tiết phiếu nhập: " + ex.getMessage());
            throw ex; // Re-throw exception
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maChiTietPhieuNhap) throws SQLException {
        String SQL_DELETE = "DELETE FROM ChiTietPhieuNhap WHERE MaChiTietPhieuNhap = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maChiTietPhieuNhap);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa chi tiết phiếu nhập: " + ex.getMessage());
            throw ex; // Re-throw exception
        }
        return success;
    }

    @Override
    public List<ChiTietPhieuNhap> getAll(){ // Đã sửa để nhận Connection
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap";
        try (Connection conn = DatabaseConnection.getConnection(); 
        	Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
                chiTietPhieuNhap.setMaChiTietPhieuNhap(rs.getInt("MaChiTietPhieuNhap"));
                chiTietPhieuNhap.setMaPhieuNhap(rs.getInt("MaPhieuNhap"));
                chiTietPhieuNhap.setMaSanPham(rs.getString("MaSanPham"));
                chiTietPhieuNhap.setSoLuong(rs.getInt("SoLuong"));
                chiTietPhieuNhap.setDonGiaNhap(rs.getInt("DonGiaNhap"));
                chiTietPhieuNhapList.add(chiTietPhieuNhap);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả chi tiết phiếu nhập: " + ex.getMessage());
        }
        return chiTietPhieuNhapList;
    }

    /**
     * Lấy danh sách ChiTietPhieuNhap theo MaPhieuNhap.
     * @param conn Kết nối CSDL.
     * @param maPhieuNhap Mã phiếu nhập.
     * @return Danh sách ChiTietPhieuNhap của phiếu nhập.
     * @throws SQLException
     */
    public List<ChiTietPhieuNhap> getByMaPhieuNhap(Connection conn, int maPhieuNhap) throws SQLException {
        List<ChiTietPhieuNhap> chiTietPhieuNhapList = new ArrayList<>();
        String SQL_SELECT_BY_MA_PN = "SELECT MaChiTietPhieuNhap, MaPhieuNhap, MaSanPham, SoLuong, DonGiaNhap FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT_BY_MA_PN)) {
            pstmt.setInt(1, maPhieuNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ChiTietPhieuNhap chiTietPhieuNhap = new ChiTietPhieuNhap();
                    chiTietPhieuNhap.setMaChiTietPhieuNhap(rs.getInt("MaChiTietPhieuNhap"));
                    chiTietPhieuNhap.setMaPhieuNhap(rs.getInt("MaPhieuNhap"));
                    chiTietPhieuNhap.setMaSanPham(rs.getString("MaSanPham"));
                    chiTietPhieuNhap.setSoLuong(rs.getInt("SoLuong"));
                    chiTietPhieuNhap.setDonGiaNhap(rs.getInt("DonGiaNhap"));
                    chiTietPhieuNhapList.add(chiTietPhieuNhap);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy chi tiết phiếu nhập theo mã phiếu nhập: " + ex.getMessage());
            throw ex;
        }
        return chiTietPhieuNhapList;
    }
}
