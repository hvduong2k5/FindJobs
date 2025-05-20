package UI;

import BLL.*;
import DTO.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class EmployerMainF extends JFrame implements ActionListener {
    private JPanel headerPanel, controlPanel, mainContentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnSetting, btnCreateJob, btnViewPostedJobs;
    
    private CardLayout cardLayout;
    private JPanel jobListDisplayPanel, createJobPanel;
    private ProfilePanel profilePanel;
    private ChangePasswordPanel changePWPanel;
    
    private JobBLL jobBLL;
    private CategoryBLL categoryBLL;
    private int userId;
    
    public EmployerMainF(int userId) {
        super("Ứng dụng tìm kiếm việc làm - Nhà tuyển dụng");
        this.userId = userId;
        
        try {
            jobBLL = new JobBLL();
            categoryBLL = new CategoryBLL();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Lỗi khởi tạo lớp xử lý nghiệp vụ: " + e.getMessage(),
                "Lỗi nghiêm trọng", JOptionPane.ERROR_MESSAGE);
        }
        
        initLookAndFeel();
        initComponents();
        setupLayout();
        addEventListeners();
    }
    
    private void initLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(1300, 750));
        
        // Header Panel
        headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(0, 102, 204));
        lbTitle = new JLabel("ỨNG DỤNG TÌM KIẾM VIỆC LÀM");
        lbTitle.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lbTitle.setForeground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        headerPanel.add(lbTitle);
        
        // Control Panel
        controlPanel = new JPanel(new BorderLayout(15, 10));
        controlPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        controlPanel.setBackground(new Color(245, 245, 245));
        
        // Search Area
        JPanel searchAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAreaPanel.setOpaque(false);
        JLabel lbSearchPrompt = new JLabel("Tìm kiếm ứng viên:");
        lbSearchPrompt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch = new JTextField(30);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSearch = new JButton("Tìm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setBackground(new Color(60, 179, 113));
        btnSearch.setForeground(Color.WHITE);
        searchAreaPanel.add(lbSearchPrompt);
        searchAreaPanel.add(txtSearch);
        searchAreaPanel.add(btnSearch);
        
        // Buttons Group
        JPanel buttonsGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsGroupPanel.setOpaque(false);
        btnCreateJob = new JButton("Đăng tin tuyển dụng");
        btnViewPostedJobs = new JButton("Tin đã đăng");
        btnSetting = new JButton("Cài đặt");
        
        JButton[] actionButtons = {btnCreateJob, btnViewPostedJobs, btnSetting};
        for (JButton btn : actionButtons) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 220, 220));
            buttonsGroupPanel.add(btn);
        }
        
        controlPanel.add(searchAreaPanel, BorderLayout.WEST);
        controlPanel.add(buttonsGroupPanel, BorderLayout.EAST);
        
        // Main Content Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainContentPanel.setBackground(Color.WHITE);
        
        // Initialize panels
        jobListDisplayPanel = createJobListPanel();
        createJobPanel = new CreateJobPanel(userId);
        profilePanel = new ProfilePanel(userId);
        changePWPanel = new ChangePasswordPanel(userId);
        
        mainContentPanel.add(jobListDisplayPanel, "JobList");
        mainContentPanel.add(createJobPanel, "CreateJob");
        mainContentPanel.add(profilePanel, "Profile");
        mainContentPanel.add(changePWPanel, "ChangePassword");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        cardLayout.show(mainContentPanel, "JobList");
    }
    
    private void addEventListeners() {
        btnSearch.addActionListener(this);
        btnCreateJob.addActionListener(this);
        btnViewPostedJobs.addActionListener(this);
        btnSetting.addActionListener(this);
        
        JPopupMenu menu = createSettingMenu();
        btnSetting.addActionListener(e -> {
            menu.show(btnSetting, 0, -menu.getPreferredSize().height);
        });
    }
    
    private JPanel createJobListPanel() {
        // TODO: Implement job list panel for employer view
        return new JPanel();
    }
    
    private JPopupMenu createSettingMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("Thông tin cá nhân");
        JMenuItem changePWItem = new JMenuItem("Đổi mật khẩu");
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        
        profileItem.addActionListener(e -> cardLayout.show(mainContentPanel, "Profile"));
        changePWItem.addActionListener(e -> cardLayout.show(mainContentPanel, "ChangePassword"));
        logoutItem.addActionListener(e -> {
            dispose();
            new LoginF().setVisible(true);
        });
        
        menu.add(profileItem);
        menu.add(changePWItem);
        menu.addSeparator();
        menu.add(logoutItem);
        
        return menu;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            performSearch(txtSearch.getText().trim());
        } else if (e.getSource() == btnCreateJob) {
            cardLayout.show(mainContentPanel, "CreateJob");
        } else if (e.getSource() == btnViewPostedJobs) {
            cardLayout.show(mainContentPanel, "JobList");
        }
    }
    
    private void performSearch(String keyword) {
        // TODO: Implement search functionality for employers
    }
} 