package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.interfaces.services.StudyService;
import ar.edu.itba.paw.models.Study;

@Service
public class StudyServiceImpl implements StudyService{

    private final StudyDao studyDao;

    @Autowired
    public StudyServiceImpl(final StudyDao studyDao){
        this.studyDao = studyDao;
    }

    @Override
    public Study create(String type, long fileId, long userId, long uploaderId, LocalDateTime uploadDate) {
        return studyDao.create(type, fileId, userId, uploaderId, uploadDate);
    }

    @Override
    public Optional<Study> getStudyById(long id) {
        return studyDao.getStudyById(id);
    }

}
