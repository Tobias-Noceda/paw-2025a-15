package ar.edu.itba.paw.form;

import java.time.LocalDate;
import java.time.Month;

public class ShiftsMonthForm {
    private Month month;

    public ShiftsMonthForm() {
        this.month = LocalDate.now().getMonth();
    }

    public ShiftsMonthForm(Month month, int year) {
        this.month = month;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public void setMonth(int month) {
        this.month = Month.of(month);
    }

    public void setMonth(String month) {
        this.month = Month.valueOf(month.toUpperCase());
    }

    @Override
    public String toString() {
        return "ShiftsMonthForm{" +
                "month=" + month +
                '}';
    }
}
