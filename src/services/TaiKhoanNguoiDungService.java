package services;

import DAO.TaiKhoanNguoiDungDAO;
import models.TaiKhoanNguoiDung;
import utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class TaiKhoanNguoiDungService {
    private TaiKhoanNguoiDungDAO taiKhoanNguoiDungDAO;
    private static TaiKhoanNguoiDungService instance; // Singleton instance

    // Private constructor for Singleton pattern
    private TaiKhoanNguoiDungService() {
        this.taiKhoanNguoiDungDAO = TaiKhoanNguoiDungDAO.getIns();
    }

    // Static method to get the singleton instance
    public static TaiKhoanNguoiDungService getIns() {
        if (instance == null) {
            instance = new TaiKhoanNguoiDungService();
        }
        return instance;
    }
    

        public String addTaiKhoan(TaiKhoanNguoiDung taiKhoan) throws SQLException {
            Connection conn = null;
            String newMaNguoiDung = null;
            try {
                conn = DatabaseConnection.getConnection();
                conn.setAutoCommit(false);

                // RẤT QUAN TRỌNG: Gán giá trị cho ngayTao ở đây nếu nó chưa được set từ View/Controller
                if (taiKhoan.getNgayTao() == null) {
                    taiKhoan.setNgayTao(new Date()); // Gán ngày tạo là thời điểm hiện tại
                }


                TaiKhoanNguoiDung createdAccount = taiKhoanNguoiDungDAO.add(conn, taiKhoan);
                if (createdAccount != null) {
                    newMaNguoiDung = createdAccount.getMaNguoiDung();
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        System.err.println("Lỗi khi rollback: " + ex.getMessage());
                    }
                }
                System.err.println("Lỗi SQL khi thêm tài khoản: " + e.getMessage());
                throw e;
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    }
                }
            }
            return newMaNguoiDung;
        }


        // Đảm bảo các phương thức lấy dữ liệu từ DB cũng set ngayTao
        public TaiKhoanNguoiDung getTaiKhoanByUsername(String username) {
            Connection conn = null;
            TaiKhoanNguoiDung account = null;
            try {
                conn = DatabaseConnection.getConnection();
                account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);
                // Sau khi lấy từ DAO, đảm bảo ngayTao được set đúng trong đối tượng 'account'
            } catch (SQLException e) {
                System.err.println("Lỗi SQL khi lấy tài khoản theo username: " + e.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                    }
                }
            }
            return account;
        }
        
   
    /**
     * Đăng nhập người dùng.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu.
     * @return Đối tượng TaiKhoanNguoiDung nếu đăng nhập thành công, hoặc null nếu thất bại.
     */
    public TaiKhoanNguoiDung dangNhap(String username, String password) {
        Connection conn = null;
        TaiKhoanNguoiDung account = null;
        try {
            conn = DatabaseConnection.getConnection();
            account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);
            if (account != null) {
                // TODO: Compare hashed password, not plain text
                if (account.getPassword().equals(password)) { // This should be PasswordHasher.verify(password, account.getPassword())
                    if (account.getTrangThaiTaiKhoan().equals("Hoạt động")) {
                        System.out.println("Đăng nhập thành công cho tài khoản: " + username);
                        return account;
                    } else {
                        System.out.println("Đăng nhập thất bại: Tài khoản không hoạt động hoặc bị khóa.");
                    }
                } else {
                    System.out.println("Đăng nhập thất bại: Sai mật khẩu.");
                }
            } else {
                System.out.println("Đăng nhập thất bại: Tên đăng nhập không tồn tại.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi đăng nhập: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Lấy tất cả danh sách tài khoản người dùng.
     * @return Danh sách các đối tượng TaiKhoanNguoiDung.
     */
    public List<TaiKhoanNguoiDung> getAllTaiKhoan() {
        List<TaiKhoanNguoiDung> accounts = null;
        accounts = taiKhoanNguoiDungDAO.getAll();
   
        return accounts;
    }

    /**
     * Quên mật khẩu: cập nhật mật khẩu mới cho tài khoản.
     * @param username Tên đăng nhập của tài khoản.
     * @param newPassword Mật khẩu mới (cần được mã hóa trước khi lưu).
     * @return True nếu cập nhật thành công, False nếu thất bại.
     */
    public boolean quenMatKhau(String username, String newPassword) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getTaiKhoanByUsername(conn, username);
            if (account != null) {
                account.setPassword(newPassword);
                success = taiKhoanNguoiDungDAO.update(conn, account);
                conn.commit();
            } else {
                System.out.println("Không tìm thấy tài khoản với tên đăng nhập: " + username);
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch quên mật khẩu: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi SQL khi quên mật khẩu: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * Quản lý trạng thái tài khoản (ví dụ: kích hoạt/vô hiệu hóa).
     * @param maNguoiDung Mã người dùng của tài khoản.
     * @param newTrangThaiTaiKhoan Trạng thái mới.
     * @return True nếu cập nhật thành công, False nếu thất bại.
     */
    public boolean quanLyTaiKhoan(String maNguoiDung, String newTrangThaiTaiKhoan) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            TaiKhoanNguoiDung account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
            if (account != null) {
                account.setTrangThaiTaiKhoan(newTrangThaiTaiKhoan);
                success = taiKhoanNguoiDungDAO.update(conn, account);
                conn.commit();
            } else {
                System.out.println("Lỗi: Không tìm thấy tài khoản với mã người dùng: " + maNguoiDung);
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch quản lý tài khoản: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi SQL khi quản lý tài khoản: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * Lấy thông tin tài khoản theo mã người dùng.
     * @param maNguoiDung Mã người dùng.
     * @return Đối tượng TaiKhoanNguoiDung, hoặc null nếu không tìm thấy.
     */
    public TaiKhoanNguoiDung getTaiKhoanByMaNguoiDung(String maNguoiDung) {
        Connection conn = null;
        TaiKhoanNguoiDung account = null;
        try {
            conn = DatabaseConnection.getConnection();
            account = taiKhoanNguoiDungDAO.getById(conn, maNguoiDung);
        } catch (SQLException e) {
            System.err.println("Lỗi SQL khi lấy tài khoản theo mã: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return account;
    }

    /**
     * Xóa tài khoản người dùng.
     * @param maNguoiDung Mã người dùng cần xóa.
     * @return True nếu xóa thành công, False nếu thất bại.
     */
    public boolean deleteTaiKhoan(String maNguoiDung) {
        Connection conn = null;
        boolean success = false;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction
            success = taiKhoanNguoiDungDAO.delete(conn, maNguoiDung);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback giao dịch xóa tài khoản: " + ex.getMessage());
                }
            }
            System.err.println("Lỗi SQL khi xóa tài khoản: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
                }
            }
        }
        return success;
    }
}