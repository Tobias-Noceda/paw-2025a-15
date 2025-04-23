package ar.edu.itba.paw.models;

public enum UserRoleEnum {
    PATIENT("Patient"),
    DOCTOR("Doctor"),
    LABORATORY("Laboratory"),
    ADMIN("Admin");

    private final String displayName;

    UserRoleEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRoleEnum fromInt(int num) {
        UserRoleEnum[] values = UserRoleEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }
}
