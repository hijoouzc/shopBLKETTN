package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DAO.ChiTietViTriDAO;
import DAO.ViTriDungSanPhamDAO;
import models.ChiTietViTri;
import models.ViTriDungSanPham;
import utils.DatabaseConnection;

public class ViTriDungSanPhamService {
    private static ViTriDungSanPhamService instance;
    private ViTriDungSanPhamDAO viTriDungSanPhamDAO;
    private ChiTietViTriDAO chiTietViTriDAO;

    /**
     * Private constructor to implement the Singleton pattern.
     * Initializes the DAO instances using their singleton `getIns()` methods.
     */
    private ViTriDungSanPhamService() {
        this.viTriDungSanPhamDAO = ViTriDungSanPhamDAO.getIns();
        this.chiTietViTriDAO = ChiTietViTriDAO.getIns();
    }

    /**
     * Provides the singleton instance of ViTriDungSanPhamService.
     *
     * @return The single instance of ViTriDungSanPhamService.
     */
    public static ViTriDungSanPhamService getIns() {
        if (instance == null) {
            instance = new ViTriDungSanPhamService();
        }
        return instance;
    }

    // --- NGHIỆP VỤ VỊ TRÍ ĐỰNG SẢN PHẨM VÀ TỒN KHO ---

    /**
     * Nghiệp vụ: Thêm vị trí đựng sản phẩm mới.
     * @param viTriDungSanPham Đối tượng ViTriDungSanPham chứa thông tin.
     * @return ViTriDungSanPham nếu thêm thành công, null nếu thất bại.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public ViTriDungSanPham themViTriDungSanPham(ViTriDungSanPham viTriDungSanPham) throws SQLException {
        Connection conn = null;
        ViTriDungSanPham newViTri = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu giao dịch

            if (viTriDungSanPham == null || viTriDungSanPham.getMaNganDung() == null || viTriDungSanPham.getMaNganDung().trim().isEmpty() ||
                viTriDungSanPham.getTenNganDung() == null || viTriDungSanPham.getTenNganDung().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu vị trí đựng sản phẩm không hợp lệ.");
                conn.rollback();
                return null;
            }

            // Kiểm tra tên ngăn đựng đã tồn tại chưa
            if (viTriDungSanPhamDAO.getViTriDungSanPhamByTen(conn, viTriDungSanPham.getTenNganDung()) != null) {
                System.err.println("Lỗi: Tên ngăn đựng '" + viTriDungSanPham.getTenNganDung() + "' đã tồn tại.");
                conn.rollback();
                return null;
            }

            newViTri = viTriDungSanPhamDAO.add(conn, viTriDungSanPham);
            if (newViTri == null) {
                System.err.println("Lỗi: Thêm vị trí đựng sản phẩm thất bại.");
                conn.rollback();
                return null;
            }
            conn.commit();
            return newViTri;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi thêm vị trí đựng sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch thêm vị trí đựng sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nghiệp vụ: Cập nhật thông tin vị trí đựng sản phẩm.
     * @param viTriDungSanPham Đối tượng ViTriDungSanPham với thông tin cập nhật.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi cơ sở dữ liệu.
     */
    public boolean capNhatViTriDungSanPham(ViTriDungSanPham viTriDungSanPham) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            if (viTriDungSanPham == null || viTriDungSanPham.getMaNganDung() == null || viTriDungSanPham.getMaNganDung().trim().isEmpty() ||
                viTriDungSanPham.getTenNganDung() == null || viTriDungSanPham.getTenNganDung().trim().isEmpty()) {
                System.err.println("Lỗi nghiệp vụ: Dữ liệu vị trí đựng sản phẩm không hợp lệ để cập nhật.");
                conn.rollback();
                return false;
            }

            ViTriDungSanPham existingViTri = viTriDungSanPhamDAO.getById(conn, viTriDungSanPham.getMaNganDung());
            if (existingViTri == null) {
                System.err.println("Lỗi: Không tìm thấy vị trí đựng sản phẩm với mã: " + viTriDungSanPham.getMaNganDung() + " để cập nhật.");
                conn.rollback();
                return false;
            }

            if (!existingViTri.getTenNganDung().equalsIgnoreCase(viTriDungSanPham.getTenNganDung())) {
                if (viTriDungSanPhamDAO.getViTriDungSanPhamByTen(conn, viTriDungSanPham.getTenNganDung()) != null) {
                    System.err.println("Lỗi: Tên ngăn đựng '" + viTriDungSanPham.getTenNganDung() + "' đã được sử dụng bởi vị trí khác.");
                    conn.rollback();
                    return false;
                }
            }

            boolean updated = viTriDungSanPhamDAO.update(conn, viTriDungSanPham);
            conn.commit();
            return updated;
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi cập nhật vị trí đựng sản phẩm: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("Đã rollback giao dịch cập nhật vị trí đựng sản phẩm.");
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
    }

 
    /**
     * Nghiệp vụ: Tăng số lượng tồn kho của một sản phẩm.
     * Tìm một ChiTietViTri hiện có cho sản phẩm. Nếu không có, tạo một ChiTietViTri mới
     * tại một vị trí mặc định (ví dụ: "KHO_CHINH").
     *
     * @param conn Kết nối CSDL.
     * @param maSanPham Mã sản phẩm.
     * @param soLuongTang Số lượng cần tăng.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi CSDL.
     */
    public boolean tangSoLuongTon(Connection conn, String maSanPham, int soLuongTang) throws SQLException {
        ChiTietViTri chiTiet = chiTietViTriDAO.getAnyByMaSanPham(conn, maSanPham); // Tìm bất kỳ chi tiết tồn kho nào của sản phẩm

        if (chiTiet != null) {
            chiTiet.setSoLuong(chiTiet.getSoLuong() + soLuongTang);
            return chiTietViTriDAO.update(conn, chiTiet);
        } else {
            // Nếu không tìm thấy, tạo một ChiTietViTri mới ở một vị trí mặc định
            // Cần đảm bảo "KHO_CHINH" tồn tại trong ViTriDungSanPham hoặc tạo nó
            String defaultMaNganDung = "KHO_CHINH"; // Hoặc một logic chọn vị trí khác
            ViTriDungSanPham defaultLocation = viTriDungSanPhamDAO.getById(conn, defaultMaNganDung);
            if (defaultLocation == null) {
                System.out.println("Tạo vị trí mặc định 'KHO_CHINH' cho sản phẩm mới nhập.");
//                defaultLocation = new ViTriDungSanPham(defaultMaNganDung, "Kho Chính");
                viTriDungSanPhamDAO.add(conn, defaultLocation); // Thêm vị trí mặc định nếu chưa có
            }

            ChiTietViTri newChiTiet = new ChiTietViTri();
            newChiTiet.setMaSanPham(maSanPham);
            newChiTiet.setMaNganDung(defaultMaNganDung);
            newChiTiet.setSoLuong(soLuongTang);
            ChiTietViTri added = chiTietViTriDAO.add(conn, newChiTiet);
            return added != null;
        }
    }

    /**
     * Nghiệp vụ: Giảm số lượng tồn kho của một sản phẩm.
     * Giảm số lượng từ ChiTietViTri hiện có. Nếu không đủ, sẽ báo lỗi.
     *
     * @param conn Kết nối CSDL.
     * @param maSanPham Mã sản phẩm.
     * @param soLuongGiam Số lượng cần giảm.
     * @return true nếu cập nhật thành công, false nếu thất bại.
     * @throws SQLException Nếu có lỗi CSDL hoặc tồn kho không đủ.
     */
    public boolean giamSoLuongTon(Connection conn, String maSanPham, int soLuongGiam) throws SQLException {
        ChiTietViTri chiTiet = chiTietViTriDAO.getAnyByMaSanPham(conn, maSanPham); // Tìm bất kỳ chi tiết tồn kho nào của sản phẩm

        if (chiTiet != null) {
            if (chiTiet.getSoLuong() >= soLuongGiam) {
                chiTiet.setSoLuong(chiTiet.getSoLuong() - soLuongGiam);
                return chiTietViTriDAO.update(conn, chiTiet);
            } else {
                System.err.println("Lỗi nghiệp vụ: Không đủ tồn kho (" + chiTiet.getSoLuong() + ") để giảm " + soLuongGiam + " cho sản phẩm " + maSanPham + " tại vị trí " + chiTiet.getMaNganDung());
                return false;
            }
        } else {
            System.err.println("Lỗi nghiệp vụ: Không tìm thấy tồn kho cho sản phẩm " + maSanPham + " để giảm.");
            return false;
        }
    }

    /**
     * Retrieves all product storage locations.
     * @return A list of all ViTriDungSanPham objects.
     * @throws SQLException If a database access error occurs.
     */
    public List<ViTriDungSanPham> getAllViTriDungSanPham() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return viTriDungSanPhamDAO.getAll();
        }
    }

    /**
     * Nghiệp vụ: Lấy tất cả chi tiết tồn kho (ChiTietViTri).
     * @return Danh sách tất cả ChiTietViTri.
     * @throws SQLException If a database access error occurs.
     */
    public List<ChiTietViTri> getAllChiTietViTri() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return chiTietViTriDAO.getAll();
        }
    }

    /**
     * Nghiệp vụ: Lấy chi tiết tồn kho theo mã ngăn đựng và mã sản phẩm.
     * @param maNganDung Mã ngăn đựng.
     * @param maSanPham Mã sản phẩm.
     * @return ChiTietViTri nếu tìm thấy, null nếu không.
     * @throws SQLException If a database access error occurs.
     */
    public ChiTietViTri getChiTietViTriByMaNganDungAndMaSanPham(String maNganDung, String maSanPham) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return chiTietViTriDAO.getByMaNganDungAndMaSanPham(conn, maNganDung, maSanPham);
        }
    }
}
