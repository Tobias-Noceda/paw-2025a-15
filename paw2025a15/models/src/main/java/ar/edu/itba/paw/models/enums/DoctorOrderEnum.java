package ar.edu.itba.paw.models.enums;

public enum DoctorOrderEnum {
    M_RECENT("mostRecent"),
    L_RECENT("leastRecent"),
    M_POPULAR("mostPopular"),
    L_POPULAR("leastPopular");

    private final String name;

    DoctorOrderEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
