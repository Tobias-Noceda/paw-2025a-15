import { error } from '@sveltejs/kit';
import { fetchDoctorById, fetchDoctors, fetchDoctorsPage } from '$lib/services/doctors';
import { fetchFreeAppointments, formatDateLocal, parseDateInLocalTimezone } from '$lib/services/appointments';
import type { PageLoad } from './$types';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { baseApiUrl, type Doctor, type Insurance, type Paginated, type Patient } from '$types/api';
import { fetchInsurances } from '$lib/services/insurances';
import { fetchPatients } from '$lib/services/patients';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    let insurances: Insurance[] | null = null;

    const searchQuery = url.searchParams.get('search') || '';
    let patientsLink: string | null = null;

    if (!currentUser || currentUser.role !== 'DOCTOR') {
        insurances = await fetchInsurances(fetch);
    } else if (currentUser.role === 'DOCTOR') {
        if (currentUserData && (currentUserData as Doctor).links.patients) {
            patientsLink = (currentUserData as Doctor).links.patients;
        }
    } else {
        throw error(500, 'Invalid user role');
    }

    return {
        userRole: currentUser?.role || null,
        insurances,
        patientsLink,
    };
};
