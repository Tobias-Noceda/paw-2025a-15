import { apiOrigin, post, put } from "$modules/api.svelte";

export function requestPasswordReset(email: string) {
    return post(`${apiOrigin}/password-resets`, { email });
};

export function resetPassword(token: string, newPassword: string) {
    return put(`${apiOrigin}/password-resets/${token}`, { newPassword });
};
