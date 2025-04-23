package ar.edu.itba.paw.form;

import org.springframework.web.multipart.MultipartFile;

public class ProfileForm {
    MultipartFile profileImage;

    String phoneNumber;

    public MultipartFile getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(MultipartFile profileImage) {
        this.profileImage = profileImage;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
