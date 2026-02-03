import { fetchDoctorById } from "$lib/services/doctors";
import { get, parseJWT } from "$modules/api.svelte";
import type { Doctor, Patient } from "$types/api";
import { writable } from "svelte/store";

type User = {
	name: string;
	role: string;
    image: string;
	self: string;
	[key: string]: any;
};

let user = writable<User | null>(null);

let userData = writable<Doctor | Patient | null>(null);

export async function setUserFromSession(sessionToken: string, fetchFn: typeof fetch = window.fetch) {
    // Parse user data from session token
	const payload = parseJWT(sessionToken);
	if (payload) {
		user.set({
			name: payload.name,
			role: payload.role,
            image: payload.image,
			self: payload.self,
			...payload
		});

		if (payload.role === 'DOCTOR' || payload.role === 'PATIENT') {
			const response = await get(payload.self, undefined, fetchFn).catch(() => null);

			if (response && response.ok) {
				const data = await response.json();
				userData.set(payload.role === 'DOCTOR' ? data as Doctor : data as Patient);
			}
		}
	}
};

export { user, userData };