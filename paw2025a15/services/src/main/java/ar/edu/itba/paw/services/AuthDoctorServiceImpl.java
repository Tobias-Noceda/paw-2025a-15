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
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class AuthDoctorServiceImpl implements AuthDoctorService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthDoctorServiceImpl.class);

    @Autowired
    private AuthDoctorDao authDoctorDao;

    @Autowired
    private AuthStudiesService ass;

    @Autowired
    private DoctorService ds;

    @Autowired
    private PatientService ps;

    @Transactional
    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
        ps.getPatientById(patientId).orElseThrow(()-> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        ds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
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
        ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        authDoctorDao.authDoctorWithLevels(patientId, doctorId, accessLevels);
        LOGGER.info("Giving authorizations for doctor with id: {} of patient with id: {}", doctorId, patientId);
    }

    private void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels==null || accessLevels.isEmpty()) return;
        ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        if(accessLevels.contains(AccessLevelEnum.VIEW_BASIC)){
            authDoctorDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            LOGGER.info("Removing all specific authorizations for doctor with id: {} for patient with id: {}", doctorId, patientId);
            return;
        }
        authDoctorDao.unauthDoctorForLevels(patientId, doctorId, accessLevels);
        LOGGER.info("Removing authorizations for doctor with id: {} of patient with id: {}", doctorId, patientId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return authDoctorDao.hasAuthDoctor(patientId, doctorId);
    }

    @Transactional
    @Override
    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        ps.getPatientById(patientId).orElseThrow(()-> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        ds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        List<AccessLevelEnum> toRemove;
        List<AccessLevelEnum> toAdd = accessLevels == null ? new ArrayList<>() : new ArrayList<>(accessLevels);
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
        ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return authDoctorDao.getAuthAccessLevelEnums(patientId, doctorId);
    }   
    
    @Transactional
    @Override
    public void deauthorizeAllDoctors(long patientId) {
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        authDoctorDao.deauthorizeAllDoctors(patientId);
        ass.unauthAllStudiesForAllDocsForPatientId(patientId);
        LOGGER.info("Deauthorized all doctors for patient with id: {}", patientId);
    }
}
