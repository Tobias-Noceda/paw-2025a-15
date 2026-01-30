import { get, getAuth } from "$modules/api.svelte";
import { type Appointment, type Paginated } from "$types/api";
import { getPaginationLinks } from "./pagination";

// Parse date in local timezone to avoid UTC conversion issues
export const parseDateInLocalTimezone = (dateStr: string): Date => {
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day);
};

// Format date for API
export const formatDateLocal = (date: Date): string => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
};

export const fetchFreeAppointments = async (
    urlString: string,
    date?: string,
    fetchFn: typeof fetch = fetch
): Promise<Paginated<Appointment>> => {
    
    let url = new URL(urlString);
    if (date) {
        url.searchParams.set('date', date);
    }

    const response = await getAuth(url.toString(), undefined, fetchFn);

    const appointments: Paginated<Appointment> = { _links: {}, results: [] };
    appointments.results = await response.json();

    appointments._links = getPaginationLinks(response);
    if (response.headers.get('X-Current-Date') && response.headers.get('X-Max-Date')) {
        appointments._pageInfo = {
            currentDate: parseDateInLocalTimezone(response.headers.get('X-Current-Date')!),
            maxDate: parseDateInLocalTimezone(response.headers.get('X-Max-Date')!)
        };
    }

    // Populate doctor data for each appointment
    for (const appointment of appointments.results) {
        await populateAppointmentData(appointment, fetchFn);
    }

    return appointments;
};

export const fetchNonFreeAppointments = async (
    urlString: string,
    fetchFn: typeof fetch = fetch
): Promise<Paginated<Appointment>> => {
    const response = await getAuth(urlString, undefined, fetchFn);
    
    const appointments: Paginated<Appointment> = { _links: {}, results: [] };
    appointments.results = await response.json();
    appointments._links = getPaginationLinks(response);

    // Populate doctor data for each appointment
    for (const appointment of appointments.results) {
        await populateAppointmentData(appointment, fetchFn);
    }

    return appointments;
};

export 

const populateAppointmentData = async (appointment: Appointment, fetchFn: typeof fetch = fetch): Promise<void> => {
    try {
        const response = await get(appointment.doctor, undefined, fetchFn);
        if (response.ok) {
            const doctorData = await response.json();
            appointment.doctorData = doctorData;
        }
    } catch (error) {
        console.error('Failed to populate appointment data:', error);
    }
    
    return;
};
