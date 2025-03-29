package ar.edu.itba.paw.models;

public class File {

    private final long id;
    private final byte[] content;
    private final String type;

    public File(long id, byte[] content, String type){
        this.content = content;
        this.id = id;
        this.type = type;
    }

    public byte[] getContent(){
        return content;
    }

    public long getId(){
        return id;
    }

    public String getType(){
        return type;
    }
}
