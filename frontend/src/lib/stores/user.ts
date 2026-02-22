import { setPatientsStudyLink } from "$lib/services/patients";
import { get, getAuth, parseJWT, resolveNonTemplatedLinks } from "$modules/api.svelte";
import type { Doctor, Patient } from "$types/api";
import { writable } from "svelte/store";

export type User = {
	id: number;
	language: string;
	name: string;
	role: 'DOCTOR' | 'PATIENT' | 'ADMIN';
    image: string;
	self: string;
	[key: string]: any;
};

let user = writable<User | null>(null);
let userData = writable<Doctor | Patient | null>(null);
let loggedOut = writable<boolean>(false);

export async function setUserFromSession(sessionToken: string, fetchFn: typeof fetch = window.fetch) {
    // Parse user data from session token
	const payload = parseJWT(sessionToken);
	if (payload) {
		user.set({
			id: payload.id,
			language: payload.language,
			name: payload.name,
			role: payload.role,
            image: payload.image,
			self: payload.self,
			...payload
		});

		if (payload.role === 'DOCTOR' || payload.role === 'PATIENT') {
			const response = await getAuth(payload.self, undefined, fetchFn).catch(() => null);

			if (response && response.ok) {
				const data = await response.json();
				
				if (payload.role === 'PATIENT') {
					const patient = setPatientsStudyLink(data as Patient);
					resolveNonTemplatedLinks(patient);
					userData.set(patient);
				} else {
					resolveNonTemplatedLinks(data as Doctor);
					userData.set(data as Doctor);
				}
			}
		}
	}
};

export { user, userData, loggedOut };