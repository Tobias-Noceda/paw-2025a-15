package ar.edu.itba.paw.models;

public enum SpecialtyEnum {

    DERMATOLOGY("DERMATOLOGY"),
    CLINIC("CLINIC"),
    CARDIOLOGY("CARDIOLOGY"),
    PEDIATRICS("PEDIATRICS"),
    NEUROLOGY("NEUROLOGY"),
    ORTHOPEDICS("ORTHOPEDICS"),
    RADIOLOGY("RADIOLOGY"),
    PSYCHIATRY("PSYCHIATRY"),
    ONCOLOGY("ONCOLOGY");

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
