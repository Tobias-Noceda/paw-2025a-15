package ar.edu.itba.paw.models;

public enum WeekdayEnum {

    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String name;

    WeekdayEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
