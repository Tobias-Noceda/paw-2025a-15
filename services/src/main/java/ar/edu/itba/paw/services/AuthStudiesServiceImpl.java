package ar.edu.itba.paw.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class AuthStudiesServiceImpl implements AuthStudiesService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthStudiesServiceImpl.class);

    @Autowired
    private AuthStudiesDao authStudiesDao;

    @Autowired
    private StudyService ss;

    @Autowired
    private PatientService ps;

    @Autowired
    private DoctorService ds;

    @Transactional
    @Override
    public boolean authStudyForDoctorId(long studyId, long doctorId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        ds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("User with id: " + doctorId + " does not exist!"));

        if(hasAuthStudy(studyId, doctorId)) return true;
        boolean result = authStudiesDao.authStudyForDoctor(studyId, doctorId);
        if(result) LOGGER.info("Given authorization of study with id:{} to doctor: {}", studyId, doctorId);
        else LOGGER.error("Failed to give authorization of study with id:{} to doctor: {}", studyId, doctorId);
        return result;
    }

    @Transactional
    @Override
    public void authStudyForDoctorIdList(List<Long> doctorsId, long studyId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        authStudiesDao.authStudyForDoctorIdList(doctorsId, studyId);
    }

    @Transactional
    @Override
    public void unauthStudyForDoctorIdList(List<Long> doctorsId, long studyId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        authStudiesDao.unauthStudyForDoctorIdList(doctorsId, studyId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        ds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

        return authStudiesDao.hasAuthStudy(studyId, doctorId);
    }

    @Transactional
    @Override
    public void unauthAllStudiesForDoctorIdAndPatientId(long patientId, long doctorId) {
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        authStudiesDao.unauthAllStudiesForDoctorAndPatient(patientId, doctorId);
        LOGGER.info("Removed all authorizations of all studies of patient with id: {} for doctor: {}", patientId, doctorId);
    }
    
    @Transactional
    @Override
    public void authorizeAllDoctorsForStudy(long studyId) {
        ss.getStudyById(studyId).orElseThrow(() -> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        authStudiesDao.authStudyForAllAuthDoctors(studyId);
        LOGGER.info("Authorized all patient authdoctors for study with id: {}", studyId);
    }
    
    @Transactional
    @Override
    public void deauthorizeAllDoctorsForStudy(long studyId) {
        ss.getStudyById(studyId).orElseThrow(() -> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        authStudiesDao.deauthStudyForAllDoctors(studyId);
        LOGGER.info("Deauthorized all doctors for study with id: {}", studyId);
    }

    @Transactional
    @Override
    public void unauthAllStudiesForAllDocsForPatientId(long patientId) {
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        authStudiesDao.unauthAllStudiesForAllDocsForPatientId(patientId);
        LOGGER.info("Deauthorized all doctors for patient with id: {}", patientId);
    }

}
