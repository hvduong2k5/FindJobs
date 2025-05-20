package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import DAL.JobDAL;
import DAL.UserSaveJobDAL;
import DTO.JobDTO;
import DTO.UserSaveJobDTO;

public class UserSaveJobBLL {

	private UserSaveJobDAL  dal  = UserSaveJobDAL.GetInstance();
	private JobDAL jobDAL = JobDAL.getInstance();
	
	public boolean AddJobToSave(int userId, int jobId)
	{
		UserSaveJobDTO addSaveJob = new UserSaveJobDTO();
		addSaveJob.setJob_id(jobId);
		addSaveJob.setUser_id(userId);
		if(!dal.IsJobSaved(userId, jobId)) {
			return dal.Insert(addSaveJob);
		}
		return false;
	}
	
	public boolean DeleteJobSaved(int userId, int jobId)
	{
		return dal.Delete(userId, jobId);
	}
	
	public List<JobDTO> GetAllJobSavedOfUser(int userId, String jobName) {
        List<JobDTO> savedJobs = new ArrayList<>();

        List<UserSaveJobDTO> userSavedJobEntries = dal.SelectAllByUserId(userId);

        if (userSavedJobEntries == null) {
            return savedJobs; // Trả về danh sách rỗng khi có lỗi DAL
        }

        for (UserSaveJobDTO savedEntry : userSavedJobEntries) {
            int jobId = savedEntry.getJob_id();

            JobDTO job = jobDAL.getJobById(jobId); // Giả định JobDAL có SelectById(int jobId)

            if (job != null) {
                savedJobs.add(job);
            } 
        }

        if (jobName != null && !jobName.trim().isEmpty()) {
            String lowerCaseJobName = jobName.trim().toLowerCase();
            return savedJobs.stream()
                            .filter(job -> job.getJobName() != null && job.getJobName().toLowerCase().contains(lowerCaseJobName)) // Giả định JobDTO có phương thức getName()
                            .collect(Collectors.toList());
        } else {
            // Nếu jobName rỗng hoặc null, trả về toàn bộ danh sách job đã lưu
            return savedJobs;
        }
    }
}
