package BLL;

import java.util.ArrayList;
import Util.Response;
import java.util.List;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import DAL.UserDAL;
import DAL.UserSaveJobDAL;
import DAL.UserApplyJobDAL;
import DTO.UserDTO;
public class UserBLL {

	private UserDAL userDAL;
	private UserSaveJobDAL userSaveJobDAL;
	private UserApplyJobDAL userApplyJobDAL;
	
	public UserBLL() {
		this.userDAL = UserDAL.GetInstance();
		this.userSaveJobDAL = UserSaveJobDAL.GetInstance();
		this.userApplyJobDAL = UserApplyJobDAL.GetInstance();
	}
	
	public UserDTO getUserById(int userId) {
        try {
            UserDTO user = userDAL.SelectById(userId);
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

            UserDTO existingUser = userDAL.SelectByAccount(account);
            if (existingUser != null) {
                return Response.Error("Tên tài khoản đã tồn tại.");// "", 409, null);
            }
            if (account.length() < 6) {
                return Response.Error("Tên tài khoản phải có ít nhất 6 ký tự.");
            }
            // Pattern.matches("^[a-z]+$", account) kiểm tra account chỉ chứa chữ thường từ a đến z
            if (!account.matches("^[a-z]+$")) {
                return Response.Error("Tên tài khoản chỉ được chứa các chữ cái viết thường (a-z).");
            }
            if(password.length()<6) {
            	return Response.Error("Mật khẩu phải có ít nhất 6 ký tự.");
            }
            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(12))); // Sử dụng salt round chuẩn
            newUser.setRole(role);
            newUser.setUser_name(name);

            if (userDAL.Insert(newUser)) {
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
            
            List<UserDTO> allUsers = userDAL.SelectAll();
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
            UserDTO user = userDAL.SelectById(userId);
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
            if (userDAL.UpdateInfo(user)) {
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
                return Response.Error("Mật khẩu mới không được để trống.");
            }

            if(newPassword.length()<6) {
                return Response.Error("Mật khẩu phải có ít nhất 6 ký tự.");
            }
            UserDTO userToUpdate = userDAL.SelectById(userId);
            if (userToUpdate == null) {
                return Response.Error("Tài khoản không tồn tại.");
            }

            UserDTO currentUser = UserSession.GetInstance().GetUser();
            if (currentUser == null) {
                return Response.Error("Phiên làm việc không hợp lệ. Vui lòng đăng nhập lại.");
            }

            boolean isAdminUpdatingOthers = currentUser.getRole() == 1 && currentUser.getUser_id() != userId; // Role admin = 1

            if (isAdminUpdatingOthers) {
                // Admin đổi mật khẩu cho người khác, không cần mật khẩu cũ
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(16)));
                if (userDAL.UpdatePassword(userToUpdate)) {
                    return Response.Success("Admin cập nhật mật khẩu thành công.");
                } else {
                    return Response.Error("Admin không thể cập nhật mật khẩu.");
                }
            } else if (currentUser.getUser_id() == userId) {
                // Người dùng tự đổi mật khẩu của mình
                if (oldPassword == null || oldPassword.isEmpty()) {
                    return Response.Error("Mật khẩu cũ không được để trống khi tự thay đổi.");
                }
                if (!BCrypt.checkpw(oldPassword, userToUpdate.getPassword())) {
                    return Response.Error("Mật khẩu cũ không chính xác.");
                }
                userToUpdate.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt(12)));
                if (userDAL.UpdatePassword(userToUpdate)) {
                    return Response.Success("Đổi mật khẩu thành công.");
                } else {
                    return Response.Error("Không thể đổi mật khẩu.");
                }
            } else {
                // Người dùng không phải admin và đang cố đổi mật khẩu của người khác
                return Response.Error("Bạn không có quyền đổi mật khẩu cho người dùng này.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống: " + ex.getMessage());
        }
    }
    public Response Login(String account, String password) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return Response.Error("Thông tin không được để trống.");// "", 400);
            }

            UserDTO user = userDAL.SelectByAccount(account);
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
        if (userId <= 0) {
            return new Response(false, "ID người dùng không hợp lệ");
        }

        // Kiểm tra xem user có phải là admin không
        UserDTO user = userDAL.SelectById(userId);
        if (user != null && user.getRole() == 1) { // Role admin = 1
            return new Response(false, "Không thể xóa tài khoản admin");
        }

        try {
            // Xóa các bài đăng đã lưu của user
            boolean deleteSavedJobs = userSaveJobDAL.deleteByUserId(userId);
            if (!deleteSavedJobs) {
                return new Response(false, "Không thể xóa các bài đăng đã lưu của người dùng");
            }
            
            // Xóa các đơn ứng tuyển của user
            boolean deleteAppliedJobs = userApplyJobDAL.deleteByUserId(userId);
            if (!deleteAppliedJobs) {
                return new Response(false, "Không thể xóa các đơn ứng tuyển của người dùng");
            }
            
            // Xóa user
            boolean deleteUser = userDAL.Delete(userId);
            if (!deleteUser) {
                return new Response(false, "Không thể xóa người dùng");
            }
            
            return new Response(true, "Xóa người dùng và các dữ liệu liên quan thành công");
            
        } catch (Exception e) {
            return new Response(false, "Lỗi khi xóa người dùng: " + e.getMessage());
        }
    }
    public Response Register(String account, String password, String name, int role ) {
        try {
            if (account == null || account.isEmpty() || password == null || password.isEmpty()) {
                return Response.Error("Thông tin không được để trống.");// "", 400);
            }
            if (account.length() < 6) {
                return Response.Error("Tên tài khoản phải có ít nhất 6 ký tự.");
            }
            if(password.length()<6) {
            	return Response.Error("Mật khẩu phải có ít nhất 6 ký tự.");
            }
            // Pattern.matches("^[a-z]+$", account) kiểm tra account chỉ chứa chữ thường từ a đến z
            if (!account.matches("^[a-z]+$")) {
                return Response.Error("Tên tài khoản chỉ được chứa các chữ cái viết thường (a-z).");
            }

            UserDTO user = userDAL.SelectByAccount(account);
            if (user != null) {
                return Response.Error("Tài khoản đã tồn tại");//
            }

            UserDTO newUser = new UserDTO();
            newUser.setAccount(account);
            newUser.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(16)));
            newUser.setRole(role);
            newUser.setUser_name(name);
            

            if(userDAL.Insert(newUser))return Response.Success("Đăng ký thành công");
 


            return Response.Error("Đăng ký thất bại.");
        } catch (Exception ex) {
            return Response.Error("Đăng ký thất bại.");
        }
    }
    
    //test 

}
