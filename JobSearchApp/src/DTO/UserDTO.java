package DTO;

public class UserDTO {
    private int user_id;
    private String user_name;
    private String account;
    private String password;
    private int role;

    public UserDTO() {
    }

    public UserDTO(int user_id, String user_name, String account, String password, int role) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.account = account;
        this.password = password;
        this.role = role;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
