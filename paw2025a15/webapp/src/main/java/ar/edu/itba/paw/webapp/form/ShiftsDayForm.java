package ar.edu.itba.paw.webapp.form;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import java.time.LocalDate;

public class ShiftsDayForm {
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    public ShiftsDayForm() {
        this.date = LocalDate.now();
    }

    public boolean hasPrevious() {
        return LocalDate.now().isBefore(date);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ShiftsMonthForm{" +
                "date=" + date +
                '}';
    }

    public void decrementDate() {
        date = date.minusDays(1);
    }

    public void incrementDate() {
        date = date.plusDays(1);
    }
}
