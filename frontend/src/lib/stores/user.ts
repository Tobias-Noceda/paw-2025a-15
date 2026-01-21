import { parseJWT } from "$modules/api.svelte";
import { writable } from "svelte/store";

type User = {
	name: string;
	role: string;
    image: string;
	[key: string]: any;
};

let user = writable<User | null>(null);

export function setUserFromSession(sessionToken: string) {
    // Parse user data from session token
	const payload = parseJWT(sessionToken);
	console.log('JWT payload:', payload);
	if (payload) {
		user.set({
			name: payload.name,
			role: payload.role,
            image: payload.image,
			...payload
		});
		console.log('Authenticated user:', user);
	}
};

export { user };