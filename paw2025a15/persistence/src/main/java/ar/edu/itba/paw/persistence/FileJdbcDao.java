package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.FileDao;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.enums.FileTypeEnum;

@Repository
public class FileJdbcDao implements FileDao{

    private static final RowMapper<File> ROW_MAPPER = (rs, rowNum) -> new File(rs.getLong("file_id"), rs.getBytes("file_content"), FileTypeEnum.fromInt(rs.getInt("file_type")));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public FileJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("files").usingGeneratedKeyColumns("file_id");
    }

    @Override
    public Optional<File> findById(long id) {
        return jdbcTemplate.query("SELECT * FROM files WHERE file_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public File create(byte[] content, FileTypeEnum type) {
        final Map<String, Object> args = new HashMap<>();
        args.put("file_content", content); 
        args.put("file_type", type.ordinal()); 
        final Number file_id = jdbcInsert.executeAndReturnKey(args);
        return new File(file_id.longValue(), content, type);
    }

}
