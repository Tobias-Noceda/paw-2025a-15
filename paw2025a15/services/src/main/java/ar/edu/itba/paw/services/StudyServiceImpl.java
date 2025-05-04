package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
        if(studyDate == null) return create(type, comment, fileId, userId, uploaderId);
        return studyDao.create(type, comment, fileId, userId, uploaderId, studyDate);   
    }

    @Transactional
    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId) {
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
        return studyDao.create(type, comment, fileId, userId, uploaderId);   
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
