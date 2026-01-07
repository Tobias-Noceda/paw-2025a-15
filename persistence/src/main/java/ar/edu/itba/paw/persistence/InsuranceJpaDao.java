package ar.edu.itba.paw.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
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
    public List<Insurance> getAllInsurances() {
        return em.createQuery("from Insurance as i",Insurance.class).getResultList();
    }

    @Override
    public List<Insurance> getInsurancesByDoctorId(long doctorId) {
        final TypedQuery<Insurance> query = em.createQuery(
            "select distinct i from Insurance i join i.doctors d where d.id = :doctorId",
            Insurance.class
        );
        query.setParameter("doctorId", doctorId);
        return query.getResultList();
    }
    
    @Override
    public int getInsurancesCount() {
        final TypedQuery<Long> query = em.createQuery("select count(i) from Insurance as i", Long.class);
        return query.getSingleResult().intValue();
    }

    @Override
    public List<Insurance> getInsurancesPage(int page, int pageSize) {
        if (page < 1 || pageSize <= 0) return Collections.emptyList();
        final TypedQuery<Insurance> query = em.createQuery("from Insurance as i", Insurance.class);
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
}
