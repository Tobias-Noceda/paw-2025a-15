package ar.edu.itba.paw.form;

import ar.edu.itba.paw.form.constraints.AvailableTurn;

@AvailableTurn
public class TakeTurnForm {
    private int shiftId;
    private String date;

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
}