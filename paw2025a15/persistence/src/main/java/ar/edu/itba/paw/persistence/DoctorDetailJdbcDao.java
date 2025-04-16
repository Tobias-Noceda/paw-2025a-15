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

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;

@Repository
public class DoctorDetailJdbcDao implements DoctorDetailDao{

    private static final RowMapper<DoctorDetail> ROW_MAPPER = (rs, rowNum) -> new DoctorDetail(rs.getLong("doctor_id"), rs.getString("doctor_licence"), SpecialtyEnum.fromInt(rs.getInt("doctor_specialty")));

    private final RowMapper<DoctorView> DV_ROW_MAPPER = (rs, rowNum) -> {
        DoctorView doc = new DoctorView(
            rs.getLong("doctor_id"),
            rs.getString("user_name"),
            SpecialtyEnum.fromInt(rs.getInt("doctor_specialty")),
            rs.getLong("picture_id"),
            getInsurancesById(rs.getLong("doctor_id")),
            getWeekdaysById(rs.getLong("doctor_id"))
        );
        return doc;
    };
    
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public DoctorDetailJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("doctor_details");
    }

    @Override
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty) {
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("doctor_licence", licence);
        args.put("doctor_specialty", specialty.ordinal());
        jdbcInsert.execute(args);
        return new DoctorDetail(doctorId, licence, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return jdbcTemplate.query("SELECT * FROM doctor_details WHERE doctor_id = ?", new Object[]  {doctorId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<DoctorView> getAllDoctors() {
        return (List<DoctorView>) jdbcTemplate.query(
                """
                SELECT dd.doctor_id, u.user_name, dd.doctor_specialty, u.picture_id
                FROM doctor_details AS dd JOIN users AS u ON dd.doctor_id = u.user_id
                """,
                DV_ROW_MAPPER
        );
    }


    //TODO estas capaz estan mal que esten aca
    public List<Insurance> getInsurancesById(long doctorId) {
        String sql = "SELECT insurances.* from insurances JOIN doctor_coverages ON doctor_coverages.insurance_id = insurances.insurance_id WHERE doctor_coverages.doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> new Insurance(rs.getLong("insurance_id"), rs.getString("insurance_name"), rs.getLong("picture_id")));
    }

    public List<WeekdayEnum> getWeekdaysById(long doctorId) {
        String sql = "SELECT DISTINCT shift_weekday FROM doctor_shifts WHERE doctor_id = ?";
        return jdbcTemplate.query(sql, new Object[]{doctorId}, new int[]{java.sql.Types.BIGINT}, (rs, rowNum) -> WeekdayEnum.fromInt(rs.getInt("shift_weekday")));
    }

    @Override
    public List<DoctorView> findDoctorsByName(String name) {
        return (List<DoctorView>) jdbcTemplate.query(
                "SELECT dd.doctor_id, u.user_name, dd.doctor_specialty FROM doctor_details AS dd JOIN users AS u ON dd.doctor_id = u.user_id WHERE u.user_name LIKE ?",
                new Object[]{ "%" + name + "%" },
                new int[]{ java.sql.Types.VARCHAR },
                DV_ROW_MAPPER
        );
    }

}
