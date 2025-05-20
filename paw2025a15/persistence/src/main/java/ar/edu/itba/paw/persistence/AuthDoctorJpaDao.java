package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.models.AuthDoctor;
import ar.edu.itba.paw.models.AuthDoctorId;
import ar.edu.itba.paw.models.AuthStudy;
import ar.edu.itba.paw.models.AuthStudyId;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AuthDoctorJpaDao implements AuthDoctorDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthDoctorsByPatientId'");
    }

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hasAuthDoctor'");
    }

    @Override
    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        return em.find(AuthDoctor.class, new AuthDoctorId(doctorId, patientId, accessLevel))!=null;
    }

    @Override
    public void authDoctor(User patient, User doctor, AccessLevelEnum accessLevel) {
        final AuthDoctor ad = new AuthDoctor(doctor, patient, accessLevel);
        em.persist(ad);
    }

    @Override
    public int[] authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'authDoctorWithLevels'");
    }

    @Override
    public void unauthDoctorAllAccessLevels(long patientId, long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unauthDoctorAllAccessLevels'");
    }

    @Override
    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unauthDoctorByAccessLevel'");
    }

    @Override
    public int[] unauthDoctorForLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unauthDoctorForLevels'");
    }

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAuthAccessLevelEnums'");
    }
    
}
