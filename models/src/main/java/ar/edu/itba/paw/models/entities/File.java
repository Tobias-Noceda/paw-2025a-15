package ar.edu.itba.paw.models.entities;

import java.util.Arrays;

import ar.edu.itba.paw.models.enums.FileTypeEnum;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class File{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "files_file_id_seq")
    @SequenceGenerator(sequenceName = "files_file_id_seq", name = "files_file_id_seq", allocationSize = 1)
    @Column( name = "file_id")
    private Long id;

    @Column(name = "file_content", nullable = false)
    private byte[] content;

    @Enumerated(EnumType.ORDINAL)
    @Column( name = "file_type", nullable = false)
    private FileTypeEnum type;

    public File(){
        //just for hibernate
    }

    public File(byte[] content, FileTypeEnum type){
        this.content = content;
        this.type = type;
    }

    public byte[] getContent(){
        return content;
    }

    public void setContent(byte[] content){
        this.content = content;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public FileTypeEnum getType(){
        return type;
    }

    public void setType(FileTypeEnum type){
        this.type = type;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof File)) return false;

        File o = (File) other;

        return (this.id.equals(o.id));
    }


    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + Arrays.hashCode(content);
        result = 31 * result + type.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "File{" +
            "id=" + id +
            ", type=" + type +
            ", content=" + Arrays.toString(content) +
            '}';
    }
}
