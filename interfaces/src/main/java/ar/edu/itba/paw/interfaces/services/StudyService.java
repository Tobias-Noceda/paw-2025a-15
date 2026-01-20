package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public interface StudyService {
    public Study create(StudyTypeEnum type, String comment, List<File> files, long userId, long uploaderId, LocalDate studyDate);

    public Study create(StudyTypeEnum type, String comment, List<File> files, long userId, long uploaderId);

    public Optional<Study> getStudyById(long id);

    public boolean deleteStudy(long id);

    public boolean isFileInStudy(long studyId, long fileId);

    public int getStudyFilesCount(long studyId);

    public List<File> getStudyFilesPage(long studyId, int page, int pageSize);

    public int getFilteredStudiesCount(long patientId, Long doctorId, StudyTypeEnum type);

    public List<Study> getFilteredStudiesPage(long patientId, Long doctorId, StudyTypeEnum type, boolean mostRecent, int page, int pageSize);
}
