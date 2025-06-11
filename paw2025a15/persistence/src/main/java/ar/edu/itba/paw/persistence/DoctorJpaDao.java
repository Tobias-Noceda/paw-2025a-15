package ar.edu.itba.paw.persistence;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.DoctorDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Repository
public class DoctorJpaDao implements DoctorDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Doctor createDoctor(String email, String password, String name, String telephone, long pictureId, LocaleEnum locale, String licence, SpecialtyEnum specialty, List<Insurance> insurances) {
        File picture = em.find(File.class, pictureId);
        if(picture == null) return null;
        Doctor doctor = new Doctor(email, password, name, telephone, picture, LocalDate.now(), locale, licence, specialty, insurances);
        em.persist(doctor);
        return doctor;
    }

    @Override
    public void updateDoctor(long doctorId, String telephone, long pictureId, LocaleEnum mailLanguage, List<Insurance> insurances) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        File picture = em.find(File.class, pictureId);
        if(doctor ==null || picture == null || (telephone == null || telephone.isEmpty()) ) return;
        doctor.setTelephone(telephone);
        doctor.setLocale(mailLanguage);
        if (insurances == null) {
            insurances = Collections.emptyList();
        }
        doctor.setInsurances(insurances);
        File oldPicture = doctor.getPicture();
        boolean remove = false;
        if(picture.getId().equals(oldPicture.getId())){
            doctor.setPicture(picture);
            if(oldPicture.getId() != 1) remove = true;
        }
        em.merge(doctor);
        if(remove) em.remove(oldPicture);
    }

    @Override
    public Optional<Doctor> getDoctorById(long doctorId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        return doctor != null ? Optional.of(doctor) : Optional.empty();
    }

    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        Insurance insurance;
        if (insuranceId != null) {
            insurance = em.find(Insurance.class, insuranceId);
        } else {
            insurance = null;
        }
        if (name == null && specialty == null && insurance == null && weekday == null) {
            return em.createQuery("SELECT d FROM Doctor d", Doctor.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        }
        
        StringBuilder queryBuilder = new StringBuilder("SELECT d FROM Doctor d WHERE 1=1");
        buildQueryByParams(queryBuilder, name, specialty, insurance, weekday, orderBy);

        TypedQuery<Doctor> query = em.createQuery(queryBuilder.toString(), Doctor.class);
        setQueryParameters(query, name, specialty, insurance, weekday);

        List<Doctor> doctors = query.setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();

        if(orderBy != null && (orderBy.equals(DoctorOrderEnum.M_POPULAR) || orderBy.equals(DoctorOrderEnum.L_POPULAR))) {
            sortByPopularity(doctors, orderBy);
        }

        return doctors;
    }

    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday) {
        Insurance insurance;
        if (insuranceId != null) {
            insurance = em.find(Insurance.class, insuranceId);
        } else {
            insurance = null;
        }
        if (name == null && specialty == null && insurance == null && weekday == null) {
            return em.createQuery("SELECT COUNT(d) FROM Doctor d", Long.class).getSingleResult().intValue();
        }
        
        StringBuilder queryBuilder = new StringBuilder("SELECT COUNT(d) FROM Doctor d WHERE 1=1");
        buildQueryByParams(queryBuilder, name, specialty, insurance, weekday, null);

        TypedQuery<Long> query = em.createQuery(queryBuilder.toString(), Long.class);
        setQueryParameters(query, name, specialty, insurance, weekday);

        return query.getSingleResult().intValue();
    }

    @Override
    public List<Patient> searchAuthPatientsPageByDoctorAndName(long doctorId, String name, int page, int pageSize) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null ||page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        TypedQuery<Patient> query = em.createQuery(
                "select distinct ad.patient from AuthDoctor as ad where ad.doctor = :doctor ", Patient.class);
        query.setParameter("doctor", doctor);
        if(name != null && !name.trim().isEmpty()) {
            query = em.createQuery("select distinct ad.patient from AuthDoctor as ad where ad.doctor = :doctor AND LOWER(ad.patient.name) LIKE :userName ", Patient.class);
            query.setParameter("doctor", doctor);
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int searchAuthPatientsCountByDoctorAndName(long doctorId, String name) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null) return 0;
        String baseQuery = " select count(distinct ad.patient.id) from AuthDoctor as ad  where ad.doctor = :doctor ";
        if(name != null && !name.trim().isEmpty()) {
            baseQuery += " AND LOWER(ad.patient.name) LIKE :userName";
        }
        TypedQuery<Long> query = em.createQuery(baseQuery, Long.class);
        query.setParameter("doctor", doctor);
        if(name != null && !name.trim().isEmpty()) {
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        return query.getSingleResult().intValue();
    }

    @Override
    public boolean licenceExists(String licence) {
        if(licence == null) return false;
        return em.createQuery(
                "SELECT COUNT(d) FROM Doctor d WHERE d.licence = :licence", Long.class)
                .setParameter("licence", licence)
                .getSingleResult().intValue() > 0;
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

    private void setQueryParameters(TypedQuery<?> query, String name, SpecialtyEnum specialty, Insurance insurance,
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
            if(orderBy.equals(DoctorOrderEnum.M_POPULAR)) {
                // Sort by appointments count in descending order
                return Integer.compare(count2, count1);
            }
            // Sort by appointments count in ascending order
            return Integer.compare(count1, count2);
        });
    }

    @Override
    public void updateShifts(long doctorId, List<DoctorSingleShift> newShifts) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if (doctor == null || newShifts == null) return;

        // setAll existing shifts' isActive to false
        for (DoctorSingleShift shift : doctor.getSingleShifts()) {
            shift.setIsActive(false);
        }
        
        // Add new shifts
        for (DoctorSingleShift shift : newShifts) {
            shift.setDoctor(doctor);
            doctor.addSingleShift(shift);
        }
        
        em.merge(doctor);
    }

    @Override
    public DoctorVacation createDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if (doctor == null || startDate == null || endDate == null) return null;

        // Create a new DoctorVacation entity
        DoctorVacation vacation = new DoctorVacation(doctor, startDate, endDate);
        em.persist(vacation);
        return vacation;
    }

    @Override
    public void deleteDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if (doctor == null || startDate == null || endDate == null) return;

        // Find the DoctorVacation entity to delete
        DoctorVacation vacation = em.createQuery(
            """
                FROM DoctorVacation dv
                WHERE dv.doctor = :doctor
                AND dv.id.startDate = :startDate
                AND dv.id.endDate = :endDate
            """,
            DoctorVacation.class
        )
            .setParameter("doctor", doctor)
            .setParameter("startDate", startDate)
            .setParameter("endDate", endDate)
            .getSingleResult();

        if (vacation != null) {
            em.remove(vacation);
        }
    }
}
