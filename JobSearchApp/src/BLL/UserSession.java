package BLL;

import DAL.UserDAL;
import DTO.UserDTO;

public class UserSession {

	private static UserSession _instance;
	
    private static UserDTO currentUser;

    private UserSession()
    {
    	
    }
    
    public static UserSession GetInstance() {
        if (_instance == null) {
            _instance = new UserSession();
        }
        return _instance;
    }
    
    public UserDTO getLoggedInUser() {
        return currentUser;
    }
    public boolean isLoggedIn() {
        return currentUser != null;
    }
 
    
    public void logout() {
        if (currentUser != null) {
            System.out.println("User logged out: " + currentUser.getUser_name()); // Log để kiểm tra
        } else {
             System.out.println("No user was logged in."); // Log để kiểm tra
        }
        currentUser = null;
    }
    
    public boolean isInRole(int roleId) {
        if (!isLoggedIn()) {
            return false;  
        }
         return currentUser.getRole() == roleId;
    }
    public boolean isAdmin() {
         final int ADMIN_ROLE_ID = 99;  
        return isInRole(ADMIN_ROLE_ID);
    }
}
