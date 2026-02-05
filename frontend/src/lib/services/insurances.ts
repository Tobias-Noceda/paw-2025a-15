import { get } from "$modules/api.svelte";
import { baseApiUrl, type Insurance, type Paginated } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";

export const fetchInsurances = async (search?: string, fetchFn: typeof fetch = window.fetch): Promise<Paginated<Insurance>> => {
    const insurances: Paginated<Insurance> = { results: [], _links: {} };
    
    const response = await get(`${baseApiUrl}/insurances`, undefined, fetchFn);
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch insurances: ' + text);
    }

    insurances.results = await response.json();
    insurances._links = getPaginationLinks(response);
    insurances._pageInfo = getPageInfoFromHeaders(response);

    return insurances;
};

export const fetchInsurancesPage = async (url: string, fetchFn: typeof fetch = window.fetch): Promise<Paginated<Insurance>> => {
    const insurances: Paginated<Insurance> = { results: [], _links: {} };

    const response = await get(url, undefined, fetchFn);
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch insurances: ' + text);
    }

    insurances.results = await response.json();
    insurances._links = getPaginationLinks(response);
    insurances._pageInfo = getPageInfoFromHeaders(response);

    return insurances;
};