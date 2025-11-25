package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.webapp.form.constraints.ValidProfileImage;

public class InsuranceForm {

    @NotEmpty(message = "{form.name.notEmpty}")
    private String name;

    @ValidProfileImage
    private MultipartFile picture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MultipartFile getPicture() {
        return picture;
    }

    public void setPicture(MultipartFile picture) {
        this.picture = picture;
    }
}