package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.File;

public interface FileDao {
    public Optional<File> findById(long id);

    public File create(final byte[] content);
}
