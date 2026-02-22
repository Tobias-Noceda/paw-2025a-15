import { apiOrigin, getAuth, postAuth } from "$modules/api.svelte";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import type { Paginated, File as ApiFile } from "$types/api";

export const fetchFilesPage = async (url: string, fetchFn: typeof fetch = fetch): Promise<Paginated<ApiFile>> => {
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

export const fetchFileById = async (fileId: number, fetchFn: typeof fetch = fetch): Promise<Blob> => {
    const url = `${apiOrigin}/files/${fileId}`;
    const response = await getAuth(url, undefined, fetchFn);

    if (response.ok) {
        return await response.blob();
    }

    console.error(`Failed to fetch file with ID ${fileId}:`, response.status, response.statusText);
    throw error(response.status || 500, 'Failed to fetch file');
};

export const createFile = async (file: File, fetchFn: typeof fetch = fetch): Promise<string> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await postAuth(
        `${apiOrigin}/files`,
        formData,
        undefined,
        fetchFn);

    if (response.ok) {
        // return the value of the 'Location' header, which contains the URL of the created file resource
        const location = response.headers.get('Location');
        if (location) {
            return location;
        } else {
            console.error('File created but no Location header found');
            throw new Error('File created but no Location header found');
        }
    }

    throw error(response.status || 500, 'Failed to upload file');
};
