package ar.edu.itba.paw.webapp.form.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.MediaTypeException;

public class ProfileImageValidator implements ConstraintValidator<ValidProfileImage, MultipartFile>{

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context){
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            return true;
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty() || contentType.equals("application/octet-stream") || contentType.equals("application/pdf")) {
            return false;
        }

        try {
            FileTypeEnum.fromString(contentType);
        } catch (MediaTypeException e) {
            return false;
        }

        return true;
    }
}
