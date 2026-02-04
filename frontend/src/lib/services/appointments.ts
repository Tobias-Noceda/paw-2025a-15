import { get, getAuth, patchAuth } from "$modules/api.svelte";
import { type Appointment, type Paginated } from "$types/api";
import { AppointmentStatus } from "$types/enums/AppointmentStatus";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";

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
    appointments._pageInfo = getPageInfoFromHeaders(response);

    // Populate doctor data for each appointment
    for (const appointment of appointments.results) {
        await populateAppointmentData(appointment, fetchFn);
    }

    return appointments;
};

export const fetchNonFreeAppointments = async (
    urlString: string,
    fetchFn: typeof fetch = fetch,
    isPaginationLink: boolean = false
): Promise<Paginated<Appointment>> => {
    let urlStringFinal = urlString;

    if (!isPaginationLink) {
        const url = new URL(urlString);
        url.searchParams.set('pageSize', '15');
        urlStringFinal = url.toString();
    }

    const response = await getAuth(urlStringFinal, undefined, fetchFn);
    
    const appointments: Paginated<Appointment> = { _links: {}, results: [] };
    appointments.results = await response.json();
    appointments._links = getPaginationLinks(response);

    // Populate doctor data for each appointment
    for (const appointment of appointments.results) {
        await populateAppointmentData(appointment, fetchFn);
    }

    return appointments;
};

export const populateAppointmentData = async (appointment: Appointment, fetchFn: typeof fetch = fetch): Promise<void> => {
    try {
        const response = await get(appointment.links.doctor, undefined, fetchFn);
        if (response.ok) {
            const doctor = await response.json();
            appointment.doctor = doctor;
        }
    } catch (error) {
        console.error('Failed to populate appointment data:', error);
    }
    
    return;
};

export const takeAppointment = async (
    url: string,
    details: string,
    fetchFn: typeof fetch = fetch
): Promise<Appointment> => {
    const response = await patchAuth(
        url,
        {
            status: AppointmentStatus.TAKEN,
            description: details
        },
        undefined,
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status, 'Failed to take appointment: ' + text);
    }

    return response.json();
};

export const cancelAppointment = async (
    url: string,
    fetchFn: typeof fetch = fetch
): Promise<Appointment> => {
    const response = await patchAuth(
        url,
        {
            status: AppointmentStatus.FREE,
            description: null
        },
        undefined,
        fetchFn
    );

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status, 'Failed to cancel appointment: ' + text);
    }

    return response.json();
};
