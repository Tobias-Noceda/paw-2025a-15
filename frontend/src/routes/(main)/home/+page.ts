import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { loggedOut, setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { type Doctor, type Insurance, type Paginated, type Patient } from '$types/api';
import { fetchInsurances } from '$lib/services/insurances';
import { fetchPatients } from '$lib/services/patients';
import { day, insurance, order, specialty } from '$stores/filters';
import { fetchDoctors } from '$lib/services/doctors';
import { invalidateAll } from '$app/navigation';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {
    if (get(loggedOut)) {
        loggedOut.set(false);
    }

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData);

    let patientsLink: string | null = null;
    let patients: Paginated<Patient> | null = null;
    let doctors: Paginated<Doctor> | null = null;
    let insurances: Paginated<Insurance> | null = null;

    const urlSearchQuery = url.searchParams.get('search') || '';
    let insuranceParam = url.searchParams.get('insurance') || 'all';
    let dayParam = url.searchParams.get('day') || 'all';
    let specialtyParam = url.searchParams.get('specialty') || 'all';
    let orderParam = url.searchParams.get('order') || '';

    if (currentUser?.role === 'DOCTOR') {
        if (currentUserData && (currentUserData as Doctor).links.patients) {
            patientsLink = (currentUserData as Doctor).links.patients;
        }
    }

    const userRole = currentUser?.role || null;
    let insuranceSearch: string | undefined = undefined;

    if (userRole === 'DOCTOR' && patientsLink) {
        patients = await fetchPatients(urlSearchQuery, patientsLink, fetch);
    } else if (userRole === 'ADMIN') {
        insuranceSearch = urlSearchQuery;
    } else {
        // Sync URL params to stores
        insurance.set(insuranceParam);
        day.set(dayParam);
        specialty.set(specialtyParam);
        order.set(orderParam);

        // Fetch doctors with current URL params
        doctors = await fetchDoctors(urlSearchQuery, insuranceParam, dayParam, specialtyParam, orderParam, fetch);
    }

    if (currentUser?.role !== 'DOCTOR') {
        insurances = await fetchInsurances(insuranceSearch, fetch);
    }

    return {
        userRole: currentUser?.role || null,
        patientsLink,
        patients,
        doctors,
        insurances,
    };
};
