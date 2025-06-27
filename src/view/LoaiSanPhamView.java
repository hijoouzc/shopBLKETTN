package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.EmptyBorder; // For EmptyBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedRectangle2D
import java.util.List;
import java.util.Vector;
import models.LoaiSanPham; // Import LoaiSanPham model

/**
 * Lớp LoaiSanPhamView tạo giao diện người dùng cho việc quản lý loại sản phẩm.
 * Nó hiển thị bảng loại sản phẩm, các trường nhập liệu và nút chức năng.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class LoaiSanPhamView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTable loaiSanPhamTable;
    private DefaultTableModel tableModel;

    // Các trường nhập liệu
    public JTextField txtMaLoaiSanPham;
    private JTextField txtTenLoaiSanPham;
    private JTextArea txtMoTa;

    // Các nút
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnSearch;
    private JTextField txtSearch;

    private JLabel messageLabel; // Để hiển thị thông báo lỗi/thành công

    // --- Color Palette (Consistent with other views) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); 
    private static final Color BORDER_COLOR = new Color(230, 230, 230); 
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); 
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252); // Consistent background

    public LoaiSanPhamView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Consistent padding
        setBackground(BACKGROUND_COLOR); // Set background for the panel

        initComponents();
        layoutComponents();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Table setup
        String[] columnNames = {"Mã Loại SP", "Tên Loại SP", "Mô Tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        loaiSanPhamTable = new JTable(tableModel);
        loaiSanPhamTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add basic styling to table header
        loaiSanPhamTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        loaiSanPhamTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); 
        loaiSanPhamTable.getTableHeader().setForeground(Color.WHITE);
        loaiSanPhamTable.setRowHeight(25); // Increase row height for better readability
        loaiSanPhamTable.setGridColor(BORDER_COLOR); // Light gray grid lines
        loaiSanPhamTable.setFont(new Font("Arial", Font.PLAIN, 12)); // Consistent font for table data


        // Input fields and labels
        txtMaLoaiSanPham = new JTextField(10);
        txtMaLoaiSanPham.setEditable(false); // Mã loại sản phẩm tự động, không cho sửa
        txtMaLoaiSanPham.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtTenLoaiSanPham = new JTextField(20);
        txtTenLoaiSanPham.putClientProperty("JTextField.placeholderText", "Nhập tên loại sản phẩm");
        txtTenLoaiSanPham.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 

        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.putClientProperty("JTextArea.placeholderText", "Nhập mô tả loại sản phẩm");
        txtMoTa.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); 
        // JScrollPane is needed for JTextArea with border
        JScrollPane moTaScrollPane = new JScrollPane(txtMoTa);
        moTaScrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default scroll pane border

        // Buttons - Using custom styling
        btnAdd = createStyledButton("Thêm");
        btnUpdate = createStyledButton("Sửa");
        btnDelete = createStyledButton("Xóa");
        btnClear = createStyledButton("Làm mới");
        btnSearch = createStyledButton("Tìm kiếm");

        txtSearch = new JTextField(15);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm theo Tên loại SP");
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
        button.setBackground(DARK_BLUE_ACCENT); 
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        button.setPreferredSize(new Dimension(100, 35)); 
        return button;
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // North Panel: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); 
        searchPanel.setBackground(Color.WHITE); 
        searchPanel.add(new JLabel("Tìm kiếm (Tên loại SP):"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        add(searchPanel, BorderLayout.NORTH);

        // Center Panel: Table
        JScrollPane scrollPane = new JScrollPane(loaiSanPhamTable);
        add(scrollPane, BorderLayout.CENTER);

        // South Section: Input form and buttons
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE); // Consistent background

        // Input form panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE); // Consistent background
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1, true), 
            "Thông tin loại sản phẩm",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 

        // Row 0: Mã Loại SP (tự động)
        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Mã Loại SP (tự động):"), gbc);
        gbc.gridx = 1; inputPanel.add(txtMaLoaiSanPham, gbc);

        // Row 1: Tên Loại SP
        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Tên Loại SP:"), gbc);
        gbc.gridx = 1; inputPanel.add(txtTenLoaiSanPham, gbc);

        // Row 2: Mô Tả
        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Mô Tả:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2; gbc.fill = GridBagConstraints.BOTH; // Allow textarea to expand
        JScrollPane moTaScrollPane = new JScrollPane(txtMoTa); // Wrap JTextArea in a JScrollPane
        moTaScrollPane.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 1)); // Apply border to scrollpane
        inputPanel.add(moTaScrollPane, gbc);
        gbc.gridheight = 1; // Reset gridheight

        // Spacer to push components up
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.weighty = 1.0; 
        inputPanel.add(new JLabel(""), gbc); 

        southPanel.add(inputPanel, BorderLayout.NORTH);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(Color.WHITE); 
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        southPanel.add(buttonPanel, BorderLayout.CENTER); 
        southPanel.add(messageLabel, BorderLayout.SOUTH); 

        add(southPanel, BorderLayout.SOUTH);
    }

    /**
     * Điền dữ liệu vào bảng loại sản phẩm.
     * @param loaiSanPhamList Danh sách các đối tượng LoaiSanPham để hiển thị.
     */
    public void populateTable(List<LoaiSanPham> loaiSanPhamList) {
        tableModel.setRowCount(0); // Xóa tất cả các hàng hiện có
        for (LoaiSanPham lsp : loaiSanPhamList) {
            Vector<Object> row = new Vector<>();
            row.add(lsp.getMaLoaiSanPham());
            row.add(lsp.getTenLoaiSanPham());
            row.add(lsp.getMoTa());
            tableModel.addRow(row);
        }
    }

    /**
     * Hiển thị thông tin loại sản phẩm lên các trường nhập liệu khi chọn một hàng trên bảng.
     * @param loaiSanPham Đối tượng LoaiSanPham để hiển thị.
     */
    public void displayLoaiSanPhamDetails(LoaiSanPham loaiSanPham) {
        if (loaiSanPham != null) {
            txtMaLoaiSanPham.setText(loaiSanPham.getMaLoaiSanPham());
            txtTenLoaiSanPham.setText(loaiSanPham.getTenLoaiSanPham());
            txtMoTa.setText(loaiSanPham.getMoTa());
            btnAdd.setEnabled(false); // Disable add when a row is selected
            btnUpdate.setEnabled(true); // Enable update
            btnDelete.setEnabled(true); // Enable delete
        } else {
            clearInputFields();
            btnAdd.setEnabled(true); // Enable add when no row is selected
            btnUpdate.setEnabled(false); // Disable update
            btnDelete.setEnabled(false); // Disable delete
        }
    }

    /**
     * Lấy dữ liệu từ các trường nhập liệu và tạo một đối tượng LoaiSanPham.
     * @return Đối tượng LoaiSanPham với dữ liệu đã nhập.
     */
    public LoaiSanPham getLoaiSanPhamFromInput() {
        LoaiSanPham lsp = new LoaiSanPham();
        lsp.setMaLoaiSanPham(txtMaLoaiSanPham.getText().trim()); 
        lsp.setTenLoaiSanPham(txtTenLoaiSanPham.getText().trim());
        lsp.setMoTa(txtMoTa.getText().trim());

        // Kiểm tra các trường bắt buộc
        if (lsp.getTenLoaiSanPham().isEmpty()) {
            displayMessage("Tên loại sản phẩm không được để trống.", true);
            return null;
        }

        return lsp;
    }

    /**
     * Xóa trắng các trường nhập liệu.
     */
    public void clearInputFields() {
        txtMaLoaiSanPham.setText("");
        txtTenLoaiSanPham.setText("");
        txtMoTa.setText("");
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

    public JTable getLoaiSanPhamTable() {
        return loaiSanPhamTable;
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
