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
// import java.sql.*; // Cần cho DB operations
// import your.package.DBConnection; // Class kết nối CSDL
// import your.package.model.Category; // Model cho ngành nghề
// import your.package.model.Job; // Model cho công việc

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

    public CategoryManagementPanel(int adminId) {
        this.adminId = adminId;
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
        // TODO: Viết code để tải danh sách ngành nghề từ CSDL
        // Ví dụ:
        // String sql = "SELECT category_id, category_name FROM category ORDER BY category_name";
        // try (Connection conn = DBConnection.getConnection();
        //      PreparedStatement pstmt = conn.prepareStatement(sql);
        //      ResultSet rs = pstmt.executeQuery()) {
        //     while (rs.next()) {
        //         categoryTableModel.addRow(new Object[]{rs.getInt("category_id"), rs.getString("category_name")});
        //     }
        // } catch (SQLException e) {
        //     JOptionPane.showMessageDialog(this, "Lỗi tải danh sách ngành nghề: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        //     e.printStackTrace();
        // }

        // --- Dữ liệu mẫu (xóa khi có CSDL thật) ---
        categoryTableModel.addRow(new Object[]{1, "Công nghệ Thông tin"});
        categoryTableModel.addRow(new Object[]{2, "Kinh doanh & Marketing"});
        categoryTableModel.addRow(new Object[]{3, "Thiết kế & Sáng tạo"});
        // --- Kết thúc dữ liệu mẫu ---

        tblCategories.clearSelection(); // Bỏ chọn dòng hiện tại (nếu có)
        btnEditCategory.setEnabled(false);
        btnDeleteCategory.setEnabled(false);
        btnAddJobToCategory.setEnabled(false);
        jobTableModel.setRowCount(0);
    }

    private void loadJobsForCategory(int categoryId) {
        jobTableModel.setRowCount(0); // Xóa dữ liệu cũ
        if (categoryId == -1) return;

        // TODO: Viết code để tải danh sách công việc thuộc categoryId từ CSDL
        // (Chỉ các thông tin cơ bản để hiển thị trên bảng, chi tiết sẽ xem/sửa ở dialog)
        // Ví dụ:
        // String sql = "SELECT j.job_id, j.job_name, j.company_name, j.address " +
        //              "FROM job j JOIN categoryofjob coj ON j.job_id = coj.job_id " +
        //              "WHERE coj.category_id = ? AND j.is_deleted = 0"; // Giả sử có cột is_deleted
        // try (Connection conn = DBConnection.getConnection();
        //      PreparedStatement pstmt = conn.prepareStatement(sql)) {
        //     pstmt.setInt(1, categoryId);
        //     ResultSet rs = pstmt.executeQuery();
        //     while (rs.next()) {
        //         jobTableModel.addRow(new Object[]{
        //                 rs.getInt("job_id"),
        //                 rs.getString("job_name"),
        //                 rs.getString("company_name"),
        //                 rs.getString("address")
        //         });
        //     }
        // } catch (SQLException e) {
        //     JOptionPane.showMessageDialog(this, "Lỗi tải danh sách việc làm: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        //     e.printStackTrace();
        // }

        // --- Dữ liệu mẫu (xóa khi có CSDL thật) ---
        if (categoryId == 1) {
            jobTableModel.addRow(new Object[]{101, "Java Developer Full-stack", "FPT Software", "Hà Nội, Đà Nẵng"});
            jobTableModel.addRow(new Object[]{102, "Frontend Developer (ReactJS)", "NashTech", "TP. Hồ Chí Minh"});
        } else if (categoryId == 2) {
            jobTableModel.addRow(new Object[]{201, "Chuyên viên Digital Marketing", "Shopee Vietnam", "TP. Hồ Chí Minh"});
        }
        // --- Kết thúc dữ liệu mẫu ---
        tblJobsInCategory.clearSelection();
        btnEditJobInCategory.setEnabled(false);
        btnDeleteJobInCategory.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        // === Xử lý cho Ngành nghề ===
        if (source == btnAddCategory) {
            String newCategoryName = JOptionPane.showInputDialog(this, "Nhập tên ngành nghề mới:", "Thêm Ngành nghề", JOptionPane.PLAIN_MESSAGE);
            if (newCategoryName != null && !newCategoryName.trim().isEmpty()) {
                // TODO: Code thêm ngành mới vào CSDL
                // Kiểm tra tên ngành đã tồn tại chưa
                // String sql = "INSERT INTO category (category_name, created_by_admin_id) VALUES (?, ?)";
                // ...
                System.out.println("Admin (ID " + adminId + ") thêm ngành: " + newCategoryName);
                JOptionPane.showMessageDialog(this, "Đã thêm ngành '" + newCategoryName + "' (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadCategories(); // Tải lại danh sách
            }
        } else if (source == btnEditCategory) {
            if (currentSelectedCategoryId != -1) {
                String currentName = (String) tblCategories.getValueAt(tblCategories.getSelectedRow(), 1);
                String updatedName = (String) JOptionPane.showInputDialog(this, "Cập nhật tên ngành nghề:", "Sửa Ngành nghề",
                        JOptionPane.PLAIN_MESSAGE, null, null, currentName);
                if (updatedName != null && !updatedName.trim().isEmpty() && !updatedName.trim().equals(currentName)) {
                    // TODO: Code cập nhật tên ngành trong CSDL
                    // String sql = "UPDATE category SET category_name = ?, updated_by_admin_id = ?, updated_at = NOW() WHERE category_id = ?";
                    // ...
                    System.out.println("Admin (ID " + adminId + ") sửa ngành ID " + currentSelectedCategoryId + " thành: " + updatedName);
                    JOptionPane.showMessageDialog(this, "Đã sửa ngành (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadCategories(); // Tải lại
                }
            }
        } else if (source == btnDeleteCategory) {
            if (currentSelectedCategoryId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa ngành '" + currentSelectedCategoryName + "'?\n" +
                        "LƯU Ý: Tất cả công việc thuộc ngành này cũng có thể bị ảnh hưởng hoặc xóa theo.\n" +
                        "Hành động này KHÔNG THỂ hoàn tác.",
                        "Xác nhận xóa ngành", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: Code xóa ngành khỏi CSDL. Cần xử lý cẩn thận các khóa ngoại:
                    // 1. Xóa các liên kết trong bảng categoryofjob.
                    // 2. Cân nhắc việc xóa hẳn các job chỉ thuộc ngành này, hoặc gán chúng vào một ngành "Chưa phân loại".
                    //    Hoặc chỉ cho xóa ngành nếu không còn job nào thuộc về nó.
                    // String sqlDeleteLinks = "DELETE FROM categoryofjob WHERE category_id = ?";
                    // String sqlDeleteCategory = "DELETE FROM category WHERE category_id = ?";
                    // ...
                    System.out.println("Admin (ID " + adminId + ") xóa ngành ID: " + currentSelectedCategoryId);
                    JOptionPane.showMessageDialog(this, "Đã xóa ngành (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadCategories(); // Tải lại
                }
            }
        }

        // === Xử lý cho Công việc trong Ngành ===
        else if (source == btnAddJobToCategory) {
            if (currentSelectedCategoryId != -1) {
                // Hiển thị một JDialog tùy chỉnh để nhập thông tin công việc chi tiết
                // JobFormDialog addJobDialog = new JobFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                //                                              "Thêm Việc mới vào ngành: " + currentSelectedCategoryName,
                //                                              null, currentSelectedCategoryId, adminId);
                // addJobDialog.setVisible(true);
                // if (addJobDialog.isSucceeded()) {
                //     loadJobsForCategory(currentSelectedCategoryId); // Tải lại danh sách việc làm
                // }
                JOptionPane.showMessageDialog(this, "Mở form thêm JOB chi tiết cho ngành '" + currentSelectedCategoryName + "'.\n(Cần tạo JobFormDialog.java)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn một ngành trước khi thêm công việc.", "Yêu cầu", JOptionPane.WARNING_MESSAGE);
            }
        } else if (source == btnEditJobInCategory) {
             if (currentSelectedJobId != -1) {
                 // TODO: Lấy thông tin job hiện tại từ CSDL dựa vào currentSelectedJobId
                 // Job jobToEdit = getJobByIdFromDatabase(currentSelectedJobId); // Hàm này bạn cần tự tạo
                 // if (jobToEdit != null) {
                 //    JobFormDialog editJobDialog = new JobFormDialog((Frame) SwingUtilities.getWindowAncestor(this),
                //                                              "Chỉnh sửa thông tin việc làm",
                //                                              jobToEdit, currentSelectedCategoryId, adminId); // categoryId có thể không cần nếu job đã có sẵn
                 //    editJobDialog.setVisible(true);
                 //    if (editJobDialog.isSucceeded()) {
                 //        loadJobsForCategory(currentSelectedCategoryId);
                 //    }
                 // } else {
                 //    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin công việc để sửa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                 // }
                 JOptionPane.showMessageDialog(this, "Mở form sửa JOB ID: " + currentSelectedJobId + ".\n(Cần tạo JobFormDialog.java và lấy data job)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            }
        } else if (source == btnDeleteJobInCategory) {
            if (currentSelectedJobId != -1) {
                String jobNameToDelete = tblJobsInCategory.getValueAt(tblJobsInCategory.getSelectedRow(), 1).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa công việc '" + jobNameToDelete + "' (ID: " + currentSelectedJobId + ") khỏi ngành này?\n" +
                        "Nếu công việc này không thuộc ngành nào khác, nó có thể bị xóa hoàn toàn.\n" +
                        "Hành động này KHÔNG THỂ hoàn tác.",
                        "Xác nhận xóa việc", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: Code xóa công việc.
                    // 1. Xóa liên kết trong categoryofjob: DELETE FROM categoryofjob WHERE job_id = ? AND category_id = ?
                    // 2. Kiểm tra xem job_id này có còn liên kết với category nào khác không.
                    //    Nếu không, cân nhắc xóa hẳn job đó: DELETE FROM job WHERE job_id = ? (và các bảng liên quan như saved_jobs, applications)
                    //    Hoặc đặt is_deleted = 1 cho job đó.
                    // ...
                    System.out.println("Admin (ID " + adminId + ") xóa việc ID: " + currentSelectedJobId + " khỏi ngành ID: " + currentSelectedCategoryId);
                    JOptionPane.showMessageDialog(this, "Đã xóa việc (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadJobsForCategory(currentSelectedCategoryId); // Tải lại
                }
            }
        }
    }
    // Bạn cần tạo JobFormDialog.java tương tự như trong hướng dẫn trước,
    // nhận vào Job object (cho sửa) hoặc null (cho thêm mới), categoryId, adminId.
    // Dialog này sẽ chứa các JTextField cho job_name, company_name, description, salary, address, requirements, etc.
    // và thực hiện lệnh INSERT hoặc UPDATE vào bảng 'job' và 'categoryofjob'.
}