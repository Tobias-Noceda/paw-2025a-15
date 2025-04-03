package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.models.Appointment;

@Repository
public class AppointmentJdbcDao implements AppointmentDao{

    private static final RowMapper<Appointment> ROW_MAPPER = (rs, rowNum) -> new Appointment(rs.getLong("shift_id"), rs.getLong("patient_id"), rs.getInt("appointment_idx"), rs.getDate("appointment_date"));

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public AppointmentJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("appointments");
    }

    @Override
    public void addApointment(long shiftId, long patientId, int idx, LocalDate date) {
        final Map<String, Object> args = new HashMap<>();
        args.put("shift_id", shiftId);
        args.put("patient_id", patientId);
        args.put("appointment_idx", idx);
        args.put("appointment_date", date);
        jdbcInsert.executeAndReturnKey(args);
    }

    @Override
    public List<Appointment> getAppointmentsByShiftId(long shiftId) {
        return jdbcTemplate.query("SELECT * FROM appointments WHERE shift_id = ?", new Object[]  {shiftId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

    @Override
    public List<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        return jdbcTemplate.query("SELECT * FROM appointments WHERE shift_id = ? AND appointment_date = ?", new Object[]  {shiftId, sqlDate},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.DATE}, ROW_MAPPER);
    }

    @Override
    public List<Integer> getAppointmentIdxByShiftAndDate(long shiftId, LocalDate date) {
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        return jdbcTemplate.query("SELECT appointment_idx FROM appointments WHERE shift_id = ? AND appointment_date = ?", new Object[]  {shiftId, sqlDate},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.DATE}, (rs, rowNum) -> rs.getInt("appointment_idx"));
    }

    @Override
    public List<Appointment> getAppointmentsByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT * FROM appointments WHERE patient_id = ?", new Object[]  {patientId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

}
