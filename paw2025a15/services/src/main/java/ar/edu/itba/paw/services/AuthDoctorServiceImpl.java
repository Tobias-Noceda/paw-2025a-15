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
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class AuthDoctorServiceImpl implements AuthDoctorService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthDoctorServiceImpl.class);

    @Autowired
    private AuthDoctorDao authDoctorDao;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Transactional(readOnly = true)
    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        return authDoctorDao.getAuthDoctorsByPatientId(id);
    }

    @Transactional
    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
        if(pds.getDetailByPatientId(patientId).isEmpty()) throw new NotFoundException("Patient with id: " + patientId + " does not exist!");
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
        if(hasAuthDoctor(patientId, doctorId)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            LOGGER.info("Removing authorization of doctor with id: {} for patient with id: {}", doctorId, patientId);
        }
        else{
            authDoctorDao.authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
            LOGGER.info("Giving basic authorization for doctor with id: {} of patient with id: {}", doctorId, patientId);
        }
    }

    private void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        for (AccessLevelEnum accessLevel: accessLevels) {//TODO: batch insert
            authDoctorDao.authDoctor(patientId, doctorId, accessLevel);
            LOGGER.info("Giving authorization for doctor with id: {} of patient with id: {} with level:", doctorId, patientId, accessLevel);
        }
    }

    private void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        if(accessLevels.contains(AccessLevelEnum.VIEW_BASIC)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            LOGGER.info("Removing authorization of doctor with id: {} for patient with id: {}", doctorId, patientId);
            return;
        }
        for (AccessLevelEnum accessLevel: accessLevels) {//TODO:batch en sql
            authDoctorDao.unauthDoctorByAccessLevel(patientId, doctorId, accessLevel);
            LOGGER.info("Removing authorization for doctor with id: {} of patient with id: {} of level:", doctorId, patientId, accessLevel);
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
        if(pds.getDetailByPatientId(patientId).isEmpty()) throw new NotFoundException("Patient with id: " + patientId + " does not exist!");
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
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
        LOGGER.info("Updated authorizations of doctor with id: {} for patient with id: {}", doctorId, patientId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        return authDoctorDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
}
