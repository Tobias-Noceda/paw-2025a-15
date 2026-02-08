import type { Doctor, DoctorAuthorizations, Insurance, Paginated, Shift } from "$types/api";
import type { Weekdays } from "$types/enums/weekdays";
import { baseApiUrl } from "$types/api";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import { get, getAuth, post, postAuth, putAuth } from "$modules/api.svelte";
import UriTemplate from "uri-templates";
import type { User } from "$stores/user";
import type { AccessLevels } from "$types/enums/accessLevels";

/**
 * Parse time string in HH:mm format and return a Date object
 */
function parseTime(timeStr: string): Date {
    const [hours, minutes] = timeStr.split(':').map(Number);
    const date = new Date();
    date.setHours(hours, minutes, 0, 0);
    return date;
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

export const fetchDoctorsPage = async (nextUrl: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Doctor>> => {
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
        await populateAuthorizationData(doctor, loggedUser, fetchFn);
    }
    
    return doctor;
};

export const fetchDoctorAuthorizations = async (doctor: Doctor, fetchFn: typeof fetch = fetch): Promise<DoctorAuthorizations | null> => {
    if (doctor.links.authorizationResolved) {
        const response = await getAuth(doctor.links.authorizationResolved, undefined, fetchFn);
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

export const createDoctor = async (
    doctorData: Partial<Doctor>,
    password: string,
    shifts: ShiftCreationData
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
        throw new Error("Failed to create doctor");
    }
};

const populateDoctorData = async (doctor: Doctor, fetchFn: typeof fetch = fetch): Promise<Doctor> => {
    const response = await get(doctor.links.schedule, undefined, fetchFn);

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
        });
        doctor.schedule = days;
        doctor.direction = direction;
    }

    const responseInsurances = await get(doctor.links.insurances, undefined, fetchFn);
    if (responseInsurances.ok) {
        const insurancesData: Insurance[] = await responseInsurances.json();
        doctor.insurances = insurancesData.map(ins => ins.name);
    }

    return doctor;
};

const populateAuthorizationData = async (doctor: Doctor, loggedUser: User, fetchFn: typeof fetch = fetch): Promise<Doctor> => {
    if (doctor.links.authorization) {
        const template = UriTemplate(doctor.links.authorization.href);
        const url = template.fill({ patientId: loggedUser.id });

        doctor.links.authorizationResolved = url; // Store the resolved URL for filtering
    }

    return doctor;
};
