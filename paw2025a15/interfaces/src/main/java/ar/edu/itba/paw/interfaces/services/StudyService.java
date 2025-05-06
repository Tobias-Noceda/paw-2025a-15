package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public interface StudyService {
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate);

    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId);

    public Optional<Study> getStudyById(long id);

    public List<Study> getStudiesByPatientId(long id);

    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId);

    public boolean authStudyForDoctorId(long studyId, long doctorId);

    public boolean hasAuthStudy(long studyId, long doctorId);

    public void unauthStudyForDoctorId(long studyId, long doctorId);
}
