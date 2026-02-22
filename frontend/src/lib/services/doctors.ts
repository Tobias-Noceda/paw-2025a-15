import type { Doctor, DoctorAuthorizations, Insurance, Paginated, Shift } from "$types/api";
import type { Weekdays } from "$types/enums/weekdays";
import { baseApiUrl } from "$types/api";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import { get, post, getAuth, postAuth, putAuth, patchAuth, deleteAuth, resolveNonTemplatedLinks } from "$modules/api.svelte";
import UriTemplate from "uri-templates";
import type { User } from "$stores/user";
import type { AccessLevels } from "$types/enums/accessLevels";
import { error } from "@sveltejs/kit";
import { fetchInsurancesPage } from "./insurances";
import { createFile } from "./files";

/**
 * Parse time string in HH:mm format and return a Date object
 */
function parseTime(timeStr: string): Date {
    const [hours, minutes] = timeStr.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes, 0, 0);
    return date;
}

function normalizeTime(timeStr: string): string {
    const [hours = "00", minutes = "00"] = timeStr.split(":");
    return `${hours.padStart(2, "0")}:${minutes.padStart(2, "0")}`;
}

export const fetchDoctors = async (
    name: string,
    insurance: string,
    day: string,
    specialty: string,
    order: string,
    fetchFn: typeof fetch = fetch,
): Promise<Paginated<Doctor>> => {
    
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        let url = new URL(`${baseApiUrl}/doctors`);
        if (insurance !== 'all') {
            url.searchParams.append('insurance', insurance);
        }
        if (day !== 'all') {
            url.searchParams.append('weekday', day);
        }
        if (specialty !== 'all') {
            url.searchParams.append('specialty', specialty);
        }
        if (order !== '') {
            url.searchParams.append('orderBy', order);
        }
        if (name.trim() !== '') {
            url.searchParams.append('name', name.trim());
        }

        const response = await get(url.toString(), undefined, fetchFn);
        if (response.ok) {
            doctors.results = await response.json();
            doctors._links = getPaginationLinks(response);
            doctors._pageInfo = getPageInfoFromHeaders(response);

            for (const doctor of doctors.results) {
                await populateDoctorData(doctor, fetchFn);
            }
        }
    } catch (error) {
        console.error('Failed to fetch doctors:', error);
    }

    return doctors;
};

export const fetchDoctorsPage = async (nextUrl: string, loggedUser?: User, fetchFn: typeof fetch = fetch): Promise<Paginated<Doctor>> => {
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        const response = await get(nextUrl, undefined, fetchFn);
        if (response.ok) {
            doctors.results = await response.json();
            doctors._links = getPaginationLinks(response);
            doctors._pageInfo = getPageInfoFromHeaders(response);
        }
        for (const doctor of doctors.results) {
            await populateDoctorData(doctor, fetchFn);
            if (loggedUser) {
                populateAuthorizationData(doctor, loggedUser);
            }
        }

        return doctors;
    } catch (error) {
        console.error('Failed to fetch next doctors:', error);
        return doctors;
    }
};

export const fetchDoctorById = async (id: string, loggedUser: User, fetchFn: typeof fetch = fetch): Promise<Doctor | null> => {
    const url = new URL(`${baseApiUrl}/doctors/${id}`);
    const response = await get(url.toString(), undefined, fetchFn);
    
    const doctor = await response.json();
    if (doctor) {
        await populateDoctorData(doctor, fetchFn);
        populateAuthorizationData(doctor, loggedUser);
    }
    
    return doctor;
};

export const fetchDoctorBySelf = async (selfUrl: string, loggedUser: User, fetchFn: typeof fetch = fetch): Promise<Doctor | null> => {
    const response = await get(selfUrl, undefined, fetchFn);
    
    if (!response.ok) {
        console.error('Failed to fetch doctor by self URL:', response.statusText);
        return null;
    }

    const doctor = await response.json();
    if (doctor) {
        await populateDoctorData(doctor, fetchFn);
        populateAuthorizationData(doctor, loggedUser);
    }
    
    return doctor;
};

export const fetchDoctorAuthorizations = async (doctor: Doctor, fetchFn: typeof fetch = fetch): Promise<DoctorAuthorizations | null> => {
    if (doctor.links.authorization.resolved) {
        const response = await getAuth(doctor.links.authorization.resolved, undefined, fetchFn);
        if (response.ok) {
            return await response.json();
        }
    }

    return null;
}

export const putAuthorizations = async (stringUrl: string, isAuthorized: boolean, accessLevels: AccessLevels[], fetchFn: typeof fetch = fetch): Promise<void> => {
    const url = new URL(stringUrl);
    if (url.searchParams.has('patientId')) {
        url.searchParams.delete('patientId'); // Remove patientId if present, as it's not needed for the PUT request
    }
    
    const response = await putAuth(url.toString(), {
        authorized: isAuthorized,
        accessLevels: accessLevels
    }, {
        headers: {
            'Content-Type': 'application/vnd.doctors.authorizations.v1+json'
        }
    }, fetchFn);

    if (!response.ok) {
        throw new Error("Failed to update authorizations");
    }
};

type ShiftCreationData = {
    startTime: string;
    endTime: string;
    duration: number;
    address: string;
    weekdays: Weekdays[];
};

export type DoctorProfileUpdateData = {
    telephone: string;
    mailLanguage: string;
    insuranceSelfs: string[];
    updateSchedule: boolean;
    keepTurns?: boolean;
    shifts?: ShiftCreationData | null;
};

export const createDoctor = async (
    doctorData: Partial<Doctor>,
    password: string,
    shifts?: ShiftCreationData
): Promise<void> => {
    const response = await post(`${baseApiUrl}/doctors`, {
            name: doctorData.name,
            email: doctorData.email,
            password: password,
            telephone: doctorData.telephone,
            license: doctorData.license,
            specialty: doctorData.specialty,
            insurances: doctorData.insurances || [],
            shifts: shifts
        },
        { 
            headers: {
                'Content-Type': 'application/vnd.doctors.creation.v1+json'
            }
        },
        fetch
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, text && text !== '' ? text : "Failed to create doctor");
    }
};

const parseInsuranceId = (insuranceUrl: string): string => {
    const divider = '/insurances/';
    const parts = insuranceUrl.split(divider);
    if (parts.length === 2) {
        return parts[1].split('/')[0]; // Return the part after /insurances/ and before any further slashes
    }
    throw new Error(`Invalid insurance URL: ${insuranceUrl}`);
};

export const updateDoctorProfile = async (
    user: User,
    payload: DoctorProfileUpdateData,
    doctor: Doctor,
    image?: File | null,
    fetchFn: typeof fetch = fetch
): Promise<Doctor> => {
    if (!user || user.role !== 'DOCTOR') {
        throw error(403, 'Unauthorized: Only doctors can update their profile');
    }

    let imageLocation: string | undefined = undefined;
    if (image) {
        imageLocation = await createFile(image, fetchFn);
    }

    const finalPayload = {
        picture: imageLocation,
        telephone: payload.telephone.trim() !== '' && payload.telephone !== doctor.telephone ? payload.telephone : undefined,
        mailLanguage: payload.mailLanguage !== user.language ? payload.mailLanguage : undefined,
        insurances: payload.insuranceSelfs.length > 0 ? payload.insuranceSelfs : undefined,
        updateSchedule: payload.updateSchedule,
        shifts: payload.shifts && payload.updateSchedule ? payload.shifts : undefined,
        keepTurns: payload.keepTurns !== undefined ? payload.keepTurns : undefined
    };

    const response = await patchAuth(
        user.self,
        finalPayload,
        {
            headers: {
                "Content-Type": "application/vnd.doctors.v1+json",
                "Accept": "application/vnd.doctors.v1+json"
            }
        },
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, "Failed to update doctor: " + text);
    }

    return response.json();
};

const populateDoctorData = async (doctor: Doctor, fetchFn: typeof fetch = fetch): Promise<Doctor> => {
    resolveNonTemplatedLinks(doctor);
    
    const response = await get(doctor.links.schedule.resolved!, undefined, fetchFn);

    if (response && response.ok) {
        const schedule: Shift[] = await response.json();

        const days = new Map<Weekdays, [Date, Date]>();
        let direction: string | undefined = undefined;
        schedule.forEach(shift => {
            // Parse time strings in HH:mm format
            const start = parseTime(shift.startTime);
            const end = parseTime(shift.endTime);
            days.set(shift.weekday as Weekdays, [start, end]);
            if (!direction) {
                direction = shift.address;
            }
            if (!doctor.startTime) {
                doctor.startTime = normalizeTime(shift.startTime);
            }
            if (!doctor.endTime) {
                doctor.endTime = normalizeTime(shift.endTime);
            }
            if (!doctor.duration) {
                doctor.duration = shift.duration;
            }
        });
        doctor.schedule = days;
        doctor.address = direction;

    }

    let insurancesPage = await fetchInsurancesPage(doctor.links.insurances.resolved!, fetchFn);
    let insurances: string[] = [...insurancesPage.results.map(i => i.name)];

    while (insurancesPage._links.next) {
        insurancesPage = await fetchInsurancesPage(insurancesPage._links.next, fetchFn);
        insurances = [...insurances, ...insurancesPage.results.map(i => i.name)];
    }

    doctor.insurances = insurances;

    return doctor;
};

const populateAuthorizationData = (doctor: Doctor, loggedUser: User): Doctor => {
    if (doctor.links.authorization) {
        const template = UriTemplate(doctor.links.authorization.href);
        const url = template.fill({ patientId: loggedUser.id });

        doctor.links.authorization.resolved = url; // Store the resolved URL for filtering
    }

    return doctor;
};
