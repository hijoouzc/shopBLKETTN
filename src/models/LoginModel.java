package models;

/**
 * Lớp LoginModel đại diện cho dữ liệu của màn hình đăng nhập.
 * Chứa thông tin tên đăng nhập và mật khẩu mà người dùng nhập vào.
 */
public class LoginModel {
    private String username;
    private String password;

    public LoginModel() {
        // Constructor mặc định
    }

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // --- Getters and Setters ---
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginModel{" +
               "username='" + username + '\'' +
               ", password='" + "[HIDDEN]" + '\'' + // Không hiển thị mật khẩu trong toString
               '}';
    }
}
