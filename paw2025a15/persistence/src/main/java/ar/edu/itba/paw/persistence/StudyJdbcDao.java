package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
import ar.edu.itba.paw.models.StudyTypeEnum;

@Repository
public class StudyJdbcDao implements StudyDao{

    private static final RowMapper<Study> ROW_MAPPER = (rs, rowNum) -> new Study(rs.getLong("study_id"), StudyTypeEnum.fromInt(rs.getInt("study_type")), rs.getString("study_comment"), rs.getLong("file_id"), rs.getLong("user_id"), rs.getLong("uploader_id"), rs.getTimestamp("upload_date").toLocalDateTime(), rs.getDate("study_date").toLocalDate());
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public StudyJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("studies").usingGeneratedKeyColumns("study_id");
    }

    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate) {
        final Map<String, Object> args = new HashMap<>();
        args.put("study_type", type.ordinal());
        args.put("study_comment", comment);
        args.put("file_id", fileId);
        args.put("user_id", userId);
        args.put("uploader_id", uploaderId);
        args.put("upload_date", uploadDate);
        args.put("study_date", studyDate);
        final Number study_id = jdbcInsert.executeAndReturnKey(args);
        return new Study(study_id.longValue(), type, comment, fileId, userId, uploaderId, uploadDate, studyDate);
    }

    @Override
    public Optional<Study> getStudyById(long id) {
        return jdbcTemplate.query("SELECT * FROM studies WHERE study_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<Study> getStudiesByPatientId(long id) {
        return jdbcTemplate.query("SELECT * FROM studies WHERE user_id = ? ORDER BY study_date DESC", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

}