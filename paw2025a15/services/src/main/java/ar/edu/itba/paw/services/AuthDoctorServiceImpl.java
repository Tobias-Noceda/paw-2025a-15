package ar.edu.itba.paw.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;

@Service
public class AuthDoctorServiceImpl implements AuthDoctorService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthDoctorServiceImpl.class);

    @Autowired
    private AuthDoctorDao authDoctorDao;

    @Transactional(readOnly = true)
    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        return authDoctorDao.getAuthDoctorsByPatientId(id);
    }

    @Transactional
    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
        if(hasAuthDoctor(patientId, doctorId)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
        }
        else{
            authDoctorDao.authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
        }
    }

    private void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        for (AccessLevelEnum accessLevel: accessLevels) {
            authDoctorDao.authDoctor(patientId, doctorId, accessLevel);
        }
    }

    private void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        //TODO:check after refactor with us the existance of patientId and docId
        if(accessLevels==null || accessLevels.isEmpty()) return;
        if(accessLevels.contains(AccessLevelEnum.VIEW_BASIC)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            return;
        }
        for (AccessLevelEnum accessLevel: accessLevels) {
            authDoctorDao.unauthDoctorByAccessLevel(patientId, doctorId, accessLevel);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        return authDoctorDao.hasAuthDoctor(patientId, doctorId);
    }

    @Transactional
    @Override
    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        //TODO:check after refactor with us the existance of patientId and docId
        List<AccessLevelEnum> toRemove;
        if(accessLevels==null || accessLevels.isEmpty()){
            toRemove = getAuthAccessLevelEnums(patientId, doctorId);
            toRemove.remove(AccessLevelEnum.VIEW_BASIC);
        } else {
            toRemove = new ArrayList<>();
            for (AccessLevelEnum currentAccessLevel : getAuthAccessLevelEnums(patientId, doctorId)) {
                if(currentAccessLevel!=AccessLevelEnum.VIEW_BASIC && !accessLevels.contains(currentAccessLevel)) toRemove.add(currentAccessLevel);
            }
        }
        unauthDoctorWithLevels(patientId, doctorId, toRemove);
        authDoctorWithLevels(patientId, doctorId, accessLevels);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        return authDoctorDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
}
