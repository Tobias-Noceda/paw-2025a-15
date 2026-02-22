import { user, userData } from '$lib/stores/user';
import { error } from '@sveltejs/kit';
import { get } from 'svelte/store';
import type { PageLoad } from './$types';
import { setUserFromSession } from '$lib/stores/user';
import { fetchInsuranceById } from '$lib/services/insurances';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, fetch }) => {
    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);

    if (!currentUser || currentUser.role !== 'ADMIN') {
        throw error(404, 'Not found');
    }

    const insurance = await fetchInsuranceById(params.id, fetch);

    return {
        insurance
    };
};
