package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
    public DoctorShift create(User doctor, WeekdayEnum weekday, String address, LocalTime startTime,
            LocalTime endTime) {
        final DoctorShift ds = new DoctorShift(doctor, weekday, address, startTime, endTime);
        em.persist(ds);
        return ds;
    }

    @Override
    public int[] batchCreate(List<DoctorShift> shifts) {
        int[] results = new int[shifts.size()];
        for (int i = 0; i < shifts.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                DoctorShift shift = shifts.get(i);
                User doctor = em.find(User.class, shift.getDoctor().getId());
                if(doctor == null) results[i] = 0;
                else{
                    shift.setDoctor(doctor);
                    em.merge(shift);
                    results[i] = 1;
                    if (i % 50 == 0) {
                        em.flush();
                        em.clear();
                    }
                }
            }
            catch(Exception e){
                results[i] = 0;
            }
        }
        return results;
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return Optional.ofNullable(em.find(DoctorShift.class, id));
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorId(long doctorId) {
        TypedQuery<DoctorShift> query = em.createQuery("from DoctorShift as ds where ds.doctor.id = :doctorId",DoctorShift.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
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
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date) {
        WeekdayEnum weekday = WeekdayEnum.fromString(date.getDayOfWeek().name());

        final List<DoctorSingleShift> shiftsList = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor.id = :doctorId 
                AND :weekday = dss.weekday
            """, 
            DoctorSingleShift.class)
                .setParameter("doctorId", doctorId)
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

    @Override
    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDateTime(long doctorId, WeekdayEnum weekday,
            LocalDate date, LocalTime time) {
        String queryString = "SELECT ds FROM DoctorShift ds WHERE ds.doctor.id = :doctorId AND ds.weekday = :weekday AND ds.startTime > :time AND NOT EXISTS (SELECT a FROM Appointment as a WHERE a.id.date = :date AND a.id.shiftId = ds.id)";
        TypedQuery<DoctorShift> query = em.createQuery(queryString,DoctorShift.class);
        query.setParameter("doctorId", doctorId);
        query.setParameter("weekday", weekday);
        query.setParameter("date", date);
        query.setParameter("time", time);
        return query.getResultList();
    }
    
}
