 package UI;
 import Util.Response;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import BLL.UserBLL;

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
    private JCheckBox chkShowPassword;

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
        

        chkShowPassword = new JCheckBox("Hiện mật khẩu");
        
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(lbOldPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(pfOldPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(lbNewPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; formPanel.add(pfNewPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(lbConfirmPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(pfConfirmPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; formPanel.add(new JLabel(""), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; formPanel.add(chkShowPassword, gbc);

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
        }
        if (e.getSource() == btnSaveChanges) {
            handleChangePassword();
        }
        if (e.getSource() == chkShowPassword) {
        	if (chkShowPassword.isSelected()) {
                pfOldPassword.setEchoChar((char) 0);
                pfNewPassword.setEchoChar((char) 0);
                pfConfirmPassword.setEchoChar((char) 0);
            } else {
                pfOldPassword.setEchoChar('•');
                pfNewPassword.setEchoChar('•');
                pfConfirmPassword.setEchoChar('•');
            }
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
        UserBLL updatePW = new UserBLL();
        Response r = updatePW.updatePassword(userId, oldPassword, newPassword);
        if(r.isSuccess()) {
          pfOldPassword.setText("");
          pfNewPassword.setText("");
          pfConfirmPassword.setText("");
        }
        JOptionPane.showMessageDialog(this, r.getMessage());
    }
}