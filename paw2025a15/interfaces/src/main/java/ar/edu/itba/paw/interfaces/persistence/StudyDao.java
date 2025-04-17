package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.StudyTypeEnum;

public interface StudyDao {
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate);

    public Optional<Study> getStudyById(long id);

    public List<Study> getStudiesByPatientId(long id);
}
