 package UI;
import DTO.*;
import BLL.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfilePanel extends JPanel implements ActionListener{
    private int userId;

    private JLabel lbUsernameLabel, lbAccountLabel, lbRoleLabel;
    private JTextField txtUsername, txtAccount, txtRole;
    private JButton btnSaveUser; 

    public ProfilePanel(int userId) {
        this.userId = userId;
        GUI();
        loadUserProfile();
    }

    private void GUI() {
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
        
        btnSaveUser = new JButton("Lưu");
        btnSaveUser.addActionListener(this);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lbUsernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(lbAccountLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; formPanel.add(txtAccount, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(lbRoleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(txtRole, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(btnSaveUser, gbc);
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
        UserDTO currentUser = UserSession.GetInstance().getLoggedInUser();
          txtAccount.setText(currentUser.getAccount());
          txtUsername.setText(currentUser.getUser_name());
	      int roleId = currentUser.getRole();
	      String roleName = "Không xác định";
	      switch (roleId) {
	          case 0: roleName = "Khách (Guest)"; break;
	          case 1: roleName = "Quản trị viên (Admin)"; break;
	          case 2: roleName = "Nhà tuyển dụng (HR)"; break;
	          case 3: roleName = "Người tìm việc (User)"; break;
	      }
	      txtRole.setText(roleName + " (ID: " + roleId + ")");
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == btnSaveUser) {
			UserSession.GetInstance().updateName(txtUsername.getText());
			}
	}
}