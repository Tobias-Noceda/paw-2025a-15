import { getAuth } from "$modules/api.svelte";
import type { Paginated, Study } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";

export const fetchStudies = async (url: string, studyType?: string, order?: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Study>> => {
    const baseUrl = new URL(url);
    if (studyType && studyType !== "all") {
        baseUrl.searchParams.append('studyType', studyType);
    }
    if (order && order === 'l_recent') {
        baseUrl.searchParams.append('recent', "false");
    }

    const response = await getAuth(baseUrl.toString(), undefined, fetchFn);

    if (response.ok) {
        const studiesData = await response.json();
        
        return {
            results: studiesData,
            _links: getPaginationLinks(response),
            _pageInfo: getPageInfoFromHeaders(response)
        };
    }

    throw error(response.status || 500, 'Failed to fetch studies');
};

export const fetchStudiesPage = async (url: string, fetchFn: typeof fetch = fetch): Promise<Paginated<Study>> => {
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const studiesData = await response.json();
        
        return {
            results: studiesData,
            _links: getPaginationLinks(response),
            _pageInfo: getPageInfoFromHeaders(response)
        };
    }

    throw error(response.status || 500, 'Failed to fetch studies');
};

export const fetchSingleStudy = async (url: string, fetchFn: typeof fetch = fetch): Promise<Study> => {
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const study: Study = await response.json();
        return study;
    }

    throw error(response.status || 500, 'Failed to fetch study');
};
