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
			String sql = "delete from usersavejob where user_id = ? and job_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, jobId);
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
                result = rs.getInt(1);
            }
            rs.close();
            ps.close();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.CloseConnection(conn);
        }
    }

    public boolean deleteByUserId(int userId) {
        Connection conn = null;
        try {
            conn = DBUtil.MakeConnection();
            String sql = "DELETE FROM usersavejob WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            int result = ps.executeUpdate();
            ps.close();
            return result >= 0; // Trả về true ngay cả khi không có bản ghi nào bị xóa
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.CloseConnection(conn);
        }
        return false;
    }

	


}
