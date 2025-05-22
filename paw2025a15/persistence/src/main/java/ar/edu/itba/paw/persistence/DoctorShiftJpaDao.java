package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.LocalTime;
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'batchCreate'");
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return Optional.ofNullable(em.find(DoctorShift.class, id));
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorId(long doctorId) {
        TypedQuery<DoctorShift> query = em.createQuery("from DoctorShift as ds where ds.id.doctorId := doctorId",DoctorShift.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate,
            LocalDate endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableTurnsByDoctorIdBetweenDates'");
    }

    @Override
    public List<DoctorShift> getAvailableShiftsByDoctorIdWeekdayAndDateTime(long doctorId, WeekdayEnum weekday,
            LocalDate date, LocalTime time) {
        String queryString = "SELECT ds FROM DoctorShift ds WHERE ds.id.doctorId = :doctorId AND ds.weekday = :weekday AND ds.startTime > :time AND NOT EXISTS (SELECT a FROM Appointment as a WHERE a.date = :date AND a.id.shiftId = ds.id)";
        TypedQuery<DoctorShift> query = em.createQuery(queryString,DoctorShift.class);
        query.setParameter("doctorId", doctorId);
        query.setParameter("weekday", weekday.ordinal());
        query.setParameter("date", date);
        query.setParameter("time", time);
        return query.getResultList();
    }
    
}
