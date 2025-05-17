package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginF extends JFrame implements ActionListener {
    private JLabel lbUsername,lbPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin,btnRegister,btnBack;
    private JCheckBox chkShowPassword; 
    private JPanel mainPanel;

    public LoginF(String title) {
        super(title);
        GUI();
    }

    public void GUI() {
    	
        lbUsername  = new JLabel("Tên đăng nhập:");
        lbPassword  = new JLabel("Mật khẩu:");
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        btnLogin    = new JButton("Đăng nhập");
        btnRegister = new JButton("Đăng ký");
        btnBack = new JButton("Quay Lại");
        chkShowPassword = new JCheckBox("Hiện mật khẩu");

        mainPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(lbUsername);
        mainPanel.add(txtUsername);

        mainPanel.add(lbPassword);
        mainPanel.add(txtPassword);

        mainPanel.add(new JLabel());
        mainPanel.add(chkShowPassword);

        mainPanel.add(btnRegister);
        mainPanel.add(btnLogin);

        mainPanel.add(new JLabel());
        mainPanel.add(btnBack);

        btnLogin.addActionListener(this);
        btnRegister.addActionListener(this);
        btnBack.addActionListener(this);

        chkShowPassword.addActionListener(e -> {
            if (chkShowPassword.isSelected()) {
                txtPassword.setEchoChar((char) 0); // Hiện mật khẩu
            } else {
                txtPassword.setEchoChar('•'); // Ẩn mật khẩu
            }
        });

        this.add(mainPanel);
        this.setSize(450, 250);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            String user = txtUsername.getText();
            String pass = new String(txtPassword.getPassword());

            if (user.equals("admin") && pass.equals("123")) {
                JOptionPane.showMessageDialog(this, "Đăng nhập thành công!");
                this.dispose();
                int user_id = 3;
                //
                //lấy thông tin người dùng user.role
                //
                int role =3;//1/2/3
                switch(role) {
                case 1:
                	break;
                case 2:
                	break;
                case 3:
                    new MainF("Ứng dụng Tìm kiếm Việc làm",user_id);
                	break;
                }
                
            } else {
                JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!");
            }
        }
        if (e.getSource() == btnRegister) {
        		this.dispose();
        		new RegisterF("Đăng ký tài khoản mới");
        }
        if (e.getSource() == btnBack) {
    		this.dispose();
    		new MainF("Ứng dụng Tìm kiếm Việc làm(chưa đăng nhập)",-1);
    }   
    }

    public static void main(String[] args) {
        new LoginF("Đăng nhập hệ thống");
    }
}
