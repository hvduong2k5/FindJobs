package BLL;


import java.util.ArrayList;
import java.util.List;
import DAL.Category_DAL;
import DTO.Category;
import DTO.Job;

public class Category_BLL {
    private Category_DAL categoryDAL;
    
    public Category_BLL() {
        this.categoryDAL = Category_DAL.getInstance();
    }
    
    public List<Category> getAllCategories() {
        return categoryDAL.getAllCategories();
    }
    
    public boolean addCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        return categoryDAL.addCategory(categoryName);
    }
        public boolean updateCategory(Category category) {
        if (category == null || category.getCategoryName() == null || 
            category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        return categoryDAL.updateCategory(category);
    }
    
    public boolean deleteCategory(int categoryId) {
        return categoryDAL.deleteCategory(categoryId);
    }
    
    public Category getCategoryById(int categoryId) {
        return categoryDAL.getCategoryById(categoryId);
    }
    
    public List<Job> getJobsByCategory(int categoryId) {
        return categoryDAL.getJobsByCategory(categoryId);
    }
    
    public List<Category> searchCategories(String keyword) {
        List<Category> allCategories = categoryDAL.getAllCategories();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCategories;
        }
        
        keyword = keyword.toLowerCase();
        List<Category> result = new ArrayList<>();
        for (Category category : allCategories) {
            if (category.getCategoryName().toLowerCase().contains(keyword)) {
                result.add(category);
            }
        }
        return result;
    }
}