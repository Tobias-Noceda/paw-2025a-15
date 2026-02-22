package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.Size;

public class PatientEditSocialInfoDTO {
    
    @Size(max = 100)
    private String hobbies;

    @Size(max = 50)
    private String job;

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
