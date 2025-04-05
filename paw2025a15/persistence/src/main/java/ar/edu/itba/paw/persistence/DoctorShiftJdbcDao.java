package ar.edu.itba.paw.persistence;

import java.time.LocalTime;
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

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

@Repository
public class DoctorShiftJdbcDao implements DoctorShiftDao{

    private static final RowMapper<DoctorShift> ROW_MAPPER = (rs, rowNum) -> new DoctorShift(rs.getLong("shift_id"), rs.getLong("doctor_id"), WeekdayEnum.fromInt(rs.getInt("shift_weekday")), rs.getString("shift_address"), rs.getTime("shift_start_time").toLocalTime(), rs.getTime("shift_start_time").toLocalTime());
    
    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public DoctorShiftJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("doctor_shifts").usingGeneratedKeyColumns("shift_id");
    }

    @Override
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime) {
        final Map<String, Object> args = new HashMap<>();
        args.put("doctor_id", doctorId);
        args.put("shift_weekday", weekday);
        args.put("shift_address", address);
        args.put("shift_start_time", startTime);
        args.put("shift_end_time", endTime);
        final Number shift_id = jdbcInsert.executeAndReturnKey(args);
        return new DoctorShift(shift_id.longValue(), doctorId, weekday, address, startTime, endTime);
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return jdbcTemplate.query("SELECT * FROM doctor_shifts WHERE shift_id = ?", new Object[]  {id},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorId(long doctorId) {
        return jdbcTemplate.query("SELECT * FROM doctor_shifts WHERE doctor_id = ?", new Object[]  {doctorId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorIdAndWeekday(long doctorId, WeekdayEnum weekday) {
        return jdbcTemplate.query("SELECT * FROM doctor_shifts WHERE doctor_id = ? AND shift_weekday = ?", new Object[]  {doctorId, weekday.ordinal()},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.INTEGER}, ROW_MAPPER);
    }

}
