package ar.edu.itba.paw.models;

import java.time.LocalDate;

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_email", length = 100, unique = true, nullable = false)
    private final String email;
    @Column(name = "user_password", length = 100, nullable = false)
    private final String password;
    @Column(name = "user_name", length = 100, nullable = false)
    private final String name;
    @Column(name = "user_telephone", length = 20, nullable = false)
    private final String telephone;
    @Column(name = "user_role")
    private final UserRoleEnum role;
    private final long pictureId;
    private final LocalDate createDate;
    private final LocaleEnum locale;

    public User(long id, String email, String password, String name, String telephone, UserRoleEnum role, LocalDate createDate, LocaleEnum locale) {
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.pictureId = 1;
        this.createDate = createDate;
        this.locale = locale;
    }

    public User(String email, String password, String name, String telephone, UserRoleEnum role, long pictureId, LocalDate createDate, LocaleEnum locale) {
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.pictureId = pictureId;
        this.createDate = createDate;
        this.locale = locale;
    }

    @Deprecated
    public User(long id, String email, String password, String name, String telephone, UserRoleEnum role, long pictureId, LocalDate createDate, LocaleEnum locale) {
        this.id = id;
        this. email = email;
        this.password = password;
        this.name= name;
        this.telephone = telephone;
        this.role = role;
        this.pictureId = pictureId;
        this.createDate = createDate;
        this.locale = locale;
    }

    public Long getId(){
        return id;
    }

    public String getEmail(){
        return email;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getTelephone(){
        return telephone;
    }

    public UserRoleEnum getRole(){
        return role;
    }

    public long getPictureId(){
        return pictureId;
    }

    public LocalDate getCreateDate(){
        return createDate;
    }

    public LocaleEnum getLocale(){
        return locale;
    }



    @Override
    public boolean equals(Object other){
        if(this == other) return true;

        if(!(other instanceof User)) return false;

        User o = (User) other;

        return (this.id==o.id) && (this.name.equals(o.name)) 
        && (this.email.equals(o.email)) && (this.password.equals(o.password))
        && (this.telephone.equals(o.telephone)) && (this.role.equals(o.role))
        && (this.pictureId == o.pictureId) && (this.createDate.equals(o.createDate))
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
        result = 31 * result + Long.hashCode(pictureId);
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
            ", pictureId=" + pictureId +
            ", createDate=" + createDate +
            ", locale=" + locale +
            '}';
    }

}