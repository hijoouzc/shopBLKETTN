package DAO;

import models.NhanVien;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO implements GenericDAO<NhanVien, String> {

    private static NhanVienDAO instance; // Singleton instance

    // Private constructor to enforce Singleton pattern
    private NhanVienDAO() {
    }

    // Static method to get the Singleton instance
    public static NhanVienDAO getIns() {
        if (instance == null) {
            instance = new NhanVienDAO();
        }
        return instance;
    }

    @Override
    public NhanVien add(Connection conn, NhanVien nhanVien) throws SQLException {
        String SQL_INSERT = "INSERT INTO NhanVien (MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, SDT, Luong, TrangThaiLamViec) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        NhanVien newNhanVien = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, nhanVien.getMaNguoiDung());
            pstmt.setString(2, nhanVien.getHoTen());
            pstmt.setDate(3, new java.sql.Date(nhanVien.getNgaySinh().getTime()));
            pstmt.setString(4, nhanVien.getGioiTinh());
            pstmt.setString(5, nhanVien.getCccd());
            pstmt.setString(6, nhanVien.getSdt());
            pstmt.setInt(7, nhanVien.getLuong());
            pstmt.setString(8, nhanVien.getTrangThaiLamViec());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        String maNhanVien = generatedKeys.getString(1); // Giả sử MaNhanVien là cột đầu tiên được tạo
                        newNhanVien = getById(conn, maNhanVien);
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm nhân viên: " + ex.getMessage());
            throw ex;
        }
        return newNhanVien;
    }

    @Override
    public NhanVien getById(Connection conn, String id) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, SDT, Luong, TrangThaiLamViec FROM NhanVien WHERE MaNhanVien = ?";
        NhanVien nhanVien = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nhanVien = new NhanVien();
                    nhanVien.setInternalID(rs.getInt("InternalID"));
                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    nhanVien.setHoTen(rs.getString("HoTen"));
                    nhanVien.setNgaySinh(rs.getDate("NgaySinh"));
                    nhanVien.setGioiTinh(rs.getString("GioiTinh"));
                    nhanVien.setCccd(rs.getString("CCCD"));
                    nhanVien.setSdt(rs.getString("SDT"));
                    nhanVien.setLuong(rs.getInt("Luong"));
                    nhanVien.setTrangThaiLamViec(rs.getString("TrangThaiLamViec"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy nhân viên theo ID: " + ex.getMessage());
            throw ex;
        }
        return nhanVien;
    }

    /**
     * Lấy thông tin nhân viên dựa trên mã người dùng (MaNguoiDung).
     * @param conn Connection đến cơ sở dữ liệu.
     * @param maNguoiDung Mã người dùng.
     * @return Đối tượng NhanVien nếu tìm thấy, null nếu không.
     * @throws SQLException Nếu có lỗi SQL.
     */
    public NhanVien getNhanVienByMaNguoiDung(Connection conn, String maNguoiDung) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, SDT, Luong, TrangThaiLamViec FROM NhanVien WHERE MaNguoiDung = ?";
        NhanVien nhanVien = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, maNguoiDung);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nhanVien = new NhanVien();
                    nhanVien.setInternalID(rs.getInt("InternalID"));
                    nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                    nhanVien.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    nhanVien.setHoTen(rs.getString("HoTen"));
                    nhanVien.setNgaySinh(rs.getDate("NgaySinh"));
                    nhanVien.setGioiTinh(rs.getString("GioiTinh"));
                    nhanVien.setCccd(rs.getString("CCCD"));
                    nhanVien.setSdt(rs.getString("SDT"));
                    nhanVien.setLuong(rs.getInt("Luong"));
                    nhanVien.setTrangThaiLamViec(rs.getString("TrangThaiLamViec"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy nhân viên theo MaNguoiDung: " + ex.getMessage());
            throw ex;
        }
        return nhanVien;
    }

    @Override
    public boolean update(Connection conn, NhanVien nhanVien) throws SQLException {
        String SQL_UPDATE = "UPDATE NhanVien SET MaNguoiDung = ?, HoTen = ?, NgaySinh = ?, GioiTinh = ?, CCCD = ?, SDT = ?, Luong = ?, TrangThaiLamViec = ? WHERE MaNhanVien = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {
            pstmt.setString(1, nhanVien.getMaNguoiDung());
            pstmt.setString(2, nhanVien.getHoTen());
            pstmt.setDate(3, new java.sql.Date(nhanVien.getNgaySinh().getTime()));
            pstmt.setString(4, nhanVien.getGioiTinh());
            pstmt.setString(5, nhanVien.getCccd());
            pstmt.setString(6, nhanVien.getSdt());
            pstmt.setInt(7, nhanVien.getLuong());
            pstmt.setString(8, nhanVien.getTrangThaiLamViec());
            pstmt.setString(9, nhanVien.getMaNhanVien()); // Điều kiện WHERE

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật nhân viên: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, String id) throws SQLException {
        String SQL_DELETE = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {
            pstmt.setString(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa nhân viên: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<NhanVien> getAll() {
        List<NhanVien> nhanVienList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaNhanVien, MaNguoiDung, HoTen, NgaySinh, GioiTinh, CCCD, SDT, Luong, TrangThaiLamViec FROM NhanVien";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setInternalID(rs.getInt("InternalID"));
                nhanVien.setMaNhanVien(rs.getString("MaNhanVien"));
                nhanVien.setMaNguoiDung(rs.getString("MaNguoiDung"));
                nhanVien.setHoTen(rs.getString("HoTen"));
                nhanVien.setNgaySinh(rs.getDate("NgaySinh"));
                nhanVien.setGioiTinh(rs.getString("GioiTinh"));
                nhanVien.setCccd(rs.getString("CCCD"));
                nhanVien.setSdt(rs.getString("SDT"));
                nhanVien.setLuong(rs.getInt("Luong"));
                nhanVien.setTrangThaiLamViec(rs.getString("TrangThaiLamViec"));
                nhanVienList.add(nhanVien);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả nhân viên: " + ex.getMessage());
        }
        return nhanVienList;
    }
}
