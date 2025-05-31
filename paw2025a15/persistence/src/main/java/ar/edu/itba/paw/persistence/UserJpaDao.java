package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.entities.User;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao {

    @PersistenceContext
    private EntityManager em;

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
}
