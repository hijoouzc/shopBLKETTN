import services.*; 
import view.*;
import controllers.*;
import utils.DatabaseConnection;

import java.sql.*;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;

public class test {
    public static void main(String[] args) {
        // --- 1. Kiểm tra kết nối cơ sở dữ liệu trước khi khởi động UI ---
        System.out.println("Đang kiểm tra kết nối cơ sở dữ liệu...");
        Connection testConn = null;
            testConn = DatabaseConnection.getConnection();
            if (testConn != null) {
                System.out.println("Kết nối cơ sở dữ liệu thành công!");
            } else {
                JOptionPane.showMessageDialog(null, "Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra cấu hình.", "Lỗi kết nối CSDL", JOptionPane.ERROR_MESSAGE);
                System.err.println("Không thể kết nối đến cơ sở dữ liệu. Vui lòng kiểm tra cấu hình.");
                return; // Thoát ứng dụng nếu không kết nối được
            }
    

        // --- 2. Chạy giao diện người dùng Swing trên EDT (Event Dispatch Thread) ---
        SwingUtilities.invokeLater(() -> {
            LoginView loginView = new LoginView();
            TaiKhoanNguoiDungService taiKhoanService = TaiKhoanNguoiDungService.getIns();
            LoginController loginController = new LoginController(loginView, taiKhoanService);
            loginController.showLoginView(); // Hiển thị màn hình đăng nhập
        });
        
   
        
    }
}
