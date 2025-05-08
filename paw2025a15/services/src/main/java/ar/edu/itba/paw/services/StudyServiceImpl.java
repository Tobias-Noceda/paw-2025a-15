package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.PatientDetailService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

@Service
public class StudyServiceImpl implements StudyService{

    private static final Logger LOGGER = LoggerFactory.getLogger(StudyServiceImpl.class);

    @Autowired
    private StudyDao studyDao;

    @Autowired
    private FileService fs;

    @Autowired
    private PatientDetailService pds;

    @Autowired
    private DoctorDetailService dds;

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate) {
        if(pds.getDetailByPatientId(userId).isEmpty()) throw new IllegalArgumentException("Patient with id: " + userId + " does not exist!");
        if(userId!=uploaderId && dds.getDetailByDoctorId(uploaderId).isEmpty()) throw new IllegalArgumentException("Uploader with id: " + uploaderId + " does not exist or isnt able to upload!");
        if(fs.findById(fileId).isEmpty()) throw new IllegalArgumentException("File not found with ID: " + fileId);
        Study study;
        if(studyDate == null) study = studyDao.create(type, comment, fileId, userId, uploaderId);
        else study = studyDao.create(type, comment, fileId, userId, uploaderId, studyDate);   
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} and fileId: {} at {}", userId, uploaderId, fileId, LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + " and fileId: " + fileId );
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and fileId: {}", userId, uploaderId, fileId);
        return study;
    }

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId) {
        if(pds.getDetailByPatientId(userId).isEmpty()) throw new IllegalArgumentException("Patient with id: " + userId + " does not exist!");
        if(userId!=uploaderId && dds.getDetailByDoctorId(uploaderId).isEmpty()) throw new IllegalArgumentException("Uploader with id: " + uploaderId + " does not exist or isnt able to upload!");
        if(fs.findById(fileId).isEmpty()) throw new IllegalArgumentException("File not found with ID: " + fileId);
        Study study = studyDao.create(type, comment, fileId, userId, uploaderId);   
        if(study == null){
            LOGGER.error("Failed to create study for userId: {} with uploaderId: {} and fileId: {} at {}", userId, uploaderId, fileId, LocalDateTime.now());
            throw new RuntimeException("Failed to create study for userId: " + userId + " with uploaderId: " + uploaderId + " and fileId: " + fileId );
        }
        LOGGER.info("Successfully created study for userId: {} with uploaderId: {} and fileId: {}", userId, uploaderId, fileId);
        return study;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Study> getStudyById(long id) {
        return studyDao.findStudyById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Study> getStudiesByPatientId(long id) {
        return studyDao.getStudiesByPatientId(id);
    }

    @Override
    public Optional<File> getStudyFile(long id) {
        Study study = studyDao.findStudyById(id).orElse(null);
        if (study == null) return Optional.empty();

        return fs.findById(study.getFileId());
    }

    @Override
    public List<Study> getFilteredStudies(long id, StudyTypeEnum type, boolean mostRecent) {
        List<Study> filtered;
        if(type == null) {
           filtered = getStudiesByPatientId(id);
        }else {
           filtered = getStudiesByPatientId(id).stream()
                    .filter(study -> study.getType() == type)
                    .collect(Collectors.toList());
        }
        if (!mostRecent) {
            Collections.reverse(filtered);
        }

        return filtered;
    }

    @Transactional(readOnly = true)
    @Override
    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId) {
        return studyDao.getStudiesByPatientIdAndDoctorId(patientId, doctorId);
    }

    @Transactional
    @Override
    public boolean authStudyForDoctorId(long studyId, long doctorId) {
        if(getStudyById(studyId).isEmpty()) throw new IllegalArgumentException("Study with id: " + studyId + " does not exist!");
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new IllegalArgumentException("Doctor with id: " + doctorId + " does not exist!");
        if(hasAuthStudy(studyId, doctorId)) return true;
        return studyDao.authStudyForDoctorId(studyId, doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        return studyDao.hasAuthStudy(studyId, doctorId);
    }

    @Transactional
    @Override
    public void unauthStudyForDoctorId(long studyId, long doctorId) {
        if(getStudyById(studyId).isEmpty()) throw new IllegalArgumentException("Study with id: " + studyId + " does not exist!");
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new IllegalArgumentException("Doctor with id: " + doctorId + " does not exist!");
        if(!hasAuthStudy(studyId, doctorId)) return;
        studyDao.unauthStudyForDoctorId(studyId, doctorId);
    }
}
