import { user, userData } from '$lib/stores/user';
import { error } from '@sveltejs/kit';
import { get } from 'svelte/store';
import type { PageLoad } from './$types';
import type { Appointment, Doctor, Paginated, Patient } from '$types/api';
import { fetchFreeAppointments, fetchNonFreeAppointments, formatDateLocal, parseDateInLocalTimezone } from '$lib/services/appointments';
import { setUserFromSession } from '../../../lib/stores/user';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {
    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    let pastAppointments: Paginated<Appointment> | null = null;
    let futureAppointments: Paginated<Appointment> | null = null;
    let futureAppointmentsLink: string | null = null;

    let freeAppointments: Paginated<Appointment> | null = null;
    let freeAppointmentsLink: string | null = null;

    // Get date from URL or use today
    const dateParam = url.searchParams.get('date');
    const selectedDate = dateParam 
        ? parseDateInLocalTimezone(dateParam) 
        : new Date();

    if (!currentUser || !currentUserData) {
        throw error(401, 'User data not available');
    }

    if (currentUser.role !== 'DOCTOR' && currentUser.role !== 'PATIENT') {
        throw error(404, 'Not found');
    }

    try {
        if (currentUser.role === 'DOCTOR' && (currentUserData as Doctor).links.freeAppointments) {
            freeAppointmentsLink = (currentUserData as Doctor).links.freeAppointments;
            freeAppointments = await fetchFreeAppointments(freeAppointmentsLink, formatDateLocal(selectedDate), fetch);
        } else if (currentUser.role === 'PATIENT' && (currentUserData as Patient).links.pastAppointments) {
            pastAppointments = await fetchNonFreeAppointments((currentUserData as Patient).links.pastAppointments, fetch);
        }

        futureAppointmentsLink = currentUserData.links.futureAppointments;
        futureAppointments = await fetchNonFreeAppointments(futureAppointmentsLink, fetch);

        return {
            pastAppointments,
            futureAppointments,
            futureAppointmentsLink,
            freeAppointments,
            freeAppointmentsLink,
            selectedDate
        };
    } catch (err: any) {
        // If it's already a SvelteKit error, rethrow it
        if (err.status) {
            throw err;
        }
        // Otherwise wrap it in a proper error
        throw error(500, err.message || 'An error occurred while fetching appointments');
    }
};
