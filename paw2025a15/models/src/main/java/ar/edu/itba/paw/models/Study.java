package ar.edu.itba.paw.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ar.edu.itba.paw.models.enums.StudyTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "studies")
public class Study {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "studies_study_id_seq")
    @SequenceGenerator(sequenceName = "studies_study_id_seq", name = "studies_study_id_seq", allocationSize = 1)
    @Column( name = "study_id")
    private long id;

    @Enumerated
    @Column( name = "study_type", nullable = false)
    private StudyTypeEnum type;

    @Column( name = "study_comment", length = 100)
    private String comment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id", referencedColumnName = "file_id", nullable = false)
    private File file;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id", referencedColumnName = "user_id", nullable = false)
    private User uploader;

    @Column (name = "upload_date", nullable = false)
    private LocalDateTime uploadDate;

    @Column( name = "study_date", nullable = false)
    private LocalDate studyDate;

    public Study(StudyTypeEnum type, String comment, File file, User user, User uploader, LocalDateTime uploadDate, LocalDate studyDate){
        this.type = type;
        this.comment = comment;
        this.file = file;
        this.user = user;
        this.uploader = uploader;
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

    public File getFile(){
        return file;
    }

    public User getUser(){
        return user;
    }

    public User getUploader(){
        return uploader;
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
        && (this.comment.equals(o.comment)) && (this.file.equals(o.file))
        && (this.user.equals(o.user)) && (this.uploader.equals(o.uploader))
        && (this.uploadDate.equals(o.uploadDate)) && (this.studyDate.equals(o.studyDate));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + type.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + file.hashCode();
        result = 31 * result + user.hashCode();
        result = 31 * result + uploader.hashCode();
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
            ", file=" + file +
            ", user=" + user +
            ", uploader=" + uploader +
            ", uploadDate=" + uploadDate +
            ", studyDate=" + studyDate +
            '}';
    }
}