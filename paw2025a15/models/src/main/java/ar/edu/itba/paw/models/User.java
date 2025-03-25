package ar.edu.itba.paw.models;

public class User {

    private final long id;
    private final String email;
    private String password;

    public User(final long id, final String email, final String password){
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }
}
