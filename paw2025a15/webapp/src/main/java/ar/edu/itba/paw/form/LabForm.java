package ar.edu.itba.paw.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class LabForm {

    @Size(min = 1)
    @NotEmpty(message = "{doctorForm.name.notEmpty}")
    private String name;

    @Size(min = 8)
    @NotEmpty
    private String password;

    @NotEmpty
    private String confirmPassword;

    @NotEmpty(message = "{doctorForm.email.notEmpty}")
    @Email(message = "{doctorForm.email.invalid}")
    private String email;

    @NotEmpty
    @Size(min = 8, max = 20)
    private String phoneNumber;

    @Size(min = 1, max = 50, message = "{form.address.size}")
    @NotEmpty(message = "{form.address.notEmpty}")
    private String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
