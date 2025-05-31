package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.entities.AuthStudy;
import ar.edu.itba.paw.models.entities.AuthStudyId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
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
    public boolean authStudyForDoctor(Study study, Doctor doctor) {
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
    public boolean hasAuthStudy(Study study, Doctor doctor) {
        return em.find(AuthStudy.class, new AuthStudyId(doctor.getId(), study.getId())) != null;
    }

    @Override
    public void authStudyForDoctorIdList(List<Long> doctorIds, Study study) {

        if (doctorIds == null || doctorIds.isEmpty() || study == null)
            return;

        TypedQuery<Doctor> query = em.createQuery("from Doctor as d where d.id in :doctorIds",Doctor.class);
        query.setParameter("doctorIds", doctorIds);
        List<Doctor> doctors = query.getResultList();

        for(Doctor doctor : doctors){
            AuthStudy as = new AuthStudy(doctor, study);
            em.persist(as);
        }
    }

    @Override
    public void unauthStudyForDoctor(Study study, Doctor doctor) {
        if(!hasAuthStudy(study, doctor)) return;
        AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctor.getId(), study.getId()));
        em.remove(as);
    }

    @Override
    public void unauthAllStudiesForDoctorAndPatient(Patient patient, Doctor doctor) {
        final TypedQuery<Study> query = em.createQuery("from Study as s where s.patient = :patient",Study.class);
        query.setParameter("patient", patient);
        List<Study> studies = query.getResultList();

        for(Study study : studies){//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctor.getId(), study.getId()));
            if(as!=null) em.remove(as);
        }
        em.flush();
        em.clear();
    }
    
}
