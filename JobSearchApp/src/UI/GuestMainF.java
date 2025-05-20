package UI;

import BLL.*;
import DTO.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GuestMainF extends JFrame implements ActionListener {
    private JPanel headerPanel, controlPanel, mainContentPanel;
    private JLabel lbTitle;
    private JTextField txtSearch;
    private JButton btnSearch, btnLogin, btnRegister;
    
    private CardLayout cardLayout;
    private JPanel categoryListPanel, jobListPanel;
    private JTable tblCategories, tblJobs;
    private DefaultTableModel categoryTableModel, jobTableModel;
    
    private JobBLL jobBLL;
    private CategoryBLL categoryBLL;
    private int selectedCategoryId = -1;
    
    public GuestMainF() {
        super("Ứng dụng tìm kiếm việc làm");
        
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
        
        // Search Area (ẩn ban đầu)
        JPanel searchAreaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchAreaPanel.setOpaque(false);
        JLabel lbSearchPrompt = new JLabel("Tìm kiếm công việc:");
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
        searchAreaPanel.setVisible(false);
        
        // Buttons Group
        JPanel buttonsGroupPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonsGroupPanel.setOpaque(false);
        btnLogin = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        
        JButton[] actionButtons = {btnLogin, btnRegister};
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
        categoryListPanel = createCategoryListPanel();
        jobListPanel = createJobListPanel();
        mainContentPanel.add(categoryListPanel, "CategoryList");
        mainContentPanel.add(jobListPanel, "JobList");
    }
    
    private JPanel createCategoryListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("DANH SÁCH NGÀNH NGHỀ");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Category Table
        String[] columns = {"ID", "Tên ngành"};
        categoryTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCategories = new JTable(categoryTableModel);
        styleTable(tblCategories);
        
        // Chỉ hiển thị cột tên ngành
        tblCategories.getColumnModel().getColumn(0).setMinWidth(0);
        tblCategories.getColumnModel().getColumn(0).setMaxWidth(0);
        tblCategories.getColumnModel().getColumn(0).setWidth(0);
        
        tblCategories.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblCategories.getSelectedRow() != -1) {
                selectedCategoryId = Integer.parseInt(tblCategories.getValueAt(tblCategories.getSelectedRow(), 0).toString());
                loadJobsByCategory(selectedCategoryId);
                cardLayout.show(mainContentPanel, "JobList");
                controlPanel.getComponent(0).setVisible(true); // Hiện thanh tìm kiếm
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblCategories);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load categories
        loadCategories();
        
        return panel;
    }
    
    private JPanel createJobListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        
        // Title and back button
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JButton btnBack = new JButton("← Quay lại");
        btnBack.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            cardLayout.show(mainContentPanel, "CategoryList");
            controlPanel.getComponent(0).setVisible(false); // Ẩn thanh tìm kiếm
        });
        
        JLabel titleLabel = new JLabel("DANH SÁCH VIỆC LÀM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        
        titlePanel.add(btnBack, BorderLayout.WEST);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        panel.add(titlePanel, BorderLayout.NORTH);
        
        // Job Table
        String[] columns = {"ID", "Tiêu đề", "Công ty", "Mức lương", "Địa điểm"};
        jobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblJobs = new JTable(jobTableModel);
        styleTable(tblJobs);
        
        // Ẩn cột ID
        tblJobs.getColumnModel().getColumn(0).setMinWidth(0);
        tblJobs.getColumnModel().getColumn(0).setMaxWidth(0);
        tblJobs.getColumnModel().getColumn(0).setWidth(0);
        
        tblJobs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblJobs.getSelectedRow() != -1) {
                int jobId = Integer.parseInt(tblJobs.getValueAt(tblJobs.getSelectedRow(), 0).toString());
                showJobDetails(jobId);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tblJobs);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadCategories() {
        categoryTableModel.setRowCount(0);
        List<CategoryDTO> categories = categoryBLL.getAllCategories();
        for (CategoryDTO category : categories) {
            categoryTableModel.addRow(new Object[]{
                category.getCategoryId(),
                category.getCategoryName()
            });
        }
    }
    
    private void loadJobsByCategory(int categoryId) {
        jobTableModel.setRowCount(0);
        List<JobDTO> jobs = categoryBLL.getJobsByCategory(categoryId);
        for (JobDTO job : jobs) {
            if (job.isPublic() == 1) { // Chỉ hiển thị job đã duyệt
                jobTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getCompanyName(),
                    String.format("%,.0f VNĐ", job.getSalary()),
                    job.getAddress()
                });
            }
        }
    }
    
    private void showJobDetails(int jobId) {
        JobDTO job = jobBLL.getJobById(jobId);
        if (job != null) {
            JDialog dialog = new JDialog(this, "Chi tiết công việc", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.getContentPane().setBackground(Color.WHITE);
            dialog.setMinimumSize(new Dimension(600, 500));

            // Panel chính
            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Tiêu đề và công ty
            JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            headerPanel.setBackground(Color.WHITE);
            
            JLabel titleLabel = new JLabel(job.getJobName());
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            
            JLabel companyLabel = new JLabel(job.getCompanyName());
            companyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            
            headerPanel.add(titleLabel);
            headerPanel.add(companyLabel);
            mainPanel.add(headerPanel, BorderLayout.NORTH);

            // Thông tin cơ bản
            JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Thông tin cơ bản",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 14)
            ));
            
            JLabel salaryLabel = new JLabel("⚫ Mức lương: " + String.format("%,.0f VNĐ", job.getSalary()));
            JLabel addressLabel = new JLabel("⚫ Địa điểm: " + job.getAddress());
            
            String categories = "";
            if (job.getCategories() != null && !job.getCategories().isEmpty()) {
                categories = job.getCategories().stream()
                    .map(CategoryDTO::getCategoryName)
                    .collect(java.util.stream.Collectors.joining(", "));
            }
            JLabel categoryLabel = new JLabel("⚫ Danh mục: " + categories);

            infoPanel.add(salaryLabel);
            infoPanel.add(addressLabel);
            infoPanel.add(categoryLabel);

            // Mô tả và yêu cầu
            JPanel detailsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.add(createDetailSection("Mô tả công việc", job.getDescription()));
            detailsPanel.add(createDetailSection("Yêu cầu", job.getRequirement()));

            JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
            contentPanel.setBackground(Color.WHITE);
            contentPanel.add(infoPanel, BorderLayout.NORTH);
            contentPanel.add(detailsPanel, BorderLayout.CENTER);

            JScrollPane scrollPane = new JScrollPane(contentPanel);
            scrollPane.setBorder(null);
            mainPanel.add(scrollPane, BorderLayout.CENTER);

            // Panel nút
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton btnLoginToApply = new JButton("Đăng nhập để ứng tuyển");
            btnLoginToApply.setBackground(new Color(0, 102, 204));
            btnLoginToApply.setForeground(Color.WHITE);
            btnLoginToApply.setFocusPainted(false);
            btnLoginToApply.addActionListener(e -> {
                dialog.dispose();
                dispose();
                new LoginF("Đăng nhập").setVisible(true);
            });
            
            JButton btnClose = new JButton("Đóng");
            btnClose.setBackground(new Color(200, 200, 200));
            btnClose.setForeground(Color.WHITE);
            btnClose.setFocusPainted(false);
            btnClose.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(btnLoginToApply);
            buttonPanel.add(btnClose);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            dialog.add(mainPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }

    private JPanel createDetailSection(String title, String content) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            title,
            TitledBorder.DEFAULT_JUSTIFICATION,
            TitledBorder.DEFAULT_POSITION,
            new Font("Segoe UI", Font.BOLD, 14)
        ));

        JTextArea textArea = new JTextArea(content);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(0, 0));
        add(headerPanel, BorderLayout.NORTH);
        add(mainContentPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        cardLayout.show(mainContentPanel, "CategoryList");
    }
    
    private void addEventListeners() {
        btnSearch.addActionListener(this);
        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSearch) {
            performSearch();
        } else if (e.getSource() == btnLogin) {
            dispose();
            new LoginF("Đăng nhập").setVisible(true);
        } else if (e.getSource() == btnRegister) {
            dispose();
            new RegisterF("Đăng ký").setVisible(true);
        }
    }
    
    private void performSearch() {
        if (selectedCategoryId == -1) return;
        
        String keyword = txtSearch.getText().trim();
        jobTableModel.setRowCount(0);
        List<JobDTO> jobs = categoryBLL.getJobsByCategory(selectedCategoryId);
        
        for (JobDTO job : jobs) {
            if (job.isPublic() == 1 && 
                (keyword.isEmpty() || 
                 job.getJobName().toLowerCase().contains(keyword.toLowerCase()) ||
                 job.getCompanyName().toLowerCase().contains(keyword.toLowerCase()))) {
                jobTableModel.addRow(new Object[]{
                    job.getJobId(),
                    job.getJobName(),
                    job.getCompanyName(),
                    String.format("%,.0f VNĐ", job.getSalary()),
                    job.getAddress()
                });
            }
        }
    }
    public static void main(String[] args) {
        new GuestMainF();
    }
} 