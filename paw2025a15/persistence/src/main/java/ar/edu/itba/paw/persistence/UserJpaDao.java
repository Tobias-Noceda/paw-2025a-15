package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;
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
        if(password == null || password.isEmpty()) return;
        user.setPassword(password);
        em.merge(user);
    }

    @Override
    public void editUser(long id, String name, String telephone, File picture) {
        User user = em.find(User.class, id);
        if( (name == null || name.isEmpty()) || (telephone == null || telephone.isEmpty()) ) return;
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
        if(locale == null) return;
        user.setLocale(locale);
        em.merge(user);
    }

    @Override
    public List<User> searchAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize) {
        if(page <= 0 || pageSize <= 0) return Collections.emptyList();
        int offset = (page - 1) * pageSize;
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAuthPatientsPageByDoctorIdAndName'");
    }

    @Override
    public int searchAuthPatientsCountByDoctorIdAndName(long doctorId, String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchAuthPatientsCountByDoctorIdAndName'");
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
