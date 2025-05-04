package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.FileDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

@Service
public class FileServiceImpl implements FileService{

    @Autowired
    private FileDao fileDao;

    @Transactional(readOnly = true)
    @Override
    public Optional<File> findById(long id) {
        return fileDao.findById(id);
    }

    @Transactional
    @Override
    public File create(byte[] content, FileTypeEnum type) {
        return fileDao.create(content, type);
    }

}
