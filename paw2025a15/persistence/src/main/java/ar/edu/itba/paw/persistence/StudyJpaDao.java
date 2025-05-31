package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public List<Study> getFilteredStudiesByPatient(Patient patient, StudyTypeEnum type, boolean mostRecent) {//TODO: le faltan cosas de la transicion, chequear en la version jdbc (creo q es solo esta funcion de ste doc)
        String q = "from Study as s where s.patient = :patient "
                + (type != null ? "and s.type = :type " : "")
                + (mostRecent ? "order by s.studyDate desc" : "order by s.studyDate asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patient", patient);
        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getResultList();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientAndDoctor(Patient patient, Doctor doctor, StudyTypeEnum type, boolean mostRecent) {
        String q = "SELECT s from Study s " +
                "join AuthStudy a on a.study = s " +
                "where s.patient = :patient " +
                "and a.doctor = :doctor ";
        if (type != null) {
            q += "and s.type = :type ";
        }
        q += (mostRecent ? "order by s.studyDate desc" : "order by s.studyDate asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patient", patient);
        query.setParameter("doctor", doctor);

        if (type != null) {
            query.setParameter("type", type);
        }

        return query.getResultList();
    }
}
