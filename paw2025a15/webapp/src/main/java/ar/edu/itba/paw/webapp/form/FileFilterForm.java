package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public class FileFilterForm {
    private StudyTypeEnum type;

    private boolean mostRecent=true;

    public StudyTypeEnum getType() {
        return type;
    }

    public void setType(StudyTypeEnum type) {
        this.type = type;
    }

    public boolean getMostRecent() {
        return mostRecent;
    }

    public void setMostRecent(boolean mostRecent) {
        this.mostRecent = mostRecent;
    }
}
