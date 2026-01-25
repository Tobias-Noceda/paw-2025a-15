import { browser } from '$app/environment';
import { goto } from '$app/navigation';
import { base } from '$app/paths';
import { PUBLIC_API_ORIGIN } from '$env/static/public';
import { user } from '$stores/user';
import type { Session } from '$types/api';

export const apiOrigin = PUBLIC_API_ORIGIN; 

let tokens = $state({
	access: browser ? (localStorage.access as string) : null,
	refresh: browser ? (localStorage.refresh as string) : null
});

let expiration = $state<Date | null>(browser && localStorage.expiration ? new Date(localStorage.expiration) : null);

export function parseJWT(token: string) {
	try {
		const base64Url = token.split('.')[1];
		const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
		const jsonPayload = decodeURIComponent(
			atob(base64)
				.split('')
				.map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
				.join('')
		);

		expiration = new Date(JSON.parse(jsonPayload).exp * 1000);
		localStorage.expiration = expiration.toString();

		return JSON.parse(jsonPayload);
	} catch (error) {
		console.error('Failed to parse JWT:', error);
		return null;
	}
}

export function setSession(data: Session) {
	tokens = data;
	localStorage.access = tokens.access;
	localStorage.refresh = tokens.refresh;
}

export async function login(e?: string, pass?: string, fetch = window.fetch): Promise<void> {
	let email = '';
	let password = '';

	if (e && pass) {
		email = e;
		password = pass;
	} else if (!tokens.access || !tokens.refresh) {
		goto(`${base}/login`);
		return;
	}

	try {
		const res = await get(
			'/api/doctors',
			{
				method: 'GET',
				headers: {
					Authorization: email && password
						? 'Basic ' + btoa(String.fromCharCode(...new TextEncoder().encode(`${email}:${password}`)))
						: `Bearer ${tokens.access}`,
					'X-Refresh-Token': email && password ? '' : `Bearer ${tokens.refresh}`
				}
			},
			fetch
		);

		if (!res.ok) {
			throw new Error(`Login failed: ${res.status} ${res.statusText}`);
		}

		let sessionData: Session = {
			access: res.headers.get('X-Access-Token') as string,
			refresh: res.headers.get('X-Refresh-Token') as string
		}

		console.log('Logged in, session data:', sessionData);

		setSession(sessionData);
	} catch (error) {
		throw error;
	}
}

export async function get(path: string, options?: RequestInit, fetch = window.fetch): Promise<Response> {
	let url;
	try {
		// Check if the path is a valid URL
		url = new URL(path);
	} catch (e) {
		if (!(e instanceof TypeError)) throw e;
		// Assume it's a relative path (/api/...)
		url = new URL(base + path, PUBLIC_API_ORIGIN);
	}

	const response = await fetch(url, options)

	return response;
}

export async function getAuth(path: string, options?: RequestInit, fetch = window.fetch): Promise<Response> {
	if (!tokens.access) {
		logout();
		return Promise.reject('No access token available');
	}

	if (expiration && new Date() <= expiration) {
		return await get(
			path,
			{
				...options,
				headers: {
					...options?.headers,
					Authorization: `Bearer ${tokens.access}`
				}
			},
			fetch
		);
	} else if (tokens.refresh) {
		return await get(
			path,
			{
				...options,
				headers: {
					...options?.headers,
					Authorization: `Bearer ${tokens.refresh}`
				}
			},
			fetch
		);
	} else {
		logout();
		return Promise.reject('Session expired');
	}
};

export function logout() {
	tokens = { access: null, refresh: null };
	user.set(null);
	if (browser) {
		localStorage.removeItem('access');
		localStorage.removeItem('refresh');
	}
	goto(`${base}/login`);
}