package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
        User patient = em.find(User.class, id);
        if(patient == null) return Collections.emptyList();
        return patient.getPatientStudies();
    }

    @Override
    public List<Study> getFilteredStudiesByPatientId(long id, StudyTypeEnum type, boolean mostRecent) {//TODO: le faltan cosas de la transicion, chequear en la version jdbc (creo q es solo esta funcion de ste doc)
        User patient = em.find(User.class, id);
    if (patient == null) return Collections.emptyList();

    return patient.getPatientStudies().stream()
        .filter(study -> type == null || study.getType() == type)
        .sorted(Comparator.comparing(Study::getStudyDate, mostRecent ? Comparator.reverseOrder() : Comparator.naturalOrder())) // Sort by study date
        .collect(Collectors.toList());
    }

    @Override
    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId) {
        User patient = em.find(User.class, patientId);
        User doctor = em.find(User.class, doctorId);
        if(patient == null || doctor == null) return Collections.emptyList();
        Set<Study> doctorStudySet = new HashSet<>(doctor.getAuthStudies());
        return patient.getPatientStudies().stream()
        .filter(doctorStudySet::contains)
        .collect(Collectors.toList());

    }

    @Override
    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent) {
        User patient = em.find(User.class, patientId);
        User doctor = em.find(User.class, doctorId);
        if (patient == null || doctor == null) return Collections.emptyList();

        Set<Study> patientStudies = new HashSet<>(patient.getPatientStudies());
        Set<Study> doctorStudies = new HashSet<>(doctor.getAuthStudies());

        List<Study> filteredStudies = patientStudies.stream()
            .filter(doctorStudies::contains) 
            .filter(study -> type == null || study.getType() == type)
            .sorted(Comparator.comparing(Study::getStudyDate, mostRecent ? Comparator.reverseOrder() : Comparator.naturalOrder())) // Sort by study date
            .collect(Collectors.toList());

        return filteredStudies;
    }


}
