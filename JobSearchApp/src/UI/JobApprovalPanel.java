package UI;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;
import DTO.*;
import Util.Response;
import BLL.*;

public class JobApprovalPanel extends JPanel implements ActionListener {
    private JTable tblJobs;
    private DefaultTableModel jobTableModel;
    private JButton btnApprove, btnReject, btnViewDetails, btnDelete;
    private JComboBox<String> cbStatusFilter;
    private JTextField txtSearch;
    private JButton btnSearch;
    private int currentSelectedJobId = -1;
    private int adminId;
    private JobBLL jobBLL;

    public JobApprovalPanel(int adminId) {
        this.adminId = adminId;
        this.jobBLL = new JobBLL();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("QUẢN LÝ BÀI ĐĂNG");
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
        cbStatusFilter = new JComboBox<>(new String[]{"Tất cả", "Chờ duyệt", "Đã duyệt", "Đã từ chối"});
        cbStatusFilter.setPreferredSize(new Dimension(200, 35));
        cbStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(cbStatusFilter);

        searchFilterPanel.add(searchPanel, BorderLayout.WEST);
        searchFilterPanel.add(filterPanel, BorderLayout.EAST);
        add(searchFilterPanel, BorderLayout.CENTER);

        // Panel nút thao tác
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        btnApprove = createActionButton("Duyệt", new Color(40, 167, 69));
        btnReject = createActionButton("Từ chối", new Color(220, 53, 69));
        btnViewDetails = createActionButton("Chi tiết", new Color(0, 123, 255));
        btnDelete = createActionButton("Xóa", new Color(108, 117, 125));

        btnApprove.setEnabled(false);
        btnReject.setEnabled(false);
        btnViewDetails.setEnabled(false);
        btnDelete.setEnabled(false);

        buttonPanel.add(btnViewDetails);
        buttonPanel.add(btnApprove);
        buttonPanel.add(btnReject);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);

        // Bảng bài đăng
        String[] columns = {"ID", "Tiêu đề", "Công ty", "Mức lương", "Địa điểm", "Trạng thái"};
        jobTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblJobs = new JTable(jobTableModel);
        styleTable(tblJobs);

        // Thêm renderer cho cột trạng thái
        tblJobs.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());

        tblJobs.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblJobs.getSelectedRow() != -1) {
                currentSelectedJobId = Integer.parseInt(tblJobs.getValueAt(tblJobs.getSelectedRow(), 0).toString());
                String status = tblJobs.getValueAt(tblJobs.getSelectedRow(), 5).toString();
                
                btnViewDetails.setEnabled(true);
                btnDelete.setEnabled(true);
                
                // Chỉ cho phép duyệt/từ chối khi bài đăng đang ở trạng thái chờ duyệt
                boolean isPending = "Chờ duyệt".equals(status);
                btnApprove.setEnabled(isPending);
                btnReject.setEnabled(isPending);
            } else {
                currentSelectedJobId = -1;
                btnApprove.setEnabled(false);
                btnReject.setEnabled(false);
                btnViewDetails.setEnabled(false);
                btnDelete.setEnabled(false);
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblJobs);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(10, 0, 0, 0)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Thêm action listeners
        btnApprove.addActionListener(this);
        btnReject.addActionListener(this);
        btnViewDetails.addActionListener(this);
        btnDelete.addActionListener(this);
        btnSearch.addActionListener(this);
        cbStatusFilter.addActionListener(this);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loadJobs();
                }
            }
        });

        loadJobs();
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

    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            String status = value.toString();
            Color bgColor;
            switch (status) {
                case "Chờ duyệt":
                    bgColor = new Color(255, 193, 7);
                    break;
                case "Đã duyệt":
                    bgColor = new Color(40, 167, 69);
                    break;
                case "Đã từ chối":
                    bgColor = new Color(220, 53, 69);
                    break;
                default:
                    bgColor = new Color(108, 117, 125);
            }
            
            label.setBackground(bgColor);
            label.setForeground(Color.WHITE);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            label.setOpaque(true);
            
            return label;
        }
    }

    public void loadJobs() {
        jobTableModel.setRowCount(0);
        String searchText = txtSearch.getText().trim();
        String selectedStatus = (String) cbStatusFilter.getSelectedItem();
        int status = -1;
        
        switch (selectedStatus) {
            case "Chờ duyệt": status = 0; break;
            case "Đã duyệt": status = 1; break;
            case "Đã từ chối": status = 2; break;
        }

        List<JobDTO> jobs = jobBLL.searchJobs(searchText, status);
        
        for (JobDTO job : jobs) {
            jobTableModel.addRow(new Object[]{
                job.getJobId(),
                job.getJobName(),
                job.getCompanyName(),
                String.format("%,.0f VNĐ", job.getSalary()),
                job.getAddress(),
                getStatusText(job.isPublic() == 1)
            });
        }
    }

    private String getStatusText(boolean status) {
        return status ? "Đã duyệt" : "Chờ duyệt";
    }

    private void showJobDetailsDialog(JobDTO job) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết bài đăng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Thêm thông tin chi tiết
        addDetailRow(contentPanel, gbc, "Tiêu đề:", job.getJobName());
        addDetailRow(contentPanel, gbc, "Công ty:", job.getCompanyName());
        addDetailRow(contentPanel, gbc, "Địa điểm:", job.getAddress());
        addDetailRow(contentPanel, gbc, "Mức lương:", String.format("%,.0f VNĐ", job.getSalary()));
        addDetailRow(contentPanel, gbc, "Trạng thái:", getStatusText(job.isPublic() == 1));
        
        // Thêm danh mục
        if (job.getCategories() != null && !job.getCategories().isEmpty()) {
            String categoryNames = job.getCategories().stream()
                .map(CategoryDTO::getCategoryName)
                .collect(Collectors.joining(", "));
            addDetailRow(contentPanel, gbc, "Danh mục:", categoryNames);
        }
        
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        contentPanel.add(new JLabel("Mô tả công việc:"), gbc);
        
        gbc.gridy++;
        JTextArea descriptionArea = new JTextArea(job.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(new Color(240, 240, 240));
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        contentPanel.add(scrollPane, gbc);

        // Thêm yêu cầu nếu có
        if (job.getRequirement() != null && !job.getRequirement().isEmpty()) {
            gbc.gridy++;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            contentPanel.add(new JLabel("Yêu cầu:"), gbc);
            
            gbc.gridy++;
            JTextArea requirementArea = new JTextArea(job.getRequirement());
            requirementArea.setLineWrap(true);
            requirementArea.setWrapStyleWord(true);
            requirementArea.setEditable(false);
            requirementArea.setBackground(new Color(240, 240, 240));
            requirementArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            JScrollPane requirementScrollPane = new JScrollPane(requirementArea);
            requirementScrollPane.setPreferredSize(new Dimension(500, 100));
            contentPanel.add(requirementScrollPane, gbc);
        }

        dialog.add(contentPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(valueComponent, gbc);

        gbc.gridy++;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnApprove) {
            if (currentSelectedJobId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn duyệt bài đăng này?",
                    "Xác nhận duyệt",
                    JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                    Response response = jobBLL.updateJobStatus(currentSelectedJobId, 1); // 1 = Đã duyệt
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(this, response.getMessage());
                        loadJobs();
                    } else {
                        JOptionPane.showMessageDialog(this, response.getMessage(),
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else if (e.getSource() == btnReject) {
            if (currentSelectedJobId != -1) {
                // Hiển thị dialog nhập lý do từ chối
                JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lý do từ chối", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(400, 200);
                dialog.setLocationRelativeTo(this);
                
                JPanel panel = new JPanel(new BorderLayout(10, 10));
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                
                JTextArea txtReason = new JTextArea(5, 30);
                txtReason.setLineWrap(true);
                txtReason.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(txtReason);
                
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton btnConfirm = new JButton("Xác nhận");
                JButton btnCancel = new JButton("Hủy");
                
                btnConfirm.addActionListener(ev -> {
                    String reason = txtReason.getText().trim();
                    if (reason.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog,
                            "Vui lòng nhập lý do từ chối",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Response response = jobBLL.rejectJob(currentSelectedJobId, reason);
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(dialog, response.getMessage());
                        dialog.dispose();
                        loadJobs();
                    } else {
                        JOptionPane.showMessageDialog(dialog,
                            response.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                btnCancel.addActionListener(ev -> dialog.dispose());
                
                buttonPanel.add(btnConfirm);
                buttonPanel.add(btnCancel);
                
                panel.add(new JLabel("Vui lòng nhập lý do từ chối:"), BorderLayout.NORTH);
                panel.add(scrollPane, BorderLayout.CENTER);
                panel.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.add(panel);
                dialog.setVisible(true);
            }
        } else if (e.getSource() == btnViewDetails) {
            if (currentSelectedJobId != -1) {
                JobDTO job = jobBLL.getJobById(currentSelectedJobId);
                if (job != null) {
                    showJobDetailsDialog(job);
                }
            }
        } else if (e.getSource() == btnDelete) {
            if (currentSelectedJobId != -1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa bài đăng này?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Response response = jobBLL.deleteJob(currentSelectedJobId);
                    if (response.isSuccess()) {
                        JOptionPane.showMessageDialog(this, response.getMessage());
                        loadJobs();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            response.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else if (e.getSource() == btnSearch || e.getSource() == cbStatusFilter) {
            loadJobs();
        }
    }
}