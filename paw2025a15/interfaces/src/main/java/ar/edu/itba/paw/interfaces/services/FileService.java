package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

public interface FileService {
    public Optional<File> findById(long id);

    public File create(final byte[] content, final FileTypeEnum type);


}
