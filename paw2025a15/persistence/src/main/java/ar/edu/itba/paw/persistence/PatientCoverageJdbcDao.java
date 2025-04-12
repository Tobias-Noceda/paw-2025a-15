package ar.edu.itba.paw.persistence;

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

import ar.edu.itba.paw.interfaces.persistence.PatientCoverageDao;
import ar.edu.itba.paw.models.Insurance;

@Repository
public class PatientCoverageJdbcDao implements PatientCoverageDao{

    private static final RowMapper<Insurance> ROW_MAPPER = (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public PatientCoverageJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("patient_coverages");
    }

    @Override
    public void addCoverage(long patientId, long insuranceId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("patient_id", patientId);
        args.put("insurance_id", insuranceId);
        jdbcInsert.execute(args);
    }

    @Override
    public boolean removeCoverage(long patientId, long insuranceId) {
        String sql = "DELETE FROM patient_coverages WHERE patient_id = ? AND insurance_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, patientId, insuranceId);
        return rowsAffected > 0;
    }

    @Override
    public Optional<Insurance> getInsuranceById(long patientId) {
        String sql = "SELECT insurances.* from insurances JOIN patient_coverages ON patient_coverages.insurance_id = insurances.insurance_id WHERE patient_coverages.patient_id = ?";
        return jdbcTemplate.query(sql, new Object[]{patientId}, new int[]{java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

}
