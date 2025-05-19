package BLL;

import java.util.ArrayList;
import java.util.List;

import DAL.JobDAL;
import DTO.JobDTO;
import DTO.CategoryDTO;

public class Job_BLL {
    private JobDAL jobDAL;
    
    public Job_BLL() {
        this.jobDAL = JobDAL.getInstance();
    }
    
    public List<JobDTO> getAllJobs() {
        return jobDAL.getAllJobs();
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
    
    public boolean deleteJob(int jobId) {
        return jobDAL.deleteJob(jobId);
    }
    
    public JobDTO getJobById(int jobId) {
        return jobDAL.getJobById(jobId);
    }
    
    public List<JobDTO> searchJobs(String keyword) {
        List<JobDTO> allJobs = jobDAL.getAllJobs();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allJobs;
        }        
        keyword = keyword.toLowerCase();
        List<JobDTO> result = new ArrayList<>();
        for (JobDTO job : allJobs) {
            if (job.getJobName().toLowerCase().contains(keyword) || 
                job.getCompanyName().toLowerCase().contains(keyword)) {
                result.add(job);
            }
        }
        return result;
    }
}
