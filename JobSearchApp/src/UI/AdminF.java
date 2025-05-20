package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import BLL.*;
import DTO.*;
public class AdminF extends JFrame implements ActionListener {

    private JPanel mainContentPanel; // Panel chính sử dụng CardLayout
    private CardLayout cardLayout;

    // Các panel con cho từng chức năng quản lý
    private JPanel dashboardPanel; // Panel chào mừng hoặc thống kê cơ bản
    private CategoryManagementPanel categoryManagementPanel;
    private UserManagementPanel userManagementPanel;
    private JobApprovalPanel jobApprovalPanel;

    // Các nút điều hướng
    private JButton btnNavDashboard, btnNavManageCategories, btnNavManageUsers, btnNavManageJobs, btnLogout;
    private JPanel navigationPanel; // Panel chứa các nút điều hướng (bên trái)
    private JLabel lbHeaderTitle; // Tiêu đề của trang Admin

    private static int adminId; // Lưu ID của admin đang đăng nhập (nếu cần thiết cho các thao tác)

    public AdminF(String title, int loggedInAdminId) {
        super(title);
        AdminF.adminId = loggedInAdminId;
        initLookAndFeel();
        initializeUI();
    }

    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720)); // Kích thước cửa sổ Admin

        // === HEADER PANEL ===
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(45, 45, 45)); // Màu nền tối cho header Admin
        lbHeaderTitle = new JLabel("TRANG QUẢN TRỊ HỆ THỐNG");
        lbHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lbHeaderTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        headerPanel.add(lbHeaderTitle);

        // === NAVIGATION PANEL (Bên trái) ===
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(60, 63, 65)); // Màu nền cho thanh điều hướng
        navigationPanel.setBorder(new EmptyBorder(15, 5, 15, 5));
        navigationPanel.setPreferredSize(new Dimension(220, 0)); // Chiều rộng cố định cho navigation

        // Tạo các nút điều hướng
        btnNavDashboard = createNavigationButton("Bảng điều khiển", "dashboard_icon.png"); // Thêm icon nếu có
        btnNavManageCategories = createNavigationButton("QL Ngành & Việc làm", "category_icon.png");
        btnNavManageUsers = createNavigationButton("QL Người dùng", "users_icon.png");
        btnNavManageJobs = createNavigationButton("QL Bài đăng (Jobs)", "jobs_icon.png");
        btnLogout = createNavigationButton("Đăng xuất", "logout_icon.png");

        // Thêm nút vào navigation panel
        addNavButtonToPanel(btnNavDashboard);
        addNavButtonToPanel(btnNavManageCategories);
        addNavButtonToPanel(btnNavManageUsers);
        addNavButtonToPanel(btnNavManageJobs);
        navigationPanel.add(Box.createVerticalGlue()); // Đẩy nút logout xuống dưới cùng
        addNavButtonToPanel(btnLogout);

        // === MAIN CONTENT PANEL (Bên phải, sử dụng CardLayout) ===
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        mainContentPanel.setBackground(new Color(235, 235, 235)); // Màu nền cho nội dung chính

        // --- Khởi tạo các Panel con ---
        // 1. Dashboard Panel
        dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(Color.WHITE);
        JLabel welcomeLabel = new JLabel("Chào mừng Admin! Chọn một chức năng từ menu bên trái.", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        dashboardPanel.add(welcomeLabel, BorderLayout.CENTER);
        mainContentPanel.add(dashboardPanel, "DashboardPanel");

        // 2. Category Management Panel
        categoryManagementPanel = new CategoryManagementPanel(adminId);
        mainContentPanel.add(categoryManagementPanel, "CategoryManagementPanel");

        // 3. User Management Panel
        userManagementPanel = new UserManagementPanel(adminId);
        mainContentPanel.add(userManagementPanel, "UserManagementPanel");

        // 4. Job Approval Panel
        jobApprovalPanel = new JobApprovalPanel(adminId);
        mainContentPanel.add(jobApprovalPanel, "JobApprovalPanel");

        // === ADD PANELS TO FRAME ===
        this.setLayout(new BorderLayout(0, 0));
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(navigationPanel, BorderLayout.WEST);
        this.add(mainContentPanel, BorderLayout.CENTER);

        // === ACTION LISTENERS cho các nút điều hướng ===
        btnNavDashboard.addActionListener(this);
        btnNavManageCategories.addActionListener(this);
        btnNavManageUsers.addActionListener(this);
        btnNavManageJobs.addActionListener(this);
        btnLogout.addActionListener(this);

        pack();
        setLocationRelativeTo(null); // Hiển thị cửa sổ giữa màn hình
        setVisible(true);

        cardLayout.show(mainContentPanel, "DashboardPanel"); // Hiển thị Dashboard mặc định khi khởi động
    }

    private JButton createNavigationButton(String text, String iconName) {
        JButton button = new JButton(text);
        // Tùy chỉnh giao diện nút (ví dụ từ MainF)
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(new Color(220, 220, 220));
        button.setBackground(new Color(75, 78, 80));
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        // button.setIcon(new ImageIcon(getClass().getResource("/icons/" + iconName))); // Nếu có icon
        button.setIconTextGap(10);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height + 10));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(90, 93, 95));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 78, 80));
            }
        });
        return button;
    }

    private void addNavButtonToPanel(JButton button) {
        navigationPanel.add(button);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 8))); // Khoảng cách giữa các nút
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnNavDashboard) {
            cardLayout.show(mainContentPanel, "DashboardPanel");
        } else if (source == btnNavManageCategories) {
            cardLayout.show(mainContentPanel, "CategoryManagementPanel");
            categoryManagementPanel.loadInitialData(); // Gọi hàm tải dữ liệu khi panel được hiển thị
        } else if (source == btnNavManageUsers) {
            cardLayout.show(mainContentPanel, "UserManagementPanel");
            userManagementPanel.loadUsers(); // Tải dữ liệu người dùng
        } else if (source == btnNavManageJobs) {
            cardLayout.show(mainContentPanel, "JobApprovalPanel");
            jobApprovalPanel.loadJobsForApproval(); // Tải danh sách JOB cần duyệt
        } else if (source == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận đăng xuất",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Đóng cửa sổ Admin
                // Quay lại màn hình đăng nhập hoặc MainF với vai trò khách
                SwingUtilities.invokeLater(() -> new LoginF("Đăng nhập")); // Hoặc new MainF(...)
//                System.out.println("Admin (ID: " + adminId + ") đã đăng xuất.");
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminF("Trang Quản Trị", adminId));
    }
}