package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

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
    public void edit(long id, String name, File picture) {
        Insurance insurance = em.find(Insurance.class, id);
        if(name == null || name.isEmpty()) return;
        insurance.setName(name);
        File oldPicture = insurance.getPicture();
        boolean remove = false;
        if(!picture.equals(oldPicture)){
            insurance.setPicture(picture);
            remove = oldPicture.getId() != 1;
        }
        em.merge(insurance);
        if(remove) em.remove(oldPicture);
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
    
}
