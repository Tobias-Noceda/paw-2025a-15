package ar.edu.itba.paw.models;

public class User {

    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private long pictureId = 1;

    public User(long id, String email, String password, String name){
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
    }

    public User(long id, String email, String password, String name, long pictureId){
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
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

    public long getPictureId(){
        return pictureId;
    }
}