package ar.edu.itba.paw.models;

public enum StudyTypeEnum {

    OTHER("Other"),
    PRESCRIPTION("Prescription"),
    RADIOGRAPHY("Radiography"),
    LABORATORY_RESULT("Laboratory result"),
    ULTRASOUND("Ultrasound"),
    MRI("MRI"),
    ELECTROCARDIOGRAM("Electrocardiogram"),
    VACCINATION_RECORD("Vaccination record");

    private final String displayName;

    StudyTypeEnum(String displayName){
        this.displayName = displayName;
    }

    public String getdisplayName(){
        return displayName;
    }

    public static StudyTypeEnum fromInt(int num) {
        StudyTypeEnum[] values = StudyTypeEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }
}
