package ar.edu.itba.paw.models;

import java.util.Objects;

public class Insurance {
    private final long id;
    private String name;
    private long pictureId;

    public Insurance(long id, String name, long pictureId){
        this.id = id;
        this.name = name;
        this.pictureId = pictureId;
    }

    @Override
    public String toString() {
        return name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insurance insurance = (Insurance) o;
        return Objects.equals(name, insurance.name);
    }

}
