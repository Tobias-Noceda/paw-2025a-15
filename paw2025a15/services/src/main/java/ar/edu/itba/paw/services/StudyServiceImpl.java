package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Study;
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

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
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate) {
        if(us.getUserById(userId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(us.getUserById(uploaderId).isEmpty()) throw new NoSuchElementException("User not found with ID: " + userId);
        if(fs.findById(fileId).isEmpty()) throw new NoSuchElementException("File not found with ID: " + fileId);
        LocalDate date = studyDate == null? uploadDate.toLocalDate() : studyDate;
        return studyDao.create(type, comment, fileId, userId, uploaderId, uploadDate, date);   
    }

    @Override
    public Optional<Study> getStudyById(long id) {
        return studyDao.findStudyById(id);
    }

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
}
