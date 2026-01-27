package ar.edu.itba.paw.webapp.dto.input;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class NewPassWordRequestDto {
    @NotNull
    @NotBlank
    @Email
    private String email;

    public NewPassWordRequestDto() {
        // Default constructor for deserialization
    }

    public NewPassWordRequestDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
