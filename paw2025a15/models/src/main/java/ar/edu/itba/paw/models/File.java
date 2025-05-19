package ar.edu.itba.paw.models;

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
    private final long id;
    @Column(name = "file_content")
    private final byte[] content;
    @Column( name = "file_type")
    private final FileTypeEnum type;

    public File(long id, byte[] content, FileTypeEnum type){
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

    public FileTypeEnum getType(){
        return type;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof File)) return false;

        File o = (File) other;

        return (this.id==o.id) && (this.type.equals(o.type)) && Arrays.equals(this.content,o.content);
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
            ", content=" + content +
            '}';
    }
}
