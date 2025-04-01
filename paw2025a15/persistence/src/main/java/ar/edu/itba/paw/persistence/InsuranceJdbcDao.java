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

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.models.Insurance;

@Repository
public class InsuranceJdbcDao implements InsuranceDao{

    private static final RowMapper<Insurance> ROW_MAPPER = (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public InsuranceJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("insurances").usingGeneratedKeyColumns("insurance_id");
    }

    @Override
    public Insurance create(String name) {
        final Map<String, Object> args = new HashMap<>();
        args.put("insurance_name", name);
        final Number insurance_id = jdbcInsert.executeAndReturnKey(args);
        return new Insurance(insurance_id.longValue(), name);
    }

    @Override
    public Optional<Insurance> getInsuranceById(long id) {
        return jdbcTemplate.query("SELECT * FROM insurances WHERE insurance_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }
    
}
