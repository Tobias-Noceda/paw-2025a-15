package ar.edu.itba.paw.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;

@Repository
public class InsuranceJpaDao implements InsuranceDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Insurance create(String name, File picture) {
        final Insurance insurance = new Insurance(name, picture);
        em.persist(insurance);
        return insurance;
    }

    @Override
    public Insurance edit(Insurance insurance, String name, File picture) {
        if(insurance==null) return insurance;
        if (name != null && !name.isEmpty()) {
            insurance.setName(name);
        }
        if (picture != null) {
            insurance.setPicture(picture);
        }
        return em.merge(insurance);
    }

    @Override
    public Optional<Insurance> getInsuranceById(long id) {
        return Optional.ofNullable(em.find(Insurance.class, id));
    }

    @Override
    public Optional<Insurance> getInsuranceByName(String name) {
        if(name == null || name.isEmpty()) return Optional.empty();
        final TypedQuery<Insurance> query = em.createQuery("from Insurance as i where i.name = :name",Insurance.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public int getInsurancesByDoctorIdCount(long doctorId) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null) return 0;
        final TypedQuery<Long> query = em.createQuery(
            "select count(i) from Doctor d join d.insurances i where d.id = :doctorId",
            Long.class
        );
        query.setParameter("doctorId", doctorId);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<Insurance> getInsurancesByDoctorIdPage(long doctorId, int page, int pageSize) {
        Doctor doctor = em.find(Doctor.class, doctorId);
        if(doctor==null ||page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        final TypedQuery<Insurance> query = em.createQuery(
            "select i from Doctor d join d.insurances i where d.id = :doctorId",
            Insurance.class
        );
        query.setParameter("doctorId", doctorId);

        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int searchInsurancesByNameCount(String name) {
        String baseQuery = "select count(i) from Insurance as i ";
        if(name != null && !name.trim().isEmpty()) {
            baseQuery += "where lower(i.name) like :name";
        }
        final TypedQuery<Long> query = em.createQuery(
            baseQuery,
            Long.class
        );
        if(name != null && !name.trim().isEmpty()) query.setParameter("name", "%" + sanitize(name) + "%");
        return query.getSingleResult().intValue();
    }

    @Override
    public List<Insurance> searchInsurancesByNamePage(String name, int page, int pageSize) {
        if (page < 1 || pageSize <= 0) return Collections.emptyList();
        String baseQuery = "from Insurance as i ";
        if(name != null && !name.trim().isEmpty()) {
            baseQuery += "where lower(i.name) like :name";
        }
        TypedQuery<Insurance> query = em.createQuery(
                    baseQuery,
                    Insurance.class
                );

        if(name != null && !name.trim().isEmpty()) query.setParameter("name", "%" + sanitize(name) + "%");
        query.setFirstResult(page == 0 ? 0 : (page - 1) * pageSize);
        query.setMaxResults(pageSize);
        
        return query.getResultList();
    }

    @Override
    public void delete(Insurance insurance) {
        if (insurance != null) {
            em.remove(em.contains(insurance) ? insurance : em.merge(insurance));
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
}
