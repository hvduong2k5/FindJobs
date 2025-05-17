package UI;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class MainF extends JFrame implements ActionListener {

    private JPanel headerPanel,searchPanel,btnPanel,contentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch,btnLogin,btnRegister,btnSetting,btnSaved;

    private JTable tblEmployers;
    private DefaultTableModel tableModel;
    
    private static int user_id = -1;//guest =-1

    public MainF(String title, int id) {
        super(title);
        user_id = id;
        GUI();
    }

    public void GUI() {
        //header
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM");
        lbTitle.setFont(new Font("Arial", Font.BOLD, 26));
        lbTitle.setForeground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 12, 0));
        headerPanel.add(lbTitle);

        //tìm kiếm+login+register
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        txtSearch = new JTextField(30);
        btnSearch = new JButton("Tìm kiếm");
        
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        btnSetting = new JButton("Cài đặt");
        btnSaved = new JButton("Công ty đã lưu");

        searchPanel.add(new JLabel("Tìm việc:"));
        searchPanel.add(txtSearch);
        searchPanel.add(btnSearch);
        
        btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,10));
        btnPanel.add(btnLogin);
        btnPanel.add(btnRegister);
        btnPanel.add(btnSaved);
        btnPanel.add(btnSetting);
        
        searchPanel.add(btnPanel);

        btnSearch.addActionListener(this);
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        btnSetting.addActionListener(this);
        btnSaved.addActionListener(this);

        //Table 
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createTitledBorder("Danh sách công việc"));

        String[] columns = {"Tên công việc", "Công ty", "Lương", "Địa chỉ", "Mô tả ngắn"};
        tableModel = new DefaultTableModel(columns, 0);
        tblEmployers = new JTable(tableModel);
        tblEmployers.setFillsViewportHeight(true);
        tblEmployers.setRowHeight(28);
        tblEmployers.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tblEmployers.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tblEmployers.setAutoCreateRowSorter(true); // Sắp xếp

        // Canh giữa cột header
        ((DefaultTableCellRenderer) tblEmployers.getTableHeader().getDefaultRenderer())
                .setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(tblEmployers);
        contentPanel.add(scrollPane, BorderLayout.CENTER);


        this.setLayout(new BorderLayout(10, 10));
        this.add(headerPanel, BorderLayout.NORTH);
        this.add(searchPanel, BorderLayout.AFTER_LAST_LINE);
        this.add(contentPanel, BorderLayout.CENTER);

        this.setSize(850, 550);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        btnLogin.setVisible(user_id == -1);
        btnRegister.setVisible(user_id == -1);
        btnSetting.setVisible(user_id >= 0);
        btnSaved.setVisible(user_id >= 0);

        // Tạo menu cài đặt (setting) cho user đã đăng nhập
        JPopupMenu menu = createSettingMenu();
        // Hiển thị menu phía trên nút Cài đặt
        btnSetting.addActionListener(e -> {
            int x = 0;
            int y = -menu.getPreferredSize().height;
            menu.show(btnSetting, x, y);
        });
    }

    // Hàm tạo menu cài đặt cho user
    private JPopupMenu createSettingMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem miProfile = new JMenuItem("Profile");
        JMenuItem miChangePass = new JMenuItem("Đổi mật khẩu");
        JMenuItem miLogout = new JMenuItem("Đăng xuất");
        menu.add(miProfile);
        menu.add(miChangePass);
        menu.add(miLogout);

        // Xử lý đăng xuất
        miLogout.addActionListener(e -> {
            user_id = -1;
            // Quay về giao diện Guest
            this.dispose();
            new MainF("Khách - Tìm kiếm việc làm", -1);
        });
        // Có thể thêm xử lý cho Profile, Đổi mật khẩu ở đây
        return menu;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            this.dispose();
            new LoginF("Đăng nhập hệ thống");
        }
        if (e.getSource() == btnRegister) {
            this.dispose();
            new RegisterF("Đăng ký tài khoản");
        }
        if (e.getSource() == btnSearch) {
            String keyword = txtSearch.getText().trim();
            if (keyword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // --- DEMO KẾT QUẢ TÌM KIẾM ---
            tableModel.setRowCount(0); // Xoá dữ liệu cũ
            tableModel.addRow(new Object[]{"Nhân viên Kinh doanh", "Công ty ABC", "10-15 triệu", "123 Lê Lợi", "Bán hàng, chăm sóc khách"});
            tableModel.addRow(new Object[]{"Lập trình viên Java", "Công ty XYZ", "15-20 triệu", "456 Hai Bà Trưng", "Phát triển phần mềm"});
            tableModel.addRow(new Object[]{"Kế toán tổng hợp", "Công ty KLM", "12-18 triệu", "789 Nguyễn Trãi", "Báo cáo tài chính"});
        }
    }

    public static void main(String[] args) {
        if (user_id >= 0) {
            new MainF("Tìm kiếm việc làm",user_id); // giao diện user
        } else {
            new MainF("Khách - Tìm kiếm việc làm",-1);
        }
    }
}
