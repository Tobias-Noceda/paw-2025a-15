import { deleteAuth, getAuth, postAuth, resolveNonTemplatedLinks } from "$modules/api.svelte";
import { type Paginated, type Vacations } from "$types/api";
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

    for (const vacation of vacations.results) {
        resolveNonTemplatedLinks(vacation);
    }

    return vacations;
};

export const createVacation = async (
    doctorSelf: string,
    startDate: Date,
    endDate: Date,
    cancel: boolean = false,
    fetchFn: typeof fetch = fetch
): Promise<Boolean> => {
    const url = `${doctorSelf}/vacations`;
    const response = await postAuth(url, { 
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0],
        cancelAppointments: cancel
    }, {
        headers: {
            'Content-Type': 'application/vnd.doctors.vacations.v1+json'
        }
    }, fetchFn);
    
    if (!response.ok) {
        const text = await response.text();
        throw error(response.status || 500, text || 'Failed to create vacation');
    }

    return true;
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
