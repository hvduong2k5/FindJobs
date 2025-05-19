package DTO;
import java.util.List;
public class JobDTO {
    private int jobId;
    private String jobName;
    private double salary;
    private String companyName;
    private String description;
    private int isPublic;
    private String requirement;
    private String address;
    private List<CategoryDTO> categoryDTOs;
    public JobDTO() {}

    public JobDTO(int jobId, String jobName, double salary, String companyName, String description, int isPublic, String requirement, String address) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.salary = salary;
        this.companyName = companyName;
        this.description = description;
        this.isPublic = isPublic;
        this.requirement = requirement;
        this.address = address;
        
    }
    public void setCategories(List<CategoryDTO> list) {
    	this.categoryDTOs=list;
    }

    public int getJobId() {
        return jobId;
    }
    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int isPublic() {
        return isPublic;
    }
    public void setPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getRequirement() {
        return requirement;
    }
    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

	public List<CategoryDTO> getCategories() {
		return this.categoryDTOs;
	}
}

