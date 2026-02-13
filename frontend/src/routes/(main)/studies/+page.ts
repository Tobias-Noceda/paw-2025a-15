import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import type { Doctor, Paginated, Patient, Study } from '$types/api';
import { fetchStudies } from '$lib/services/studies';
import type { StudyType } from '$types/enums/studyTypes';
import { fetchDoctorsPage } from '$lib/services/doctors';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData)

    if (!currentUser || !currentUserData || currentUser.role !== 'PATIENT') {
        throw error(404, 'Not found');
    }

    try {
        let studies: Paginated<Study> = { _links: {}, results: [] };
        let doctors: Paginated<Doctor> = { _links: {}, results: [] };
        let studiesLink: string | undefined = undefined;
        let studyType: string = 'all';
        let order: string = 'm_recent';

        const loggedPatient = currentUserData as Patient;

        if (loggedPatient.links.studies.resolved) {
            let typeParam = url.searchParams.get('type');
            typeParam = typeParam ? typeParam.replace(/_/g, ' ') : null;
            if (typeParam && typeParam as StudyType) {
                studyType = typeParam;
            }
            
            const orderParam = url.searchParams.get('order');
            if (orderParam && orderParam === 'l_recent') {
                order = orderParam;
            }

            studiesLink = loggedPatient.links.studies.resolved;
            studies = await fetchStudies(studiesLink, studyType, order, fetch);

            // Fetch doctors for the doctor list
            const doctorsLink = loggedPatient.links.doctors;
            if (doctorsLink) {
                doctors = await fetchDoctorsPage(doctorsLink, currentUser, fetch);
            }
        }

        return {
            currentUser,
            doctors,
            studies,
            studiesLink,
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
