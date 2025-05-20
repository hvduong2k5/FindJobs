package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import DTO.*;
import Util.Response;
import BLL.*;

public class UserManagementPanel extends JPanel implements ActionListener {
    private JTable tblUsers;
    private DefaultTableModel userTableModel;
    private JButton btnAddUser, btnEditUser, btnDeleteUser, btnToggleStatus;
    private JComboBox<String> cbRoleFilter;
    private JTextField txtSearch;
    private JButton btnSearch;
    private int currentSelectedUserId = -1;
    private int adminId;

    public UserManagementPanel(int adminId) {
        this.adminId = adminId;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("QUẢN LÝ NGƯỜI DÙNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Panel tìm kiếm và lọc
        JPanel searchFilterPanel = new JPanel(new BorderLayout(10, 0));
        searchFilterPanel.setBackground(Color.WHITE);
        searchFilterPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch.setBackground(new Color(0, 102, 204));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);

        // Panel lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        cbRoleFilter = new JComboBox<>(new String[]{"Tất cả", "Quản trị viên", "Nhà tuyển dụng", "Ứng viên"});
        cbRoleFilter.setPreferredSize(new Dimension(200, 35));
        cbRoleFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(new JLabel("Vai trò:"));
        filterPanel.add(cbRoleFilter);

        searchFilterPanel.add(searchPanel, BorderLayout.WEST);
        searchFilterPanel.add(filterPanel, BorderLayout.EAST);
        add(searchFilterPanel, BorderLayout.CENTER);

        // Panel nút thao tác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnAddUser = createActionButton("Thêm mới", new Color(40, 167, 69));
        btnEditUser = createActionButton("Chỉnh sửa", new Color(0, 123, 255));
        btnToggleStatus = createActionButton("Khóa/Mở khóa", new Color(255, 193, 7));
        btnDeleteUser = createActionButton("Xóa", new Color(220, 53, 69));

        btnEditUser.setEnabled(false);
        btnToggleStatus.setEnabled(false);
        btnDeleteUser.setEnabled(false);

        buttonPanel.add(btnAddUser);
        buttonPanel.add(btnEditUser);
        buttonPanel.add(btnToggleStatus);
        buttonPanel.add(btnDeleteUser);
        add(buttonPanel, BorderLayout.SOUTH);

        // Bảng người dùng
        String[] columns = {"ID", "Tên đăng nhập", "Họ và tên", "Vai trò"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblUsers = new JTable(userTableModel);
        styleTable(tblUsers);

        // Thêm renderer cho cột vai trò
        tblUsers.getColumnModel().getColumn(3).setCellRenderer(new RoleRenderer());

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

        JScrollPane scrollPane = new JScrollPane(tblUsers);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Thêm action listeners
        btnAddUser.addActionListener(this);
        btnEditUser.addActionListener(this);
        btnToggleStatus.addActionListener(this);
        btnDeleteUser.addActionListener(this);
        btnSearch.addActionListener(this);
        cbRoleFilter.addActionListener(this);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadUsers();
                }
            }
        });

        loadUsers();
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setForeground(new Color(50, 50, 50));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(200, 200, 200));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private class RoleRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            int role = (int) value;
            String roleText = "";
            Color roleColor = null;
            
            switch (role) {
                case 1:
                    roleText = "Quản trị viên";
                    roleColor = new Color(220, 53, 69); // Đỏ
                    break;
                case 2:
                    roleText = "Nhà tuyển dụng";
                    roleColor = new Color(0, 123, 255); // Xanh dương
                    break;
                case 3:
                    roleText = "Ứng viên";
                    roleColor = new Color(40, 167, 69); // Xanh lá
                    break;
                default:
                    roleText = "Không xác định";
                    roleColor = new Color(108, 117, 125); // Xám
            }
            
            label.setText(roleText);
            label.setBackground(roleColor);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setOpaque(true);
            
            return label;
        }
    }

    public void loadUsers() {
        userTableModel.setRowCount(0);
        String searchText = txtSearch.getText().trim();
        String selectedRole = (String) cbRoleFilter.getSelectedItem();
        int roleId = 0;
        
        switch (selectedRole) {
            case "Quản trị viên": roleId = 1; break;
            case "Nhà tuyển dụng": roleId = 2; break;
            case "Ứng viên": roleId = 3; break;
        }

        UserBLL userBLL = new UserBLL();
        List<UserDTO> users = userBLL.getUsersByName(searchText, roleId);
        
        for (UserDTO user : users) {
            userTableModel.addRow(new Object[]{
                user.getUser_id(),
                user.getAccount(),
                user.getUser_name(),
                user.getRole()
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        
        if (source == btnSearch || source == cbRoleFilter) {
            loadUsers();
            return;
        }

        if (currentSelectedUserId == -1) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn một người dùng để thực hiện thao tác!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (source == btnAddUser) {
            UserFormDialog dialog = new UserFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Thêm người dùng mới",
                -1
            );
            dialog.setVisible(true);
            loadUsers();
        }
        else if (source == btnEditUser) {
            UserFormDialog dialog = new UserFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chỉnh sửa thông tin người dùng",
                currentSelectedUserId
            );
            dialog.setVisible(true);
            loadUsers();
        }
        else if (source == btnDeleteUser) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa người dùng này không?\n" +
                "Lưu ý: Hành động này không thể hoàn tác!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                UserBLL userBLL = new UserBLL();
                Response response = userBLL.deleteUser(currentSelectedUserId);
                
                if (response.isSuccess()) {
                    JOptionPane.showMessageDialog(this,
                        "Xóa người dùng thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this,
                        response.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}