package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.constraints.PastDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.scheduling.annotation.Scheduled;

import javax.validation.constraints.Future;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.TimeUnit;

public class ShiftsWeekForm {
    private final Month month;
    private final int weekOfMonth;
    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;
    private LocalDate today;
    public int index = 0;

    public ShiftsWeekForm() {
        this.month = LocalDate.now().getMonth();
        final int monthdate = LocalDate.now().getDayOfMonth();
        this.weekOfMonth = switch (monthdate) {
            case 1, 2, 3, 4, 5, 6, 7 -> 0;
            case 8, 9, 10, 11, 12, 13, 14 -> 1;
            case 15, 16, 17, 18, 19, 20, 21 -> 2;
            case 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 -> 3;
            default -> -1;
        };
        this.today = LocalDate.now().plusDays(1);
    }

    public void setIndex(int index) {
        if(index < 0) {
            this.index = 0;
        } else if(index > 10) {
            this.index = 10;
        } else {
            this.index = index;
        }
    }

    public void addIndex(int index) {
        if(this.index + index < 0) {
            this.index = 0;
        } else if(this.index + index > 10) {
            this.index = 10;
        } else {
            this.index += index;
        }
    }

    public void incrementIndex() {
        if(this.index < 10) {
            this.index++;
        }
    }

    public void decrementIndex() {
        if(this.index > 0) {
            this.index--;
        }
    }

    public int getIndex() {
        return index;
    }

    public boolean hasPrevious() {
        return index > 0;
    }

    public boolean hasNext() {
        return index < 10;
    }

    public Month getMonth() {
        return month.plus((weekOfMonth + index) / 4);
    }

    public int getWeekOfMonth() {
        return (weekOfMonth + index) % 4;
    }

    private int getFirstDayOfWeek() {
        return switch (getWeekOfMonth()) {
            case 0 -> 1;
            case 1 -> 8;
            case 2 -> 15;
            case 3 -> 22;
            default -> -1;
        };
    }

    public LocalDate getStartDate() {
        return LocalDate.now().plusMonths((weekOfMonth + index) / 4).withDayOfMonth(getFirstDayOfWeek());
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
}
