package DAO;

import models.ViTriDungSanPham;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViTriDungSanPhamDAO implements GenericDAO<ViTriDungSanPham, String> {
	
    private static ViTriDungSanPhamDAO instance;
    public static ViTriDungSanPhamDAO getIns() {
        if (instance == null) {
            instance = new ViTriDungSanPhamDAO();
        }
        return instance;
    }

    @Override
    public ViTriDungSanPham add(Connection conn, ViTriDungSanPham viTri) throws SQLException {
        String SQL_INSERT = "INSERT INTO ViTriDungSanPham (TenNganDung) VALUES (?)";
        ViTriDungSanPham newViTri = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, viTri.getTenNganDung());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newViTri = getViTriDungSanPhamByTen(conn, viTri.getTenNganDung());
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm vị trí đựng sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return newViTri;
    }

    @Override
    public ViTriDungSanPham getById(Connection conn, String maNganDung) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham WHERE MaNganDung = ?";
        ViTriDungSanPham viTri = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, maNganDung);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    viTri = new ViTriDungSanPham();
                    viTri.setInternalID(rs.getInt("InternalID"));
                    viTri.setMaNganDung(rs.getString("MaNganDung"));
                    viTri.setTenNganDung(rs.getString("TenNganDung"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy vị trí đựng sản phẩm theo mã: " + ex.getMessage());
            throw ex;
        }
        return viTri;
    }

    public ViTriDungSanPham getViTriDungSanPhamByTen(Connection conn, String tenNganDung) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham WHERE TenNganDung = ?";
        ViTriDungSanPham viTri = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, tenNganDung);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    viTri = new ViTriDungSanPham();
                    viTri.setInternalID(rs.getInt("InternalID"));
                    viTri.setMaNganDung(rs.getString("MaNganDung"));
                    viTri.setTenNganDung(rs.getString("TenNganDung"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy vị trí đựng sản phẩm theo tên: " + ex.getMessage());
            throw ex;
        }
        return viTri;
    }

    @Override
    public boolean update(Connection conn, ViTriDungSanPham viTri) throws SQLException {
        String SQL_UPDATE = "UPDATE ViTriDungSanPham SET TenNganDung = ? WHERE MaNganDung = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setString(1, viTri.getTenNganDung());
            pstmt.setString(2, viTri.getMaNganDung());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật vị trí đựng sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, String maNganDung) throws SQLException {
        String SQL_DELETE = "DELETE FROM ViTriDungSanPham WHERE MaNganDung = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setString(1, maNganDung);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa vị trí đựng sản phẩm: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<ViTriDungSanPham> getAll() {
        List<ViTriDungSanPham> viTriList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaNganDung, TenNganDung FROM ViTriDungSanPham";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                ViTriDungSanPham viTri = new ViTriDungSanPham();
                viTri.setInternalID(rs.getInt("InternalID"));
                viTri.setMaNganDung(rs.getString("MaNganDung"));
                viTri.setTenNganDung(rs.getString("TenNganDung"));
                viTriList.add(viTri);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả vị trí đựng sản phẩm: " + ex.getMessage());
            ex.printStackTrace();
        }
        return viTriList;
    }

}