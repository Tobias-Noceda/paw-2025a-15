package ar.edu.itba.paw.models;

import java.time.LocalDateTime;

public class Study {

    private final long id;
    private final StudyTypeEnum type;
    private final String comment;
    private final long fileId;
    private final long userId;
    private final long uploaderId;
    private final LocalDateTime uploadDate;

    public Study(long id, StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate){
        this.id = id;
        this.type = type;
        this.comment = comment;
        this.fileId = fileId;
        this.userId = userId;
        this.uploaderId = uploaderId;
        this.uploadDate = uploadDate;
    }

    public long getId(){
        return id;
    }

    public StudyTypeEnum getType(){
        return type;
    }

    public String getComment(){
        return comment;
    }

    public long getFileId(){
        return fileId;
    }

    public long getUserId(){
        return userId;
    }

    public long getUploaderId(){
        return uploaderId;
    }

    public LocalDateTime getUploadDate(){
        return uploadDate;
    }
}