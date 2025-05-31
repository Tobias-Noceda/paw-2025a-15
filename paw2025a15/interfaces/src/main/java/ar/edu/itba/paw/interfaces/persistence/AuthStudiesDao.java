package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;

public interface AuthStudiesDao {

    public boolean authStudyForDoctor(Study study, Doctor doctor);

    public boolean hasAuthStudy(Study study, Doctor doctor);

    public void authStudyForDoctorIdList(List<Long> doctorsId, Study study);

    public void unauthStudyForDoctor(Study study, Doctor doctor);

    public void unauthAllStudiesForDoctorAndPatient(Patient patient, Doctor doctor);
}
