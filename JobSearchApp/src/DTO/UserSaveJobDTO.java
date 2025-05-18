package DTO;

public class UserSaveJobDTO {

    private int user_save_job_id;
    private int user_id;
    private int job_id;

     public UserSaveJobDTO() {
    }

     public UserSaveJobDTO(int user_save_job_id, int user_id, int job_id) {
        this.user_save_job_id = user_save_job_id;
        this.user_id = user_id;
        this.job_id = job_id;
    }

     public int getUser_save_job_id() {
        return user_save_job_id;
    }

    public void setUser_save_job_id(int user_save_job_id) {
        this.user_save_job_id = user_save_job_id;
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
}
