package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.sql.*;
// import your.package.DBConnection;
// import your.package.model.Job; // Model cho Job

public class JobApprovalPanel extends JPanel implements ActionListener {

    private JTable tblJobsForApproval;
    private DefaultTableModel jobApprovalTableModel;
    private JButton btnApproveJob, btnRejectJob, btnViewJobDetailsByAdmin, btnEditJobByAdmin, btnDeleteJobByAdmin;
    private JComboBox<String> cbJobStatusFilter; // Lọc theo trạng thái JOB

    private int currentSelectedJobIdForAction = -1;
    // private Job currentSelectedJobObject; // Lưu trữ object Job được chọn để xem chi tiết hoặc sửa
    private int adminId;

    public JobApprovalPanel(int adminId) {
        this.adminId = adminId;
        setLayout(new BorderLayout(10, 10));
         setBorder(BorderFactory.createTitledBorder(null, "Quản lý & Phê duyệt Bài đăng Tuyển dụng",
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 102, 204)));
        setBackground(Color.WHITE);

        // --- Panel Controls (Filter, Buttons) ---
        JPanel topControlPanel = new JPanel(new BorderLayout(10,5));
        topControlPanel.setBackground(Color.WHITE);
        topControlPanel.setBorder(new EmptyBorder(5,0,10,0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Lọc trạng thái JOB:"));
        // Giả sử job_status: 0-Chờ duyệt, 1-Đã duyệt (Public), 2-Bị từ chối, 3-Đã ẩn bởi HR, 4-Đã đóng/Hết hạn
        cbJobStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Chờ duyệt", "Đã duyệt", "Bị từ chối", "Đã ẩn", "Đã đóng"});
        cbJobStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbJobStatusFilter.addActionListener(e -> loadJobsForApproval());
        filterPanel.add(cbJobStatusFilter);
        topControlPanel.add(filterPanel, BorderLayout.WEST);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionButtonPanel.setBackground(Color.WHITE);
        btnApproveJob = createCrudButton("Phê duyệt");
        btnRejectJob = createCrudButton("Từ chối");
        btnViewJobDetailsByAdmin = createCrudButton("Xem Chi tiết");
        btnEditJobByAdmin = createCrudButton("Sửa (Admin)");
        btnDeleteJobByAdmin = createCrudButton("Xóa (Admin)");

        // Ban đầu vô hiệu hóa các nút cho đến khi có job được chọn
        enableActionButtons(false, false); // Chỉ enable nút View/Edit/Delete chung

        actionButtonPanel.add(btnApproveJob);
        actionButtonPanel.add(btnRejectJob);
        actionButtonPanel.add(btnViewJobDetailsByAdmin);
        actionButtonPanel.add(btnEditJobByAdmin);
        actionButtonPanel.add(btnDeleteJobByAdmin);
        topControlPanel.add(actionButtonPanel, BorderLayout.EAST);
        add(topControlPanel, BorderLayout.NORTH);


        // --- Table hiển thị Jobs cần review/quản lý ---
        // Cột: ID Việc, Tên Việc, Công ty, Người đăng (HR ID/Tên), Ngày đăng, Ngày hết hạn, Trạng thái
        String[] jobReviewColumns = {"ID", "Tên Công việc", "Công ty", "HR Đăng", "Ngày đăng", "Hạn nộp", "Trạng thái"};
        jobApprovalTableModel = new DefaultTableModel(jobReviewColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblJobsForApproval = new JTable(jobApprovalTableModel);
        styleTable(tblJobsForApproval); // Dùng hàm style chung
        tblJobsForApproval.getColumn("Trạng thái").setCellRenderer(new JobStatusCellRenderer()); // Custom renderer cho cột trạng thái

        tblJobsForApproval.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblJobsForApproval.getSelectedRow() != -1) {
                int selectedRow = tblJobsForApproval.getSelectedRow();
                currentSelectedJobIdForAction = Integer.parseInt(tblJobsForApproval.getValueAt(selectedRow, 0).toString());
                String currentStatus = tblJobsForApproval.getValueAt(selectedRow, 6).toString();

                // TODO: Lấy full Job object nếu cần cho việc xem chi tiết hoặc sửa
                // currentSelectedJobObject = getFullJobDetailsFromDB(currentSelectedJobIdForAction);

                enableActionButtons(true, "Chờ duyệt".equalsIgnoreCase(currentStatus));
            } else {
                currentSelectedJobIdForAction = -1;
                // currentSelectedJobObject = null;
                enableActionButtons(false, false);
            }
        });
        add(new JScrollPane(tblJobsForApproval), BorderLayout.CENTER);

        // Action Listeners
        btnApproveJob.addActionListener(this);
        btnRejectJob.addActionListener(this);
        btnViewJobDetailsByAdmin.addActionListener(this);
        btnEditJobByAdmin.addActionListener(this);
        btnDeleteJobByAdmin.addActionListener(this);

        loadJobsForApproval(); // Tải dữ liệu
    }

    private void enableActionButtons(boolean isJobSelected, boolean isPendingApproval) {
        btnViewJobDetailsByAdmin.setEnabled(isJobSelected);
        btnEditJobByAdmin.setEnabled(isJobSelected);
        btnDeleteJobByAdmin.setEnabled(isJobSelected);

        btnApproveJob.setEnabled(isJobSelected && isPendingApproval);
        btnRejectJob.setEnabled(isJobSelected && isPendingApproval);
    }

    // Copy hàm styleTable và createCrudButton
    private void styleTable(JTable table) { /* ... như trên ... */
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.getTableHeader().setForeground(new Color(50,50,50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 200, 200));
    }
    private JButton createCrudButton(String text) { /* ... như trên ... */
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

    public void loadJobsForApproval() {
        jobApprovalTableModel.setRowCount(0);
        String selectedStatusFilter = (String) cbJobStatusFilter.getSelectedItem();
        int statusValueToFilter = -99; // Giá trị đặc biệt cho "Tất cả"
        // Ánh xạ tên trạng thái sang giá trị số trong CSDL
        // 0: Chờ duyệt (Pending), 1: Đã duyệt (Approved/Public), 2: Bị từ chối (Rejected)
        // 3: Đã ẩn bởi HR (Hidden), 4: Đã đóng/Hết hạn (Closed/Expired)
        if ("Chờ duyệt".equals(selectedStatusFilter)) statusValueToFilter = 0;
        else if ("Đã duyệt".equals(selectedStatusFilter)) statusValueToFilter = 1;
        else if ("Bị từ chối".equals(selectedStatusFilter)) statusValueToFilter = 2;
        else if ("Đã ẩn".equals(selectedStatusFilter)) statusValueToFilter = 3;
        else if ("Đã đóng".equals(selectedStatusFilter)) statusValueToFilter = 4;


        // TODO: Load jobs từ CSDL, join với bảng users (HR) để lấy tên người đăng.
        // Filter theo job_status nếu statusValueToFilter != -99.
        // Ví dụ:
        // String sql = "SELECT j.job_id, j.job_name, j.company_name, u.username AS hr_username, " +
        //              "j.created_at, j.deadline, j.job_status " +
        //              "FROM job j LEFT JOIN users u ON j.hr_id = u.user_id "; // hr_id là id của HR đăng bài
        // if (statusValueToFilter != -99) {
        //     sql += " WHERE j.job_status = " + statusValueToFilter;
        // }
        // sql += " ORDER BY j.created_at DESC";
        //
        // try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        //     ResultSet rs = pstmt.executeQuery();
        //     while (rs.next()) {
        //         jobApprovalTableModel.addRow(new Object[]{
        //             rs.getInt("job_id"),
        //             rs.getString("job_name"),
        //             rs.getString("company_name"),
        //             rs.getString("hr_username") != null ? rs.getString("hr_username") : "N/A",
        //             rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime().toLocalDate().toString() : "N/A",
        //             rs.getTimestamp("deadline") != null ? rs.getTimestamp("deadline").toLocalDateTime().toLocalDate().toString() : "N/A",
        //             mapJobStatusToString(rs.getInt("job_status"))
        //         });
        //     }
        // } catch (SQLException e) {
        //    JOptionPane.showMessageDialog(this, "Lỗi tải danh sách JOB: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        //    e.printStackTrace();
        // }

        // --- Dữ liệu mẫu ---
        if (statusValueToFilter == -99 || statusValueToFilter == 0)
            jobApprovalTableModel.addRow(new Object[]{201, "Frontend Dev (VueJS)", "Lazada Group", "hr_lazada", "2025-05-16", "2025-06-15", "Chờ duyệt"});
        if (statusValueToFilter == -99 || statusValueToFilter == 0)
            jobApprovalTableModel.addRow(new Object[]{202, "Data Analyst Intern", "KMS Technology", "hr_kms", "2025-05-15", "2025-05-30", "Chờ duyệt"});
        if (statusValueToFilter == -99 || statusValueToFilter == 1)
            jobApprovalTableModel.addRow(new Object[]{101, "Java Developer Full-stack", "FPT Software", "hr_fpt", "2025-05-10", "2025-06-10", "Đã duyệt"});
        if (statusValueToFilter == -99 || statusValueToFilter == 2)
            jobApprovalTableModel.addRow(new Object[]{203, "Content Writer (Part-time)", "TopCV", "hr_topcv", "2025-05-12", "2025-05-20", "Bị từ chối"});
         if (statusValueToFilter == -99 || statusValueToFilter == 3)
            jobApprovalTableModel.addRow(new Object[]{204, "Senior PHP Developer", "Sendo.vn", "hr_sendo", "2025-04-20", "2025-05-20", "Đã ẩn"});
        // --- Kết thúc dữ liệu mẫu ---

        tblJobsForApproval.clearSelection();
        enableActionButtons(false, false);
    }

    private String mapJobStatusToString(int status) {
        switch (status) {
            case 0: return "Chờ duyệt";
            case 1: return "Đã duyệt";
            case 2: return "Bị từ chối";
            case 3: return "Đã ẩn"; // Do HR tự ẩn
            case 4: return "Đã đóng"; // Hết hạn hoặc HR đóng
            default: return "Không xác định (" + status + ")";
        }
    }
    private int mapJobStatusToInt(String statusString) {
        switch (statusString) {
            case "Chờ duyệt": return 0;
            case "Đã duyệt": return 1;
            case "Bị từ chối": return 2;
            case "Đã ẩn": return 3;
            case "Đã đóng": return 4;
            default: return -1; // Hoặc throw exception
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (currentSelectedJobIdForAction == -1 &&
            (source == btnApproveJob || source == btnRejectJob || source == btnViewJobDetailsByAdmin || source == btnEditJobByAdmin || source == btnDeleteJobByAdmin)) {
             JOptionPane.showMessageDialog(this, "Vui lòng chọn một bài đăng từ danh sách.", "Yêu cầu", JOptionPane.WARNING_MESSAGE);
             return;
        }

        if (source == btnApproveJob) {
            // TODO: Cập nhật job_status = 1 (Đã duyệt) và is_public = 1 (nếu có) trong CSDL
            // Ghi lại admin_id người duyệt và thời gian duyệt (approved_by_admin_id, approved_at)
            // String sql = "UPDATE job SET job_status = 1, is_public = 1, approved_by_admin_id = ?, approved_at = NOW() WHERE job_id = ?";
            // ...
            System.out.println("Admin (ID " + adminId + ") phê duyệt Job ID: " + currentSelectedJobIdForAction);
            JOptionPane.showMessageDialog(this, "Đã phê duyệt Job ID: " + currentSelectedJobIdForAction + " (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadJobsForApproval();
        } else if (source == btnRejectJob) {
            String reason = JOptionPane.showInputDialog(this, "Nhập lý do từ chối bài đăng (nếu có):", "Từ chối Bài đăng", JOptionPane.PLAIN_MESSAGE);
            // TODO: Cập nhật job_status = 2 (Bị từ chối) trong CSDL.
            // Ghi lại admin_id người từ chối, thời gian và lý do (reject_reason).
            // String sql = "UPDATE job SET job_status = 2, reject_reason = ?, approved_by_admin_id = ?, approved_at = NOW() WHERE job_id = ?"; // approved_at ở đây có thể hiểu là processed_at
            // ...
            System.out.println("Admin (ID " + adminId + ") từ chối Job ID: " + currentSelectedJobIdForAction + ". Lý do: " + (reason != null ? reason : "Không có"));
            JOptionPane.showMessageDialog(this, "Đã từ chối Job ID: " + currentSelectedJobIdForAction + " (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadJobsForApproval();
        } else if (source == btnViewJobDetailsByAdmin) {
            // TODO: Lấy toàn bộ thông tin chi tiết của job (từ currentSelectedJobObject hoặc query lại CSDL)
            // Hiển thị trong một JDialog không cho sửa (chỉ xem).
            // JobDetailViewDialog detailDialog = new JobDetailViewDialog((Frame) SwingUtilities.getWindowAncestor(this), currentSelectedJobObject);
            // detailDialog.setVisible(true);
            JOptionPane.showMessageDialog(this, "Xem chi tiết Job ID: " + currentSelectedJobIdForAction + "\n(Cần Dialog hiển thị đầy đủ thông tin Job, không cho sửa)", "Chi tiết Job", JOptionPane.INFORMATION_MESSAGE);
        } else if (source == btnEditJobByAdmin) {
            // Admin có thể sửa bất kỳ bài đăng nào.
            // TODO: Lấy Job object hiện tại. Mở form sửa job tương tự như JobFormDialog bên CategoryManagementPanel
            // nhưng có thể có thêm các trường admin mới có quyền sửa (ví dụ: job_status, is_featured,...)
            // AdminJobEditFormDialog editDialog = new AdminJobEditFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa bài đăng (Admin)", currentSelectedJobObject, adminId);
            // editDialog.setVisible(true);
            // if(editDialog.isSucceeded()){
            //     loadJobsForApproval();
            // }
            JOptionPane.showMessageDialog(this, "Admin sửa Job ID: " + currentSelectedJobIdForAction + "\n(Cần form sửa chi tiết cho Admin, có thể bao gồm cả trạng thái, is_public...)", "Sửa Job (Admin)", JOptionPane.INFORMATION_MESSAGE);
        } else if (source == btnDeleteJobByAdmin) {
             String jobNameToDelete = tblJobsForApproval.getValueAt(tblJobsForApproval.getSelectedRow(), 1).toString();
             int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn XÓA VĨNH VIỄN bài đăng '" + jobNameToDelete + "' (ID: " + currentSelectedJobIdForAction + ")?\n" +
                        "Hành động này KHÔNG THỂ hoàn tác và sẽ xóa mọi dữ liệu liên quan (ứng tuyển,...).",
                        "Xác nhận xóa vĩnh viễn JOB", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                // TODO: Xóa job khỏi CSDL (và các bảng liên quan như categoryofjob, saved_jobs, applications).
                // Cần thực hiện trong transaction.
                // String deleteFromCategoryOfJob = "DELETE FROM categoryofjob WHERE job_id = ?";
                // String deleteFromSavedJobs = "DELETE FROM saved_jobs WHERE job_id = ?";
                // String deleteFromApplications = "DELETE FROM job_applications WHERE job_id = ?";
                // String deleteJob = "DELETE FROM job WHERE job_id = ?";
                // ...
                System.out.println("Admin (ID " + adminId + ") xóa vĩnh viễn Job ID: " + currentSelectedJobIdForAction);
                JOptionPane.showMessageDialog(this, "Đã xóa Job ID: " + currentSelectedJobIdForAction + " (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadJobsForApproval();
            }
        }
    }

    // Custom Renderer để hiển thị màu sắc cho cột Trạng thái Job
    class JobStatusCellRenderer extends JLabel implements TableCellRenderer {
        public JobStatusCellRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(new EmptyBorder(2, 5, 2, 5));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText(value.toString());
            // 0: Chờ duyệt, 1: Đã duyệt, 2: Bị từ chối, 3: Đã ẩn, 4: Đã đóng
            String status = value.toString();
            if ("Chờ duyệt".equalsIgnoreCase(status)) {
                setBackground(new Color(255, 193, 7)); // Amber
                setForeground(Color.DARK_GRAY);
            } else if ("Đã duyệt".equalsIgnoreCase(status)) {
                setBackground(new Color(76, 175, 80)); // Green
                setForeground(Color.WHITE);
            } else if ("Bị từ chối".equalsIgnoreCase(status)) {
                setBackground(new Color(244, 67, 54)); // Red
                setForeground(Color.WHITE);
            } else if ("Đã ẩn".equalsIgnoreCase(status)) {
                setBackground(new Color(158, 158, 158)); // Grey
                setForeground(Color.WHITE);
            } else if ("Đã đóng".equalsIgnoreCase(status)) {
                setBackground(new Color(96, 125, 139)); // Blue Grey
                setForeground(Color.WHITE);
            } else { // Không xác định
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }

            if (isSelected) {
                // Giữ màu nền trạng thái ngay cả khi được chọn, nhưng có thể làm mờ đi một chút
                // hoặc thay đổi màu chữ để nổi bật.
                Color currentBg = getBackground();
                setBackground(currentBg.darker()); // Làm tối màu nền một chút khi chọn
                // setForeground(table.getSelectionForeground()); // Hoặc giữ màu chữ gốc
            }
            return this;
        }
    }
}