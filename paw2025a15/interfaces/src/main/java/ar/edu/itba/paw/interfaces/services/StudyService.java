package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.Study;

public interface StudyService {
    public Study create(String type, long fileId, long userId);

    public Optional<Study> getStudyById(long id);
}
