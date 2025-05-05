package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
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
    private UserService us;

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate) {
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("Uploader not found with ID: " + uploaderId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
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
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("Uploader not found with ID: " + uploaderId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
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
        if(us.getUserById(id).isEmpty()) throw new NoSuchElementException("User not found with ID: " + id);
        return studyDao.getStudiesByPatientId(id);
    }
}
