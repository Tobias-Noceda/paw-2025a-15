import { user, userData } from '$lib/stores/user';
import { error } from '@sveltejs/kit';
import { get } from 'svelte/store';
import type { PageLoad } from './$types';
import type { Appointment, Doctor, Paginated, Patient } from '$types/api';
import { fetchFreeAppointments, fetchNonFreeAppointments, formatDateLocal, parseDateInLocalTimezone } from '$lib/services/appointments';
import { setUserFromSession } from '$lib/stores/user';
import { fetchInsuranceById } from '$lib/services/insurances';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ fetch }) => {
    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);

    if (!currentUser || currentUser.role !== 'ADMIN') {
        throw error(404, 'Not found');
    }

    return {};
};
