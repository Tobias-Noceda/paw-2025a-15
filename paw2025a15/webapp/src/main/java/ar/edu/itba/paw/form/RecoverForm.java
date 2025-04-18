package ar.edu.itba.paw.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class RecoverForm {

    @Size(min = 1, max = 50, message = "email must be less than 100 characters")
    @NotEmpty(message = "{doctorForm.email.notEmpty}")
    @Email(message = "{doctorForm.email.invalid}")
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
