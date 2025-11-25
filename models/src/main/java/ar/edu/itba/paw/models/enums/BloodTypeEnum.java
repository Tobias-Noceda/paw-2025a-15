package ar.edu.itba.paw.models.enums;

public enum BloodTypeEnum {

    O_POSITIVE("O+"),
    O_NEGATIVE("O-"),
    A_POSITIVE("A+"),
    A_NEGATIVE("A-"),
    B_POSITIVE("B+"),
    B_NEGATIVE("B-"),
    AB_POSITIVE("AB+"),
    AB_NEGATIVE("AB-");

    private final String name;

    BloodTypeEnum(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static BloodTypeEnum fromInt(int num){
        BloodTypeEnum[] values = BloodTypeEnum.values();
        if (num >= 0 && num < values.length){
            return values[num];
        }
        throw new IllegalArgumentException("Number out of BloodType range");
    }

    public static BloodTypeEnum fromString(String str){
        for (BloodTypeEnum type : BloodTypeEnum.values()) {
            if (type.getName().equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported blood type: " + str);
    }
}
