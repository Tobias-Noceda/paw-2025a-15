package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.AppointmentNewId;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;

@Repository
public class AppointmentJpaDao implements AppointmentDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail) {
        DoctorSingleShift shift = em.find(DoctorSingleShift.class, shiftId);
        User user = em.find(User.class, patientId);
        if(shift==null || user==null) return null;
        final AppointmentNew app = new AppointmentNew(shift, user, date, startTime, endTime, detail);
        em.persist(app);
        return app;
    }

    @Override
    public Optional<AppointmentNew> getAppointmentByShiftDateAndTime(DoctorSingleShift shift, LocalDate date, LocalTime startTime, LocalTime endTime) {
        AppointmentNew app = em.find(AppointmentNew.class, new AppointmentNewId(shift.getId(), date, startTime, endTime));
        if(app == null) {
            return Optional.empty();
        }
        return Optional.of(app);
    }

    @Override
    public List<AppointmentNew> getFutureAppointmentDataByPatient(Patient patient) {
        String query = "FROM AppointmentNew a " +
                    "WHERE a.patient = :patient " +
                    "AND (a.id.date > :today " +
                    "OR (a.id.date = :today AND a.id.startTime > :todaysTime)) " +
                    "ORDER BY a.id.date ASC, a.id.startTime ASC";

        LocalDateTime now = LocalDateTime.now();

        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("patient", patient)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
                        .getResultList();
    }

    @Override
    public List<AppointmentNew> getOldAppointmentDataByPatient(Patient patient) {
        String query = "FROM AppointmentNew a " +
                    "WHERE a.patient = :patient " +
                    "AND (a.id.date < :today " +
                    "OR (a.id.date = :today AND a.id.startTime < :todaysTime)) " +
                    "ORDER BY a.id.date DESC, a.id.startTime DESC";

        LocalDateTime now = LocalDateTime.now();

        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("patient", patient)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
                        .getResultList();
    }

    @Override
    public List<AppointmentNew> getFutureAppointmentDataByDoctor(Doctor doctor) {
        String query = "FROM AppointmentNew a " +
                    "WHERE a.shift.doctor = :doctor " +
                    "AND (a.patient <> :doctor) " +
                    "AND (a.id.date > :today " +
                    "OR (a.id.date = :today AND a.id.startTime > :todaysTime)) " +
                    "ORDER BY a.id.date ASC, a.id.startTime ASC";

        LocalDateTime now = LocalDateTime.now();
        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("doctor", doctor)
                        .setParameter("today", now.toLocalDate())
                        .setParameter("todaysTime", now.toLocalTime())
                        .getResultList();
    }

    @Override
    public boolean cancelAppointment(DoctorSingleShift shift, LocalDate date, LocalTime startTime, LocalTime endTime) {
        AppointmentNew app = em.find(AppointmentNew.class, new AppointmentNewId(shift.getId(), date, startTime, endTime));
        if(app ==null) return false;
        em.remove(app);
        return true;
    }

    @Override
    public List<AppointmentNew> getAppointmentsForDate(LocalDate date) {
        String query = "from AppointmentNew a where a.id.date = :date";
        return em.createQuery(query, AppointmentNew.class)
                        .setParameter("date", date)
                        .getResultList();
    }

    @Override
    public void clearRemovedAppointmentBeforeDate(LocalDate date) { 
        em.createQuery("DELETE FROM AppointmentNew a WHERE a.id.date < :date AND a.patient.id IN (SELECT ds.doctor.id FROM DoctorSingleShift ds)")
            .setParameter("date", date)
            .executeUpdate();
    }
    
}
