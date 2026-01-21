import { browser } from '$app/environment';
import { goto } from '$app/navigation';
import { base } from '$app/paths';
import { PUBLIC_API_ORIGIN } from '$env/static/public';
import { user } from '$stores/user';
import type { Session } from '$types/api';

export const apiOrigin = PUBLIC_API_ORIGIN; 

let tokens = $state({
	session: browser ? (localStorage.session as string) : null,
	refresh: browser ? (localStorage.refresh as string) : null
});

let expiration = $state(null) as Date | null;

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
		return JSON.parse(jsonPayload);
	} catch (error) {
		console.error('Failed to parse JWT:', error);
		return null;
	}
}

export function setSession(data: Session) {
	tokens = data;
	expiration = new Date(new Date().getTime() + 30 * 24 * 60 * 1000);
	localStorage.session = tokens.session;
	localStorage.refresh = tokens.refresh;
	localStorage.expiration = expiration.toString();
}

export async function login(e?: string, pass?: string, fetch = window.fetch): Promise<void> {
	let email = '';
	let password = '';

	if (e && pass) {
		email = e;
		password = pass;
	} else if (!tokens.session || !tokens.refresh) {
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
						: `Bearer ${tokens.session}`,
					'X-Refresh-Token': email && password ? '' : `Bearer ${tokens.refresh}`
				}
			},
			fetch
		);

		if (!res.ok) {
			throw new Error(`Login failed: ${res.status} ${res.statusText}`);
		}

		let sessionData: Session = {
			session: res.headers.get('X-Auth-Token') as string,
			refresh: res.headers.get('X-Refresh-Token') as string
		}

		console.log('Logged in, session data:', sessionData);

		setSession(sessionData);


	} catch (error) {
		throw error;
	}
}

export async function get(path: string, options?: RequestInit, fetch = window.fetch) {
	let url;
	try {
		// Check if the path is a valid URL
		url = new URL(path);
	} catch (e) {
		if (!(e instanceof TypeError)) throw e;
		// Assume it's a relative path (/api/...)
		url = new URL(base + path, PUBLIC_API_ORIGIN);
	}

	console.log('API request:', url.href, options);
	const response = await fetch(url, options);

	if (!response.ok) {
		console.error(response);
	}

	return response;
}

export async function getAuth(path: string, options?: RequestInit, fetch = window.fetch) {
	if (!expiration || expiration < new Date()) {
		await login(undefined, undefined, fetch);
	}

	return get(
		path,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${tokens.session}`
			}
		},
		fetch
	);
};

export function logout() {
	tokens = { session: null, refresh: null };
	user.set(null);
	expiration = null;
	if (browser) {
		localStorage.removeItem('session');
		localStorage.removeItem('refresh');
		localStorage.removeItem('expiration');
	}
	goto(`${base}/login`);
}