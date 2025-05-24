package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public interface StudyDao {
    public Study create(StudyTypeEnum type, String comment, File file, User user, User uploader, LocalDate studyDate);

    public Study create(StudyTypeEnum type, String comment, File file, User user, User uploader);

    public Optional<Study> findStudyById(long id);

    public List<Study> getStudiesByPatientId(long id);

    public List<Study> getFilteredStudiesByPatientId(long id, StudyTypeEnum type, boolean mostRecent);
    
    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId);
    
    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent);

}
