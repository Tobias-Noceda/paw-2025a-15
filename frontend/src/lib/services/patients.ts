import { get, getAuth, patchAuth, post, resolveNonTemplatedLinks } from "$modules/api.svelte";
import { baseApiUrl, type Insurance, type Paginated, type Patient } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import UriTemplate from "uri-templates";
import type { User } from "$stores/user";
import { createFile } from "./files";

export const fetchPatients = async (search: string, url: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Patient>> => {
    if (!url || url.trim() === '') throw error(400, 'URL is required to fetch patients');

    const urlObj = new URL(url);
    if (search.trim() !== '') {
        urlObj.searchParams.append('name', search.trim());
    }

    const response = await getAuth(urlObj.toString(), undefined, fetchFn);
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch patients: ' + text);
    }

    const patients: Paginated<Patient> = { _links: {}, results: [] };
    patients.results = await response.json();
    patients._links = getPaginationLinks(response);
    patients._pageInfo = getPageInfoFromHeaders(response);

    return patients;
};

export const fetchPatientById = async (id: number, loggedUser?: User | null, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    const response = await getAuth(
        `${baseApiUrl}/patients/${id}`,
        {
            headers: {
                'Accept': 'application/vnd.patients.v1+json'
            }
        },
        fetchFn
    );
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch patients: ' + text);
    }

    const patient: Patient = await response.json();
    
    await populatePatientData(patient, fetchFn);
    console.log('Fetched patient data:', patient);

    return setPatientsStudyLink(patient, loggedUser);
};

export const fetchPatientBySelf = async (selfUrl: string, loggedUser?: User | null, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    const response = await getAuth(
        selfUrl,
        {
            headers: {
                'Accept': 'application/vnd.patients.v1+json'
            }
        },
        fetchFn
    );
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch patients: ' + text);
    }

    const patient: Patient = await response.json();
    
    await populatePatientData(patient, fetchFn);

    return setPatientsStudyLink(patient, loggedUser);
}

export const createPatient = async (patient: Partial<Patient>, password: string): Promise<void> => {
    const response = await post(`${baseApiUrl}/patients`,
        {
            name: patient.name,
            email: patient.email,
            password: password,
            telephone: patient.telephone,
            height: patient.height,
            weight: patient.weight,
            birthdate: patient.birthdate,
        },
        {
            headers: {
                'Content-Type': 'application/vnd.patients.creation.v1+json'
            }
        },
        fetch
    ).catch(async (e) => {
        const text = await response.text();
        throw error(response.status || 500, text && text !== '' ? text : 'Failed to create patient');
    });
    
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, text && text !== '' ? text : 'Failed to create patient');
    }
};

export type PatientProfileUpdateData = {
    telephone: string;
    mailLanguage: string;
    birthdate: Date | null;
    bloodType?: string;
    height?: number;
    weight?: number;
    insuranceSelf?: string;
    insuranceNumber?: string;
};

export const updatePatientProfile = async (user: User, payload: PatientProfileUpdateData, patient: Patient, image?: File | null, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    if (!user || user.role !== 'PATIENT') {
        throw error(403, 'Unauthorized: Only patients can update their profile');
    }
    let imageLocation: string | undefined;

    if (image) {
        imageLocation = await createFile(image, fetchFn);
    }

    const finalPayload = {
        picture: imageLocation,
        telephone: payload.telephone.trim() !== '' && payload.telephone !== patient.telephone ? payload.telephone : undefined,
        mailLanguage: payload.mailLanguage !== user.language ? payload.mailLanguage : undefined,
        birthdate: payload.birthdate ? payload.birthdate.toISOString().split('T')[0] : undefined,
        bloodType: patient.bloodType !== payload.bloodType ? payload.bloodType : undefined,
        height: payload.height !== patient.height ? payload.height : undefined,
        weight: payload.weight !== patient.weight ? payload.weight : undefined,
        insurance: payload.insuranceSelf && payload.insuranceSelf !== patient.links.insurance?.resolved! ? payload.insuranceSelf : undefined,
        insuranceNumber: payload.insuranceNumber && payload.insuranceNumber.trim() !== '' && payload.insuranceNumber !== patient.insuranceNumber ? payload.insuranceNumber : undefined
    };


    const response = await patchAuth(
        user.self,
        finalPayload,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.v1+json',
                'Accept': 'application/vnd.patients.v1+json'
            }
        },
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to update patient: ' + text);
    }

    return response.json();
};

export const updatePatientHabitsInfo = async (patient: Patient, payload: { smokes?: boolean; drinks?: boolean; diet?: string }, fetchFn: typeof fetch = fetch): Promise<void> => {
    const response = await patchAuth(
        patient.links.habitsInfo.resolved!,
        payload,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.habitsInfo.v1+json',
                'Accept': 'application/vnd.patients.habitsInfo.v1+json'
            }
        },
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to update habits info: ' + text);
    }
};

export const updatePatientMedicalInfo = async (patient: Patient, payload: { meds?: string; conditions?: string; allergies?: string }, fetchFn: typeof fetch = fetch): Promise<void> => {
    const response = await patchAuth(
        patient.links.medicalInfo.resolved!,
        payload,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.medicalInfo.v1+json',
                'Accept': 'application/vnd.patients.medicalInfo.v1+json'
            }
        },
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to update medical info: ' + text);
    }
};

export const updatePatientSocialInfo = async (patient: Patient, payload: { hobbies?: string; job?: string }, fetchFn: typeof fetch = fetch): Promise<void> => {
    const response = await patchAuth(
        patient.links.socialInfo.resolved!,
        payload,
        {
            headers: {
                'Content-Type': 'application/vnd.patients.socialInfo.v1+json',
                'Accept': 'application/vnd.patients.socialInfo.v1+json'
            }
        },
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to update social info: ' + text);
    }
};

const populatePatientData = async (patient: Patient, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    resolveNonTemplatedLinks(patient);

    if (patient.links.insurance) {
        const insuranceResponse = await get(patient.links.insurance.resolved!, undefined, fetchFn);

        if (insuranceResponse.ok) {
            const insurancesData: Insurance = await insuranceResponse.json();
            patient.insurance = insurancesData.name;
        }
    }

    console.log('Patient data after fetching insurance:', patient);
    // Fetch habits info
    const habitsResponse = await getAuth(patient.links.habitsInfo.resolved!, undefined, fetchFn)
        .catch(() => null);
    if (habitsResponse && habitsResponse.ok) {
        patient.gaveHabits = true;
        const data = await habitsResponse.json();
        patient.drinks = data.drinks;
        patient.smokes = data.smokes;
        patient.diet = data.diet;
    }

    // Fetch medical info
    const medicalResponse = await getAuth(patient.links.medicalInfo.resolved!, undefined, fetchFn)
        .catch(() => null);

    if (medicalResponse && medicalResponse.ok) {
        patient.gaveMedical = true;
        const data = await medicalResponse.json();
        patient.meds = data.meds;
        patient.conditions = data.conditions;
        patient.allergies = data.allergies;
    }

    // Fetch social info
    const socialResponse = await getAuth(patient.links.socialInfo.resolved!, undefined, fetchFn)
        .catch(() => null);

    if (socialResponse && socialResponse.ok) {
        patient.gaveSocial = true;
        const data = await socialResponse.json();
        patient.hobbies = data.hobbies;
        patient.job = data.job;
    }

    return patient;
};

export const setPatientsStudyLink = (patient: Patient, loggedUser?: User | null): Patient => {
    if (patient.links.studies && patient.links.studies.templated) {
        if (loggedUser && loggedUser.role === 'DOCTOR' && loggedUser.id !== undefined) {
            const template = UriTemplate(patient.links.studies.href);
            const url = template.fill({ doctorId: loggedUser.id });
            patient.links.studies.resolved = url; // Store the resolved URL for filtering
        } else {
            patient.links.studies.resolved = patient.links.studies.href.split('{')[0]; // Remove the template part if user is not a doctor or not logged in
        }
    }

    return patient;
};
