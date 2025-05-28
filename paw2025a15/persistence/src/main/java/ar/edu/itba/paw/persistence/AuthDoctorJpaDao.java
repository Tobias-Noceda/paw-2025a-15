package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.AuthDoctorId;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import java.util.List;

@Repository
public class AuthDoctorJpaDao implements AuthDoctorDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Doctor> getAuthDoctorsByPatientId(long id) {
        return em.createQuery("SELECT ad.doctor FROM AuthDoctor ad WHERE ad.patient.id = :patientId", Doctor.class)
                .setParameter("patientId", id)
                .getResultList();
    }

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        try{
            em.createQuery("from AuthDoctor as ad where ad.id.doctorId = :doctorId and ad.id.patientId = :patientId LIMIT 1",AuthDoctor.class)
            .setParameter("doctorId", doctorId)
            .setParameter("patientId", patientId)
            .getSingleResult();
            return true;
        }
        catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        return em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevel))!=null;
    }

    @Override
    public void authDoctor(Patient patient, Doctor doctor, AccessLevelEnum accessLevel) {
        if(hasAuthDoctorWithAccessLevel(patient.getId(), doctor.getId(), accessLevel)) return;
        if(accessLevel!=AccessLevelEnum.VIEW_BASIC && !hasAuthDoctorWithAccessLevel(patient.getId(), doctor.getId(), AccessLevelEnum.VIEW_BASIC)) authDoctor(patient, doctor, AccessLevelEnum.VIEW_BASIC);
        final AuthDoctor ad = new AuthDoctor(doctor, patient, accessLevel);
        em.persist(ad);
    }

    @Override
    public int[] authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        Patient patient = em.find(Patient.class, patientId);
        if(doctor==null || patient==null) return new int[0];
        int[] results = new int[accessLevels.size()];
        for (int i = 0; i < accessLevels.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                AuthDoctor ad = new AuthDoctor(doctor, patient, accessLevels.get(i));
                em.persist(ad);
                results[i] = 1;
                if (i % 50 == 0) {
                    em.flush();
                    em.clear();
                }
            }
            catch(Exception e){
                results[i] = 0;
            }
        }
        return results;
    }

    @Override
    public void unauthDoctorAllAccessLevels(long patientId, long doctorId) {
        if(!hasAuthDoctor(patientId, doctorId)) return;
        em.createQuery("DELETE FROM AuthDoctor ad WHERE ad.patient.id = :patientId AND ad.doctor.id = :doctorId")
            .setParameter("patientId", patientId)
            .setParameter("doctorId", doctorId)
            .executeUpdate();
    }

    @Override
    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        if(accessLevel!=AccessLevelEnum.VIEW_BASIC){
            AuthDoctor ad = em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevel));
            if(ad != null) em.remove(ad);
        }
        else unauthDoctorAllAccessLevels(patientId, doctorId);
    }

    @Override
    public int[] unauthDoctorForLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        Patient patient = em.find(Patient.class, patientId);
        if(doctor==null || patient==null) return new int[0];
        int[] results = new int[accessLevels.size()];
        for (int i = 0; i < accessLevels.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                AuthDoctor ad = em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevels.get(i)));
                if(ad!=null){
                    em.remove(ad);
                    results[i] = 1;
                    if (i % 50 == 0) {
                        em.flush();
                        em.clear();
                    }
                }
                else results[i] = 0;
            }
            catch(Exception e){
                results[i] = 0;
            }
        }
        return results;
    }

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        String query = "SELECT DISTINCT ad.accessLevel FROM AuthDoctor ad " +
                        "WHERE ad.doctor.id = :doctorId AND ad.patient.id = :patientId";

        return em.createQuery(query, AccessLevelEnum.class)
                            .setParameter("doctorId", doctorId)
                            .setParameter("patientId", patientId)
                            .getResultList();
    }
    
}
