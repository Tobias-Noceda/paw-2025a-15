package ar.edu.itba.paw.models;

public class Study {

    private final long id;
    private final String type;
    private final long fileId;
    private final long patientId;

    public Study(long id, String type, long fileId, long patientId){
        this.id = id;
        this.type = type;
        this.fileId = fileId;
        this.patientId = patientId;
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

    public long getPatientId(){
        return patientId;
    }
}