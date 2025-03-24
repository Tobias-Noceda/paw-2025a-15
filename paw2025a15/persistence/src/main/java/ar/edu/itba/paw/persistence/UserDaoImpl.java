package ar.edu.itba.paw.persistence;

import org.springframework.stereotype.Repository;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.models.User;

@Repository
public class UserDaoImpl implements UserDao{

    @Override
    public User create(final String email, final String password) {
        return new User(email, password);
    }

}
