import { get, getAuth, post } from "$modules/api.svelte";
import { baseApiUrl, type Insurance, type Paginated, type Patient } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import UriTemplate from "uri-templates";
import type { User } from "$stores/user";

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

    // for (const patient of patients.results) {
    //     await populatePatientData(patient, fetchFn);
    // }

    return patients;
};

export const fetchPatientById = async (id: number, loggedUser?: User | null, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    const response = await getAuth(`${baseApiUrl}/patients/${id}`, undefined, fetchFn);
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch patients: ' + text);
    }

    const patient: Patient = await response.json();

    await populatePatientData(patient, fetchFn);
    await populatePatientExtraData(patient, loggedUser, fetchFn);

    return setPatientsStudyLink(patient, loggedUser);
};

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
    );
    if (!response.ok) {
        throw new Error("Failed to create patient");
    }
};

const populatePatientData = async (patient: Patient, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    if (!patient.links.insurance) {
        return patient;
    }
    
    const response = await get(patient.links.insurance, undefined, fetchFn);

    if (response.ok) {
        const insurancesData: Insurance = await response.json();
        patient.insurance = insurancesData.name;
    }

    return patient;
};

const populatePatientExtraData = async (patient: Patient, loggedUser?: User | null, fetchFn: typeof fetch = fetch): Promise<Patient> => {
    let response;
    let data;

    // Fetch habits info
    if (patient.links.habitsInfo) {    
        response = await getAuth(patient.links.habitsInfo, undefined, fetchFn)
            .catch(() => null);

        if (response && response.ok) {
            patient.gaveHabits = true;
            data = await response.json();
            patient.drinks = data.drinks;
            patient.smokes = data.smokes;
            patient.diet = data.diet;
        }
    }

    // Fetch medical info
    if (patient.links.medicalInfo) {
        response = await getAuth(patient.links.medicalInfo, undefined, fetchFn)
            .catch(() => null);

        if (response && response.ok) {
            patient.gaveMedical = true;
            data = await response.json();
            patient.meds = data.meds;
            patient.conditions = data.conditions;
            patient.allergies = data.allergies;
        }
    }

    // Fetch social info
    if (patient.links.socialInfo) {
        response = await getAuth(patient.links.socialInfo, undefined, fetchFn)
            .catch(() => null);

        if (response && response.ok) {
            patient.gaveSocial = true;
            data = await response.json();
            patient.hobbies = data.hobbies;
            patient.job = data.job;
        }
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
