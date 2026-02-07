import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user } from '$stores/user';
import { get } from 'svelte/store';
import { fetchPatientById } from '$lib/services/patients';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);

    if (currentUser && currentUser.role !== 'DOCTOR') {
        throw error(404, 'Not found');
    }

    try {
        const patient = await fetchPatientById(Number.parseInt(params.id), currentUser, fetch);

        if (!patient) {
            throw error(404, 'Patient not found');
        }

        return {
            patient
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
