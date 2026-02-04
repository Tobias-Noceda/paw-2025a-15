import { get, getAuth, post } from "$modules/api.svelte";
import { baseApiUrl, type Insurance, type Paginated, type Patient } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";

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

    for (const patient of patients.results) {
        await populatePatientData(patient, fetchFn);
    }

    return patients;
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
            birthDate: patient.birthDate,
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
