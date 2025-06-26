package view;

import models.ChiTietHoaDon;
import models.HoaDon;
import models.KhachHang;
import models.SanPham;
import models.ChiTietViTri;

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
import java.util.Locale;
import java.util.Vector;

/**
 * Lớp HoaDonView tạo giao diện người dùng cho việc quản lý bán hàng (tạo hóa đơn mới).
 * Nó hiển thị các trường nhập liệu cho hóa đơn, khách hàng, sản phẩm, và bảng chi tiết hóa đơn.
 * Không chứa logic nghiệp vụ trực tiếp.
 */
public class HoaDonView extends JPanel {
    private static final long serialVersionUID = 1L;
    // Customer Info Panel
    private JTextField txtMaKhachHang;
    private JTextField txtTenKhachHang;
    private JTextField txtSdtKhachHang;
    private JButton btnSelectCustomer;
    private JRadioButton rbRegisteredCustomer;
    private JRadioButton rbWalkInCustomer;
    private ButtonGroup customerTypeGroup;

    // Product Selection Panel
    private JTextField txtSearchProduct;
    private JButton btnSearchProduct;
    private JComboBox<String> cbProductList; // Display product names found
    private JLabel lblSelectedProductPrice;
    private JLabel lblSelectedProductStock;
    private JTextField txtQuantity;
    private JButton btnAddProductToCart;

    // Invoice Details Table
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotalAmount;
    private JButton btnRemoveProductFromCart;
    private JButton btnUpdateQuantityInCart;

    // Payment and Finalize Panel
    private JComboBox<String> cbPaymentMethod;
    private JButton btnCreateInvoice;
    private JButton btnClearForm;
    private JLabel messageLabel; // To display status/error messages

    private SanPham selectedProductInComboBox; // Temporarily holds the product selected in JComboBox

    private List<ChiTietHoaDon> currentCartItems; // List to hold items in the current invoice cart
    private int currentTotalAmount; // Đã đổi kiểu từ double sang int để khớp với DonGia và ThanhTien là int

    // For number formatting (VND)
    private NumberFormat currencyFormatter = new DecimalFormat("#,##0₫");

    // --- Color Palette (Consistent with other views) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); 
    private static final Color BORDER_COLOR = new Color(230, 230, 230); 
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); 
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252); // Consistent background

    public HoaDonView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BACKGROUND_COLOR);

        currentCartItems = new ArrayList<>();
        currentTotalAmount = 0;

        initComponents();
        layoutComponents();
        setupInitialState();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Customer Info
        txtMaKhachHang = new JTextField(10);
        txtMaKhachHang.setEditable(false);
        txtMaKhachHang.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        txtTenKhachHang = new JTextField(20);
        txtTenKhachHang.putClientProperty("JTextField.placeholderText", "Nhập tên khách hàng");
        txtTenKhachHang.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        txtSdtKhachHang = new JTextField(15);
        txtSdtKhachHang.putClientProperty("JTextField.placeholderText", "Nhập SĐT khách hàng");
        txtSdtKhachHang.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        btnSelectCustomer = createStyledButton("Chọn khách hàng");

        rbRegisteredCustomer = new JRadioButton("Khách hàng đã đăng ký");
        rbWalkInCustomer = new JRadioButton("Khách vãng lai");
        customerTypeGroup = new ButtonGroup();
        customerTypeGroup.add(rbRegisteredCustomer);
        customerTypeGroup.add(rbWalkInCustomer);
        rbRegisteredCustomer.setSelected(true); // Default selection
        // Styling for Radio Buttons
        rbRegisteredCustomer.setBackground(Color.WHITE);
        rbWalkInCustomer.setBackground(Color.WHITE);
        rbRegisteredCustomer.setForeground(PRIMARY_TEXT_COLOR);
        rbWalkInCustomer.setForeground(PRIMARY_TEXT_COLOR);
        rbRegisteredCustomer.setFont(new Font("Arial", Font.PLAIN, 12));
        rbWalkInCustomer.setFont(new Font("Arial", Font.PLAIN, 12));


        // Product Selection
        txtSearchProduct = new JTextField(15);
        txtSearchProduct.putClientProperty("JTextField.placeholderText", "Nhập tên hoặc mã SP");
        txtSearchProduct.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        btnSearchProduct = createStyledButton("Tìm SP");
        
        cbProductList = new JComboBox<>();
        ((JLabel)cbProductList.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align items
        cbProductList.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        lblSelectedProductPrice = new JLabel("Giá: 0₫");
        lblSelectedProductPrice.setFont(new Font("Arial", Font.BOLD, 12));
        lblSelectedProductPrice.setForeground(PRIMARY_TEXT_COLOR);

        lblSelectedProductStock = new JLabel("Tồn: 0");
        lblSelectedProductStock.setFont(new Font("Arial", Font.BOLD, 12));
        lblSelectedProductStock.setForeground(PRIMARY_TEXT_COLOR);

        txtQuantity = new JTextField("1", 5); // Default quantity is 1
        txtQuantity.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        btnAddProductToCart = createStyledButton("Thêm vào hóa đơn");

        // Cart Table
        String[] cartColumnNames = {"Mã SP", "Tên SP", "Giá bán", "Số lượng", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cartColumnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép chỉnh sửa trực tiếp trên bảng
            }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Table styling
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        cartTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); 
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.setRowHeight(25);
        cartTable.setGridColor(BORDER_COLOR); 
        cartTable.setFont(new Font("Arial", Font.PLAIN, 12));

        btnRemoveProductFromCart = createStyledButton("Xóa SP"); // Shorter text
        btnUpdateQuantityInCart = createStyledButton("Sửa SL"); // Shorter text


        // Payment and Finalize
        lblTotalAmount = new JLabel("Tổng tiền: 0₫");
        lblTotalAmount.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotalAmount.setForeground(DARK_BLUE_ACCENT); // Consistent blue color

        cbPaymentMethod = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"});
        ((JLabel)cbPaymentMethod.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align items
        cbPaymentMethod.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thickness increased to 2

        btnCreateInvoice = createStyledButton("Tạo hóa đơn");
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
        // --- TOP PANEL: Customer Information ---
        JPanel customerPanel = new JPanel(new GridBagLayout());
        customerPanel.setBackground(Color.WHITE); // Consistent background
        customerPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Thông tin khách hàng",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Allow columns to expand

        // Customer Type Radio Buttons
        JPanel customerTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerTypePanel.setOpaque(false); // Transparent background
        customerTypePanel.add(rbRegisteredCustomer);
        customerTypePanel.add(rbWalkInCustomer);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; customerPanel.add(customerTypePanel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Registered Customer Fields
        gbc.gridx = 0; gbc.gridy = 1; customerPanel.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1; customerPanel.add(txtMaKhachHang, gbc);
        gbc.gridx = 2; customerPanel.add(btnSelectCustomer, gbc);

        // Walk-in Customer Fields
        gbc.gridx = 0; gbc.gridy = 2; customerPanel.add(new JLabel("Tên KH:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; customerPanel.add(txtTenKhachHang, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 3; customerPanel.add(new JLabel("SĐT KH:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; customerPanel.add(txtSdtKhachHang, gbc);
        gbc.gridwidth = 1;
        
        // Spacer to push components up
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 3; gbc.weighty = 1.0; 
        customerPanel.add(new JLabel(""), gbc); 


        // --- MIDDLE LEFT PANEL: Product Selection ---
        JPanel productSelectionPanel = new JPanel(new GridBagLayout());
        productSelectionPanel.setBackground(Color.WHITE); // Consistent background
        productSelectionPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Chọn sản phẩm",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.insets = new Insets(5, 5, 5, 5);
        gbcProd.fill = GridBagConstraints.HORIZONTAL;
        gbcProd.weightx = 1.0; // Allow columns to expand

        gbcProd.gridx = 0; gbcProd.gridy = 0; productSelectionPanel.add(new JLabel("Tìm sản phẩm:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtSearchProduct, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnSearchProduct, gbcProd);

        gbcProd.gridx = 0; gbcProd.gridy = 1; gbcProd.gridwidth = 3; productSelectionPanel.add(cbProductList, gbcProd);
        gbcProd.gridwidth = 1;

        gbcProd.gridx = 0; gbcProd.gridy = 2; productSelectionPanel.add(lblSelectedProductPrice, gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 2; productSelectionPanel.add(lblSelectedProductStock, gbcProd);

        gbcProd.gridx = 0; gbcProd.gridy = 3; productSelectionPanel.add(new JLabel("Số lượng:"), gbcProd);
        gbcProd.gridx = 1; productSelectionPanel.add(txtQuantity, gbcProd);
        gbcProd.gridx = 2; productSelectionPanel.add(btnAddProductToCart, gbcProd);

        // Spacer to push components up
        gbcProd.gridx = 0; gbcProd.gridy = 4; gbcProd.gridwidth = 3; gbcProd.weighty = 1.0; 
        productSelectionPanel.add(new JLabel(""), gbcProd); 


        // --- MIDDLE RIGHT PANEL: Cart Table ---
        JPanel cartPanel = new JPanel(new BorderLayout(5, 5));
        cartPanel.setBackground(Color.WHITE); // Consistent background
        cartPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thickness increased to 2
            "Chi tiết hóa đơn",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        JScrollPane cartScrollPane = new JScrollPane(cartTable);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);

        JPanel cartButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        cartButtonsPanel.setOpaque(false); // Transparent background
        cartButtonsPanel.add(btnUpdateQuantityInCart);
        cartButtonsPanel.add(btnRemoveProductFromCart);
        cartPanel.add(cartButtonsPanel, BorderLayout.SOUTH);

        // --- COMBINE MIDDLE PANELS ---
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Gap between panels
        middlePanel.setOpaque(false); // Transparent background
        middlePanel.add(productSelectionPanel);
        middlePanel.add(cartPanel);

        // --- BOTTOM PANEL: Total, Payment, and Actions ---
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE); // Consistent background
        bottomPanel.setBorder(new EmptyBorder(5, 10, 10, 10)); // Padding for total/payment area

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setOpaque(false); // Transparent background
        totalPanel.add(lblTotalAmount);
        bottomPanel.add(totalPanel, BorderLayout.NORTH);

        JPanel paymentActionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        paymentActionPanel.setOpaque(false); // Transparent background
        paymentActionPanel.add(new JLabel("Phương thức thanh toán:"));
        paymentActionPanel.add(cbPaymentMethod);
        paymentActionPanel.add(btnCreateInvoice);
        paymentActionPanel.add(btnClearForm);
        bottomPanel.add(paymentActionPanel, BorderLayout.CENTER);
        bottomPanel.add(messageLabel, BorderLayout.SOUTH);


        // --- Add all panels to the main HoaDonView ---
        add(customerPanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Thiết lập trạng thái ban đầu của giao diện (ví dụ: vô hiệu hóa các trường liên quan đến khách hàng đã đăng ký).
     */
    private void setupInitialState() {
        toggleCustomerFields(true); // Default to registered customer
        updateTotalAmountLabel(); // Initialize total amount
    }

    /**
     * Bật/tắt các trường nhập liệu khách hàng dựa trên loại khách hàng.
     * Đồng thời, xóa các trường không liên quan.
     * @param isRegistered True nếu là khách hàng đã đăng ký, False nếu là khách vãng lai.
     */
    public void toggleCustomerFields(boolean isRegistered) {
        txtMaKhachHang.setEnabled(isRegistered);
        btnSelectCustomer.setEnabled(isRegistered);
        txtTenKhachHang.setEnabled(!isRegistered);
        txtSdtKhachHang.setEnabled(!isRegistered);

        // Xóa các trường không liên quan khi chuyển đổi loại khách hàng
        if (isRegistered) {
            txtTenKhachHang.setText(""); // Xóa tên khách vãng lai
            txtSdtKhachHang.setText(""); // Xóa SĐT khách vãng lai
        } else { // Khách vãng lai
            txtMaKhachHang.setText(""); // Xóa mã khách hàng đã đăng ký
            // Ensure walk-in fields are enabled for input
            txtTenKhachHang.setEditable(true);
            txtSdtKhachHang.setEditable(true);
        }
    }

    /**
     * Hiển thị thông tin khách hàng đã chọn lên các trường nhập liệu.
     * @param khachHang Đối tượng KhachHang được chọn.
     */
    public void displaySelectedCustomer(KhachHang khachHang) {
        if (khachHang != null) {
            txtMaKhachHang.setText(khachHang.getMaKhachHang());
            txtTenKhachHang.setText(khachHang.getHoTen());
            txtSdtKhachHang.setText(khachHang.getSdt());
            // Optionally, disable manual input for these fields if a registered customer is selected
            txtTenKhachHang.setEnabled(false); // Disable editing for registered customer's name
            txtSdtKhachHang.setEnabled(false); // Disable editing for registered customer's phone
        } else {
            clearCustomerFields(); // Chỉ xóa text, không reset radio button
            txtTenKhachHang.setEnabled(true); // Enable for manual input again
            txtSdtKhachHang.setEnabled(true); // Enable for manual input again
        }
    }

    /**
     * Xóa trắng các trường thông tin khách hàng (chỉ text fields).
     * Không ảnh hưởng đến trạng thái của radio button.
     */
    public void clearCustomerFields() {
        txtMaKhachHang.setText("");
        txtTenKhachHang.setText("");
        txtSdtKhachHang.setText("");
        // Ensure walk-in fields are editable after clearing if walk-in radio is selected
        if(rbWalkInCustomer.isSelected()) {
            txtTenKhachHang.setEditable(true);
            txtSdtKhachHang.setEditable(true);
        }
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
        lblSelectedProductPrice.setText("Giá: 0₫");
        lblSelectedProductStock.setText("Tồn: 0");
        selectedProductInComboBox = null;
    }

    /**
     * Hiển thị thông tin chi tiết của sản phẩm được chọn từ ComboBox.
     * @param sanPham Đối tượng SanPham được chọn.
     */
    public void displaySelectedProductDetails(SanPham sanPham) {
        if (sanPham != null) {
            selectedProductInComboBox = sanPham;
            lblSelectedProductPrice.setText("Giá: " + currencyFormatter.format(sanPham.getDonGia()));
            lblSelectedProductStock.setText("Tồn: " + sanPham.getSoLuongTon()); // Lấy số lượng tồn từ SanPham model
        } else {
            selectedProductInComboBox = null;
            lblSelectedProductPrice.setText("Giá: 0₫");
            lblSelectedProductStock.setText("Tồn: 0");
        }
    }

    /**
     * Thêm một sản phẩm vào giỏ hàng (bảng chi tiết hóa đơn).
     * @param cthd ChiTietHoaDon để thêm vào giỏ hàng.
     * @param sanPham SanPham tương ứng (chứa tên và đơn giá).
     */
    public void addProductToCart(ChiTietHoaDon cthd, SanPham sanPham) {
        // Check if product already exists in cart
        boolean found = false;
        for (int i = 0; i < currentCartItems.size(); i++) {
            ChiTietHoaDon existingCthd = currentCartItems.get(i);
            if (existingCthd.getMaSanPham().equals(cthd.getMaSanPham())) {
                existingCthd.setSoLuong(existingCthd.getSoLuong() + cthd.getSoLuong());
                existingCthd.setThanhTien(existingCthd.getSoLuong() * sanPham.getDonGia()); // Sử dụng DonGia từ SanPham
                cartTableModel.setValueAt(existingCthd.getSoLuong(), i, 3); // Update quantity column
                cartTableModel.setValueAt(currencyFormatter.format(existingCthd.getThanhTien()), i, 4); // Update subtotal column
                found = true;
                break;
            }
        }

        if (!found) {
            // Add new item to cart
            currentCartItems.add(cthd);
            Vector<Object> row = new Vector<>();
            row.add(cthd.getMaSanPham());
            row.add(sanPham.getTenSanPham()); // Need SanPham name
            row.add(currencyFormatter.format(sanPham.getDonGia())); // Hiển thị giá bán
            row.add(cthd.getSoLuong());
            row.add(currencyFormatter.format(cthd.getThanhTien()));
            cartTableModel.addRow(row);
        }
        updateTotalAmountLabel();
        txtQuantity.setText("1"); // Reset quantity field
    }

    /**
     * Cập nhật số lượng của một sản phẩm trong giỏ hàng.
     * @param rowIndex Hàng của sản phẩm trong bảng.
     * @param newQuantity Số lượng mới.
     * @param sanPham Đối tượng SanPham tương ứng (cần để lấy đơn giá).
     */
    public void updateProductQuantityInCart(int rowIndex, int newQuantity, SanPham sanPham) { 
        if (rowIndex >= 0 && rowIndex < currentCartItems.size()) {
            ChiTietHoaDon cthd = currentCartItems.get(rowIndex);
            
            cthd.setSoLuong(newQuantity);
            cthd.setThanhTien(newQuantity * sanPham.getDonGia()); // Cập nhật Thành tiền dựa trên đơn giá
            
            cartTableModel.setValueAt(newQuantity, rowIndex, 3);
            cartTableModel.setValueAt(currencyFormatter.format(cthd.getThanhTien()), rowIndex, 4);
            updateTotalAmountLabel();
        }
    }


    /**
     * Xóa một sản phẩm khỏi giỏ hàng (bảng chi tiết hóa đơn).
     * @param rowIndex Hàng của sản phẩm cần xóa.
     */
    public void removeProductFromCart(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < currentCartItems.size()) {
            currentCartItems.remove(rowIndex);
            cartTableModel.removeRow(rowIndex);
            updateTotalAmountLabel();
        }
    }

    /**
     * Cập nhật hiển thị tổng tiền của hóa đơn.
     */
    public void updateTotalAmountLabel() {
        currentTotalAmount = 0; // Reset total
        for (ChiTietHoaDon item : currentCartItems) {
            currentTotalAmount += item.getThanhTien(); // Sum integer amounts
        }
        lblTotalAmount.setText("Tổng tiền: " + currencyFormatter.format(currentTotalAmount));
    }

    /**
     * Xóa trắng toàn bộ form hóa đơn.
     */
    public void clearInvoiceForm() {
        clearCustomerFields(); // Chỉ xóa text fields
        rbRegisteredCustomer.setSelected(true); // Đặt lại radio button về khách hàng đã đăng ký
        toggleCustomerFields(true); // Cập nhật trạng thái các trường khách hàng

        txtSearchProduct.setText("");
        cbProductList.removeAllItems();
        lblSelectedProductPrice.setText("Giá: 0₫");
        lblSelectedProductStock.setText("Tồn: 0");
        txtQuantity.setText("1");
        selectedProductInComboBox = null;

        currentCartItems.clear();
        cartTableModel.setRowCount(0); // Clear cart table
        updateTotalAmountLabel();
        cbPaymentMethod.setSelectedIndex(0);
        messageLabel.setText("Sẵn sàng.");
        messageLabel.setForeground(PRIMARY_TEXT_COLOR);
    }

    /**
     * Lấy danh sách các sản phẩm hiện có trong giỏ hàng.
     * @return Danh sách ChiTietHoaDon trong giỏ hàng.
     */
    public List<ChiTietHoaDon> getCurrentCartItems() {
        return currentCartItems;
    }

    /**
     * Lấy tổng số tiền hiện tại của hóa đơn.
     * @return Tổng số tiền.
     */
    public int getCurrentTotalAmount() { 
        return currentTotalAmount;
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

    // --- Getters for UI Components to allow Controller to add listeners and get data ---

    public JRadioButton getRbRegisteredCustomer() {
        return rbRegisteredCustomer;
    }

    public JRadioButton getRbWalkInCustomer() {
        return rbWalkInCustomer;
    }

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

    public JTable getCartTable() {
        return cartTable;
    }

    public JButton getBtnRemoveProductFromCart() { 
        return btnRemoveProductFromCart;
    }

    public JButton getBtnUpdateQuantityInCart() { 
        return btnUpdateQuantityInCart;
    }

    public String getSelectedPaymentMethod() {
        return (String) cbPaymentMethod.getSelectedItem();
    }

    public String getTxtMaKhachHang() {
        return txtMaKhachHang.getText();
    }

    public String getTxtTenKhachHang() {
        return txtTenKhachHang.getText();
    }

    public String getTxtSdtKhachHang() {
        return txtSdtKhachHang.getText();
    }

    // --- Action Listeners for Controller ---
    public void addSelectCustomerButtonListener(ActionListener listener) {
        btnSelectCustomer.addActionListener(listener);
    }

    public void addSearchProductButtonListener(ActionListener listener) {
        btnSearchProduct.addActionListener(listener);
    }

    public void addAddProductToCartButtonListener(ActionListener listener) {
        btnAddProductToCart.addActionListener(listener);
    }

    public void addRemoveProductFromCartButtonListener(ActionListener listener) {
        btnRemoveProductFromCart.addActionListener(listener);
    }
    
    public void addUpdateQuantityInCartButtonListener(ActionListener listener) {
        btnUpdateQuantityInCart.addActionListener(listener);
    }

    public void addCreateInvoiceButtonListener(ActionListener listener) {
        btnCreateInvoice.addActionListener(listener);
    }

    public void addClearFormButtonListener(ActionListener listener) {
        btnClearForm.addActionListener(listener);
    }

    public void addRegisteredCustomerRadioButtonListener(ActionListener listener) {
        rbRegisteredCustomer.addActionListener(listener);
    }

    public void addWalkInCustomerRadioButtonListener(ActionListener listener) {
        rbWalkInCustomer.addActionListener(listener);
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
