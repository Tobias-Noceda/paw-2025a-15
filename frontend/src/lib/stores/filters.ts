import { writable } from "svelte/store";

export const searchQuery = writable('');
export const insurance = writable('all');
export const day = writable('all');
export const specialty = writable('all');
export const order = writable('');

export function clearSearchQuery() {
    searchQuery.set('');
}

export function clearAllFilters() {
    searchQuery.set('');
    insurance.set('all');
    day.set('all');
    specialty.set('all');
    order.set('');
}

export function getFiltersURL(
    search: string,
    insurance: string,
    day: string,
    specialty: string,
    order: string,
) {
    // Update URL with current filter values
    const params = new URLSearchParams();

    if (search.trim() !== '') params.set('search', search.trim());
    if (insurance !== 'all') params.set('insurance', insurance);
    if (day !== 'all') params.set('day', day);
    if (specialty !== 'all') params.set('specialty', specialty);
    if (order !== '') params.set('order', order);
    
    return params.toString();
}
