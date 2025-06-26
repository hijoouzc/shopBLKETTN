package view;

import models.SanPham;
import models.LoaiSanPham; // Import LoaiSanPham để đổ dữ liệu vào JComboBox

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.EmptyBorder; // For EmptyBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedRectangle2D
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * Lớp SanPhamView tạo giao diện người dùng cho việc quản lý sản phẩm.
 * Nó hiển thị bảng sản phẩm, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class SanPhamView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable productTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    private JTextField txtMaSanPham;
    private JTextField txtTenSanPham;
    private JTextField txtDonGia;
    private JTextField txtNgaySanXuat; // Định dạng YYYY-MM-DD
    private JTextArea txtThongSoKyThuat; // Could be JSON string or simple text
    private JComboBox<String> cbxMaLoaiSanPham; // For selecting product category
    private JLabel lblSoLuongTon; // Thêm JLabel để hiển thị số lượng tồn

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // --- Color Palette (Consistent with other views) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); 
    private static final Color BORDER_COLOR = new Color(230, 230, 230); 
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); 
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252); // Consistent background

    public SanPhamView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BACKGROUND_COLOR); // Set background for the panel

        initComponents();
        layoutComponents();
        clearInputFields(); // Set initial button states
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã SP", "Tên SP", "Đơn Giá", "Ngày SX", "Thông Số KT", "Mã Loại SP", "Tồn Kho"}; 
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        productTable = new JTable(tableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Table styling
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        productTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); 
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.setRowHeight(25);
        productTable.setGridColor(BORDER_COLOR); 
        productTable.setFont(new Font("Arial", Font.PLAIN, 12));

        // Input fields and labels
        txtMaSanPham = new JTextField(10);
        txtMaSanPham.setEditable(false); // Mã sản phẩm tự động, không cho sửa
        txtMaSanPham.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        txtTenSanPham = new JTextField(20);
        txtTenSanPham.putClientProperty("JTextField.placeholderText", "Nhập tên sản phẩm");
        txtTenSanPham.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        txtDonGia = new JTextField(10);
        txtDonGia.putClientProperty("JTextField.placeholderText", "Nhập đơn giá (số nguyên)");
        txtDonGia.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        txtNgaySanXuat = new JTextField(10);
        txtNgaySanXuat.putClientProperty("JTextField.placeholderText", "YYYY-MM-DD");
        txtNgaySanXuat.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        txtThongSoKyThuat = new JTextArea(3, 20); // 3 hàng, 20 cột
        txtThongSoKyThuat.setLineWrap(true);
        txtThongSoKyThuat.setWrapStyleWord(true);
        txtThongSoKyThuat.putClientProperty("JTextArea.placeholderText", "Nhập thông số kỹ thuật");
        // Wrap JTextArea in a JScrollPane and apply border to the scrollpane
        // JScrollPane scrollThongSo = new JScrollPane(txtThongSoKyThuat); // Moved to layoutComponents to apply border
        // scrollThongSo.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Apply border to scroll pane
        // scrollThongSo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);


        cbxMaLoaiSanPham = new JComboBox<>(); // Sẽ được điền dữ liệu từ Controller
        ((JLabel)cbxMaLoaiSanPham.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align items
        cbxMaLoaiSanPham.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        lblSoLuongTon = new JLabel("Tồn Kho: 0"); // Khởi tạo nhãn tồn kho
        lblSoLuongTon.setFont(new Font("Arial", Font.BOLD, 12));
        lblSoLuongTon.setForeground(PRIMARY_TEXT_COLOR);

        // Buttons - Using custom styling
        btnAdd = createStyledButton("Thêm");
        btnUpdate = createStyledButton("Sửa");
        btnDelete = createStyledButton("Xóa");
        btnClear = createStyledButton("Làm mới");
        btnSearch = createStyledButton("Tìm kiếm");

        txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo Tên SP");
        txtSearch.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

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
        button.setBackground(DARK_BLUE_ACCENT); 
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        button.setPreferredSize(new Dimension(100, 35)); // Consistent button size
        return button;
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Center align search
        searchPanel.setBackground(Color.WHITE); // Consistent background
        searchPanel.add(new JLabel("Tìm kiếm (Tên SP):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Create a JSplitPane for left (table) and right (details/controls)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerSize(8); // Thicker divider
        splitPane.setContinuousLayout(true); // Smooth resizing

        // Left side: Product Table
        JScrollPane scrollPane = new JScrollPane(productTable);
        splitPane.setLeftComponent(scrollPane);

        // Right side: Details Panel (contains input form, buttons, message)
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(BACKGROUND_COLOR); // Background for the details panel

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE); // Consistent background
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thicker border
            "Thông tin sản phẩm",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 

        // Row 0: Mã SP
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã SP (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaSanPham, gbc);

        // Row 1: Tên SP
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Tên SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtTenSanPham, gbc);

        // Row 2: Đơn Giá
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Đơn Giá:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtDonGia, gbc);

        // Row 3: Ngày Sản Xuất
        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Ngày SX (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtNgaySanXuat, gbc);

        // Row 4: Thông Số Kỹ Thuật (wrapped in JScrollPane)
        gbc.gridx = 0; gbc.gridy = 4; inputPanel.add(new JLabel("Thông Số KT:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2; // Allow JTextArea to take more vertical space
        JScrollPane scrollThongSo = new JScrollPane(txtThongSoKyThuat); 
        scrollThongSo.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Apply border to scroll pane
        inputPanel.add(scrollThongSo, gbc); 
        gbc.gridheight = 1; // Reset gridheight

        // Row 5 (effective row 6 due to JTextArea span): Mã Loại Sản Phẩm
        gbc.gridx = 0; gbc.gridy = 6; inputPanel.add(new JLabel("Loại SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(cbxMaLoaiSanPham, gbc);
        
        // Row 6 (effective row 7): Tồn Kho (display only)
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; inputPanel.add(lblSoLuongTon, gbc); 
        gbc.gridwidth = 1; // Reset gridwidth

        // Spacer to push components up
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2; gbc.weighty = 1.0; 
        inputPanel.add(new JLabel(""), gbc); 

        detailsPanel.add(inputPanel, BorderLayout.NORTH); // Input panel at the top of detailsPanel

        // Panel chứa các nút và nhãn thông báo
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
        
        detailsPanel.add(buttonAndMessagePanel, BorderLayout.CENTER); // buttonAndMessagePanel in the center of detailsPanel

        splitPane.setRightComponent(detailsPanel);
        add(splitPane, BorderLayout.CENTER); // Add the split pane to the main view
    }

    /**
     * Điền dữ liệu vào bảng sản phẩm.
     * @param sanPhamList Danh sách các đối tượng SanPham để hiển thị.
     */
    public void populateTable(List<SanPham> sanPhamList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (SanPham sp : sanPhamList) {
            Vector<Object> row = new Vector<>();
            row.add(sp.getMaSanPham());
            row.add(sp.getTenSanPham());
            row.add(sp.getDonGia());
            row.add(sp.getNgaySanXuat() != null ? dateFormat.format(sp.getNgaySanXuat()) : "");
            row.add(sp.getThongSoKyThuat());
            row.add(sp.getMaLoaiSanPham());
            row.add(sp.getSoLuongTon()); // Thêm cột tồn kho
            tableModel.addRow(row);
        }
    }

    /**
     * Điền dữ liệu vào ComboBox loại sản phẩm.
     * @param loaiSanPhamList Danh sách các đối tượng LoaiSanPham.
     */
    public void populateLoaiSanPhamComboBox(List<LoaiSanPham> loaiSanPhamList) {
        cbxMaLoaiSanPham.removeAllItems();
        for (LoaiSanPham lsp : loaiSanPhamList) {
            cbxMaLoaiSanPham.addItem(lsp.getMaLoaiSanPham() + " - " + lsp.getTenLoaiSanPham());
        }
        if (cbxMaLoaiSanPham.getItemCount() > 0) {
            cbxMaLoaiSanPham.setSelectedIndex(0);
        }
    }

    /**
     * Hiển thị thông tin sản phẩm lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param sanPham Đối tượng SanPham để hiển thị.
     */
    public void displaySanPhamDetails(SanPham sanPham) {
        if (sanPham != null) {
            txtMaSanPham.setText(sanPham.getMaSanPham());
            txtTenSanPham.setText(sanPham.getTenSanPham());
            txtDonGia.setText(String.valueOf(sanPham.getDonGia())); // Convert int to String
            txtNgaySanXuat.setText(sanPham.getNgaySanXuat() != null ? dateFormat.format(sanPham.getNgaySanXuat()) : "");
            txtThongSoKyThuat.setText(sanPham.getThongSoKyThuat());
            
            // Hiển thị số lượng tồn kho
            lblSoLuongTon.setText("Tồn Kho: " + sanPham.getSoLuongTon());
            
            // Chọn đúng item trong combobox
            for (int i = 0; i < cbxMaLoaiSanPham.getItemCount(); i++) {
                if (cbxMaLoaiSanPham.getItemAt(i).startsWith(sanPham.getMaLoaiSanPham())) {
                    cbxMaLoaiSanPham.setSelectedIndex(i);
                    break;
                }
            }
            btnAdd.setEnabled(false); // Disable add when a row is selected
            btnUpdate.setEnabled(true); // Enable update
            btnDelete.setEnabled(true); // Enable delete
        } else {
            clearInputFields();
            // Buttons will be set by clearInputFields
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng SanPham.
     * @return Đối tượng SanPham với dữ liệu đã nhập, hoặc null nếu validate thất bại.
     */
    public SanPham getSanPhamFromInput() {
        SanPham sp = new SanPham();
        sp.setMaSanPham(txtMaSanPham.getText().trim()); // Có thể là rỗng nếu đang thêm mới
        sp.setTenSanPham(txtTenSanPham.getText().trim());
        try {
            if (txtDonGia.getText().trim().isEmpty()) {
                displayMessage("Đơn giá không được để trống.", true);
                return null;
            }
            int donGia = Integer.parseInt(txtDonGia.getText().trim()); // Convert String to int
            if (donGia <= 0) {
                displayMessage("Đơn giá phải lớn hơn 0.", true);
                return null;
            }
            sp.setDonGia(donGia);
        } catch (NumberFormatException e) {
            displayMessage("Đơn giá không hợp lệ (phải là số nguyên).", true);
            return null;
        }
        try {
            if (!txtNgaySanXuat.getText().trim().isEmpty()) {
                sp.setNgaySanXuat(dateFormat.parse(txtNgaySanXuat.getText().trim()));
            } else {
                sp.setNgaySanXuat(null);
            }
        } catch (ParseException e) {
            displayMessage("Ngày sản xuất không hợp lệ (YYYY-MM-DD).", true);
            return null;
        }
        sp.setThongSoKyThuat(txtThongSoKyThuat.getText().trim());
        
        // Lấy MaLoaiSanPham từ combobox
        String selectedLoaiSP = (String) cbxMaLoaiSanPham.getSelectedItem();
        if (selectedLoaiSP != null && !selectedLoaiSP.isEmpty()) {
            sp.setMaLoaiSanPham(selectedLoaiSP.split(" - ")[0]); // Chỉ lấy phần mã
        } else {
            displayMessage("Vui lòng chọn loại sản phẩm.", true);
            return null;
        }

        // soLuongTon không được nhập từ đây, nó được tính toán từ CSDL
        // sp.setSoLuongTon(...) // KHÔNG đặt giá trị từ UI

        // Kiểm tra các trường bắt buộc
        if (sp.getTenSanPham().isEmpty()) {
            displayMessage("Tên sản phẩm không được để trống.", true);
            return null;
        }
        
        return sp;
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaSanPham.setText("");
        txtTenSanPham.setText("");
        txtDonGia.setText("");
        txtNgaySanXuat.setText("");
        txtThongSoKyThuat.setText("");
        if (cbxMaLoaiSanPham.getItemCount() > 0) {
            cbxMaLoaiSanPham.setSelectedIndex(0);
        }
        lblSoLuongTon.setText("Tồn Kho: 0"); // Reset nhãn tồn kho
        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(PRIMARY_TEXT_COLOR);

        btnAdd.setEnabled(true); // Enable add
        btnUpdate.setEnabled(false); // Disable update
        btnDelete.setEnabled(false); // Disable delete
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
             // JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE); // Removed for consistency
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

    public JTable getProductTable() {
        return productTable;
    }

    // Thêm các getters cho các trường input để Controller có thể đọc dữ liệu
    public JTextField getTxtMaSanPham() {
        return txtMaSanPham;
    }

    public JTextField getTxtTenSanPham() {
        return txtTenSanPham;
    }

    public JTextField getTxtDonGia() {
        return txtDonGia;
    }

    public JTextField getTxtNgaySanXuat() {
        return txtNgaySanXuat;
    }

    public JTextArea getTxtThongSoKyThuat() {
        return txtThongSoKyThuat;
    }

    public JComboBox<String> getCbxMaLoaiSanPham() {
        return cbxMaLoaiSanPham;
    }

    /**
     * Lớp tĩnh lồng vào để tạo đường viền bo tròn cho các thành phần Swing.
     * Có thể được sử dụng cho JTextField, JPasswordField, JComboBox, JTextArea, v.v.
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
