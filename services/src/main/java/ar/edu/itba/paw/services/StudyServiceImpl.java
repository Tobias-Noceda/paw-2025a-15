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
import ar.edu.itba.paw.interfaces.services.AuthDoctorService;
import ar.edu.itba.paw.interfaces.services.AuthStudiesService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientService;
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
    private PatientService ps;

    @Autowired
    private DoctorService ds;

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
    public Study create(StudyTypeEnum type, String comment, List<File> files, long userId, long uploaderId, LocalDate studyDate) {
        checkAllFilesExist(files);
        Study study = null;
        Doctor doctor = null;
        Patient patient = ps.getPatientById(userId).orElseThrow(() -> new NotFoundException("Patient with id: " + userId + " does not exist!"));
        if(userId==uploaderId){
            if(studyDate == null) study = studyDao.create(type, comment, files, patient, patient);
            else study = studyDao.create(type, comment, files, patient, patient, studyDate);
        }
        else{
            doctor = ds.getDoctorById(uploaderId).orElseThrow(() -> new NotFoundException("Doctor with id: " + uploaderId + " does not exist!"));
            if(!ads.hasAuthDoctor(userId, uploaderId)) throw new UnauthorizedException("Doctor with id: " + uploaderId + " isnt able to upload!");
            if(studyDate == null) study = studyDao.create(type, comment, files, patient, doctor);
            else study = studyDao.create(type, comment, files, patient, doctor, studyDate);   
        }
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} at {}", userId, uploaderId, LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + ".");
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and studyId: {}", userId, uploaderId, study.getId());
        if(userId!=uploaderId && doctor!=null) {
            es.sendRecievedStudyEmail(patient, doctor, files, study, comment);
            ass.authStudyForDoctorId(study.getId(), uploaderId);
        }
        return study;
    }

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, List<File> files, long userId, long uploaderId) {
        checkAllFilesExist(files);
        Study study = null;
        Doctor doctor = null;
        Patient patient = ps.getPatientById(userId).orElseThrow(() -> new NotFoundException("Patient with id: " + userId + " does not exist!"));
        if(userId==uploaderId){
            study = studyDao.create(type, comment, files, patient, patient);
        }
        else{
            doctor = ds.getDoctorById(uploaderId).orElseThrow(() -> new NotFoundException("Doctor with id: " + uploaderId + " does not exist!"));
            if(!ads.hasAuthDoctor(userId, uploaderId)) throw new UnauthorizedException("Doctor with id: " + uploaderId + " isnt able to upload!");
            study = studyDao.create(type, comment, files, patient, doctor);  
        }
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} at {}", userId, uploaderId, LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + ".");
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and studyId: {}", userId, uploaderId, study.getId());
        if(userId!=uploaderId && doctor!=null) {
            es.sendRecievedStudyEmail(patient, doctor, files, study, comment);
            ass.authStudyForDoctorId(study.getId(), uploaderId);
        }
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Study> getStudyById(long id) {
        return studyDao.findStudyById(id);
    }

    @Transactional
    @Override
    public boolean deleteStudy(long id) {
        return studyDao.deleteStudy(id);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isFileInStudy(long studyId, long fileId) {
        return studyDao.isFileInStudy(studyId, fileId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getFilteredStudiesCount(long patientId, Long doctorId, StudyTypeEnum type){
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        if(doctorId != null){
            ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

            return studyDao.getFilteredStudiesByPatientAndDoctorCount(patientId, doctorId, type);
        }
        else {
            return studyDao.getFilteredStudiesByPatientCount(patientId, type);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Study> getFilteredStudiesPage(long patientId, Long doctorId, StudyTypeEnum type, boolean mostRecent, int page, int pageSize){
        ps.getPatientById(patientId).orElseThrow(() -> new NotFoundException("Patient with id: " + patientId + " does not exist!"));
        if(doctorId != null){
            ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

            return studyDao.getFilteredStudiesByPatientAndDoctorPage(patientId, doctorId, type, mostRecent, page, pageSize);
        }
        else {
            return studyDao.getFilteredStudiesByPatientPage(patientId, type, mostRecent, page, pageSize);
        }
    }

    private void checkAllFilesExist(List<File> files) {
        for(File file : files) {
            fs.findById(file.getId()).orElseThrow(() -> new NotFoundException("File not found with ID: " + file.getId()));
        }
    }
}
