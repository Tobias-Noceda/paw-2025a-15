package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.User;

@Service
public class UserServiceImpl implements UserService{

    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(final UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    public User create(String email, String password, String name, long pictureId) {
        return userDao.create(email, password, name, pictureId);
    }

    @Override
    public User create(String email, String password, String name) {
        return userDao.create(email, password, name, 1);//TODO armar una funcion en fileService que traiga la picDefault de users
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userDao.getUserById(id);
    }

}
