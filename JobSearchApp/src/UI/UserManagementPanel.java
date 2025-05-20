package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import DTO.*;
import Util.Response;
import BLL.*;

public class UserManagementPanel extends JPanel implements ActionListener {

    private JTable tblUsers;
    private DefaultTableModel userTableModel;
    private JButton btnAddUser, btnEditUser, btnDeleteUser; 
    private JComboBox<String> cbRoleFilter; 

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
        cbRoleFilter = new JComboBox<>(new String[]{"Tất cả", "Quản lý viên (Admin)","Nhà tuyển dụng (HR)", "Người tìm việc (Ứng viên)"});
        cbRoleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbRoleFilter.addActionListener(e -> loadUsers()); // Tải lại khi filter thay đổi
        filterPanel.add(cbRoleFilter);
        topControlPanel.add(filterPanel, BorderLayout.WEST);

        JPanel buttonActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonActionPanel.setBackground(Color.WHITE);
        btnAddUser = createCrudButton("Thêm Người dùng");
        btnEditUser = createCrudButton("Sửa Thông tin");
        btnDeleteUser = createCrudButton("Xóa Người dùng");

        btnEditUser.setEnabled(false);
        btnDeleteUser.setEnabled(false);

        buttonActionPanel.add(btnAddUser);
        buttonActionPanel.add(btnEditUser);
        buttonActionPanel.add(btnDeleteUser);
        topControlPanel.add(buttonActionPanel, BorderLayout.EAST);
        add(topControlPanel, BorderLayout.NORTH);

        // --- Table hiển thị người dùng ---
        // Thêm các cột cần thiết: ID, Username, Họ Tên, Email, Số ĐT, Vai trò, Trạng thái (Active/Banned)
        String[] userColumns = {"ID", "Tên đăng nhập", "Họ Tên", "Vai trò"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblUsers = new JTable(userTableModel);
        styleTable(tblUsers); // Sử dụng lại hàm styleTable từ CategoryManagementPanel hoặc tạo mới
        tblUsers.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblUsers.getSelectedRow() != -1) {
                currentSelectedUserId = Integer.parseInt(tblUsers.getValueAt(tblUsers.getSelectedRow(), 0).toString());
                btnEditUser.setEnabled(true);
                btnDeleteUser.setEnabled(true);
            } else {
                currentSelectedUserId = -1;
                btnEditUser.setEnabled(false);
                btnDeleteUser.setEnabled(false);
            }
        });
        add(new JScrollPane(tblUsers), BorderLayout.CENTER);

        // Action Listeners
        btnAddUser.addActionListener(this);
        btnEditUser.addActionListener(this);
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


    public void loadUsers() {
        userTableModel.setRowCount(0);
        String selectedRoleFilter = (String) cbRoleFilter.getSelectedItem();
        int roleIdToFilter = 0; 
        if("Quản lý viên (Admin)".equals(selectedRoleFilter)) roleIdToFilter = 1;
        else if ("Nhà tuyển dụng (HR)".equals(selectedRoleFilter)) roleIdToFilter = 2; 
        else if ("Người tìm việc (Ứng viên)".equals(selectedRoleFilter)) roleIdToFilter = 3; 
          UserBLL User = new UserBLL();
          List<UserDTO> listUser = User.getUsersByName("", roleIdToFilter);
          for (UserDTO user : listUser) {
        	  userTableModel.addRow(new Object[]{
        		                     user.getUser_id(),
        		                     user.getAccount(),
        		                     user.getUser_name(),
        		                     user.getRole(),
        		                     });
		}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnAddUser) {
//        	int Id = Integer.parseInt(tblUsers.getValueAt(tblUsers.getSelectedRow(), 0).toString());
            UserFormDialog addUserDialog = new UserFormDialog(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    "Thêm Người dùng mới",
                    -1//add
            );
            addUserDialog.setVisible(true);
            
                loadUsers();
              
            // JOptionPane.showMessageDialog(this, "Mở form thêm USER chi tiết.\n(Cần tạo UserFormDialog.java)", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
        if (source == btnEditUser) {
            if (currentSelectedUserId != -1) {
            	UserFormDialog editUserDialog = new UserFormDialog(
                      (Frame) SwingUtilities.getWindowAncestor(this),
                      "Chỉnh sửa Thông tin Người dùng (ID: " + currentSelectedUserId + ")",
                      currentSelectedUserId
              );
              editUserDialog.setVisible(true);
                  loadUsers(); 
            }
        }
        if(source ==btnDeleteUser) {
        	UserBLL deleteUser = new UserBLL();
//        	JOptionPane.showMessageDialog(this,"hehe");
            Response r = deleteUser.deleteUser(currentSelectedUserId);
            if(r.isSuccess()) {
            	JOptionPane.showMessageDialog(this, r.getMessage());	
            }else {
            	JOptionPane.showMessageDialog(this, r.getMessage());
            }
        }
    }
}