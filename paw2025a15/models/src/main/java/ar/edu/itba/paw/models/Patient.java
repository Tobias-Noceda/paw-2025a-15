package ar.edu.itba.paw.models;

public class Patient {

    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private final long pictureId;

    public Patient(long id, String email, String password, String name, long pictureId){
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
        this.pictureId = pictureId;
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

    public long getPictureId(){
        return pictureId;
    }
}