package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.StudyTypeEnum;

public interface StudyService {
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate);

    public List<Study> getStudiesByPatientId(long id);
}
