import { deleteAuth, getAuth } from "$modules/api.svelte";
import type { Paginated, Vacations } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";

export const fetchVacations = async (url: string, fetchFn: typeof fetch = window.fetch): Promise<Paginated<Vacations>> => {
    const response = await getAuth(url, undefined, fetchFn);

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, text || 'Failed to fetch vacations');
    }

    const vacations: Paginated<Vacations> = {
        results: await response.json(),
        _links: getPaginationLinks(response),
        _pageInfo: getPageInfoFromHeaders(response)
    };

    return vacations;
};

export const createVacation = async (
    doctorId: string,
    startDate: string,
    endDate: string,
    fetchFn: typeof fetch = fetch
): Promise<boolean> => {
    try {
        const url = `${baseApiUrl}/doctors/${doctorId}/vacations`;
        const response = await postAuth(url, { startDate, endDate }, undefined, fetchFn);
        
        return response.ok || response.status === 201;
    } catch (error) {
        console.error('Failed to create vacation:', error);
        return false;
    }
};

export const deleteVacation = async (
    selfLink: string,
    fetchFn: typeof fetch = fetch
): Promise<boolean> => {
    try {
        const response = await deleteAuth(selfLink, undefined, fetchFn);
        
        return response.ok || response.status === 204;
    } catch (error) {
        console.error('Failed to delete vacation:', error);
        return false;
    }
};
