package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.models.AuthStudy;
import ar.edu.itba.paw.models.AuthStudyId;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.User;
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
    public boolean authStudyForDoctorId(Study study, User doctor) {
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
        return em.find(AuthStudy.class, new AuthStudyId(doctorId, studyId))!=null;
    }

    @Override
    public void unauthStudyForDoctorId(long studyId, long doctorId) {
        AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctorId, studyId));
        em.remove(as);
    }

    @Override
    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId) {
        final TypedQuery<Study> query = em.createQuery("from Study as s where s.user.id = :userId",Study.class);
        query.setParameter("userId", userId);
        List<Study> studies = query.getResultList();

        for(Study study : studies){//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            AuthStudy as = em.find(AuthStudy.class, new AuthStudyId(doctorId, study.getId()));
            if(as!=null) em.remove(as);
        }
        em.flush();
        em.clear();
    }
    
}
