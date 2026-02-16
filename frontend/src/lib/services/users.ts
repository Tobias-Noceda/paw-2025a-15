import { apiOrigin, get, getJWTEmail, post, put } from "$modules/api.svelte";

export function requestPasswordReset(email: string, fetchFn: typeof fetch = fetch) {
    return post(`${apiOrigin}/users`, { email }, {
        headers: {
            'Content-Type': 'application/vnd.user-password.creation.v1+json'
        }
    }, fetchFn);
};

export function resetPassword(token: string, newPassword: string, fetchFn: typeof fetch = fetch) {
    return put(`${apiOrigin}/users/${token}`, { newPassword }, {
        headers: {
            'Content-Type': 'application/vnd.user-password.reset.v1+json'
        }
    }, fetchFn);
};

export async function verifyEmail(token: string, fetchFn: typeof fetch = fetch): Promise<string> {
    const doctorsResponse = await get(`${apiOrigin}/doctors`, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    }, fetchFn);

    if (!doctorsResponse.ok) {
        throw new Error('Failed to verify email');
    }

    return getJWTEmail(token) || '';
}