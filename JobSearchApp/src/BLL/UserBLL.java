package BLL;

import java.util.ArrayList;
import Util.Response;
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
	
    public Response createUser(String name, String account, String password, int role) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty() || name == null || name.isEmpty()) {
                return Response.Error("Tên tài khoản, mật khẩu và tên người dùng không được để trống.");// "", 400, null);
            }

            UserDTO existingUser = dal.SelectByAccount(account);
            if (existingUser != null) {
                return Response.Error("Tên tài khoản đã tồn tại.");// "", 409, null);
            }

            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12))); // Sử dụng salt round chuẩn
            newUser.setRole(role);
            newUser.setUser_name(name);

            if (dal.Insert(newUser)) {
                return Response.Success("Tạo người dùng thành công.");// "", 201, createdUser); // 201 Created
            } else {
                return Response.Error("Không thể tạo người dùng do lỗi hệ thống."); //"", 500, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Không thể tạo người dùng do lỗi hệ thống.");
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
	public Response updatePassword(int userId, String oldPassword, String newPassword) {
        try {
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return Response.Error("Mật khẩu mới không được để trống.");// "", 400);
            }

            UserDTO userToUpdate = dal.SelectById(userId);
            if (userToUpdate == null) {
                return Response.Error("Tài khoản không tồn tại.");//  "", 404);
            }

            UserDTO currentUser = UserSession.GetInstance().GetUser();
            if (currentUser == null) {
                return Response.Error("Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.");// "", 401); // Unauthorized
            }

            boolean isAdminUpdatingOthers = currentUser.getRole() == 99 && currentUser.getUser_id() != userId;

            if (isAdminUpdatingOthers) {
                // Admin đổi mật khẩu cho người khác, không cần mật khẩu cũ
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(16)));
                if (dal.UpdatePassword(userToUpdate)) {
                    return Response.Success("Admin cập nhật mật khẩu thành công.");// "", 200);
                } else {
                    return Response.Error("Admin không thể cập nhật mật khẩu.");//  "", 500);
                }
            } else if (currentUser.getUser_id() == userId) {
                // Người dùng tự đổi mật khẩu của mình
                if (oldPassword == null || oldPassword.isEmpty()) {
                     return Response.Error("Mật khẩu cũ không được để trống khi tự thay đổi.");// "
                }
                if (!BCrypt.checkpw(oldPassword, userToUpdate.getPassword())) {
                    return Response.Error("Mật khẩu cũ không chính xác.");//  "", 401);
                }
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
                if (dal.UpdatePassword(userToUpdate)) {
                    return Response.Success("Đổi mật khẩu thành công.");//  "", 200);
                } else {
                    return Response.Error("Không thể đổi mật khẩu.");// "", 500);
                }
            } else {
                // Người dùng không phải admin và đang cố đổi mật khẩu của người khác
                return Response.Error("Bạn không có quyền đổi mật khẩu cho người dùng này.");// "", 403); // Forbidden
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống: "+ex.getMessage());//new ActionResult(false, " " + ex.getMessage(), 500);
        }
    }
    public Response Login(String account, String password) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return Response.Error("Thông tin không được để trống.");// "", 400);
            }

            UserDTO user = dal.SelectByAccount(account);
            if (user == null) {
                return Response.Error("Tài khoản không tồn tại.");//  "", 404);
            }

            if (!BCrypt.checkpw(password, user.getPassword())) {
                return Response.Error("Mật khẩu không đúng.");//  "", 404);
            }

            
            UserDTO userDto = new UserDTO(); // Tạo UserDTO từ thông tin của _User
            userDto.setUser_id(user.getUser_id());
            userDto.setUser_name(user.getUser_name());
            userDto.setAccount(user.getAccount());
            userDto.setRole(user.getRole());
 


            UserSession.GetInstance().setCurrentUser(userDto); // UserSession.setCurrentUser cần được triển khai
            return Response.Success("Đăng nhập thành công.");
        } catch (Exception ex) {
            return Response.Error("Lỗi hệ thống : " +ex.getMessage());
        }
    }
    public Response deleteUser(int userId) {
        try {
            UserDTO currentUser = UserSession.GetInstance().GetUser();
            if (currentUser == null) {

                return Response.Error("Bạn chưa đăng nhập.");//  "", 404);
            }

            if (currentUser.getUser_id() == userId) {
                return Response.Error("Bạn không thể tự xóa tài khoản của mình.");// "", 400);
            }
            
            // Chỉ admin mới có quyền xóa (hoặc bạn có thể có logic quyền phức tạp hơn)
            if (dal.SelectById(currentUser.getUser_id()).getRole() != 99) {
                 return Response.Error("Bạn không có quyền xóa người dùng.");// "", 403);
            }

            UserDTO userToDelete = dal.SelectById(userId);
            if (userToDelete == null) {
                return Response.Error("Người dùng không tồn tại.");// "", 404);
            }

            if (dal.Delete(userId)) {
                return Response.Success("Xóa người dùng thành công.");// "", 200);
            } else {
                return Response.Error("Không thể xóa người dùng.");//, "", 500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống : "+ex.getMessage());
        }
    }
    public Response Register(String account, String password, String name, int role ) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return Response.Error("Thông tin không được để trống.");// "", 400);
            }

            UserDTO user = dal.SelectByAccount(account);
            if (user != null) {
                return Response.Error("Tài khoản đã tồn tại");//
            }

            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(16)));
            newUser.setRole(role);
            newUser.setUser_name(name);
            

            if(dal.Insert(newUser))return Response.Success("Đăng ký thành công");
 


            return Response.Error("Đăng ký thất bại.");
        } catch (Exception ex) {
            return Response.Error("Đăng ký thất bại.");
        }
    }
    
    //test 
    public static void main(String[] args) {
        UserBLL bll = new UserBLL();

        System.out.println("=== TEST NGƯỜI DÙNG ===");

        // 1. Đăng ký người dùng
        System.out.println("→ Đăng ký người dùng mới:");
        Response registerResponse = bll.Register("test_user", "123456", "Người dùng test", 1);
        System.out.println("Đăng ký: " + registerResponse.getMessage());

        // 2. Đăng nhập với tài khoản vừa đăng ký
        System.out.println("→ Đăng nhập:");
        Response loginResponse = bll.Login("test_user", "123456");
        System.out.println("Đăng nhập: " + loginResponse.getMessage());

        // 3. Lấy thông tin người dùng
        System.out.println("→ Lấy thông tin người dùng ID=2:");
        UserDTO user = bll.getUserById(2); // Giả sử ID = 2 tồn tại
        if (user != null) {
            System.out.println("Tên: " + user.getUser_name());
            System.out.println("Tài khoản: " + user.getAccount());
            System.out.println("Vai trò: " + user.getRole());
        } else {
            System.out.println("Không tìm thấy người dùng.");
        }

        // 4. Cập nhật thông tin người dùng
        System.out.println("→ Cập nhật tên người dùng:");
        boolean updateResponse = bll.updateUser(2, "Tên mới", 1);
        System.out.println("Cập nhật: " + (updateResponse ? "Thành công" : "Thất bại"));

        // 5. Đổi mật khẩu người dùng tự đổi
        System.out.println("→ Đổi mật khẩu:");
        Response passwordResponse = bll.updatePassword(2, "123456", "newpass123");
        System.out.println("Đổi mật khẩu: " + passwordResponse.getMessage());

        // 6. Xóa người dùng (chỉ admin mới thực hiện được)
        System.out.println("→ Xóa người dùng:");
        Response deleteResponse = bll.deleteUser(2);
        System.out.println("Xóa: " + deleteResponse.getMessage());
    }

}
