package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

public class NewPasswordSetDto {
    @NotNull
    @NotBlank
    @Length(min = 8, max = 64)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }
}
