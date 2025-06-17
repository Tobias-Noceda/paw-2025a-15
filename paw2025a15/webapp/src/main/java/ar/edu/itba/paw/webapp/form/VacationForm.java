package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.constraints.ValidVacation;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@ValidVacation(message = "{form.vacation.invalid}")
public class VacationForm {

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
