package ar.edu.itba.paw.models.enums;

public enum AppointmentStatus {
    Free("Free"),
    Booked("Booked");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static AppointmentStatus fromString(String status) {
        for (AppointmentStatus s : AppointmentStatus.values()) {
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
