package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import DAL.CategoryDAL;
import DAL.JobDAL;
import DAL.UserSaveJobDAL;
import DAL.UserApplyJobDAL;
import DTO.CategoryDTO;
import DTO.JobDTO;
import DTO.UserSaveJobDTO;
import DTO.UserApplyJobDTO;
import Util.Response;

public class CategoryBLL {
    private CategoryDAL categoryDAL;
    private JobDAL jobDAL;
    private UserSaveJobDAL userSaveJobDAL;
    private UserApplyJobDAL userApplyJobDAL;
    
    public CategoryBLL() {
        this.categoryDAL = CategoryDAL.getInstance();
        this.jobDAL = JobDAL.getInstance();
        this.userSaveJobDAL = UserSaveJobDAL.GetInstance();
        this.userApplyJobDAL = UserApplyJobDAL.GetInstance();
    }
    
    public List<CategoryDTO> getAllCategories() {
        return categoryDAL.getAllCategories();
    }
    
    public CategoryDTO getCategoryById(int categoryId) {
        return categoryDAL.getCategoryById(categoryId);
    }
    
    public Response addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new Response(false, "Tên danh mục không được để trống");
        }
        
        // Kiểm tra trùng tên
        List<CategoryDTO> categories = categoryDAL.getAllCategories();
        for (CategoryDTO category : categories) {
            if (category.getCategoryName().equalsIgnoreCase(name.trim())) {
                return new Response(false, "Tên danh mục đã tồn tại");
            }
        }
        
        if (categoryDAL.addCategory(name.trim())) {
            return new Response(true, "Thêm danh mục thành công");
        }
        return new Response(false, "Không thể thêm danh mục");
    }
    
    public Response updateCategory(int categoryId, String newName) {
        if (categoryId <= 0) {
            return new Response(false, "ID danh mục không hợp lệ");
        }
        
        if (newName == null || newName.trim().isEmpty()) {
            return new Response(false, "Tên danh mục không được để trống");
        }
        
        // Kiểm tra trùng tên với các danh mục khác
        List<CategoryDTO> categories = categoryDAL.getAllCategories();
        for (CategoryDTO category : categories) {
            if (category.getCategoryId() != categoryId && 
                category.getCategoryName().equalsIgnoreCase(newName.trim())) {
                return new Response(false, "Tên danh mục đã tồn tại");
            }
        }
        
        CategoryDTO category = categoryDAL.getCategoryById(categoryId);
        if (category == null) {
            return new Response(false, "Không tìm thấy danh mục");
        }
        
        category.setCategoryName(newName.trim());
        if (categoryDAL.updateCategory(category)) {
            return new Response(true, "Cập nhật danh mục thành công");
        }
        return new Response(false, "Không thể cập nhật danh mục");
    }
    
    public Response deleteCategory(int categoryId) {
        if (categoryId <= 0) {
            return new Response(false, "ID danh mục không hợp lệ");
        }
        
        // Kiểm tra danh mục tồn tại
        CategoryDTO category = categoryDAL.getCategoryById(categoryId);
        if (category == null) {
            return new Response(false, "Không tìm thấy danh mục");
        }
        
        try {
            // Lấy danh sách job thuộc category này
            List<JobDTO> jobs = categoryDAL.getJobsByCategory(categoryId);
            
            // Xóa các bản ghi liên quan đến từng job
            for (JobDTO job : jobs) {
                // Kiểm tra xem job có thuộc category khác không
                List<CategoryDTO> jobCategories = job.getCategories();
                boolean hasOtherCategories = jobCategories.stream()
                    .anyMatch(cat -> cat.getCategoryId() != categoryId);
                
                // Chỉ xóa job và các bản ghi liên quan nếu job không còn category nào khác
                if (!hasOtherCategories) {
                    // Lấy danh sách các bản ghi cần xóa
                    List<UserSaveJobDTO> savedJobs = userSaveJobDAL.SelectAllByJobId(job.getJobId());
                    List<UserApplyJobDTO> appliedJobs = userApplyJobDAL.SelectAllByJobId(job.getJobId());
                    
                    // Xóa các bài đăng đã lưu
                    if (savedJobs != null) {
                        for (UserSaveJobDTO savedJob : savedJobs) {
                            boolean deleteSavedJob = userSaveJobDAL.Delete(savedJob.getUser_id(), savedJob.getJob_id());
                            if (!deleteSavedJob) {
                                return new Response(false, "Không thể xóa các bài đăng đã lưu liên quan");
                            }
                        }
                    }
                    
                    // Xóa các đơn ứng tuyển
                    if (appliedJobs != null) {
                        for (UserApplyJobDTO appliedJob : appliedJobs) {
                            boolean deleteAppliedJob = userApplyJobDAL.Delete(appliedJob.getUser_id(), appliedJob.getJob_id());
                            if (!deleteAppliedJob) {
                                return new Response(false, "Không thể xóa các đơn ứng tuyển liên quan");
                            }
                        }
                    }
                    
                    // Xóa job
                    boolean deleteJob = jobDAL.deleteJob(job.getJobId());
                    if (!deleteJob) {
                        return new Response(false, "Không thể xóa các bài đăng trong danh mục");
                    }
                } else {
                    // Nếu job còn category khác, chỉ xóa liên kết với category hiện tại
                    // Cập nhật lại danh sách category của job
                    List<CategoryDTO> remainingCategories = jobCategories.stream()
                        .filter(cat -> cat.getCategoryId() != categoryId)
                        .collect(Collectors.toList());
                    job.setCategories(remainingCategories);
                    
                    // Cập nhật job với danh sách category mới
                    if (!jobDAL.updateJob(job)) {
                        return new Response(false, "Không thể xóa liên kết giữa bài đăng và danh mục");
                    }
                }
            }
            
            // Xóa category
            boolean deleteCategory = categoryDAL.deleteCategory(categoryId);
            if (!deleteCategory) {
                return new Response(false, "Không thể xóa danh mục");
            }
            
            return new Response(true, "Xóa danh mục và các dữ liệu liên quan thành công");
            
        } catch (Exception e) {
            return new Response(false, "Lỗi khi xóa danh mục: " + e.getMessage());
        }
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