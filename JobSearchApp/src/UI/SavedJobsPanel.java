package UI;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

class SavedJobsPanel extends JPanel {
    private int userId; // Nên là biến instance nếu mỗi user có panel riêng

    public SavedJobsPanel(int id) {
        this.userId = id;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Công việc đã lưu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(0, 102, 204));
        add(titleLabel, BorderLayout.NORTH);

        // TODO: Thêm JTable hoặc JList để hiển thị các công việc đã lưu
        // Ví dụ:
        DefaultTableModel savedJobsModel = new DefaultTableModel(new String[]{"Vị trí", "Công ty", "Ngày lưu"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tblSavedJobs = new JTable(savedJobsModel);
        tblSavedJobs.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblSavedJobs.setRowHeight(28);
        tblSavedJobs.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        // Thêm dữ liệu mẫu hoặc tải từ DB
        if (userId != -1) { // Chỉ tải nếu đã đăng nhập
             savedJobsModel.addRow(new Object[]{"Lập trình viên Java Backend", "FPT Software", "17/05/2025"});
        } else {
            // Có thể hiển thị thông báo yêu cầu đăng nhập ở đây thay vì JTable trống
        }


        add(new JScrollPane(tblSavedJobs), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRemoveSaved = new JButton("Xóa khỏi danh sách lưu");
        btnRemoveSaved.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bottomPanel.add(btnRemoveSaved);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRemoveSaved.addActionListener(e -> {
            int selectedRow = tblSavedJobs.getSelectedRow();
            if (selectedRow != -1) {
                // Xử lý xóa
                savedJobsModel.removeRow(selectedRow);
                 JOptionPane.showMessageDialog(this, "Đã xóa công việc khỏi danh sách lưu.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn một công việc để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        });

        // System.out.println("SavedJobsPanel khởi tạo cho User ID: " + this.userId);
    }
}
