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

import ar.edu.itba.paw.interfaces.persistence.PatientDao;
import ar.edu.itba.paw.models.Patient;

@Repository
public class PatientJdbcDao implements PatientDao{
    private static final RowMapper<Patient> ROW_MAPPER = (rs, rowNum) -> new Patient(rs.getLong("patient_id"), rs.getString("patient_email"), rs.getString("patient_password"), rs.getString("patient_name"), rs.getLong("picture_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public PatientJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("patients").usingGeneratedKeyColumns("patient_id");
    }

    @Override
    public Patient create(String email, String password, String name, long pictureId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("patient_email", email);
        args.put("patient_password", password);
        args.put("patient_name", name);
        args.put("picture_id", pictureId);
        final Number patient_id = jdbcInsert.executeAndReturnKey(args);
        return new Patient(patient_id.longValue(), email, password, name, pictureId);
    }

    @Override
    public Optional<Patient> getPatientById(long id) {
        return jdbcTemplate.query("SELECT * FROM patients WHERE patient_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }
}

