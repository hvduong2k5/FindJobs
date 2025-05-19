package DAL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import DTO.*;
import Util.DBUtil;
public class CategoryDAL {
	private static CategoryDAL instance=null;
	private CategoryDAL() {
		
	}
	public static CategoryDAL getInstance() {
		if(instance==null){
			instance = new CategoryDAL();
		}
		return instance;
	}
	public boolean addCategory(String category_name) {
		String sql="Insert into category (category_name) values (?)";
		try (Connection conn = DBUtil.MakeConnection();
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
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {        
	        stmt.setInt(1, id);
	        int affectedRows = stmt.executeUpdate();
	        	        return affectedRows == 1;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public List<CategoryDTO> getAllCategories(){
		 List<CategoryDTO> categoryDTOs = new ArrayList<>();
	        String sql = "SELECT * FROM category";
	        try (Connection conn = DBUtil.MakeConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	            ResultSet rs = stmt.executeQuery();
	            while (rs.next()) {
	                categoryDTOs.add(new CategoryDTO(rs.getInt("category_id"),rs.getString("category_name")));}}
	        catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return categoryDTOs;
	}
	public boolean updateCategory(CategoryDTO categoryDTO) {
	    String sql = "UPDATE category SET category_name = ? WHERE category_id = ?";
	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, categoryDTO.getCategoryName());
	        stmt.setInt(2, categoryDTO.getCategoryId());
	        
	        return stmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public CategoryDTO getCategoryById(int categoryId) {
	    String sql = "SELECT * FROM category WHERE category_id = ?";
	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, categoryId);
	        ResultSet rs = stmt.executeQuery();
	        
	        if (rs.next()) {
	            return new CategoryDTO(
	                rs.getInt("category_id"),
	                rs.getString("category_name")
	            );
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	public List<JobDTO> getJobsByCategory(int categoryId) {
	    List<JobDTO> jobDTOs = new ArrayList<>();
	    String sql = "SELECT j.* FROM job j " +
	                 "JOIN categoryofjob cj ON j.job_id = cj.job_id " +
	                 "WHERE cj.category_id = ?";
	    
	    try (Connection conn = DBUtil.MakeConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, categoryId);
	        ResultSet rs = stmt.executeQuery();
	        
	        while (rs.next()) {
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
	            
	            jobDTO.setCategories(getCategoriesForJob(conn, jobDTO.getJobId()));
	            jobDTOs.add(jobDTO);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return jobDTOs;
	}
	private List<CategoryDTO> getCategoriesForJob(Connection conn, int jobId) throws SQLException {
	    List<CategoryDTO> categoryDTOs = new ArrayList<>();
	    String sql = "SELECT c.* FROM category c " +
	                 "JOIN categoryofjob cj ON c.category_id = cj.category_id " +
	                 "WHERE cj.job_id = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, jobId);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            categoryDTOs.add(new CategoryDTO(
	                rs.getInt("category_id"),
	                rs.getString("category_name")
	            ));
	        }
	    }
	    return categoryDTOs;
	}
	
}
