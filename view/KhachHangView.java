package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // Import AbstractBorder
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import models.KhachHang; // Import KhachHang model
import models.TaiKhoanNguoiDung; // Import TaiKhoanNguoiDung model
import java.net.URL; // For loading images

/**
 * Lớp KhachHangView tạo giao diện người dùng cho việc quản lý khách hàng.
 * Nó hiển thị bảng khách hàng, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class KhachHangView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable customerTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu thông tin khách hàng
    JTextField txtMaKhachHang;
    JTextField txtHoTen;
    JTextField txtNgaySinh; // Định dạng YYYY-MM-DD
    JComboBox<String> cbxGioiTinh;
    JTextField txtSdt;
    public JTextField txtMaNguoiDung; // Mã người dùng liên kết (FK)

    // Các trường cho thông tin tài khoản liên kết (có tùy chọn)
    JTextField txtUsername;
    JPasswordField txtPassword;
    JTextField txtEmail; // Thêm trường Email
    JCheckBox chkLinkAccount; // Checkbox để quyết định có liên kết tài khoản không

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public KhachHangView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Consistent padding

        initComponents();
        layoutComponents();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã KH", "Họ Tên", "Ngày Sinh", "Giới Tính", "SĐT", "Mã Người Dùng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add basic styling to table header
        customerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        customerTable.getTableHeader().setBackground(new Color(25, 69, 137));
        customerTable.getTableHeader().setForeground(Color.WHITE);
        customerTable.setRowHeight(25); // Increase row height for better readability


        // Input fields and labels - Customer Info
        txtMaKhachHang = new JTextField(10);
        txtMaKhachHang.setEditable(false); // Mã khách hàng tự động, không cho sửa
        txtMaKhachHang.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtHoTen = new JTextField(20);
        txtHoTen.putClientProperty("JTextField.placeholderText", "Nhập họ tên");
        txtHoTen.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtNgaySinh = new JTextField(10);
        txtNgaySinh.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        txtNgaySinh.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        // Basic styling for JComboBox
        ((JLabel)cbxGioiTinh.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cbxGioiTinh.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtSdt = new JTextField(15);
        txtSdt.putClientProperty("JTextField.placeholderText", "Nhập số điện thoại");
        txtSdt.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtMaNguoiDung = new JTextField(10);
        txtMaNguoiDung.setEditable(false); // Mã người dùng tự động, không cho sửa
        txtMaNguoiDung.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        // Input fields and labels - User Account Info (Optional)
        txtUsername = new JTextField(15);
        txtUsername.putClientProperty("JTextField.placeholderText", "Tên đăng nhập mới");
        txtUsername.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JTextField.placeholderText", "Mật khẩu mới");
        txtPassword.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        txtEmail = new JTextField(20); // Initialize new Email field
        txtEmail.putClientProperty("JTextField.placeholderText", "Email tài khoản");
        txtEmail.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1)); 

        chkLinkAccount = new JCheckBox("Liên kết tài khoản mới");
        chkLinkAccount.setFont(new Font("Arial", Font.BOLD, 12)); // Make it bold for visibility
        chkLinkAccount.setForeground(new Color(25, 69, 137));
        
        // Listener cho checkbox để bật/tắt các trường tài khoản
        chkLinkAccount.addActionListener(_ -> {
            boolean enabled = chkLinkAccount.isSelected();
            txtUsername.setEnabled(enabled);
            txtPassword.setEnabled(enabled);
            txtEmail.setEnabled(enabled);
            if (!enabled) { // Clear fields if disabled
                txtUsername.setText("");
                txtPassword.setText("");
                txtEmail.setText("");
            }
        });

        // Khởi tạo trạng thái ban đầu: các trường tài khoản bị vô hiệu hóa
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtEmail.setEnabled(false);

        // Buttons - Using custom styling from LoginView
        btnAdd = createStyledButton("Thêm");
        btnUpdate = createStyledButton("Sửa");
        btnDelete = createStyledButton("Xóa");
        btnClear = createStyledButton("Làm mới");
        btnSearch = createStyledButton("Tìm kiếm");

        txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập từ khóa tìm kiếm");
        txtSearch.setBorder(new RoundedCornerBorder(10, Color.LIGHT_GRAY, 1));

        messageLabel = new JLabel("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Consistent font style
    }

    /**
     * Helper method to create a JButton with LoginView's styling.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) { /* No default border */ }
        };
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBackground(new Color(25, 69, 137)); // Dark blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Slightly smaller font for consistency
        button.setPreferredSize(new Dimension(100, 35)); // Consistent button size
        return button;
    }

    /**
     * Sắp xếp các thành phần UI.
     * Đã điều chỉnh để chia phần nhập liệu thành 2 cột và mở rộng bảng.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Center align search
        searchPanel.setBackground(Color.WHITE); // Consistent background
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table (chiếm nhiều không gian hơn)
        JScrollPane scrollPane = new JScrollPane(customerTable);
        // Removed setPreferredSize for better responsiveness with BorderLayout.CENTER
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Container for input form, buttons, and message
        JPanel southSectionPanel = new JPanel(new BorderLayout(10, 10));
        southSectionPanel.setBackground(Color.WHITE); // Consistent background

        // Input form panel: Using GridBagLayout to create 2 columns
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE); // Consistent background
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true), // Rounded border for title border
            "Thông tin Khách hàng & Tài khoản (tùy chọn)",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), new Color(25, 69, 137) // Consistent title font/color
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Make columns expand equally

        // Column 1 (Left side: Customer Info)
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã KH (tự động):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(txtMaKhachHang, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Họ Tên (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(txtHoTen, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ngày Sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(txtNgaySinh, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Giới Tính:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(cbxGioiTinh, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("SĐT (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; inputPanel.add(txtSdt, gbc);
        
        // Column 2 (Right side: User Account Info)
        gbc.gridx = 2; gbc.gridy = 0; inputPanel.add(new JLabel("Mã Người Dùng (FK):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0; inputPanel.add(txtMaNguoiDung, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; inputPanel.add(chkLinkAccount, gbc); // Checkbox chiếm 2 cột
        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 2; gbc.gridy = 2; inputPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; inputPanel.add(txtUsername, gbc);

        gbc.gridx = 2; gbc.gridy = 3; inputPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; inputPanel.add(txtPassword, gbc);

        gbc.gridx = 2; gbc.gridy = 4; inputPanel.add(new JLabel("Email:"), gbc); // New Email field
        gbc.gridx = 3; gbc.gridy = 4; inputPanel.add(txtEmail, gbc);
        
        // Spacer for visual separation and pushing components up
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4; gbc.weighty = 1.0; 
        inputPanel.add(new JLabel(""), gbc); // Empty component to push content up

        southSectionPanel.add(inputPanel, BorderLayout.NORTH);

        // Panel containing buttons and message label
        JPanel buttonAndMessagePanel = new JPanel(new BorderLayout(0, 5));
        buttonAndMessagePanel.setBackground(Color.WHITE); // Consistent background
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE); // Consistent background
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        buttonAndMessagePanel.add(buttonPanel, BorderLayout.NORTH);

        // Message label at the bottom of buttonAndMessagePanel
        buttonAndMessagePanel.add(messageLabel, BorderLayout.SOUTH);
        
        southSectionPanel.add(buttonAndMessagePanel, BorderLayout.CENTER);

        add(southSectionPanel, BorderLayout.SOUTH);
    }

    /**
     * Điền dữ liệu vào bảng khách hàng.
     * @param khachHangList Danh sách các đối tượng KhachHang để hiển thị.
     */
    public void populateTable(List<KhachHang> khachHangList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (KhachHang kh : khachHangList) {
            Vector<Object> row = new Vector<>();
            row.add(kh.getMaKhachHang());
            row.add(kh.getHoTen());
            row.add(kh.getNgaySinh() != null ? dateFormat.format(kh.getNgaySinh()) : "");
            row.add(kh.getGioiTinh());
            row.add(kh.getSdt());
            row.add(kh.getMaNguoiDung() != null ? kh.getMaNguoiDung() : "N/A");
            tableModel.addRow(row);
        }
    }

    /**
     * Hiển thị thông tin khách hàng lên các trường nhập liệu khi chọn một hàng trên bảng.
     * Khi hiển thị chi tiết của một khách hàng đã tồn tại, các trường tạo tài khoản mới sẽ bị khóa,
     * vì tài khoản đã được liên kết và không thể thay đổi từ đây.
     * @param khachHang Đối tượng KhachHang để hiển thị.
     */
    public void displayKhachHangDetails(KhachHang khachHang) {
        if (khachHang != null) {
            txtMaKhachHang.setText(khachHang.getMaKhachHang());
            txtHoTen.setText(khachHang.getHoTen());
            txtNgaySinh.setText(khachHang.getNgaySinh() != null ? dateFormat.format(khachHang.getNgaySinh()) : "");
            cbxGioiTinh.setSelectedItem(khachHang.getGioiTinh());
            txtSdt.setText(khachHang.getSdt());
            txtMaNguoiDung.setText(khachHang.getMaNguoiDung() != null ? khachHang.getMaNguoiDung() : "");

            // Khi hiển thị chi tiết, không cho phép liên kết tài khoản mới
            chkLinkAccount.setSelected(false);
            txtUsername.setText("");
            txtPassword.setText("");
            txtEmail.setText("");
            txtUsername.setEnabled(false);
            txtPassword.setEnabled(false);
            txtEmail.setEnabled(false);
            btnAdd.setEnabled(false); // Không cho phép thêm khi đang chọn 1 dòng
            btnUpdate.setEnabled(true); // Cho phép sửa
            btnDelete.setEnabled(true); // Cho phép xóa
        } else {
            clearInputFields();
            btnAdd.setEnabled(true); // Cho phép thêm khi không chọn dòng nào
            btnUpdate.setEnabled(false); // Vô hiệu hóa sửa
            btnDelete.setEnabled(false); // Vô hiệu hóa xóa
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng KhachHang.
     * Hàm này chỉ kiểm tra thông tin khách hàng.
     * @return Đối tượng KhachHang với dữ liệu đã nhập, hoặc null nếu validate thất bại.
     */
    public KhachHang getKhachHangFromInput() {
        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(txtMaKhachHang.getText().trim()); 
        kh.setHoTen(txtHoTen.getText().trim());
        try {
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                kh.setNgaySinh(dateFormat.parse(txtNgaySinh.getText().trim()));
            } else {
                kh.setNgaySinh(null);
            }
        } catch (ParseException e) {
            displayMessage("Ngày sinh không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        kh.setGioiTinh((String) cbxGioiTinh.getSelectedItem());
        kh.setSdt(txtSdt.getText().trim());
        // MaNguoiDung sẽ được thiết lập bởi controller khi có tài khoản liên kết
        kh.setMaNguoiDung(txtMaNguoiDung.getText().trim());

        // Kiểm tra các trường bắt buộc
        if (kh.getHoTen().isEmpty() || kh.getSdt().isEmpty()) {
            displayMessage("Họ tên và Số điện thoại không được để trống.", true);
            return null;
        }
        
        // Kiểm tra định dạng số điện thoại (bắt đầu bằng 0, 10 hoặc 11 chữ số)
        if (!kh.getSdt().matches("0\\d{9,10}")) { 
            displayMessage("Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10-11 chữ số).", true);
            return null;
        }

        return kh;
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng TaiKhoanNguoiDung.
     * Chỉ được gọi khi checkbox liên kết tài khoản được chọn.
     * @return Đối tượng TaiKhoanNguoiDung với dữ liệu đã nhập, hoặc null nếu validate thất bại.
     */
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungFromInput() {
        // Chỉ tạo TaiKhoanNguoiDung nếu checkbox được chọn
        if (!chkLinkAccount.isSelected()) {
            return null;
        }

        TaiKhoanNguoiDung tk = new TaiKhoanNguoiDung();
        tk.setUsername(txtUsername.getText().trim());
        tk.setPassword(new String(txtPassword.getPassword()).trim());
        tk.setEmail(txtEmail.getText().trim());
        // Mặc định cho khách hàng mới
        tk.setLoaiNguoiDung("Khách hàng"); 
        tk.setTrangThaiTaiKhoan("Hoạt động"); 

        // Validate basic TaiKhoanNguoiDung fields
        if (tk.getUsername().isEmpty() || tk.getPassword().isEmpty() || tk.getEmail().isEmpty()) {
            displayMessage("Tên đăng nhập, Mật khẩu và Email của tài khoản là bắt buộc khi liên kết tài khoản.", true);
            return null;
        }
        
        // Basic email format validation
        if (!tk.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            displayMessage("Email không hợp lệ.", true);
            return null;
        }

        // Validate password length (example: min 6 characters)
        if (tk.getPassword().length() < 6) {
            displayMessage("Mật khẩu phải có ít nhất 6 ký tự.", true);
            return null;
        }

        return tk;
    }

    /**
     * Kiểm tra xem checkbox liên kết tài khoản có được chọn không.
     * @return true nếu được chọn, false nếu không.
     */
    public boolean isLinkAccountSelected() {
        return chkLinkAccount.isSelected();
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaKhachHang.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        cbxGioiTinh.setSelectedIndex(0);
        txtSdt.setText("");
        txtMaNguoiDung.setText("");
        
        // Reset checkbox và các trường tài khoản
        chkLinkAccount.setSelected(false); 
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtUsername.setEnabled(false); 
        txtPassword.setEnabled(false); 
        txtEmail.setEnabled(false);

        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(Color.BLACK);

        btnAdd.setEnabled(true); // Cho phép thêm
        btnUpdate.setEnabled(false); // Vô hiệu hóa sửa
        btnDelete.setEnabled(false); // Vô hiệu hóa xóa
    }

    /**
     * Lấy văn bản từ trường tìm kiếm.
     * @return Chuỗi tìm kiếm.
     */
    public String getSearchText() {
        return txtSearch.getText();
    }

    /**
     * Hiển thị thông báo trên giao diện.
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

    // --- Add ActionListeners for Controller to register ---
    public void addAddButtonListener(ActionListener listener) {
        btnAdd.addActionListener(listener);
    }

    public void addUpdateButtonListener(ActionListener listener) {
        btnUpdate.addActionListener(listener);
    }

    public void addDeleteButtonListener(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    public void addClearButtonListener(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    public void addSearchButtonListener(ActionListener listener) {
        btnSearch.addActionListener(listener);
    }

    public JTable getCustomerTable() {
        return customerTable;
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
