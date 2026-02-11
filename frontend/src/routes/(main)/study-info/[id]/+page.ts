import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { baseApiUrl, type Doctor, type Paginated, type Patient, type Study } from '$types/api';
import { fetchSingleStudy, fetchStudies } from '$lib/services/studies';
import type { StudyType } from '$types/enums/studyTypes';
import { fetchDoctorsPage } from '$lib/services/doctors';
import { getAuth } from '$modules/api.svelte';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    if (!currentUser || !currentUserData || (currentUser.role !== 'PATIENT' && currentUser.role !== 'DOCTOR')) {
        throw error(404, 'Not found');
    }

    try {
        let study: Study;
        let doctors: Doctor[] = [];
        let authorizedDoctorsEmails: string[] = [];

        const ids = params.id.split('-');
        if (ids.length !== 2) {
            throw error(404, 'Not found');
        }

        study = await fetchSingleStudy(Number(ids[1]), Number(ids[0]), fetch);

        const files = await getAuth(study.links.files, undefined, fetch);

        if (currentUser.role === 'PATIENT') {
            const doctorsLink = (currentUserData as Patient).links.doctors;
            if (doctorsLink) {
                let doctorsResponse = await fetchDoctorsPage(doctorsLink, currentUser, fetch);
                doctors = [...doctorsResponse.results];

                while (doctorsResponse._links.next) {
                    doctorsResponse = await fetchDoctorsPage(doctorsResponse._links.next, currentUser, fetch);
                    doctors = [...doctors, ...doctorsResponse.results];
                }
            }

            let authDoctorsResponse = await fetchDoctorsPage(study.links.authDoctors, currentUser, fetch);
            authorizedDoctorsEmails = [...authDoctorsResponse.results.map(doctor => doctor.email)];

            while (authDoctorsResponse._links.next) {
                authDoctorsResponse = await fetchDoctorsPage(authDoctorsResponse._links.next, currentUser, fetch);
                authorizedDoctorsEmails = [...authorizedDoctorsEmails, ...authDoctorsResponse.results.map(doctor => doctor.email)];
            }
        }

        return {
            currentUser,
            study,
            doctors,
            authorizedDoctorsEmails
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
