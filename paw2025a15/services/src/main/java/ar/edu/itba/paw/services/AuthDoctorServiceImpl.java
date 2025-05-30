package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AuthDoctorDao;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
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

    @Autowired
    private UserService us;

    @Autowired
    private AuthStudiesService ass;

    @Transactional(readOnly = true)
    @Override
    public List<Doctor> getAuthDoctorsByPatientId(long id) {
        return authDoctorDao.getAuthDoctorsByPatientId(id);
    }

    @Transactional
    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
        Patient patient = (Patient) us.getUserById(patientId).orElseThrow(()-> new NotFoundException("User with id: " + patientId + " does not exist!"));//TODO:check change for hibernate
        Doctor doctor = (Doctor) us.getUserById(doctorId).orElseThrow(()-> new NotFoundException("User with id: " + doctorId + " does not exist!"));//TODO:check change for hibernate
        if(pds.getDetailByPatientId(patientId).isEmpty()) throw new NotFoundException("Patient with id: " + patientId + " does not exist!");
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
        if(hasAuthDoctor(patientId, doctorId)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            ass.unauthAllStudiesForDoctorIdAndPatientId(patientId, doctorId);
            LOGGER.info("Removing authorization of doctor with id: {} for patient with id: {}", doctorId, patientId);
        }
        else{
            authDoctorDao.authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
            LOGGER.info("Giving basic authorization for doctor with id: {} of patient with id: {}", doctorId, patientId);
        }
    }

    private void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        int[] results = authDoctorDao.authDoctorWithLevels(patientId, doctorId, accessLevels);
        for (int i = 0; i < results.length; i++) {
            if (results[i] > 0) {
                LOGGER.info("Giving authorization for doctor with id: {} of patient with id: {} with level:{}", doctorId, patientId, accessLevels.get(i).name());
            } else {
                LOGGER.error("Failed to authorize doctor with id: {} of patient with id: {} with level:{} at {}", doctorId, patientId, accessLevels.get(i).name(), LocalDateTime.now());
                throw new RuntimeException("Failed to authorize doctor with id: " + doctorId + " of patient with id: " + patientId + " with level:" + accessLevels.get(i).name());
            }
        }
    }

    private void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        if(accessLevels.contains(AccessLevelEnum.VIEW_BASIC)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            LOGGER.info("Removing all specific authorizations for doctor with id: {} for patient with id: {}", doctorId, patientId);
            return;
        }
        int[] results = authDoctorDao.unauthDoctorForLevels(patientId, doctorId, accessLevels);
        for (int i = 0; i < results.length; i++) {
            if (results[i] > 0) {
                LOGGER.info("Removing authorization for doctor with id: {} of patient with id: {} with level:{}", doctorId, patientId, accessLevels.get(i).name());
            } else {
                LOGGER.error("Failed to remove authorization of doctor with id: {} of patient with id: {} with level:{} at {}", doctorId, patientId, accessLevels.get(i).name(), LocalDateTime.now());
                throw new RuntimeException("Failed to remove authorization of doctor with id: " + doctorId + " of patient with id: " + patientId + " with level:" + accessLevels.get(i).name());
            }
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
        List<AccessLevelEnum> toAdd = new ArrayList<>(accessLevels);
        if(accessLevels==null || accessLevels.isEmpty()){
            toRemove = getAuthAccessLevelEnums(patientId, doctorId);
            toRemove.remove(AccessLevelEnum.VIEW_BASIC);
        } else {
            toRemove = new ArrayList<>();
            for (AccessLevelEnum currentAccessLevel : getAuthAccessLevelEnums(patientId, doctorId)) {
                if(currentAccessLevel!=AccessLevelEnum.VIEW_BASIC && !accessLevels.contains(currentAccessLevel))
                    toRemove.add(currentAccessLevel);
                else
                    toAdd.remove(currentAccessLevel);
            }
        }
        unauthDoctorWithLevels(patientId, doctorId, toRemove);
        authDoctorWithLevels(patientId, doctorId, toAdd);
        LOGGER.info("Updated authorizations of doctor with id: {} for patient with id: {}", doctorId, patientId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        return authDoctorDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
}
