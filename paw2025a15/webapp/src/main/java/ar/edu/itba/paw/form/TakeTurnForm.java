package ar.edu.itba.paw.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TakeTurnForm {
    @Size(min = 1, max = 100)
    //@Pattern(regexp = "[a-zA-Z]+") TODO rethink logic
    private String name;

    @Size(min = 1, max = 100)
    //@Pattern(regexp = "[a-zA-Z]+")
    private String surname;
    
    @Email
    private String email;

    @Size(min = 11, max = 13)
    // between 2 and 4 digits, followed by a space, followed by 8 digits
    @Pattern(regexp = "\\d{2,4} \\d{8}")
    private String phoneNumber;

    private int shiftId;
    private String date;
    private String timeRange;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}