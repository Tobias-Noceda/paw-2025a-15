import type { Doctor, Insurance, Paginated, Shift } from "$types/api";
import type { Weekdays } from "$types/enums/weekdays";
import { baseApiUrl } from "$types/api";
import { getPaginationLinks } from "./pagination";

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

        const response = await fetch(url.toString());
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

            await Promise.all(doctors.results.map(async (doctor) => {
                await populateDoctorData(doctor);
            }));
        }
    } catch (error) {
        console.error('Failed to fetch doctors:', error);
    }

    return doctors;
};

export const fetchDoctorsPage = async (nextUrl: string): Promise<Paginated<Doctor>> => {
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        const response = await fetch(nextUrl)
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
        await Promise.all(doctors.results.map(async (doctor) => {
            await populateDoctorData(doctor);
        }));

        return doctors;
    } catch (error) {
        console.error('Failed to fetch next doctors:', error);
        return doctors;
    }
};

export const fetchDoctorById = async (id: string): Promise<Doctor | null> => {
    let doctor: Doctor | null = null;
    try {
        let url = new URL(`${baseApiUrl}/doctors/${id}`);

        const response = await fetch(url.toString());
        if (response.ok) {
            doctor = await response.json();

            if (doctor) {
                await populateDoctorData(doctor);
            }
        }
    } catch (error) {
        console.error('Failed to fetch doctors:', error);
    }

    return doctor;
};

const populateDoctorData = async (doctor: Doctor): Promise<Doctor> => {
    const response = await fetch(doctor.schedule);

    if (response.ok) {
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
        doctor.scheduleDays = days;
        doctor.direction = direction;
    } else {
        throw new Error('Failed to fetch schedule');
    }

    const responseInsurances = await fetch(doctor.insurances);
    if (responseInsurances.ok) {
        const insurancesData: Insurance[] = await responseInsurances.json();
        doctor.insuranceNames = insurancesData.map(ins => ins.name);
    } else {
        throw new Error('Failed to fetch insurances');
    }
    return doctor;
};