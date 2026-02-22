import { browser } from '$app/environment';
import { goto, invalidateAll } from '$app/navigation';
import { base } from '$app/paths';
import { PUBLIC_API_ORIGIN } from '$env/static/public';
import { loggedOut, user, userData } from '$stores/user';
import type { Session, UriTemplate } from '$types/api';
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

export function getJWTEmail(token: string): string | null {
	const payload = parseJWT(token);
	console.log('Parsed JWT payload:', payload);
	return payload ? payload.sub : null;
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

		setSession(sessionData);
	} catch (error) {
		throw error;
	}
}

export async function refreshToken(): Promise<void> {
	if (!tokens.refresh) {
		logout();
		return;
	}

	try {
		const res = await get(
			'/api/doctors',
			{
				method: 'GET',
				headers: {
					Authorization: `Bearer ${tokens.refresh}`,
				}
			}
		);

		if (!res.ok) {
			throw new Error(`Refresh failed: ${res.status} ${res.statusText}`);
		}

		let sessionData: Session = {
			access: res.headers.get('X-Access-Token') as string,
			refresh: tokens.refresh
		}

		setSession(sessionData);
	} catch (error) {
		console.error('Token refresh failed:', error);
		logout();
	}
}

function getToken(): string | null {
	if (tokens.access && expiration && new Date() <= expiration) {
		return tokens.access;
	} else if (tokens.refresh) {
		return tokens.refresh;
	} else {
		if (!browser) {
			throw error(401, 'Authentication required');
		}
		logout();
		return null;
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
	const requestToken = getToken();

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

export async function post(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	const isFormData = body instanceof FormData;
	
	return await fetchFn(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'POST',
		headers: {
			...options?.headers,
		},
		// Don't stringify FormData
		body: isFormData ? body : JSON.stringify(body)
	});
};

export async function postAuth(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	const requestToken = getToken();

	return await post(
		path,
		body,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		},
		fetchFn
	);
};

export async function put(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	return await fetchFn(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'PUT',
		headers: {
			...options?.headers,
		},
		body: JSON.stringify(body)
	});
};

export async function putAuth(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	const requestToken = getToken();

	return await put(
		path,
		body,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		},
		fetchFn
	);
};

export async function patch(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	return await fetchFn(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'PATCH',
		headers: {
			'Content-Type': 'application/json',
			...options?.headers,
		},
		body: JSON.stringify(body)
	});
};

export async function patchAuth(path: string, body: any, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	let requestToken = getToken();

	return await patch(
		path,
		body,
		{
			...options,
			headers: {
				...options?.headers,
				Authorization: `Bearer ${requestToken}`
			}
		},
		fetchFn
	);
};

export async function del(path: string, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	return await fetchFn(new URL(path, PUBLIC_API_ORIGIN), {
		...options,
		method: 'DELETE',
		headers: {
			...options?.headers,
		}
	});
};

export async function deleteAuth(path: string, options?: RequestInit, fetchFn: typeof fetch = fetch): Promise<Response> {
	const requestToken = getToken();

	return await del(
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
};

/** Fetch with auth but without SvelteKit error() handling - safe for use in click handlers */
export async function fetchWithAuth(url: string, options?: RequestInit): Promise<Response> {
	const requestToken = getToken();
	return fetch(url, {
		...options,
		headers: {
			...options?.headers,
			...(requestToken ? { Authorization: `Bearer ${requestToken}` } : {})
		}
	});
};

export function resolveNonTemplatedLinks<T extends { links: Record<string, UriTemplate> }>(data: T): T {
	for (const key in data.links) {
		const link = data.links[key];
		if (!link.templated) {
			data.links[key].resolved = link.href;
		}
	}

	return data;
};

export function logout(redirectTo: string = '/home'): void {
	if (!browser) {
		return;
	}
	loggedOut.set(true);
	invalidateAll();
	window.location.href = `${base}${redirectTo}`;
	
	tokens = { access: null, refresh: null };
	user.set(null);
	userData.set(null);
	if (browser) {
		localStorage.removeItem('access');
		localStorage.removeItem('refresh');
	}
};
