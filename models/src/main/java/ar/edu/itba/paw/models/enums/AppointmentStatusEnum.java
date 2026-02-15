package ar.edu.itba.paw.models.enums;

public enum AppointmentStatusEnum {
    FREE("free"),
    TAKEN("taken"),
    COMPLETED("completed");

    private final String displayName;

    AppointmentStatusEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AppointmentStatusEnum fromString(String status) {//TODO sacar tolower de los otros casos y otros enum
        for (AppointmentStatusEnum s : AppointmentStatusEnum.values()) {
            if (s.displayName.equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No enum constant for status: " + status);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
