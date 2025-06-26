package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedRectangle2D
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

/**
 * Lớp ReportView tạo giao diện người dùng cho việc xem các báo cáo và thống kê.
 * Nó cung cấp các tùy chọn để chọn loại báo cáo, khoảng thời gian (nếu có),
 * và hiển thị kết quả trong một bảng.
 */
public class BaoCaoView extends JPanel {
    private static final long serialVersionUID = 1L;
    private JComboBox<String> cbReportType;
    private JTextField txtYear;
    private JButton btnGenerateReport;
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private JLabel messageLabel;

    private NumberFormat currencyFormatter = new DecimalFormat("#,##0₫");

    // --- Color Palette (Consistent with other views) ---
    private static final Color DARK_BLUE_ACCENT = new Color(25, 69, 137); 
    private static final Color BORDER_COLOR = new Color(230, 230, 230); 
    private static final Color PRIMARY_TEXT_COLOR = new Color(50, 50, 50); 
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 252); // Consistent background

    // Constants for report types
    public static final String REPORT_TYPE_MONTHLY_REVENUE = "Doanh thu theo tháng (năm)";
    public static final String REPORT_TYPE_YEARLY_REVENUE = "Doanh thu theo năm";
    public static final String REPORT_TYPE_TOP_SELLING_PRODUCTS = "Sản phẩm bán chạy nhất";
    public static final String REPORT_TYPE_PRODUCT_STOCK_LEVELS = "Mức tồn kho sản phẩm";
    public static final String REPORT_TYPE_MONTHLY_IMPORT_QUANTITY = "Số lượng nhập theo tháng (năm)";

    public BaoCaoView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(BACKGROUND_COLOR); // Set background for the panel

        initComponents();
        layoutComponents();
        setupInitialState();
    }

    /**
     * Khởi tạo các thành phần UI.
     */
    private void initComponents() {
        // Report type selection
        String[] reportTypes = {
            REPORT_TYPE_MONTHLY_REVENUE,
            REPORT_TYPE_YEARLY_REVENUE,
            REPORT_TYPE_TOP_SELLING_PRODUCTS,
            REPORT_TYPE_PRODUCT_STOCK_LEVELS,
            REPORT_TYPE_MONTHLY_IMPORT_QUANTITY
        };
        cbReportType = new JComboBox<>(reportTypes);
        ((JLabel)cbReportType.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center align items
        cbReportType.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        txtYear = new JTextField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), 4);
        txtYear.putClientProperty("JTextField.placeholderText", "Năm");
        txtYear.setBorder(new RoundedCornerBorder(10, BORDER_COLOR, 2)); // Thicker border

        btnGenerateReport = createStyledButton("Tạo Báo cáo");

        // Report table
        reportTableModel = new DefaultTableModel() {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table is read-only
            }
        };
        reportTable = new JTable(reportTableModel);
        reportTable.setFillsViewportHeight(true); // Make table fill the scroll pane height
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Consistent selection mode
        reportTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        // Table styling
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reportTable.getTableHeader().setBackground(DARK_BLUE_ACCENT); 
        reportTable.getTableHeader().setForeground(Color.WHITE);
        reportTable.setRowHeight(25);
        reportTable.setGridColor(BORDER_COLOR); 
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));


        messageLabel = new JLabel("Chọn loại báo cáo để bắt đầu.");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setForeground(PRIMARY_TEXT_COLOR); // Consistent text color
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 12)); // Consistent font style
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
        button.setPreferredSize(new Dimension(130, 35)); // Slightly wider for "Tạo Báo cáo"
        return button;
    }

    /**
     * Sắp xếp các thành phần UI trên panel.
     */
    private void layoutComponents() {
        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); // Center align controls
        controlPanel.setBackground(Color.WHITE); // Consistent background
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 2, true), // Thicker border
            "Tùy chọn Báo cáo",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14), DARK_BLUE_ACCENT 
        ));
        controlPanel.add(new JLabel("Loại báo cáo:"));
        controlPanel.add(cbReportType);
        controlPanel.add(new JLabel("Năm (nếu có):"));
        controlPanel.add(txtYear);
        controlPanel.add(btnGenerateReport);

        add(controlPanel, BorderLayout.NORTH);

        // Center panel for report table
        JScrollPane scrollPane = new JScrollPane(reportTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel for messages
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.WHITE); // Consistent background
        statusPanel.add(messageLabel);
        add(statusPanel, BorderLayout.SOUTH);
    }

    /**
     * Thiết lập trạng thái ban đầu của giao diện.
     */
    private void setupInitialState() {
        // Ensure initial table is empty or has a default message
        reportTableModel.setColumnCount(0);
        reportTableModel.setRowCount(0);
    }

    /**
     * Cập nhật tiêu đề cột và dữ liệu cho bảng báo cáo.
     *
     * @param columnNames Mảng các tên cột.
     * @param data Vector chứa các Vector dòng dữ liệu.
     */
    public void updateReportTable(String[] columnNames, Vector<Vector<Object>> data) {
        reportTableModel.setColumnIdentifiers(columnNames);
        reportTableModel.setRowCount(0); // Clear existing data
        for (Vector<Object> row : data) {
            reportTableModel.addRow(row);
        }
    }

    /**
     * Lấy loại báo cáo được chọn từ ComboBox.
     *
     * @return Chuỗi tên loại báo cáo.
     */
    public String getSelectedReportType() {
        return (String) cbReportType.getSelectedItem();
    }

    /**
     * Lấy giá trị năm được nhập vào JTextField.
     *
     * @return Chuỗi năm.
     */
    public String getYearInput() {
        return txtYear.getText().trim();
    }

    /**
     * Định dạng số tiền thành chuỗi tiền tệ (VND).
     *
     * @param amount Số tiền.
     * @return Chuỗi số tiền đã định dạng.
     */
    public String formatCurrency(double amount) {
        return currencyFormatter.format(amount);
    }

    /**
     * Định dạng số nguyên thành chuỗi.
     *
     * @param number Số nguyên.
     * @return Chuỗi số nguyên đã định dạng.
     */
    public String formatNumber(int number) {
        return String.format(Locale.getDefault(), "%,d", number);
    }

    /**
     * Hiển thị thông báo trên giao diện.
     *
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

    // --- Action Listeners for Controller ---
    public void addGenerateReportButtonListener(ActionListener listener) {
        btnGenerateReport.addActionListener(listener);
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
