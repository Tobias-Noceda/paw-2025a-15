package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorCoverage;
import ar.edu.itba.paw.models.DoctorCoverageId;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

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
        User doctor = em.find(User.class, doctorId);
        Insurance insurance = em.find(Insurance.class, insuranceId);
        final DoctorCoverage dc = new DoctorCoverage(doctor, insurance);
        em.persist(dc);
    }

    @Override
    public int[] addDoctorCoverages(long doctorId, List<Long> insurancesIds) {
        User doctor = em.find(User.class, doctorId);
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
    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorsPageByParams'");
    }

    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId,
            WeekdayEnum weekday) {
//
//        TypedQuery<Integer> query = em.createQuery("select count(*) from DoctorDetail as dd", Integer.class);
//
//        List<Object> params = new ArrayList<>();
//        List<Integer> types = new ArrayList<>();
//        addFiltersToQuery(query, params, types, specialty, insurance, weekday);
//        if(name != null && !name.trim().isEmpty()) {
//            query.append(" AND LOWER(u.user_name) LIKE ? ");
//            params.add("%" + sanitize(name) + "%");
//            types.add(java.sql.Types.VARCHAR);
//        }

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDoctorsPageByParams'");

    }


    private void addFiltersToQuery(StringBuilder query, List<Object> params, List<Integer> types, SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday) {
        if(specialty != null){
            query.append(" AND dd.doctor_specialty = ? ");
            params.add(specialty.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
        if(insurance != null){
            query.append(" AND u.user_id IN (SELECT dc.doctor_id FROM doctor_coverages AS dc WHERE dc.insurance_id = ?)");
            params.add(insurance.getId());
            types.add(java.sql.Types.BIGINT);
        }
        if(weekday != null){
            query.append(" AND u.user_id IN (SELECT ds.doctor_id FROM doctor_shifts AS ds WHERE ds.shift_weekday = ?) ");
            params.add(weekday.ordinal());
            types.add(java.sql.Types.INTEGER);
        }
    }


}
