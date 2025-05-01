package ar.edu.itba.paw.models;

import java.util.Arrays;

public class File {

    private final long id;
    private final byte[] content;
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
