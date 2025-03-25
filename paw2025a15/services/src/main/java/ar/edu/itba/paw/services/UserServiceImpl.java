package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;

@Service
public class UserServiceImpl implements UserService{

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(final UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    public User create(String email, String password) {
        return userDao.create(email, password);
    }

    @Override
    public Optional<User> findById(long id) {
        return userDao.findById(id);
    }

}
