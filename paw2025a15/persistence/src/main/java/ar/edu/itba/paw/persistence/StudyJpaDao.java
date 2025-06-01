package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

@Repository
public class StudyJpaDao implements StudyDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Study create(StudyTypeEnum type, String comment, File file, Patient patient, User uploader, LocalDate studyDate) {
        final Study study = new Study(type, comment, file, patient, uploader, LocalDateTime.now(), studyDate);
        em.persist(study);
        return study;
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, File file, Patient patient, User uploader) {
        final Study study = new Study(type, comment, file, patient, uploader, LocalDateTime.now(), LocalDate.now());
        em.persist(study);
        return study;
    }

    @Override
    public Optional<Study> findStudyById(long id) {
        return Optional.ofNullable(em.find(Study.class, id));
    }

    @Override
    public boolean deleteStudy(long id) {
        Study study = em.find(Study.class, id);
        if (study == null) {
            return false;
        }
        em.remove(study);
        return true;
    }

    @Override
    public List<Study> getFilteredStudiesByPatient(long patientId, StudyTypeEnum type, boolean mostRecent) {
        Patient patient = em.find(Patient.class, patientId);
        if(patient==null) return Collections.emptyList();
        String q = "from Study as s where s.patient.id = :patientId "
                + (type != null ? "and s.type = :type " : "")
                + (mostRecent ? "order by s.studyDate desc" : "order by s.studyDate asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patientId", patientId);
        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getResultList();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientAndDoctor(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent) {
        Patient patient = em.find(Patient.class, patientId);
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(patient==null || doctor==null) return Collections.emptyList();
        String q = "SELECT s from Study s " +
                "join AuthStudy a on a.study = s " +
                "where s.patient.id = :patientId " +
                "and a.doctor.id = :doctorId ";
        if (type != null) {
            q += "and s.type = :type ";
        }
        q += (mostRecent ? "order by s.studyDate desc" : "order by s.studyDate asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patientId", patientId);
        query.setParameter("doctorId", doctorId);

        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getResultList();
    }
}
