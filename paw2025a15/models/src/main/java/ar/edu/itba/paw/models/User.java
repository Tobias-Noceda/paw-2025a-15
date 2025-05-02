package ar.edu.itba.paw.models;

import java.time.LocalDate;

import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;

public class User {

    private final long id;
    private final String email;
    private final String password;
    private final String name;
    private final String telephone;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", pictureId=" + pictureId +
                '}';
    }

    public long getId(){
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

}