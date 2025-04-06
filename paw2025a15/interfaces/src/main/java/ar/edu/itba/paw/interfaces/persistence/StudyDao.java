package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDateTime;
import java.util.Optional;

import ar.edu.itba.paw.models.Study;

public interface StudyDao {
    public Study create(String type, long fileId, long userId, long uploaderId, LocalDateTime uploadDate);

    public Optional<Study> getStudyById(long id);
}
