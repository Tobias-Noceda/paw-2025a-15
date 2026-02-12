import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { fetchFileById } from '$lib/services/files';
import { setUserFromSession, user, userData } from '$stores/user';
import { get } from 'svelte/store';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, fetch }) => {

    if (localStorage.getItem('access')) {
        await setUserFromSession(localStorage.getItem('access')!, fetch);
    }

    const currentUser = get(user);
    const currentUserData = get(userData);

    if (!currentUser || !currentUserData) {
        console.log('No user in store, trying to set from session...');
        throw error(404, 'Not found');
    }

	const fileId = params.id;
	
	if (!fileId || isNaN(Number(fileId))) {
        console.log('Invalid file ID:', fileId);
		throw error(404, 'File not found');
	}

    const file = await fetchFileById(Number(fileId), fetch);
    const fileLink = URL.createObjectURL(file);

	return {
        file,
        fileLink
	};
};
