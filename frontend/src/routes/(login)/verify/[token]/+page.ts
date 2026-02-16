import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';
import { verifyEmail } from '$lib/services/users';

// Disable SSR since we need localStorage for authentication tokens
export const ssr = false;

export const load: PageLoad = async ({ params, fetch }) => {

    try {
        const verifiedEmail = await verifyEmail(params.token, fetch)
            .catch((e) => {
                console.error('Email verification failed:', e);
                return null;
            });

        return {
            verifiedEmail
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
