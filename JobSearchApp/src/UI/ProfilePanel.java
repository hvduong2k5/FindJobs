 package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilePanel extends JPanel {
    private int userId;

    private JLabel lbUsernameLabel, lbAccountLabel, lbRoleLabel;
    private JTextField txtUsername, txtAccount, txtRole;
    // Thêm các trường khác nếu cần từ bảng user (ví dụ: email, phone nếu có)

    public ProfilePanel(int userId) {
        this.userId = userId;
        initComponents();
        loadUserProfile();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                " Thông tin cá nhân ",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 18),
                new Color(0, 102, 204)
            ),
            new EmptyBorder(15, 20, 15, 20)
        ));
        setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(70, 70, 70);

        lbUsernameLabel = new JLabel("Tên người dùng:");
        lbUsernameLabel.setFont(labelFont);
        lbUsernameLabel.setForeground(labelColor);
        txtUsername = new JTextField(25);
        txtUsername.setFont(textFont);
        txtUsername.setEditable(false); // Chỉ hiển thị, không cho sửa ở đây

        lbAccountLabel = new JLabel("Tài khoản đăng nhập:");
        lbAccountLabel.setFont(labelFont);
        lbAccountLabel.setForeground(labelColor);
        txtAccount = new JTextField(25);
        txtAccount.setFont(textFont);
        txtAccount.setEditable(false);

        lbRoleLabel = new JLabel("Vai trò:");
        lbRoleLabel.setFont(labelFont);
        lbRoleLabel.setForeground(labelColor);
        txtRole = new JTextField(25);
        txtRole.setFont(textFont);
        txtRole.setEditable(false);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lbUsernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(lbAccountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; formPanel.add(txtAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(lbRoleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(txtRole, gbc);

        // Optional: Add an "Edit" button if you want to allow editing later
        // JButton btnEdit = new JButton("Chỉnh sửa thông tin");
        // btnEdit.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE;
        // formPanel.add(btnEdit, gbc);

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadUserProfile() {
        if (this.userId == -1) {
            JOptionPane.showMessageDialog(this, "Không có thông tin người dùng để hiển thị (Khách).", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            txtUsername.setText("Khách");
            txtAccount.setText("N/A");
            txtRole.setText("Khách");
            return;
        }
//
//        String sql = "SELECT user_name, account, role FROM user WHERE user_id = ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            pstmt.setInt(1, this.userId);
//            ResultSet rs = pstmt.executeQuery();
//
//            if (rs.next()) {
//                txtUsername.setText(rs.getString("user_name"));
//                txtAccount.setText(rs.getString("account"));
//                int roleId = rs.getInt("role");
//                String roleName = "Không xác định";
//                switch (roleId) {
//                    case 0: roleName = "Khách (Guest)"; break;
//                    case 1: roleName = "Quản trị viên (Admin)"; break;
//                    case 2: roleName = "Nhà tuyển dụng (HR)"; break;
//                    case 3: roleName = "Người tìm việc (User)"; break;
//                }
//                txtRole.setText(roleName + " (ID: " + roleId + ")");
//            } else {
//                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin người dùng với ID: " + this.userId, "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi khi tải thông tin cá nhân: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//        }
    }
}