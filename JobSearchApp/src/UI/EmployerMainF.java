package UI;

import BLL.*;
import DTO.*;
import Util.Response;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerMainF extends JFrame implements ActionListener {
    private JPanel headerPanel, controlPanel;
    // Made mainContentPanel and cardLayout package-private (as they were)
    // but interaction will be through a public method.
    JPanel mainContentPanel; 
    CardLayout cardLayout;
    
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnSetting, btnCreateJob, btnViewPostedJobs;
    
    private JPanel jobListDisplayPanel;
    private CreateJobPanel createJobPanel;
    private ProfilePanel profilePanel;
    private ChangePasswordPanel changePWPanel;
    
    private JobBLL jobBLL;
    private CategoryBLL categoryBLL;
    private UserApplyJobBLL userApplyJobBLL;
    private UserSaveJobBLL userSaveJobBLL;
    private DefaultTableModel postedJobsTableModel;
    private JTable tblPostedJobs;
    private int userId;
    
    public EmployerMainF(int userId) {
        super("Ứng dụng tìm kiếm việc làm - Nhà tuyển dụng");
        this.userId = userId;
        
        try {
            jobBLL = new JobBLL();
            categoryBLL = new CategoryBLL();
            userApplyJobBLL = new UserApplyJobBLL();
            userSaveJobBLL = new UserSaveJobBLL();
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
        loadPostedJobs("");
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
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM - NHÀ TUYỂN DỤNG");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lbTitle);
        
        // Control Panel
        controlPanel = new JPanel(new BorderLayout(15, 10));
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        controlPanel.setBackground(new Color(245, 245, 245));
        
        // Search Area
        JPanel searchAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAreaPanel.setOpaque(false);
        JLabel lbSearchPrompt = new JLabel("Tìm kiếm việc làm:");
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
        
        // Buttons Group
        JPanel buttonsGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsGroupPanel.setOpaque(false);
        btnCreateJob = new JButton("Đăng tin tuyển dụng");
        btnViewPostedJobs = new JButton("Tin đã đăng");
        btnSetting = new JButton("Cài đặt");
        
        JButton[] actionButtons = {btnCreateJob, btnViewPostedJobs, btnSetting};
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
        jobListDisplayPanel = createJobListPanel();
        createJobPanel = new CreateJobPanel(userId);
        createJobPanel.setParent(this); // *** ADDED THIS LINE ***
        
        // Assuming ProfilePanel and ChangePasswordPanel might need similar parent reference
        // if they also navigate or interact directly with EmployerMainF's layout.
        // For now, only fixing CreateJobPanel interaction.
        profilePanel = new ProfilePanel(userId); 
        // if (profilePanel instanceof SomeInterfaceThatNeedsParent) { profilePanel.setParent(this); }
        changePWPanel = new ChangePasswordPanel(userId);
        // if (changePWPanel instanceof SomeInterfaceThatNeedsParent) { changePWPanel.setParent(this); }
        
        mainContentPanel.add(jobListDisplayPanel, "JobList");
        mainContentPanel.add(createJobPanel, "CreateJob");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(changePWPanel, "ChangePassword");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        cardLayout.show(mainContentPanel, "JobList");
    }
    
    private void addEventListeners() {
        btnSearch.addActionListener(this);
        btnCreateJob.addActionListener(this);
        btnViewPostedJobs.addActionListener(this);
        // btnSetting listener is more complex, handled by creating a JPopupMenu
        
        JPopupMenu menu = createSettingMenu();
        btnSetting.addActionListener(e -> {
            // Ensure menu is not null and btnSetting is valid before showing
            if (menu != null && btnSetting != null) {
                 menu.show(btnSetting, 0, -menu.getPreferredSize().height);
            }
        });
    }

    // *** ADDED PUBLIC METHOD FOR NAVIGATION ***
    public void showView(String viewName) {
        if (cardLayout != null && mainContentPanel != null) {
            cardLayout.show(mainContentPanel, viewName);
        } else {
            System.err.println("Error: CardLayout or MainContentPanel not initialized in EmployerMainF when trying to show view: " + viewName);
            // Optionally, show a JOptionPane error to the user
            // JOptionPane.showMessageDialog(this, "Lỗi điều hướng giao diện.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createJobListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Tạo bảng việc làm đã đăng
        String[] columnNames = {"ID", "Tên công việc", "Công ty", "Mức lương", "Địa chỉ", "Trạng thái", "Thao tác"};
        postedJobsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho phép chỉnh sửa cột thao tác
            }
        };
        
        tblPostedJobs = new JTable(postedJobsTableModel);
        tblPostedJobs.setRowHeight(35);
        tblPostedJobs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tblPostedJobs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Thiết lập renderer cho cột thao tác
        tblPostedJobs.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        tblPostedJobs.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this));
        
        // Thiết lập độ rộng các cột
        int[] columnWidths = {50, 200, 150, 100, 150, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            tblPostedJobs.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        
        JScrollPane scrollPane = new JScrollPane(tblPostedJobs);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPopupMenu createSettingMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Thông tin cá nhân");
        JMenuItem changePWItem = new JMenuItem("Đổi mật khẩu");
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        
        profileItem.addActionListener(e -> showView("Profile")); // Use new method
        changePWItem.addActionListener(e -> showView("ChangePassword")); // Use new method
        logoutItem.addActionListener(e -> {
            UserSession.GetInstance().logout();
            dispose();
            new LoginF("Đăng nhập").setVisible(true);
        });
        
        menu.add(profileItem);
        menu.add(changePWItem);
        menu.addSeparator();
        menu.add(logoutItem);
        
        return menu;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnSearch) {
            loadPostedJobs(txtSearch.getText().trim());
        } else if (source == btnCreateJob) {
            showView("CreateJob"); // Use new method
        } else if (source == btnViewPostedJobs) {
            showView("JobList"); // Use new method
            loadPostedJobs(""); // Refresh job list when viewing posted jobs
        }
        // btnSetting action is handled directly in addEventListeners via lambda
    }
    
    public void loadPostedJobs(String keyword) {
        postedJobsTableModel.setRowCount(0); // Clear existing rows
        List<JobDTO> jobs;
        if (jobBLL != null) { // Check if BLL is initialized
            jobs = jobBLL.searchJobs(keyword, -1); // -1 để lấy tất cả trạng thái
            if (jobs != null) {
                for (JobDTO job : jobs) {
                    String status = "Không xác định";
                    if (job != null) { // Check if job object is not null
                         status = job.isPublic() == 0 ? "Chờ duyệt" : 
                                  job.isPublic() == 1 ? "Đã duyệt" : "Từ chối";
                        
                        postedJobsTableModel.addRow(new Object[]{
                            job.getJobId(),
                            job.getJobName(),
                            job.getCompanyName(),
                            String.format("%.0f", job.getSalary()),
                            job.getAddress(),
                            status,
                            "Xem chi tiết"
                        });
                    }
                }
            }
        } else {
             JOptionPane.showMessageDialog(this, "Lỗi: JobBLL chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void refreshJobList() {
        loadPostedJobs(txtSearch.getText().trim());
    }
    
    // ButtonRenderer class
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            // You might want to set background/foreground based on selection state here
            // if (!isSelected) {
            //     setBackground(table.getBackground());
            // } else {
            //     setBackground(table.getSelectionBackground());
            // }
            return this;
        }
    }
    
    // ButtonEditor class
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private EmployerMainF parentFrame; // Changed name to avoid confusion
        
        public ButtonEditor(JCheckBox checkBox, EmployerMainF parentFrame) {
            super(checkBox);
            this.parentFrame = parentFrame;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                if (tblPostedJobs != null && tblPostedJobs.getSelectedRow() != -1) {
                    int selectedRow = tblPostedJobs.getSelectedRow();
                    if (selectedRow < tblPostedJobs.getRowCount()) { // Bounds check
                        Object idObj = tblPostedJobs.getValueAt(selectedRow, 0);
                        if (idObj != null) {
                            try {
                                int jobId = Integer.parseInt(idObj.toString());
                                // Hiển thị dialog xem chi tiết việc làm
                                parentFrame.showJobDetailDialog(jobId);
                            } catch (NumberFormatException ex) {
                                System.err.println("Error parsing Job ID: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
    private void showJobDetailDialog(int jobId) {
        if (jobBLL == null) {
             JOptionPane.showMessageDialog(this, "Lỗi: JobBLL chưa được khởi tạo.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JobDTO job = jobBLL.getJobById(jobId);
        if (job == null) {
            JOptionPane.showMessageDialog(this, "Không thể tải chi tiết việc làm.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(this, "Chi tiết việc làm", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Panel thông tin việc làm
        JPanel jobInfoPanel = new JPanel(new GridBagLayout());
        jobInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = -1; // Start gbc.gridy from -1 so first ++gbc.gridy is 0
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Thêm các thông tin việc làm
        addLabelAndValue(jobInfoPanel, gbc, "Tên công việc:", job.getJobName());
        addLabelAndValue(jobInfoPanel, gbc, "Công ty:", job.getCompanyName());
        addLabelAndValue(jobInfoPanel, gbc, "Mức lương:", String.format("%.0f", job.getSalary()));
        addLabelAndValue(jobInfoPanel, gbc, "Địa chỉ:", job.getAddress());
        
        // Use JTextArea for multiline descriptions/requirements
        addLabelAndTextArea(jobInfoPanel, gbc, "Mô tả:", job.getDescription());
        addLabelAndTextArea(jobInfoPanel, gbc, "Yêu cầu:", job.getRequirement());
        
        // Danh sách người ứng tuyển
        JPanel applicantsPanel = new JPanel(new BorderLayout(5, 5));
        applicantsPanel.setBorder(BorderFactory.createTitledBorder("Danh sách ứng viên"));
        
        String[] applicantColumns = {"ID", "Tên ứng viên", "Link CV", "Trạng thái", "Thao tác"};
        DefaultTableModel applicantModel = new DefaultTableModel(applicantColumns, 0){
             @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };
        JTable tblApplicants = new JTable(applicantModel);
        tblApplicants.setRowHeight(25); // Adjust row height if needed
        
        if (userApplyJobBLL != null) { // Check BLL initialization
            List<UserDTO> applicants = userApplyJobBLL.getApplicantUsersForJob(jobId);
            if (applicants != null) {
                for (UserDTO applicant : applicants) {
                    if (applicant != null) { // Check applicant DTO
                        UserApplyJobDTO application = userApplyJobBLL.getApplicationByUserIdAndJobId(applicant.getUser_id(), jobId);
                        if (application != null) { // Check application DTO
                            String status = application.getState_id() == 0 ? "Chờ duyệt" :
                                          application.getState_id() == 1 ? "Đã duyệt" : "Từ chối";
                            
                            applicantModel.addRow(new Object[]{
                                applicant.getUser_id(),
                                applicant.getUser_name(),
                                application.getCv_link(),
                                status,
                                "Xử lý" // Changed button text for clarity
                            });
                        }
                    }
                }
            }
        }
        
        // Thiết lập renderer và editor cho cột thao tác của bảng ứng viên
        // Using a simpler DefaultCellEditor with a button for action.
        tblApplicants.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        tblApplicants.getColumnModel().getColumn(4).setCellEditor(
            new DefaultCellEditor(new JCheckBox()) { // Checkbox is a placeholder for DefaultCellEditor
                private JButton actionButton;
                private int currentApplicantId;
                private int currentRow;

                {
                    actionButton = new JButton("Xử lý");
                    actionButton.addActionListener(e -> {
                        fireEditingStopped(); // Important to stop editing mode

                        if (userApplyJobBLL != null) {
                            UserApplyJobDTO application = userApplyJobBLL.getApplicationByUserIdAndJobId(currentApplicantId, jobId);
                            if (application == null) {
                                JOptionPane.showMessageDialog(dialog, "Không tìm thấy đơn ứng tuyển.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                return;
                            }

                            String[] options = {"Phê duyệt", "Từ chối", "Hủy"};
                            int choice = JOptionPane.showOptionDialog(dialog,
                                "Chọn hành động cho ứng viên '" + tblApplicants.getValueAt(currentRow, 1) + "':",
                                "Xử lý đơn ứng tuyển",
                                JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                options,
                                options[2]); // Default to Cancel
                            
                            int newStateId = application.getState_id(); // Keep current state if cancelled
                            if (choice == 0) { // Phê duyệt
                                newStateId = 1;
                            } else if (choice == 1) { // Từ chối
                                newStateId = -1; 
                            }

                            if (choice == 0 || choice == 1) { // If Phê duyệt or Từ chối
                                Response updateResponse = userApplyJobBLL.updateApplicationStatus(application.getUser_apply_job_id(), newStateId, userId);
                                if (updateResponse.isSuccess()) {
                                    // Refresh status in the table
                                    String newStatusStr = newStateId == 0 ? "Chờ duyệt" :
                                                      newStateId == 1 ? "Đã duyệt" : "Từ chối";
                                    applicantModel.setValueAt(newStatusStr, currentRow, 3);
                                    JOptionPane.showMessageDialog(dialog, updateResponse.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(dialog, updateResponse.getMessage(), "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    });
                }
                
                @Override
                public Component getTableCellEditorComponent(JTable table, Object value,
                        boolean isSelected, int row, int column) {
                    this.currentApplicantId = Integer.parseInt(table.getValueAt(row, 0).toString());
                    this.currentRow = row;
                    actionButton.setText(value != null ? value.toString() : "Xử lý");
                    return actionButton;
                }

                @Override
                public Object getCellEditorValue() {
                    return actionButton.getText(); 
                }
            }
        );
        
        JScrollPane applicantScroll = new JScrollPane(tblApplicants);
        applicantsPanel.add(applicantScroll, BorderLayout.CENTER);
        
        // Layout tổng thể
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            new JScrollPane(jobInfoPanel), applicantsPanel);
        splitPane.setDividerLocation(250); // Adjusted divider location
        
        dialog.add(splitPane, BorderLayout.CENTER);
        
        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        
        JPanel bottomButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Renamed to avoid conflict
        bottomButtonPanel.add(btnClose);
        dialog.add(bottomButtonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void addLabelAndValue(JPanel panel, GridBagConstraints gbc, String labelText, String valueText) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0; // Label doesn't expand
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0; // Value expands
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel valueLabel = new JLabel(valueText != null ? valueText : ""); // Handle null value
        panel.add(valueLabel, gbc);
    }

    private void addLabelAndTextArea(JPanel panel, GridBagConstraints gbc, String labelText, String valueText) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST; // Align label to top-left of textarea
        panel.add(new JLabel(labelText), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; // Allow textarea to expand vertically if needed
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea textArea = new JTextArea(valueText != null ? valueText : "");
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(panel.getBackground()); // Match background
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(100, 60)); // Default size for text area
        panel.add(scrollPane, gbc);
        gbc.weighty = 0.0; // Reset weighty for next components
    }
}