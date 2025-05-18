 package UI;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePasswordPanel extends JPanel implements ActionListener {
    private int userId;

    private JLabel lbOldPassword, lbNewPassword, lbConfirmPassword;
    private JPasswordField pfOldPassword, pfNewPassword, pfConfirmPassword;
    private JButton btnSaveChanges, btnClear;

    public ChangePasswordPanel(int userId) {
        this.userId = userId;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
         setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
                " Đổi mật khẩu ",
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
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
        Color labelColor = new Color(70, 70, 70);

        lbOldPassword = new JLabel("Mật khẩu cũ:");
        lbOldPassword.setFont(labelFont);
        lbOldPassword.setForeground(labelColor);
        pfOldPassword = new JPasswordField(20);
        pfOldPassword.setFont(textFont);

        lbNewPassword = new JLabel("Mật khẩu mới:");
        lbNewPassword.setFont(labelFont);
        lbNewPassword.setForeground(labelColor);
        pfNewPassword = new JPasswordField(20);
        pfNewPassword.setFont(textFont);

        lbConfirmPassword = new JLabel("Xác nhận mật khẩu mới:");
        lbConfirmPassword.setFont(labelFont);
        lbConfirmPassword.setForeground(labelColor);
        pfConfirmPassword = new JPasswordField(20);
        pfConfirmPassword.setFont(textFont);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lbOldPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(pfOldPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(lbNewPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; formPanel.add(pfNewPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(lbConfirmPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(pfConfirmPassword, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setOpaque(false);
        btnClear = new JButton("Làm mới");
        btnClear.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnClear.setPreferredSize(new Dimension(120, 35));
        btnClear.addActionListener(this);

        btnSaveChanges = new JButton("Lưu thay đổi");
        btnSaveChanges.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSaveChanges.setBackground(new Color(0, 102, 204));
        btnSaveChanges.setForeground(Color.WHITE);
        btnSaveChanges.setPreferredSize(new Dimension(150, 35));
        btnSaveChanges.addActionListener(this);

        buttonPanel.add(btnClear);
        buttonPanel.add(btnSaveChanges);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnClear) {
            pfOldPassword.setText("");
            pfNewPassword.setText("");
            pfConfirmPassword.setText("");
        } else if (e.getSource() == btnSaveChanges) {
            handleChangePassword();
        }
    }

    private void handleChangePassword() {
        if (this.userId == -1) {
            JOptionPane.showMessageDialog(this, "Chức năng này yêu cầu đăng nhập.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String oldPassword = new String(pfOldPassword.getPassword());
        String newPassword = new String(pfNewPassword.getPassword());
        String confirmPassword = new String(pfConfirmPassword.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            pfNewPassword.requestFocus();
            return;
        }

        // --- QUAN TRỌNG: Xử lý mật khẩu ---
        // Trong thực tế, bạn cần:
        // 1. Lấy mật khẩu ĐÃ BĂM (hashed password) từ CSDL.
        // 2. Băm `oldPassword` người dùng nhập và so sánh với mật khẩu đã băm từ CSDL.
        //    Ví dụ với jBCrypt: if (BCrypt.checkpw(oldPassword, hashedPasswordFromDB)) { ... }
        // 3. Nếu hợp lệ, băm `newPassword` và cập nhật vào CSDL.
        //    Ví dụ: String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        // Vì mục đích minh họa, phần này sẽ so sánh trực tiếp (KHÔNG AN TOÀN)
        // Bạn PHẢI thay thế bằng logic băm mật khẩu.
//
//        String sqlCheckOldPass = "SELECT password FROM user WHERE user_id = ?";
//        String sqlUpdatePass = "UPDATE user SET password = ? WHERE user_id = ?";
//
//        try (Connection conn = DBConnection.getConnection()) {
//            conn.setAutoCommit(false); // Bắt đầu transaction
//
//            // Bước 1: Kiểm tra mật khẩu cũ (KHÔNG AN TOÀN - CẦN BĂM)
//            try (PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckOldPass)) {
//                pstmtCheck.setInt(1, this.userId);
//                ResultSet rs = pstmtCheck.executeQuery();
//                if (rs.next()) {
//                    String currentPasswordFromDB = rs.getString("password");
//                    // **** THAY THẾ PHẦN SO SÁNH NÀY BẰNG BĂM MẬT KHẨU ****
//                    if (!currentPasswordFromDB.equals(oldPassword)) {
//                    // Ví dụ đúng: if (!BCrypt.checkpw(oldPassword, currentPasswordFromDB)) {
//                        JOptionPane.showMessageDialog(this, "Mật khẩu cũ không chính xác.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                        conn.rollback();
//                        return;
//                    }
//                } else {
//                    JOptionPane.showMessageDialog(this, "Không tìm thấy người dùng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                    conn.rollback();
//                    return;
//                }
//            }
//
//            // Bước 2: Cập nhật mật khẩu mới (KHÔNG AN TOÀN - CẦN BĂM)
//            // String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt()); // Ví dụ
//            try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdatePass)) {
//                pstmtUpdate.setString(1, newPassword); // Nên là hashedNewPassword
//                pstmtUpdate.setInt(2, this.userId);
//                int rowsAffected = pstmtUpdate.executeUpdate();
//
//                if (rowsAffected > 0) {
//                    conn.commit();
//                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                    pfOldPassword.setText("");
//                    pfNewPassword.setText("");
//                    pfConfirmPassword.setText("");
//                } else {
//                    conn.rollback();
//                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(this, "Lỗi CSDL khi đổi mật khẩu: " + e.getMessage(), "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
//            // Cân nhắc rollback ở đây nếu conn chưa được đóng
//        }
    }
}