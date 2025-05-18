package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DTO.*;
public class Job_DAL {
	private static Job_DAL instance=null;
	private Job_DAL() {
		
	}
	public static Job_DAL getInstance() {
		if(instance==null){
			instance = new Job_DAL();
		}
		return instance;
	}
	public List<Job> getAllJobs() {
        List<Job> jobs = new ArrayList<>();
        String sql = "SELECT * FROM job";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Job job=new Job(
                    rs.getInt("job_id"),
                    rs.getString("job_name"),
                    rs.getDouble("salary"),
                    rs.getString("company_name"),
                    rs.getString("description"),
                    rs.getInt("is_public"),
                    rs.getString("requirement"),
                    rs.getString("address")
                );
                List<Category> list=getCategoriesForJob(conn,job.getJobId());
                job.setCategories(list);
                jobs.add(job);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return jobs;
    }
	private List<Category> getCategoriesForJob(Connection conn,int job_id) throws SQLException{
		List<Category> categories=new ArrayList<>();
		String sql= "Select c.* from category c"
					+" Join categoryofjob coj on c.category_id=coj.category_id" +" Where coj.job_id=?";
		 try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		        stmt.setInt(1, job_id);
		        ResultSet rs = stmt.executeQuery();
		        while (rs.next()) {
		            categories.add(new Category(rs.getInt("category_id"),rs.getString("category_name")));
		        }
		    }
		 return categories;
	}
	public boolean addJob(Job job) {
	    String sql = "INSERT INTO job (job_name, salary, company_name, description, is_public, requirement, address) " +
	                 "VALUES (?, ?, ?, ?, ?, ?, ?)";	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {	        
	        stmt.setString(1, job.getJobName());
	        stmt.setDouble(2, job.getSalary());
	        stmt.setString(3, job.getCompanyName());
	        stmt.setString(4, job.getDescription());
	        stmt.setInt(5, job.isPublic());
	        stmt.setString(6, job.getRequirement());
	        stmt.setString(7, job.getAddress());        
	        int affectedRows = stmt.executeUpdate();        
	        if (affectedRows == 0) {
	            return false;
	        }
	        	        try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                job.setJobId(generatedKeys.getInt(1));
	                	                if (job.getCategories() != null && !job.getCategories().isEmpty()) {
	                    addJobCategories(conn, job.getJobId(), job.getCategories());
	                }
	            }
	        }
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public boolean updateJob(Job job) {
	    String sql = "UPDATE job SET job_name = ?, salary = ?, company_name = ?, description = ?, " +
	                 "is_public = ?, requirement = ?, address = ? WHERE job_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, job.getJobName());
	        stmt.setDouble(2, job.getSalary());
	        stmt.setString(3, job.getCompanyName());
	        stmt.setString(4, job.getDescription());
	        stmt.setInt(5, job.isPublic());
	        stmt.setString(6, job.getRequirement());
	        stmt.setString(7, job.getAddress());
	        stmt.setInt(8, job.getJobId());
	        
	        int affectedRows = stmt.executeUpdate();
	        
	        if (affectedRows > 0 && job.getCategories() != null) {
	            updateJobCategories(conn, job.getJobId(), job.getCategories());
	        }
	        
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	private void updateJobCategories(Connection conn, int jobId, List<Category> categories) throws SQLException {
	    deleteJobCategories(conn, jobId);
	    	    if (!categories.isEmpty()) {
	        addJobCategories(conn, jobId, categories);
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
	        conn = DBConnection.getConnection();
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
	private void addJobCategories(Connection conn, int jobId, List<Category> categories) throws SQLException {
	    String sql = "INSERT INTO categoryofjob (job_id, category_id) VALUES (?, ?)";	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        for (Category category : categories) {
	            stmt.setInt(1, jobId);
	            stmt.setInt(2, category.getCategoryId());
	            stmt.addBatch();
	        }
	        stmt.executeBatch();
	    }
	}
	public Job getJobById(int jobId) {
	    String sql = "SELECT * FROM job WHERE job_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, jobId);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            Job job = new Job(
	                rs.getInt("job_id"),
	                rs.getString("job_name"),
	                rs.getDouble("salary"),
	                rs.getString("company_name"),
	                rs.getString("description"),
	                rs.getInt("is_public"),
	                rs.getString("requirement"),
	                rs.getString("address")
	            );
	            
	            job.setCategories(getCategoriesForJob(conn, jobId));
	            return job;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}
