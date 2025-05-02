package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.StudyTypeEnum;

@Service
public class StudyServiceImpl implements StudyService{

    private final StudyDao studyDao;

    private final FileService fs;

    private final UserService us;

    @Autowired
    public StudyServiceImpl(final StudyDao studyDao, final FileService fs, final UserService us){
        this.studyDao = studyDao;
        this.fs = fs;
        this.us = us;
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate) {
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
        if(studyDate == null) return create(type, comment, fileId, userId, uploaderId);
        return studyDao.create(type, comment, fileId, userId, uploaderId, studyDate);   
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId) {
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
        return studyDao.create(type, comment, fileId, userId, uploaderId);   
    }

    @Override
    public List<Study> getStudiesByPatientId(long id) {
        return studyDao.getStudiesByPatientId(id);
    }
}
