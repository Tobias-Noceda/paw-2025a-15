package ar.edu.itba.paw.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FileJpaDaoTest {
    
    @Autowired
    private FileJpaDao fileDao;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void testCreate(){
        final byte[] CONTENT = TestData.Images.newImage.getContent();
        final FileTypeEnum TYPE = TestData.Images.newImage.getType();

        File file = fileDao.create(CONTENT, TYPE);
        File filePersisted = em.find(File.class, file.getId());

        Assert.assertNotNull(filePersisted); 
        Assert.assertArrayEquals(CONTENT, filePersisted.getContent());
    }

}
