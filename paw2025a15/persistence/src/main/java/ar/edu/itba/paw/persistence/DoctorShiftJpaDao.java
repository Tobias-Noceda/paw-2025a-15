package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Repository
public class DoctorShiftJpaDao implements DoctorShiftDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public DoctorSingleShift create(Doctor doctor, WeekdayEnum weekday, String address, LocalTime startTime,
            LocalTime endTime, int slot) {
        final DoctorSingleShift ds = new DoctorSingleShift(doctor, weekday, address, startTime, endTime, slot);
        em.persist(ds);
        return ds;
    }

    @Override
    public Optional<DoctorSingleShift> getShiftById(long id) {
        DoctorSingleShift shift = em.find(DoctorSingleShift.class, id);
        if (shift == null) {
            return Optional.empty();
        }
        return Optional.of(shift);
    }

    @Override
    public void doctorSetShifts(Doctor doctor, List<DoctorSingleShift> shifts) {
        for (DoctorSingleShift shift : shifts) {
            em.persist(shift);
        }
        doctor.setSingleShifts(shifts);
        em.merge(doctor);
    }

    private List<AvailableTurn> getAvailableTurnsByShift(DoctorSingleShift dss, List<AppointmentNew> takenAppointments, LocalDate date) {
        final List<AvailableTurn> availableTurns = new ArrayList<>();

        // iterator of the taken appointments
        Iterator<AppointmentNew> it = takenAppointments.iterator();
        AppointmentNew currentAppointment = it.hasNext() ? it.next() : null;

        LocalTime startTime = dss.getStartTime();

        while (startTime.isBefore(dss.getEndTime())) {
            LocalTime endTime = startTime.plusMinutes(dss.getDuration());
            if(endTime.isAfter(dss.getEndTime())) break;
            AvailableTurn availableTurn = new AvailableTurn(
                date,
                startTime,
                endTime,
                dss.getAddress(),
                dss.getId()
            );
            boolean isAvailable = true;

            while(currentAppointment != null) {
                if (currentAppointment.getId().getEndTime().isBefore(startTime)) {
                    // If the current appointment ends before the start time of the available turn, we can skip it
                    currentAppointment = it.hasNext() ? it.next() : null;
                } else if(currentAppointment.getId().getStartTime().equals(startTime)) {
                    isAvailable = false;
                    break;
                } else {
                    break;
                }
            }
            if (isAvailable) {
                availableTurns.add(availableTurn);
            }
            startTime = endTime; // Move to the next time slot
        }
        return availableTurns.isEmpty() ? Collections.emptyList() : availableTurns;
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorByDate(Doctor doctor, LocalDate date) {
        WeekdayEnum weekday = WeekdayEnum.fromString(date.getDayOfWeek().name());

        final List<DoctorSingleShift> shiftsList = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
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
                AND a.id.shiftId = :shiftId 
                ORDER BY a.id.startTime
            """,
            AppointmentNew.class)
                .setParameter("date", date)
                .setParameter("shiftId", dss.getId())
                .getResultList();

        return getAvailableTurnsByShift(dss, takenAppointments, date);
    }    
}
