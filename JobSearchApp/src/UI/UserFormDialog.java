package UI;

import DTO.UserDTO;
import Util.Response;
import BLL.UserBLL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserFormDialog extends JDialog implements ActionListener {

    private JTextField txtUsername, txtFullName;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cbRole;
    private JButton btnSave, btnCancel;

//    private UserDTO currentUser; // User hiện tại để chỉnh sửa, null nếu là thêm mới
    private int Id =-1;
    private boolean succeeded = false;
//    private UserBLL userBLL;

    public UserFormDialog(Frame parent, String title, int Id) {
        super(parent, title, true);
        this.Id =Id;
//        this.userBLL = new UserBLL(); // Khởi tạo BLL

        initComponents();
        populateFields(); // Điền thông tin nếu là sửa

        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        mainPanel.setBackground(Color.WHITE);

        // --- Input Fields Panel ---
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Tên đăng nhập (*):"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtUsername, gbc);

        // Password (chỉ hiển thị nếu thêm mới hoặc có cơ chế reset riêng)
        if ( Id == -1) {
            gbc.gridx = 0; gbc.gridy = 1;
            fieldsPanel.add(new JLabel("Mật khẩu (*):"), gbc);
            gbc.gridx = 1;
            txtPassword = new JPasswordField(20);
            txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fieldsPanel.add(txtPassword, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            fieldsPanel.add(new JLabel("Xác nhận mật khẩu (*):"), gbc);
            gbc.gridx = 1;
            txtConfirmPassword = new JPasswordField(20);
            txtConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fieldsPanel.add(txtConfirmPassword, gbc);
        } else {
            // Nếu sửa, không hiển thị password trừ khi có chức năng "Đổi mật khẩu" riêng
            // Hoặc bạn có thể thêm một checkbox "Đổi mật khẩu?" để hiện các trường password
            gbc.gridy = 1; // Bỏ qua hàng password
        }


        // Họ Tên
        gbc.gridx = 0; gbc.gridy++; // Tăng gridy để xuống hàng tiếp theo
        fieldsPanel.add(new JLabel("Họ và Tên (*):"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField(20);
        txtFullName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(txtFullName, gbc);

//        // Email
//        gbc.gridx = 0; gbc.gridy++;
//        fieldsPanel.add(new JLabel("Email:"), gbc);
//        gbc.gridx = 1;
//        txtEmail = new JTextField(20);
//        txtEmail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        fieldsPanel.add(txtEmail, gbc);
//
//        // Số điện thoại
//        gbc.gridx = 0; gbc.gridy++;
//        fieldsPanel.add(new JLabel("Số điện thoại:"), gbc);
//        gbc.gridx = 1;
//        txtPhone = new JTextField(20);
//        txtPhone.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        fieldsPanel.add(txtPhone, gbc);
//
//        // Địa chỉ (giả sử lưu trong user_profile, ở đây đơn giản hóa)
//        gbc.gridx = 0; gbc.gridy++;
//        fieldsPanel.add(new JLabel("Địa chỉ:"), gbc);
//        gbc.gridx = 1;
//        txtAddress = new JTextField(20);
//        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
//        fieldsPanel.add(txtAddress, gbc);


        // Vai trò
        gbc.gridx = 0; gbc.gridy++;
        fieldsPanel.add(new JLabel("Vai trò (*):"), gbc);
        gbc.gridx = 1;
        // Giả sử role_id: 1-Admin, 2-HR, 3-User/Candidate
        // Trong form này, thường Admin sẽ không tạo Admin khác, hoặc chỉ có Super Admin mới làm được
        // Tạm thời cho phép chọn HR hoặc User
        cbRole = new JComboBox<>(new String[]{"Nhà tuyển dụng (HR)", "Người tìm việc (Ứng viên)"});
        cbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldsPanel.add(cbRole, gbc);

        // --- Buttons Panel ---
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.decode("#F0F0F0"));
        buttonsPanel.setBorder(new EmptyBorder(10,0,0,0));

        btnSave = new JButton("Lưu");
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.setBackground(new Color(0, 120, 215));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(this);

        btnCancel = new JButton("Hủy");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.addActionListener(this);

        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnCancel);

        mainPanel.add(fieldsPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void populateFields() {
        if (Id != -1) {
        	UserBLL User = new UserBLL();
        	UserDTO UserInfor = User.getUserById(Id);
        	txtUsername.setText(UserInfor.getAccount());
            txtUsername.setEditable(false); 
            txtFullName.setText(UserInfor.getUser_name());
            if (UserInfor.getRole() == 2) { 
              cbRole.setSelectedItem("Nhà tuyển dụng (HR)");
            } else if (UserInfor.getRole() == 3) { 
              cbRole.setSelectedItem("Người tìm việc (Ứng viên)");
            } else if (UserInfor.getRole() == 1){
              cbRole.setSelectedItem("Quản lý viên (Admin)");
            }
        }
    }

    private int getSelectedRoleId() {
        String selectedRole = (String) cbRole.getSelectedItem();
        if ("Nhà tuyển dụng (HR)".equals(selectedRole)) return 2;
        if ("Người tìm việc (Ứng viên)".equals(selectedRole)) return 3;
        if ("Quản lý viên (Admin)".equals(selectedRole)) return 1;
        return 3; 
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            saveUser();
        } else if (e.getSource() == btnCancel) {
            succeeded = false;
            dispose();
        }
    }

    private void saveUser() {
        // --- Basic Validation ---
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        int roleId = getSelectedRoleId();

        if (username.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập và Họ Tên không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (Id == -1) { // Thêm mới
            String password = new String(txtPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mật khẩu không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu và xác nhận mật khẩu không khớp.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            UserBLL createUser = new UserBLL();
            Response r = createUser.createUser(fullName, username, password, roleId);
            if(r.isSuccess()) {
            	JOptionPane.showMessageDialog(this, r.getMessage());	
            }else {
            	JOptionPane.showMessageDialog(this, r.getMessage());
            }
        } else { // Sửa thông tin
            UserBLL updateUser = new UserBLL();
            boolean r = updateUser.updateUser(Id, fullName, roleId);
            if(r) {
            	JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thành công");	
            }else {
            	JOptionPane.showMessageDialog(this, "Cập nhật thông tin người dùng thất bại");
            }
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

}