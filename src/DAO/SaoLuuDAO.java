package DAO;

import models.SaoLuu; 
import utils.DatabaseConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class SaoLuuDAO implements GenericDAO<SaoLuu, Integer> {

    @Override
    public SaoLuu add(Connection conn, SaoLuu saoLuu) throws SQLException {
        String SQL_INSERT = "INSERT INTO SaoLuu (NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru) VALUES (?, ?, ?, ?)";
        SaoLuu newSaoLuu = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, new Timestamp(saoLuu.getNgaySaoLuu().getTime()));
            pstmt.setString(2, saoLuu.getLoaiSaoLuu());
            pstmt.setString(3, saoLuu.getMaNguoiDungThucHien());
            pstmt.setString(4, saoLuu.getViTriLuuTru());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newSaoLuu = getById(conn, id);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm sao lưu: " + ex.getMessage());
            throw ex;
        }
        return newSaoLuu;
    }

    @Override
    public SaoLuu getById(Connection conn, Integer maSaoLuu) throws SQLException {
        String SQL_SELECT = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu WHERE MaSaoLuu = ?";
        SaoLuu saoLuu = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maSaoLuu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    saoLuu = new SaoLuu();
                    saoLuu.setMaSaoLuu(rs.getInt("MaSaoLuu"));
                    saoLuu.setNgaySaoLuu(new Date(rs.getTimestamp("NgaySaoLuu").getTime()));
                    saoLuu.setLoaiSaoLuu(rs.getString("LoaiSaoLuu"));
                    saoLuu.setMaNguoiDungThucHien(rs.getString("MaNguoiDungThucHien"));
                    saoLuu.setViTriLuuTru(rs.getString("ViTriLuuTru"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy sao lưu theo mã: " + ex.getMessage());
            throw ex;
        }
        return saoLuu;
    }

    @Override
    public boolean update(Connection conn, SaoLuu saoLuu) throws SQLException {
        String SQL_UPDATE = "UPDATE SaoLuu SET NgaySaoLuu = ?, LoaiSaoLuu = ?, MaNguoiDungThucHien = ?, ViTriLuuTru = ? WHERE MaSaoLuu = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setTimestamp(1, new Timestamp(saoLuu.getNgaySaoLuu().getTime()));
            pstmt.setString(2, saoLuu.getLoaiSaoLuu());
            pstmt.setString(3, saoLuu.getMaNguoiDungThucHien());
            pstmt.setString(4, saoLuu.getViTriLuuTru());
            pstmt.setInt(5, saoLuu.getMaSaoLuu());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật sao lưu: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maSaoLuu) throws SQLException {
        String SQL_DELETE = "DELETE FROM SaoLuu WHERE MaSaoLuu = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maSaoLuu);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa sao lưu: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<SaoLuu> getAll() {
        List<SaoLuu> saoLuuList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaSaoLuu, NgaySaoLuu, LoaiSaoLuu, MaNguoiDungThucHien, ViTriLuuTru FROM SaoLuu";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                SaoLuu saoLuu = new SaoLuu();
                saoLuu.setMaSaoLuu(rs.getInt("MaSaoLuu"));
                saoLuu.setNgaySaoLuu(new Date(rs.getTimestamp("NgaySaoLuu").getTime()));
                saoLuu.setLoaiSaoLuu(rs.getString("LoaiSaoLuu"));
                saoLuu.setMaNguoiDungThucHien(rs.getString("MaNguoiDungThucHien"));
                saoLuu.setViTriLuuTru(rs.getString("ViTriLuuTru"));
                saoLuuList.add(saoLuu);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả sao lưu: " + ex.getMessage());
            ex.printStackTrace();
        }
        return saoLuuList;
    }
}