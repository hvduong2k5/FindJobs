package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Util.DBUtil;
import DTO.UserDTO;
public class UserDAL {

	private static UserDAL _instance;
	
	public static UserDAL GetInstance() {
		
			if(_instance == null) {
				_instance = new UserDAL();
			}
			return _instance;
	};
	
	private UserDAL() {
		
	}
	public UserDTO SelectById(int id)
	{
		Connection conn =  null;
		try
		{
			conn = DBUtil.MakeConnection();
			String sql = "Select * from user where user_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			UserDTO result = null;
			if(rs.next())
			{
				result = new UserDTO(rs.getInt("user_id"),
						             rs.getString("user_name"),
						             rs.getString("account"),
						             rs.getString("password"),
						             rs.getInt("role"));
			}
			rs.close();
			ps.close();
			return result;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			DBUtil.CloseConnection(conn);
		}
		return null;
	}
	
	public List<UserDTO> SelectAll(){
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "Select * from user";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			List<UserDTO> allUser = new ArrayList<UserDTO>();
			while(rs.next())
			{
				allUser.add(new UserDTO(rs.getInt("user_id"),
			             rs.getString("user_name"),
			             rs.getString("account"),
			             rs.getString("password"),
			             rs.getInt("role")));
			}
			rs.close();
			ps.close();
			return allUser;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return null;
	}
	
	public boolean UpdateInfo(UserDTO user)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "Update user Set user_name = ? , role = ? where user_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getUser_name());
			ps.setInt(2, user.getRole());
			ps.setInt(3, user.getUser_id());
			int result = ps.executeUpdate();
			ps.close();
			
			if(result > 0) return true;
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	
	public boolean UpdatePassword(UserDTO user)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "update user set password = ? where user_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getPassword());
			ps.setInt(2, user.getUser_id());
			int result = ps.executeUpdate();
			ps.close();
			if(result > 0 )return true;
		}
		catch (Exception e) {

			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	
	public boolean Insert(UserDTO user)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "Insert into user(user_name,account,password,role) values (?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, user.getUser_name());
			ps.setString(2, user.getAccount());
			ps.setString(3, user.getPassword());
			ps.setInt(4, user.getRole());
			int result = ps.executeUpdate();
			ps.close();
			if(result > 0)return true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	
	public boolean Delete(int id) {
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "DELETE FROM user WHERE user_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);
			int result = ps.executeUpdate();
			ps.close();
			if(result > 0)return true;
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	
	public static void main(String[] args) {
	    UserDAL dal = UserDAL.GetInstance();

	    // Test Insert
	    UserDTO newUser = new UserDTO(0, "Test User", "test_account", "123456", 1);
	    boolean inserted = dal.Insert(newUser);
	    System.out.println("Insert test: " + (inserted ? "Passed" : "Failed"));

	    // Test SelectAll
	    List<UserDTO> users = dal.SelectAll();
	    System.out.println("SelectAll test: Found " + (users != null ? users.size() : 0) + " users.");

	    // Giả sử ID 1 tồn tại trong DB
	    int testId = 1;

	    // Test SelectById
	    UserDTO selectedUser = dal.SelectById(testId);
	    System.out.println("SelectById test: " + (selectedUser != null ? "Passed (Found user: " + selectedUser.getUser_name() + ")" : "Failed"));

	    // Test UpdateInfo
	    if (selectedUser != null) {
	        selectedUser.setUser_name("Updated Name");
	        selectedUser.setRole(2);
	        boolean updated = dal.UpdateInfo(selectedUser);
	        System.out.println("UpdateInfo test: " + (updated ? "Passed" : "Failed"));
	    }

	    // Test UpdatePassword
	    if (selectedUser != null) {
	        selectedUser.setPassword("newpassword123");
	        boolean passwordUpdated = dal.UpdatePassword(selectedUser);
	        System.out.println("UpdatePassword test: " + (passwordUpdated ? "Passed" : "Failed"));
	    }

	    // Test Delete (chỉ thử nếu Insert thành công trước đó)
	    if (inserted) {
	        // Tìm lại user mới tạo để lấy ID
	        List<UserDTO> allUsers = dal.SelectAll();
	        UserDTO lastUser = allUsers.get(allUsers.size() - 1); // Lấy user cuối cùng
	        boolean deleted = dal.Delete(lastUser.getUser_id());
	        System.out.println("Delete test: " + (deleted ? "Passed" : "Failed"));
	    }
	}


}
