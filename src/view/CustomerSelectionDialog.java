package view;

import models.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

/**
 * Dialog cho phép người dùng chọn một khách hàng từ danh sách.
 */
public class CustomerSelectionDialog extends JDialog {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTable customerTable;
    private DefaultTableModel tableModel;
    private KhachHang selectedCustomer;
    private List<KhachHang> allCustomers; // Store the original list to easily find the object

    public CustomerSelectionDialog(JFrame parent, List<KhachHang> customers) {
        super(parent, "Chọn Khách Hàng", true); // Modal dialog
        this.allCustomers = customers; // Store the list
        setSize(600, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents(customers);
        layoutComponents();
    }

    private void initComponents(List<KhachHang> customers) {
        String[] columnNames = {"Mã KH", "Tên KH", "SĐT"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Populate table with data
        for (KhachHang kh : customers) {
            Vector<Object> row = new Vector<>();
            row.add(kh.getMaKhachHang());
            row.add(kh.getHoTen());
            row.add(kh.getSdt());
            tableModel.addRow(row);
        }

        // Double click to select customer
        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double click
                    int selectedRow = customerTable.getSelectedRow();
                    if (selectedRow != -1) {
                        String maKhachHang = (String) tableModel.getValueAt(selectedRow, 0);
                        // Find the customer object from the original list
                        for (KhachHang kh : allCustomers) {
                            if (kh.getMaKhachHang().equals(maKhachHang)) {
                                selectedCustomer = kh;
                                break;
                            }
                        }
                        dispose(); // Close dialog
                    }
                }
            }
        });
    }

    private void layoutComponents() {
        JScrollPane scrollPane = new JScrollPane(customerTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton selectButton = new JButton("Chọn");
        selectButton.addActionListener(e -> {
            int selectedRow = customerTable.getSelectedRow();
            if (selectedRow != -1) {
                String maKhachHang = (String) tableModel.getValueAt(selectedRow, 0);
                // Find the customer object from the original list
                for (KhachHang kh : allCustomers) {
                    if (kh.getMaKhachHang().equals(maKhachHang)) {
                        selectedCustomer = kh;
                        break;
                    }
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> {
            selectedCustomer = null; // No customer selected
            dispose(); // Close dialog
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Trả về khách hàng đã được chọn.
     * @return KhachHang đã chọn, hoặc null nếu không chọn.
     */
    public KhachHang getSelectedCustomer() {
        return selectedCustomer;
    }
}

