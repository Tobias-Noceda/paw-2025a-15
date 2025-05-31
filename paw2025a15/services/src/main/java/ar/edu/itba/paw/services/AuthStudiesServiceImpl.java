package ar.edu.itba.paw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

import java.util.List;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;

@Service
public class AuthStudiesServiceImpl implements AuthStudiesService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthStudiesServiceImpl.class);

    @Autowired
    private AuthStudiesDao authStudiesDao;

    @Autowired
    private StudyService ss;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Transactional
    @Override
    public boolean authStudyForDoctorId(long studyId, long doctorId) {
        Study study = ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));//TODO:check change for hibernate
        Doctor doctor = dds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("User with id: " + doctorId + " does not exist!"));//TODO:check change for hibernate

        if(hasAuthStudy(studyId, doctorId)) return true;
        boolean result = authStudiesDao.authStudyForDoctor(study, doctor);
        if(result) LOGGER.info("Given authorization of study with id:{} to doctor: {}", studyId, doctorId);
        else LOGGER.error("Failed to give authorization of study with id:{} to doctor: {}", studyId, doctorId);
        return result;
    }

    @Transactional
    @Override
    public void authStudyForDoctorIdList(List<Long> doctorsId, long studyId) {
        Study study = ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        authStudiesDao.authStudyForDoctorIdList(doctorsId, study);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        Study study = ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        Doctor doctor = dds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

        return authStudiesDao.hasAuthStudy(study, doctor);
    }

    @Transactional
    @Override
    public void unauthStudyForDoctorId(long studyId, long doctorId) {
        if(!hasAuthStudy(studyId, doctorId)) return;
        Study study = ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        Doctor doctor = dds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        authStudiesDao.unauthStudyForDoctor(study, doctor);
        LOGGER.info("Removed authorization of study with id:{} for doctor: {}", studyId, doctorId);
    }

    @Transactional
    @Override
    public void toggleStudyForDoctorId(long studyId, long doctorId) {
        if(hasAuthStudy(studyId, doctorId)) {
            unauthStudyForDoctorId(studyId, doctorId);
        }else{
            authStudyForDoctorId(studyId, doctorId);
        }
    }

    @Transactional
    @Override
    public void unauthAllStudiesForDoctorIdAndPatientId(long patientId, long doctorId) {
        Patient patient = pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        Doctor doctor = dds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        authStudiesDao.unauthAllStudiesForDoctorAndPatient(patient, doctor);
        LOGGER.info("Removed all authorizations of all studies of patient with id: {} for doctor: {}", patientId, doctorId);
    }

}
