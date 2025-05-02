package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

public class Study {

    private final long id;
    private final StudyTypeEnum type;
    private final String comment;
    private final long fileId;
    private final long userId;
    private final long uploaderId;
    private final LocalDateTime uploadDate;
    private final LocalDate studyDate;

    public Study(long id, StudyTypeEnum type, String comment, long fileId, long userId, long uploaderId, LocalDateTime uploadDate, LocalDate studyDate){
        this.id = id;
        this.type = type;
        this.comment = comment;
        this.fileId = fileId;
        this.userId = userId;
        this.uploaderId = uploaderId;
        this.uploadDate = uploadDate;
        this.studyDate = studyDate;
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

    public LocalDate getStudyDate(){
        return studyDate;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof Study)) return false;

        Study o = (Study) other;

        return (this.id==o.id) && (this.type.equals(o.type))
        && (this.comment.equals(o.comment)) && (this.fileId==o.fileId)
        && (this.userId==o.userId) && (this.uploaderId==o.uploaderId)
        && (this.uploadDate.equals(o.uploadDate)) && (this.studyDate.equals(o.studyDate));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + type.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + Long.hashCode(fileId);
        result = 31 * result + Long.hashCode(userId);
        result = 31 * result + Long.hashCode(uploaderId);
        result = 31 * result + uploadDate.hashCode();
        result = 31 * result + studyDate.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "File{" +
            "id=" + id +
            ", type=" + type +
            ", comment=" + comment +
            ", fileId=" + fileId +
            ", userId=" + userId +
            ", uploaderId=" + uploaderId +
            ", uploadDate=" + uploadDate +
            ", studyDate=" + studyDate +
            '}';
    }
}