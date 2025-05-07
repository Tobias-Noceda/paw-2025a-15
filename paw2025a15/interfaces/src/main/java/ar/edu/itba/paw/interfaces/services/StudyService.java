package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public interface StudyService {
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate);

    public Optional<Study> getStudyById(long id);

    public List<Study> getStudiesByPatientId(long id);

    public Optional<File> getStudyFile(long id);
    
    public List<Study> getFilteredStudies(long id, StudyTypeEnum type, boolean mostRecent);
}
