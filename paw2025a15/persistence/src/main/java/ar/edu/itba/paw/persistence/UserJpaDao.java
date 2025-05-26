package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, File picture, LocaleEnum locale) {
        final User user = new User(email, password, name, telephone, role, picture, LocalDate.now(),locale);
        em.persist(user);
        return user;
    }

    @Override
    public Optional<User> getUserById(long id){
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> getUserByEmail(String email){
        if(email == null || email.isEmpty()) return Optional.empty();
        final TypedQuery<User> query = em.createQuery("from User as u where u.email = :email",User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void changePasswordByID(long id, String password){
        User user = em.find(User.class, id);
        if(user == null || password == null || password.isEmpty()) return;
        user.setPassword(password);
        em.merge(user);
    }

    @Override
    public void editUser(long id, String name, String telephone, File picture) {
        User user = em.find(User.class, id);
        if( (user ==null || name == null || name.isEmpty()) || (telephone == null || telephone.isEmpty()) ) return;
        user.setName(name);
        user.setTelephone(telephone);
        File oldPicture = user.getPicture();
        boolean remove = false;
        if(!picture.equals(oldPicture)){
            user.setPicture(picture);
            remove = oldPicture.getId() != 1;
        }
        em.merge(user);
        if(remove) em.remove(oldPicture);
    }

    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        User user = em.find(User.class, userId);
        if(user == null || locale == null) return;
        user.setLocale(locale);
        em.merge(user);
    }

    @Override
    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        if(page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        TypedQuery<User> query = em.createQuery(
                "select distinct ad.patient from AuthDoctor as ad where ad.doctor.id = :doctorId ", User.class);
        query.setParameter("doctorId", doctorId);
        if(name != null && !name.trim().isEmpty()) {
            query = em.createQuery("select distinct ad.patient from AuthDoctor as ad where ad.doctor.id = :doctorId AND LOWER(ad.patient.name) LIKE :userName ", User.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        String baseQuery = " select count(distinct ad.patient.id) from AuthDoctor as ad  where ad.doctor.id = :doctorId ";
        if(name != null && !name.trim().isEmpty()) {
            baseQuery += " AND LOWER(ad.patient.name) LIKE :userName";
        }
        TypedQuery<Long> query = em.createQuery(baseQuery, Long.class);
        query.setParameter("doctorId", doctorId);
        if(name != null && !name.trim().isEmpty()) {
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        return query.getSingleResult().intValue();
    }

    private String sanitize(String name) {
        if (name == null) return null;
        return name
                .replace("\\", "\\\\\\")
                .replace("%", "\\\\%")
                .replace("_", "\\\\_")
                .trim()
                .toLowerCase();
    }
}
