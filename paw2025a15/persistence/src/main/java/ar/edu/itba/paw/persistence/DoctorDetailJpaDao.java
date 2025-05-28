package ar.edu.itba.paw.persistence;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorCoverage;
import ar.edu.itba.paw.models.entities.DoctorCoverageId;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Repository
public class DoctorDetailJpaDao implements DoctorDetailDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public DoctorDetail create(User doctor, String licence, SpecialtyEnum specialty) {
        final DoctorDetail dd = new DoctorDetail(doctor, licence, specialty);
        em.persist(dd);
        return dd;
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return Optional.ofNullable(em.find(DoctorDetail.class, doctorId));
    }

    @Override
    public void addDoctorCoverage(long doctorId, long insuranceId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        Insurance insurance = em.find(Insurance.class, insuranceId);
        final DoctorCoverage dc = new DoctorCoverage(doctor, insurance);
        em.persist(dc);
    }

    @Override
    public int[] addDoctorCoverages(long doctorId, List<Long> insurancesIds) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        int[] results = new int[insurancesIds.size()];
        for (int i = 0; i < insurancesIds.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            try{
                DoctorCoverage dc = new DoctorCoverage(doctor, em.getReference(Insurance.class, insurancesIds.get(i)));
                em.persist(dc);
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
    public void removeAllCoveragesForDoctorId(long doctorId) {
        String q = "delete from DoctorCoverage as dc where dc.doctor.id = :doctorId";
        em.createQuery(q).setParameter("doctorId", doctorId).executeUpdate();
    }

    @Override
    public void removeDoctorCoverages(long doctorId, List<Long> toRemove) {
        User doctor = em.find(User.class, doctorId);
        if(doctor==null) return;
        for (int i = 0; i < toRemove.size(); i++) {//TODO: preguntar si no hay un batch, por lo que vi no pareciera haber
            DoctorCoverage dc = em.find(DoctorCoverage.class, new DoctorCoverageId(doctorId, toRemove.get(i)));
            if(dc!=null) em.remove(dc);
            if (i % 50 == 0) {
                em.flush();
                em.clear();
            }
        }
    }

    @Override
    public List<Insurance> getDoctorInsurancesById(long doctorId) {
        TypedQuery<Insurance> query = em.createQuery("from DoctorCoverage as dc where dc.doctor.id = :doctorId ", Insurance.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        // Select all the posible doctors for "Doctor" entity
        return em
            .createQuery("SELECT d FROM Doctor d", Doctor.class)
            .getResultList();
    }

    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday) {
                // get the total number of doctors
                return em
            .createQuery("SELECT d FROM Doctor d", Doctor.class)
            .getResultList().size();
    }
}
