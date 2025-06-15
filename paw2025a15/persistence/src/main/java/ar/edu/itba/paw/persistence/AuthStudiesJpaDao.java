package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.entities.AuthStudy;
import ar.edu.itba.paw.models.entities.AuthStudyId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Study;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;

@Repository
public class AuthStudiesJpaDao implements AuthStudiesDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean authStudyForDoctor(long studyId, long doctorId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        Study study = em.find(Study.class, studyId);
        if(study == null || doctor == null) return false;
        try{
            final AuthStudy as = new AuthStudy(doctor, study);
            em.persist(as);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        return em.find(AuthStudy.class, new AuthStudyId(doctorId, studyId)) != null;
    }

    @Override
    public void authStudyForDoctorIdList(List<Long> doctorIds, long studyId) {
        if (doctorIds == null || doctorIds.isEmpty()) return;
        Study study = em.find(Study.class, studyId);
        TypedQuery<Doctor> query = em.createQuery("from Doctor as d where d.id in :doctorIds",Doctor.class);
        query.setParameter("doctorIds", doctorIds);
        List<Doctor> doctors = query.getResultList();

        for(Doctor doctor : doctors){
            AuthStudy as = new AuthStudy(doctor, study);
            em.persist(as);
        }
    }

    @Override
    public void unauthStudyForDoctor(long studyId, long doctorId) {
        if(!hasAuthStudy(studyId, doctorId)) return;
        AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctorId, studyId));
        em.remove(as);
    }

    @Override
    public void unauthAllStudiesForDoctorAndPatient(long patientId, long doctorId) {
        final TypedQuery<Study> query = em.createQuery("from Study as s where s.patient.id = :patientId",Study.class);
        query.setParameter("patientId", patientId);
        List<Study> studies = query.getResultList();

        for(Study study : studies){
            AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctorId, study.getId()));
            if(as!=null) em.remove(as);
        }
        em.flush();
        em.clear();
    }
    
}
