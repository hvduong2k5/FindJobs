package UI;

import BLL.*;
import DTO.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import Util.Response;

public class JobSeekerMainF extends JFrame implements ActionListener {
    private JPanel headerPanel, controlPanel, mainContentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnSetting, btnSaveJob, btnViewJobs;
    
    private CardLayout cardLayout;
    private JPanel categoryListPanel, jobListPanel, savedJobsPanel, appliedJobsPanel;
    private JTable tblCategories, tblJobs, tblSavedJobs, tblAppliedJobs;
    private DefaultTableModel categoryTableModel, jobTableModel, savedJobTableModel, appliedJobTableModel;
    private ProfilePanel profilePanel;
    private ChangePasswordPanel changePWPanel;
    
    private JobBLL jobBLL;
    private CategoryBLL categoryBLL;
    private UserSaveJobBLL userSaveJobBLL;
    private UserApplyJobBLL userApplyJobBLL;
    private int userId;
    private int selectedCategoryId = -1;
    
    public JobSeekerMainF(int userId) {
        super("Ứng dụng tìm kiếm việc làm - Người tìm việc");
        this.userId = UserSession.GetUser().getUser_id();
        
        try {
            jobBLL = new JobBLL();
            categoryBLL = new CategoryBLL();
            userSaveJobBLL = new UserSaveJobBLL();
            userApplyJobBLL = new UserApplyJobBLL();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo lớp xử lý nghiệp vụ: " + e.getMessage(),
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }
        
        initLookAndFeel();
        initComponents();
        setupLayout();
        addEventListeners();
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
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1300, 750));
        
        // Header Panel
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 102, 204));
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lbTitle);
        
        // Control Panel
        controlPanel = new JPanel(new BorderLayout(15, 10));
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        controlPanel.setBackground(new Color(245, 245, 245));
        
        // Search Area (ẩn ban đầu)
        JPanel searchAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAreaPanel.setOpaque(false);
        JLabel lbSearchPrompt = new JLabel("Tìm kiếm công việc:");
        lbSearchPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBackground(new Color(60, 179, 113));
        btnSearch.setForeground(Color.WHITE);
        searchAreaPanel.add(lbSearchPrompt);
        searchAreaPanel.add(txtSearch);
        searchAreaPanel.add(btnSearch);
        searchAreaPanel.setVisible(false);
        
        // Buttons Group
        JPanel buttonsGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsGroupPanel.setOpaque(false);
        btnViewJobs = new JButton("Việc làm theo ngành");
        btnSaveJob = new JButton("Việc làm đã lưu");
        btnSetting = new JButton("Cài đặt");
        
        JButton[] actionButtons = {btnViewJobs, btnSaveJob, btnSetting};
        for (JButton btn : actionButtons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 220, 220));
            buttonsGroupPanel.add(btn);
        }
        
        controlPanel.add(searchAreaPanel, BorderLayout.WEST);
        controlPanel.add(buttonsGroupPanel, BorderLayout.EAST);
        
        // Main Content Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainContentPanel.setBackground(Color.WHITE);
        
        // Initialize panels
        categoryListPanel = createCategoryListPanel();
        jobListPanel = createJobListPanel();
        savedJobsPanel = createSavedJobsPanel();
        appliedJobsPanel = createAppliedJobsPanel();
        profilePanel = new ProfilePanel(userId);
        changePWPanel = new ChangePasswordPanel(userId);
        
        mainContentPanel.add(categoryListPanel, "CategoryList");
        mainContentPanel.add(jobListPanel, "JobList");
        mainContentPanel.add(savedJobsPanel, "SavedJobs");
        mainContentPanel.add(appliedJobsPanel, "AppliedJobs");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(changePWPanel, "ChangePassword");
    }
    
    private JPanel createCategoryListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("DANH SÁCH NGÀNH NGHỀ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Category Table
        String[] columns = {"ID", "Tên ngành"};
        categoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCategories = new JTable(categoryTableModel);
        styleTable(tblCategories);
        
        // Chỉ hiển thị cột tên ngành
        tblCategories.getColumnModel().getColumn(0).setMinWidth(0);
        tblCategories.getColumnModel().getColumn(0).setMaxWidth(0);
        tblCategories.getColumnModel().getColumn(0).setWidth(0);
        
        tblCategories.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategories.getSelectedRow() != -1) {
                selectedCategoryId = Integer.parseInt(tblCategories.getValueAt(tblCategories.getSelectedRow(), 0).toString());
                loadJobsByCategory(selectedCategoryId);
                cardLayout.show(mainContentPanel, "JobList");
                controlPanel.getComponent(0).setVisible(true); // Hiện thanh tìm kiếm
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblCategories);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load categories
        loadCategories();
        
        return panel;
    }
    
    private JPanel createJobListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "CategoryList");
            controlPanel.getComponent(0).setVisible(false); // Ẩn thanh tìm kiếm
        });
        
        JLabel titleLabel = new JLabel("DANH SÁCH VIỆC LÀM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        titlePanel.add(btnBack, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Job Table
        String[] columns = {"ID", "Tiêu đề", "Công ty", "Mức lương", "Địa điểm", "Lưu", "Ứng tuyển"};
        jobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // Chỉ cho phép chỉnh sửa cột Lưu và Ứng tuyển
            }
        };
        tblJobs = new JTable(jobTableModel);
        styleTable(tblJobs);
        
        // Ẩn cột ID
        tblJobs.getColumnModel().getColumn(0).setMinWidth(0);
        tblJobs.getColumnModel().getColumn(0).setMaxWidth(0);
        tblJobs.getColumnModel().getColumn(0).setWidth(0);
        
        // Thêm renderer và editor cho cột Lưu và Ứng tuyển
        tblJobs.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tblJobs.getColumnModel().getColumn(5).setCellEditor(new SaveButtonEditor(tblJobs));
        tblJobs.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tblJobs.getColumnModel().getColumn(6).setCellEditor(new ApplyButtonEditor(tblJobs));
        
        tblJobs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblJobs.getSelectedRow() != -1) {
                int jobId = Integer.parseInt(tblJobs.getValueAt(tblJobs.getSelectedRow(), 0).toString());
                showJobDetails(jobId);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblJobs);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSavedJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("VIỆC LÀM ĐÃ LƯU");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JTextField txtSearchSaved = new JTextField(20);
        JButton btnSearchSaved = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearchSaved);
        searchPanel.add(btnSearchSaved);
        
        btnSearchSaved.addActionListener(e -> {
            loadSavedJobs(txtSearchSaved.getText().trim());
        });
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Saved Jobs Table
        String[] columns = {"ID", "Tiêu đề", "Công ty", "Mức lương", "Địa điểm", "Thao tác"};
        savedJobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        tblSavedJobs = new JTable(savedJobTableModel);
        styleTable(tblSavedJobs);
        
        // Ẩn cột ID
        tblSavedJobs.getColumnModel().getColumn(0).setMinWidth(0);
        tblSavedJobs.getColumnModel().getColumn(0).setMaxWidth(0);
        tblSavedJobs.getColumnModel().getColumn(0).setWidth(0);
        
        // Thêm renderer và editor cho cột thao tác
        tblSavedJobs.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        tblSavedJobs.getColumnModel().getColumn(5).setCellEditor(new SaveButtonEditor(tblSavedJobs));
        
        JScrollPane scrollPane = new JScrollPane(tblSavedJobs);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Load saved jobs
        loadSavedJobs("");
        
        return panel;
    }
    
    private JPanel createAppliedJobsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("VIỆC LÀM ĐÃ ỨNG TUYỂN");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        JTextField txtSearchApplied = new JTextField(20);
        JButton btnSearchApplied = new JButton("Tìm kiếm");
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearchApplied);
        searchPanel.add(btnSearchApplied);
        
        btnSearchApplied.addActionListener(e -> {
            loadAppliedJobs(txtSearchApplied.getText().trim());
        });
        
        panel.add(searchPanel, BorderLayout.CENTER);
        
        // Applied Jobs Table
        String[] columns = {"ID", "Tiêu đề", "Công ty", "Mức lương", "Địa điểm", "Trạng thái", "Thao tác"};
        appliedJobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        tblAppliedJobs = new JTable(appliedJobTableModel);
        styleTable(tblAppliedJobs);
        
        // Ẩn cột ID
        tblAppliedJobs.getColumnModel().getColumn(0).setMinWidth(0);
        tblAppliedJobs.getColumnModel().getColumn(0).setMaxWidth(0);
        tblAppliedJobs.getColumnModel().getColumn(0).setWidth(0);
        
        // Thêm renderer cho cột trạng thái
        tblAppliedJobs.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        
        // Thêm renderer và editor cho cột thao tác
        tblAppliedJobs.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tblAppliedJobs.getColumnModel().getColumn(6).setCellEditor(new ApplyButtonEditor(tblAppliedJobs));
        
        JScrollPane scrollPane = new JScrollPane(tblAppliedJobs);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        // Load applied jobs
        loadAppliedJobs("");
        
        return panel;
    }
    
    private void loadCategories() {
        categoryTableModel.setRowCount(0);
        List<CategoryDTO> categories = categoryBLL.getAllCategories();
        for (CategoryDTO category : categories) {
            categoryTableModel.addRow(new Object[]{
                category.getCategoryId(),
                category.getCategoryName()
            });
        }
    }
    
    private void loadJobsByCategory(int categoryId) {
        jobTableModel.setRowCount(0);
        List<JobDTO> jobs = categoryBLL.getJobsByCategory(categoryId);
        List<JobDTO> savedJobs = userSaveJobBLL.GetAllJobSavedOfUser(userId, "");
        
        for (JobDTO job : jobs) {
            if (job.isPublic() == 1) { // Chỉ hiển thị job đã duyệt
                boolean isSaved = savedJobs.stream()
                    .anyMatch(savedJob -> savedJob.getJobId() == job.getJobId());
                boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, job.getJobId()) != null;
                
                jobTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getCompanyName(),
                    String.format("%,.0f VNĐ", job.getSalary()),
                    job.getAddress(),
                    isSaved ? "Bỏ lưu" : "Lưu",
                    isApplied ? "Đã ứng tuyển" : "Ứng tuyển"
                });
            }
        }
    }
    
    private void loadSavedJobs(String keyword) {
        savedJobTableModel.setRowCount(0);
        List<JobDTO> savedJobs = userSaveJobBLL.GetAllJobSavedOfUser(userId, keyword);
        for (JobDTO job : savedJobs) {
            boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, job.getJobId()) != null;
            String actionText;
            if (isApplied) {
                actionText = "Đã ứng tuyển | Bỏ lưu";
            } else {
                actionText = "Ứng tuyển | Bỏ lưu";
            }
            savedJobTableModel.addRow(new Object[]{
                job.getJobId(),
                job.getJobName(),
                job.getCompanyName(),
                String.format("%,.0f VNĐ", job.getSalary()),
                job.getAddress(),
                actionText
            });
        }
    }
    
    private void loadAppliedJobs(String keyword) {
        appliedJobTableModel.setRowCount(0);
        List<JobDTO> appliedJobs = userApplyJobBLL.getAppliedJobsByUserIdAndJobName(userId, keyword);
        for (JobDTO job : appliedJobs) {
            UserApplyJobDTO application = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, job.getJobId());
            String status = "";
            switch (application.getState_id()) {
                case 0: status = "Chờ duyệt"; break;
                case 1: status = "Đã duyệt"; break;
                case -1: status = "Đã từ chối"; break;
            }
            appliedJobTableModel.addRow(new Object[]{
                job.getJobId(),
                job.getJobName(),
                job.getCompanyName(),
                String.format("%,.0f VNĐ", job.getSalary()),
                job.getAddress(),
                status,
                "Cập nhật CV"
            });
        }
    }
    
    private void showJobDetails(int jobId) {
        JobDTO job = jobBLL.getJobById(jobId);
        if (job != null) {
            // Kiểm tra trạng thái lưu và ứng tuyển
            boolean isSaved = userSaveJobBLL.GetAllJobSavedOfUser(userId, "").stream()
                .anyMatch(savedJob -> savedJob.getJobId() == jobId);
            UserApplyJobDTO application = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId);
            boolean isApplied = application != null;

            JDialog dialog = new JDialog(this, "Chi tiết công việc", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(Color.WHITE);
            dialog.setMinimumSize(new Dimension(800, 600));

            // Panel chính
            JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

            // Tiêu đề
            JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
            headerPanel.setBackground(Color.WHITE);
            JLabel titleLabel = new JLabel(job.getJobName());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            JLabel companyLabel = new JLabel(job.getCompanyName());
            companyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            headerPanel.add(titleLabel, BorderLayout.NORTH);
            headerPanel.add(companyLabel, BorderLayout.CENTER);
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Thông tin cơ bản
            JPanel infoPanel = new JPanel(new GridLayout(3, 2, 15, 15));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    "Thông tin cơ bản",
                    javax.swing.border.TitledBorder.LEFT,
                    javax.swing.border.TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 16),
                    new Color(44, 62, 80)
                ),
                new EmptyBorder(10, 10, 10, 10)
            ));
            addInfoItem(infoPanel, "Mức lương:", String.format("%,.0f VNĐ", job.getSalary()), "💰");
            addInfoItem(infoPanel, "Địa điểm:", job.getAddress(), "📍");
            if (job.getCategories() != null && !job.getCategories().isEmpty()) {
                String categoryNames = job.getCategories().stream()
                    .map(CategoryDTO::getCategoryName)
                    .collect(java.util.stream.Collectors.joining(", "));
                addInfoItem(infoPanel, "Danh mục:", categoryNames, "📋");
            }

            // Mô tả và yêu cầu
            JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 0, 20));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.add(createDetailSection("Mô tả công việc", job.getDescription(), "📝"));
            detailsPanel.add(createDetailSection("Yêu cầu", job.getRequirement(), "📋"));

            JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.add(infoPanel, BorderLayout.NORTH);
            contentPanel.add(detailsPanel, BorderLayout.CENTER);

            mainPanel.add(contentPanel, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            // Nút lưu/bỏ lưu
            JButton btnSave = new JButton(isSaved ? "Bỏ lưu" : "Lưu công việc");
            btnSave.setBackground(isSaved ? new Color(231, 76, 60) : new Color(52, 152, 219));
            btnSave.setForeground(Color.WHITE);
            btnSave.setFocusPainted(false);
            btnSave.addActionListener(e -> {
                if (isSaved) {
                    if (userSaveJobBLL.DeleteJobSaved(userId, jobId)) {
                        JOptionPane.showMessageDialog(dialog, "Đã bỏ lưu công việc");
                        btnSave.setText("Lưu công việc");
                        btnSave.setBackground(new Color(52, 152, 219));
                        if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                            loadSavedJobs("");
                        }
                        if (mainContentPanel.getComponent(0) == jobListPanel) {
                            loadJobsByCategory(selectedCategoryId);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Không thể bỏ lưu công việc",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (userSaveJobBLL.AddJobToSave(userId, jobId)) {
                        JOptionPane.showMessageDialog(dialog, "Đã lưu công việc");
                        btnSave.setText("Bỏ lưu");
                        btnSave.setBackground(new Color(231, 76, 60));
                        if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                            loadSavedJobs("");
                        }
                        if (mainContentPanel.getComponent(0) == jobListPanel) {
                            loadJobsByCategory(selectedCategoryId);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Không thể lưu công việc",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            
            // Nút ứng tuyển/hủy ứng tuyển
            JButton btnApply = new JButton(isApplied ? "Hủy ứng tuyển" : "Ứng tuyển");
            btnApply.setBackground(isApplied ? new Color(231, 76, 60) : new Color(46, 204, 113));
            btnApply.setForeground(Color.WHITE);
            btnApply.setFocusPainted(false);
            btnApply.addActionListener(e -> {
                if (isApplied) {
                    int choice = JOptionPane.showConfirmDialog(dialog,
                        "Bạn có chắc chắn muốn hủy ứng tuyển công việc này?",
                        "Xác nhận hủy",
                        JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        Response response = userApplyJobBLL.deleteApplyJob(jobId, userId);
                        if (response.isSuccess()) {
                            JOptionPane.showMessageDialog(dialog, response.getMessage());
                            dialog.dispose();
                            // Cập nhật lại các bảng
                            loadAppliedJobs("");
                            if (mainContentPanel.getComponent(0) == jobListPanel) {
                                loadJobsByCategory(selectedCategoryId);
                            } else if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                                loadSavedJobs("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog,
                                response.getMessage(),
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    dialog.dispose();
                    showApplyDialog(jobId);
                }
            });
            
            // Nút đóng
            JButton btnClose = new JButton("Đóng");
            btnClose.setBackground(new Color(149, 165, 166));
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnSave);
            buttonPanel.add(btnApply);
            buttonPanel.add(btnClose);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }
    
    private JPanel createDetailSection(String title, String content, String icon) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                icon + " " + title,
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(44, 62, 80)
            ),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JTextArea textArea = new JTextArea(content);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(new Color(248, 249, 250));
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void addInfoItem(JPanel panel, String label, String value, String icon) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        iconLabel.setForeground(new Color(52, 152, 219));
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(44, 62, 80));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(52, 73, 94));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.add(iconLabel);
        leftPanel.add(labelComponent);
        
        itemPanel.add(leftPanel, BorderLayout.WEST);
        itemPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(itemPanel);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void showApplyDialog(int jobId) {
        // Kiểm tra xem đã ứng tuyển chưa
        UserApplyJobDTO existingApplication = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId);
        if (existingApplication != null) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Bạn đã ứng tuyển công việc này. Bạn có muốn cập nhật CV không?",
                "Đã ứng tuyển",
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                showUpdateCvDialog(jobId);
            }
            return;
        }

        JDialog dialog = new JDialog(this, "Ứng tuyển công việc", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Thêm tiêu đề và hướng dẫn
        JPanel headerPanel = new JPanel(new BorderLayout(5, 5));
        headerPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Nhập link CV của bạn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel instructionLabel = new JLabel("Vui lòng cung cấp link CV (Google Drive, Dropbox, etc.)");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionLabel.setForeground(new Color(100, 100, 100));
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(instructionLabel, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Thêm text field nhập CV
        JTextField txtCvLink = new JTextField();
        txtCvLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtCvLink.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 5, 5, 5)
        ));
        panel.add(txtCvLink, BorderLayout.CENTER);
        
        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnApply = new JButton("Gửi đơn ứng tuyển");
        JButton btnCancel = new JButton("Hủy");
        
        btnApply.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnApply.setBackground(new Color(46, 204, 113));
        btnApply.setForeground(Color.WHITE);
        btnApply.setFocusPainted(false);
        
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancel.setBackground(new Color(149, 165, 166));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        
        btnApply.addActionListener(e -> {
            String cvLink = txtCvLink.getText().trim();
            if (cvLink.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập link CV",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Response response = userApplyJobBLL.applyJob(userId, jobId, cvLink);
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(dialog, response.getMessage());
                dialog.dispose();
                // Cập nhật lại tất cả các bảng liên quan
                loadAppliedJobs("");
                if (mainContentPanel.getComponent(0) == jobListPanel) {
                    loadJobsByCategory(selectedCategoryId);
                } else if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                    loadSavedJobs("");
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    response.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnApply);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void showUpdateCvDialog(int jobId) {
        UserApplyJobDTO application = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId);
        if (application == null) return;
        
        JDialog dialog = new JDialog(this, "Cập nhật CV", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel("Nhập link CV mới:");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label, BorderLayout.NORTH);
        
        JTextField txtCvLink = new JTextField(application.getCv_link());
        txtCvLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(txtCvLink, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton btnUpdate = new JButton("Cập nhật");
        JButton btnCancel = new JButton("Hủy");
        
        btnUpdate.addActionListener(e -> {
            String cvLink = txtCvLink.getText().trim();
            if (cvLink.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập link CV",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Response response = userApplyJobBLL.updateCvLink(jobId, userId, cvLink);
            if (response.isSuccess()) {
                JOptionPane.showMessageDialog(dialog, response.getMessage());
                dialog.dispose();
                // Cập nhật lại tất cả các bảng liên quan
                loadAppliedJobs("");
                if (mainContentPanel.getComponent(0) == jobListPanel) {
                    loadJobsByCategory(selectedCategoryId);
                } else if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                    loadSavedJobs("");
                }
            } else {
                JOptionPane.showMessageDialog(dialog,
                    response.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnCancel);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }
    
    private class SaveButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        
        public SaveButtonEditor(JTable table) {
            super(new JTextField());
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                int jobId = Integer.parseInt(table.getValueAt(row, 0).toString());
                
                if (table == tblJobs) {
                    if (label.equals("Lưu")) {
                        if (userSaveJobBLL.AddJobToSave(userId, jobId)) {
                            JOptionPane.showMessageDialog(null, "Đã lưu công việc");
                            table.setValueAt("Bỏ lưu", row, 5);
                            if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                                loadSavedJobs("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Không thể lưu công việc",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (label.equals("Bỏ lưu")) {
                        if (userSaveJobBLL.DeleteJobSaved(userId, jobId)) {
                            JOptionPane.showMessageDialog(null, "Đã bỏ lưu công việc");
                            table.setValueAt("Lưu", row, 5);
                            if (mainContentPanel.getComponent(0) == savedJobsPanel) {
                                loadSavedJobs("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Không thể bỏ lưu công việc",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (label.equals("Ứng tuyển")) {
                        showApplyDialog(jobId);
                        boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId) != null;
                        table.setValueAt(isApplied ? "Đã ứng tuyển" : "Ứng tuyển", row, 6);
                    }
                } else if (table == tblSavedJobs) {
                    if (label.contains("Bỏ lưu")) {
                        if (userSaveJobBLL.DeleteJobSaved(userId, jobId)) {
                            JOptionPane.showMessageDialog(null, "Đã bỏ lưu công việc");
                            loadSavedJobs("");
                            if (mainContentPanel.getComponent(0) == jobListPanel) {
                                loadJobsByCategory(selectedCategoryId);
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Không thể bỏ lưu công việc",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else if (label.contains("Ứng tuyển")) {
                        showApplyDialog(jobId);
                        boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId) != null;
                        table.setValueAt(isApplied ? "Đã ứng tuyển | Bỏ lưu" : "Ứng tuyển | Bỏ lưu", row, 5);
                        loadSavedJobs("");
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }
    
    private class ApplyButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        
        public ApplyButtonEditor(JTable table) {
            super(new JTextField());
            this.table = table;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                int jobId = Integer.parseInt(table.getValueAt(row, 0).toString());
                
                if (table == tblJobs) {
                    showApplyDialog(jobId);
                    // Sau khi ứng tuyển, cập nhật lại trạng thái nút
                    boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId) != null;
                    table.setValueAt(isApplied ? "Đã ứng tuyển" : "Ứng tuyển", row, 6);
                } else if (table == tblSavedJobs) {
                    showApplyDialog(jobId);
                    // Sau khi ứng tuyển, cập nhật lại trạng thái nút
                    boolean isApplied = userApplyJobBLL.getApplicationByUserIdAndJobId(userId, jobId) != null;
                    table.setValueAt(isApplied ? "Đã ứng tuyển | Bỏ lưu" : "Ứng tuyển | Bỏ lưu", row, 5);
                } else if (table == tblAppliedJobs) {
                    showUpdateCvDialog(jobId);
                }
            }
            isPushed = false;
            return label;
        }
    }
    
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            String status = value.toString();
            Color bgColor;
            switch (status) {
                case "Chờ duyệt":
                    bgColor = new Color(255, 193, 7);
                    break;
                case "Đã duyệt":
                    bgColor = new Color(40, 167, 69);
                    break;
                case "Đã từ chối":
                    bgColor = new Color(220, 53, 69);
                    break;
                default:
                    bgColor = new Color(108, 117, 125);
            }
            
            label.setBackground(bgColor);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setOpaque(true);
            
            return label;
        }
    }
    
    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(new Color(50, 50, 50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        cardLayout.show(mainContentPanel, "CategoryList");
    }
    
    private void addEventListeners() {
        btnSearch.addActionListener(this);
        btnViewJobs.addActionListener(this);
        btnSaveJob.addActionListener(this);
        btnSetting.addActionListener(this);
        
        JPopupMenu menu = createSettingMenu();
        btnSetting.addActionListener(e -> {
            menu.show(btnSetting, 0, -menu.getPreferredSize().height);
        });
        
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }
    
    private JPopupMenu createSettingMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Thông tin cá nhân");
        JMenuItem changePWItem = new JMenuItem("Đổi mật khẩu");
        JMenuItem appliedJobsItem = new JMenuItem("Việc làm đã ứng tuyển");
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        
        profileItem.addActionListener(e -> cardLayout.show(mainContentPanel, "Profile"));
        changePWItem.addActionListener(e -> cardLayout.show(mainContentPanel, "ChangePassword"));
        appliedJobsItem.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "AppliedJobs");
            loadAppliedJobs("");
        });
        logoutItem.addActionListener(e -> {
            UserSession.GetInstance().logout();
            dispose();
            new LoginF("Đăng nhập").setVisible(true);
        });
        
        menu.add(profileItem);
        menu.add(changePWItem);
        menu.add(appliedJobsItem);
        menu.addSeparator();
        menu.add(logoutItem);
        
        return menu;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            performSearch();
        } else if (e.getSource() == btnViewJobs) {
            cardLayout.show(mainContentPanel, "CategoryList");
        } else if (e.getSource() == btnSaveJob) {
            cardLayout.show(mainContentPanel, "SavedJobs");
            loadSavedJobs("");
        }
    }
    
    private void performSearch() {
        if (selectedCategoryId == -1) return;
        
        String keyword = txtSearch.getText().trim();
        jobTableModel.setRowCount(0);
        List<JobDTO> jobs = categoryBLL.getJobsByCategory(selectedCategoryId);
        
        for (JobDTO job : jobs) {
            if (job.isPublic() == 1 && 
                (keyword.isEmpty() || 
                 job.getJobName().toLowerCase().contains(keyword.toLowerCase()) ||
                 job.getCompanyName().toLowerCase().contains(keyword.toLowerCase()))) {
                jobTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getCompanyName(),
                    String.format("%,.0f VNĐ", job.getSalary()),
                    job.getAddress(),
                    "Lưu & Ứng tuyển"
                });
            }
        }
    }
} 