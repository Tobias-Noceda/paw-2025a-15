import type { Doctor, Insurance, Paginated, Shift } from "$types/api";
import type { Weekdays } from "$types/enums/weekdays";

export const fetchDoctors = async (
    ensurance: string,
    day: string,
    specialty: string,
    order: string
): Promise<Paginated<Doctor>> => {
    
    let doctors: Paginated<Doctor> = { results: [], _links: {} };
    try {
        let url = new URL('http://localhost:8080/paw-2025a-15/api/doctors');
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
            response.headers.get('Link')?.split(',').forEach(link => {
                const match = link.match(/<([^>]+)>;\s*rel="([^"]+)"/);
                if (match) {
                    const [, linkUrl, rel] = match;
                    doctors._links[rel as keyof typeof doctors._links] = linkUrl;
                }
            });
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

const populateDoctorData = async (doctor: Doctor): Promise<Doctor> => {
    const response = await fetch(doctor.schedule);

    if (response.ok) {
        const schedule: Shift[] = await response.json();

        const days = new Set<Weekdays>();
        schedule.forEach(shift => {
            days.add(shift.weekday as Weekdays);
        });
        doctor.scheduleDays = days;
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