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
        TypedQuery<Insurance> query = em.createQuery(" SELECT dc.insurance from DoctorCoverage as dc where dc.doctor.id = :doctorId ", Insurance.class);
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }

    private void buildQueryByParams(StringBuilder queryBuilder, String name, SpecialtyEnum specialty, Insurance insurance,
            WeekdayEnum weekday, DoctorOrderEnum orderBy) {
        // Build the query based on the parameters provided
        if (name != null && !name.isEmpty()) {
            queryBuilder.append(" AND LOWER(d.name) LIKE :name");
        }
        if (specialty != null) {
            queryBuilder.append(" AND d.specialty = :specialty");
        }
        if (insurance != null) {
            queryBuilder.append(" AND :insurance MEMBER OF d.insurances");
        }
        if (weekday != null) {
            queryBuilder.append(" AND EXISTS (SELECT 1 FROM DoctorSingleShift dss WHERE dss.doctor = d AND dss.weekday = :weekday)");
        }
        if (orderBy != null) {
            switch (orderBy) {
                case M_RECENT -> queryBuilder.append(" ORDER BY d.createDate ASC");
                case L_RECENT -> queryBuilder.append(" ORDER BY d.createDate DESC");
                default -> {
                    // Imposible to filter by popularity in a query.
                    // If this is the case, the order will be done in Java
                }
            }
        }
    }

    private String sanitize(String name) {
        if (name == null) return null;
        return name
                .replace("\\", "\\\\\\")
                .replace("%", "\\\\%")
                .replace("_", "\\\\_")
                .replaceAll("\\s+", " ")
                .toLowerCase();
    }

    private void setQueryParameters(TypedQuery<Doctor> query, String name, SpecialtyEnum specialty, Insurance insurance,
            WeekdayEnum weekday) {
        if (name != null && !name.isEmpty()) {
            query.setParameter("name", "%" + sanitize(name) + "%");
        }
        if (specialty != null) {
            query.setParameter("specialty", specialty);
        }
        if (insurance != null) {
            query.setParameter("insurance", insurance);
        }
        if (weekday != null) {
            query.setParameter("weekday", weekday);
        }
    }

    private void sortByPopularity(List<Doctor> doctors, DoctorOrderEnum orderBy) {
        doctors.sort((d1, d2) -> {
            int count1 = em.createQuery(
                """
                    SELECT COUNT(a)
                    FROM AppointmentNew a
                    WHERE a.shift.doctor = :doctor1
                    AND a.shift.doctor <> a.patient
                """,
                Long.class
            ).setParameter("doctor1", d1).getSingleResult().intValue();
            int count2 = em.createQuery(
                """
                    SELECT COUNT(a)
                    FROM AppointmentNew a
                    WHERE a.shift.doctor = :doctor2
                    AND a.shift.doctor <> a.patient
                """,
                Long.class
            ).setParameter("doctor2", d2).getSingleResult().intValue();
            if(orderBy == DoctorOrderEnum.M_POPULAR) {
                // Sort by appointments count in descending order
                return Integer.compare(count2, count1);
            }
            // Sort by appointments count in ascending order
            return Integer.compare(count1, count2);
        });
    }

    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insurance,
            WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        // Select all the posible doctors for "Doctor" entity
        if (name == null && specialty == null && insurance == null && weekday == null) {
            return em.createQuery("SELECT d FROM Doctor d", Doctor.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
        // Build the query based on the parameters provided
        StringBuilder queryBuilder = new StringBuilder("SELECT d FROM Doctor d WHERE 1=1");
        buildQueryByParams(queryBuilder, name, specialty, insurance, weekday, orderBy);

        TypedQuery<Doctor> query = em.createQuery(queryBuilder.toString(), Doctor.class);
        setQueryParameters(query, name, specialty, insurance, weekday);

        List<Doctor> doctors = query.setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

        if(orderBy != null && (orderBy == DoctorOrderEnum.M_POPULAR || orderBy == DoctorOrderEnum.L_POPULAR)) {
            sortByPopularity(doctors, orderBy);
        }

        return doctors;
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
