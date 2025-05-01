package ar.edu.itba.paw.persistence;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        return jdbcTemplate.query("SELECT * FROM appointments WHERE shift_id = ? AND appointment_date = ?", new Object[]  {shiftId, sqlDate},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.DATE}, ROW_MAPPER).stream().findFirst();
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT ds.shift_id, u.user_name AS patient_name, d.user_name AS doctor_name, a.appointment_date, ds.shift_start_time, ds.shift_end_time, ds.shift_address FROM appointments AS a JOIN users AS u ON a.patient_id = u.user_id JOIN doctor_shifts AS ds ON ds.shift_id = a.shift_id JOIN users AS d ON ds.doctor_id = d.user_id WHERE a.patient_id = ? AND (a.appointment_date + ds.shift_start_time) > ? ORDER BY a.appointment_date ASC, ds.shift_start_time ASC",
          new Object[]  {patientId, LocalDateTime.now()},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.TIMESTAMP}, (rs, rowNum) -> new AppointmentData(rs.getLong("shift_id"), rs.getString("patient_name"), rs.getString("doctor_name"), rs.getDate("appointment_date").toLocalDate(), rs.getTime("shift_start_time").toLocalTime(), rs.getTime("shift_end_time").toLocalTime(), rs.getString("shift_address")));
    }

    @Override
    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId) {
        return jdbcTemplate.query("SELECT ds.shift_id, u.user_name AS patient_name, d.user_name AS doctor_name, a.appointment_date, ds.shift_start_time, ds.shift_end_time, ds.shift_address FROM appointments AS a JOIN users AS u ON a.patient_id = u.user_id JOIN doctor_shifts AS ds ON ds.shift_id = a.shift_id JOIN users AS d ON ds.doctor_id = d.user_id WHERE a.patient_id = ? AND (a.appointment_date + ds.shift_start_time) < ? ORDER BY a.appointment_date DESC, ds.shift_start_time DESC",
          new Object[]  {patientId, LocalDateTime.now()},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.TIMESTAMP}, (rs, rowNum) -> new AppointmentData(rs.getLong("shift_id"), rs.getString("patient_name"), rs.getString("doctor_name"), rs.getDate("appointment_date").toLocalDate(), rs.getTime("shift_start_time").toLocalTime(), rs.getTime("shift_end_time").toLocalTime(), rs.getString("shift_address")));
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId) {
        return jdbcTemplate.query("SELECT ds.shift_id, u.user_name AS patient_name, d.user_name AS doctor_name, a.appointment_date, ds.shift_start_time, ds.shift_end_time, ds.shift_address FROM appointments AS a JOIN users AS u ON a.patient_id = u.user_id JOIN doctor_shifts AS ds ON ds.shift_id = a.shift_id JOIN users AS d ON ds.doctor_id = d.user_id WHERE ds.doctor_id = ? AND ds.doctor_id <> a.patient_id AND (a.appointment_date + ds.shift_start_time) > ? ORDER BY a.appointment_date ASC, ds.shift_start_time ASC",
          new Object[]  {doctorId, LocalDateTime.now()},
          new int[] {java.sql.Types.BIGINT, java.sql.Types.TIMESTAMP}, (rs, rowNum) -> new AppointmentData(rs.getLong("shift_id"), rs.getString("patient_name"), rs.getString("doctor_name"), rs.getDate("appointment_date").toLocalDate(), rs.getTime("shift_start_time").toLocalTime(), rs.getTime("shift_end_time").toLocalTime(), rs.getString("shift_address")));
    }

    @Override
    public boolean removeAppointment(long shiftId, LocalDate date) {
        int rowsAffected = jdbcTemplate.update(
            "DELETE FROM appointments WHERE shift_id = ? AND appointment_date = ?", 
            new Object[]{shiftId, date},
            new int[]{java.sql.Types.BIGINT, java.sql.Types.DATE}
        );
        return rowsAffected > 0;
    }

    @Override
    public void clearRemovedAppointmentBeforeDate(LocalDate date) {
        jdbcTemplate.update(
            """
                DELETE FROM appointments a
                USING doctor_shifts ds
                WHERE a.shift_id = ds.shift_id
                AND a.patient_id = ds.doctor_id
                AND a.appointment_date < ?
            """,
            new Object[]{Date.valueOf(date)},
            new int[]{java.sql.Types.DATE}
        );
    }
}
