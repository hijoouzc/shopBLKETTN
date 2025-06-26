package DAO;

import models.LoaiSanPham;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO implements GenericDAO<LoaiSanPham, String> {

    @Override
    public LoaiSanPham add(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        String SQL_INSERT = "INSERT INTO LoaiSanPham (TenLoaiSanPham, MoTa) VALUES (?, ?)";
        LoaiSanPham newLoaiSanPham = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, loaiSanPham.getTenLoaiSanPham());
            if (loaiSanPham.getMoTa() != null) {
                pstmt.setString(2, loaiSanPham.getMoTa());
            } else {
                pstmt.setNull(2, Types.NVARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newLoaiSanPham = getLoaiSanPhamByTen(conn, loaiSanPham.getTenLoaiSanPham());
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm loại sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return newLoaiSanPham;
    }

    @Override
    public LoaiSanPham getById(Connection conn, String maLoaiSanPham) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
        LoaiSanPham loaiSanPham = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, maLoaiSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loaiSanPham = new LoaiSanPham();
                    loaiSanPham.setInternalID(rs.getInt("InternalID"));
                    loaiSanPham.setMaLoaiSanPham(rs.getString("MaLoaiSanPham"));
                    loaiSanPham.setTenLoaiSanPham(rs.getString("TenLoaiSanPham"));
                    loaiSanPham.setMoTa(rs.getString("MoTa"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy loại sản phẩm theo mã: " + ex.getMessage());
            throw ex;
        }
        return loaiSanPham;
    }

    public LoaiSanPham getLoaiSanPhamByTen(Connection conn, String tenLoaiSanPham) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE TenLoaiSanPham = ?";
        LoaiSanPham loaiSanPham = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, tenLoaiSanPham);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    loaiSanPham = new LoaiSanPham();
                    loaiSanPham.setInternalID(rs.getInt("InternalID"));
                    loaiSanPham.setMaLoaiSanPham(rs.getString("MaLoaiSanPham"));
                    loaiSanPham.setTenLoaiSanPham(rs.getString("TenLoaiSanPham"));
                    loaiSanPham.setMoTa(rs.getString("MoTa"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy loại sản phẩm theo tên: " + ex.getMessage());
            throw ex;
        }
        return loaiSanPham;
    }

    @Override
    public boolean update(Connection conn, LoaiSanPham loaiSanPham) throws SQLException {
        String SQL_UPDATE = "UPDATE LoaiSanPham SET TenLoaiSanPham = ?, MoTa = ? WHERE MaLoaiSanPham = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, loaiSanPham.getTenLoaiSanPham());
            if (loaiSanPham.getMoTa() != null) {
                pstmt.setString(2, loaiSanPham.getMoTa());
            } else {
                pstmt.setNull(2, Types.NVARCHAR);
            }
            pstmt.setString(3, loaiSanPham.getMaLoaiSanPham());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật loại sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, String maLoaiSanPham) throws SQLException {
        String SQL_DELETE = "DELETE FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setString(1, maLoaiSanPham);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa loại sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<LoaiSanPham> getAll() {
        List<LoaiSanPham> loaiSanPhamList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                LoaiSanPham loaiSanPham = new LoaiSanPham();
                loaiSanPham.setInternalID(rs.getInt("InternalID"));
                loaiSanPham.setMaLoaiSanPham(rs.getString("MaLoaiSanPham"));
                loaiSanPham.setTenLoaiSanPham(rs.getString("TenLoaiSanPham"));
                loaiSanPham.setMoTa(rs.getString("MoTa"));
                loaiSanPhamList.add(loaiSanPham);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả loại sản phẩm: " + ex.getMessage());
            ex.printStackTrace();
        }
        return loaiSanPhamList;
    }
}