package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.models.entities.Appointment;
import ar.edu.itba.paw.models.entities.AppointmentId;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;

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
    public List<AppointmentNew> getFutureAppointmentDataByPatientId(long patientId) {//TODO:check from migration jpa
        String query = "FROM AppointmentNew a " +
                    "WHERE a.patient.id = :patientId " +
                    "AND (a.id.date > :today " +
                    "OR (a.id.date = :today AND a.id.startTime > :todaysTime)) " +
                    "ORDER BY a.id.date ASC, a.id.startTime ASC";

        LocalDateTime now = LocalDateTime.now();

        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("patientId", patientId)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
                        .getResultList();
    }

    @Override
    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId) {//TODO:check from migration jpa
        String query = "FROM AppointmentNew a " +
                    "WHERE a.patient.id = :patientId " +
                    "AND (a.id.date < :today " +
                    "OR (a.id.date = :today AND a.id.startTime < :todaysTime)) " +
                    "ORDER BY a.id.date DESC, a.id.startTime DESC";

        LocalDateTime now = LocalDateTime.now();

        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("patientId", patientId)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
                        .getResultList();
    }

    @Override
    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId) {//TODO:check from migration jpa
        String query = "FROM AppointmentNew a " +
                    "WHERE a.shift.doctor.id = :doctorId " +
                    "AND (a.id.date > :today " +
                    "OR (a.id.date = :today AND a.id.startTime > :todaysTime)) " +
                    "ORDER BY a.id.date ASC, a.id.startTime ASC";

        LocalDateTime now = LocalDateTime.now();
        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("doctorId", doctorId)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
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
