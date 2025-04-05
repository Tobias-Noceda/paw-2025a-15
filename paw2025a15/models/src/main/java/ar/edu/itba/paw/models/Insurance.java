package ar.edu.itba.paw.models;

public class Insurance {
    private final long id;
    private String name;
    private long pictureId;

    public Insurance(long id, String name, long pictureId){
        this.id = id;
        this.name = name;
        this.pictureId = pictureId;
    }

    public long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public long getPictureId(){
        return pictureId;
    }

    public void setPictureId(long pictureId){
        this.pictureId = pictureId;
    }

    public void setName(String name){
        this.name = name;
    }
    
}
