package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import BLL.*;
import DTO.*;
import Util.Response;
import java.util.*;
import java.util.List;

public class CategoryManagementPanel extends JPanel implements ActionListener {

    private JTable tblCategories, tblJobsInCategory;
    private DefaultTableModel categoryTableModel, jobTableModel;
    private JButton btnAddCategory, btnEditCategory, btnDeleteCategory;
    private JButton btnAddJobToCategory, btnEditJobInCategory, btnDeleteJobInCategory;

    private JSplitPane splitPane;
    private JPanel categoryCrudPanel, jobCrudPanel; // Panels chứa các nút CRUD

    private int currentSelectedCategoryId = -1;
    private String currentSelectedCategoryName = "";
    private int currentSelectedJobId = -1;
    private int adminId;

    private CategoryBLL categoryBLL;
    private JobBLL jobBLL;

    public CategoryManagementPanel(int adminId) {
        this.adminId = adminId;
        try {
            categoryBLL = new CategoryBLL();
            jobBLL = new JobBLL();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo lớp xử lý nghiệp vụ: " + e.getMessage(),
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // --- Panel quản lý ngành nghề (Bên trái của SplitPane) ---
        JPanel categoryListPanel = new JPanel(new BorderLayout(5, 5));
        categoryListPanel.setBorder(BorderFactory.createTitledBorder(null, "Danh sách Ngành nghề",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 102, 204)));
        categoryListPanel.setBackground(Color.WHITE);

        String[] categoryColumns = {"ID", "Tên Ngành nghề"};
        categoryTableModel = new DefaultTableModel(categoryColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCategories = new JTable(categoryTableModel);
        styleTable(tblCategories);
        tblCategories.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategories.getSelectedRow() != -1) {
                int selectedRow = tblCategories.getSelectedRow();
                currentSelectedCategoryId = Integer.parseInt(tblCategories.getValueAt(selectedRow, 0).toString());
                currentSelectedCategoryName = tblCategories.getValueAt(selectedRow, 1).toString();

                btnEditCategory.setEnabled(true);
                btnDeleteCategory.setEnabled(true);
                btnAddJobToCategory.setEnabled(true); // Cho phép thêm job khi đã chọn ngành
                loadJobsForCategory(currentSelectedCategoryId);
                ((TitledBorder) ((JPanel)splitPane.getRightComponent()).getBorder())
                    .setTitle("Công việc trong ngành: " + currentSelectedCategoryName);
                ((JPanel)splitPane.getRightComponent()).repaint();

            } else {
                currentSelectedCategoryId = -1;
                currentSelectedCategoryName = "";
                btnEditCategory.setEnabled(false);
                btnDeleteCategory.setEnabled(false);
                btnAddJobToCategory.setEnabled(false);
                jobTableModel.setRowCount(0); // Xóa bảng công việc
                 ((TitledBorder) ((JPanel)splitPane.getRightComponent()).getBorder())
                    .setTitle("Công việc trong ngành (Chọn một ngành)");
                ((JPanel)splitPane.getRightComponent()).repaint();
            }
             // Reset job selection and buttons
            tblJobsInCategory.clearSelection();
            btnEditJobInCategory.setEnabled(false);
            btnDeleteJobInCategory.setEnabled(false);
        });
        categoryListPanel.add(new JScrollPane(tblCategories), BorderLayout.CENTER);

        categoryCrudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        categoryCrudPanel.setBackground(Color.WHITE);
        btnAddCategory = createCrudButton("Thêm Ngành");
        btnEditCategory = createCrudButton("Sửa Ngành");
        btnDeleteCategory = createCrudButton("Xóa Ngành");
        btnEditCategory.setEnabled(false);
        btnDeleteCategory.setEnabled(false);
        categoryCrudPanel.add(btnAddCategory);
        categoryCrudPanel.add(btnEditCategory);
        categoryCrudPanel.add(btnDeleteCategory);
        categoryListPanel.add(categoryCrudPanel, BorderLayout.SOUTH);


        // --- Panel quản lý công việc trong ngành (Bên phải của SplitPane) ---
        JPanel jobListPanel = new JPanel(new BorderLayout(5, 5));
         jobListPanel.setBorder(BorderFactory.createTitledBorder(null, "Công việc trong ngành (Chọn một ngành)",
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 102, 204)));
        jobListPanel.setBackground(Color.WHITE);

        String[] jobColumns = {"ID Việc", "Tên Công việc", "Công ty", "Địa điểm"}; // Thêm cột nếu cần
        jobTableModel = new DefaultTableModel(jobColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblJobsInCategory = new JTable(jobTableModel);
        styleTable(tblJobsInCategory);
        tblJobsInCategory.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblJobsInCategory.getSelectedRow() != -1) {
                currentSelectedJobId = Integer.parseInt(tblJobsInCategory.getValueAt(tblJobsInCategory.getSelectedRow(), 0).toString());
                btnEditJobInCategory.setEnabled(true);
                btnDeleteJobInCategory.setEnabled(true);
            } else {
                currentSelectedJobId = -1;
                btnEditJobInCategory.setEnabled(false);
                btnDeleteJobInCategory.setEnabled(false);
            }
        });
        jobListPanel.add(new JScrollPane(tblJobsInCategory), BorderLayout.CENTER);

        jobCrudPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        jobCrudPanel.setBackground(Color.WHITE);
        btnAddJobToCategory = createCrudButton("Thêm Việc vào Ngành");
        btnEditJobInCategory = createCrudButton("Sửa Việc");
        btnDeleteJobInCategory = createCrudButton("Xóa Việc");
        btnAddJobToCategory.setEnabled(false);
        btnEditJobInCategory.setEnabled(false);
        btnDeleteJobInCategory.setEnabled(false);
        jobCrudPanel.add(btnAddJobToCategory);
        jobCrudPanel.add(btnEditJobInCategory);
        jobCrudPanel.add(btnDeleteJobInCategory);
        jobListPanel.add(jobCrudPanel, BorderLayout.SOUTH);


        // --- SplitPane để chia đôi màn hình ---
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, categoryListPanel, jobListPanel);
        splitPane.setDividerLocation(450); // Vị trí thanh chia ban đầu
        splitPane.setResizeWeight(0.4); // Tỉ lệ khi resize
        add(splitPane, BorderLayout.CENTER);

        // Thêm action listeners
        btnAddCategory.addActionListener(this);
        btnEditCategory.addActionListener(this);
        btnDeleteCategory.addActionListener(this);
        btnAddJobToCategory.addActionListener(this);
        btnEditJobInCategory.addActionListener(this);
        btnDeleteJobInCategory.addActionListener(this);

        loadInitialData(); // Tải dữ liệu ban đầu
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(new Color(50,50,50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 200, 200));
        // Thêm padding cho cell
        DefaultCellEditor editor = (DefaultCellEditor) table.getDefaultEditor(Object.class);
        if (editor != null) {
            Component C = editor.getComponent();
            if (C instanceof JTextField) {
                ((JTextField)C).setBorder(new EmptyBorder(0,5,0,5));
            }
        }
    }

    private JButton createCrudButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(new Color(230,230,230));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return button;
    }

    public void loadInitialData() {
        loadCategories();
        // Không load jobs vội, chỉ khi nào user chọn một category
    }

    private void loadCategories() {
        categoryTableModel.setRowCount(0); // Xóa dữ liệu cũ
        try {
            List<CategoryDTO> categories = new ArrayList<CategoryDTO>();
            categories= categoryBLL.getAllCategories();
            if (categories != null) {
                for (CategoryDTO category : categories) {
                    categoryTableModel.addRow(new Object[]{
                        category.getCategoryId(),
                        category.getCategoryName()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi tải danh sách ngành nghề: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        tblCategories.clearSelection(); // Bỏ chọn dòng hiện tại (nếu có)
        btnEditCategory.setEnabled(false);
        btnDeleteCategory.setEnabled(false);
        btnAddJobToCategory.setEnabled(false);
        jobTableModel.setRowCount(0);
    }

    private void loadJobsForCategory(int categoryId) {
        jobTableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (categoryId == -1) return;

        try {
            List<JobDTO> jobs = new ArrayList<JobDTO>();
            		jobs = categoryBLL.getJobsByCategory(categoryId);
            if (jobs != null) {
                for (JobDTO job : jobs) {
                    jobTableModel.addRow(new Object[]{
                        job.getJobId(),
                        job.getJobName(),
                        job.getCompanyName(),
                        job.getAddress()
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi tải danh sách công việc: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        tblJobsInCategory.clearSelection();
        btnEditJobInCategory.setEnabled(false);
        btnDeleteJobInCategory.setEnabled(false);
    }

    private void showCategoryDialog(String title, CategoryDTO category) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lbName = new JLabel("Tên ngành nghề:");
        JTextField txtName = new JTextField(category != null ? category.getCategoryName() : "", 20);
        inputPanel.add(lbName);
        inputPanel.add(txtName);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        dialog.add(inputPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        btnSave.addActionListener(e -> {
            String categoryName = txtName.getText().trim();
            if (categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                    "Vui lòng nhập tên ngành nghề!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Response success;
                if (category == null) {
                    // Thêm mới
                    success = categoryBLL.addCategory(categoryName);
                    if (success.isSuccess()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Thêm ngành nghề thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadCategories(); // Tải lại danh sách
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Không thể thêm ngành nghề. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Cập nhật
                    category.setCategoryName(categoryName);
                    success = categoryBLL.addCategory(category.getCategoryName());
                    if (success.isSuccess()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Cập nhật ngành nghề thành công!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadCategories(); // Tải lại danh sách
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            "Không thể cập nhật ngành nghề. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                    "Lỗi: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddCategory) {
            showCategoryDialog("Thêm ngành nghề mới", null);
        } else if (e.getSource() == btnEditCategory) {
            if (currentSelectedCategoryId != -1) {
                try {
                    CategoryDTO category = categoryBLL.getCategoryById(currentSelectedCategoryId);
                    if (category != null) {
                        showCategoryDialog("Sửa ngành nghề", category);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Không tìm thấy thông tin ngành nghề.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == btnDeleteCategory) {
            if (currentSelectedCategoryId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa ngành nghề này?\n" +
                    "Tất cả công việc trong ngành này cũng sẽ bị xóa!",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Response success = categoryBLL.deleteCategory(currentSelectedCategoryId);
                        if (success.isSuccess()) {
                            JOptionPane.showMessageDialog(this,
                                "Xóa ngành nghề thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            loadCategories(); // Tải lại danh sách
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Không thể xóa ngành nghề. Vui lòng thử lại.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }

        // === Xử lý cho Công việc trong Ngành ===
        else if (e.getSource() == btnAddJobToCategory) {
            if (currentSelectedCategoryId != -1) {
                // TODO: Tạo JobFormDialog để thêm công việc mới
                JOptionPane.showMessageDialog(this, 
                    "Mở form thêm JOB chi tiết cho ngành '" + currentSelectedCategoryName + "'.\n(Cần tạo JobFormDialog.java)", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một ngành trước khi thêm công việc.", 
                    "Yêu cầu", JOptionPane.WARNING_MESSAGE);
            }
        } else if (e.getSource() == btnEditJobInCategory) {
            if (currentSelectedJobId != -1) {
                try {
                    JobDTO job = jobBLL.getJobById(currentSelectedJobId);
                    if (job != null) {
                        // TODO: Tạo JobFormDialog để sửa công việc
                        JOptionPane.showMessageDialog(this, 
                            "Mở form sửa JOB ID: " + currentSelectedJobId + ".\n(Cần tạo JobFormDialog.java)", 
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Không tìm thấy thông tin công việc.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                        "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (e.getSource() == btnDeleteJobInCategory) {
            if (currentSelectedJobId != -1) {
                String jobNameToDelete = tblJobsInCategory.getValueAt(tblJobsInCategory.getSelectedRow(), 1).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa công việc '" + jobNameToDelete + "' (ID: " + currentSelectedJobId + ") khỏi ngành này?\n" +
                    "Nếu công việc này không thuộc ngành nào khác, nó có thể bị xóa hoàn toàn.\n" +
                    "Hành động này KHÔNG THỂ hoàn tác.",
                    "Xác nhận xóa việc", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        Response success = jobBLL.deleteJob(currentSelectedJobId);
                        if (success.isSuccess()) {
                            JOptionPane.showMessageDialog(this,
                                "Xóa công việc thành công!",
                                "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            loadJobsForCategory(currentSelectedCategoryId); // Tải lại danh sách
                        } else {
                            JOptionPane.showMessageDialog(this,
                                "Không thể xóa công việc. Vui lòng thử lại.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Lỗi: " + ex.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    // Bạn cần tạo JobFormDialog.java tương tự như trong hướng dẫn trước,
    // nhận vào Job object (cho sửa) hoặc null (cho thêm mới), categoryId, adminId.
    // Dialog này sẽ chứa các JTextField cho job_name, company_name, description, salary, address, requirements, etc.
    // và thực hiện lệnh INSERT hoặc UPDATE vào bảng 'job' và 'categoryofjob'.
}