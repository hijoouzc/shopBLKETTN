package DAO;

import models.KhachHang;
import utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 

public class KhachHangDAO implements GenericDAO<KhachHang, String> {
	
	private static KhachHangDAO instance;
	public static KhachHangDAO getIns() {
        if (instance == null) {
            instance = new KhachHangDAO();
        }
        return instance;
	}

    @Override
    public KhachHang add(Connection conn, KhachHang khachHang) throws SQLException {
        String SQL_INSERT = "INSERT INTO KhachHang (MaNguoiDung, HoTen, NgaySinh, GioiTinh, SDT) VALUES (?, ?, ?, ?, ?)";
        KhachHang newKhachHang = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            if (khachHang.getMaNguoiDung() != null) {
                pstmt.setString(1, khachHang.getMaNguoiDung());
            } else {
                pstmt.setNull(1, Types.VARCHAR); // Set NULL if MaNguoiDung is not provided
            }
            pstmt.setString(2, khachHang.getHoTen());
            if (khachHang.getNgaySinh() != null) {
                pstmt.setDate(3, new java.sql.Date(khachHang.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            if (khachHang.getGioiTinh() != null) {
                pstmt.setString(4, khachHang.getGioiTinh());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }
            pstmt.setString(5, khachHang.getSdt());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Do MaKhachHang là computed column dựa trên InternalID,
                        // cách tốt nhất là truy vấn lại khách hàng dựa trên SDT (là UNIQUE)
                        newKhachHang = getBySdt(conn, khachHang.getSdt());
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm khách hàng: " + ex.getMessage());
            throw ex;
        }
        return newKhachHang;
    }

    @Override
    public KhachHang getById(Connection conn, String maKhachHang) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, SDT FROM KhachHang WHERE MaKhachHang = ?";
        KhachHang khachHang = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, maKhachHang);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    khachHang = new KhachHang();
                    khachHang.setInternalID(rs.getInt("InternalID"));
                    khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
                    khachHang.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setNgaySinh(rs.getDate("NgaySinh"));
                    khachHang.setGioiTinh(rs.getString("GioiTinh"));
                    khachHang.setSdt(rs.getString("SDT"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy khách hàng theo mã: " + ex.getMessage());
            throw ex;
        }
        return khachHang;
    }

    public KhachHang getBySdt(Connection conn, String sdt) throws SQLException {
        String SQL_SELECT = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, SDT FROM KhachHang WHERE SDT = ?";
        KhachHang khachHang = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setString(1, sdt);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    khachHang = new KhachHang();
                    khachHang.setInternalID(rs.getInt("InternalID"));
                    khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
                    khachHang.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    khachHang.setHoTen(rs.getString("HoTen"));
                    khachHang.setNgaySinh(rs.getDate("NgaySinh"));
                    khachHang.setGioiTinh(rs.getString("GioiTinh"));
                    khachHang.setSdt(rs.getString("SDT"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy khách hàng theo SDT: " + ex.getMessage());
            throw ex;
        }
        return khachHang;
    }

    @Override
    public boolean update(Connection conn, KhachHang khachHang) throws SQLException {
        String SQL_UPDATE = "UPDATE KhachHang SET MaNguoiDung = ?, HoTen = ?, NgaySinh = ?, GioiTinh = ?, SDT = ? WHERE MaKhachHang = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            if (khachHang.getMaNguoiDung() != null) {
                pstmt.setString(1, khachHang.getMaNguoiDung());
            } else {
                pstmt.setNull(1, Types.VARCHAR);
            }
            pstmt.setString(2, khachHang.getHoTen());
            if (khachHang.getNgaySinh() != null) {
                pstmt.setDate(3, new java.sql.Date(khachHang.getNgaySinh().getTime()));
            } else {
                pstmt.setNull(3, Types.DATE);
            }
            if (khachHang.getGioiTinh() != null) {
                pstmt.setString(4, khachHang.getGioiTinh());
            } else {
                pstmt.setNull(4, Types.NVARCHAR);
            }
            pstmt.setString(5, khachHang.getSdt());
            pstmt.setString(6, khachHang.getMaKhachHang());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật khách hàng: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, String maKhachHang) throws SQLException {
        String SQL_DELETE = "DELETE FROM KhachHang WHERE MaKhachHang = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setString(1, maKhachHang);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa khách hàng: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<KhachHang> getAll() {
        List<KhachHang> khachHangList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT InternalID, MaKhachHang, MaNguoiDung, HoTen, NgaySinh, GioiTinh, SDT FROM KhachHang";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                KhachHang khachHang = new KhachHang();
                khachHang.setInternalID(rs.getInt("InternalID"));
                khachHang.setMaKhachHang(rs.getString("MaKhachHang"));
                khachHang.setMaNguoiDung(rs.getString("MaNguoiDung"));
                khachHang.setHoTen(rs.getString("HoTen"));
                khachHang.setNgaySinh(rs.getDate("NgaySinh"));
                khachHang.setGioiTinh(rs.getString("GioiTinh"));
                khachHang.setSdt(rs.getString("SDT"));
                khachHangList.add(khachHang);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả khách hàng: " + ex.getMessage());
            ex.printStackTrace();
        }
        return khachHangList;
    }
    
    // --- Phương thức tìm kiếm mới ---
    /**
     * Tìm kiếm khách hàng theo tên hoặc số điện thoại (có thể là một phần).
     * @param keyword Từ khóa tìm kiếm (tên hoặc SĐT).
     * @return Danh sách các KhachHang khớp với từ khóa.
     */
    public List<KhachHang> search(String keyword) {
        List<KhachHang> khachHangList = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE HoTen LIKE ? OR Sdt LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setMaKhachHang(rs.getString("MaKhachHang"));
                    kh.setHoTen(rs.getString("HoTen"));
                    kh.setNgaySinh(rs.getDate("NgaySinh"));
                    kh.setGioiTinh(rs.getString("GioiTinh"));
                    kh.setSdt(rs.getString("Sdt"));
                    kh.setMaNguoiDung(rs.getString("MaNguoiDung"));
                    khachHangList.add(kh);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi tìm kiếm khách hàng: " + e.getMessage());
            e.printStackTrace();
        }
        return khachHangList;
    }
}