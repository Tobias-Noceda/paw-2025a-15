import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { fetchFileById } from '$lib/services/files';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, fetch }) => {

    let currentUser = get(user);

    if (!currentUser && localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    currentUser = get(user);
    const currentUserData = get(userData);

    if (!currentUser || !currentUserData) {
        console.log('No user in store, trying to set from session...');
        throw error(404, 'Not found');
    }

	const fileId = params.id;
	
	if (!fileId || isNaN(Number(fileId))) {
		throw error(404, 'File not found');
	}

    const file = await fetchFileById(Number(fileId), fetch);
    const fileLink = URL.createObjectURL(file);

	return {
        file,
        fileLink
	};
};
