package view;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Lớp LoginView tạo giao diện người dùng cho màn hình đăng nhập.
 * Nó hiển thị các trường nhập liệu cho tên đăng nhập và mật khẩu, cùng với nút đăng nhập.
 * Thiết kế được điều chỉnh để giống với hình ảnh mẫu đã cung cấp.
 */
public class LoginView extends JFrame {
    private static final long serialVersionUID = 1L; // Serial version UID

    private JLabel logoLabel; // Thêm JLabel cho logo
    private JTextField usernameField; // Đã giữ nguyên là usernameField
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công
    private JLabel forgotPasswordLabel; // Nhãn "Forgot Password?"
    private JButton googleLoginButton; // Nút đăng nhập Google
    private JLabel signupCombinedLabel; // Kết hợp "Don't have an account? Signup"

    public LoginView() {
        setTitle("Đăng nhập Hệ thống Quản lý Cửa hàng");
        setSize(400, 550); // Tăng kích thước để chứa logo
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Đặt cửa sổ ở giữa màn hình

        initComponents(); // Khởi tạo các thành phần UI
        layoutComponents(); // Sắp xếp các thành phần UI
    }

    /**
     * Khởi tạo các thành phần giao diện người dùng.
     */
    private void initComponents() {
        // Khởi tạo logoLabel
        logoLabel = new JLabel();
        // Cố gắng tải hình ảnh từ đường dẫn /images/logo.png
        URL logoImageUrl = getClass().getResource("/icons/logo.png"); 
            ImageIcon logoIcon = new ImageIcon(logoImageUrl);
            // Thay đổi kích thước logo nếu cần
            Image scaledLogoImage = logoIcon.getImage().getScaledInstance(30, 40, Image.SCALE_SMOOTH); // Ví dụ: 100x100
            logoLabel.setIcon(new ImageIcon(scaledLogoImage));

        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);


        usernameField = new JTextField(20);
        // Placeholder text for username field
        usernameField.putClientProperty("JTextField.placeholderText", "Enter your username"); 
        
        passwordField = new JPasswordField(20);
        // Placeholder text for password field
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password"); 
        
        // Custom login button with rounded corners
        loginButton = new JButton("Log In") {
            /**
			 * */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); // Sử dụng màu nền của nút
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Vẽ nền bo tròn (radius 15)
                super.paintComponent(g2); // Vẽ văn bản và icon
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Không vẽ border mặc định để có thể kiểm soát hoàn toàn bởi paintComponent
            }
        };
        loginButton.setContentAreaFilled(false); // Tắt fill mặc định để paintComponent có hiệu lực
        loginButton.setBorderPainted(false); // Tắt vẽ border mặc định
        loginButton.setFocusPainted(false); // Bỏ viền focus
        loginButton.setBackground(new Color(25, 69, 137)); // Màu nền tương tự nút trong hình
        loginButton.setForeground(Color.WHITE); // Màu chữ trắng
        loginButton.setFont(new Font("Arial", Font.BOLD, 16)); // Font chữ đậm, to hơn
        loginButton.setPreferredSize(new Dimension(200, 40)); // Đặt kích thước cố định cho nút

        // Áp dụng RoundedCornerBorder cho usernameField và passwordField
        usernameField.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); // radius 10
        passwordField.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); // radius 10


        messageLabel = new JLabel(""); // Ban đầu trống
        messageLabel.setForeground(Color.RED); // Màu đỏ cho thông báo lỗi
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        forgotPasswordLabel = new JLabel("<html><u>Forgot Password?</u></html>"); // Thêm underline
        forgotPasswordLabel.setForeground(new Color(25, 69, 137)); // Màu xanh đậm hơn
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Đổi con trỏ thành hình bàn tay
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // Font nhỏ hơn

        // Custom Google Login Button with rounded (circular) shape
        googleLoginButton = new JButton() {
            /**
			 * */
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground()); // Sử dụng màu nền của nút (ví dụ: trắng)
                g2.fillOval(0, 0, getWidth(), getHeight()); // Vẽ nền hình tròn
                super.paintComponent(g2); // Vẽ văn bản và icon (quan trọng để icon hiển thị)
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.LIGHT_GRAY); // Màu border
                g2.setStroke(new BasicStroke(1)); // Độ dày border
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1); // Vẽ border hình tròn
                g2.dispose();
            }
        };
        googleLoginButton.setContentAreaFilled(false); // Tắt fill mặc định
        googleLoginButton.setBorderPainted(false); // Tắt vẽ border mặc định
        googleLoginButton.setBackground(Color.WHITE); // Đặt nền trắng rõ ràng
        googleLoginButton.setPreferredSize(new Dimension(80, 80)); // Kích thước icon (W=H để thành tròn)

        // Tải hình ảnh Google một cách an toàn

            googleLoginButton.setText("G"); // Placeholder text if image not found
            googleLoginButton.setFont(new Font("Arial", Font.BOLD, 30));
            googleLoginButton.setForeground(Color.GRAY);


        // Combined "Don't have an account? Signup" label using HTML
        signupCombinedLabel = new JLabel(
            "<html>Don't have an account? <span style='color:rgb(25, 69, 137); font-weight:bold; cursor:hand;'>Signup</span></html>"
        );
        signupCombinedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signupCombinedLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signupCombinedLabel.setFont(new Font("Arial", Font.PLAIN, 12)); // Consistent font size
        
        // Thêm MouseListener trực tiếp vào JLabel kết hợp để bắt sự kiện click vào phần "Signup"
        signupCombinedLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kiểm tra xem click có nằm trên phần "Signup" không
                // Đây là cách đơn giản để xử lý, có thể phức tạp hơn nếu cần xác định chính xác vùng click
                // Ước lượng vị trí "Signup" (có thể cần điều chỉnh tùy thuộc vào font và nội dung)
                // Một cách chính xác hơn là sử dụng FontMetrics để tính toán chiều rộng của phần text
                String text = signupCombinedLabel.getText();
                int signupStart = text.indexOf("Signup");
                if (signupStart != -1) {
                    // Loại bỏ thẻ HTML để tính toán độ rộng văn bản
                    String plainText = signupCombinedLabel.getText().replaceAll("<[^>]*>", "");
                    String prefixText = plainText.substring(0, plainText.indexOf("Signup"));
                    
                    FontMetrics fm = signupCombinedLabel.getFontMetrics(signupCombinedLabel.getFont());
                    int prefixWidth = fm.stringWidth(prefixText);
                    int signupWidth = fm.stringWidth("Signup");

                    // Lấy vị trí x của JLabel trong panel
                    Point labelLocation = signupCombinedLabel.getLocation();
                    int clickXRelativeToLabel = e.getX();

                    // Điều chỉnh theo căn giữa của JLabel
                    int totalTextWidth = fm.stringWidth(plainText);
                    int labelWidth = signupCombinedLabel.getWidth();
                    int offsetX = (labelWidth - totalTextWidth) / 2; // Offset nếu label được căn giữa

                    if (clickXRelativeToLabel >= offsetX + prefixWidth && clickXRelativeToLabel <= offsetX + prefixWidth + signupWidth) {
                        System.out.println("Signup link clicked!");
                        // Thực hiện hành động chuyển sang màn hình đăng ký
                    }
                }
            }
        });
    }

    /**
     * Sắp xếp các thành phần giao diện trên cửa sổ.
     */
    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE); // Nền trắng
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Tăng padding tổng thể
        
        // --- Thêm Logo vào đầu ---
        GridBagConstraints gbcLogo = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcLogo.gridx = 0;
        gbcLogo.gridy = 0;
        gbcLogo.gridwidth = 2;
        gbcLogo.insets = new Insets(0, 0, 15, 0); // Khoảng cách dưới logo
        gbcLogo.anchor = GridBagConstraints.CENTER;
        panel.add(logoLabel, gbcLogo);

        // --- Tiêu đề "Log In" ---
        GridBagConstraints gbcHeader = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcHeader.insets = new Insets(4, 5, 4, 5);
        gbcHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcHeader.gridx = 0;
        gbcHeader.gridy = 1;
        gbcHeader.gridwidth = 2;
        JLabel headerLabel = new JLabel("Log In");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 22));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(headerLabel, gbcHeader);

        // --- Nhãn "Enter details to continue" ---
        GridBagConstraints gbcSubHeader = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcSubHeader.insets = new Insets(4, 5, 4, 5);
        gbcSubHeader.fill = GridBagConstraints.HORIZONTAL;
        gbcSubHeader.gridx = 0;
        gbcSubHeader.gridy = 2;
        gbcSubHeader.gridwidth = 2;
        JLabel subHeaderLabel = new JLabel("Enter details to continue");
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subHeaderLabel.setForeground(Color.GRAY);
        subHeaderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(subHeaderLabel, gbcSubHeader);

        // --- Username Label ---
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcUsernameLabel.insets = new Insets(8, 5, 2, 5);
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.gridy = 3;
        gbcUsernameLabel.gridwidth = 2;
        gbcUsernameLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username"), gbcUsernameLabel);

        // --- Username Field ---
        GridBagConstraints gbcUsernameField = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcUsernameField.insets = new Insets(2, 5, 8, 5); // Đã điều chỉnh insets
        gbcUsernameField.gridx = 0;
        gbcUsernameField.gridy = 4;
        gbcUsernameField.gridwidth = 2;
        gbcUsernameField.fill = GridBagConstraints.HORIZONTAL;
        gbcUsernameField.ipady = 8;
        panel.add(usernameField, gbcUsernameField);

        // --- Password Label ---
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcPasswordLabel.insets = new Insets(8, 5, 2, 5);
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.gridy = 5;
        gbcPasswordLabel.gridwidth = 2;
        gbcPasswordLabel.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Enter Password"), gbcPasswordLabel);

        // --- Password Field ---
        GridBagConstraints gbcPasswordField = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcPasswordField.insets = new Insets(2, 5, 8, 5); // Đã điều chỉnh insets
        gbcPasswordField.gridx = 0;
        gbcPasswordField.gridy = 6;
        gbcPasswordField.gridwidth = 2;
        gbcPasswordField.fill = GridBagConstraints.HORIZONTAL;
        gbcPasswordField.ipady = 8;
        panel.add(passwordField, gbcPasswordField);

        // --- Forgot Password? Label ---
        GridBagConstraints gbcForgotPassword = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcForgotPassword.insets = new Insets(5, 5, 15, 5);
        gbcForgotPassword.gridx = 0;
        gbcForgotPassword.gridy = 7;
        gbcForgotPassword.gridwidth = 2;
        gbcForgotPassword.fill = GridBagConstraints.NONE;
        gbcForgotPassword.anchor = GridBagConstraints.CENTER;
        panel.add(forgotPasswordLabel, gbcForgotPassword);

        // --- Login Button ---
        GridBagConstraints gbcLoginButton = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcLoginButton.insets = new Insets(5, 5, 10, 5); // Khoảng cách dưới nút Login
        gbcLoginButton.gridx = 0;
        gbcLoginButton.gridy = 8;
        gbcLoginButton.gridwidth = 2;
        gbcLoginButton.fill = GridBagConstraints.HORIZONTAL;
        gbcLoginButton.ipady = 10;
        gbcLoginButton.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbcLoginButton);

        // --- Message Label ---
        GridBagConstraints gbcMessageLabel = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcMessageLabel.insets = new Insets(10, 5, 10, 5);
        gbcMessageLabel.gridx = 0;
        gbcMessageLabel.gridy = 9;
        gbcMessageLabel.gridwidth = 2;
        gbcMessageLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcMessageLabel.anchor = GridBagConstraints.CENTER;
        panel.add(messageLabel, gbcMessageLabel);

        // --- Google Login Button ---
        GridBagConstraints gbcGoogleButton = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcGoogleButton.insets = new Insets(20, 5, 20, 5);
        gbcGoogleButton.gridx = 0;
        gbcGoogleButton.gridy = 10;
        gbcGoogleButton.gridwidth = 2;
        gbcGoogleButton.fill = GridBagConstraints.NONE;
        gbcGoogleButton.anchor = GridBagConstraints.CENTER;
        panel.add(googleLoginButton, gbcGoogleButton);

        // --- Don't have an account? Signup ---
        GridBagConstraints gbcSignupLabel = new GridBagConstraints(); // Tạo đối tượng GBC mới
        gbcSignupLabel.insets = new Insets(4, 5, 4, 5); // Đã điều chỉnh insets
        gbcSignupLabel.gridx = 0;
        gbcSignupLabel.gridy = 11;
        gbcSignupLabel.gridwidth = 2;
        gbcSignupLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcSignupLabel.anchor = GridBagConstraints.CENTER;
        panel.add(signupCombinedLabel, gbcSignupLabel);


        add(panel); // Thêm panel vào JFrame
    }

    /**
     * Lấy tên đăng nhập từ trường nhập liệu.
     * @return Tên đăng nhập đã nhập.
     */
    public String getUsername() { // Đã đổi tên getter
        return usernameField.getText();
    }

    /**
     * Lấy mật khẩu từ trường nhập liệu.
     * @return Mật khẩu đã nhập dưới dạng mảng ký tự.
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }

    /**
     * Hiển thị thông báo (lỗi hoặc thành công) trên giao diện.
     * @param message Nội dung thông báo.
     * @param isError True nếu là thông báo lỗi, False nếu là thông báo thành công.
     */
    public void displayMessage(String message, boolean isError) {
        messageLabel.setText(message);
        messageLabel.setForeground(isError ? Color.RED : Color.BLUE);
        if (isError) {
             JOptionPane.showMessageDialog(this, message, "Thông báo lỗi", JOptionPane.ERROR_MESSAGE);
        } else {
             // JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Thiết lập ActionListener cho nút đăng nhập.
     * Điều này cho phép Controller đăng ký để lắng nghe sự kiện từ View.
     * @param listener ActionListener để gán.
     */
    public void addLoginListener(ActionListener listener) {
        loginButton.addActionListener(listener);
    }

    // Các getters cho các nút và nhãn khác nếu cần để Controller thêm listener
    public JLabel getForgotPasswordLabel() {
        return forgotPasswordLabel;
    }

    public JButton getGoogleLoginButton() {
        return googleLoginButton;
    }

    // Trả về JLabel kết hợp để Controller có thể thêm MouseListener vào nó.
    public JLabel getSignupLabel() {
        return signupCombinedLabel;
    }

    /**
     * Lớp tĩnh lồng vào để tạo đường viền bo tròn cho các thành phần Swing.
     * Có thể được sử dụng cho JTextField, JPasswordField, v.v.
     */
    static class RoundedCornerBorder extends AbstractBorder {
        /**
		 * */
		private static final long serialVersionUID = 1L;
		private int radius;
        private Color color;
        private int thickness;

        RoundedCornerBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));

            // Điều chỉnh tọa độ để vẽ bên trong vùng border
            // width và height được giảm đi để vẽ bên trong đường viền
            RoundRectangle2D roundRect = new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                    width - thickness, height - thickness, radius, radius);
            g2.draw(roundRect);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            // Đặt padding để nội dung không bị chồng lấn lên border bo tròn
            return new Insets(thickness + radius / 4, thickness + radius / 4, thickness + radius / 4, thickness + radius / 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.bottom = insets.top = thickness + radius / 4;
            return insets;
        }
    }
}
