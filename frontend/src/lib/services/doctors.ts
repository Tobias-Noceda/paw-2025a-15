import type { Doctor, Insurance, Paginated, Shift } from "$types/api";
import type { Weekdays } from "$types/enums/weekdays";
import { baseApiUrl } from "$types/api";
import { getPaginationLinks } from "./pagination";
import { get, post } from "$modules/api.svelte";

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
    ensurance: string,
    day: string,
    specialty: string,
    order: string
): Promise<Paginated<Doctor>> => {
    
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        let url = new URL(`${baseApiUrl}/doctors`);
        if (ensurance !== 'all') {
            url.searchParams.append('insurance', ensurance);
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

        const response = await get(url.toString());
        if (response.ok) {
            doctors.results = await response.json();
            response.headers.get('Link')?.split(',').forEach(link => {
                const match = link.match(/<([^>]+)>;\s*rel="([^"]+)"/);
                if (match) {
                    const [, linkUrl, rel] = match;
                    doctors._links[rel as keyof typeof doctors._links] = linkUrl;
                }
            });

            if (response.headers.get('X-Current-Page') && response.headers.get('X-Total-Pages')) {
                doctors._pageInfo = {
                    currentPage: Number(response.headers.get('X-Current-Page')),
                    totalPages: Number(response.headers.get('X-Total-Pages'))
                };
            }

            for (const doctor of doctors.results) {
                await populateDoctorData(doctor);
            }
        }
    } catch (error) {
        console.error('Failed to fetch doctors:', error);
    }

    return doctors;
};

export const fetchDoctorsPage = async (nextUrl: string): Promise<Paginated<Doctor>> => {
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        const response = await get(nextUrl);
        if (response.ok) {
            doctors.results = await response.json();
            doctors._links = getPaginationLinks(response);
            if (response.headers.get('X-Current-Page') && response.headers.get('X-Total-Pages')) {
                doctors._pageInfo = {
                    currentPage: Number(response.headers.get('X-Current-Page')),
                    totalPages: Number(response.headers.get('X-Total-Pages'))
                };
            }
        }
        for (const doctor of doctors.results) {
            await populateDoctorData(doctor);
        }

        return doctors;
    } catch (error) {
        console.error('Failed to fetch next doctors:', error);
        return doctors;
    }
};

export const fetchDoctorById = async (id: string, fetchFn: typeof fetch = fetch): Promise<Doctor | null> => {
    const url = new URL(`${baseApiUrl}/doctors/${id}`);
    const response = await get(url.toString(), undefined, fetchFn);
    
    const doctor = await response.json();
    if (doctor) {
        await populateDoctorData(doctor, fetchFn);
    }
    
    return doctor;
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
