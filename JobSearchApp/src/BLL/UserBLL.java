package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import DAL.UserDAL;
import DTO.UserDTO;
public class UserBLL {

	UserDAL dal = UserDAL.GetInstance();
	public UserDTO getUserById(int userId) {
        try {
            UserDTO user = dal.SelectById(userId);
            if (user != null) {
                user.setPassword(null); // Không trả về mật khẩu
            }
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
    public int createUser(String name, String account, String password, int role) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty() || name == null || name.isEmpty()) {
                return 400;// "Tên tài khoản, mật khẩu và tên người dùng không được để trống.", 400, null);
            }

            UserDTO existingUser = dal.SelectByAccount(account);
            if (existingUser != null) {
                return 409;// "Tên tài khoản đã tồn tại.", 409, null);
            }

            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12))); // Sử dụng salt round chuẩn
            newUser.setRole(role);
            newUser.setUser_name(name);

            if (dal.Insert(newUser)) {
                return 201;// "Tạo người dùng thành công.", 201, createdUser); // 201 Created
            } else {
                return 500; //"Không thể tạo người dùng do lỗi hệ thống.", 500, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 500;
        }
    }

	
	public List<UserDTO> getUsersByName(String nameQuery, int role ) {
        try {
            
            List<UserDTO> allUsers = dal.SelectAll();
            if (allUsers == null) return new ArrayList<>();
            if ((nameQuery == null || nameQuery.trim().isEmpty() )&& role == 0) {
            	return allUsers;
            }
            String lowerNameQuery = nameQuery.toLowerCase();
            return allUsers.stream()
                    .filter(user -> user.getUser_name() != null && (user.getRole() == role || role ==0) && user.getUser_name().toLowerCase().contains(lowerNameQuery))
                    .peek(user -> user.setPassword(null)) // Loại bỏ mật khẩu
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();

    }
	public boolean updateUser(int userId, String newName, int role) {
        try {
            if (newName == null || newName.trim().isEmpty()) {
                return false;
            }
            UserDTO user = dal.SelectById(userId);
            if (user == null) {
                return false;
            }
            user.setUser_name(newName);
            user.setRole(role);
            if(user.getUser_id() == UserSession.GetUser().getUser_id())
            {
            	if(role != UserSession.GetUser().getRole())
            	{
            		return false;
            	}
            }
            if (dal.UpdateInfo(user)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
	public int updatePassword(int userId, String oldPassword, String newPassword) {
        try {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return 400;// "Mật khẩu mới không được để trống.", 400);
            }

            UserDTO userToUpdate = dal.SelectById(userId);
            if (userToUpdate == null) {
                return 404;//  "Tài khoản không tồn tại.", 404);
            }

            UserDTO currentUser = UserSession.GetInstance().GetUser();
            if (currentUser == null) {
                return 401;// "Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.", 401); // Unauthorized
            }

            boolean isAdminUpdatingOthers = currentUser.getRole() == 99 && currentUser.getUser_id() != userId;

            if (isAdminUpdatingOthers) {
                // Admin đổi mật khẩu cho người khác, không cần mật khẩu cũ
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(16)));
                if (dal.UpdatePassword(userToUpdate)) {
                    return 200;// "Admin cập nhật mật khẩu thành công.", 200);
                } else {
                    return 500;//  "Admin không thể cập nhật mật khẩu.", 500);
                }
            } else if (currentUser.getUser_id() == userId) {
                // Người dùng tự đổi mật khẩu của mình
                if (oldPassword == null || oldPassword.isEmpty()) {
                     return 400;// "Mật khẩu cũ không được để trống khi tự thay đổi.
                }
                if (!BCrypt.checkpw(oldPassword, userToUpdate.getPassword())) {
                    return 401;//  "Mật khẩu cũ không chính xác.", 401);
                }
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
                if (dal.UpdatePassword(userToUpdate)) {
                    return 200;//  "Đổi mật khẩu thành công.", 200);
                } else {
                    return 500;// "Không thể đổi mật khẩu.", 500);
                }
            } else {
                // Người dùng không phải admin và đang cố đổi mật khẩu của người khác
                return 403;// "Bạn không có quyền đổi mật khẩu cho người dùng này.", 403); // Forbidden
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return 500;//new ActionResult(false, "Lỗi hệ thống: " + ex.getMessage(), 500);
        }
    }
    public int Login(String account, String password) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return 400;//bad request
            }

            UserDTO user = dal.SelectByAccount(account);
            if (user == null) {
                return 404;//not found
            }

            if (!BCrypt.checkpw(password, user.getPassword())) {
                return 404;//
            }

            
            UserDTO userDto = new UserDTO(); // Tạo UserDTO từ thông tin của _User
            userDto.setUser_id(user.getUser_id());
            userDto.setUser_name(user.getUser_name());
            userDto.setAccount(user.getAccount());
            userDto.setRole(user.getRole());
 


            UserSession.GetInstance().setCurrentUser(userDto); // UserSession.setCurrentUser cần được triển khai
            return 200;
        } catch (Exception ex) {
            return 500;
        }
    }
    public int deleteUser(int userId) {
        try {
            UserDTO currentUser = UserSession.GetInstance().GetUser();
            if (currentUser == null) {

            	return 401;
            }

            if (currentUser.getUser_id() == userId) {
                return 400;// "Bạn không thể tự xóa tài khoản của mình.", 400);
            }
            
            // Chỉ admin mới có quyền xóa (hoặc bạn có thể có logic quyền phức tạp hơn)
            if (dal.SelectById(currentUser.getUser_id()).getRole() != 99) {
                 return 403;// "Bạn không có quyền xóa người dùng.", 403);
            }

            UserDTO userToDelete = dal.SelectById(userId);
            if (userToDelete == null) {
                return 404;// "Người dùng không tồn tại.", 404);
            }

            if (dal.Delete(userId)) {
                return 200;// "Xóa người dùng thành công.", 200);
            } else {
                return 500;//, "Không thể xóa người dùng.", 500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 500;
        }
    }
    public int Register(String account, String password, String name, int role ) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return 400;//bad request
            }

            UserDTO user = dal.SelectByAccount(account);
            if (user != null) {
                return 409;//đã tồn tại
            }

            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(16)));
            newUser.setRole(role);
            newUser.setUser_name(name);
            

            if(dal.Insert(newUser))return 200;
 


            return 500;
        } catch (Exception ex) {
            return 500;
        }
    }
    
    //test 
    public static void main(String[] args) {
        UserBLL bll = new UserBLL();

        System.out.println("=== TEST NGƯỜI DÙNG THƯỜNG ===");

        // 1. Tạo người dùng thường
        int resultUser = bll.createUser("User Test", "usertest1", "123456", 1);
        System.out.println("Tạo user thường: " + resultUser); // 201

        // 2. Đăng nhập người dùng
        int loginUser = bll.Login("usertest1", "123456");
        System.out.println("Đăng nhập user: " + loginUser); // 200
        int userId = UserSession.GetInstance().GetUser().getUser_id();

        // 3. Đổi mật khẩu đúng
        int changePwd = bll.updatePassword(userId, "123456", "654321");
        System.out.println("User đổi mật khẩu đúng: " + changePwd); // 200

        // 4. Đăng nhập lại bằng mật khẩu mới
        int relogin = bll.Login("usertest", "654321");
        System.out.println("Đăng nhập lại: " + relogin); // 200

        // 5. Xóa bản thân (phải bị từ chối)
        int deleteSelf = bll.deleteUser(userId);
        System.out.println("User tự xóa chính mình: " + deleteSelf); // 400

        System.out.println("\n=== TEST ADMIN ===");

        // 6. Tạo admin
        int resultAdmin = bll.createUser("Admin Test", "admintest1", "admin123", 99);
        System.out.println("Tạo admin: " + resultAdmin); // 201

        // 7. Đăng nhập admin
        int loginAdmin = bll.Login("admintest1", "admin123");
        System.out.println("Đăng nhập admin: " + loginAdmin); // 200
        int adminId = UserSession.GetInstance().GetUser().getUser_id();

        // 8. Admin đổi mật khẩu cho user
        int adminChangeUserPwd = bll.updatePassword(userId, null, "111111");
        System.out.println("Admin đổi mật khẩu cho user: " + adminChangeUserPwd); // 200

        // 9. Đăng nhập user bằng mật khẩu mới
        int reloginAfterAdminChange = bll.Login("usertest", "111111");
        System.out.println("User đăng nhập sau khi admin đổi: " + reloginAfterAdminChange); // 200

        // 10. Admin xóa người dùng
        int deleteUser = bll.deleteUser(userId);
        System.out.println("Admin xóa user: " + deleteUser); // 200

        // 11. Admin cố xóa chính mình (bị từ chối)
        int deleteSelfAdmin = bll.deleteUser(adminId);
        System.out.println("Admin tự xóa chính mình: " + deleteSelfAdmin); // 400
    }
}
