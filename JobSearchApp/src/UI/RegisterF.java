package UI;
import BLL.UserBLL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Util.Response;
public class RegisterF extends JFrame implements ActionListener {
    private JLabel lbName,lbUsername,lbPassword,lbConfirm;
    private JTextField txtName,txtUsername;
    private JPasswordField txtPassword,txtConfirm;
    private JButton btnRegister,btnBack;
    private JCheckBox chkShowPassword;
    private JPanel mainPanel;
    private Choice ch;

    public RegisterF(String title) {
        super(title);
        GUI();
    }

    public void GUI() {
    	lbName = new JLabel("Họ Và Tên:");
        lbUsername = new JLabel("Tên đăng nhập:");
        lbPassword = new JLabel("Mật khẩu:");
        lbConfirm  = new JLabel("Xác nhận mật khẩu:");
        
        txtName = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtConfirm  =new JPasswordField();

        ch = new Choice();
        ch.add("người tìm việc");
        ch.add("người tuyển dụng");
        chkShowPassword = new JCheckBox("Hiện mật khẩu");

        btnRegister = new JButton("Đăng ký");
        btnBack = new JButton("Quay lại");

        mainPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(lbName);
        mainPanel.add(txtName);
        
        mainPanel.add(lbUsername);
        mainPanel.add(txtUsername);

        mainPanel.add(lbPassword);
        mainPanel.add(txtPassword);

        mainPanel.add(lbConfirm);
        mainPanel.add(txtConfirm);

        mainPanel.add(ch); // khoảng trống
        mainPanel.add(chkShowPassword);

        mainPanel.add(btnRegister);
        mainPanel.add(btnBack);

        btnRegister.addActionListener(this);
        btnBack.addActionListener(this);
        
        chkShowPassword.addActionListener(e -> {
            char echo = chkShowPassword.isSelected() ? 0 : '•';
            txtPassword.setEchoChar(echo);
            txtConfirm.setEchoChar(echo);
        });

        this.add(mainPanel);
        this.setSize(400, 280);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnRegister) {
        	String name = txtName.getText();
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String confirm = new String(txtConfirm.getPassword());

            if (username.isEmpty() || password.isEmpty() ||confirm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
                return;
            }

            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!");
                return;
            }
            int role = -1;
            String txtRole = ch.getSelectedItem();
            switch (txtRole) {
            case "người tìm việc":
            	role = 3;
            	break;
            case "người tuyển dụng":
            	role = 2;
            	break;
            default:
            	//
            	break;
            }
            //code 
            UserBLL createUser = new UserBLL();
            Response r = createUser.Register(name, username, password, role);
            if(r.isSuccess()) {
            	JOptionPane.showMessageDialog(this, r.getMessage());	
            }else {
            	JOptionPane.showMessageDialog(this, r.getMessage());
            }
            this.dispose();
            new LoginF("Đăng nhập"); 
            
        } else if (e.getSource() == btnBack) {
            this.dispose();
            new LoginF("Đăng nhập");
        }
    }

    public static void main(String[] args) {
        new RegisterF("Đăng ký tài khoản");
    }
}
