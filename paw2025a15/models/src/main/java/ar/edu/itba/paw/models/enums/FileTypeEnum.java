package ar.edu.itba.paw.models.enums;

public enum FileTypeEnum {

    PNG("image/png"),
    JPEG("image/jpeg"),
    PDF("application/pdf");

    private final String name;

    FileTypeEnum(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static FileTypeEnum fromInt(int num){
        FileTypeEnum[] values = FileTypeEnum.values();
        if (num >= 0 && num < values.length){
            return values[num];
        }
        throw new IllegalArgumentException("Number out of range");
    }

    public static FileTypeEnum fromString(String str){
        for (FileTypeEnum type : FileTypeEnum.values()) {
            if (type.getName().equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsupported file type: " + str);
    }

}
