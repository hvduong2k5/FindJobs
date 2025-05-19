package BLL;

import java.util.ArrayList;
import java.util.List;
import DAL.Job_DAL;
import DTO.Job;
import DTO.Category;

public class Job_BLL {
    private Job_DAL jobDAL;
    
    public Job_BLL() {
        this.jobDAL = Job_DAL.getInstance();
    }
    
    public List<Job> getAllJobs() {
        return jobDAL.getAllJobs();
    }
    
    public boolean addJob(Job job) {
        if (job == null || job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            return false;
        }
        return jobDAL.addJob(job);
    }
    
    public boolean updateJob(Job job) {
        if (job == null || job.getJobName() == null || job.getJobName().trim().isEmpty()) {
            return false;
        }
        return jobDAL.updateJob(job);
    }
    
    public boolean deleteJob(int jobId) {
        return jobDAL.deleteJob(jobId);
    }
    
    public Job getJobById(int jobId) {
        return jobDAL.getJobById(jobId);
    }
    
    public List<Job> searchJobs(String keyword) {
        List<Job> allJobs = jobDAL.getAllJobs();
        if (keyword == null || keyword.trim().isEmpty()) {
            return allJobs;
        }        
        keyword = keyword.toLowerCase();
        List<Job> result = new ArrayList<>();
        for (Job job : allJobs) {
            if (job.getJobName().toLowerCase().contains(keyword) || 
                job.getCompanyName().toLowerCase().contains(keyword)) {
                result.add(job);
            }
        }
        return result;
    }
}
