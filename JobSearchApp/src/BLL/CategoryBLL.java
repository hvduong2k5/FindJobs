package BLL;


import java.util.ArrayList;
import java.util.List;

import DAL.CategoryDAL;
import DTO.CategoryDTO;
import DTO.JobDTO;

public class CategoryBLL {
    private CategoryDAL categoryDAL;
    
    public CategoryBLL() {
        this.categoryDAL = CategoryDAL.getInstance();
    }
    
    public List<CategoryDTO> getAllCategories() {
        return categoryDAL.getAllCategories();
    }
    
    public boolean addCategory(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return false;
        }
        return categoryDAL.addCategory(categoryName);
    }
        public boolean updateCategory(CategoryDTO category) {
        if (category == null || category.getCategoryName() == null || 
            category.getCategoryName().trim().isEmpty()) {
            return false;
        }
        return categoryDAL.updateCategory(category);
    }
    
    public boolean deleteCategory(int categoryId) {
        return categoryDAL.deleteCategory(categoryId);
    }
    
    public CategoryDTO getCategoryById(int categoryId) {
        return categoryDAL.getCategoryById(categoryId);
    }
    
    public List<JobDTO> getJobsByCategory(int categoryId) {
        return categoryDAL.getJobsByCategory(categoryId);
    }
    
    public List<CategoryDTO> searchCategories(String keyword) {
        List<CategoryDTO> allCategories = categoryDAL.getAllCategories();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allCategories;
        }
        
        keyword = keyword.toLowerCase();
        List<CategoryDTO> result = new ArrayList<>();
        for (CategoryDTO category : allCategories) {
            if (category.getCategoryName().toLowerCase().contains(keyword)) {
                result.add(category);
            }
        }
        return result;
    }
}