package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public interface StudyDao {
    public Study create(StudyTypeEnum type, String comment, List<File> file, Patient patient, User uploader, LocalDate studyDate);

    public Study create(StudyTypeEnum type, String comment, List<File> file, Patient patient, User uploader);

    public Optional<Study> findStudyById(long id);

    public boolean deleteStudy(long id);

    public boolean isFileInStudy(long studyId, long fileId);

    public List<Study> getFilteredStudiesByPatient(long patientId, StudyTypeEnum type, boolean mostRecent);
    
    public List<Study> getFilteredStudiesByPatientAndDoctor(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent);
}
