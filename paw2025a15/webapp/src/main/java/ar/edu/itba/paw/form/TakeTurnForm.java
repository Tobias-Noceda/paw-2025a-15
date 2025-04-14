package ar.edu.itba.paw.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import ar.edu.itba.paw.annotation.ValidArgPhone;
import ar.edu.itba.paw.form.constraints.AvailableTurn;
import ar.edu.itba.paw.form.constraints.ValidTurnStart;

@ValidTurnStart
@AvailableTurn
public class TakeTurnForm {
    @Size(min = 1, max = 50, message = "{form.name.size}")
    @NotEmpty(message = "{form.name.notEmpty}")
    private String name;

    @Size(min = 1, max = 50, message = "{form.surname.size}")
    @NotEmpty(message = "{form.surname.notEmpty}")
    private String surname;
    
    @Size(min = 1, max = 100, message = "{form.email.size}")
    @NotEmpty(message = "{form.email.notEmpty}")
    @Email(message = "{form.email.invalid}")
    private String email;

    @ValidArgPhone(message = "{form.phoneNumber.invalid}")
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

    public String getMonthDate() {
        return date.substring(8, 10);
    }
    
    public String getMonth() {
        return date.substring(5, 7);
    }

    public String getYear() {
        return date.substring(0, 4);
    }

    public String getTimeRange() {
        return timeRange;
    }

    public String getStartTime() {
        if (timeRange == null || !timeRange.contains("-")) {
            return null;
        }
        return timeRange.split("-")[0];
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }
}