package ar.edu.itba.paw.webapp.controller.Util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;

public class SelectItem {

    @Autowired
    private static MessageSource ms;

    private String value;
    private String label;

    // Constructor, getters y setters
    public SelectItem(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public static List<SelectItem> getListOfSpecialties(Locale locale) {
        final List<SelectItem> specialties = new ArrayList<>();
        for (SpecialtyEnum specialty : SpecialtyEnum.values()) {
            specialties.add(new SelectItem(specialty.name(), ms.getMessage("specialty." + specialty.name(), null, locale)));
        }
        return specialties;
    }

    public static List<SelectItem> getListOfWeekdays(Locale locale) {
        final List<SelectItem> weekdays = new ArrayList<>();
        for (WeekdayEnum weekday : WeekdayEnum.values()) {
            weekdays.add(new SelectItem(
                weekday.name(),
                ms.getMessage("weekday." + weekday.name(), null, locale)));
        }
        return weekdays;
    }

    public static List<SelectItem> getNextThreeMonths(Locale locale) {
        final List<SelectItem> months = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 3; i++) {
            LocalDate nextMonth = currentDate.plusMonths(i);
            months.add(new SelectItem(
                nextMonth.getMonth().name(),
                ms.getMessage("month." + nextMonth.getMonth().name(), null, locale) + " " + nextMonth.getYear()
            ));
        }
        return months;
    }

    public static List<SelectItem> getHoursSelectItems() {
        final List<SelectItem> times = new ArrayList<>();
        for(Integer hour = 6; hour < 22; hour++) {
            StringBuilder a = new StringBuilder();
            StringBuilder b = new StringBuilder();
            if (hour < 10) {
                a.append("0");
                b.append("0");
            }
            a.append(hour).append(":00");
            b.append(hour).append(":30");
            times.add(new SelectItem(a.toString(), a.toString()));
            times.add(new SelectItem((b.toString()), b.toString()));
        }
        return times;
    }
}
