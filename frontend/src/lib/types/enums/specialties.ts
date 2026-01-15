import { m } from "$lib/paraglide/messages";

export enum Specialties {
    BARIATRIC_SURGERY = "BARIATRIC_SURGERY",
    BREAST_PATHOLOGY = "BREAST_PATHOLOGY",
    CARDIOLOGY = "CARDIOLOGY",
    DERMATOLOGY = "DERMATOLOGY",
    ENDOCRINOLOGY = "ENDOCRINOLOGY",
    GASTROENTEROLOGY = "GASTROENTEROLOGY",
    GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY = "GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY",
    GYNECOLOGY = "GYNECOLOGY",
    HEAD_AND_NECK_SURGERY = "HEAD_AND_NECK_SURGERY",
    HEMATOLOGY = "HEMATOLOGY",
    INFECTIOUS_DISEASES = "INFECTIOUS_DISEASES",
    INTERNAL_MEDICINE = "INTERNAL_MEDICINE",
    PEDIATRIC_DERMATOLOGY = "PEDIATRIC_DERMATOLOGY",
    PEDIATRIC_CARDIOLOGY = "PEDIATRIC_CARDIOLOGY",
    PHLEBOLOGY = "PHLEBOLOGY",
    PLASTIC_AND_RECONSTRUCTIVE_SURGERY = "PLASTIC_AND_RECONSTRUCTIVE_SURGERY",
    NEPHROLOGY = "NEPHROLOGY",
    NEUROLOGY = "NEUROLOGY",
    NEUROSURGERY = "NEUROSURGERY",
    NUTRITION = "NUTRITION",
    ONCOLOGY = "ONCOLOGY",
    OTOLARYNGOLOGY = "OTOLARYNGOLOGY",
    PEDIATRICS = "PEDIATRICS",
    PEDIATRIC_NUTRITION_AND_DIABETOLOGY = "PEDIATRIC_NUTRITION_AND_DIABETOLOGY",
    PEDIATRIC_PULMONOLOGY = "PEDIATRIC_PULMONOLOGY",
    PULMONOLOGY = "PULMONOLOGY",
    OPHTHALMOLOGY = "OPHTHALMOLOGY",
    PROCTOLOGY_AND_SURGERY = "PROCTOLOGY_AND_SURGERY",
    RHEUMATOLOGY = "RHEUMATOLOGY",
    SPEECH_THERAPY = "SPEECH_THERAPY",
    SURGERY = "SURGERY",
    TRAUMATOLOGY = "TRAUMATOLOGY",
    UROLOGY = "UROLOGY"
};

export const getSpecialtyLabel = (spec: Specialties) => {
    switch(spec) {
        case Specialties.BARIATRIC_SURGERY: return m['specialties.BARIATRIC_SURGERY']();
        case Specialties.BREAST_PATHOLOGY: return m['specialties.BREAST_PATHOLOGY']();
        case Specialties.CARDIOLOGY: return m['specialties.CARDIOLOGY']();
        case Specialties.DERMATOLOGY: return m['specialties.DERMATOLOGY']();
        case Specialties.ENDOCRINOLOGY: return m['specialties.ENDOCRINOLOGY']();
        case Specialties.GASTROENTEROLOGY: return m['specialties.GASTROENTEROLOGY']();
        case Specialties.GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY: return m['specialties.GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY']();
        case Specialties.GYNECOLOGY: return m['specialties.GYNECOLOGY']();
        case Specialties.HEAD_AND_NECK_SURGERY: return m['specialties.HEAD_AND_NECK_SURGERY']();
        case Specialties.HEMATOLOGY: return m['specialties.HEMATOLOGY']();
        case Specialties.INFECTIOUS_DISEASES: return m['specialties.INFECTIOUS_DISEASES']();
        case Specialties.INTERNAL_MEDICINE: return m['specialties.INTERNAL_MEDICINE']();
        case Specialties.PEDIATRIC_DERMATOLOGY: return m['specialties.PEDIATRIC_DERMATOLOGY']();
        case Specialties.PEDIATRIC_CARDIOLOGY: return m['specialties.PEDIATRIC_CARDIOLOGY']();
        case Specialties.PHLEBOLOGY: return m['specialties.PHLEBOLOGY']();
        case Specialties.PLASTIC_AND_RECONSTRUCTIVE_SURGERY: return m['specialties.PLASTIC_AND_RECONSTRUCTIVE_SURGERY']();
        case Specialties.NEPHROLOGY: return m['specialties.NEPHROLOGY']();
        case Specialties.NEUROLOGY: return m['specialties.NEUROLOGY']();
        case Specialties.NEUROSURGERY: return m['specialties.NEUROSURGERY']();
        case Specialties.NUTRITION: return m['specialties.NUTRITION']();
        case Specialties.ONCOLOGY: return m['specialties.ONCOLOGY']();
        case Specialties.OTOLARYNGOLOGY: return m['specialties.OTOLARYNGOLOGY']();
        case Specialties.PEDIATRICS: return m['specialties.PEDIATRICS']();
        case Specialties.PEDIATRIC_NUTRITION_AND_DIABETOLOGY: return m['specialties.PEDIATRIC_NUTRITION_AND_DIABETOLOGY']();
        case Specialties.PEDIATRIC_PULMONOLOGY: return m['specialties.PEDIATRIC_PULMONOLOGY']();
        case Specialties.PULMONOLOGY: return m['specialties.PULMONOLOGY']();
        case Specialties.OPHTHALMOLOGY: return m['specialties.OPHTHALMOLOGY']();
        case Specialties.PROCTOLOGY_AND_SURGERY: return m['specialties.PROCTOLOGY_AND_SURGERY']();
        case Specialties.RHEUMATOLOGY: return m['specialties.RHEUMATOLOGY']();
        case Specialties.SPEECH_THERAPY: return m['specialties.SPEECH_THERAPY']();
        case Specialties.SURGERY: return m['specialties.SURGERY']();
        case Specialties.TRAUMATOLOGY: return m['specialties.TRAUMATOLOGY']();
        case Specialties.UROLOGY: return m['specialties.UROLOGY']();
    }
};