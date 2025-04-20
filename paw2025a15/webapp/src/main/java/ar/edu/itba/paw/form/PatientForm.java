package ar.edu.itba.paw.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class PatientForm {
    @Size(min = 1, max = 50, message = "name must be less than 50 characters")
    @NotEmpty(message = "{doctorForm.name.notEmpty}")
    private String name;

    @Size(min = 1, max = 50, message = "surname must be less than 50 characters")
    @NotEmpty(message = "{doctorForm.surname.notEmpty}")
    private String surname;

    @Size(min=8)
    @NotEmpty
    private String password;

    @Size(min = 1, max = 50, message = "email must be less than 100 characters")
    @NotEmpty(message = "{doctorForm.email.notEmpty}")
    @Email(message = "{doctorForm.email.invalid}")
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
