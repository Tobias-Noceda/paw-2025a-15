package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.AppointmentId;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentJpaDao implements AppointmentDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Appointment addAppointment(DoctorShift shift, User patient, LocalDate date) {
        final Appointment app = new Appointment(shift, patient, date);
        em.persist(app);
        return app;
    }

    @Override
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return Optional.ofNullable(em.find(Appointment.class, new AppointmentId(shiftId, date)));
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId) {//TODO:check from migration jpa
        String query = "SELECT NEW AppointmentData(ds.id, u.id, u.name, d.id, d.name, " +
                  "a.date, ds.startTime, ds.endTime, ds.address) " +
                  "FROM Appointment a " +
                  "JOIN a.patient u " +
                  "JOIN a.shift ds " +
                  "JOIN ds.doctor d " +
                  "WHERE a.patient.id = :patientId " +
                  "AND FUNCTION('TO_TIMESTAMP', CONCAT(a.date, ' ', ds.startTime), 'YYYY-MM-DD HH24:MI:SS') > :now " +
                  "ORDER BY a.date ASC, ds.startTime ASC";

        return em.createQuery(query, AppointmentData.class)
                        .setParameter("patientId", patientId)
                        .setParameter("now", LocalDateTime.now())
                        .getResultList();
    }

    @Override
    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId) {//TODO:check from migration jpa
        String query = "SELECT NEW AppointmentData(ds.id, u.id, u.name, d.id, d.name, " +
                  "a.date, ds.startTime, ds.endTime, ds.address) " +
                  "FROM Appointment a " +
                  "JOIN a.patient u " +
                  "JOIN a.shift ds " +
                  "JOIN ds.doctor d " +
                  "WHERE a.patient.id = :patientId " +
                  "AND FUNCTION('TO_TIMESTAMP', CONCAT(a.date, ' ', ds.startTime), 'YYYY-MM-DD HH24:MI:SS') < :now " +
                  "ORDER BY a.date DESC, ds.startTime DESC";

        return em.createQuery(query, AppointmentData.class)
                        .setParameter("patientId", patientId)
                        .setParameter("now", LocalDateTime.now())
                        .getResultList();
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId) {//TODO:check from migration jpa
        String query = "SELECT NEW AppointmentData(ds.id, u.id, u.name, d.id, d.name, " +
                  "a.date, ds.startTime, ds.endTime, ds.address) " +
                  "FROM Appointment a " +
                  "JOIN a.patient u " +
                  "JOIN a.shift ds " +
                  "JOIN ds.doctor d " +
                  "WHERE ds.doctor.id = :doctorId " +
                  "AND ds.doctor.id <> a.patient.id " +
                  "AND FUNCTION('TO_TIMESTAMP', CONCAT(a.date, ' ', ds.startTime), 'YYYY-MM-DD HH24:MI:SS') > :now " +
                  "ORDER BY a.date ASC, ds.startTime ASC";

        return em.createQuery(query, AppointmentData.class)
                        .setParameter("doctorId", doctorId)
                        .setParameter("now", LocalDateTime.now())
                        .getResultList();
    }

    @Override
    public List<Appointment> getAppointmentsForDate(LocalDate date) {
        String query = "SELECT from Appointment a where a.date = :date";
        return em.createQuery(query, Appointment.class)
                        .setParameter("date", date)
                        .getResultList();
    }

    @Override
    public boolean removeAppointment(long shiftId, LocalDate date) {
        Appointment app = em.find(Appointment.class, new AppointmentId(shiftId, date));
        if(app==null) return false;
        em.remove(app);
        return true;
    }

    @Override
    public void clearRemovedAppointmentBeforeDate(LocalDate date) {
        em.createQuery("DELETE FROM Appointment a WHERE a.appointmentDate < :date AND a.patient.id = a.shift.doctor.id")
            .setParameter("date", date)
            .executeUpdate();
    }
    
}
