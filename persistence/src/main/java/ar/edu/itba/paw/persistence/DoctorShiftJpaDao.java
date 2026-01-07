package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

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
        if (doctor == null || shifts == null || shifts.isEmpty()) return;
        
        List<DoctorSingleShift> managedShifts = new ArrayList<>();
        
        for(DoctorSingleShift shift : shifts) {
            managedShifts.add(em.merge(shift));
        }

        doctor.setSingleShifts(managedShifts);
        em.merge(doctor);
    }

    @Override
    public void updateShifts(long doctorId, List<DoctorSingleShift> newShifts) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if (doctor == null || newShifts == null || newShifts.isEmpty()) return;

        List<DoctorSingleShift> shiftsToAdd = new ArrayList<>(newShifts);
        // setAll existing shifts' isActive to false
        for (DoctorSingleShift shift : doctor.getSingleShifts()) {
            if (newShifts.contains(shift)) {
                // If the shift is in the new shifts, we keep it active
                shift.setIsActive(true);
                shiftsToAdd.remove(shift); // Remove it from the list of shifts to add
            } else {
                // Otherwise, we deactivate it
                shift.setIsActive(false);
            }
        }
        
        // Add new shifts
        for (DoctorSingleShift shift : shiftsToAdd) {
            shift.setIsActive(true);
            shift.setDoctor(doctor);
            em.persist(shift);
            doctor.addSingleShift(shift);
        }
        
        em.merge(doctor);
    }

    private List<AvailableTurn> getAvailableTurnsByShift(DoctorSingleShift dss, List<AppointmentNew> takenAppointments, LocalDate date) {
        final List<AvailableTurn> availableTurns = new ArrayList<>();

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
            AvailableTurn availableTurn = new AvailableTurn(
                date,
                startTime,
                endTime,
                dss.getAddress(),
                dss.getId()
            );
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
                availableTurns.add(availableTurn);
            }
            startTime = endTime; // Move to the next time slot
        }
        return availableTurns.isEmpty() ? Collections.emptyList() : availableTurns;
    }

    @Override
    public List<DoctorSingleShift> getActiveShiftsByDoctor(Doctor doctor) {
        if (doctor == null) {
            return Collections.emptyList();
        }
        final List<DoctorSingleShift> activeShifts = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
                AND dss.isActive = true
            """, 
            DoctorSingleShift.class)
                .setParameter("doctor", doctor)
                .getResultList();
        return activeShifts.isEmpty() ? Collections.emptyList() : activeShifts;
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorByDate(Doctor doctor, LocalDate date) {
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
