package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.UnauthorizedException;

@Service
public class StudyServiceImpl implements StudyService{

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceImpl.class);

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private AuthDoctorService ads;

    @Autowired
    private AuthStudiesService ass;

    @Autowired
    private EmailService es;

    @Autowired
    private FileService fs;

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, File file, long userId, long uploaderId, LocalDate studyDate) {
        if(fs.findById(file.getId()).isEmpty()) throw new NotFoundException("File not found with ID: " + file.getId());
        Study study = null;
        Doctor doctor = null;
        Patient patient = pds.getPatientById(userId).orElseThrow(() -> new NotFoundException("Patient with id: " + userId + " does not exist!"));
        if(userId==uploaderId){
            if(studyDate == null) study = studyDao.create(type, comment, file, patient, patient);
            else study = studyDao.create(type, comment, file, patient, patient, studyDate);
        }
        else{
            doctor = dds.getDoctorById(uploaderId).orElseThrow(() -> new NotFoundException("Doctor with id: " + uploaderId + " does not exist!"));
            if(!ads.hasAuthDoctor(userId, uploaderId)) throw new UnauthorizedException("Doctor with id: " + uploaderId + " isnt able to upload!");
            if(studyDate == null) study = studyDao.create(type, comment, file, patient, doctor);
            else study = studyDao.create(type, comment, file, patient, doctor, studyDate);   
        }
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} and fileId: {} at {}", userId, uploaderId, file.getId(), LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + " and fileId: " + file.getId() );
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and fileId: {}", userId, uploaderId, file.getId());
        if(userId!=uploaderId && doctor!=null) {
            es.sendRecievedStudyEmail(patient, doctor, file, study, comment);
            ass.authStudyForDoctorId(study.getId(), uploaderId);
        }
        return study;
    }

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, File file, long userId, long uploaderId) {
        if(fs.findById(file.getId()).isEmpty()) throw new NotFoundException("File not found with ID: " + file.getId());
        Study study = null;
        Doctor doctor = null;
        Patient patient = pds.getPatientById(userId).orElseThrow(() -> new NotFoundException("Patient with id: " + userId + " does not exist!"));
        if(userId==uploaderId){
            study = studyDao.create(type, comment, file, patient, patient);
        }
        else{
            doctor = dds.getDoctorById(uploaderId).orElseThrow(() -> new NotFoundException("Doctor with id: " + uploaderId + " does not exist!"));
            if(!ads.hasAuthDoctor(userId, uploaderId)) throw new UnauthorizedException("Doctor with id: " + uploaderId + " isnt able to upload!");
            study = studyDao.create(type, comment, file, patient, doctor);  
        }
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} and fileId: {} at {}", userId, uploaderId, file.getId(), LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + " and fileId: " + file.getId() );
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and fileId: {}", userId, uploaderId, file.getId());
        if(userId!=uploaderId && doctor!=null) {
            es.sendRecievedStudyEmail(patient, doctor, file, study, comment);
            ass.authStudyForDoctorId(study.getId(), uploaderId);
        }
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Study> getStudyById(long id) {
        return studyDao.findStudyById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Study> getFilteredStudies(long patientId, StudyTypeEnum type, boolean mostRecent) {
        pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        return studyDao.getFilteredStudiesByPatient(patientId, type, mostRecent);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent) {
        pds.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        dds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

        return studyDao.getFilteredStudiesByPatientAndDoctor(patientId, doctorId, type, mostRecent);
    }

}
