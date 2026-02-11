import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { loggedOut, setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';
import { type Doctor, type Insurance, type Paginated } from '$types/api';
import { fetchInsurances } from '$lib/services/insurances';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, url, fetch }) => {
    if (get(loggedOut)) {
        loggedOut.set(false);
    }

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    let patientsLink: string | null = null;

    if (currentUser?.role === 'DOCTOR') {
        if (currentUserData && (currentUserData as Doctor).links.patients) {
            patientsLink = (currentUserData as Doctor).links.patients;
        }
    }

    return {
        userRole: currentUser?.role || null,
        patientsLink,
    };
};
