package ar.edu.itba.paw.webapp.controller.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;

public class SelectItem {

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

    public static List<SelectItem> getListOfSpecialties(MessageSource ms, Locale locale) {
        final List<SelectItem> specialties = new ArrayList<>();
        for (SpecialtyEnum specialty : SpecialtyEnum.values()) {
            specialties.add(new SelectItem(specialty.name(), ms.getMessage("specialty." + specialty.name(), null, locale)));
        }
        return specialties;
    }

    public static List<SelectItem> getListOfWeekdays(MessageSource ms, Locale locale) {
        final List<SelectItem> weekdays = new ArrayList<>();
        for (WeekdayEnum weekday : WeekdayEnum.values()) {
            weekdays.add(new SelectItem(
                weekday.name(),
                ms.getMessage("weekday." + weekday.name(), null, locale)));
        }
        return weekdays;
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
