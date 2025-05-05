package ar.edu.itba.paw.form;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public class FileFilterForm {
    private StudyTypeEnum type;

    public StudyTypeEnum getType() {
        return type;
    }

    public void setType(StudyTypeEnum type) {
        this.type = type;
    }
}
