package ar.edu.itba.paw.form;

import javax.validation.constraints.Size;

public class ChangePasswordForm {

    @Size(min=8)
    private String password;

    @Size(min=8)
    private String repeatPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeatPassword() {
        return repeatPassword;
    }

    public void setRepeatPassword(String repeatPassword) {
        this.repeatPassword = repeatPassword;
    }
}
