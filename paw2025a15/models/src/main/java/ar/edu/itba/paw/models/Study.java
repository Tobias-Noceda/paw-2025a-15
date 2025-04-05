package ar.edu.itba.paw.models;

public class Study {

    private final long id;
    private final String type;
    private final long fileId;
    private final long userId;

    public Study(long id, String type, long fileId, long userId){
        this.id = id;
        this.type = type;
        this.fileId = fileId;
        this.userId = userId;
    }

    public long getId(){
        return id;
    }

    public String getType(){
        return type;
    }

    public long getFileId(){
        return fileId;
    }

    public long getUserId(){
        return userId;
    }
}