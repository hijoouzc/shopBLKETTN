package DAO; // Updated package

import models.HoaDon; // Updated import
import models.PhieuNhapHang;
import utils.DatabaseConnection; // Updated import

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO implements GenericDAO<HoaDon, Integer> {
	
	private static HoaDonDAO instance;
	public static HoaDonDAO getIns() {
        if (instance == null) {
            instance = new HoaDonDAO();
        }
        return instance;
	}

    @Override
    public HoaDon add(Connection conn, HoaDon hoaDon) throws SQLException {
        String SQL_INSERT = "INSERT INTO HoaDon (MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?)";
        HoaDon newHoaDon = null;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            if (hoaDon.getMaKhachHang() != null) {
                pstmt.setString(1, hoaDon.getMaKhachHang());
                pstmt.setNull(2, Types.NVARCHAR); // Ensure other fields are null if MaKhachHang is present
                pstmt.setNull(3, Types.NVARCHAR);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, hoaDon.getTenKhachHangVangLai());
                pstmt.setString(3, hoaDon.getSdtKhachHangVangLai());
            }
            pstmt.setString(4, hoaDon.getMaNhanVienLap());
            pstmt.setTimestamp(5, new Timestamp(hoaDon.getNgayBan().getTime()));
            pstmt.setString(6, hoaDon.getPhuongThucThanhToan());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        newHoaDon = getById(conn, id); // Lấy lại hóa đơn với ID được tạo
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi thêm hóa đơn: " + ex.getMessage());
            throw ex;
        }
        return newHoaDon;
    }

    @Override
    public HoaDon getById(Connection conn, Integer maHoaDon) throws SQLException {
        String SQL_SELECT = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE MaHoaDon = ?";
        HoaDon hoaDon = null;

        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {

            pstmt.setInt(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    hoaDon = new HoaDon();
                    hoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                    hoaDon.setMaKhachHang(rs.getString("MaKhachHang"));
                    hoaDon.setTenKhachHangVangLai(rs.getString("TenKhachHangVangLai"));
                    hoaDon.setSdtKhachHangVangLai(rs.getString("SDTKhachHangVangLai"));
                    hoaDon.setMaNhanVienLap(rs.getString("MaNhanVienLap"));
                    hoaDon.setNgayBan(new Date(rs.getTimestamp("NgayBan").getTime()));
                    hoaDon.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy hóa đơn theo mã: " + ex.getMessage());
            throw ex;
        }
        return hoaDon;
    }

    @Override
    public boolean update(Connection conn, HoaDon hoaDon) throws SQLException {
        String SQL_UPDATE = "UPDATE HoaDon SET MaKhachHang = ?, TenKhachHangVangLai = ?, SDTKhachHangVangLai = ?, MaNhanVienLap = ?, NgayBan = ?, PhuongThucThanhToan = ? WHERE MaHoaDon = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_UPDATE)) {

            if (hoaDon.getMaKhachHang() != null) {
                pstmt.setString(1, hoaDon.getMaKhachHang());
                pstmt.setNull(2, Types.NVARCHAR);
                pstmt.setNull(3, Types.NVARCHAR);
            } else {
                pstmt.setNull(1, Types.VARCHAR);
                pstmt.setString(2, hoaDon.getTenKhachHangVangLai());
                pstmt.setString(3, hoaDon.getSdtKhachHangVangLai());
            }
            pstmt.setString(4, hoaDon.getMaNhanVienLap());
            pstmt.setTimestamp(5, new Timestamp(hoaDon.getNgayBan().getTime()));
            pstmt.setString(6, hoaDon.getPhuongThucThanhToan());
            pstmt.setInt(7, hoaDon.getMaHoaDon());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public boolean delete(Connection conn, Integer maHoaDon) throws SQLException {
        String SQL_DELETE = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
        boolean success = false;
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_DELETE)) {

            pstmt.setInt(1, maHoaDon);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                success = true;
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi xóa hóa đơn: " + ex.getMessage());
            throw ex;
        }
        return success;
    }

    @Override
    public List<HoaDon> getAll() {
        List<HoaDon> hoaDonList = new ArrayList<>();
        String SQL_SELECT_ALL = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon";
        try (Connection conn = DatabaseConnection.getConnection(); // Mở và đóng Connection tại đây
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SQL_SELECT_ALL)) {

            while (rs.next()) {
                HoaDon hoaDon = new HoaDon();
                hoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                hoaDon.setMaKhachHang(rs.getString("MaKhachHang"));
                hoaDon.setTenKhachHangVangLai(rs.getString("TenKhachHangVangLai"));
                hoaDon.setSdtKhachHangVangLai(rs.getString("SDTKhachHangVangLai"));
                hoaDon.setMaNhanVienLap(rs.getString("MaNhanVienLap"));
                hoaDon.setNgayBan(new Date(rs.getTimestamp("NgayBan").getTime()));
                hoaDon.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
                hoaDonList.add(hoaDon);
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy tất cả hóa đơn: " + ex.getMessage());
            ex.printStackTrace();
        }
        return hoaDonList;
    }
    
    
    public List<HoaDon> getHoaDonByMaKhachHang(Connection conn, String maKhachHang) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        // SQL_SELECT này sẽ lấy các hóa đơn dựa trên MaKhachHang của khách hàng đã đăng ký
        String SQL_SELECT = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE MaKhachHang = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, maKhachHang); // Đặt MaKhachHang vào câu truy vấn
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hoaDon = new HoaDon();
                    hoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                    hoaDon.setMaKhachHang(rs.getString("MaKhachHang"));
                    hoaDon.setTenKhachHangVangLai(rs.getString("TenKhachHangVangLai"));
                    hoaDon.setSdtKhachHangVangLai(rs.getString("SDTKhachHangVangLai"));
                    hoaDon.setMaNhanVienLap(rs.getString("MaNhanVienLap"));
                    hoaDon.setNgayBan(new Date(rs.getTimestamp("NgayBan").getTime()));
                    hoaDon.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
                    hoaDonList.add(hoaDon);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy hóa đơn theo mã khách hàng: " + ex.getMessage());
            throw ex; // Ném ngoại lệ để tầng Service xử lý transaction
        }
        return hoaDonList;
    }
    
    public List<HoaDon> getHoaDonBySdt(Connection conn, String Sdt) throws SQLException {
        List<HoaDon> hoaDonList = new ArrayList<>();
        // SQL_SELECT này sẽ lấy các hóa đơn dựa trên SDT của khách hàng
        String SQL_SELECT = "SELECT MaHoaDon, MaKhachHang, TenKhachHangVangLai, SDTKhachHangVangLai, MaNhanVienLap, NgayBan, PhuongThucThanhToan FROM HoaDon WHERE SDTKhachHangVangLai = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_SELECT)) {
            pstmt.setString(1, Sdt); // Đặt SDT vào câu truy vấn
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hoaDon = new HoaDon();
                    hoaDon.setMaHoaDon(rs.getInt("MaHoaDon"));
                    hoaDon.setMaKhachHang(rs.getString("MaKhachHang"));
                    hoaDon.setTenKhachHangVangLai(rs.getString("TenKhachHangVangLai"));
                    hoaDon.setSdtKhachHangVangLai(rs.getString("SDTKhachHangVangLai"));
                    hoaDon.setMaNhanVienLap(rs.getString("MaNhanVienLap"));
                    hoaDon.setNgayBan(new Date(rs.getTimestamp("NgayBan").getTime()));
                    hoaDon.setPhuongThucThanhToan(rs.getString("PhuongThucThanhToan"));
                    hoaDonList.add(hoaDon);
                }
            }
        } catch (SQLException ex) {
            System.err.println("Lỗi khi lấy hóa đơn theo Số điện thoại: " + ex.getMessage());
            throw ex; // Ném ngoại lệ để tầng Service xử lý transaction
        }
        return hoaDonList;
    }
    
    // --- Phương thức xóa mới ---
    /**
     * Xóa tất cả hóa đơn của một khách hàng cụ thể.
     * @param conn Connection hiện tại (để hỗ trợ transaction).
     * @param maKhachHang Mã khách hàng.
     * @return true nếu xóa thành công (ít nhất 1 bản ghi được xóa hoặc không có bản ghi nào để xóa), false nếu lỗi.
     * @throws SQLException Nếu có lỗi SQL xảy ra.
     */
    public boolean deleteByMaKhachHang(Connection conn, String maKhachHang) throws SQLException {
        String sql = "DELETE FROM HoaDon WHERE MaKhachHang = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, maKhachHang);
            int affectedRows = pst.executeUpdate();
            return affectedRows >= 0; // Trả về true nếu >= 0 dòng bị ảnh hưởng (bao gồm 0 nếu không có hóa đơn)
        }
    }
    
	
}