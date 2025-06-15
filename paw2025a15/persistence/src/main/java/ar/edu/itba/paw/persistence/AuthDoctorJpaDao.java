package ar.edu.itba.paw.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.models.entities.AuthDoctor;
import ar.edu.itba.paw.models.entities.AuthDoctorId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;

@Repository
public class AuthDoctorJpaDao implements AuthDoctorDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        try{
            em.createQuery("from AuthDoctor as ad where ad.doctor.id = :doctorId and ad.patient.id = :patientId",AuthDoctor.class)
            .setParameter("doctorId", doctorId)
            .setParameter("patientId", patientId)
            .setMaxResults(1)
            .getSingleResult();
            return true;
        }
        catch (NoResultException e) {
            return false;
        }
    }

    @Override
    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        return em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevel)) != null;
    }

    @Override
    public void authDoctor(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        Patient patient = em.find(Patient.class, patientId);
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(patient==null || doctor==null) return;
        if(hasAuthDoctorWithAccessLevel(patientId, doctorId, accessLevel)) return;
        if(accessLevel != AccessLevelEnum.VIEW_BASIC && !hasAuthDoctorWithAccessLevel(patientId, doctorId, AccessLevelEnum.VIEW_BASIC)) authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
        final AuthDoctor ad = new AuthDoctor(doctor, patient, accessLevel);
        em.persist(ad);
    }

    @Override
    public void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        Patient patient = em.find(Patient.class, patientId);
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null || patient==null) return;
        for (int i = 0; i < accessLevels.size(); i++) {
            AuthDoctor ad = new AuthDoctor(doctor, patient, accessLevels.get(i));
            em.persist(ad);
            if (i!=0 && i % 50 == 0) {
                em.flush();
                em.clear();
                doctor = em.find(Doctor.class, doctorId);
                patient = em.find(Patient.class, patientId);
            }
        }
        return;
    }

    @Override
    public void unauthDoctorAllAccessLevels(long patientId, long doctorId) {
        em.createQuery("DELETE FROM AuthDoctor ad WHERE ad.patient.id = :patientId AND ad.doctor.id = :doctorId")
            .setParameter("patientId", patientId)
            .setParameter("doctorId", doctorId)
            .executeUpdate();
    }

    @Override
    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        if(accessLevel.equals(AccessLevelEnum.VIEW_BASIC)) {
            unauthDoctorAllAccessLevels(patientId, doctorId);
            return;
        }
        AuthDoctor ad = em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevel));
        if(ad != null) em.remove(ad);
    }

    @Override
    public void unauthDoctorForLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        Patient patient = em.find(Patient.class, patientId);
        if(doctor==null || patient==null) return;
        for (int i = 0; i < accessLevels.size(); i++) {
            AuthDoctor ad = em.find(AuthDoctor.class, new AuthDoctorId(doctor.getId(), patient.getId(), accessLevels.get(i)));
            if(ad!=null){
                em.remove(ad);
                if (i % 50 == 0) {
                    em.flush();
                    em.clear();
                }
            }
        }
        return;
    }

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        String query = "SELECT DISTINCT ad.id.accessLevel FROM AuthDoctor ad " +
                        "WHERE ad.doctor.id = :doctorId AND ad.patient.id = :patientId";

        return em.createQuery(query, AccessLevelEnum.class)
                            .setParameter("doctorId", doctorId)
                            .setParameter("patientId", patientId)
                            .getResultList();
    }
}
