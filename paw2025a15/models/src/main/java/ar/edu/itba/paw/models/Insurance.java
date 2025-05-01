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

    @Override
    public String toString() {
        return "Insurance{" +
            "id=" + id +
            ", name=" + name +
            ", pictureId=" + pictureId +
            '}';
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) return true;

        if(!(other instanceof Insurance)) return false;

        Insurance o = (Insurance) other;

        return (this.id==o.id) && (this.name.equals(o.name)) && (this.pictureId == o.pictureId);
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + Long.hashCode(pictureId);
        return result;
    }
}
