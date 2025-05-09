package ar.edu.itba.paw.models.enums;

public enum AccessLevelEnum {
    VIEW_BASIC("Only access to necessary user data"),
    VIEW_MEDICAL("Access to medical details: conditions, meds, allergies"),
    VIEW_HABITS("Access to habits details: smokes, drinks, diet"),
    VIEW_SOCIAL("Access to social details: hobbies, job");

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
        throw new IllegalArgumentException("Number out of access range");
    }
}
