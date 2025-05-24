package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.User;
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
        int[] results = new int[shifts.size()];
        for (int i = 0; i < shifts.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                DoctorShift shift = shifts.get(i);
                em.persist(shift);
                results[i] = 1;
                if (i % 50 == 0) {
                    em.flush();
                    em.clear();
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
        TypedQuery<DoctorShift> query = em.createQuery("from DoctorShift as ds where ds.id.doctorId = :doctorId",DoctorShift.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate,
            LocalDate endDate) {
        String query = "SELECT NEW AvailableTurn(gs.date, ds.id, ds.weekday, ds.startTime, ds.endTime, ds.address) " +
                    "FROM DoctorShift ds, " +
                    "FUNCTION('generate_series', :startDate, :endDate, INTERVAL '1 day') gs(date) " +
                    "WHERE ds.doctor.id = :doctorId " +
                    "AND (FUNCTION('EXTRACT', 'ISODOW', gs.date) - 1) = ds.weekday " +
                    "AND (gs.date + ds.startTime) >= CURRENT_TIMESTAMP " +
                    "AND NOT EXISTS ( " +
                    "   SELECT 1 FROM Appointment a " +
                    "   WHERE a.shift.id = ds.id " +
                    "   AND a.date = gs.date " +
                    ") " +
                    "ORDER BY gs.date, ds.startTime";

        return em.createQuery(query, AvailableTurn.class)
                            .setParameter("doctorId", doctorId)
                            .setParameter("startDate", startDate)
                            .setParameter("endDate", endDate)
                            .getResultList();
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
