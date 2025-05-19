package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DTO.*;
import Util.DBUtil;
public class JobDAL {
	private static JobDAL instance=null;
	private JobDAL() {
		
	}
	public static JobDAL getInstance() {
		if(instance==null){
			instance = new JobDAL();
		}
		return instance;
	}
	public List<JobDTO> getAllJobs() {
        List<JobDTO> jobDTOs = new ArrayList<>();
        String sql = "SELECT * FROM job";
        try (Connection conn = DBUtil.MakeConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                JobDTO jobDTO=new JobDTO(
                    rs.getInt("job_id"),
                    rs.getString("job_name"),
                    rs.getDouble("salary"),
                    rs.getString("company_name"),
                    rs.getString("description"),
                    rs.getInt("is_public"),
                    rs.getString("requirement"),
                    rs.getString("address")
                );
                List<CategoryDTO> list=getCategoriesForJob(conn,jobDTO.getJobId());
                jobDTO.setCategories(list);
                jobDTOs.add(jobDTO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return jobDTOs;
    }
	private List<CategoryDTO> getCategoriesForJob(Connection conn,int job_id) throws SQLException{
		List<CategoryDTO> categoryDTOs=new ArrayList<>();
		String sql= "Select c.* from category c"
					+" Join categoryofjob coj on c.category_id=coj.category_id" +" Where coj.job_id=?";
		 try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, job_id);
		        ResultSet rs = stmt.executeQuery();
		        while (rs.next()) {
		            categoryDTOs.add(new CategoryDTO(rs.getInt("category_id"),rs.getString("category_name")));
		        }
		    }
		 return categoryDTOs;
	}
	public boolean addJob(JobDTO jobDTO) {
	    String sql = "INSERT INTO job (job_name, salary, company_name, description, is_public, requirement, address) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {	        
	        stmt.setString(1, jobDTO.getJobName());
	        stmt.setDouble(2, jobDTO.getSalary());
	        stmt.setString(3, jobDTO.getCompanyName());
	        stmt.setString(4, jobDTO.getDescription());
	        stmt.setInt(5, jobDTO.isPublic());
	        stmt.setString(6, jobDTO.getRequirement());
	        stmt.setString(7, jobDTO.getAddress());        
	        int affectedRows = stmt.executeUpdate();        
	        if (affectedRows == 0) {
	            return false;
	        }
	        	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                jobDTO.setJobId(generatedKeys.getInt(1));
	                	                if (jobDTO.getCategories() != null && !jobDTO.getCategories().isEmpty()) {
	                    addJobCategories(conn, jobDTO.getJobId(), jobDTO.getCategories());
	                }
	            }
	        }
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean updateJob(JobDTO jobDTO) {
	    String sql = "UPDATE job SET job_name = ?, salary = ?, company_name = ?, description = ?, " +
	                 "is_public = ?, requirement = ?, address = ? WHERE job_id = ?";
	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, jobDTO.getJobName());
	        stmt.setDouble(2, jobDTO.getSalary());
	        stmt.setString(3, jobDTO.getCompanyName());
	        stmt.setString(4, jobDTO.getDescription());
	        stmt.setInt(5, jobDTO.isPublic());
	        stmt.setString(6, jobDTO.getRequirement());
	        stmt.setString(7, jobDTO.getAddress());
	        stmt.setInt(8, jobDTO.getJobId());
	        
	        int affectedRows = stmt.executeUpdate();
	        
	        if (affectedRows > 0 && jobDTO.getCategories() != null) {
	            updateJobCategories(conn, jobDTO.getJobId(), jobDTO.getCategories());
	        }
	        
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	private void updateJobCategories(Connection conn, int jobId, List<CategoryDTO> categoryDTOs) throws SQLException {
	    deleteJobCategories(conn, jobId);
	    	    if (!categoryDTOs.isEmpty()) {
	        addJobCategories(conn, jobId, categoryDTOs);
	    }
	}
	private void deleteJobCategories(Connection conn, int jobId) throws SQLException {
	    String sql = "DELETE FROM categoryofjob WHERE job_id = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, jobId);
	        stmt.executeUpdate();
	    }
	}
	public boolean deleteJob(int jobId) {
	    Connection conn = null;
	    try {
	        conn = DBUtil.MakeConnection();
	        conn.setAutoCommit(false);
	        
	        deleteJobCategories(conn, jobId);
	        
	        String sql = "DELETE FROM job WHERE job_id = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setInt(1, jobId);
	            int affectedRows = stmt.executeUpdate();
	            conn.commit();
	            return affectedRows > 0;
	        }
	    } catch (SQLException e) {
	        if (conn != null) {
	            try {
	                conn.rollback();
	            } catch (SQLException ex) {
	                ex.printStackTrace();
	            }
	        }
	        e.printStackTrace();
	        return false;
	    } finally {
	        if (conn != null) {
	            try {
	                conn.setAutoCommit(true);
	                conn.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	private void addJobCategories(Connection conn, int jobId, List<CategoryDTO> categoryDTOs) throws SQLException {
	    String sql = "INSERT INTO categoryofjob (job_id, category_id) VALUES (?, ?)";	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        for (CategoryDTO categoryDTO : categoryDTOs) {
	            stmt.setInt(1, jobId);
	            stmt.setInt(2, categoryDTO.getCategoryId());
	            stmt.addBatch();
	        }
	        stmt.executeBatch();
	    }
	}
	public JobDTO getJobById(int jobId) {
	    String sql = "SELECT * FROM job WHERE job_id = ?";
	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, jobId);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            JobDTO jobDTO = new JobDTO(
	                rs.getInt("job_id"),
	                rs.getString("job_name"),
	                rs.getDouble("salary"),
	                rs.getString("company_name"),
	                rs.getString("description"),
	                rs.getInt("is_public"),
	                rs.getString("requirement"),
	                rs.getString("address")
	            );
	            
	            jobDTO.setCategories(getCategoriesForJob(conn, jobId));
	            return jobDTO;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}
