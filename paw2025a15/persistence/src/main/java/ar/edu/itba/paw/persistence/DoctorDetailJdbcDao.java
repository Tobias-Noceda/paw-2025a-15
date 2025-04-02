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

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorDetail;

@Repository
public class DoctorDetailJdbcDao implements DoctorDetailDao{

    private static final RowMapper<DoctorDetail> ROW_MAPPER = (rs, rowNum) -> new DoctorDetail(rs.getLong("doctor_id"), rs.getString("doctor_licence"), rs.getString("doctor_specialty"));
   
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public DoctorDetailJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("doctor_details");
    }

    @Override
    public DoctorDetail create(long doctorId, String licence, String specialty) {
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("doctor_licence", licence);
        args.put("doctor_specialty", specialty);
        jdbcInsert.executeAndReturnKey(args);
        return new DoctorDetail(doctorId, licence, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return jdbcTemplate.query("SELECT * FROM doctor_details WHERE doctor_id = ?", new Object[]  {doctorId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

}
