package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.FileDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

@Service
public class FileServiceImpl implements FileService{

    private static final Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

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
        File file = fileDao.create(content, type);
        if(file == null){
            LOGGER.error("Failed to create file at {}", LocalDateTime.now());
            throw new RuntimeException("Failed to create file");
        }
        LOGGER.info("Successfully created file with id: {}", file.getId());
        return file;
    }

}
