import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { baseApiUrl, type Doctor, type Paginated, type Patient, type Study } from '$types/api';
import { fetchSingleStudy, fetchStudies } from '$lib/services/studies';
import type { StudyType } from '$types/enums/studyTypes';
import { fetchDoctorsPage } from '$lib/services/doctors';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    if (!currentUser || !currentUserData) {
        throw error(404, 'Not found');
    }

    try {
        let study: Study;

        const ids = params.id.split('-');
        if (ids.length !== 2) {
            console.error('Invalid study ID format:', params.id);
            throw error(404, 'Not found');
        }

        study = await fetchSingleStudy(Number(ids[1]), Number(ids[2]), fetch);

        return {
            currentUser,
            study,
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
