package ar.edu.itba.paw.persistence;

import java.util.Optional;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;
import ar.edu.itba.paw.persistence.config.TestConfig;

@Sql("classpath:images.sql")
@Transactional
@Rollback
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class FileJdbcDaoTest {

    @Autowired
    private DataSource ds;  

    @Autowired
    private FileJdbcDao fileDao;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp(){
        jdbcTemplate = new JdbcTemplate(ds);
    }

    @Test
    public void testCreate(){
        final byte[] CONTENT = TestData.Images.newImage.getContent();
        final FileTypeEnum TYPE = TestData.Images.newImage.getType();

        File file = fileDao.create(CONTENT, TYPE);

        Assert.assertNotNull(file); 
        Assert.assertArrayEquals(CONTENT, file.getContent());
    }

    @Test
    public void testFindById(){
        final long IMAGE_ID = TestData.Images.validImage.getId();
        final File IMAGE = TestData.Images.validImage;
        
        Optional<File> file = fileDao.findById(IMAGE_ID);

        Assert.assertTrue(file.isPresent());
        Assert.assertEquals(IMAGE, file.get());
    }

    @Test
    public void testFindByIdNonexistent(){
        final long IMAGE_ID = 0;
        
        Optional<File> image = fileDao.findById(IMAGE_ID);

        Assert.assertFalse(image.isPresent());
    }

}
