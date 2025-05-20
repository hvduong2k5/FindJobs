package BLL;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import DAL.JobDAL;
import DAL.UserApplyJobDAL;
import DAL.UserDAL;
import DTO.JobDTO;
import DTO.UserApplyJobDTO;
import DTO.UserDTO;
import Util.Response;

public class UserApplyJobBLL {
    private final UserApplyJobDAL userApplyJobDAL;
    private final JobDAL jobDAL;
    private final UserDAL userDAL;

    public UserApplyJobBLL() {
        this.userApplyJobDAL = UserApplyJobDAL.GetInstance();
        this.jobDAL = JobDAL.getInstance();
        this.userDAL = UserDAL.GetInstance();
    }

    


    
    public Response applyJob(int userId, int jobId, String cvLink) {
        try {
            if (cvLink == null || cvLink.trim().isEmpty()) {
                return Response.Error("Link CV không được để trống.");
            }

            UserDTO user = userDAL.SelectById(userId);
            if (user == null) {
                return Response.Error("Người dùng không tồn tại.");
            }

            JobDTO job = jobDAL.getJobById(jobId);
            if (job == null) {
                return Response.Error("Công việc không tồn tại.");
            }

            if (userApplyJobDAL.IsApplied(userId, jobId)) {
                return Response.Error("Bạn đã ứng tuyển công việc này rồi.");
            }

            UserApplyJobDTO application = new UserApplyJobDTO();
            application.setUser_id(userId);
            application.setJob_id(jobId);
            application.setCv_link(cvLink);
            application.setState_id(0); // 0: Chờ duyệt

            if (userApplyJobDAL.Insert(application)) {
                return Response.Success("Ứng tuyển thành công.");
            } else {
                return Response.Error("Không thể ứng tuyển do lỗi hệ thống.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống khi ứng tuyển: " + ex.getMessage());
        }
    }

    
    public Response deleteApplyJob(int jobId, int userId) {
        try {
            UserApplyJobDTO existingApplication = userApplyJobDAL.SelectByUserIdJobId(userId, jobId);
            if (existingApplication == null) {
                return Response.Error("Đơn ứng tuyển không tồn tại hoặc bạn không có quyền xóa.");
            }


            if (userApplyJobDAL.Delete(userId,jobId)) {
                return Response.Success("Đã xóa đơn ứng tuyển.");
            } else {
                return Response.Error("Không thể xóa đơn ứng tuyển do lỗi hệ thống.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống khi xóa đơn ứng tuyển: " + ex.getMessage());
        }
    }

    
    public Response updateCvLink(int jobId, int userId, String newCvLink)
    {
        try {
            if (newCvLink == null || newCvLink.trim().isEmpty()) {
                return Response.Error("Link CV mới không được để trống.");
            }

            UserApplyJobDTO existingApplication = userApplyJobDAL.SelectByUserIdJobId(userId,jobId);
            if (existingApplication == null) {
                return Response.Error("Đơn ứng tuyển không tồn tại hoặc bạn không có quyền cập nhật.");
            }

            // Không cho cập nhật CV nếu đã được duyệt hoặc từ chối
            if (existingApplication.getState_id() == 1 || existingApplication.getState_id() == -1) {
                return Response.Error("Không thể cập nhật CV cho đơn ứng tuyển đã được duyệt hoặc từ chối.");
            }

            if (userApplyJobDAL.UpdateCvLink(existingApplication.getUser_apply_job_id(), newCvLink)) {
                return Response.Success("Cập nhật link CV thành công. Trạng thái đã được reset về 'Chờ duyệt'.");
            } else {
                return Response.Error("Không thể cập nhật link CV do lỗi hệ thống.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống khi cập nhật CV: " + ex.getMessage());
        }
    }

    
    public List<JobDTO> getAppliedJobsByUserIdAndJobName(int userId, String jobNameQuery) {
        List<JobDTO> appliedJobsResult = new ArrayList<>();
        try {
            List<UserApplyJobDTO> applications = userApplyJobDAL.SelectAllByUserId(userId);
            if (applications == null || applications.isEmpty()) {
                return appliedJobsResult;
            }

            List<Integer> appliedJobIds = applications.stream()
                                                 .map(UserApplyJobDTO::getJob_id)
                                                 .distinct()
                                                 .collect(Collectors.toList());

            if (appliedJobIds.isEmpty()) {
                return appliedJobsResult;
            }

            String lowerJobNameQuery = (jobNameQuery == null) ? "" : jobNameQuery.trim().toLowerCase();

            for (Integer jobId : appliedJobIds) {
                JobDTO job = jobDAL.getJobById(jobId);
                if (job != null) {
                    if (lowerJobNameQuery.isEmpty() || job.getJobName().toLowerCase().contains(lowerJobNameQuery)) {
                        appliedJobsResult.add(job);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi
        }
        return appliedJobsResult;
    }

    
    public UserApplyJobDTO getApplicationByUserIdAndJobId(int userId, int jobId) {
        try {
            
            List<UserApplyJobDTO> applications = userApplyJobDAL.SelectAllByUserId(userId);
            if (applications != null) {
                return applications.stream()
                        .filter(app -> app.getJob_id() == jobId)
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * HR lấy danh sách người dùng (UserDTO) đã ứng tuyển cho một công việc cụ thể.
     * Frontend sẽ dùng thông tin user và gọi API khác để lấy CV/trạng thái.
     *
     * @param jobId ID của công việc
     * @return Danh sách UserDTO của các ứng viên.
     */
    public List<UserDTO> getApplicantUsersForJob(int jobId) {
        List<UserDTO> applicantUsers = new ArrayList<>();
        try {
            JobDTO job = jobDAL.getJobById(jobId);
            if (job == null) {
                System.err.println("HR - Lấy ứng viên: Công việc không tồn tại với ID: " + jobId);
                return applicantUsers; // Trả về danh sách rỗng
            }

            List<UserApplyJobDTO> applications = userApplyJobDAL.SelectAllByJobId(jobId);
            if (applications == null || applications.isEmpty()) {
                return applicantUsers; // Không có ứng viên
            }

            Set<Integer> userIds = applications.stream()
                                             .map(UserApplyJobDTO::getUser_id)
                                             .collect(Collectors.toSet());

            for (Integer userId : userIds) {
                UserDTO user = userDAL.SelectById(userId);
                if (user != null) {
                    user.setPassword(null); // Không bao giờ trả về mật khẩu
                    applicantUsers.add(user);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return applicantUsers;
    }

    /**
     * HR cập nhật trạng thái apply.
     *
     * @param userApplyJobId ID của đơn ứng tuyển
     * @param newStateId     Trạng thái mới (0: Chờ duyệt, 1: Được duyệt, -1: Từ chối)
     * @param hrUserId       ID của HR (để kiểm tra quyền, ví dụ: HR phải có role phù hợp)
     * @return Response chứa kết quả của thao tác
     */
    public Response updateApplicationStatus(int userApplyJobId, int newStateId, int hrUserId) {
        try {
            UserDTO hrUser = userDAL.SelectById(hrUserId);
            // Kiểm tra quyền của HR. Ví dụ: role >= 2 (HR) hoặc role == 99 (Admin)
            // Điều này cần được định nghĩa rõ ràng trong hệ thống của bạn.
            if (hrUser == null || hrUser.getRole() < 2) { // Giả sử role 0, 1 là user thường
                return Response.Error("Bạn không có quyền thực hiện thao tác này.");
            }

            // Kiểm tra xem đơn ứng tuyển có tồn tại không (quan trọng!)
            // UserApplyJobDAL nên có hàm getById(applyId)
            // Nếu không có, thì việc update có thể thành công mà không biết applyId đó có thật hay không
            // Tuy nhiên, userApplyJobDAL.UpdateState sẽ trả về false nếu không có dòng nào được cập nhật.

            if (newStateId < -1 || newStateId > 1) {
                return Response.Error("Trạng thái cập nhật không hợp lệ. Chỉ chấp nhận 0, 1, -1.");
            }

            if (userApplyJobDAL.UpdateState(userApplyJobId, newStateId)) {
                return Response.Success("Cập nhật trạng thái ứng tuyển thành công.");
            } else {
                return Response.Error("Không thể cập nhật trạng thái. Đơn ứng tuyển có thể không tồn tại hoặc đã ở trạng thái này.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.Error("Lỗi hệ thống khi cập nhật trạng thái: " + ex.getMessage());
        }
    }
}