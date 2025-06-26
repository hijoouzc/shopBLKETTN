package DAO;

import models.PhucHoi; 
import utils.DatabaseConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class PhucHoiDAO implements GenericDAO<PhucHoi, Integer> {

    @Override
    public PhucHoi add(Connection conn, PhucHoi phucHoi) throws SQLException {
        String SQL_INSERT = "INSERT INTO PhucHoi (NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu) VALUES (?, ?, ?, ?)";
        PhucHoi newPhucHoi = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, new Timestamp(phucHoi.getNgayPhucHoi().getTime()));
            pstmt.setString(2, phucHoi.getLoaiPhucHoi());
            pstmt.setString(3, phucHoi.getMaNguoiDungThucHien());
            pstmt.setInt(4, phucHoi.getMaSaoLuu());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newPhucHoi = getById(conn, id);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm phục hồi: " + ex.getMessage());
            throw ex;
        }
        return newPhucHoi;
    }

    @Override
    public PhucHoi getById(Connection conn, Integer maPhucHoi) throws SQLException {
        String SQL_SELECT = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi WHERE MaPhucHoi = ?";
        PhucHoi phucHoi = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maPhucHoi);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    phucHoi = new PhucHoi();
                    phucHoi.setMaPhucHoi(rs.getInt("MaPhucHoi"));
                    phucHoi.setNgayPhucHoi(new Date(rs.getTimestamp("NgayPhucHoi").getTime()));
                    phucHoi.setLoaiPhucHoi(rs.getString("LoaiPhucHoi"));
                    phucHoi.setMaNguoiDungThucHien(rs.getString("MaNguoiDungThucHien"));
                    phucHoi.setMaSaoLuu(rs.getInt("MaSaoLuu"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy phục hồi theo mã: " + ex.getMessage());
            throw ex;
        }
        return phucHoi;
    }

    @Override
    public boolean update(Connection conn, PhucHoi phucHoi) throws SQLException {
        String SQL_UPDATE = "UPDATE PhucHoi SET NgayPhucHoi = ?, LoaiPhucHoi = ?, MaNguoiDungThucHien = ?, MaSaoLuu = ? WHERE MaPhucHoi = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setTimestamp(1, new Timestamp(phucHoi.getNgayPhucHoi().getTime()));
            pstmt.setString(2, phucHoi.getLoaiPhucHoi());
            pstmt.setString(3, phucHoi.getMaNguoiDungThucHien());
            pstmt.setInt(4, phucHoi.getMaSaoLuu());
            pstmt.setInt(5, phucHoi.getMaPhucHoi());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật phục hồi: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maPhucHoi) throws SQLException {
        String SQL_DELETE = "DELETE FROM PhucHoi WHERE MaPhucHoi = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maPhucHoi);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa phục hồi: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<PhucHoi> getAll() {
        List<PhucHoi> phucHoiList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaPhucHoi, NgayPhucHoi, LoaiPhucHoi, MaNguoiDungThucHien, MaSaoLuu FROM PhucHoi";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                PhucHoi phucHoi = new PhucHoi();
                phucHoi.setMaPhucHoi(rs.getInt("MaPhucHoi"));
                phucHoi.setNgayPhucHoi(new Date(rs.getTimestamp("NgayPhucHoi").getTime()));
                phucHoi.setLoaiPhucHoi(rs.getString("LoaiPhucHoi"));
                phucHoi.setMaNguoiDungThucHien(rs.getString("MaNguoiDungThucHien"));
                phucHoi.setMaSaoLuu(rs.getInt("MaSaoLuu"));
                phucHoiList.add(phucHoi);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả phục hồi: " + ex.getMessage());
            ex.printStackTrace();
        }
        return phucHoiList;
    }
}