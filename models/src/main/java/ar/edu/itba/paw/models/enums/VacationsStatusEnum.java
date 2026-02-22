package ar.edu.itba.paw.models.enums;

public enum VacationsStatusEnum {
    COMPLETED("completed"),
    PROGRAMMED("programmed");

    private final String value;

    VacationsStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static VacationsStatusEnum fromValue(String value) {
        for (VacationsStatusEnum status : VacationsStatusEnum.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown VacationsStatusEnum value: " + value);
    }
}
