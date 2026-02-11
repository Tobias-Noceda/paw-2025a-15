package ar.edu.itba.paw.persistence;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
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
            DoctorSingleShift managedShift = em.merge(shift);
            doctor.addSingleShift(managedShift);
        }
        
        em.merge(doctor);
    }

    @Override
    public List<DoctorSingleShift> getActiveShiftsByDoctorId(long doctorId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        TypedQuery<DoctorSingleShift> query = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
                AND dss.isActive = true
            """, 
            DoctorSingleShift.class);
        query.setParameter("doctor", doctor);
        return query.getResultList();
    }

    @Override
    public List<DoctorSingleShift> getActiveShiftsByDoctorIdPage(long doctorId, int page, int pageSize) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null ||page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        TypedQuery<DoctorSingleShift> query = em.createQuery(
            """
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
                AND dss.isActive = true
            """, 
            DoctorSingleShift.class);
        query.setParameter("doctor", doctor);
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int getActiveShiftsByDoctorIdCount(long doctorId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null) return 0;
        TypedQuery<Long> query = em.createQuery(
            """
                SELECT count(dss)
                FROM DoctorSingleShift dss 
                WHERE dss.doctor = :doctor 
                AND dss.isActive = true
            """, 
            Long.class);
        query.setParameter("doctor", doctor);
        return query.getSingleResult().intValue();
    }
}
