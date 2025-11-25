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

    public List<Study> getFilteredStudies(long id, StudyTypeEnum type, boolean mostRecent);

    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent);
}
