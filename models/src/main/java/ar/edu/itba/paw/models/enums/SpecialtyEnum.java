package ar.edu.itba.paw.models.enums;

public enum SpecialtyEnum {

    BARIATRIC_SURGERY,
    BREAST_PATHOLOGY,
    CARDIOLOGY,
    DERMATOLOGY,
    ENDOCRINOLOGY,
    GASTROENTEROLOGY,
    GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY,
    GYNECOLOGY,
    HEAD_AND_NECK_SURGERY,
    HEMATOLOGY,
    INFECTIOUS_DISEASES,
    INTERNAL_MEDICINE,
    PEDIATRIC_DERMATOLOGY,
    PEDIATRIC_CARDIOLOGY,
    PHLEBOLOGY,
    PLASTIC_AND_RECONSTRUCTIVE_SURGERY,
    NEPHROLOGY,
    NEUROLOGY,
    NEUROSURGERY,
    NUTRITION,
    ONCOLOGY,
    OTOLARYNGOLOGY,
    PEDIATRICS,
    PEDIATRIC_NUTRITION_AND_DIABETOLOGY,
    PEDIATRIC_PULMONOLOGY,
    PULMONOLOGY,
    OPHTHALMOLOGY,
    PROCTOLOGY_AND_SURGERY,
    RHEUMATOLOGY,
    SPEECH_THERAPY,
    SURGERY,
    TRAUMATOLOGY,
    UROLOGY;

    public static SpecialtyEnum fromInt(int num) {
        SpecialtyEnum[] values = SpecialtyEnum.values();
        if (num >= 0 && num < values.length) {
            return values[num];
        }
        throw new IllegalArgumentException("Number out of SpecialtyEnum range");
    }
}
