package ar.edu.itba.paw.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "insurances",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "insurance_name")}
)
public class Insurance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "insurances_insurance_id_seq")
    @SequenceGenerator(sequenceName = "insurances_insurance_id_seq", name = "insurances_insurance_id_seq", allocationSize = 1)
    @Column( name = "insurance_id")
    private Long id;

    @Column( name = "insurance_name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id", referencedColumnName = "file_id", nullable = false)
    private File picture;

    public Insurance(){
        //just for hibernate
    }

    public Insurance(String name, File picture){
        this.name = name;
        this.picture = picture;
    }

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public File getPicture(){
        return picture;
    }

    public void setPicture(File picture){
        this.picture = picture;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return "Insurance{" +
            "id=" + id +
            ", name=" + name +
            ", picture=" + picture +
            '}';
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) return true;

        if(!(other instanceof Insurance)) return false;

        Insurance o = (Insurance) other;

        return (this.id.equals(o.id)) && (this.name.equals(o.name));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + picture.hashCode();
        return result;
    }
}
