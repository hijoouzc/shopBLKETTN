package DAO; // Package của bạn, có thể là com.yourcompany.projectname.dao

import models.PhieuNhapHang; // Import lớp model PhieuNhapHang
import utils.DatabaseConnection; // Import lớp tiện ích kết nối CSDL

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapHangDAO implements GenericDAO<PhieuNhapHang, Integer> {
	
    private static PhieuNhapHangDAO instance;
    public static PhieuNhapHangDAO getIns() {
        if (instance == null) {
            instance = new PhieuNhapHangDAO();
        }
        return instance;
    }

    @Override
    public PhieuNhapHang add(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        String SQL_INSERT = "INSERT INTO PhieuNhapHang (NgayNhap, MaNhanVienThucHien) VALUES (?, ?)";
        PhieuNhapHang newPhieuNhapHang = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setTimestamp(1, new Timestamp(phieuNhapHang.getNgayNhap().getTime()));
            pstmt.setString(2, phieuNhapHang.getMaNhanVienThucHien());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newPhieuNhapHang = getById(conn, id);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm phiếu nhập hàng: " + ex.getMessage());
            throw ex;
        }
        return newPhieuNhapHang;
    }

    @Override
    public PhieuNhapHang getById(Connection conn, Integer maPhieuNhap) throws SQLException {
        String SQL_SELECT = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien FROM PhieuNhapHang WHERE MaPhieuNhap = ?";
        PhieuNhapHang phieuNhapHang = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maPhieuNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    phieuNhapHang = new PhieuNhapHang();
                    phieuNhapHang.setMaPhieuNhap(rs.getInt("MaPhieuNhap"));
                    phieuNhapHang.setNgayNhap(new Date(rs.getTimestamp("NgayNhap").getTime()));
                    phieuNhapHang.setMaNhanVienThucHien(rs.getString("MaNhanVienThucHien"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy phiếu nhập hàng theo mã: " + ex.getMessage());
            throw ex;
        }
        return phieuNhapHang;
    }

    @Override
    public boolean update(Connection conn, PhieuNhapHang phieuNhapHang) throws SQLException {
        String SQL_UPDATE = "UPDATE PhieuNhapHang SET NgayNhap = ?, MaNhanVienThucHien = ? WHERE MaPhieuNhap = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            pstmt.setTimestamp(1, new Timestamp(phieuNhapHang.getNgayNhap().getTime()));
            pstmt.setString(2, phieuNhapHang.getMaNhanVienThucHien());
            pstmt.setInt(3, phieuNhapHang.getMaPhieuNhap());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật phiếu nhập hàng: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maPhieuNhap) throws SQLException {
        String SQL_DELETE = "DELETE FROM PhieuNhapHang WHERE MaPhieuNhap = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maPhieuNhap);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa phiếu nhập hàng: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<PhieuNhapHang> getAll() {
        List<PhieuNhapHang> phieuNhapHangList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaPhieuNhap, NgayNhap, MaNhanVienThucHien FROM PhieuNhapHang";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                PhieuNhapHang phieuNhapHang = new PhieuNhapHang();
                phieuNhapHang.setMaPhieuNhap(rs.getInt("MaPhieuNhap"));
                phieuNhapHang.setNgayNhap(new Date(rs.getTimestamp("NgayNhap").getTime()));
                phieuNhapHang.setMaNhanVienThucHien(rs.getString("MaNhanVienThucHien"));
                phieuNhapHangList.add(phieuNhapHang);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả phiếu nhập hàng: " + ex.getMessage());
            ex.printStackTrace();
        }
        return phieuNhapHangList;
    }


}