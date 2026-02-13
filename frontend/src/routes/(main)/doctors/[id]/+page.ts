import { error } from '@sveltejs/kit';
import { fetchDoctorAuthorizations, fetchDoctorById } from '$lib/services/doctors';
import { fetchFreeAppointments, formatDateLocal, parseDateInLocalTimezone } from '$lib/services/appointments';
import type { PageLoad } from './$types';
import { setUserFromSession, user } from '$stores/user';
import { get } from 'svelte/store';
import { base } from '$app/paths';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);

    if (!currentUser) {
        window.location.href = `${base}/login`;
        return;
    }
    
    if (currentUser.role !== 'PATIENT') {
        throw error(404, 'Not found');
    }

    
    // Get date from URL or use today
    const dateParam = url.searchParams.get('date');
    const selectedDate = dateParam 
        ? parseDateInLocalTimezone(dateParam) 
        : new Date();

    try {
        const doctor = await fetchDoctorById(params.id, currentUser, fetch);
        
        if (!doctor) {
            throw error(404, 'Doctor not found');
        }
        
        const appointments = await fetchFreeAppointments(
            doctor.links.freeAppointments, 
            formatDateLocal(selectedDate)
        );

        const doctorAuthorizations = await fetchDoctorAuthorizations(doctor, fetch);

        return {
            doctor,
            doctorAuthorizations,
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
