package ar.edu.itba.paw.models;

public enum SpecialtyEnum {

    DERMATOLOGY("Dermatology"),
    CLINIC("Clinic"),
    CARDIOLOGY("Cardiology"),
    PEDIATRICS("Pediatrics"),
    NEUROLOGY("Neurology"),
    ORTHOPEDICS("Orthopedics"),
    RADIOLOGY("Radiology"),
    PSYCHIATRY("Psychiatry"),
    ONCOLOGY("Oncology");

    private final String name;

    SpecialtyEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SpecialtyEnum fromInt(int num) {
        SpecialtyEnum[] values = SpecialtyEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }
}
