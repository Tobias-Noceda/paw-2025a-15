package ar.edu.itba.paw.persistence;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import ar.edu.itba.paw.models.enums.StudyTypeEnum;

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
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDate studyDate) {
        final Map<String, Object> args = new HashMap<>();
        args.put("study_type", type.ordinal());
        args.put("study_comment", comment);
        args.put("file_id", fileId);
        args.put("user_id", userId);
        args.put("uploader_id", uploaderId);
        LocalDateTime uploadDate = LocalDateTime.now();
        args.put("upload_date", Timestamp.valueOf(uploadDate));
        args.put("study_date", studyDate);
        final Number study_id = jdbcInsert.executeAndReturnKey(args);
        return new Study(study_id.longValue(), type, comment, fileId, userId, uploaderId, uploadDate, studyDate);
    }
    
    @Override
    public Study create(StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("study_type", type.ordinal());
        args.put("study_comment", comment);
        args.put("file_id", fileId);
        args.put("user_id", userId);
        args.put("uploader_id", uploaderId);
        LocalDateTime uploadDate = LocalDateTime.now();
        args.put("upload_date", Timestamp.valueOf(uploadDate));
        args.put("study_date", Date.valueOf(uploadDate.toLocalDate()));
        final Number study_id = jdbcInsert.executeAndReturnKey(args);
        return new Study(study_id.longValue(), type, comment, fileId, userId, uploaderId, uploadDate, uploadDate.toLocalDate());
    }

    @Override
    public Optional<Study> findStudyById(long id) {
        return jdbcTemplate.query(
            """
                SELECT *
                FROM studies
                WHERE study_id = ?
            """,
            new Object[]  {id},
            new int[] {java.sql.Types.BIGINT},
            ROW_MAPPER
        ).stream().findFirst();
    }

    @Override
    public List<Study> getStudiesByPatientId(long id) {
        return jdbcTemplate.query("SELECT * FROM studies WHERE user_id = ? ORDER BY study_date DESC", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

    @Override
    public List<Study> getFilteredStudiesByPatientId(long id, StudyTypeEnum type, boolean mostRecent) {
        StringBuilder query = new StringBuilder(
            """
                SELECT *
                FROM studies
                WHERE user_id = ?
            """);
        
        List<Object> args = new ArrayList<>();
        args.add(id);
        List<Integer> types = new ArrayList<>();
        types.add(java.sql.Types.BIGINT);
        if(type != null) {
            query.append(" AND study_type = ?");
            args.add(type.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
        if(mostRecent) {
            query.append(" ORDER BY study_date DESC");
        } else {
            query.append(" ORDER BY study_date ASC");
        }
        return jdbcTemplate.query(
            query.toString(),
            args.toArray(),
            types.stream().mapToInt(i -> i).toArray(),
            ROW_MAPPER
        );
    }

    @Override
    public List<Study> getStudiesByPatientIdAndDoctorId(long patientId, long doctorId) {
        return jdbcTemplate.query("SELECT * FROM studies AS s JOIN auth_studies AS ast ON s.study_id = ast.study_id WHERE s.user_id = ? AND ast.doctor_id = ? ORDER BY study_date DESC", new Object[]  {patientId, doctorId},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT}, ROW_MAPPER);
    }

    @Override
    public List<Study> getFilteredStudiesByPatientIdAndDoctorId(long patientId, long doctorId, StudyTypeEnum type, boolean mostRecent) {
        StringBuilder query = new StringBuilder(
            """
                SELECT *
                FROM studies AS s JOIN auth_studies AS ast ON s.study_id = ast.study_id
                WHERE ast.doctor_id = ? 
                AND s.user_id = ?
            """);
        
        List<Object> args = new ArrayList<>();
        args.add(doctorId);
        args.add(patientId);
        List<Integer> types = new ArrayList<>();
        types.add(java.sql.Types.BIGINT);
        types.add(java.sql.Types.BIGINT);
        if(type != null) {
            query.append(" AND s.study_type = ?");
            args.add(type.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
        if(mostRecent) {
            query.append(" ORDER BY study_date DESC");
        } else {
            query.append(" ORDER BY study_date ASC");
        }
        return jdbcTemplate.query(
            query.toString(),
            args.toArray(),
            types.stream().mapToInt(i -> i).toArray(),
            ROW_MAPPER
        );
    }

}