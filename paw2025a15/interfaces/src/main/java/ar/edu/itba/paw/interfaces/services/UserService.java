package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.User;

public interface UserService {
    public User create(String email, String password, String name, long pictureId);
    
    public User create(String email, String password, String name);

    public Optional<User> getUserById(long id);
}
