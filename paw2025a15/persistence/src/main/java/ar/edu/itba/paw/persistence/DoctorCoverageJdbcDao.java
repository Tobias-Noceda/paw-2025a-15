package ar.edu.itba.paw.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorCoverageDao;
import ar.edu.itba.paw.models.Insurance;

@Repository
public class DoctorCoverageJdbcDao implements DoctorCoverageDao{    

    private static final RowMapper<Insurance> ROW_MAPPER = (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public DoctorCoverageJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("doctor_coverages");
    }

    @Override
    public void addCoverage(long doctorId, long insuranceId) {
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("insurance_id", insuranceId);
        jdbcInsert.execute(args);
    }

    @Override
    public boolean removeCoverage(long doctorId, long insuranceId) {
        String sql = "DELETE FROM doctor_coverages WHERE doctor_id = ? AND insurance_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, doctorId, insuranceId);
        return rowsAffected > 0;
    }

    @Override
    public List<Insurance> getInsurancesById(long doctorId) {
        String sql = "SELECT insurances.* from insurances JOIN doctor_coverages ON doctor_coverages.insurance_id = insurances.insurance_id WHERE doctor_coverages.doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, ROW_MAPPER);
    }
}
