package UI;

import BLL.*;
import DTO.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class CreateJobPanel extends JPanel {
    private JTextField txtJobName, txtCompanyName, txtSalary, txtAddress;
    private JTextArea txtDescription, txtRequirement;
    private JComboBox<CategoryDTO> cboCategory;
    private JButton btnSave, btnCancel;
    private JobBLL jobBLL;
    private CategoryBLL categoryBLL;
    private UserSaveJobBLL userSaveJobBLL;
    private int userId;
    private EmployerMainF parent; // This should be correctly set via setParent()
    
    public CreateJobPanel(int userId) {
        this.userId = userId;
        
        try {
            jobBLL = new JobBLL();
            categoryBLL = new CategoryBLL();
            userSaveJobBLL = new UserSaveJobBLL();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo lớp xử lý nghiệp vụ: " + e.getMessage(),
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }
        
        initComponents();
        loadCategories();
    }
    
    public void setParent(EmployerMainF parent) {
        this.parent = parent;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel chính chứa form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Đăng tin tuyển dụng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tên công việc
        formPanel.add(new JLabel("Tên công việc:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtJobName = new JTextField(30);
        formPanel.add(txtJobName, gbc);
        
        // Công ty
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Công ty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtCompanyName = new JTextField(30);
        formPanel.add(txtCompanyName, gbc);
        
        // Mức lương
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mức lương:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtSalary = new JTextField(30);
        formPanel.add(txtSalary, gbc);
        
        // Địa chỉ
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtAddress = new JTextField(30);
        formPanel.add(txtAddress, gbc);
        
        // Ngành nghề
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Ngành nghề:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        cboCategory = new JComboBox<>();
        cboCategory.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value instanceof CategoryDTO) {
                    CategoryDTO category = (CategoryDTO) value;
                    setText(category.getCategoryName() != null ? category.getCategoryName() : "[Chưa có tên]");
                } else if (value == null) {
                    setText(""); 
                }
                return this;
            }
        });
        formPanel.add(cboCategory, gbc);
        
        // Thêm label hiển thị trạng thái
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JLabel lblStatus = new JLabel("Chờ duyệt");
        lblStatus.setForeground(Color.ORANGE);
        lblStatus.setFont(lblStatus.getFont().deriveFont(Font.BOLD));
        formPanel.add(lblStatus, gbc);
        
        // Mô tả
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtDescription = new JTextArea(5, 30);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        formPanel.add(descScroll, gbc);
        
        // Yêu cầu
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Yêu cầu:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtRequirement = new JTextArea(5, 30);
        txtRequirement.setLineWrap(true);
        txtRequirement.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(txtRequirement);
        formPanel.add(reqScroll, gbc);
        
        // Panel nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu");
        btnCancel = new JButton("Hủy");
        
        btnSave.addActionListener(e -> saveJob());
        btnCancel.addActionListener(e -> {
            clearForm();
            if (parent != null) { // Check if parent is initialized
                parent.showView("JobList"); // *** MODIFIED THIS LINE ***
            } else {
                 System.err.println("Parent not set in CreateJobPanel, cannot navigate on cancel.");
            }
        });
        
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        
        // Thêm các panel vào layout chính
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCategories() {
        try {
            if (categoryBLL != null) { // Check BLL initialization
                List<CategoryDTO> categories = categoryBLL.getAllCategories();
                cboCategory.removeAllItems(); // Clear before adding
                if (categories != null) {
                    for (CategoryDTO category : categories) {
                        if (category != null) { // Ensure category object itself is not null
                            cboCategory.addItem(category);
                        }
                    }
                } else {
                    // Handle case where categories list is null, though BLL should ideally return empty list
                     JOptionPane.showMessageDialog(this, "Không thể tải danh sách ngành nghề (danh sách null).", "Lỗi", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: CategoryBLL chưa được khởi tạo.", "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải danh sách ngành nghề: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void saveJob() {
        try {
            // Validate dữ liệu
            if (txtJobName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên công việc");
                txtJobName.requestFocusInWindow();
                return;
            }
            if (txtCompanyName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên công ty");
                txtCompanyName.requestFocusInWindow();
                return;
            }
            if (txtSalary.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mức lương");
                txtSalary.requestFocusInWindow();
                return;
            }
            if (txtAddress.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập địa chỉ");
                txtAddress.requestFocusInWindow();
                return;
            }
            if (cboCategory.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngành nghề");
                cboCategory.requestFocusInWindow();
                return;
            }
            
            // Parse lương
            double salary;
            try {
                salary = Double.parseDouble(txtSalary.getText().trim());
                if (salary < 0) {
                     JOptionPane.showMessageDialog(this, "Mức lương không thể là số âm.");
                     txtSalary.requestFocusInWindow();
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mức lương không hợp lệ. Vui lòng nhập số.");
                txtSalary.requestFocusInWindow();
                return;
            }
            
            // Lấy category đã chọn
            CategoryDTO selectedCategory = (CategoryDTO) cboCategory.getSelectedItem();
            
            // Tạo job mới
            JobDTO newJob = new JobDTO();
            newJob.setJobName(txtJobName.getText().trim());
            newJob.setCompanyName(txtCompanyName.getText().trim());
            newJob.setSalary(salary);
            newJob.setAddress(txtAddress.getText().trim());
            newJob.setDescription(txtDescription.getText().trim());
            newJob.setRequirement(txtRequirement.getText().trim());
            newJob.setPublic(0); // Chờ duyệt
            
            // Thêm category vào job
            List<CategoryDTO> categoriesForJob = new ArrayList<>(); // Renamed to avoid conflict
            categoriesForJob.add(selectedCategory);
            newJob.setCategories(categoriesForJob);
            
            // Thêm job vào database
            if (jobBLL != null && jobBLL.addJob(newJob)) {
                // Lấy job vừa thêm để có ID
                // Assuming searchJobs can find the job by name immediately after adding.
                // This might need a more robust way to get the ID of the newly added job, e.g., addJob returning the ID.
                List<JobDTO> jobs = jobBLL.searchJobs(newJob.getJobName(), -1); 
                if (jobs != null && !jobs.isEmpty()) {
                    // Find the most recently added job, ideally by matching more fields or if addJob returned it.
                    // For simplicity, taking the first one matching the name.
                    JobDTO addedJob = jobs.stream()
                                          .filter(j -> j.getCompanyName().equals(newJob.getCompanyName()) &&
                                                       j.getAddress().equals(newJob.getAddress())) // Add more checks if needed
                                          .findFirst().orElse(jobs.get(0)); // Fallback or refine

                    if (userSaveJobBLL != null) {
                        userSaveJobBLL.AddJobToSave(userId, addedJob.getJobId());
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Đăng tin tuyển dụng thành công!");
                clearForm();
                if (parent != null) { // Check parent
                    parent.refreshJobList();
                    parent.showView("JobList"); // *** MODIFIED THIS LINE ***
                }
            } else {
                JOptionPane.showMessageDialog(this, "Không thể đăng tin tuyển dụng. Vui lòng thử lại.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khi đăng tin tuyển dụng: " + e.getMessage(),
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtJobName.setText("");
        txtCompanyName.setText("");
        txtSalary.setText("");
        txtAddress.setText("");
        txtDescription.setText("");
        txtRequirement.setText("");
        if (cboCategory.getItemCount() > 0) { // Only set selected index if items exist
            cboCategory.setSelectedIndex(-1); // Clears selection, shows placeholder from renderer if value is null
        } else {
            cboCategory.setSelectedItem(null); // Ensure it's null if empty
        }
    }
}