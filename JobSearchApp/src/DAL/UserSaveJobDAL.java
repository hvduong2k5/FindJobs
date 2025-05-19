package DAL;

import Util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.UserSaveJobDTO;
public class UserSaveJobDAL {

	private static UserSaveJobDAL _instance ;
	public static UserSaveJobDAL GetInstance()
	{
		if(_instance == null)
		{
			_instance = new UserSaveJobDAL();
		}
		return _instance;
	}
	private UserSaveJobDAL()
	{
		
	}


	public UserSaveJobDTO SelectByUserIdJobId(int userId, int JobId)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "select * from usersavejob where user_id = ? and job_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, JobId);
			ResultSet rs = ps.executeQuery();
			UserSaveJobDTO usj = null;
			if(rs.next())
			{
				usj = (new UserSaveJobDTO(rs.getInt("user_save_job_id"),
						                       rs.getInt("user_id"),
						                       rs.getInt("job_id")));
			}
			rs.close();
			ps.close();
			return usj;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return null;
	}
	
	
	public List<UserSaveJobDTO> SelectAllByUserId (int userId)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "select * from usersavejob where user_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ResultSet rs = ps.executeQuery();
			List<UserSaveJobDTO> listUSJ = new ArrayList<UserSaveJobDTO>();
			while(rs.next())
			{
				listUSJ.add(new UserSaveJobDTO(rs.getInt("user_save_job_id"),
						                       rs.getInt("user_id"),
						                       rs.getInt("job_id")));
			}
			rs.close();
			ps.close();
			return listUSJ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return null;
	}
	
	public List<UserSaveJobDTO> SelectAllByJobId (int jobId)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "select * from usersavejob where job_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, jobId);
			ResultSet rs = ps.executeQuery();
			List<UserSaveJobDTO> listUSJ = new ArrayList<UserSaveJobDTO>();
			while(rs.next())
			{
				listUSJ.add(new UserSaveJobDTO(rs.getInt("user_save_job_id"),
						                       rs.getInt("user_id"),
						                       rs.getInt("job_id")));
			}
			rs.close();
			ps.close();
			return listUSJ;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return null;
	}

	public boolean Insert(UserSaveJobDTO userSaveJob)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "insert into usersavejob(user_id, job_id) values (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userSaveJob.getUser_id());
			ps.setInt(2, userSaveJob.getJob_id());
			int result = ps.executeUpdate();
			ps.close();
			if(result > 0)return true;
		}
		catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
			
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	
	public boolean Delete(int userId, int jobId)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "delete from usersavejob where user_id = ? and job_id";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, jobId);
			int result = ps.executeUpdate();
			ps.close();
			if(result > 0)return true;
		}
		catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
		}
		finally {
			DBUtil.CloseConnection(conn);
		}
		return false;
	}
	public boolean IsJobSaved(int userId, int jobId) {
        Connection conn = null;
        

        try {
        	PreparedStatement ps = null;
            ResultSet rs = null;
            conn = DBUtil.MakeConnection();
            String sql = "SELECT COUNT(*) FROM usersavejob WHERE user_id = ? AND job_id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            rs = ps.executeQuery();

            int result = 0;
            if (rs.next()) {
                result =  rs.getInt(1);
            }
            rs.close();
            ps.close();
            if(result > 0) return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            
            DBUtil.CloseConnection(conn);
        }
        return false;
    }
	public static void main(String[] args) {
	    UserSaveJobDAL dal = UserSaveJobDAL.GetInstance();

	    // Tạo dữ liệu mẫu để test Insert
	    UserSaveJobDTO newSaveJob = new UserSaveJobDTO(0, 1, 2); // user_id = 1, job_id = 2
	    boolean insertResult = dal.Insert(newSaveJob);
	    System.out.println("Insert: " + (insertResult ? "Success" : "Fail"));

	    // Test SelectAllByUserId
	    List<UserSaveJobDTO> userJobs = dal.SelectAllByUserId(1);
	    System.out.println("Jobs saved by user 1: " + (userJobs != null ? userJobs.size() : 0));

	    // Test SelectAllByJobId
	    List<UserSaveJobDTO> jobUsers = dal.SelectAllByJobId(2);
	    System.out.println("Users who saved job 2: " + (jobUsers != null ? jobUsers.size() : 0));

	    // Test IsJobSaved
	    boolean isSaved = dal.IsJobSaved(1, 2);
	    System.out.println("Is job 2 saved by user 1? " + (isSaved ? "Yes" : "No"));

	    // Test Delete (xóa bản ghi mới thêm, cần lấy ID thật nếu có tự tăng)
	    if (userJobs != null && !userJobs.isEmpty()) {
	        int lastId = userJobs.get(userJobs.size() - 1).getUser_save_job_id();
	        boolean deleteResult = dal.Delete(lastId, 1);
	        System.out.println("Delete last save job: " + (deleteResult ? "Success" : "Fail"));
	    } else {
	        System.out.println("No save job to delete");
	    }
	}


}
