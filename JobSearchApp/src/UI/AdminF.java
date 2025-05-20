package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import BLL.*;
import DTO.*;

public class AdminF extends JFrame implements ActionListener {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JPanel dashboardPanel;
    private UserManagementPanel userManagementPanel;
    private JobApprovalPanel jobApprovalPanel;
    private JButton btnNavDashboard, btnNavManageUsers, btnNavManageJobs, btnLogout;
    private JPanel navigationPanel;
    private JLabel lbHeaderTitle;
    private static int adminId;

    public AdminF(String title, int loggedInAdminId) {
        super(title);
        AdminF.adminId = loggedInAdminId;
        initLookAndFeel();
        initializeUI();
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1280, 720));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        lbHeaderTitle = new JLabel("TRANG QUẢN TRỊ HỆ THỐNG");
        lbHeaderTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbHeaderTitle.setForeground(Color.WHITE);
        headerPanel.add(lbHeaderTitle, BorderLayout.WEST);

        // Navigation Panel
        navigationPanel = new JPanel();
        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.Y_AXIS));
        navigationPanel.setBackground(new Color(240, 240, 240));
        navigationPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 10, 0)
        ));
        navigationPanel.setPreferredSize(new Dimension(250, 0));

        // Tạo các nút điều hướng
        btnNavDashboard = createNavButton("Bảng điều khiển", "dashboard");
        btnNavManageUsers = createNavButton("Quản lý người dùng", "users");
        btnNavManageJobs = createNavButton("Quản lý bài đăng", "jobs");
        btnLogout = createNavButton("Đăng xuất", "logout");

        // Thêm nút vào navigation panel
        addNavButtonToPanel(btnNavDashboard);
        addNavButtonToPanel(btnNavManageUsers);
        addNavButtonToPanel(btnNavManageJobs);
        navigationPanel.add(Box.createVerticalGlue());
        addNavButtonToPanel(btnLogout);

        // Main Content Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Dashboard Panel
        dashboardPanel = createDashboardPanel();
        mainContentPanel.add(dashboardPanel, "DashboardPanel");

        // User Management Panel
        userManagementPanel = new UserManagementPanel(adminId);
        mainContentPanel.add(userManagementPanel, "UserManagementPanel");

        // Job Approval Panel
        jobApprovalPanel = new JobApprovalPanel(adminId);
        mainContentPanel.add(jobApprovalPanel, "JobApprovalPanel");

        // Layout
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.WEST);
        add(mainContentPanel, BorderLayout.CENTER);

        // Action Listeners
        btnNavDashboard.addActionListener(this);
        btnNavManageUsers.addActionListener(this);
        btnNavManageJobs.addActionListener(this);
        btnLogout.addActionListener(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Hiển thị dashboard mặc định
        cardLayout.show(mainContentPanel, "DashboardPanel");
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Thống kê tổng quan
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(Color.WHITE);

        // Card thống kê người dùng
        JPanel userStatsCard = createStatCard(
            "Tổng số người dùng",
            "0",
            new Color(0, 102, 204),
            "users"
        );
        statsPanel.add(userStatsCard);

        // Card thống kê bài đăng
        JPanel jobStatsCard = createStatCard(
            "Tổng số bài đăng",
            "0",
            new Color(40, 167, 69),
            "jobs"
        );
        statsPanel.add(jobStatsCard);

        // Card bài đăng chờ duyệt
        JPanel pendingJobCard = createStatCard(
            "Bài đăng chờ duyệt",
            "0",
            new Color(255, 193, 7),
            "pending"
        );
        statsPanel.add(pendingJobCard);

        // Card ứng viên mới
        JPanel newApplicantCard = createStatCard(
            "Ứng viên mới",
            "0",
            new Color(220, 53, 69),
            "applicants"
        );
        statsPanel.add(newApplicantCard);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(statsPanel, gbc);

        // Thêm các thành phần khác vào dashboard nếu cần
        // ...

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color, String iconName) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private JButton createNavButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(50, 50, 50));
        button.setBackground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(230, 40));
        button.setMaximumSize(new Dimension(230, 40));

        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        return button;
    }

    private void addNavButtonToPanel(JButton button) {
        navigationPanel.add(button);
        navigationPanel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnNavDashboard) {
            cardLayout.show(mainContentPanel, "DashboardPanel");
        } else if (source == btnNavManageUsers) {
            cardLayout.show(mainContentPanel, "UserManagementPanel");
            userManagementPanel.loadUsers();
        } else if (source == btnNavManageJobs) {
            cardLayout.show(mainContentPanel, "JobApprovalPanel");
            jobApprovalPanel.loadJobs();
        } else if (source == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận đăng xuất",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginF("Đăng nhập");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminF("Trang Quản Trị", adminId));
    }
}