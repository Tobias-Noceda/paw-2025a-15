package ar.edu.itba.paw.models;

public enum AccessLevelEnum {
    VIEW_RESTRICTED("Only access to necessary user data"),
    VIEW_BASIC("Basic access to the patient's age and blood type"),
    VIEW_MEDICAL("Access to medical details: conditions, meds, allergies"),
    VIEW_LIFESTYLE("Access to lifestyle details: diet, hobbies, job");

    private final String description;

    AccessLevelEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static AccessLevelEnum fromInt(int num) {
        AccessLevelEnum[] values = AccessLevelEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }
}
