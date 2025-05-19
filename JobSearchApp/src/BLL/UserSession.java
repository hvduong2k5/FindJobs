package BLL;

import DAL.UserDAL;
import DTO.UserDTO;

public class UserSession {

	private static UserSession _instance;
	private UserDAL dal = UserDAL.GetInstance();
	
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
    public void setCurrentUser(UserDTO user)
    {
    	currentUser = new UserDTO(user.getUser_id()
    			,user.getUser_name()
    			,user.getAccount()
    			,user.getPassword()
    			,user.getRole());
    }
    public boolean updateName(String name)
    {
    	currentUser.setUser_name(name);
    	return dal.UpdateInfo(currentUser);
    }
    public UserDTO getLoggedInUser() {
        return currentUser;
    }
    public boolean isLoggedIn() {
        return currentUser != null;
    }
 
    
    public void logout() {
 
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
