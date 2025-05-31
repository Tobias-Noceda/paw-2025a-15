package ar.edu.itba.paw.models.entities;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "user_email")
    }
)
@DiscriminatorColumn(
    name = "user_role",
    discriminatorType = DiscriminatorType.INTEGER
)
public abstract class User implements Serializable {

    private static final long serialVersionUID = 1L;
    
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
    @Column(name = "user_role", insertable = false, updatable = false)
    private UserRoleEnum userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "picture_id", referencedColumnName = "file_id", nullable = false)
    private File picture;

    @Column(name = "create_date", nullable = false)
    private LocalDate createDate;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "locale", nullable = false)
    private LocaleEnum locale;

    public User(){
        //just for hibernate;
    }

    public User(String email, String password, String name, String telephone, UserRoleEnum role, File picture, LocalDate createDate, LocaleEnum locale) {
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.userRole = role;
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

    public UserRoleEnum getUserRole(){
        return userRole;
    }

    public void setUserRole(UserRoleEnum userRole){
        this.userRole = userRole;
    }

    public UserRoleEnum getRole(){
        return userRole;
    }

    public void setRole(UserRoleEnum role){
        this.userRole = role;
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
    public boolean equals(Object other) {
        if(this == other) return true;

        if(!(other instanceof User)) return false;

        User o = (User) other;

        return (this.id==o.id) && (this.email.equals(o.email));
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(id);
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + telephone.hashCode();
        result = 31 * result + userRole.hashCode();
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
            ", role=" + userRole +
            ", createDate=" + createDate +
            ", locale=" + locale +
            '}';
    }
}