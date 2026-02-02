import { error } from '@sveltejs/kit';
import { fetchDoctorById } from '$lib/services/doctors';
import { fetchFreeAppointments, formatDateLocal, parseDateInLocalTimezone } from '$lib/services/appointments';
import type { PageLoad } from './$types';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {
    // Get date from URL or use today
    const dateParam = url.searchParams.get('date');
    const selectedDate = dateParam 
        ? parseDateInLocalTimezone(dateParam) 
        : new Date();

    try {
        const doctor = await fetchDoctorById(params.id, fetch);

        if (!doctor) {
            throw error(404, 'Doctor not found');
        }

        const appointments = await fetchFreeAppointments(
            doctor.links.freeAppointments, 
            formatDateLocal(selectedDate)
        );

        return {
            doctor,
            appointments,
            selectedDate
        };
    } catch (err: any) {
        // If it's already a SvelteKit error, rethrow it
        if (err.status) {
            throw err;
        }
        // Otherwise wrap it in a proper error
        throw error(500, err.message || 'An error occurred');
    }
};
