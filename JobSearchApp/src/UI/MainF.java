package UI;

import BLL.*;
import DTO.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*; // Cần cho JTable và TableModel
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class MainF extends JFrame implements ActionListener {

    private JPanel headerPanel, controlPanel, mainContentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnLogin, btnRegister, btnSetting, btnCreateJob, btnSaveJob, btnViewJobs;

    private JTable tblJobList; // Bảng hiển thị công việc (giữ nguyên)
    private DefaultTableModel tableModel; // Model cho bảng công việc (giữ nguyên)
    private JLabel lbCurrentJobListViewTitle;

    private static int user_id = -1;
    private static int role = 0;

    private CardLayout cardLayout;
    private JPanel jobListDisplayPanel, savedJobsPanel, createJobPanel;
    private ProfilePanel profilePanel;
    private ChangePasswordPanel changePWPanel;

    private CardLayout jobPanelInternalCardLayout;
    private JPanel jobPanelContainer;
    private JPanel categorySubPanel;
    private JPanel jobsSubPanel;

    // --- THAY ĐỔI: Từ JList sang JTable để hiển thị ngành nghề ---
    // private JList<CategoryDTO> listCategories;
    // private DefaultListModel<CategoryDTO> categoryListModel;
    private JTable tblCategories; // MỚI: Bảng hiển thị ngành nghề
    private DefaultTableModel categoryTableModel; // MỚI: Model cho bảng ngành nghề
    // --- KẾT THÚC THAY ĐỔI ---
    private JButton btnBackToCategories;

    private CategoryBLL categoryBLL;
    private JobBLL jobBLL;

    public MainF(String title, int id, int rl) {
        super(title);
        MainF.user_id = id;
        MainF.role = rl;

        try {
            categoryBLL = new CategoryBLL();
            jobBLL = new JobBLL();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo lớp xử lý nghiệp vụ: " + e.getMessage() +
                "\nỨng dụng có thể không hoạt động đúng.",
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }

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
        // ... (Các phần headerPanel, controlPanel, mainContentPanel khởi tạo giữ nguyên) ...
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1300, 750));

        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 102, 204));
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lbTitle);

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
        btnViewJobs = new JButton("Việc làm theo ngành");
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

        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainContentPanel.setBackground(Color.WHITE);

        jobListDisplayPanel = createJobListPanel();
        mainContentPanel.add(jobListDisplayPanel, "JobListFlow");

        savedJobsPanel =  new SavedJobsPanel(MainF.user_id);
        createJobPanel =  new CreateJobPanel(MainF.user_id);
        profilePanel =  new ProfilePanel(MainF.user_id);
        changePWPanel =  new ChangePasswordPanel(MainF.user_id);

        mainContentPanel.add(savedJobsPanel, "SavedJobs");
        mainContentPanel.add(createJobPanel, "CreateJob");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(changePWPanel, "ChangePassword");

        this.setLayout(new BorderLayout(0, 0));
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(mainContentPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(this);
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        btnSetting.addActionListener(this);
        btnCreateJob.addActionListener(this);
        btnSaveJob.addActionListener(this);
        btnViewJobs.addActionListener(this);

        updateButtonVisibility();
        JPopupMenu menu = createSettingMenu();
        btnSetting.addActionListener(e -> {
            if (btnSetting.isVisible()){
                 menu.show(btnSetting, 0, -menu.getPreferredSize().height);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        cardLayout.show(mainContentPanel, "JobListFlow");
        jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView");
        loadCategoriesData();
    }

    private void updateButtonVisibility() {
        // ... (Giữ nguyên)
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

    // SỬA ĐỔI PHƯƠNG THỨC NÀY
    private JPanel createJobListPanel() {
        JPanel mainPanelForJobs = new JPanel(new BorderLayout(0, 10));
        mainPanelForJobs.setOpaque(false);

        jobPanelInternalCardLayout = new CardLayout();
        jobPanelContainer = new JPanel(jobPanelInternalCardLayout);
        jobPanelContainer.setOpaque(false);

        // 1. Sub-panel hiển thị danh sách ngành nghề (CategoriesView) - SỬ DỤNG JTABLE
        categorySubPanel = new JPanel(new BorderLayout(10, 10));
        categorySubPanel.setOpaque(false);
        categorySubPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel categoryTitle = new JLabel("Chọn một ngành nghề để xem việc làm:", SwingConstants.LEFT);
        categoryTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        categorySubPanel.add(categoryTitle, BorderLayout.NORTH);

        // --- BẮT ĐẦU THAY ĐỔI: Sử dụng JTable cho Categories ---
        String[] categoryColumnNames = {"ID Ngành", "Tên Ngành Nghề"}; // Các cột cho bảng ngành nghề
        categoryTableModel = new DefaultTableModel(categoryColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép sửa trực tiếp trên bảng
            }
        };
        tblCategories = new JTable(categoryTableModel);
        tblCategories.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // Điều chỉnh font nếu cần
        tblCategories.setRowHeight(28); // Điều chỉnh chiều cao hàng
        tblCategories.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Chỉ cho chọn 1 hàng
        tblCategories.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblCategories.getTableHeader().setBackground(new Color(230, 230, 230));
        tblCategories.getTableHeader().setForeground(new Color(50, 50, 50));
        tblCategories.setFillsViewportHeight(true); // Đảm bảo bảng tô đầy không gian nếu ít hàng

        // Tùy chỉnh độ rộng cột (ví dụ)
        TableColumnModel categoryTCM = tblCategories.getColumnModel();
        categoryTCM.getColumn(0).setPreferredWidth(80);  // Cột ID
        categoryTCM.getColumn(0).setMaxWidth(100);
        categoryTCM.getColumn(1).setPreferredWidth(350); // Cột Tên Ngành

        // Thêm sự kiện lắng nghe lựa chọn hàng trên bảng
        tblCategories.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Đảm bảo sự kiện chỉ được xử lý một lần và có hàng được chọn
                if (!e.getValueIsAdjusting() && tblCategories.getSelectedRow() != -1) {
                    int selectedRow = tblCategories.getSelectedRow();
                    // Lấy categoryId và categoryName từ hàng được chọn trong table model
                    // Giả sử cột 0 là ID, cột 1 là Name
                    int categoryId = (int) categoryTableModel.getValueAt(selectedRow, 0);
                    String categoryName = (String) categoryTableModel.getValueAt(selectedRow, 1);

                    loadJobsForCategory(categoryId, categoryName);
                    jobPanelInternalCardLayout.show(jobPanelContainer, "JobsView");
                }
            }
        });

        JScrollPane categoryScrollPane = new JScrollPane(tblCategories);
        categoryScrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        categorySubPanel.add(categoryScrollPane, BorderLayout.CENTER);
        // --- KẾT THÚC THAY ĐỔI ---

        jobPanelContainer.add(categorySubPanel, "CategoriesView");

        // 2. Sub-panel hiển thị danh sách công việc (JobsView) - Giữ nguyên
        jobsSubPanel = new JPanel(new BorderLayout(0, 10));
        jobsSubPanel.setOpaque(false);
        jobsSubPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel jobsHeaderPanel = new JPanel(new BorderLayout());
        jobsHeaderPanel.setOpaque(false);
        lbCurrentJobListViewTitle = new JLabel("Danh sách việc làm", SwingConstants.LEFT);
        lbCurrentJobListViewTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jobsHeaderPanel.add(lbCurrentJobListViewTitle, BorderLayout.CENTER);

        btnBackToCategories = new JButton("◀ Quay lại chọn ngành");
        btnBackToCategories.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnBackToCategories.addActionListener(e -> {
            jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView");
            txtSearch.setText("");
            tblCategories.clearSelection(); // Bỏ chọn trên bảng ngành nghề
        });
        jobsHeaderPanel.add(btnBackToCategories, BorderLayout.EAST);
        jobsSubPanel.add(jobsHeaderPanel, BorderLayout.NORTH);

        // Cấu hình bảng công việc (tblJobList) - Giữ nguyên
        String[] columnNames = {"Vị trí", "Công ty", "Mức lương", "Địa điểm", "Mô tả"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblJobList = new JTable(tableModel);
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
            if (tblJobList.getColumnModel().getColumnCount() > i)
                 tblJobList.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        TableColumnModel jobColumnModel = tblJobList.getColumnModel();
        jobColumnModel.getColumn(0).setPreferredWidth(200);
        jobColumnModel.getColumn(1).setPreferredWidth(150);
        jobColumnModel.getColumn(2).setPreferredWidth(100);
        jobColumnModel.getColumn(3).setPreferredWidth(150);
        jobColumnModel.getColumn(4).setPreferredWidth(300);
        tblJobList.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(tblJobList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        jobsSubPanel.add(scrollPane, BorderLayout.CENTER);
        jobPanelContainer.add(jobsSubPanel, "JobsView");

        mainPanelForJobs.add(jobPanelContainer, BorderLayout.CENTER);
        return mainPanelForJobs;
    }

    // SỬA ĐỔI PHƯƠNG THỨC NÀY
    private void loadCategoriesData() {
        if (categoryBLL == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: CategoryBLL chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // --- THAY ĐỔI: Đổ dữ liệu vào categoryTableModel ---
        categoryTableModel.setRowCount(0); // Xóa dữ liệu cũ trên bảng ngành nghề

        try {
            List<CategoryDTO> listCategory = categoryBLL.getAllCategories();
            if (listCategory != null && !listCategory.isEmpty()) {
                for (CategoryDTO categoryDTO : listCategory) {
                    // Thêm một hàng mới vào bảng ngành nghề
                    // Đảm bảo thứ tự khớp với categoryColumnNames
                    categoryTableModel.addRow(new Object[]{
                        categoryDTO.getCategoryId(),    // ID Ngành
                        categoryDTO.getCategoryName()   // Tên Ngành Nghề
                    });
                }
            } else if (listCategory == null) {
                 JOptionPane.showMessageDialog(this, "Không thể tải danh sách ngành nghề. Có lỗi xảy ra.", "Lỗi tải dữ liệu", JOptionPane.WARNING_MESSAGE);
            }
            // Nếu listCategory rỗng, bảng sẽ trống, không cần thông báo đặc biệt.
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách ngành nghề: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
        // --- KẾT THÚC THAY ĐỔI ---
    }

    private void loadJobsForCategory(int categoryId, String categoryName) {
        if (jobBLL == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: JobBLL chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);
        lbCurrentJobListViewTitle.setText("Việc làm trong ngành: " + categoryName); // Bạn có thể thêm ID nếu muốn: + " (ID: " + categoryId + ")"
        btnBackToCategories.setVisible(true);

        try {
            List<JobDTO> jobs = jobBLL.searchJobs(categoryName);
            if (jobs != null && !jobs.isEmpty()) {
                for (JobDTO job : jobs) {
                    tableModel.addRow(new Object[]{
                            job.getJobName(),
                            job.getCompanyName(),
                            job.getSalary(),
                            job.getAddress(),
                            job.getDescription()
                    });
                }
            } else if (jobs == null) {
                JOptionPane.showMessageDialog(this, "Không thể tải danh sách việc làm. Có lỗi xảy ra.", "Lỗi tải dữ liệu", JOptionPane.WARNING_MESSAGE);
                tableModel.addRow(new Object[]{"Lỗi tải dữ liệu.", "", "", "", ""});
            }
            else {
                 tableModel.addRow(new Object[]{"Không có việc làm nào trong ngành này.", "", "", "", ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách việc làm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
             tableModel.addRow(new Object[]{"Lỗi tải dữ liệu: " + e.getMessage(), "", "", "", ""});
        }
    }

    private JPopupMenu createSettingMenu() {
        // ... (Giữ nguyên)
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        menu.setBackground(new Color(250,250,250));

        JMenuItem miProfile = new JMenuItem("Thông tin cá nhân");
        JMenuItem miChangePass = new JMenuItem("Đổi mật khẩu");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");

        Font menuFont = new Font("Segoe UI", Font.PLAIN, 24);
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
            profilePanel = new ProfilePanel(MainF.user_id);
            mainContentPanel.add(profilePanel, "Profile");
            cardLayout.show(mainContentPanel, "Profile");
        });
        miChangePass.addActionListener(e -> {
            if (MainF.user_id == -1) {
                 JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập.", "Yêu cầu", JOptionPane.INFORMATION_MESSAGE); return;
            }
            changePWPanel = new ChangePasswordPanel(MainF.user_id);
            mainContentPanel.add(changePWPanel, "ChangePassword");
            cardLayout.show(mainContentPanel, "ChangePassword");
        });

        miLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                SwingUtilities.invokeLater(() -> new MainF("Ứng dụng Tìm việc làm - Khách", -1, 0));
            }
        });
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == btnLogin) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginF("Đăng nhập"));
        } else if (source == btnRegister) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new RegisterF("Đăng ký"));
        } else if (source == btnSearch) {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa.", "Yêu cầu", JOptionPane.WARNING_MESSAGE);
                txtSearch.requestFocus();
                return;
            }
            cardLayout.show(mainContentPanel, "JobListFlow");
            performSearch(keyword);
            jobPanelInternalCardLayout.show(jobPanelContainer, "JobsView");
        } else if (source == btnSaveJob) {
             if (MainF.role == 0) {
                 JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để sử dụng chức năng này.", "Yêu cầu", JOptionPane.INFORMATION_MESSAGE); return;
            }
            cardLayout.show(mainContentPanel, "SavedJobs");
        } else if (source == btnCreateJob) {
            if (MainF.role != 2) {
                 JOptionPane.showMessageDialog(this, "Chỉ Nhà tuyển dụng mới có quyền đăng tin.", "Không có quyền", JOptionPane.WARNING_MESSAGE); return;
            }
            cardLayout.show(mainContentPanel, "CreateJob");
        } else if (source == btnViewJobs) {
            cardLayout.show(mainContentPanel, "JobListFlow");
            jobPanelInternalCardLayout.show(jobPanelContainer, "CategoriesView");
            loadCategoriesData(); // Tải lại dữ liệu vào bảng ngành nghề
            txtSearch.setText("");
            tblCategories.clearSelection(); // Bỏ chọn trên bảng ngành nghề
        }
    }

    private void performSearch(String keyword) {
        if (jobBLL == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: JobBLL chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        tableModel.setRowCount(0);
        lbCurrentJobListViewTitle.setText("Kết quả tìm kiếm cho: \"" + keyword + "\"");
        // btnBackToCategories.setVisible(true); // Có thể ẩn hoặc thay đổi chức năng nút này sau khi tìm kiếm

        try {
            List<JobDTO> jobs = jobBLL.searchJobs(keyword);
            if (jobs != null && !jobs.isEmpty()) {
                for (JobDTO job : jobs) {
                    tableModel.addRow(new Object[]{
                        job.getJobName(),
                        job.getCompanyName(),
                        job.getSalary(),
                        job.getAddress(),
                        job.getDescription()
                    });
                }
            } else if (jobs == null) {
                 JOptionPane.showMessageDialog(this, "Lỗi khi thực hiện tìm kiếm.", "Lỗi tìm kiếm", JOptionPane.WARNING_MESSAGE);
                 tableModel.addRow(new Object[]{"Lỗi tìm kiếm.", "", "", "", ""});
            }
            else {
                tableModel.addRow(new Object[]{"Không tìm thấy công việc nào với từ khóa '" + keyword + "'.", "", "", "", ""});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage(), "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            tableModel.addRow(new Object[]{"Lỗi hệ thống: " + e.getMessage(), "", "", "", ""});
        }
    }

    public static void main(String[] args) {
    	String frameTitle = "Ứng dụng Tìm việc làm";
        switch (role) {
    	case 0: frameTitle += " - Khách"; break;
    	case 2: frameTitle += " - Nhà tuyển dụng"; break;
    	case 3: frameTitle += " - Người tìm việc"; break;
    	case 1: frameTitle += " - Quản trị viên"; break;
    	}
        new MainF(frameTitle, user_id, role);
    }
}