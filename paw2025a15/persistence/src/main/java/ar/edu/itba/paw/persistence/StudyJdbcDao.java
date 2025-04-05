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

import ar.edu.itba.paw.interfaces.persistence.StudyDao;
import ar.edu.itba.paw.models.Study;

@Repository
public class StudyJdbcDao implements StudyDao{

    private static final RowMapper<Study> ROW_MAPPER = (rs, rowNum) -> new Study(rs.getLong("study_id"), rs.getString("study_type"), rs.getLong("file_id"), rs.getLong("user_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public StudyJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("studies").usingGeneratedKeyColumns("study_id");
    }

    @Override
    public Study create(String type, long fileId, long userId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("study_type", type);
        args.put("file_id", fileId);
        args.put("user_id", userId);
        final Number study_id = jdbcInsert.executeAndReturnKey(args);
        return new Study(study_id.longValue(), type, fileId, userId);
    }

    @Override
    public Optional<Study> getStudyById(long id) {
        return jdbcTemplate.query("SELECT * FROM studies WHERE study_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

}