package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedRectangle2D
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import models.NhanVien;
import models.TaiKhoanNguoiDung;

/**
 * Lớp EmployeeView tạo giao diện người dùng cho việc quản lý nhân viên.
 * Nó hiển thị bảng nhân viên, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class NhanVienView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable employeeTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu thông tin nhân viên
    private JTextField txtMaNhanVien;
    private JTextField txtHoTen;
    private JTextField txtNgaySinh; // Định dạng YYYY-MM-DD
    private JComboBox<String> cbxGioiTinh;
    private JTextField txtCCCD;
    private JTextField txtSdt;
    private JTextField txtLuong; // Số nguyên
    private JComboBox<String> cbxTrangThaiLamViec;
    private JTextField txtMaNguoiDung; // Mã người dùng liên kết (FK)

    // Các trường cho thông tin tài khoản liên kết (có tùy chọn, như KhachHangView)
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtEmail;
    private JCheckBox chkLinkAccount; // Checkbox để quyết định có liên kết tài khoản không

    // Các nút chức năng
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // --- Color Palette (Consistent with MainDashBoard & KhachHangView) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); // Darker blue accent (from LoginView/KhachHangView)
    private static final Color BORDER_COLOR = new Color(230, 230, 230); // Light gray for borders
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); // Dark gray for primary text

    public NhanVienView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Consistent padding
        setBackground(new Color(245, 248, 252)); // Consistent background color for the panel

        initComponents();
        layoutComponents();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã NV", "Họ Tên", "Ngày Sinh", "Giới Tính", "CCCD", "SĐT", "Lương", "Trạng Thái", "Mã Người Dùng"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add basic styling to table header
        employeeTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        employeeTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); // Dark blue from LoginView
        employeeTable.getTableHeader().setForeground(Color.WHITE);
        employeeTable.setRowHeight(25); // Increase row height for better readability
        employeeTable.setGridColor(BORDER_COLOR); // Light gray grid lines
        employeeTable.setFont(new Font("Arial", Font.PLAIN, 12)); // Consistent font for table data


        // Input fields and labels - Employee Info
        txtMaNhanVien = new JTextField(10);
        txtMaNhanVien.setEditable(false);
        txtMaNhanVien.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtHoTen = new JTextField(20);
        txtHoTen.putClientProperty("JTextField.placeholderText", "Nhập họ tên nhân viên");
        txtHoTen.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtNgaySinh = new JTextField(10);
        txtNgaySinh.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        txtNgaySinh.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        ((JLabel)cbxGioiTinh.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cbxGioiTinh.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtCCCD = new JTextField(15);
        txtCCCD.putClientProperty("JTextField.placeholderText", "Nhập số CCCD (12 chữ số)");
        txtCCCD.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtSdt = new JTextField(15);
        txtSdt.putClientProperty("JTextField.placeholderText", "Nhập số điện thoại (0xxxx)");
        txtSdt.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtLuong = new JTextField(10);
        txtLuong.putClientProperty("JTextField.placeholderText", "Nhập lương (số nguyên)");
        txtLuong.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        cbxTrangThaiLamViec = new JComboBox<>(new String[]{"Hoạt động", "Nghỉ phép", "Đã nghỉ việc"});
        ((JLabel)cbxTrangThaiLamViec.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cbxTrangThaiLamViec.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtMaNguoiDung = new JTextField(10);
        txtMaNguoiDung.setEditable(false);
        txtMaNguoiDung.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        // Input fields and labels - User Account Info (Optional, like KhachHangView)
        txtUsername = new JTextField(15);
        txtUsername.putClientProperty("JTextField.placeholderText", "Tên đăng nhập tài khoản");
        txtUsername.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtPassword = new JPasswordField(15);
        txtPassword.putClientProperty("JTextField.placeholderText", "Mật khẩu tài khoản (>= 6 ký tự)");
        txtPassword.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtEmail = new JTextField(20);
        txtEmail.putClientProperty("JTextField.placeholderText", "Email tài khoản");
        txtEmail.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        chkLinkAccount = new JCheckBox("Liên kết tài khoản mới"); // Added checkbox
        chkLinkAccount.setFont(new Font("Arial", Font.BOLD, 12)); 
        chkLinkAccount.setForeground(DARK_BLUE_ACCENT);
        
        // Listener for checkbox to enable/disable account fields
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

        // Initial state: account fields disabled if no account is linked by default
        txtUsername.setEnabled(false);
        txtPassword.setEnabled(false);
        txtEmail.setEnabled(false);

        // Buttons - Using custom styling
        btnAdd = createStyledButton("Thêm");
        btnUpdate = createStyledButton("Sửa");
        btnDelete = createStyledButton("Xóa");
        btnClear = createStyledButton("Làm mới");
        btnSearch = createStyledButton("Tìm kiếm");

        txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo Tên/SĐT/CCCD");
        txtSearch.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1));

        messageLabel = new JLabel("Sẵn sàng.");
        messageLabel.setForeground(PRIMARY_TEXT_COLOR);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 12)); 
    }

    /**
     * Helper method to create a JButton with consistent styling.
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
        button.setBackground(DARK_BLUE_ACCENT); // Dark blue accent
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        button.setPreferredSize(new Dimension(100, 35)); // Consistent button size
        return button;
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     * Đã điều chỉnh để chia phần nhập liệu thành 2 cột và mở rộng bảng.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Center align search
        searchPanel.setBackground(Color.WHITE); // Consistent background
        searchPanel.add(new JLabel("Tìm kiếm (Tên/SĐT/CCCD):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table (chiếm nhiều không gian hơn)
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Container for input form, buttons, and message
        JPanel southSectionPanel = new JPanel(new BorderLayout(10, 10));
        southSectionPanel.setBackground(Color.WHITE); // Consistent background

        // Input form panel: Using GridBagLayout to create 2 columns
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE); // Consistent background
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true), // Rounded border for title border
            "Thông tin Nhân viên & Tài khoản (tùy chọn)", // Added "(tùy chọn)"
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT // Consistent title font/color
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các ô
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Make columns expand equally

        // Column 1 (Left side: Employee Info)
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã NV (tự động):"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(txtMaNhanVien, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Họ Tên (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(txtHoTen, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Ngày Sinh (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(txtNgaySinh, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Giới Tính:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; inputPanel.add(cbxGioiTinh, gbc);

        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("CCCD (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; inputPanel.add(txtCCCD, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5; inputPanel.add(new JLabel("SĐT (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; inputPanel.add(txtSdt, gbc);

        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(new JLabel("Lương (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 6; inputPanel.add(txtLuong, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7; inputPanel.add(new JLabel("Trạng Thái NV:"), gbc);
        gbc.gridx = 1; gbc.gridy = 7; inputPanel.add(cbxTrangThaiLamViec, gbc);

        // Column 2 (Right side: User Account Info) - Starts from row 0
        gbc.gridx = 2; gbc.gridy = 0; inputPanel.add(new JLabel("Mã Người Dùng (FK):"), gbc); 
        gbc.gridx = 3; gbc.gridy = 0; inputPanel.add(txtMaNguoiDung, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2; inputPanel.add(chkLinkAccount, gbc); // Checkbox spans 2 columns
        gbc.gridwidth = 1; // Reset gridwidth

        gbc.gridx = 2; gbc.gridy = 2; inputPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; inputPanel.add(txtUsername, gbc);

        gbc.gridx = 2; gbc.gridy = 3; inputPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 3; gbc.gridy = 3; inputPanel.add(txtPassword, gbc);

        gbc.gridx = 2; gbc.gridy = 4; inputPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.gridy = 4; inputPanel.add(txtEmail, gbc);

        // Spacer to push components up (gridx=0, gridy=5 in KhachHangView)
        // Here, it would be gridy=8 or higher for column 1, and gridy=5 or higher for column 2
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 4; gbc.weighty = 1.0; 
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
     * Điền dữ liệu vào bảng nhân viên.
     * @param nhanVienList Danh sách các đối tượng NhanVien để hiển thị.
     */
    public void populateTable(List<NhanVien> nhanVienList) {
        tableModel.setRowCount(0); // Clear all existing rows
        for (NhanVien nv : nhanVienList) {
            Vector<Object> row = new Vector<>();
            row.add(nv.getMaNhanVien());
            row.add(nv.getHoTen());
            row.add(nv.getNgaySinh() != null ? dateFormat.format(nv.getNgaySinh()) : "");
            row.add(nv.getGioiTinh());
            row.add(nv.getCccd());
            row.add(nv.getSdt());
            row.add(nv.getLuong());
            row.add(nv.getTrangThaiLamViec());
            row.add(nv.getMaNguoiDung() != null ? nv.getMaNguoiDung() : "N/A");
            tableModel.addRow(row);
        }
    }

    /**
     * Hiển thị thông tin nhân viên lên các trường nhập liệu khi chọn một hàng trên bảng.
     * Khi hiển thị chi tiết của một nhân viên đã tồn tại, các trường tạo tài khoản mới sẽ bị khóa,
     * vì tài khoản đã được liên kết và không thể thay đổi từ đây (chỉ có thể sửa thông tin nhân viên).
     * @param nhanVien Đối tượng NhanVien để hiển thị.
     */
    public void displayNhanVienDetails(NhanVien nhanVien) {
        if (nhanVien != null) {
            txtMaNhanVien.setText(nhanVien.getMaNhanVien());
            txtHoTen.setText(nhanVien.getHoTen());
            txtNgaySinh.setText(nhanVien.getNgaySinh() != null ? dateFormat.format(nhanVien.getNgaySinh()) : "");
            cbxGioiTinh.setSelectedItem(nhanVien.getGioiTinh());
            txtCCCD.setText(nhanVien.getCccd());
            txtSdt.setText(nhanVien.getSdt());
            txtLuong.setText(String.valueOf(nhanVien.getLuong()));
            cbxTrangThaiLamViec.setSelectedItem(nhanVien.getTrangThaiLamViec());
            txtMaNguoiDung.setText(nhanVien.getMaNguoiDung() != null ? nhanVien.getMaNguoiDung() : "");

            // Khi hiển thị chi tiết, các trường tài khoản sẽ bị khóa và xóa trắng,
            // và checkbox sẽ không được chọn
            chkLinkAccount.setSelected(false); // Reset checkbox
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
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng NhanVien.
     * Hàm này chỉ kiểm tra thông tin nhân viên.
     * @return Đối tượng NhanVien với dữ liệu đã nhập, hoặc null nếu validate thất bại.
     */
    public NhanVien getNhanVienFromInput() {
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(txtMaNhanVien.getText().trim()); 
        nv.setHoTen(txtHoTen.getText().trim());
        try {
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                nv.setNgaySinh(dateFormat.parse(txtNgaySinh.getText().trim()));
            } else {
                nv.setNgaySinh(null);
            }
        } catch (ParseException e) {
            displayMessage("Ngày sinh không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        nv.setGioiTinh((String) cbxGioiTinh.getSelectedItem());
        nv.setCccd(txtCCCD.getText().trim());
        nv.setSdt(txtSdt.getText().trim());
        try {
            if (txtLuong.getText().trim().isEmpty()) {
                displayMessage("Lương không được để trống.", true);
                return null;
            }
            int luong = Integer.parseInt(txtLuong.getText().trim());
            if (luong < 0) {
                displayMessage("Lương không thể là số âm.", true);
                return null;
            }
            nv.setLuong(luong);
        } catch (NumberFormatException e) {
            displayMessage("Lương không hợp lệ (phải là số nguyên).", true);
            return null;
        }
        nv.setTrangThaiLamViec((String) cbxTrangThaiLamViec.getSelectedItem());
        nv.setMaNguoiDung(txtMaNguoiDung.getText().trim()); 

        if (nv.getHoTen().isEmpty() || nv.getCccd().isEmpty() || nv.getSdt().isEmpty()) {
            displayMessage("Họ tên, CCCD và SĐT của nhân viên không được để trống.", true);
            return null;
        }
        
        if (!nv.getCccd().matches("\\d{12}")) {
            displayMessage("CCCD không hợp lệ (phải là 12 chữ số).", true);
            return null;
        }
        if (!nv.getSdt().matches("0\\d{9,10}")) {
            displayMessage("Số điện thoại không hợp lệ (phải bắt đầu bằng 0 và có 10 hoặc 11 chữ số).", true);
            return null;
        }
        
        return nv;
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng TaiKhoanNguoiDung.
     * Chỉ được gọi khi checkbox liên kết tài khoản được chọn (tức là khi thêm mới và checkbox được bật).
     * @return Đối tượng TaiKhoanNguoiDung với dữ liệu đã nhập, hoặc null nếu validate thất bại.
     */
    public TaiKhoanNguoiDung getTaiKhoanNguoiDungFromInput() {
        // Only create TaiKhoanNguoiDung if the checkbox is selected
        if (!chkLinkAccount.isSelected()) {
            return null;
        }

        TaiKhoanNguoiDung tk = new TaiKhoanNguoiDung();
        tk.setUsername(txtUsername.getText().trim());
        tk.setPassword(new String(txtPassword.getPassword()).trim());
        tk.setEmail(txtEmail.getText().trim());
        tk.setLoaiNguoiDung("Nhân viên"); // Mặc định là Nhân viên
        tk.setTrangThaiTaiKhoan("Hoạt động"); 

        if (tk.getUsername().isEmpty() || tk.getPassword().isEmpty() || tk.getEmail().isEmpty()) {
            displayMessage("Tên đăng nhập, Mật khẩu và Email của tài khoản là bắt buộc khi liên kết tài khoản.", true); // Updated message
            return null;
        }
        
        if (!tk.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            displayMessage("Email không hợp lệ.", true);
            return null;
        }

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
        txtMaNhanVien.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        cbxGioiTinh.setSelectedIndex(0);
        txtCCCD.setText("");
        txtSdt.setText("");
        txtLuong.setText("");
        cbxTrangThaiLamViec.setSelectedIndex(0);
        txtMaNguoiDung.setText("");
        
        // Reset checkbox and user account fields
        chkLinkAccount.setSelected(false); // Reset checkbox
        txtUsername.setText("");
        txtPassword.setText("");
        txtEmail.setText("");
        txtUsername.setEnabled(false); // Disable by default
        txtPassword.setEnabled(false); 
        txtEmail.setEnabled(false);

        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(PRIMARY_TEXT_COLOR);
        
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

    public JTable getEmployeeTable() {
        return employeeTable;
    }

    /**
     * Lớp tĩnh lồng vào để tạo đường viền bo tròn cho các thành phần Swing.
     * Có thể được sử dụng cho JTextField, JPasswordField, v.v.
     */
    static class RoundedCornerBorder extends AbstractBorder {
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

            RoundRectangle2D roundRect = new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0,
                    width - thickness, height - thickness, radius, radius);
            g2.draw(roundRect);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + radius / 4, thickness + radius / 4, thickness + radius / 4, thickness + radius / 4);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.right = insets.bottom = insets.top = thickness + radius / 4;
            return insets;
        }
    }
}
