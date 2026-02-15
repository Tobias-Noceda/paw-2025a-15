import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { type Doctor } from '$types/api';
import { fetchVacations } from '$lib/services/vacations';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData);

    if (!currentUser || !currentUserData || currentUser.role !== 'DOCTOR') {
        throw error(404, 'Not found');
    }

    try {
        const doctor = currentUserData as Doctor;
        const pastVacations = await fetchVacations(doctor.links.pastVacations, fetch);
        const futureVacations = await fetchVacations(doctor.links.futureVacations, fetch);

        return {
            doctor,
            pastVacations,
            futureVacations
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
