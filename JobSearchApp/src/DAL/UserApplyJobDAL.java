package DAL;

import Util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.UserApplyJobDTO;
import DTO.UserSaveJobDTO;

public class UserApplyJobDAL {

	private static UserApplyJobDAL _instance;
	public static UserApplyJobDAL GetInstance()
	{
		if(_instance == null)
		{
			_instance = new UserApplyJobDAL();
		}
		return _instance;
	}
	private  UserApplyJobDAL() {
	}
	
	public List<UserApplyJobDTO> SelectAllByJobId(int jobId) {
        Connection conn = null;
        
        try {
            conn = DBUtil.MakeConnection();
            String sql = "SELECT * FROM userapplyjob WHERE job_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = null;
            List<UserApplyJobDTO> applicationList = new ArrayList<>();
            ps.setInt(1, jobId);
            rs = ps.executeQuery();

            while (rs.next()) {
                applicationList.add(new UserApplyJobDTO(
                        rs.getInt("user_apply_job_id"),
                        rs.getInt("user_id"),
                        rs.getInt("job_id"),
                        rs.getString("cv_link"),
                        rs.getInt("state_id")
                ));
            }
            rs.close();
            ps.close();
            return applicationList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
            DBUtil.CloseConnection(conn);
        }
        return null; 
    }

	public List<UserApplyJobDTO> SelectAllByUserId(int userId)
	{
        Connection conn = null;
        
        try {
            conn = DBUtil.MakeConnection();
            String sql = "SELECT * FROM userapplyjob WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = null;
            List<UserApplyJobDTO> applicationList = new ArrayList<>();
            ps.setInt(1, userId);
            rs = ps.executeQuery();

            while (rs.next()) {
                applicationList.add(new UserApplyJobDTO(
                        rs.getInt("user_apply_job_id"),
                        rs.getInt("user_id"),
                        rs.getInt("job_id"),
                        rs.getString("cv_link"),
                        rs.getInt("state_id")
                ));
            }
            rs.close();
            ps.close();
            return applicationList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            
            DBUtil.CloseConnection(conn);
        }
        return null; 
    }
	public boolean Insert(UserApplyJobDTO application) {
        Connection conn = null;

        try {
            conn = DBUtil.MakeConnection();
            String sql = "INSERT INTO userapplyjob (user_id, job_id, cv_link, state_id) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, application.getUser_id());
            ps.setInt(2, application.getJob_id());
            ps.setString(3, application.getCv_link());
            ps.setInt(4, application.getState_id()); 
            int result = ps.executeUpdate();

            ps.close();
            return result > 0; 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.CloseConnection(conn);
        }
        return false; 
    }
	public boolean UpdateState(int applyJobId, int newStateId) {
        Connection conn = null;

        try {
            conn = DBUtil.MakeConnection();
            String sql = "UPDATE userapplyjob SET state_id = ? WHERE user_apply_job_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, newStateId);
            ps.setInt(2, applyJobId);

            int result = ps.executeUpdate();

            ps.close();
            return result > 0; 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.CloseConnection(conn);
        }
        return false; 
    }
	
	public UserApplyJobDTO SelectByUserIdJobId(int userId, int JobId)
	{
		Connection conn = null;
		try {
			conn = DBUtil.MakeConnection();
			String sql = "select * from userapplyjob where user_id = ? and job_id = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.setInt(2, JobId);
			ResultSet rs = ps.executeQuery();
			UserApplyJobDTO usj = null;
			if(rs.next())
			{
				usj = (new UserApplyJobDTO(
                        rs.getInt("user_apply_job_id"),
                        rs.getInt("user_id"),
                        rs.getInt("job_id"),
                        rs.getString("cv_link"),
                        rs.getInt("state_id")));
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
	public boolean IsApplied(int userId, int jobId) {
        Connection conn = null;
  
        try {
            conn = DBUtil.MakeConnection();
            String sql = "SELECT COUNT(*) FROM userapplyjob WHERE user_id = ? AND job_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, jobId);
            ResultSet rs = ps.executeQuery();

            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1) ; 
            }
            if(result > 0)return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.CloseConnection(conn);
        }
        return false; 
    }
	public boolean Delete(int userId,int jobId) {
        Connection conn = null;
 
        try {
            conn = DBUtil.MakeConnection();
            String sql = "DELETE FROM userapplyjob WHERE user_id = ? and job_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, jobId);

            int result = ps.executeUpdate();

            ps.close();
            return result > 0;  
        } catch (Exception e) {
            e.printStackTrace();
         } finally {
             DBUtil.CloseConnection(conn);
        }
        return false; 
    }
	
	public boolean UpdateCvLink(int applyJobId, String newCvLink) {
        Connection conn = null;

        try {
            conn = DBUtil.MakeConnection();
            String sql = "UPDATE userapplyjob SET cv_link = ? , state_id = 0 WHERE user_apply_job_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newCvLink);
            ps.setInt(2, applyJobId);

            int result = ps.executeUpdate();

            ps.close();
            return result > 0; 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBUtil.CloseConnection(conn);
        }
        return false; 
    }

    public boolean deleteByUserId(int userId) {
        Connection conn = null;
        try {
            conn = DBUtil.MakeConnection();
            String sql = "DELETE FROM userapplyjob WHERE user_id = ?";
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

	public static void main(String[] args) {
	    UserApplyJobDAL dal = UserApplyJobDAL.GetInstance();

	    // Test Insert
	    UserApplyJobDTO dto = new UserApplyJobDTO(0, 2, 3, "https://example.com/cv123.pdf", 1);
	    boolean inserted = dal.Insert(dto);
	    System.out.println("Insert: " + inserted);

	    // Test SelectAllByUserId
	    List<UserApplyJobDTO> listByUser = dal.SelectAllByUserId(2);
	    System.out.println("SelectAllByUserId: ");
	    for (UserApplyJobDTO item : listByUser) {
	        System.out.println("ID: " + item.getUser_apply_job_id() + ", Job ID: " + item.getJob_id());
	    }

	    // Test SelectAllByJobId
	    List<UserApplyJobDTO> listByJob = dal.SelectAllByJobId(3);
	    System.out.println("SelectAllByJobId: ");
	    for (UserApplyJobDTO item : listByJob) {
	        System.out.println("ID: " + item.getUser_apply_job_id() + ", User ID: " + item.getUser_id());
	    }

	    // Test IsApplied
	    boolean applied = dal.IsApplied(2, 3);
	    System.out.println("IsApplied (user 2, job 3): " + applied);

	    // Test UpdateState
	    if (!listByUser.isEmpty()) {
	        int applyJobId = listByUser.get(0).getUser_apply_job_id();
	        boolean updated = dal.UpdateState(applyJobId, 2);
	        System.out.println("UpdateState: " + updated);
	    }

	    // Test UpdateCvLink
	    if (!listByUser.isEmpty()) {
	        int applyJobId = listByUser.get(0).getUser_apply_job_id();
	        boolean updatedLink = dal.UpdateCvLink(applyJobId, "https://example.com/updatedcv.pdf");
	        System.out.println("UpdateCvLink: " + updatedLink);
	    }

	    // Test Delete
	    if (!listByUser.isEmpty()) {
	        int applyJobId = listByUser.get(0).getUser_apply_job_id();
//	        boolean deleted = dal.Delete(applyJobId);
//	        System.out.println("Delete: " + deleted);
	    }
	}


}
