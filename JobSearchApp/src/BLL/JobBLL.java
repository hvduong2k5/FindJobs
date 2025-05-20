package BLL;

import java.util.ArrayList;
import java.util.List;

import DAL.JobDAL;
import DTO.JobDTO;
import DTO.CategoryDTO;
import Util.Response;
import DAL.UserSaveJobDAL;
import DAL.UserApplyJobDAL;
import DTO.UserSaveJobDTO;
import DTO.UserApplyJobDTO;

public class JobBLL {
    private JobDAL jobDAL;
    private UserSaveJobDAL userSaveJobDAL;
    private UserApplyJobDAL userApplyJobDAL;
    
    public JobBLL() {
        this.jobDAL = JobDAL.getInstance();
        this.userSaveJobDAL = UserSaveJobDAL.GetInstance();
        this.userApplyJobDAL = UserApplyJobDAL.GetInstance();
    }
    
    public List<JobDTO> getAllJobs() {
        return jobDAL.getAllJobs();
    }
    
    public Response updateJobStatus(int jobId, int status) {
        if (jobId <= 0) {
            return new Response(false, "ID công việc không hợp lệ");
        }
        
        if (status < 0 || status > 2) {
            return new Response(false, "Trạng thái không hợp lệ");
        }
        
        JobDTO job = jobDAL.getJobById(jobId);
        if (job == null) {
            return new Response(false, "Không tìm thấy bài đăng");
        }
        
        job.setPublic(status);
        boolean success = jobDAL.updateJob(job);
        if (success) {
            String statusText = status == 0 ? "chờ duyệt" : 
                              status == 1 ? "đã duyệt" : "đã từ chối";
            return new Response(true, "Cập nhật trạng thái thành công: " + statusText);
        }
        return new Response(false, "Không thể cập nhật trạng thái công việc");
    }
    
    public Response rejectJob(int jobId, String reason) {
        if (jobId <= 0) {
            return new Response(false, "ID công việc không hợp lệ");
        }
        
        if (reason == null || reason.trim().isEmpty()) {
            return new Response(false, "Vui lòng nhập lý do từ chối");
        }
        
        JobDTO job = jobDAL.getJobById(jobId);
        if (job == null) {
            return new Response(false, "Không tìm thấy bài đăng");
        }
        
        job.setPublic(2); // Trạng thái từ chối
        job.setRequirement(reason); // Lưu lý do từ chối vào requirement
        boolean success = jobDAL.updateJob(job);
        if (success) {
            return new Response(true, "Đã từ chối bài đăng thành công");
        }
        return new Response(false, "Không thể từ chối bài đăng");
    }
    
    public boolean addJob(JobDTO job) {
        if (job == null || job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            return false;
        }
        return jobDAL.addJob(job);
    }
    
    public boolean updateJob(JobDTO job) {
        if (job == null || job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            return false;
        }
        return jobDAL.updateJob(job);
    }
    
    public Response deleteJob(int jobId) {
        if (jobId <= 0) {
            return new Response(false, "ID công việc không hợp lệ");
        }
        
        try {
            // Lấy danh sách các bản ghi cần xóa
            List<UserSaveJobDTO> savedJobs = userSaveJobDAL.SelectAllByJobId(jobId);
            List<UserApplyJobDTO> appliedJobs = userApplyJobDAL.SelectAllByJobId(jobId);
            
            // Xóa các bản ghi trong UserSaveJob
            if (savedJobs != null) {
                for (UserSaveJobDTO savedJob : savedJobs) {
                    boolean deleteSavedJob = userSaveJobDAL.Delete(savedJob.getUser_id(), savedJob.getJob_id());
                    if (!deleteSavedJob) {
                        return new Response(false, "Không thể xóa các bài đăng đã lưu liên quan");
                    }
                }
            }
            
            // Xóa các bản ghi trong UserApplyJob
            if (appliedJobs != null) {
                for (UserApplyJobDTO appliedJob : appliedJobs) {
                    boolean deleteAppliedJob = userApplyJobDAL.Delete(appliedJob.getUser_id(), appliedJob.getJob_id());
                    if (!deleteAppliedJob) {
                        return new Response(false, "Không thể xóa các đơn ứng tuyển liên quan");
                    }
                }
            }
            
            // Xóa job (đã có xử lý transaction trong DAL)
            boolean deleteJob = jobDAL.deleteJob(jobId);
            if (!deleteJob) {
                return new Response(false, "Không thể xóa bài đăng");
            }
            
            return new Response(true, "Xóa bài đăng và các dữ liệu liên quan thành công");
            
        } catch (Exception e) {
            return new Response(false, "Lỗi khi xóa bài đăng: " + e.getMessage());
        }
    }
    
    public JobDTO getJobById(int jobId) {
        return jobDAL.getJobById(jobId);
    }
    
    public List<JobDTO> searchJobs(String keyword, int status) {
        List<JobDTO> jobs = jobDAL.getAllJobs();
        if (keyword == null || keyword.trim().isEmpty()) {
            return jobs;
        }        
        keyword = keyword.toLowerCase();
        List<JobDTO> result = new ArrayList<>();
        for (JobDTO job : jobs) {
            if ((status == -1 || job.isPublic() == status) && 
                (job.getJobName().toLowerCase().contains(keyword) || 
                job.getCompanyName().toLowerCase().contains(keyword))) {
                result.add(job);
            }
        }
        return result;
    }
    
    public List<JobDTO> getJobsByStatus(int status) {
        List<JobDTO> allJobs = jobDAL.getAllJobs();
        List<JobDTO> result = new ArrayList<>();
        for (JobDTO job : allJobs) {
            if (job.isPublic() == status) {
                result.add(job);
            }
        }
        return result;
    }
    
    public List<JobDTO> getJobsByEmployer(int employerId) {
        // Vì không có employer_id trong database nên trả về tất cả job
        return jobDAL.getAllJobs();
    }
    
    public int getJobCountByStatus(int status) {
        List<JobDTO> jobs = getJobsByStatus(status);
        return jobs.size();
    }
}
