package ar.edu.itba.paw.models.enums;

public enum MonthEnum {

    JANUARY("January"),
    FEBRUARY("February"),
    MARCH("March"),
    APRIL("April"),
    JUNE("June"),
    JULY("July"),
    AUGUST("August"),
    SEPTEMBER("September"),
    OCTOBER("October"),
    NOVEMBER("November"),
    DECEMBER("December");

    private final String name;

    MonthEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MonthEnum fromInt(int num) {
        MonthEnum[] values = MonthEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of month range");
    }
}
