 package UI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateJobPanel extends JPanel implements ActionListener {
    private int hrId; // ID của HR đăng tin (nếu cần lưu vào bảng job)

    private JTextField txtJobName, txtSalary, txtCompanyName, txtAddress;
    private JTextArea txtDescription, txtRequirement;
    private JCheckBox chkIsPublic;
    private JButton btnPostJob, btnClearForm;

    // For categories
    private JList<String> categoryList;
    private DefaultListModel<String> categoryListModel;
    private Map<String, Integer> categoryMap; // Store category_name -> category_id

    public CreateJobPanel(int hrId) { // Truyền ID của HR nếu cần
        this.hrId = hrId;
        categoryMap = new HashMap<>();
        initComponents();
        loadCategories();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                " Đăng tin tuyển dụng mới ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(0, 102, 204)
            ),
            new EmptyBorder(10, 15, 15, 15)
        ));
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Job Name
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Tên công việc:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtJobName = new JTextField(30); txtJobName.setFont(textFont);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtJobName, gbc);

        // Salary
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Mức lương (VD: 1000.00):", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtSalary = new JTextField(30); txtSalary.setFont(textFont);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtSalary, gbc);

        // Company Name
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Tên công ty:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtCompanyName = new JTextField(30); txtCompanyName.setFont(textFont);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtCompanyName, gbc);

        // Address
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Địa chỉ:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtAddress = new JTextField(30); txtAddress.setFont(textFont);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtAddress, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Mô tả công việc:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtDescription = new JTextArea(5, 30); txtDescription.setFont(textFont);
        txtDescription.setLineWrap(true); txtDescription.setWrapStyleWord(true);
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weighty = 0.5; gbc.fill = GridBagConstraints.BOTH; formPanel.add(scrollDescription, gbc);

        // Requirement
        gbc.gridx = 0; gbc.gridy = 5; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Yêu cầu công việc:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        txtRequirement = new JTextArea(5, 30); txtRequirement.setFont(textFont);
        txtRequirement.setLineWrap(true); txtRequirement.setWrapStyleWord(true);
        JScrollPane scrollRequirement = new JScrollPane(txtRequirement);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weighty = 0.5; gbc.fill = GridBagConstraints.BOTH; formPanel.add(scrollRequirement, gbc);
        
        // Categories
        gbc.gridx = 0; gbc.gridy = 6; gbc.weighty = 0; gbc.anchor = GridBagConstraints.NORTHWEST; formPanel.add(new JLabel("Ngành nghề:", JLabel.LEFT) {{setFont(labelFont);}}, gbc);
        categoryListModel = new DefaultListModel<>();
        categoryList = new JList<>(categoryListModel);
        categoryList.setFont(textFont);
        categoryList.setVisibleRowCount(4); // Số dòng hiển thị
        categoryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollCategories = new JScrollPane(categoryList);
        gbc.gridx = 1; gbc.gridy = 6; gbc.weighty = 0.3; gbc.fill = GridBagConstraints.BOTH; formPanel.add(scrollCategories, gbc);


        // Is Public
        gbc.gridx = 0; gbc.gridy = 7; gbc.weighty = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
        chkIsPublic = new JCheckBox("Hiển thị công khai"); chkIsPublic.setFont(labelFont);
        chkIsPublic.setSelected(true); chkIsPublic.setOpaque(false);
        gbc.gridx = 1; gbc.gridy = 7; formPanel.add(chkIsPublic, gbc);


        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        btnClearForm = new JButton("Làm mới");
        btnClearForm.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClearForm.setPreferredSize(new Dimension(120, 35));
        btnClearForm.addActionListener(this);

        btnPostJob = new JButton("Đăng tin");
        btnPostJob.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPostJob.setBackground(new Color(0, 102, 204));
        btnPostJob.setForeground(Color.WHITE);
        btnPostJob.setPreferredSize(new Dimension(120, 35));
        btnPostJob.addActionListener(this);

        buttonPanel.add(btnClearForm);
        buttonPanel.add(btnPostJob);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadCategories() {
//        String sql = "SELECT category_id, category_name FROM category ORDER BY category_name";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//
//            categoryListModel.clear();
//            categoryMap.clear();
//            while (rs.next()) {
//                String categoryName = rs.getString("category_name");
//                int categoryId = rs.getInt("category_id");
//                categoryListModel.addElement(categoryName);
//                categoryMap.put(categoryName, categoryId);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách ngành nghề: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClearForm) {
            clearForm();
        } else if (e.getSource() == btnPostJob) {
            handlePostJob();
        }
    }

    private void clearForm() {
        txtJobName.setText("");
        txtSalary.setText("");
        txtCompanyName.setText("");
        txtAddress.setText("");
        txtDescription.setText("");
        txtRequirement.setText("");
        chkIsPublic.setSelected(true);
        categoryList.clearSelection();
    }

    private void handlePostJob() {
        String jobName = txtJobName.getText().trim();
        String salaryStr = txtSalary.getText().trim();
        String companyName = txtCompanyName.getText().trim();
        String description = txtDescription.getText().trim();
        String requirement = txtRequirement.getText().trim();
        String address = txtAddress.getText().trim();
        boolean isPublic = chkIsPublic.isSelected();
        List<String> selectedCategoryNames = categoryList.getSelectedValuesList();


        if (jobName.isEmpty() || companyName.isEmpty() || description.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ các trường bắt buộc (Tên CV, Tên CT, Mô tả, Địa chỉ).", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedCategoryNames.isEmpty()){
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ngành nghề.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal salary = null;
        if (!salaryStr.isEmpty()) {
            try {
                salary = new BigDecimal(salaryStr);
                if (salary.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Mức lương không thể là số âm.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mức lương không hợp lệ. Vui lòng nhập số (VD: 1500.50).", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

//        Connection conn = null;
//        PreparedStatement pstmtJob = null;
//        PreparedStatement pstmtCategoryJob = null;
//        ResultSet generatedKeys = null;
//
//        String sqlInsertJob = "INSERT INTO job (job_name, salary, company_name, description, is_public, requirement, address) VALUES (?, ?, ?, ?, ?, ?, ?)";
//        String sqlInsertCategoryJob = "INSERT INTO categoryofjob (job_id, category_id) VALUES (?, ?)";
//
//        try {
//            conn = DBConnection.getConnection();
//            conn.setAutoCommit(false); // Bắt đầu transaction
//
//            // Bước 1: Insert vào bảng 'job'
//            pstmtJob = conn.prepareStatement(sqlInsertJob, Statement.RETURN_GENERATED_KEYS);
//            pstmtJob.setString(1, jobName);
//            if (salary != null) {
//                pstmtJob.setBigDecimal(2, salary);
//            } else {
//                pstmtJob.setNull(2, Types.DECIMAL);
//            }
//            pstmtJob.setString(3, companyName);
//            pstmtJob.setString(4, description);
//            pstmtJob.setBoolean(5, isPublic);
//            pstmtJob.setString(6, requirement.isEmpty() ? null : requirement);
//            pstmtJob.setString(7, address);
//
//            int rowsAffectedJob = pstmtJob.executeUpdate();
//            if (rowsAffectedJob == 0) {
//                throw new SQLException("Tạo công việc thất bại, không có dòng nào được thêm vào bảng job.");
//            }
//
//            // Bước 2: Lấy job_id vừa được tạo
//            generatedKeys = pstmtJob.getGeneratedKeys();
//            int newJobId;
//            if (generatedKeys.next()) {
//                newJobId = generatedKeys.getInt(1);
//            } else {
//                throw new SQLException("Tạo công việc thất bại, không lấy được ID công việc đã tạo.");
//            }
//            
//            // Bước 3: Insert vào bảng 'categoryofjob'
//            pstmtCategoryJob = conn.prepareStatement(sqlInsertCategoryJob);
//            for (String categoryName : selectedCategoryNames) {
//                Integer categoryId = categoryMap.get(categoryName);
//                if (categoryId != null) {
//                    pstmtCategoryJob.setInt(1, newJobId);
//                    pstmtCategoryJob.setInt(2, categoryId);
//                    pstmtCategoryJob.addBatch(); // Thêm vào batch để thực thi cùng lúc
//                }
//            }
//            pstmtCategoryJob.executeBatch(); // Thực thi tất cả các lệnh insert cho categoryofjob
//
//            conn.commit(); // Hoàn thành transaction
//            JOptionPane.showMessageDialog(this, "Đăng tin tuyển dụng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//            clearForm();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            if (conn != null) {
//                try {
//                    conn.rollback(); // Rollback nếu có lỗi
//                } catch (SQLException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            JOptionPane.showMessageDialog(this, "Lỗi khi đăng tin: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        } finally {
//            try {
//                if (generatedKeys != null) generatedKeys.close();
//                if (pstmtJob != null) pstmtJob.close();
//                if (pstmtCategoryJob != null) pstmtCategoryJob.close();
//                if (conn != null) {
//                    conn.setAutoCommit(true); // Trả về trạng thái auto-commit mặc định
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
    }
}