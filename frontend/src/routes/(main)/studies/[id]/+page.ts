import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { type Doctor } from '$types/api';
import { fetchDoctorsPage } from '$lib/services/doctors';
import { fetchPatientById } from '$lib/services/patients';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData);
    const patientId = Number(params.id);

    if (!currentUser || !currentUserData || (currentUser.role !== 'PATIENT' && currentUser.role !== 'DOCTOR') || Number.isNaN(patientId)) {
        throw error(404, 'Not found');
    }

    if (currentUser.role === 'PATIENT' && currentUser.id !== patientId) {
        throw error(404, 'Not found');
    }

    try {
        const patient = await fetchPatientById(patientId, undefined, fetch);

        if (!patient) {
            throw error(404, 'Patient not found');
        }
        let doctors: Doctor[] | null = null;

        if (currentUser.role === 'PATIENT') {
            let doctorsPage = await fetchDoctorsPage(patient.links.doctors.resolved!, undefined, fetch);
            if (doctorsPage && doctorsPage.results) {
                doctors = [...doctorsPage.results];
            }

            while (doctors && doctorsPage._links.next) {
                doctorsPage = await fetchDoctorsPage(doctorsPage._links.next, undefined, fetch);
                doctors = [...doctors, ...doctorsPage.results];
            }
        }

        return {
            currentUser,
            patient,
            patientId,
            doctors
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
