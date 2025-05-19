package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import org.hibernate.Query;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.springframework.stereotype.Repository;
import org.w3c.dom.css.CSSStyleDeclaration;
//ver imports
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class UserHibernateDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public User create(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId, LocaleEnum locale) {
        final User user = new User(email, password, name, telephone, role, pictureId, LocalDate.now(),locale);
        em.persist(user);
        return user;
    }

    @Override
    public void editUser(long id, String name, String telephone, long pictureId) {
        final TypedQuery<User>  query = em.createQuery("update User u set u.name = :name, u.telephone = :telephone where u.id = :id", User.class );
        query.setParameter("name", name);
        query.setParameter("telephone", telephone);
        query.setParameter("id", id);
        query.executeUpdate();
    }

    @Override
    public void updateLocale(long userId, LocaleEnum locale) {
        final TypedQuery<User> query = em.createQuery("update User u set u.locale = :locale", User.class );
        query.setParameter("locale", locale);
        query.executeUpdate();
    }

    @Override
    public Optional<User> getUserById(long id){
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> getUserByEmail(String email){
        final TypedQuery<User> query = em.createQuery("from User as u where u.email = :email",User.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        if(page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        TypedQuery<User> query = em.createQuery(
        "select distinct u from AuthDoctor as ad join User as u on ad.patientId = u.id where ad.doctorId = :doctorId ", User.class);
        query.setParameter("doctorId", doctorId);
        if(name != null && !name.trim().isEmpty()) {
            query = em.createQuery("select distinct u from AuthDoctor as ad join User as u on ad.patientId = u.id where ad.doctorId = :doctorId AND LOWER(u.name) LIKE :userName ", User.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        query.setFirstResult(offset);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }

    @Override
    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        TypedQuery<Integer> query = em.createQuery(" select count(distinct ad.patientId) from AuthDoctor as ad join User as u on ad.patientId = u.id where ad.doctorId = :doctorId ", Integer.class);
        query.setParameter("doctorId", doctorId);
        if(name != null && !name.trim().isEmpty()) {
            query = em.createQuery("from AuthDoctor as ad join User as u on ad.patientId = u.id where ad.doctorId = :doctorId AND LOWER(u.name) LIKE :userName", Integer.class);
            query.setParameter("doctorId", doctorId);
            query.setParameter("userName","%" + sanitize(name) + "%");
        }
        return query.getSingleResult();
    }

    @Override
    public void changePasswordByID(long id, String password){
        final TypedQuery<User> query = em.createQuery("update User u set u.password = :password where u.id = :id", User.class);
        query.setParameter("password", password);
        query.setParameter("id", id);
        query.executeUpdate();
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
