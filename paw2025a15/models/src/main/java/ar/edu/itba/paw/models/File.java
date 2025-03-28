package ar.edu.itba.paw.models;

public class File {
    private final long id;

    private final byte[] content;

    public File(long id, byte[] content){
        this.content = content;
        this.id = id;
    }

    public byte[] getContent(){
        return content;
    }

    public long getId(){
        return id;
    }
}
