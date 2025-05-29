package ar.edu.itba.paw.webapp.controller.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ar.edu.itba.paw.models.enums.*;
import org.springframework.context.MessageSource;

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

    public static List<SelectItem> getStudyTypeSelectItems(MessageSource ms, Locale locale) {
        final List<SelectItem> studyTypes = new ArrayList<>();
        for(StudyTypeEnum studyType : StudyTypeEnum.values()) {
            studyTypes.add(new SelectItem(studyType.name(), ms.getMessage("studyType." + studyType.name(), null, locale)));
        }
        return studyTypes;
    }

    public static List<SelectItem> getDoctorOrderSelectItems(MessageSource ms, Locale locale) {
        final List<SelectItem> studyTypes = new ArrayList<>();
        for(DoctorOrderEnum order : DoctorOrderEnum.values()) {
            studyTypes.add(new SelectItem(order.name(), ms.getMessage("landing.order." + order.name(), null, locale)));
        }
        return studyTypes;
    }

    public static List<SelectItem> getLocalesSelectItems(MessageSource ms, Locale locale) {
        final List<SelectItem> locales = new ArrayList<>();
        for(LocaleEnum localeElement : LocaleEnum.values()) {
            locales.add(
                    new SelectItem(localeElement.name(), ms.getMessage("locale." + localeElement.name(), null, locale))
            );
        }
        return locales;
    }

}
