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
    public Study create(StudyTypeEnum type, String comment, List<File> files, Patient patient, User uploader, LocalDate studyDate) {
        final Study study = new Study(type, comment, files, patient, uploader, LocalDateTime.now(), studyDate);
        em.persist(study);
        return study;
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, List<File> files, Patient patient, User uploader) {
        final Study study = new Study(type, comment, files, patient, uploader, LocalDateTime.now(), LocalDate.now());
        em.persist(study);
        return study;
    }

    @Override
    public Optional<Study> findStudyById(long id) {
        return em.createQuery(
            "SELECT s FROM Study s " +
            "JOIN FETCH s.patient " +
            "JOIN FETCH s.uploader " +
            "WHERE s.id = :id", Study.class)
            .setParameter("id", id)
            .getResultList()
            .stream()
            .findFirst();
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
    public boolean isFileInStudy(long studyId, long fileId) {
        String q = "SELECT COUNT(f) > 0 FROM Study s JOIN s.files f WHERE s.id = :studyId AND f.id = :fileId";
        TypedQuery<Boolean> query = em.createQuery(q, Boolean.class);
        query.setParameter("studyId", studyId);
        query.setParameter("fileId", fileId);
        return query.getSingleResult();
    }

    @Override
    public int getStudyFilesCount(long studyId) {
        Study study = em.find(Study.class, studyId);
        if(study==null) return 0;
        String q = "SELECT COUNT(f) FROM Study s JOIN s.files f WHERE s.id = :studyId";
        TypedQuery<Long> query = em.createQuery(q, Long.class);
        query.setParameter("studyId", studyId);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<File> getStudyFilesPage(long studyId, int page, int pageSize) {
        Study study = em.find(Study.class, studyId);
        if(study==null || page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        String q = "SELECT f FROM Study s JOIN s.files f WHERE s.id = :studyId";
        TypedQuery<File> query = em.createQuery(q, File.class);
        query.setParameter("studyId", studyId);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int getFilteredStudiesByPatientCount(long patientId, StudyTypeEnum type) {
        Patient patient = em.find(Patient.class, patientId);
        if(patient==null) return 0;
        String q = "select count(s) from Study as s where s.patient.id = :patientId ";
        if (type != null) {
            q += "and s.type = :type ";
        }

        TypedQuery<Long> query = em.createQuery(q, Long.class);
        query.setParameter("patientId", patientId);
        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientPage(long patientId, StudyTypeEnum type, boolean mostRecent, int page, int pageSize) {
        Patient patient = em.find(Patient.class, patientId);
        if(patient==null ||page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        String q = "SELECT DISTINCT s from Study s " +
                "LEFT JOIN FETCH s.files " +
                "JOIN FETCH s.patient " +
                "JOIN FETCH s.uploader " +
                "where s.patient.id = :patientId ";
        if (type != null) {
            q += "and s.type = :type ";
        }
        q += " order by s.studyDate " + (mostRecent ? "desc" : "asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patientId", patientId);

        if (type != null) {
            query.setParameter("type", type);
        }

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int getFilteredStudiesByPatientAndDoctorCount(long patientId, long doctorId, StudyTypeEnum type) {
        Patient patient = em.find(Patient.class, patientId);
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(patient==null || doctor==null) return 0;
        String q = "SELECT count(s) from Study s " +
                "join AuthStudy a on a.study = s " +
                "where s.patient.id = :patientId " +
                "and a.doctor.id = :doctorId ";
        if (type != null) {
            q += "and s.type = :type ";
        }

        TypedQuery<Long> query = em.createQuery(q, Long.class);
        query.setParameter("patientId", patientId);
        query.setParameter("doctorId", doctorId);

        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientAndDoctorPage(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent, int page, int pageSize) {
        Patient patient = em.find(Patient.class, patientId);
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(patient==null || doctor==null ||page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        String q = "SELECT DISTINCT s from Study s " +
                "LEFT JOIN FETCH s.files " +
                "JOIN FETCH s.patient " +
                "JOIN FETCH s.uploader " +
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

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int getAuthDoctorsCount(long studyId) {
        Study study = em.find(Study.class, studyId);
        if(study==null) return 0;
        String q = "SELECT COUNT(ad) FROM Study s JOIN s.authDoctors ad WHERE s.id = :studyId";
        TypedQuery<Long> query = em.createQuery(q, Long.class);
        query.setParameter("studyId", studyId);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<Doctor> getAuthDoctorsPage(long studyId, int page, int pageSize) {
        Study study = em.find(Study.class, studyId);
        if(study==null || page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        String q = "SELECT ad FROM Study s JOIN s.authDoctors ad WHERE s.id = :studyId";
        TypedQuery<Doctor> query = em.createQuery(q, Doctor.class);
        query.setParameter("studyId", studyId);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
}
