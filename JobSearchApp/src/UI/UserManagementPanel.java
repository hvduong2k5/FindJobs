package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// import java.sql.*;
// import your.package.DBConnection;
// import your.package.model.User; // Model cho User

public class UserManagementPanel extends JPanel implements ActionListener {

    private JTable tblUsers;
    private DefaultTableModel userTableModel;
    private JButton btnAddUser, btnEditUser, btnDeleteUser, btnChangeUserRole; // Thêm nút đổi vai trò
    private JComboBox<String> cbRoleFilter; // Lọc theo vai trò

    private int currentSelectedUserId = -1;
    private int adminId;

    public UserManagementPanel(int adminId) {
        this.adminId = adminId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder(null, "Quản lý Tài khoản Người dùng (HR & Ứng viên)",
            TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16), new Color(0, 102, 204)));
        setBackground(Color.WHITE);

        // --- Panel Controls (Filter, Buttons) ---
        JPanel topControlPanel = new JPanel(new BorderLayout(10, 5));
        topControlPanel.setBackground(Color.WHITE);
        topControlPanel.setBorder(new EmptyBorder(5,0,10,0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Lọc theo vai trò:"));
        // Giả sử role_id: 1-Admin, 2-HR, 3-User/Candidate
        cbRoleFilter = new JComboBox<>(new String[]{"Tất cả", "Nhà tuyển dụng (HR)", "Người tìm việc (Ứng viên)"});
        cbRoleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRoleFilter.addActionListener(e -> loadUsers()); // Tải lại khi filter thay đổi
        filterPanel.add(cbRoleFilter);
        topControlPanel.add(filterPanel, BorderLayout.WEST);

        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonActionPanel.setBackground(Color.WHITE);
        btnAddUser = createCrudButton("Thêm Người dùng");
        btnEditUser = createCrudButton("Sửa Thông tin");
        btnChangeUserRole = createCrudButton("Đổi Vai trò");
        btnDeleteUser = createCrudButton("Xóa Người dùng");

        btnEditUser.setEnabled(false);
        btnChangeUserRole.setEnabled(false);
        btnDeleteUser.setEnabled(false);

        buttonActionPanel.add(btnAddUser);
        buttonActionPanel.add(btnEditUser);
        buttonActionPanel.add(btnChangeUserRole);
        buttonActionPanel.add(btnDeleteUser);
        topControlPanel.add(buttonActionPanel, BorderLayout.EAST);
        add(topControlPanel, BorderLayout.NORTH);

        // --- Table hiển thị người dùng ---
        // Thêm các cột cần thiết: ID, Username, Họ Tên, Email, Số ĐT, Vai trò, Trạng thái (Active/Banned)
        String[] userColumns = {"ID", "Tên đăng nhập", "Họ Tên", "Email", "Vai trò", "Trạng thái"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblUsers = new JTable(userTableModel);
        styleTable(tblUsers); // Sử dụng lại hàm styleTable từ CategoryManagementPanel hoặc tạo mới
        tblUsers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsers.getSelectedRow() != -1) {
                currentSelectedUserId = Integer.parseInt(tblUsers.getValueAt(tblUsers.getSelectedRow(), 0).toString());
                btnEditUser.setEnabled(true);
                btnChangeUserRole.setEnabled(true);
                btnDeleteUser.setEnabled(true);
            } else {
                currentSelectedUserId = -1;
                btnEditUser.setEnabled(false);
                btnChangeUserRole.setEnabled(false);
                btnDeleteUser.setEnabled(false);
            }
        });
        add(new JScrollPane(tblUsers), BorderLayout.CENTER);

        // Action Listeners
        btnAddUser.addActionListener(this);
        btnEditUser.addActionListener(this);
        btnChangeUserRole.addActionListener(this);
        btnDeleteUser.addActionListener(this);

        loadUsers(); // Tải dữ liệu ban đầu
    }

    // Copy hàm styleTable và createCrudButton từ CategoryManagementPanel hoặc tạo chung ở 1 utility class
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


    public void loadUsers() {
        userTableModel.setRowCount(0);
        String selectedRoleFilter = (String) cbRoleFilter.getSelectedItem();
        int roleIdToFilter = 0; // 0 = Tất cả
        if ("Nhà tuyển dụng (HR)".equals(selectedRoleFilter)) roleIdToFilter = 2; // Giả sử role_id 2 là HR
        else if ("Người tìm việc (Ứng viên)".equals(selectedRoleFilter)) roleIdToFilter = 3; // Giả sử role_id 3 là User

        // TODO: Load users từ CSDL, áp dụng filter nếu có
        // JOIN với bảng 'user_profile' để lấy họ tên, email và bảng 'role' để lấy tên vai trò.
        // Ví dụ:
        // String sql = "SELECT u.user_id, u.username, up.full_name, up.email, r.role_name, u.is_active " +
        //              "FROM users u " +
        //              "LEFT JOIN user_profile up ON u.user_id = up.user_id " +
        //              "JOIN role r ON u.role_id = r.role_id ";
        // if (roleIdToFilter != 0) {
        //     sql += " WHERE u.role_id = " + roleIdToFilter;
        // }
        // sql += " ORDER BY u.user_id DESC";
        //
        // try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
        //     ResultSet rs = pstmt.executeQuery();
        //     while (rs.next()) {
        //         userTableModel.addRow(new Object[]{
        //             rs.getInt("user_id"),
        //             rs.getString("username"),
        //             rs.getString("full_name"),
        //             rs.getString("email"),
        //             rs.getString("role_name"),
        //             rs.getBoolean("is_active") ? "Hoạt động" : "Bị khóa"
        //         });
        //     }
        // } catch (SQLException e) {
        //    JOptionPane.showMessageDialog(this, "Lỗi tải danh sách người dùng: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        //    e.printStackTrace();
        // }

        // --- Dữ liệu mẫu ---
        if (roleIdToFilter == 0 || roleIdToFilter == 2) {
            userTableModel.addRow(new Object[]{10, "hr_tiki", "HR Department Tiki", "hr@tiki.vn", "Nhà tuyển dụng", "Hoạt động"});
            userTableModel.addRow(new Object[]{12, "hr_grab", "Grab Recruitment", "tuyendung@grab.com", "Nhà tuyển dụng", "Bị khóa"});
        }
        if (roleIdToFilter == 0 || roleIdToFilter == 3) {
            userTableModel.addRow(new Object[]{11, "nguyenvanb", "Nguyễn Văn B", "vanb@gmail.com", "Người tìm việc", "Hoạt động"});
            userTableModel.addRow(new Object[]{13, "tranthic", "Trần Thị C", "thic@outlook.com", "Người tìm việc", "Hoạt động"});
        }
        // --- Kết thúc dữ liệu mẫu ---
        tblUsers.clearSelection();
        btnEditUser.setEnabled(false);
        btnChangeUserRole.setEnabled(false);
        btnDeleteUser.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnAddUser) {
            // Hiển thị JDialog để thêm người dùng mới (nhập username, password, role, và các thông tin profile)
            // UserFormDialog addUserDialog = new UserFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Người dùng mới", null, adminId);
            // addUserDialog.setVisible(true);
            // if (addUserDialog.isSucceeded()) {
            //    loadUsers();
            // }
            JOptionPane.showMessageDialog(this, "Mở form thêm USER chi tiết.\n(Cần tạo UserFormDialog.java)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else if (source == btnEditUser) {
            if (currentSelectedUserId != -1) {
                // TODO: Lấy thông tin user và user_profile hiện tại từ CSDL
                // User userToEdit = getUserAndProfileById(currentSelectedUserId); // Hàm này bạn tự tạo
                // if (userToEdit != null) {
                //    UserFormDialog editUserDialog = new UserFormDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa Thông tin Người dùng", userToEdit, adminId);
                //    editUserDialog.setVisible(true);
                //    if (editUserDialog.isSucceeded()) {
                //        loadUsers();
                //    }
                // }
                 JOptionPane.showMessageDialog(this, "Mở form sửa USER ID: " + currentSelectedUserId + ".\n(Cần UserFormDialog.java và lấy data)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (source == btnChangeUserRole) {
            if (currentSelectedUserId != -1) {
                // Lấy vai trò hiện tại của user
                String currentRoleName = tblUsers.getValueAt(tblUsers.getSelectedRow(), 4).toString();
                // Các lựa chọn vai trò (không bao gồm vai trò hiện tại và Admin)
                Object[] possibleRoles;
                String newRolePrompt;
                if ("Nhà tuyển dụng".equals(currentRoleName)) {
                    possibleRoles = new Object[]{"Người tìm việc"};
                    newRolePrompt = "Chọn vai trò mới cho người dùng (hiện tại là HR):";
                } else if ("Người tìm việc".equals(currentRoleName)) {
                    possibleRoles = new Object[]{"Nhà tuyển dụng"};
                    newRolePrompt = "Chọn vai trò mới cho người dùng (hiện tại là Ứng viên):";
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thay đổi vai trò cho người dùng này từ đây.", "Thông báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String newRoleStr = (String) JOptionPane.showInputDialog(
                        this, newRolePrompt, "Thay đổi Vai trò Người dùng",
                        JOptionPane.PLAIN_MESSAGE, null, possibleRoles, possibleRoles[0]);

                if (newRoleStr != null) {
                    int newRoleId = "Nhà tuyển dụng".equals(newRoleStr) ? 2 : 3; // Ánh xạ lại role_id
                    // TODO: Cập nhật role_id cho user trong CSDL
                    // String sql = "UPDATE users SET role_id = ?, updated_by_admin_id = ?, updated_at = NOW() WHERE user_id = ?";
                    // ...
                    System.out.println("Admin (ID " + adminId + ") đổi vai trò User ID: " + currentSelectedUserId + " thành " + newRoleStr + " (Role ID: " + newRoleId + ")");
                    JOptionPane.showMessageDialog(this, "Đã thay đổi vai trò (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                }
            }
        } else if (source == btnDeleteUser) {
            if (currentSelectedUserId != -1) {
                String userNameToDelete = tblUsers.getValueAt(tblUsers.getSelectedRow(), 1).toString();
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc chắn muốn xóa người dùng '" + userNameToDelete + "' (ID: " + currentSelectedUserId + ")?\n" +
                        "LƯU Ý: Hành động này KHÔNG THỂ hoàn tác và có thể ảnh hưởng đến dữ liệu liên quan (bài đăng, ứng tuyển,...).\n" +
                        "Cân nhắc việc KHÓA TÀI KHOẢN thay vì xóa vĩnh viễn.",
                        "Xác nhận xóa người dùng", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // TODO: Xóa user khỏi CSDL (và các bảng liên quan như user_profile, tokens,...)
                    // Hoặc tốt hơn là đặt cờ is_deleted = 1 hoặc is_active = 0 (soft delete)
                    // String sql = "UPDATE users SET is_deleted = 1, updated_by_admin_id = ?, updated_at = NOW() WHERE user_id = ?"; // Soft delete
                    // Hoặc:
                    // String sqlDeleteProfile = "DELETE FROM user_profile WHERE user_id = ?";
                    // String sqlDeleteUser = "DELETE FROM users WHERE user_id = ?";
                    // ... (cần thực hiện trong transaction)
                    System.out.println("Admin (ID " + adminId + ") xóa User ID: " + currentSelectedUserId);
                     JOptionPane.showMessageDialog(this, "Đã xóa người dùng (Mô phỏng).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers(); // Tải lại
                }
            }
        }
    }
    // Bạn cần tạo UserFormDialog.java để thêm/sửa người dùng.
    // Dialog này sẽ có các trường cho username, password (chỉ khi thêm mới hoặc reset),
    // full_name, email, phone, address (trong user_profile), và lựa chọn role_id.
}