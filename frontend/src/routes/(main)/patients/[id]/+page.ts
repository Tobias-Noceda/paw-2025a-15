import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user } from '$stores/user';
import { get } from 'svelte/store';
import { fetchPatientById } from '$lib/services/patients';
import type { Paginated, Study } from '$types/api';
import { fetchStudies } from '$lib/services/studies';
import type { StudyType } from '$types/enums/studyTypes';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);

    if (!currentUser || currentUser.role !== 'DOCTOR') {
        throw error(404, 'Not found');
    }

    try {
        const patient = await fetchPatientById(Number.parseInt(params.id), currentUser, fetch)
            .catch((error) => {
                if (error.status === 403) {
                    return null;
                }
                throw error;
            });

        let studies: Paginated<Study> = { _links: {}, results: [] };
        let studyType: string = 'all';
        let order: string = 'm_recent';

        if (!patient) {
            throw error(404, 'Patient not found');
        }

        if (patient.links.studies.resolved) {
            let typeParam = url.searchParams.get('type');
            typeParam = typeParam ? typeParam.replace(/_/g, ' ') : null;
            if (typeParam && typeParam as StudyType) {
                studyType = typeParam;
            }
            
            const orderParam = url.searchParams.get('order');
            if (orderParam && orderParam === 'l_recent') {
                order = orderParam;
            }

            studies = await fetchStudies(patient.links.studies.resolved, studyType, order, fetch);
        }

        return {
            patient,
            studies,
            studyType,
            order
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
