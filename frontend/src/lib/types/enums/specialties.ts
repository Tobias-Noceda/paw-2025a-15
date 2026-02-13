import { m } from "$lib/paraglide/messages";

export enum Specialty {
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

export const getSpecialtyLabel = (spec: Specialty) => {
    switch(spec) {
        case Specialty.BARIATRIC_SURGERY: return m['specialties.BARIATRIC_SURGERY']();
        case Specialty.BREAST_PATHOLOGY: return m['specialties.BREAST_PATHOLOGY']();
        case Specialty.CARDIOLOGY: return m['specialties.CARDIOLOGY']();
        case Specialty.DERMATOLOGY: return m['specialties.DERMATOLOGY']();
        case Specialty.ENDOCRINOLOGY: return m['specialties.ENDOCRINOLOGY']();
        case Specialty.GASTROENTEROLOGY: return m['specialties.GASTROENTEROLOGY']();
        case Specialty.GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY: return m['specialties.GYNECOLOGICAL_GENERAL_ENDOCRINOLOGY']();
        case Specialty.GYNECOLOGY: return m['specialties.GYNECOLOGY']();
        case Specialty.HEAD_AND_NECK_SURGERY: return m['specialties.HEAD_AND_NECK_SURGERY']();
        case Specialty.HEMATOLOGY: return m['specialties.HEMATOLOGY']();
        case Specialty.INFECTIOUS_DISEASES: return m['specialties.INFECTIOUS_DISEASES']();
        case Specialty.INTERNAL_MEDICINE: return m['specialties.INTERNAL_MEDICINE']();
        case Specialty.PEDIATRIC_DERMATOLOGY: return m['specialties.PEDIATRIC_DERMATOLOGY']();
        case Specialty.PEDIATRIC_CARDIOLOGY: return m['specialties.PEDIATRIC_CARDIOLOGY']();
        case Specialty.PHLEBOLOGY: return m['specialties.PHLEBOLOGY']();
        case Specialty.PLASTIC_AND_RECONSTRUCTIVE_SURGERY: return m['specialties.PLASTIC_AND_RECONSTRUCTIVE_SURGERY']();
        case Specialty.NEPHROLOGY: return m['specialties.NEPHROLOGY']();
        case Specialty.NEUROLOGY: return m['specialties.NEUROLOGY']();
        case Specialty.NEUROSURGERY: return m['specialties.NEUROSURGERY']();
        case Specialty.NUTRITION: return m['specialties.NUTRITION']();
        case Specialty.ONCOLOGY: return m['specialties.ONCOLOGY']();
        case Specialty.OTOLARYNGOLOGY: return m['specialties.OTOLARYNGOLOGY']();
        case Specialty.PEDIATRICS: return m['specialties.PEDIATRICS']();
        case Specialty.PEDIATRIC_NUTRITION_AND_DIABETOLOGY: return m['specialties.PEDIATRIC_NUTRITION_AND_DIABETOLOGY']();
        case Specialty.PEDIATRIC_PULMONOLOGY: return m['specialties.PEDIATRIC_PULMONOLOGY']();
        case Specialty.PULMONOLOGY: return m['specialties.PULMONOLOGY']();
        case Specialty.OPHTHALMOLOGY: return m['specialties.OPHTHALMOLOGY']();
        case Specialty.PROCTOLOGY_AND_SURGERY: return m['specialties.PROCTOLOGY_AND_SURGERY']();
        case Specialty.RHEUMATOLOGY: return m['specialties.RHEUMATOLOGY']();
        case Specialty.SPEECH_THERAPY: return m['specialties.SPEECH_THERAPY']();
        case Specialty.SURGERY: return m['specialties.SURGERY']();
        case Specialty.TRAUMATOLOGY: return m['specialties.TRAUMATOLOGY']();
        case Specialty.UROLOGY: return m['specialties.UROLOGY']();
    }
};