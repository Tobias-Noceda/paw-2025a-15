import { deleteAuth, get, patchAuth, postAuth } from "$modules/api.svelte";
import { baseApiUrl, type Insurance, type Paginated } from "$types/api";
import { error } from "@sveltejs/kit";
import { getPageInfoFromHeaders, getPaginationLinks } from "./pagination";
import { createFile } from "./files";

export const fetchInsurances = async (search?: string, fetchFn: typeof fetch = window.fetch): Promise<Paginated<Insurance>> => {
    const insurances: Paginated<Insurance> = { results: [], _links: {} };
    
    let url = new URL(`${baseApiUrl}/insurances`);
    if (search && search.trim() !== '') {
        url.searchParams.append('name', search);
    }

    const response = await get(url.toString(), undefined, fetchFn);
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

export const fetchInsuranceById = async (id: string, fetchFn: typeof fetch = window.fetch): Promise<Insurance> => {
    const response = await get(`${baseApiUrl}/insurances/${id}`, undefined, fetchFn);

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to fetch insurance: ' + text);
    }

    return await response.json();
};

export const createInsurance = async (name: string, file?: File, fetchFn: typeof fetch = window.fetch): Promise<string> => {
    let imageLocation = null;
    if (file) {
        imageLocation = await createFile(file, fetchFn);
    }

    const body: any = { name };
    if (imageLocation) {
        body.picture = imageLocation;
    }

    const response = await postAuth(`${baseApiUrl}/insurances`, body, {
        headers: {
            'Content-Type': 'application/vnd.insurances.creation.v1+json'
        }
    }, fetchFn);

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to create insurance: ' + text);
    }

    const location = response.headers.get('Location');
    if (!location) {
        console.error('Insurance created but no Location header found');
        throw error(500, 'Insurance created but no Location header found');
    }
    
    return location;
};

export const editInsurance = async (path: string, name: string, newFile?: File, fetchFn: typeof fetch = window.fetch): Promise<Insurance> => {
    let imageLocation = null;
    if (newFile) {
        imageLocation = await createFile(newFile, fetchFn);
    }

    const body: any = { name };
    if (imageLocation) {
        body.picture = imageLocation;
    }

    const response = await patchAuth(path, body, {
        headers: {
            'Content-Type': 'application/vnd.insurances.v1+json'
        }
    }, fetchFn);

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to edit insurance: ' + text);
    }

    return await response.json();
};

export const deleteInsurance = async (path: string, fetchFn: typeof fetch = window.fetch): Promise<void> => {
    const response = await deleteAuth(path, undefined, fetchFn);

    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, 'Failed to delete insurance: ' + text);
    }
};
