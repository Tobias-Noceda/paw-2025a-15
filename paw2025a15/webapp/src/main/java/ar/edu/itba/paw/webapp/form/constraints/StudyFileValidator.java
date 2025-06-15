package ar.edu.itba.paw.webapp.form.constraints;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.exceptions.MediaTypeException;

public class StudyFileValidator implements ConstraintValidator<ValidStudyFile, List<MultipartFile>>{

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context){
        if (files == null || files.isEmpty()) {
            return false;
        }
        
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null || contentType.isEmpty() || contentType.equals("application/octet-stream")) {
                return false;
            }

            try {
                FileTypeEnum.fromString(contentType);
            } catch (MediaTypeException e) {
                return false;
            }
        }

        return true;
    }
}
