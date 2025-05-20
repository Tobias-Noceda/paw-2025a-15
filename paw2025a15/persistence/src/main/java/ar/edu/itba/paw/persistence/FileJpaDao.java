package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.FileDao;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.Optional;

@Repository
public class FileJpaDao implements FileDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<File> findById(long id) {
        return Optional.ofNullable(em.find(File.class, id));
    }

    @Override
    public File create(byte[] content, FileTypeEnum type) {
        final File file = new File(content, type);
        em.persist(file);
        return file;
    }

}
