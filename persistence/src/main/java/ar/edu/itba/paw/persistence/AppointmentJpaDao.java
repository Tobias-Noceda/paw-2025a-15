package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

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

    @Override
    public void cancelAppointmentRange(long doctorId, LocalDate startDate, LocalDate endDate) {
        List<AppointmentNew> toRemove =  em.createQuery("from AppointmentNew a where a.id.date >= :startDate and a.id.date <= :endDate and a.shift.doctor.id = :doctorId", AppointmentNew.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .setParameter("doctorId", doctorId)
                .getResultList();
        for (AppointmentNew appointment : toRemove) {
            em.remove(appointment);
        }
        em.flush();
        em.clear();
    }

    private List<AppointmentNew> getAvailableTurnsByShift(DoctorSingleShift dss, List<AppointmentNew> takenAppointments, LocalDate date) {
        final List<AppointmentNew> availableTurns = new ArrayList<>();

        // iterator of the taken appointments
        Iterator<AppointmentNew> it = takenAppointments.iterator();
        AppointmentNew currentAppointment = it.hasNext() ? it.next() : null;

        LocalTime startTime = dss.getStartTime();
        if (date.equals(LocalDate.now()) && dss.getEndTime().isBefore(LocalTime.now())) {
            return Collections.emptyList();
        }

        while (startTime.isBefore(dss.getEndTime())) {
            LocalTime endTime = startTime.plusMinutes(dss.getDuration());
            if(endTime.isAfter(dss.getEndTime())) break;
            boolean isAvailable = true;
            if(date.equals(LocalDate.now()) && startTime.isBefore(LocalTime.now())) {
                isAvailable = false;
            }

            while(currentAppointment != null && isAvailable) {
                if (currentAppointment.getId().getEndTime().isBefore(startTime)) {
                    // If the current appointment ends before the start time of the available turn, we can skip it
                } else if(currentAppointment.getId().getStartTime().equals(startTime)) {
                    isAvailable = false;
                } else {
                    break;
                }
                currentAppointment = it.hasNext() ? it.next() : null;
            }
            if (isAvailable) {
                availableTurns.add(new AppointmentNew(dss, null, date, startTime, endTime, null));
            }
            startTime = endTime; // Move to the next time slot
        }
        return availableTurns.isEmpty() ? Collections.emptyList() : availableTurns;
    }

    @Override
    public List<AppointmentNew> getAvailableTurnsByDoctorByDate(Doctor doctor, LocalDate date) {
        if (doctor == null || date == null) {
            return Collections.emptyList();
        }
        if (date.isBefore(LocalDate.now())) {
            return Collections.emptyList();
        }
        if (date.isEqual(LocalDate.now()) && LocalTime.now().isAfter(LocalTime.of(23, 59))) {
            return Collections.emptyList();
        }
        if (doctor.getActiveSingleShifts() == null || doctor.getActiveSingleShifts().isEmpty()) {
            return Collections.emptyList();
        }

        final List<DoctorVacation> vacations = em.createQuery(
            """
                FROM DoctorVacation dv 
                WHERE dv.doctor = :doctor 
                AND dv.id.startDate <= :date 
                AND dv.id.endDate >= :date
            """, 
        DoctorVacation.class
        ).setParameter("doctor", doctor).setParameter("date", date).getResultList();
        
        if (vacations != null && !vacations.isEmpty()) {
            return Collections.emptyList();
        }

        WeekdayEnum weekday = WeekdayEnum.fromString(date.getDayOfWeek().name());

        final List<DoctorSingleShift> shiftsList = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
                AND dss.isActive = true
                AND :weekday = dss.weekday
            """, 
            DoctorSingleShift.class)
                .setParameter("doctor", doctor)
                .setParameter("weekday", weekday)
                .getResultList();

        if (shiftsList.isEmpty()) {
            return Collections.emptyList();
        }

        DoctorSingleShift dss = shiftsList.get(0);

        final List<AppointmentNew> takenAppointments = em.createQuery(
            """
                FROM AppointmentNew a 
                WHERE a.id.date = :date 
                AND a.shift.doctor.id = :doctorId
                ORDER BY a.id.startTime ASC
            """,
            AppointmentNew.class)
                .setParameter("date", date)
                .setParameter("doctorId", doctor.getId())
                .getResultList();

        return getAvailableTurnsByShift(dss, takenAppointments, date);
    }
}
