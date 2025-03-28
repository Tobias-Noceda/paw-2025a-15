package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.FileDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.File;

@Service
public class FileServiceImpl implements FileService{

    private final FileDao fileDao;

    @Autowired
    public FileServiceImpl(final FileDao fileDao){
        this.fileDao = fileDao;
    }

    @Override
    public Optional<File> findById(long id) {
        return fileDao.findById(id);
    }

    @Override
    public File create(byte[] content) {
        return fileDao.create(content);
    }

}
