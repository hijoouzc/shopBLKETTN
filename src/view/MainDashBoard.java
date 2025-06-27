package view;

import models.*;
import services.*;
import controllers.*;

import javax.swing.*;
import javax.swing.border.AbstractBorder; // For RoundedCornerBorder
import javax.swing.border.TitledBorder; // For TitledBorder styling
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; // For RoundedCornerBorder
import java.sql.SQLException; // Keep for SQLException handling if needed
import java.net.URL; // For loading images

/**
 * Lớp MainDashBoard là màn hình chính của ứng dụng sau khi đăng nhập thành công.
 * Nó sử dụng JTabbedPane để tổ chức các module chức năng khác nhau.
 */
public class MainDashBoard extends JFrame {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private TaiKhoanNguoiDung loggedInUser;
    private JTabbedPane tabbedPane;

    public MainDashBoard(TaiKhoanNguoiDung user) {
        this.loggedInUser = user;
        setTitle("Trang chủ - Hệ thống Quản lý Cửa hàng (" + user.getLoaiNguoiDung() + ")");
        setSize(1200, 750); // Tăng kích thước để chứa nhiều tab và nội dung hơn
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE); // Set background for the frame content pane

        // Áp dụng các cài đặt Look and Feel cho JTabbedPane
        // Điều này nên được thực hiện ở đây hoặc ở đầu ứng dụng để đảm bảo tính nhất quán
        UIManager.put("TabbedPane.selectedBackground", new Color(25, 69, 137)); // Màu nền tab được chọn
        UIManager.put("TabbedPane.selectedForeground", Color.WHITE); // Màu chữ tab được chọn
        UIManager.put("TabbedPane.focus", new Color(100, 150, 200)); // Màu viền focus (có thể thay đổi)
        UIManager.put("TabbedPane.contentBorder", BorderFactory.createEmptyBorder()); // Xóa viền xung quanh nội dung tab

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14)); // Consistent font for tabs
        tabbedPane.setBackground(Color.WHITE); // Background của vùng tabbed pane (các tab không được chọn)
        tabbedPane.setForeground(new Color(25, 69, 137)); // Màu chữ của các tab không được chọn
        
        // 1. Tab Trang chủ (Welcome)
        JPanel homePanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
        homePanel.setBackground(Color.WHITE);
        homePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0); // Padding giữa các thành phần trong homePanel
        gbc.anchor = GridBagConstraints.CENTER; // Căn giữa các thành phần

        // Add an icon to the home panel
        // Đảm bảo tệp home_icon.png nằm trong thư mục resources của dự án
        URL imageUrl = getClass().getResource("/icons/img1.png"); 
        if (imageUrl != null) {
            ImageIcon homeIcon = new ImageIcon(imageUrl);
            Image image = homeIcon.getImage();
            Image scaledImage = image.getScaledInstance(200, 200, Image.SCALE_SMOOTH); // Scale image
            homeIcon = new ImageIcon(scaledImage);
            JLabel iconLabel = new JLabel(homeIcon);
            homePanel.add(iconLabel, gbc);
            gbc.gridy++; // Move to next row for text
        } else {
            System.err.println("Warning: /resources/home_icon.png not found. Home panel might look plain.");
        }
        
        JLabel welcomeLabel = new JLabel(
            "<html><h1 style='text-align: center; color: #254589; font-size: 28px;'>Chào mừng, " + loggedInUser.getUsername() + "!</h1>" +
            "<p style='text-align: center; font-size: 16px; color: #555;'>Bạn đang đăng nhập với quyền: <b style='color: #254589;'>" + loggedInUser.getLoaiNguoiDung() + "</b></p>" +
            "<p style='text-align: center; font-size: 14px; color: #777;'>Sử dụng các tab bên trên để truy cập các chức năng quản lý hệ thống.</p></html>"
        );
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Base font for description
        homePanel.add(welcomeLabel, gbc);
        tabbedPane.addTab("Trang chủ", homePanel);
        
        // Ensure all views are initialized and added with white backgrounds
        // Khởi tạo tất cả các Service một lần và truyền chúng vào các Controller
        SanPhamService sanPhamService = SanPhamService.getIns();
        LoaiSanPhamService loaiSanPhamService = LoaiSanPhamService.getIns();
        KhachHangService khachHangService = KhachHangService.getIns();
        TaiKhoanNguoiDungService taiKhoanNguoiDungService = TaiKhoanNguoiDungService.getIns();
        NhanVienService nhanVienService = NhanVienService.getIns();
        HoaDonService hoaDonService = HoaDonService.getIns();
        PhieuNhapHangService phieuNhapHangService = PhieuNhapHangService.getIns(); 
        BaoCaoService baoCaoService = BaoCaoService.getIns(); 


        // Lấy MaNhanVien của người dùng đang đăng nhập
        String maNhanVienLap = "";
            NhanVien currentNhanVien = nhanVienService.getNhanVienByMaNguoiDung(loggedInUser.getMaNguoiDung());
            if (currentNhanVien != null) {
                maNhanVienLap = currentNhanVien.getMaNhanVien();
            } else {
                System.err.println("Cảnh báo: Không tìm thấy thông tin nhân viên cho người dùng hiện tại (MaNguoiDung: " + loggedInUser.getMaNguoiDung() + "). MaNhanVienLap sẽ rỗng.");
                // Có thể hiển thị thông báo lỗi hoặc xử lý khác tùy theo yêu cầu nghiệp vụ
            }
        
            

        // 6. Tab Quản lý Bán hàng
        HoaDonView hoaDonView = new HoaDonView();
        hoaDonView.setBackground(Color.WHITE); // Set panel background
        new HoaDonController(hoaDonView, hoaDonService, sanPhamService, khachHangService, nhanVienService, maNhanVienLap);
        tabbedPane.addTab("Quản lý Bán hàng", hoaDonView);
        
        
        // 2. Tab Quản lý Sản phẩm
        SanPhamView sanPhamView = new SanPhamView();
        sanPhamView.setBackground(Color.WHITE); // Set panel background
        new SanPhamController(sanPhamView, sanPhamService, loaiSanPhamService);
        tabbedPane.addTab("Quản lý Sản phẩm", sanPhamView);
        
        // 3. Tab Quản lý Khách hàng
        KhachHangView khachHangView = new KhachHangView();
        khachHangView.setBackground(Color.WHITE); // Set panel background
        new KhachHangController(khachHangView, khachHangService, taiKhoanNguoiDungService);
        tabbedPane.addTab("Quản lý Khách hàng", khachHangView);
        
        // 4. Tab Quản lý Nhân viên
        NhanVienView nhanVienView = new NhanVienView();
        nhanVienView.setBackground(Color.WHITE); // Set panel background
        new NhanVienController(nhanVienView, nhanVienService, taiKhoanNguoiDungService);
        tabbedPane.addTab("Quản lý Nhân viên", nhanVienView);
        

        // 5. Tab Quản lý Loại Sản phẩm
        LoaiSanPhamView loaiSanPhamView = new LoaiSanPhamView();
        loaiSanPhamView.setBackground(Color.WHITE); // Set panel background
        new LoaiSanPhamController(loaiSanPhamView, loaiSanPhamService);
        tabbedPane.addTab("Quản lý Loại Sản phẩm", loaiSanPhamView);
        
        


        // 7. Tab Quản lý Nhập hàng
        PhieuNhapView phieuNhapView = new PhieuNhapView();
        phieuNhapView.setBackground(Color.WHITE); // Set panel background
        new PhieuNhapController(phieuNhapView, phieuNhapHangService, sanPhamService, nhanVienService, maNhanVienLap);
        tabbedPane.addTab("Quản lý Nhập hàng", phieuNhapView);
        
        //8. Tab Quản lý báo cáo thống kê
        BaoCaoView baoCaoView = new BaoCaoView();
        baoCaoView.setBackground(Color.WHITE); // Set panel background
        new BaoCaoController(baoCaoView, baoCaoService);
        tabbedPane.addTab("Quản lý Báo cáo", baoCaoView);
        
    }

    private void layoutComponents() {
        // Tạo một JPanel wrapper để thêm padding xung quanh tabbedPane
        JPanel contentWrapperPanel = new JPanel(new BorderLayout());
        contentWrapperPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding xung quanh tabbedPane
        contentWrapperPanel.setBackground(Color.WHITE);
        contentWrapperPanel.add(tabbedPane, BorderLayout.CENTER);
        add(contentWrapperPanel, BorderLayout.CENTER); // Thêm wrapper vào JFrame thay vì tabbedPane trực tiếp

        JButton logoutButton = createStyledButton("Đăng xuất"); // Apply styled button
        logoutButton.setPreferredSize(new Dimension(120, 40)); // Slightly larger for clarity
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                });
            }
        });
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); // Add padding to flow layout
        footerPanel.setBackground(new Color(240, 240, 240)); // Slightly off-white for footer to distinguish
        footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY)); // Top border
        footerPanel.add(logoutButton);
        add(footerPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method to create a JButton with LoginView/KhachHangView's styling.
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
        button.setFont(new Font("Arial", Font.BOLD, 14)); 
        return button;
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
