package ar.edu.itba.paw.models.enums;

public enum WeekdayEnum {

    MONDAY("MONDAY"),
    TUESDAY("TUESDAY"),
    WEDNESDAY("WEDNESDAY"),
    THURSDAY("THURSDAY"),
    FRIDAY("FRIDAY"),
    SATURDAY("SATURDAY"),
    SUNDAY("SUNDAY");

    private final String name;

    WeekdayEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static WeekdayEnum fromInt(int num) {
        WeekdayEnum[] values = WeekdayEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }
}
