package controllers;

import models.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung model
import services.TaiKhoanNguoiDungService; // Import TaiKhoanNguoiDungService
import view.LoginView; // Import LoginView
import view.MainDashBoard; // Giả định có MainDashboard để chuyển đến
import javax.swing.*;
import java.awt.*; // Import for Layout and Component
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // For MouseListener
import java.awt.event.MouseEvent; // For MouseEvent
import java.util.Arrays; // Để so sánh mảng ký tự mật khẩu
import java.sql.SQLException; // Import SQLException

/**
 * Lớp LoginController xử lý logic nghiệp vụ cho màn hình đăng nhập.
 * Nó lắng nghe các sự kiện từ LoginView, tương tác với TaiKhoanNguoiDungService
 * và cập nhật LoginView hoặc điều hướng đến màn hình chính.
 */
public class LoginController {
    private LoginView view;
    private TaiKhoanNguoiDungService taiKhoanService; // Service layer

    public LoginController(LoginView view, TaiKhoanNguoiDungService taiKhoanService) {
        this.view = view;
        this.taiKhoanService = taiKhoanService;

        // Đăng ký ActionListener cho nút đăng nhập
        this.view.addLoginListener(new LoginListener());
        // Đăng ký MouseListener cho nhãn "Forgot Password?"
        this.view.getForgotPasswordLabel().addMouseListener(new ForgotPasswordListener());
        // Đăng ký MouseListener cho nhãn "Signup"
        this.view.getSignupLabel().addMouseListener(new SignupListener()); // Thêm listener cho signup
    }

    /**
     * Lớp nội bộ để xử lý sự kiện click nút đăng nhập.
     * Đây là cách Controller lắng nghe View.
     */
    class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = view.getUsername();
            char[] passwordChars = view.getPassword(); // Lấy mật khẩu dưới dạng mảng ký tự
            String password = new String(passwordChars); // Chuyển đổi sang String để truyền vào Service

            // Xóa mảng ký tự mật khẩu ngay sau khi sử dụng để tăng cường bảo mật
            Arrays.fill(passwordChars, ' ');

            if (username.isEmpty() || password.isEmpty()) {
                view.displayMessage("Vui lòng nhập tên đăng nhập và mật khẩu.", true);
                return;
            }

            // Gọi service để xử lý logic đăng nhập
            TaiKhoanNguoiDung loggedInAccount = taiKhoanService.dangNhap(username, password);

            if (loggedInAccount != null) {
                view.displayMessage("Đăng nhập thành công! Chào mừng " + loggedInAccount.getUsername() + ".", false);
                // Đóng cửa sổ đăng nhập và mở màn hình chính
                view.dispose(); // Đóng LoginView
                
                // Mở MainDashboard hoặc điều hướng đến các màn hình khác tùy theo LoaiNguoiDung
                SwingUtilities.invokeLater(() -> {
                    // Truyền thông tin tài khoản đã đăng nhập vào MainDashboard
                    MainDashBoard mainDashboard = new MainDashBoard(loggedInAccount);
                    mainDashboard.setVisible(true);
                });
            } else {
                view.displayMessage("Tên đăng nhập hoặc mật khẩu không đúng, hoặc tài khoản bị khóa.", true);
            }
        }
    }

    /**
     * Lớp nội bộ để xử lý sự kiện click trên nhãn "Forgot Password?".
     * Hiển thị dialog để người dùng nhập tên đăng nhập và sau đó nhập mật khẩu mới.
     */
    class ForgotPasswordListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            String username = JOptionPane.showInputDialog(view, "Vui lòng nhập tên đăng nhập của bạn:", "Quên Mật Khẩu", JOptionPane.QUESTION_MESSAGE);

            if (username != null && !username.trim().isEmpty()) {
                // Kiểm tra xem tài khoản có tồn tại không trước khi yêu cầu mật khẩu mới
                TaiKhoanNguoiDung account = taiKhoanService.getTaiKhoanByUsername(username.trim());
                if (account == null) {
                    view.displayMessage("Tên đăng nhập không tồn tại. Vui lòng kiểm tra lại.", true);
                    return;
                }

                // Tạo JDialog tùy chỉnh để nhập mật khẩu mới và xác nhận
                JDialog resetPasswordDialog = new JDialog(view, "Đặt Lại Mật Khẩu", true);
                resetPasswordDialog.setLayout(new GridBagLayout());
                resetPasswordDialog.setSize(350, 200);
                resetPasswordDialog.setLocationRelativeTo(view);
                resetPasswordDialog.setResizable(false);

                GridBagConstraints gbc = new GridBagConstraints();
                gbc.insets = new Insets(5, 5, 5, 5);
                gbc.fill = GridBagConstraints.HORIZONTAL;

                JLabel newPasswordLabel = new JLabel("Mật khẩu mới:");
                JPasswordField newPasswordField = new JPasswordField(20);
                JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
                JPasswordField confirmPasswordField = new JPasswordField(20);
                JButton resetButton = new JButton("Đặt lại Mật khẩu");

                gbc.gridx = 0; gbc.gridy = 0; resetPasswordDialog.add(newPasswordLabel, gbc);
                gbc.gridx = 1; resetPasswordDialog.add(newPasswordField, gbc);

                gbc.gridx = 0; gbc.gridy = 1; resetPasswordDialog.add(confirmPasswordLabel, gbc);
                gbc.gridx = 1; resetPasswordDialog.add(confirmPasswordField, gbc);

                gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
                resetPasswordDialog.add(resetButton, gbc);

                resetButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        char[] newPassChars = newPasswordField.getPassword();
                        char[] confirmPassChars = confirmPasswordField.getPassword();

                        String newPassword = new String(newPassChars);
                        String confirmPassword = new String(confirmPassChars);

                        // Xóa mảng ký tự mật khẩu ngay sau khi sử dụng
                        Arrays.fill(newPassChars, ' ');
                        Arrays.fill(confirmPassChars, ' ');

                        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            JOptionPane.showMessageDialog(resetPasswordDialog, "Mật khẩu mới và xác nhận mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!newPassword.equals(confirmPassword)) {
                            JOptionPane.showMessageDialog(resetPasswordDialog, "Mật khẩu mới và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Gọi service để cập nhật mật khẩu
                        boolean success = taiKhoanService.quenMatKhau(username.trim(), newPassword);
                        if (success) {
                            JOptionPane.showMessageDialog(resetPasswordDialog, "Mật khẩu đã được đặt lại thành công. Vui lòng đăng nhập lại.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            resetPasswordDialog.dispose(); // Đóng dialog đặt lại mật khẩu
                        } else {
                            JOptionPane.showMessageDialog(resetPasswordDialog, "Không thể đặt lại mật khẩu. Vui lòng thử lại sau.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                resetPasswordDialog.setVisible(true); // Hiển thị dialog
            } else if (username != null) {
                view.displayMessage("Tên đăng nhập không được để trống.", true);
            }
            // Nếu username == null (người dùng nhấn Cancel), không làm gì cả
        }
    }

    /**
     * Lớp nội bộ để xử lý sự kiện click trên nhãn "Signup".
     * Hiển thị dialog để người dùng nhập thông tin đăng ký tài khoản mới.
     */
    class SignupListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JDialog signupDialog = new JDialog(view, "Đăng Ký Tài Khoản Mới", true);
            signupDialog.setLayout(new GridBagLayout());
            signupDialog.setSize(400, 300); // Tăng kích thước dialog để chứa trường email
            signupDialog.setLocationRelativeTo(view);
            signupDialog.setResizable(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel usernameLabel = new JLabel("Tên đăng nhập:");
            JTextField signupUsernameField = new JTextField(20);
            JLabel emailLabel = new JLabel("Email:"); // Thêm nhãn Email
            JTextField signupEmailField = new JTextField(20); // Thêm trường Email
            JLabel passwordLabel = new JLabel("Mật khẩu:");
            JPasswordField signupPasswordField = new JPasswordField(20);
            JLabel confirmPasswordLabel = new JLabel("Xác nhận mật khẩu:");
            JPasswordField signupConfirmPasswordField = new JPasswordField(20);
            JButton signupButton = new JButton("Đăng Ký");

            gbc.gridx = 0; gbc.gridy = 0; signupDialog.add(usernameLabel, gbc);
            gbc.gridx = 1; signupDialog.add(signupUsernameField, gbc);

            gbc.gridx = 0; gbc.gridy = 1; signupDialog.add(emailLabel, gbc); // Thêm Email vào layout
            gbc.gridx = 1; signupDialog.add(signupEmailField, gbc);

            gbc.gridx = 0; gbc.gridy = 2; signupDialog.add(passwordLabel, gbc);
            gbc.gridx = 1; signupDialog.add(signupPasswordField, gbc);

            gbc.gridx = 0; gbc.gridy = 3; signupDialog.add(confirmPasswordLabel, gbc);
            gbc.gridx = 1; signupDialog.add(signupConfirmPasswordField, gbc);

            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
            signupDialog.add(signupButton, gbc);

            signupButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String username = signupUsernameField.getText().trim();
                    String email = signupEmailField.getText().trim(); // Lấy giá trị Email
                    char[] passwordChars = signupPasswordField.getPassword();
                    char[] confirmPasswordChars = signupConfirmPasswordField.getPassword();

                    String password = new String(passwordChars);
                    String confirmPassword = new String(confirmPasswordChars);

                    // Xóa mảng ký tự mật khẩu ngay sau khi sử dụng
                    Arrays.fill(passwordChars, ' ');
                    Arrays.fill(confirmPasswordChars, ' ');

                    if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) { // Thêm kiểm tra email
                        JOptionPane.showMessageDialog(signupDialog, "Tên đăng nhập, email, mật khẩu và xác nhận mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(signupDialog, "Mật khẩu và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        // Kiểm tra xem tên đăng nhập đã tồn tại chưa
                        if (taiKhoanService.getTaiKhoanByUsername(username) != null) {
                            JOptionPane.showMessageDialog(signupDialog, "Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Tạo đối tượng TaiKhoanNguoiDung mới
                        TaiKhoanNguoiDung newAccount = new TaiKhoanNguoiDung();
                        newAccount.setUsername(username);
                        newAccount.setEmail(email); // Gán email vào đối tượng TaiKhoanNguoiDung
                        newAccount.setPassword(password); // Mật khẩu cần được mã hóa trong Service hoặc DAO
                        newAccount.setLoaiNguoiDung("Khách hàng"); // Mặc định quyền cho tài khoản đăng ký
                        newAccount.setTrangThaiTaiKhoan("Hoạt động"); // Mặc định trạng thái

                        String createdMaNguoiDung = taiKhoanService.addTaiKhoan(newAccount);
                        if (createdMaNguoiDung != null) {
                            JOptionPane.showMessageDialog(signupDialog, "Đăng ký tài khoản thành công! Mã người dùng: " + createdMaNguoiDung + ". Vui lòng đăng nhập.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            signupDialog.dispose(); // Đóng dialog đăng ký
                        } else {
                            JOptionPane.showMessageDialog(signupDialog, "Đăng ký tài khoản thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(signupDialog, "Lỗi khi đăng ký tài khoản: " + ex.getMessage(), "Lỗi SQL", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(signupDialog, "Lỗi không xác định khi đăng ký tài khoản: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });
            signupDialog.setVisible(true); // Hiển thị dialog
        }
    }

    /**
     * Hiển thị màn hình đăng nhập.
     */
    public void showLoginView() {
        view.setVisible(true);
    }
}
