package DTO;

public class UserApplyJobDTO {
    private int user_apply_job_id;
    private int user_id;
    private int job_id;
    private String cv_link;
    private int state_id;

    public UserApplyJobDTO(int user_apply_job_id, int user_id, int job_id, String cv_link, int state_id) {
        this.user_apply_job_id = user_apply_job_id;
        this.user_id = user_id;
        this.job_id = job_id;
        this.cv_link = cv_link;
        this.state_id = state_id;
    }

    public UserApplyJobDTO() {
    }

    public int getUser_apply_job_id() {
        return user_apply_job_id;
    }

    public void setUser_apply_job_id(int user_apply_job_id) {
        this.user_apply_job_id = user_apply_job_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public String getCv_link() {
        return cv_link;
    }

    public void setCv_link(String cv_link) {
        this.cv_link = cv_link;
    }

    public int getState_id() {
        return state_id;
    }

    public void setState_id(int state_id) {
        this.state_id = state_id;
    }
}
