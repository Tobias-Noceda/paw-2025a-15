import { get } from "$modules/api.svelte";
import { baseApiUrl, type Insurance } from "$types/api";
import { error } from "@sveltejs/kit";

export const fetchInsurances = async (fetchFn: typeof fetch = window.fetch): Promise<Insurance[]> => {
    const response = await get(`${baseApiUrl}/insurances`);
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch insurances: ' + text);
    }

    return response.json();
};
