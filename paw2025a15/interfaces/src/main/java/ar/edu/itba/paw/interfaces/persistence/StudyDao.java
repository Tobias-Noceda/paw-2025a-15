package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.StudyTypeEnum;

public interface StudyDao {
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate);

    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId);

    public List<Study> getStudiesByPatientId(long id);
}
