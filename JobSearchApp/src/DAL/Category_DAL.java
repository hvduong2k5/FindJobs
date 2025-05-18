package DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.*;
public class Category_DAL {
	private static Category_DAL instance=null;
	private Category_DAL() {
		
	}
	public static Category_DAL getInstance() {
		if(instance==null){
			instance = new Category_DAL();
		}
		return instance;
	}
	public boolean addCategory(String category_name) {
		String sql="Insert into category (category_name) values (?)";
		try (Connection conn = DBConnection.getConnection();
		         PreparedStatement stmt = conn.prepareStatement(sql)) {		        
		        stmt.setString(1, category_name);
		        return stmt.executeUpdate() > 0;
		        
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
	}
	public boolean deleteCategory(int id) {
		String sql = "DELETE FROM category WHERE category_id = ?";	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {        
	        stmt.setInt(1, id);
	        int affectedRows = stmt.executeUpdate();
	        	        return affectedRows == 1;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public List<Category> getAllCategories(){
		 List<Category> categories = new ArrayList<>();
	        String sql = "SELECT * FROM category";
	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                categories.add(new Category(rs.getInt("category_id"),rs.getString("category_name")));}}
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return categories;
	}
	public boolean updateCategory(Category category) {
	    String sql = "UPDATE category SET category_name = ? WHERE category_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, category.getCategoryName());
	        stmt.setInt(2, category.getCategoryId());
	        
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public Category getCategoryById(int categoryId) {
	    String sql = "SELECT * FROM category WHERE category_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, categoryId);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            return new Category(
	                rs.getInt("category_id"),
	                rs.getString("category_name")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	public List<Job> getJobsByCategory(int categoryId) {
	    List<Job> jobs = new ArrayList<>();
	    String sql = "SELECT j.* FROM job j " +
	                 "JOIN categoryofjob cj ON j.job_id = cj.job_id " +
	                 "WHERE cj.category_id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, categoryId);
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
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
	            
	            job.setCategories(getCategoriesForJob(conn, job.getJobId()));
	            jobs.add(job);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return jobs;
	}
	private List<Category> getCategoriesForJob(Connection conn, int jobId) throws SQLException {
	    List<Category> categories = new ArrayList<>();
	    String sql = "SELECT c.* FROM category c " +
	                 "JOIN categoryofjob cj ON c.category_id = cj.category_id " +
	                 "WHERE cj.job_id = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, jobId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            categories.add(new Category(
	                rs.getInt("category_id"),
	                rs.getString("category_name")
	            ));
	        }
	    }
	    return categories;
	}
	
}
