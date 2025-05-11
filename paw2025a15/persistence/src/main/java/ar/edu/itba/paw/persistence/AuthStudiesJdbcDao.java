package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.AuthStudiesDao;

@Repository
public class AuthStudiesJdbcDao implements AuthStudiesDao{

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public AuthStudiesJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("auth_studies");
    }


    @Override
    public boolean authStudyForDoctorId(long studyId, long doctorId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("study_id", studyId);
        args.put("doctor_id", doctorId);
        return jdbcInsert.execute(args)==1;
    }

    @Override
    public boolean hasAuthStudy(long studyId, long doctorId) {
        return jdbcTemplate.query("SELECT 1 FROM auth_studies WHERE study_id = ? AND doctor_id = ? LIMIT 1", new Object[]{studyId, doctorId}, new int[]{java.sql.Types.BIGINT, java.sql.Types.BIGINT}, (rs, rowNum)-> rs.next()).stream().findFirst().isPresent() ;
    }

    @Override
    public void unauthStudyForDoctorId(long studyId, long doctorId) {
        if(!hasAuthStudy(studyId, doctorId)) return;
        String query = "DELETE FROM auth_studies WHERE study_id = ? AND doctor_id = ?";
        jdbcTemplate.update(query,
        new Object[] {studyId, doctorId},
        new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }

    @Override
    public void unauthAllStudiesForDoctorIdAndPatientId(long userId, long doctorId) {
        String query = "DELETE FROM auth_studies WHERE doctor_id = ? AND study_id IN (SELECT study_id FROM studies WHERE user_id = ?)";
        jdbcTemplate.update(query,
        new Object[] {doctorId, userId},
        new int[] {java.sql.Types.BIGINT, java.sql.Types.BIGINT});
    }
}
