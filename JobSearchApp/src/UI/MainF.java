 package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Giả sử DBConnection.java và CategoryItem.java đã tồn tại trong cùng package
// hoặc được import đúng cách

public class MainF extends JFrame implements ActionListener {

    // ... (Các khai báo biến cũ giữ nguyên)
    private JPanel headerPanel, controlPanel, mainContentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnLogin, btnRegister, btnSetting, btnCreateJob, btnSaveJob, btnViewJobs;

    // Cho Job List (sẽ được dùng lại cho cả hiển thị job theo category và kết quả search)
    private JTable tblJobList;
    private DefaultTableModel tableModel;
    private JLabel lbCurrentJobListViewTitle; // Label để hiển thị "Việc làm ngành X" hoặc "Kết quả tìm kiếm"

    private static int user_id = -1;
    private static int role = 0;

    private CardLayout cardLayout; // CardLayout cho mainContentPanel
    private JPanel jobListDisplayPanel, savedJobsPanel, createJobPanel;
    private ProfilePanel profilePanel;
    private ChangePasswordPanel changePWPanel;

    // Components cho phần hiển thị ngành nghề và công việc trong jobListDisplayPanel
    private CardLayout jobPanelInternalCardLayout; // CardLayout bên trong jobListDisplayPanel
    private JPanel jobPanelContainer; // Panel chứa categorySubPanel và jobsSubPanel
    private JPanel categorySubPanel;
    private JPanel jobsSubPanel;
//    private JList<CategoryItem> listCategories;
//    private DefaultListModel<CategoryItem> categoryListModel;
    private JButton btnBackToCategories;


    public MainF(String title, int id, int rl) {
        super(title);
        MainF.user_id = id;
        MainF.role = rl;
        initLookAndFeel();
        GUI();
    }

    private void initLookAndFeel() {
        // ... (Giữ nguyên)
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

    public void GUI() {
        // ... (Phần headerPanel, controlPanel giữ nguyên như code trước)
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1300, 750)); // Có thể tăng chiều cao một chút

        // === HEADER PANEL ===
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 102, 204));
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lbTitle);

        // === CONTROL PANEL (Tìm kiếm và các nút chính) ===
        controlPanel = new JPanel(new BorderLayout(15, 10));
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        controlPanel.setBackground(new Color(245, 245, 245));

        JPanel searchAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAreaPanel.setOpaque(false);
        JLabel lbSearchPrompt = new JLabel("Tìm kiếm công việc:");
        lbSearchPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setPreferredSize(new Dimension(txtSearch.getPreferredSize().width, 30));
        btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBackground(new Color(60, 179, 113));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(100, 30));
        searchAreaPanel.add(lbSearchPrompt);
        searchAreaPanel.add(txtSearch);
        searchAreaPanel.add(btnSearch);
        controlPanel.add(searchAreaPanel, BorderLayout.WEST);

        JPanel buttonsGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsGroupPanel.setOpaque(false);
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        btnViewJobs = new JButton("Việc làm theo ngành"); // Đổi tên nút
        btnSaveJob = new JButton("Việc làm đã lưu");
        btnCreateJob = new JButton("Đăng tin");
        btnSetting = new JButton("Cài đặt");
        JButton[] actionButtons = {btnLogin, btnRegister, btnViewJobs, btnSaveJob, btnCreateJob, btnSetting};
        for (JButton btn : actionButtons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 220, 220));
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180)),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
            ));
            buttonsGroupPanel.add(btn);
        }
        controlPanel.add(buttonsGroupPanel, BorderLayout.EAST);


        // === MAIN CONTENT PANEL (Sử dụng CardLayout) ===
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainContentPanel.setBackground(Color.WHITE);

        // --- Panel danh sách công việc (Bao gồm chọn ngành và hiển thị việc) ---
        jobListDisplayPanel = createJobListPanel(); // Hàm này sẽ được viết lại
        mainContentPanel.add(jobListDisplayPanel, "JobListFlow"); // Đổi tên key

        // --- Các panel khác ---
        savedJobsPanel = new SavedJobsPanel(MainF.user_id);
        createJobPanel = new CreateJobPanel(MainF.user_id);
        profilePanel = new ProfilePanel(MainF.user_id);
        changePWPanel = new ChangePasswordPanel(MainF.user_id);

        mainContentPanel.add(savedJobsPanel, "SavedJobs");
        mainContentPanel.add(createJobPanel, "CreateJob");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(changePWPanel, "ChangePassword");

        // === ADD PANELS TO FRAME ===
        this.setLayout(new BorderLayout(0, 0));
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainContentPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        // === ACTION LISTENERS ===
        btnSearch.addActionListener(this);
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        btnSetting.addActionListener(this);
        btnCreateJob.addActionListener(this);
        btnSaveJob.addActionListener(this);
        btnViewJobs.addActionListener(this);

        updateButtonVisibility();
        JPopupMenu menu = createSettingMenu();
        btnSetting.addActionListener(e -> menu.show(btnSetting, 0, -menu.getPreferredSize().height));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Mặc định hiển thị danh sách ngành nghề khi khởi động hoặc khi nhấn "Việc làm theo ngành"
        cardLayout.show(mainContentPanel, "JobListFlow");
        jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView");
        loadCategoriesData();
    }

    private void updateButtonVisibility() { /* Giữ nguyên */
        boolean isLoggedIn = (MainF.role > 0);
        boolean isGuest = (MainF.role == 0);
        boolean isHR = (MainF.role == 2);
        boolean isUser = (MainF.role == 3);

        btnLogin.setVisible(isGuest);
        btnRegister.setVisible(isGuest);
        btnSetting.setVisible(isLoggedIn);
        btnSaveJob.setVisible(isUser || isHR);
        btnCreateJob.setVisible(isHR);
        btnViewJobs.setVisible(true);
    }

    // ** HÀM NÀY ĐƯỢC VIẾT LẠI HOÀN TOÀN **
    private JPanel createJobListPanel() {
        JPanel mainPanelForJobs = new JPanel(new BorderLayout(0,10)); // Panel chính trả về
        mainPanelForJobs.setOpaque(false);

        jobPanelInternalCardLayout = new CardLayout();
        jobPanelContainer = new JPanel(jobPanelInternalCardLayout); // Panel dùng CardLayout nội bộ
        jobPanelContainer.setOpaque(false);

        // 1. Sub-panel hiển thị danh sách ngành nghề (CategoriesView)
        categorySubPanel = new JPanel(new BorderLayout(10,10));
        categorySubPanel.setOpaque(false);
        categorySubPanel.setBorder(new EmptyBorder(5,5,5,5));

        JLabel categoryTitle = new JLabel("Chọn một ngành nghề để xem việc làm:", SwingConstants.LEFT);
        categoryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        categorySubPanel.add(categoryTitle, BorderLayout.NORTH);

//        categoryListModel = new DefaultListModel<>();
//        listCategories = new JList<>(categoryListModel);
//        listCategories.setFont(new Font("Segoe UI", Font.PLAIN, 15));
//        listCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        listCategories.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
//        listCategories.addListSelectionListener(new ListSelectionListener() {
//            @Override
//            public void valueChanged(ListSelectionEvent e) {
//                if (!e.getValueIsAdjusting() && listCategories.getSelectedValue() != null) {
//                    CategoryItem selectedCategory = listCategories.getSelectedValue();
//                    loadJobsForCategory(selectedCategory.getId(), selectedCategory.getName());
//                    jobPanelInternalCardLayout.show(jobPanelContainer, "JobsView");
//                }
//            }
//        });
//        categorySubPanel.add(new JScrollPane(listCategories), BorderLayout.CENTER);
        jobPanelContainer.add(categorySubPanel, "CategoriesView");


        // 2. Sub-panel hiển thị danh sách công việc (JobsView)
        jobsSubPanel = new JPanel(new BorderLayout(0, 10));
        jobsSubPanel.setOpaque(false);
        jobsSubPanel.setBorder(new EmptyBorder(5,5,5,5));

        // Panel chứa tiêu đề và nút Back
        JPanel jobsHeaderPanel = new JPanel(new BorderLayout());
        jobsHeaderPanel.setOpaque(false);
        lbCurrentJobListViewTitle = new JLabel("Danh sách việc làm", SwingConstants.LEFT); // Sẽ được cập nhật
        lbCurrentJobListViewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jobsHeaderPanel.add(lbCurrentJobListViewTitle, BorderLayout.CENTER);

        btnBackToCategories = new JButton("◀ Quay lại chọn ngành");
        btnBackToCategories.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBackToCategories.addActionListener(e -> {
            jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView");
//            listCategories.clearSelection(); // Bỏ chọn item cũ
        });
        jobsHeaderPanel.add(btnBackToCategories, BorderLayout.EAST);
        jobsSubPanel.add(jobsHeaderPanel, BorderLayout.NORTH);

        // Table hiển thị công việc (tái sử dụng từ code cũ)
        String[] columnNames = {"Vị trí", "Công ty", "Mức lương", "Địa điểm", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblJobList = new JTable(tableModel);
        // ... (các cài đặt cho tblJobList như setFont, setRowHeight, getTableHeader, etc. giữ nguyên)
        tblJobList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblJobList.setRowHeight(30);
        tblJobList.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblJobList.getTableHeader().setBackground(new Color(230, 230, 230));
        tblJobList.getTableHeader().setForeground(new Color(50, 50, 50));
        tblJobList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblJobList.setFillsViewportHeight(true);
        tblJobList.setGridColor(new Color(200, 200, 200));
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 5, 0, 5));
        for (int i = 0; i < tblJobList.getColumnCount(); i++) {
            if (tblJobList.getColumnModel().getColumnCount() > i) // Kiểm tra tồn tại cột
                 tblJobList.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(tblJobList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        jobsSubPanel.add(scrollPane, BorderLayout.CENTER);
        jobPanelContainer.add(jobsSubPanel, "JobsView");

        mainPanelForJobs.add(jobPanelContainer, BorderLayout.CENTER);
        return mainPanelForJobs;
    }

    private void loadCategoriesData() {
//        categoryListModel.clear();
//        String sql = "SELECT category_id, category_name FROM category ORDER BY category_name";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//            while (rs.next()) {
//                categoryListModel.addElement(new CategoryItem(rs.getInt("category_id"), rs.getString("category_name")));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách ngành nghề: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void loadJobsForCategory(int categoryId, String categoryName) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        lbCurrentJobListViewTitle.setText("Việc làm trong ngành: " + categoryName);
        btnBackToCategories.setVisible(true); // Hiển thị nút back khi xem job theo category

//        String sql = "SELECT j.job_id, j.job_name, j.salary, j.company_name, j.description, j.address " +
//                     "FROM job j " +
//                     "JOIN categoryofjob coj ON j.job_id = coj.job_id " +
//                     "WHERE coj.category_id = ? AND j.is_public = 1"; // Chỉ lấy job public
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, categoryId);
//            ResultSet rs = pstmt.executeQuery();
//            boolean found = false;
//            while (rs.next()) {
//                found = true;
//                tableModel.addRow(new Object[]{
//                        rs.getString("job_name"),
//                        rs.getString("company_name"),
//                        rs.getBigDecimal("salary") != null ? rs.getBigDecimal("salary").toString() + " (triệu)" : "Thỏa thuận",
//                        rs.getString("address"),
//                        rs.getString("description")
//                });
//            }
//            if (!found) {
//                 tableModel.addRow(new Object[]{"Không có việc làm nào trong ngành này.", "", "", "", ""});
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi tải danh sách việc làm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private JPopupMenu createSettingMenu() { /* Giữ nguyên */
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        menu.setBackground(new Color(250,250,250));

        JMenuItem miProfile = new JMenuItem("Thông tin cá nhân");
        JMenuItem miChangePass = new JMenuItem("Đổi mật khẩu");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");

        Font menuFont = new Font("Segoe UI", Font.PLAIN, 13);
        Color menuItemFgColor = new Color(50,50,50);

        miProfile.setFont(menuFont); miProfile.setForeground(menuItemFgColor);
        miChangePass.setFont(menuFont); miChangePass.setForeground(menuItemFgColor);
        miLogout.setFont(menuFont); miLogout.setForeground(menuItemFgColor);

        menu.add(miProfile);
        menu.add(miChangePass);
        menu.addSeparator();
        menu.add(miLogout);

        miProfile.addActionListener(e -> {
            if (MainF.user_id == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập.", "Yêu cầu", JOptionPane.INFORMATION_MESSAGE); return;
            }
            profilePanel = new ProfilePanel(MainF.user_id); // Refresh panel
            if (mainContentPanel.getLayout() instanceof CardLayout) { // Ensure profilePanel is added if not present
                boolean found = false;
                for (Component comp : mainContentPanel.getComponents()) {
                    if (comp == profilePanel) {
                        found = true;
                        break;
                    }
                }
                if (!found) mainContentPanel.add(profilePanel, "Profile");
            }
            cardLayout.show(mainContentPanel, "Profile");
        });
        miChangePass.addActionListener(e -> {
            if (MainF.user_id == -1) {
                 JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập.", "Yêu cầu", JOptionPane.INFORMATION_MESSAGE); return;
            }
            changePWPanel = new ChangePasswordPanel(MainF.user_id); // Refresh panel
            if (mainContentPanel.getLayout() instanceof CardLayout) { // Ensure changePWPanel is added if not present
                boolean found = false;
                for (Component comp : mainContentPanel.getComponents()) {
                    if (comp == changePWPanel) {
                        found = true;
                        break;
                    }
                }
                if (!found) mainContentPanel.add(changePWPanel, "ChangePassword");
            }
            cardLayout.show(mainContentPanel, "ChangePassword");
        });

        miLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                MainF.user_id = -1;
                MainF.role = 0;
                this.dispose();
                SwingUtilities.invokeLater(() -> new MainF("Ứng dụng Tìm việc làm - Khách", -1, 0));
            }
        });
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnLogin) { /* Giữ nguyên */
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginF("Đăng nhập"));
        } else if (source == btnRegister) { /* Giữ nguyên */
            this.dispose();
            SwingUtilities.invokeLater(() -> new RegisterF("Đăng ký"));
        } else if (source == btnSearch) {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Yêu cầu", JOptionPane.WARNING_MESSAGE);
                txtSearch.requestFocus();
                return;
            }
            cardLayout.show(mainContentPanel, "JobListFlow"); // Chuyển sang panel chứa luồng job
            performSearch(keyword); // Hàm tìm kiếm sẽ trực tiếp hiển thị kết quả vào job table
            jobPanelInternalCardLayout.show(jobPanelContainer, "JobsView"); // Hiển thị view job
        } else if (source == btnSaveJob) { /* Giữ nguyên */
             if (MainF.role == 0) {
                 JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập.", "Yêu cầu", JOptionPane.INFORMATION_MESSAGE); return;
            }
            cardLayout.show(mainContentPanel, "SavedJobs");
        } else if (source == btnCreateJob) { /* Giữ nguyên */
            if (MainF.role != 2) {
                 JOptionPane.showMessageDialog(this, "Chỉ Nhà tuyển dụng mới có quyền đăng tin.", "Không có quyền", JOptionPane.WARNING_MESSAGE); return;
            }
            cardLayout.show(mainContentPanel, "CreateJob");
        } else if (source == btnViewJobs) { // Nút "Việc làm theo ngành"
            cardLayout.show(mainContentPanel, "JobListFlow");
            jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView"); // Hiển thị view chọn ngành
            loadCategoriesData(); // Tải lại danh sách ngành nghề
            txtSearch.setText("");
        }
    }

    private void performSearch(String keyword) {
        tableModel.setRowCount(0);
        lbCurrentJobListViewTitle.setText("Kết quả tìm kiếm cho: \"" + keyword + "\"");
        btnBackToCategories.setVisible(true); // Cho phép quay lại danh sách ngành

//        // Câu SQL tìm kiếm (ví dụ đơn giản, bạn có thể làm phức tạp hơn với LIKE trên nhiều cột)
//        String sql = "SELECT job_id, job_name, salary, company_name, description, address " +
//                     "FROM job " +
//                     "WHERE (job_name LIKE ? OR company_name LIKE ? OR description LIKE ? OR requirement LIKE ? OR address LIKE ?) " +
//                     "AND is_public = 1";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            String searchTerm = "%" + keyword + "%";
//            for (int i = 1; i <= 5; i++) { // 5 placeholders cho LIKE
//                pstmt.setString(i, searchTerm);
//            }
//            ResultSet rs = pstmt.executeQuery();
//            boolean found = false;
//            while (rs.next()) {
//                found = true;
//                tableModel.addRow(new Object[]{
//                    rs.getString("job_name"),
//                    rs.getString("company_name"),
//                    rs.getBigDecimal("salary") != null ? rs.getBigDecimal("salary").toString() + " (triệu)" : "Thỏa thuận",
//                    rs.getString("address"),
//                    rs.getString("description")
//                });
//            }
//            if (!found) {
//                tableModel.addRow(new Object[]{"Không tìm thấy công việc nào với từ khóa '" + keyword + "'.", "", "", "", ""});
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        }
    }

    public static void main(String[] args) { /* Giữ nguyên */
        SwingUtilities.invokeLater(() -> {
            int initialUserId = 102;
            int initialRole = 2;
            String frameTitle = "Ứng dụng Tìm việc làm";
            switch (initialRole) {
                case 0: frameTitle += " - Khách"; break;
                case 2: frameTitle += " - Nhà tuyển dụng"; break;
                case 3: frameTitle += " - Người tìm việc"; break;
                case 1: frameTitle += " - Quản trị viên"; break;
            }
            new MainF(frameTitle, initialUserId, initialRole);
        });
    }
}