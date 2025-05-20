package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.User;
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
    public Study create(StudyTypeEnum type, String comment, File file, User user, User uploader, LocalDate studyDate) {
        final Study study = new Study(type, comment, file, user, uploader, LocalDateTime.now(), studyDate);
        em.persist(study);
        return study;
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, File file, User user, User uploader) {
        final Study study = new Study(type, comment, file, user, uploader, LocalDateTime.now(), LocalDate.now());
        em.persist(study);
        return study;
    }

    @Override
    public Optional<Study> findStudyById(long id) {
        return Optional.ofNullable(em.find(Study.class, id));
    }

        @Override
        public List<Study> getStudiesByPatientId(long id) {
            final TypedQuery<Study> query = em.createQuery("from Study as s where s.user.id = :id",Study.class);
            query.setParameter("id", id);
            return query.getResultList();
        }
    @Override
    public List<Study> getFilteredStudiesByPatientId(long id, StudyTypeEnum type, boolean mostRecent) {
        String q = "from Study as s where s.user.id = :id and s.file.type = :type "
                + (mostRecent ? "order by s.date desc" : "order by s.date asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("id", id);
        query.setParameter("type", type);

        return query.getResultList();
    }


    @Override
    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId) {
        TypedQuery<Study> query = em.createQuery("from Study as s where s.user.id = :patientId and s.uploader.id = :doctorId",Study.class);
        query.setParameter("patientId", patientId);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent) {
        String q = "from Study as s where s.user.id = :patientId and s.uploader.id = :doctorId and s.file.type = :type "
                + (mostRecent ? "order by s.date desc" : "order by s.date asc");

        TypedQuery<Study> query = em.createQuery(q, Study.class);
        query.setParameter("patientId", patientId);
        query.setParameter("doctorId", doctorId);
        query.setParameter("type", type);

        return query.getResultList();
    }


}
