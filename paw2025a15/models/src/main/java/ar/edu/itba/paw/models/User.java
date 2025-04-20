package ar.edu.itba.paw.models;

public class User {

    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private final String telephone;
    private final UserRoleEnum role;
    private final long pictureId;

    public User(long id, String email, String password, String name, String telephone, UserRoleEnum role) {
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.pictureId = 1;
    }

    public User(long id, String email, String password, String name, String telephone, UserRoleEnum role, long pictureId) {
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.pictureId = pictureId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", pictureId=" + pictureId +
                '}';
    }

    public long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getTelephone(){
        return telephone;
    }

    public UserRoleEnum getRole(){
        return role;
    }

    public long getPictureId(){
        return pictureId;
    }
}