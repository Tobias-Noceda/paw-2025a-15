import { apiOrigin, getAuth } from "$modules/api.svelte";
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
    baseUrl.searchParams.append('pageSize', '15');

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

export const fetchSingleStudy = async (patientId: number, studyId: number, fetchFn: typeof fetch = fetch): Promise<Study> => {
    if (!patientId || !studyId) {
        throw error(404, 'Patient ID and Study ID are required');
    }
    const url = `${apiOrigin}/patients/${patientId}/studies/${studyId}`;
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const study: Study = await response.json();

        if (study) {
            const filesResponse = await getAuth(study.links.files, undefined, fetchFn)
                .catch(() => null);
            
            if (filesResponse && filesResponse.ok) {
                const filesData = await filesResponse.json();
                study.files = {
                    results: filesData,
                    _links: getPaginationLinks(filesResponse),
                    _pageInfo: getPageInfoFromHeaders(filesResponse)
                };
            }

            const uploader = await getAuth(study.links.uploader, undefined, fetchFn).then(res => {
                if (res.ok) {
                    return res.json();
                }
                return null;
            }).catch(() => null);
            study.uploaderName = uploader?.name;
        }

        return study;
    }

    throw error(response.status || 500, 'Failed to fetch study');
};

export const fetchFilesPage = async (url: string, fetchFn: typeof fetch = fetch): Promise<Paginated<File>> => {
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        const filesData = await response.json();
        
        return {
            results: filesData,
            _links: getPaginationLinks(response),
            _pageInfo: getPageInfoFromHeaders(response)
        };
    }

    throw error(response.status || 500, 'Failed to fetch files');
};
