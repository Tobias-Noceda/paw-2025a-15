package ar.edu.itba.paw.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

import java.util.List;

@Service
public class AuthStudiesServiceImpl implements AuthStudiesService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthStudiesServiceImpl.class);

    @Autowired
    private AuthStudiesDao authStudiesDao;

    @Autowired
    private StudyService ss;

    @Autowired
    private UserService us;

    @Transactional
    @Override
    public boolean authStudyForDoctorId(long studyId, long doctorId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        if(hasAuthStudy(studyId, doctorId)) return true;
        boolean result = authStudiesDao.authStudyForDoctorId(studyId, doctorId);
        if(result) LOGGER.info("Given authorization of study with id:{} to doctor: {}", studyId, doctorId);
        else LOGGER.error("Failed to give authorization of study with id:{} to doctor: {}", studyId, doctorId);
        return result;
    }

    @Transactional
    @Override
    public void authStudyForDoctorIdList(List<Long> doctorsId, long StudyId) {
        authStudiesDao.authStudyForDoctorIdList(doctorsId, StudyId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        return authStudiesDao.hasAuthStudy(studyId, doctorId);
    }

    @Transactional
    @Override
    public void unauthStudyForDoctorId(long studyId, long doctorId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        if(!hasAuthStudy(studyId, doctorId)) return;
        authStudiesDao.unauthStudyForDoctorId(studyId, doctorId);
        LOGGER.info("Removed authorization of study with id:{} for doctor: {}", studyId, doctorId);
    }

    @Transactional
    @Override
    public void toggleStudyForDoctorId(long studyId, long doctorId) {
        ss.getStudyById(studyId).orElseThrow(()-> new NotFoundException("Study with id: " + studyId + " does not exist!"));
        us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        if(hasAuthStudy(studyId, doctorId)) {
            unauthStudyForDoctorId(studyId, doctorId);
        }else{
            authStudyForDoctorId(studyId, doctorId);
        }
    }

    @Transactional
    @Override
    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId){
        us.getPatientById(userId).orElseThrow(()-> new NotFoundException("Patient with id: " + userId + " does not exist!"));
        us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        authStudiesDao.unauthAllStudiesForDoctorIdAndPatientId(userId, doctorId);
        LOGGER.info("Removed all authorizations of all studies of userId:{} for doctor: {}", userId, doctorId);
    }

}
