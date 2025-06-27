package view;

import models.SanPham;
import models.ChiTietPhieuNhap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.EmptyBorder; // For EmptyBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedRectangle2D
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Lớp ImportView tạo giao diện người dùng cho việc quản lý nhập hàng (tạo phiếu nhập mới).
 * Nó hiển thị các trường nhập liệu cho thông tin phiếu nhập, sản phẩm, và bảng chi tiết nhập.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class PhieuNhapView extends JPanel {
    private static final long serialVersionUID = 1L;
    // Phiếu nhập info
    private JTextField txtMaPhieuNhap; // Display only
    private JTextField txtMaNhanVienLap; // Will be auto-filled by controller
    private JTextField txtNgayNhap; // Display only, current date

    // Product Selection Panel
    private JTextField txtSearchProduct;
    private JButton btnSearchProduct;
    private JComboBox<String> cbProductList; // Display product names found
    private JLabel lblSelectedProductUnitPrice; // Don Gia Nhap
    private JTextField txtQuantity;
    private JButton btnAddProductToImportCart;

    // Import Details Table (Cart)
    private JTable importCartTable;
    private DefaultTableModel importCartTableModel;
    private JLabel lblTotalImportAmount; // Tong Thanh Tien
    private JButton btnRemoveProductFromCart;
    private JButton btnUpdateQuantityInCart;
    private JButton btnUpdatePriceInCart;


    // Finalize Panel
    private JButton btnCreateImportInvoice;
    private JButton btnClearForm;
    private JLabel messageLabel; // To display status/error messages

    private SanPham selectedProductInComboBox; // Temporarily holds the product selected in JComboBox

    private List<ChiTietPhieuNhap> currentImportCartItems; // List to hold items in the current import cart
    private double currentTotalImportAmount;

    // For number formatting (VND)
    private NumberFormat currencyFormatter = new DecimalFormat("#,##0₫");

    // --- Color Palette (Consistent with other views) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); 
    private static final Color BORDER_COLOR = new Color(230, 230, 230); 
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); 
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252); // Consistent background

    public PhieuNhapView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BACKGROUND_COLOR); // Set background for the panel

        currentImportCartItems = new ArrayList<>();
        currentTotalImportAmount = 0;

        initComponents();
        layoutComponents();
        setupInitialState();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Import Invoice Info
        txtMaPhieuNhap = new JTextField(5);
        txtMaPhieuNhap.setEditable(false);
        txtMaPhieuNhap.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));

        txtMaNhanVienLap = new JTextField(10);
        txtMaNhanVienLap.setEditable(false);
        txtMaNhanVienLap.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));

        txtNgayNhap = new JTextField(15);
        txtNgayNhap.setEditable(false);
        txtNgayNhap.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));
        
        // Product Selection
        txtSearchProduct = new JTextField(15);
        txtSearchProduct.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc mã SP");
        txtSearchProduct.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));

        btnSearchProduct = createStyledButton("Tìm SP");

        cbProductList = new JComboBox<>();
        ((JLabel)cbProductList.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align items
        cbProductList.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));

        lblSelectedProductUnitPrice = new JLabel("Đơn giá nhập: 0₫");
        lblSelectedProductUnitPrice.setFont(new Font("Arial", Font.BOLD, 12));
        lblSelectedProductUnitPrice.setForeground(PRIMARY_TEXT_COLOR);

        txtQuantity = new JTextField("1", 5); // Default quantity is 1
        txtQuantity.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2));

        btnAddProductToImportCart = createStyledButton("Thêm vào phiếu nhập");

        // Import Cart Table
        String[] cartColumnNames = {"Mã SP", "Tên SP", "Đơn giá nhập", "Số lượng", "Thành tiền"};
        importCartTableModel = new DefaultTableModel(cartColumnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        importCartTable = new JTable(importCartTableModel);
        importCartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Table styling
        importCartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        importCartTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); 
        importCartTable.getTableHeader().setForeground(Color.WHITE);
        importCartTable.setRowHeight(25);
        importCartTable.setGridColor(BORDER_COLOR); 
        importCartTable.setFont(new Font("Arial", Font.PLAIN, 12));

        btnRemoveProductFromCart = createStyledButton("Xóa SP"); // Shorter text
        btnUpdateQuantityInCart = createStyledButton("Sửa SL"); // Shorter text
        btnUpdatePriceInCart = createStyledButton("Sửa ĐG"); // Shorter text

        // Finalize Panel
        lblTotalImportAmount = new JLabel("Tổng tiền nhập: 0₫");
        lblTotalImportAmount.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalImportAmount.setForeground(DARK_BLUE_ACCENT);

        btnCreateImportInvoice = createStyledButton("Tạo Phiếu Nhập");
        btnClearForm = createStyledButton("Làm mới"); // Shorter text

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
        button.setPreferredSize(new Dimension(120, 35)); // Consistent button size, slightly wider
        return button;
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // --- TOP PANEL: Import Invoice Information ---
        JPanel importInfoPanel = new JPanel(new GridBagLayout());
        importInfoPanel.setBackground(Color.WHITE); // Consistent background
        importInfoPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Thông tin Phiếu nhập",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; 

        gbc.gridx = 0; gbc.gridy = 0; importInfoPanel.add(new JLabel("Mã Phiếu Nhập:"), gbc);
        gbc.gridx = 1; importInfoPanel.add(txtMaPhieuNhap, gbc);

        gbc.gridx = 2; gbc.gridy = 0; importInfoPanel.add(new JLabel("Ngày Nhập:"), gbc);
        gbc.gridx = 3; importInfoPanel.add(txtNgayNhap, gbc);

        gbc.gridx = 0; gbc.gridy = 1; importInfoPanel.add(new JLabel("Nhân viên lập:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; importInfoPanel.add(txtMaNhanVienLap, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Spacer to push components up
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4; gbc.weighty = 1.0; 
        importInfoPanel.add(new JLabel(""), gbc); 


        // --- MIDDLE LEFT PANEL: Product Selection ---
        JPanel productSelectionPanel = new JPanel(new GridBagLayout());
        productSelectionPanel.setBackground(Color.WHITE); // Consistent background
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Chọn sản phẩm nhập",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.insets = new Insets(5, 5, 5, 5);
        gbcProd.fill = GridBagConstraints.HORIZONTAL;
        gbcProd.weightx = 1.0; 

        gbcProd.gridx = 0; gbcProd.gridy = 0; productSelectionPanel.add(new JLabel("Tìm sản phẩm:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtSearchProduct, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnSearchProduct, gbcProd);

        gbcProd.gridx = 0; gbcProd.gridy = 1; gbcProd.gridwidth = 3; productSelectionPanel.add(cbProductList, gbcProd);
        gbcProd.gridwidth = 1;

        gbcProd.gridx = 0; gbcProd.gridy = 2; productSelectionPanel.add(lblSelectedProductUnitPrice, gbcProd);
        // lblSelectedProductStock is not needed here, as we are importing

        gbcProd.gridx = 0; gbcProd.gridy = 3; productSelectionPanel.add(new JLabel("Số lượng nhập:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtQuantity, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnAddProductToImportCart, gbcProd);

        // Spacer to push components up
        gbcProd.gridx = 0; gbcProd.gridy = 4; gbcProd.gridwidth = 3; gbcProd.weighty = 1.0; 
        productSelectionPanel.add(new JLabel(""), gbcProd); 

        // --- MIDDLE RIGHT PANEL: Import Cart Table ---
        JPanel importCartPanel = new JPanel(new BorderLayout(5, 5));
        importCartPanel.setBackground(Color.WHITE); // Consistent background
        importCartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Chi tiết Phiếu nhập",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        JScrollPane cartScrollPane = new JScrollPane(importCartTable);
        importCartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel cartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        cartButtonsPanel.setOpaque(false); // Transparent background
        cartButtonsPanel.add(btnUpdateQuantityInCart);
        cartButtonsPanel.add(btnUpdatePriceInCart);
        cartButtonsPanel.add(btnRemoveProductFromCart);
        importCartPanel.add(cartButtonsPanel, BorderLayout.SOUTH);

        // --- COMBINE MIDDLE PANELS ---
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Gap between panels
        middlePanel.setOpaque(false); // Transparent background
        middlePanel.add(productSelectionPanel);
        middlePanel.add(importCartPanel);

        // --- BOTTOM PANEL: Total and Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE); // Consistent background
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10)); // Padding for total/payment area

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false); // Transparent background
        totalPanel.add(lblTotalImportAmount);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        actionPanel.setOpaque(false); // Transparent background
        actionPanel.add(btnCreateImportInvoice);
        actionPanel.add(btnClearForm);
        bottomPanel.add(actionPanel, BorderLayout.CENTER);
        bottomPanel.add(messageLabel, BorderLayout.SOUTH);

        // --- Add all panels to the main ImportView ---
        add(importInfoPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Thiết lập trạng thái ban đầu của giao diện.
     */
    private void setupInitialState() {
        txtNgayNhap.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // Set current date and time
        updateTotalImportAmountLabel(); // Initialize total amount
    }

    /**
     * Cập nhật hiển thị mã nhân viên lập phiếu.
     * @param maNhanVien Mã nhân viên.
     */
    public void setMaNhanVienLap(String maNhanVien) {
        txtMaNhanVienLap.setText(maNhanVien);
    }

    /**
     * Cập nhật danh sách sản phẩm trong ComboBox tìm kiếm.
     * @param productNames Danh sách tên sản phẩm.
     */
    public void populateProductComboBox(List<String> productNames) {
        cbProductList.removeAllItems();
        for (String name : productNames) {
            cbProductList.addItem(name);
        }
        cbProductList.setSelectedIndex(-1); // No item selected initially
        lblSelectedProductUnitPrice.setText("Đơn giá nhập: 0₫");
        selectedProductInComboBox = null;
    }

    /**
     * Hiển thị thông tin chi tiết của sản phẩm được chọn từ ComboBox.
     * @param sanPham Đối tượng SanPham được chọn.
     */
    public void displaySelectedProductDetails(SanPham sanPham) {
        if (sanPham != null) {
            selectedProductInComboBox = sanPham;
            // For import, we use GiaNhap or ask for it explicitly. Here we use GiaBan as a placeholder if GiaNhap doesn't exist.
            // Ideally, SanPham should have a GiaNhap field for import.
            lblSelectedProductUnitPrice.setText("Đơn giá nhập: " + currencyFormatter.format(sanPham.getDonGia())); // Assuming GiaBan as initial DonGiaNhap
        } else {
            selectedProductInComboBox = null;
            lblSelectedProductUnitPrice.setText("Đơn giá nhập: 0₫");
        }
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng nhập (bảng chi tiết phiếu nhập).
     * @param ctpn ChiTietPhieuNhap để thêm vào giỏ hàng.
     * @param sanPham Đối tượng SanPham để lấy tên hiển thị.
     */
    public void addProductToImportCart(ChiTietPhieuNhap ctpn, SanPham sanPham) {
        // Check if product already exists in cart
        boolean found = false;
        for (int i = 0; i < currentImportCartItems.size(); i++) {
            ChiTietPhieuNhap existingCtpn = currentImportCartItems.get(i);
            if (existingCtpn.getMaSanPham().equals(ctpn.getMaSanPham())) {
                existingCtpn.setSoLuong(existingCtpn.getSoLuong() + ctpn.getSoLuong());
                existingCtpn.setDonGiaNhap(ctpn.getDonGiaNhap()); // Cập nhật đơn giá nếu người dùng nhập lại
                // ThanhTien được tính bởi getter, không cần set ở đây
                importCartTableModel.setValueAt(existingCtpn.getSoLuong(), i, 3); // Update quantity column
                importCartTableModel.setValueAt(currencyFormatter.format(existingCtpn.getThanhTien()), i, 4); // Update subtotal column
                importCartTableModel.setValueAt(currencyFormatter.format(existingCtpn.getDonGiaNhap()), i, 2); // Update DonGiaNhap
                found = true;
                break;
            }
        }

        if (!found) {
            // Add new item to cart
            currentImportCartItems.add(ctpn);
            Vector<Object> row = new Vector<>();
            row.add(ctpn.getMaSanPham());
            row.add(sanPham.getTenSanPham()); // Need SanPham name
            row.add(currencyFormatter.format(ctpn.getDonGiaNhap()));
            row.add(ctpn.getSoLuong());
            row.add(currencyFormatter.format(ctpn.getThanhTien()));
            importCartTableModel.addRow(row);
        }
        updateTotalImportAmountLabel();
        txtQuantity.setText("1"); // Reset quantity field
        // lblSelectedProductUnitPrice.setText("Đơn giá nhập: 0₫"); // Reset price - might not be desired
    }

    /**
     * Cập nhật số lượng của một sản phẩm trong giỏ hàng nhập.
     * @param rowIndex Hàng của sản phẩm trong bảng.
     * @param newQuantity Số lượng mới.
     */
    public void updateProductQuantityInImportCart(int rowIndex, int newQuantity) {
        if (rowIndex >= 0 && rowIndex < currentImportCartItems.size()) {
            ChiTietPhieuNhap ctpn = currentImportCartItems.get(rowIndex);
            ctpn.setSoLuong(newQuantity);
            // ThanhTien được tính bởi getter, không cần set ở đây
            importCartTableModel.setValueAt(newQuantity, rowIndex, 3);
            importCartTableModel.setValueAt(currencyFormatter.format(ctpn.getThanhTien()), rowIndex, 4);
            updateTotalImportAmountLabel();
        }
    }

    /**
     * Cập nhật đơn giá nhập của một sản phẩm trong giỏ hàng nhập.
     * @param rowIndex Hàng của sản phẩm trong bảng.
     * @param newUnitPrice Đơn giá nhập mới.
     */
    public void updateProductPriceInImportCart(int rowIndex, int newUnitPrice) {
        if (rowIndex >= 0 && rowIndex < currentImportCartItems.size()) {
            ChiTietPhieuNhap ctpn = currentImportCartItems.get(rowIndex);
            ctpn.setDonGiaNhap(newUnitPrice);
            // ThanhTien được tính bởi getter, không cần set ở đây
            importCartTableModel.setValueAt(currencyFormatter.format(newUnitPrice), rowIndex, 2);
            importCartTableModel.setValueAt(currencyFormatter.format(ctpn.getThanhTien()), rowIndex, 4);
            updateTotalImportAmountLabel();
        }
    }

    /**
     * Xóa một sản phẩm khỏi giỏ hàng nhập (bảng chi tiết phiếu nhập).
     * @param rowIndex Hàng của sản phẩm cần xóa.
     */
    public void removeProductFromImportCart(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < currentImportCartItems.size()) {
            currentImportCartItems.remove(rowIndex);
            importCartTableModel.removeRow(rowIndex);
            updateTotalImportAmountLabel();
        }
    }

    /**
     * Cập nhật hiển thị tổng tiền nhập của phiếu nhập.
     */
    public void updateTotalImportAmountLabel() {
        currentTotalImportAmount = 0;
        for (ChiTietPhieuNhap item : currentImportCartItems) {
            currentTotalImportAmount += item.getThanhTien();
        }
        lblTotalImportAmount.setText("Tổng tiền nhập: " + currencyFormatter.format(currentTotalImportAmount));
    }

    /**
     * Xóa trắng toàn bộ form phiếu nhập.
     */
    public void clearImportForm() {
        txtMaPhieuNhap.setText("");
        txtNgayNhap.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // Reset current date and time
        // txtMaNhanVienLap should be set by controller, not cleared by user action directly
        txtSearchProduct.setText("");
        cbProductList.removeAllItems();
        lblSelectedProductUnitPrice.setText("Đơn giá nhập: 0₫");
        txtQuantity.setText("1");
        selectedProductInComboBox = null;

        currentImportCartItems.clear();
        importCartTableModel.setRowCount(0); // Clear cart table
        updateTotalImportAmountLabel();
        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(PRIMARY_TEXT_COLOR);
    }

    /**
     * Lấy danh sách các sản phẩm hiện có trong giỏ hàng nhập.
     * @return Danh sách ChiTietPhieuNhap trong giỏ hàng.
     */
    public List<ChiTietPhieuNhap> getCurrentImportCartItems() {
        return currentImportCartItems;
    }

    // --- Getters for UI Components to allow Controller to add listeners and get data ---
    public JTextField getTxtSearchProduct() {
        return txtSearchProduct;
    }

    public JComboBox<String> getCbProductList() {
        return cbProductList;
    }

    public SanPham getSelectedProductInComboBox() {
        return selectedProductInComboBox;
    }

    public JTextField getTxtQuantity() {
        return txtQuantity;
    }

    public JTable getImportCartTable() {
        return importCartTable;
    }

    public String getTxtMaNhanVienLap() {
        return txtMaNhanVienLap.getText();
    }

    // --- Action Listeners for Controller ---
    public void addSearchProductButtonListener(ActionListener listener) {
        btnSearchProduct.addActionListener(listener);
    }

    public void addAddProductToImportCartButtonListener(ActionListener listener) {
        btnAddProductToImportCart.addActionListener(listener);
    }

    public void addRemoveProductFromCartButtonListener(ActionListener listener) {
        btnRemoveProductFromCart.addActionListener(listener);
    }

    public void addUpdateQuantityInCartButtonListener(ActionListener listener) {
        btnUpdateQuantityInCart.addActionListener(listener);
    }

    public void addUpdatePriceInCartButtonListener(ActionListener listener) {
        btnUpdatePriceInCart.addActionListener(listener);
    }

    public void addCreateImportInvoiceButtonListener(ActionListener listener) {
        btnCreateImportInvoice.addActionListener(listener);
    }

    public void addClearFormButtonListener(ActionListener listener) {
        btnClearForm.addActionListener(listener);
    }

    public void addProductComboBoxListener(ActionListener listener) {
        cbProductList.addActionListener(listener);
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
