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
import ar.edu.itba.paw.models.AppointmentData;

@Repository
public class AppointmentJdbcDao implements AppointmentDao{

    private static final RowMapper<Appointment> ROW_MAPPER = (rs, rowNum) -> new Appointment(rs.getLong("shift_id"), rs.getLong("patient_id"), rs.getDate("appointment_date").toLocalDate());

    private final JdbcTemplate jdbcTemplate;

    private final SimpleJdbcInsert jdbcInsert;

    @Autowired
    public AppointmentJdbcDao(final DataSource ds){
        jdbcTemplate = new JdbcTemplate(ds);
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("appointments");
    }

    @Override
    public Appointment addAppointment(long shiftId, long patientId, LocalDate date) {
        final Map<String, Object> args = new HashMap<>();
        args.put("shift_id", shiftId);
        args.put("patient_id", patientId);
        args.put("appointment_date", date);
        jdbcInsert.execute(args);

        return new Appointment(shiftId, patientId, date);
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
    public List<Appointment> getAppointmentsByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT * FROM appointments WHERE patient_id = ?", new Object[]  {patientId},
          new int[] {java.sql.Types.BIGINT}, ROW_MAPPER);
    }

    @Override
    public List<AppointmentData> getAppointmentDataByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT ds.shift_id, u.user_name AS patient_name, d.user_name AS doctor_name, a.appointment_date, ds.shift_start_time, ds.shift_end_time, ds.shift_address FROM appointments AS a JOIN users AS u ON a.patient_id = u.user_id JOIN doctor_shifts AS ds ON ds.shift_id = a.shift_id JOIN users AS d ON ds.doctor_id = d.user_id WHERE patient_id = ?",
          new Object[]  {patientId},
          new int[] {java.sql.Types.BIGINT}, (rs, rowNum) -> new AppointmentData(rs.getLong("shift_id"), rs.getString("patient_name"), rs.getString("doctor_name"), rs.getDate("appointment_date").toLocalDate(), rs.getTime("shift_start_time").toLocalTime(), rs.getTime("shift_end_time").toLocalTime(), rs.getString("shift_address")));
    }

}
