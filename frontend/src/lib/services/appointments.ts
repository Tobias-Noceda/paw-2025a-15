import { get } from "$modules/api.svelte";
import { baseApiUrl, type Appointment, type Paginated } from "$types/api";
import { getPaginationLinks } from "./pagination";


// Parse date in local timezone to avoid UTC conversion issues
export const parseDateInLocalTimezone = (dateStr: string): Date => {
    const [year, month, day] = dateStr.split('-').map(Number);
    return new Date(year, month - 1, day);
};

export const fetchFreeAppointments = async (
    urlString: string,
    date?: string
): Promise<Paginated<Appointment>> => {
    
    let appointments: Paginated<Appointment> = { _links: {}, results: [] };
    try {
        let url = new URL(urlString);
        if (date) {
            url.searchParams.set('date', date);
        }

        const response = await get(url.toString(), undefined, fetch);
        if (response.ok) {
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
                await populateAppointmentData(appointment);
            }
        }
    } catch (error) {
        console.error('Failed to fetch appointments:', error);
    }

    return appointments;
};

const populateAppointmentData = async (appointment: Appointment): Promise<void> => {
    try {
        const response = await get(appointment.doctor, undefined, fetch);
        if (response.ok) {
            const doctorData = await response.json();
            appointment.doctorData = doctorData;
        }
    } catch (error) {
        console.error('Failed to populate appointment data:', error);
    }
    
    return;
};
