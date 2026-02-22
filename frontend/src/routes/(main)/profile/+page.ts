import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import type { Doctor, Insurance, Paginated, Patient, Study } from '$types/api';
import { fetchStudies } from '$lib/services/studies';
import type { StudyType } from '$types/enums/studyTypes';
import { fetchDoctorBySelf, fetchDoctorsPage } from '$lib/services/doctors';
import { fetchPatientBySelf } from '$lib/services/patients';
import { fetchInsurances, fetchInsurancesPage } from '$lib/services/insurances';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData)

    if (!currentUser || !currentUserData || (currentUser.role !== 'PATIENT' && currentUser.role !== 'DOCTOR')) {
        throw error(404, 'Not found');
    }

    try {
        let doctor: Doctor | null = null;
        let patient: Patient | null = null;
        let insurances: Insurance[] = [];

        if (currentUser.role === 'DOCTOR') {
            doctor = await fetchDoctorBySelf(currentUser.self, currentUser, fetch);
        } else if (currentUser.role === 'PATIENT') {
            patient = await fetchPatientBySelf(currentUser.self, currentUser, fetch);
        }

        if (!doctor && !patient) {
            throw error(404, 'User data not found');
        }

        const insurancePage = await fetchInsurances(undefined, fetch);
        insurances = [...insurancePage.results];

        while (insurancePage._links.next) {
            const nextPage = await fetchInsurancesPage(insurancePage._links.next, fetch);
            insurances = [...insurances, ...nextPage.results];
        }
        
        return {
            currentUser,
            doctor,
            patient,
            insurances
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
