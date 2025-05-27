package ar.edu.itba.paw.models.entities;

import java.time.LocalDate;
import java.util.Objects;

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

import javax.persistence.*;

@Entity
@Table(name = "users",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "user_email")}
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private long id;

    @Column(name = "user_email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "user_password", length = 100, nullable = false)
    private String password;

    @Column(name = "user_name", length = 100, nullable = false)
    private String name;

    @Column(name = "user_telephone", length = 20, nullable = false)
    private String telephone;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "user_role", nullable = false)
    private UserRoleEnum role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "picture_id", referencedColumnName = "file_id", nullable = false)
    private File picture;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "locale", nullable = false)
    private LocaleEnum locale;

    /*
    //Opcional TODO: esto esta raro capaz cambia si hacemos lo de herencia porque asi como esta es re antiintuitivo
    //Patient appointments
    @OneToMany(orphanRemoval = true, mappedBy = "patientId")
    @Column(insertable = true, updatable = true)
    private List<Appointment> appointments;

    //Doctor coverages
    @OneToMany(orphanRemoval = true, mappedBy = "doctorId")
    @Column(insertable = true, updatable = true)
    private List<DoctorCoverage> doctorCoverages;

    //Doctor detail
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "doctor_id")
    private DoctorDetail doctorDetail;
    */

    public User(){
        //just for hibernate;
    }

    public User(String email, String password, String name, String telephone, UserRoleEnum role, File picture, LocalDate createDate, LocaleEnum locale) {
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.picture = picture;
        this.createDate = createDate;
        this.locale = locale;
    }

    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getTelephone(){
        return telephone;
    }

    public void setTelephone(String telephone){
        this.telephone = telephone;
    }

    public UserRoleEnum getRole(){
        return role;
    }

    public void setRole(UserRoleEnum role){
        this.role = role;
    }

    public File getPicture(){
        return picture;
    }

    public void setPicture(File picture){
        this.picture = picture;
    }

    public LocalDate getCreateDate(){
        return createDate;
    }

    public void setCreateDate(LocalDate createDate){
        this.createDate = createDate;
    }

    public LocaleEnum getLocale(){
        return locale;
    }

    public void setLocale(LocaleEnum locale){
        this.locale = locale;
    }

    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof User)) return false;

        User o = (User) other;

        return Objects.equals(this.id, o.id)
        && (this.name.equals(o.name)) 
        && (this.email.equals(o.email))
        && (this.password.equals(o.password))
        && (this.telephone.equals(o.telephone))
        && (this.role.equals(o.role))
        && (this.picture.equals(o.picture))
        && (this.createDate.equals(o.createDate))
        && (this.locale.equals(o.locale));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + telephone.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + picture.hashCode();
        result = 31 * result + createDate.hashCode();
        result = 31 * result + locale.hashCode();
        return result;
    }

    @Override
    public String toString(){
        return "User{" +
            "id=" + id +
            ", name=" + name +
            ", email=" + email +
            ", password=" + password +
            ", telephone=" + telephone +
            ", role=" + role +
            ", picture=" + picture +
            ", createDate=" + createDate +
            ", locale=" + locale +
            '}';
    }

    /*
    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<DoctorCoverage> getDoctorCoverages() {
        return doctorCoverages;
    }

    public DoctorDetail getDoctorDetail() {
        return doctorDetail;
    }
        */
}