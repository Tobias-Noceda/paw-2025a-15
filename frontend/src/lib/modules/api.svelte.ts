import { browser } from '$app/environment';
import { goto } from '$app/navigation';
import { base } from '$app/paths';
import { PUBLIC_API_ORIGIN } from '$env/static/public';
import { user } from '$stores/user';
import type { Session } from '$types/api';
import { error } from '@sveltejs/kit';

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

export async function login(e?: string, pass?: string): Promise<void> {
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
			}
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

export async function get(path: string, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	let url;
	try {
		// Check if the path is a valid URL
		url = new URL(path);
	} catch (e) {
		if (!(e instanceof TypeError)) throw e;
		// Assume it's a relative path (/api/...)
		url = new URL(base + path, PUBLIC_API_ORIGIN);
	}

	let response: Response;
	try {
		response = await fetchFn(url, options);
	} catch (err) {
		// Network errors (CORS, timeout, connection failed) don't have status codes
		console.error('Network error:', err);
		throw error(503, `Network error: Unable to reach server`);
	}

	if (!response.ok) {
		const message = response.statusText || 'Request failed';
		throw error(response.status, message);
	}

	return response;
}

export async function getAuth(path: string, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	let requestToken;
	if (tokens.access && expiration && new Date() <= expiration) {
		requestToken = tokens.access;
	} else if (tokens.refresh) {
		requestToken = tokens.refresh;
	} else {
		// On server-side, throw error instead of calling logout (which uses goto)
		console.log('Tokens: ', tokens);
		if (!browser) {
			throw error(401, 'Authentication required');
		}
		logout();
		throw error(401, 'Session expired');
	}

	// get() already throws proper SvelteKit errors with status codes
	const response = await get(
		path,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		},
		fetchFn
	);

	return response;
};

export async function post(path: string, body: any, options?: RequestInit): Promise<Response> {
	return await fetch(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
			...options?.headers,
		},
		body: JSON.stringify(body)
	});
};

export async function postAuth(path: string, body: any, options?: RequestInit): Promise<Response> {
	let requestToken;
	if (tokens.access && expiration && new Date() <= expiration) {
		requestToken = tokens.access;
	} else if (tokens.refresh) {
		requestToken = tokens.refresh;
	} else {
		if (!browser) {
			throw error(401, 'Authentication required');
		}
		logout();
		throw error(401, 'Session expired');
	}

	return await post(
		path,
		body,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		}
	);
};

export async function put(path: string, body: any, options?: RequestInit): Promise<Response> {
	return await fetch(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'PUT',
		headers: {
			'Content-Type': 'application/json',
			...options?.headers,
		},
		body: JSON.stringify(body)
	});
};

export async function putAuth(path: string, body: any, options?: RequestInit): Promise<Response> {
	let requestToken;
	if (tokens.access && expiration && new Date() <= expiration) {
		requestToken = tokens.access;
	} else if (tokens.refresh) {
		requestToken = tokens.refresh;
	} else {
		if (!browser) {
			throw error(401, 'Authentication required');
		}
		logout();
		throw error(401, 'Session expired');
	}

	return await put(
		path,
		body,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		}
	);
};

export function logout(redirectTo: string = '/login'): void {
	tokens = { access: null, refresh: null };
	user.set(null);
	if (browser) {
		localStorage.removeItem('access');
		localStorage.removeItem('refresh');
	}
	goto(`${base}${redirectTo}`);
}