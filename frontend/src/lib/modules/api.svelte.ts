import type { Session } from '$lib/types/api';

import { base } from '$app/paths';
import { browser } from '$app/environment';

import LinkHeader from 'http-link-header';
import { jwtDecode } from 'jwt-decode';

let tokens = $state({
	session: browser ? (localStorage.session as string) : null,
	refresh: browser ? (localStorage.refresh as string) : null
});

let expiration = $state(null) as Date | null;

export class ApiError extends Error {
	constructor(readonly status: number) {
		super(`API request failed with status ${status}`);
	}
}

/**
 * Send a request to the API
 *
 * @param path The path to the endpoint
 * @param options The fetch options
 * @param fetch The fetch function to use
 * @returns The fetch response
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
export async function send(path: string, options?: RequestInit, fetch = window.fetch) {
	let url;
	try {
		// Check if the path is a valid URL
		url = new URL(path);
	} catch (e) {
		if (!(e instanceof TypeError)) throw e;
		// Assume it's a relative path (/api/...)
		url = new URL(base + path, "");
	}

	console.log('API request:', url.href, options);
	const response = await fetch(url, options);

	if (!response.ok) {
		console.error(response);
		throw new ApiError(response.status);
	}

	return response;
}

/**
 * Send an authorized request to the API
 *
 * @note If revalidation is deemed necessary, it will attempt to refresh the token
 *
 * @param path The path to the endpoint
 * @param options The fetch options
 * @param fetch The fetch function to use
 * @returns The fetch response
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
send.auth = async function (path: string, options?: RequestInit, fetch = window.fetch) {
	if (!expiration || expiration < new Date()) {
		await login(undefined, fetch);
	}

	return send(
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

/**
 * Enable authorized requests to the API
 *
 * @param form The data to send to the API.
 * If undefined, it will attempt a token refresh.
 * 			   - email: string
 * 			   - password: string
 * @param fetch The fetch function to use
 * @throws TypeError if the form data is invalid
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails, logging out the user if the status is 401
 */
export async function login(form?: FormData, fetch = window.fetch): Promise<void> {
	let user: string = '';
	let pass: string = '';

	if (form) {
		const e = form.get('email');
		const p = form.get('password');

		if (typeof e !== 'string' || typeof p !== 'string') {
			throw new TypeError('Invalid form data');
		}

		user = e.split('@')[0];
		pass = p;
	} else console.warn('Refreshing');

	let data: Session;
	try {
		// IMPORTANT! Never use send.auth here, as it will cause an infinite loop on revalidation
		const res = await send(
			'/api/sessions',
			{
				method: 'POST',
				headers: {
					Authorization: form
						? 'Basic ' + btoa(String.fromCharCode(...new TextEncoder().encode(`${user}:${pass}`)))
						: `Bearer ${tokens.session}`,
					'X-Refresh-Token': form ? '' : `Bearer ${tokens.refresh}`
				}
			},
			fetch
		);

		data = await res.json();
	} catch (error) {
		if (error instanceof ApiError && error.status === 401) {
			await logout();
		}

		throw error;
	}

	setSession(data);
}

/**
 * Disable authorized requests to the API
 */
export async function logout() {
	tokens.session = null;
	tokens.refresh = null;
	expiration = null;

	if (browser) {
		localStorage.removeItem('session');
		localStorage.removeItem('refresh');
		localStorage.removeItem('username');
	}
}

export async function verify(user: string, token: string, fetch = window.fetch) {
	const body = new FormData();
	body.append('token', token);

	const response = await send(
		`/api/users/${user}`,
		{
			method: 'POST',
			body
		},
		fetch
	);

	setSession(await response.json());
}

export function setSession(data: Session) {
	tokens = data;
	localStorage.session = tokens.session;
	localStorage.refresh = tokens.refresh;
	expiration = new Date(new Date().getTime() + 2 * 60 * 1000);
}

/**
 * Add `_links.prev` and `_links.next` to the response data if the Link header is present
 *
 * @param response The fetch response to parse
 * @returns The response data with `_links.prev` and `_links.next` added if available
 */
async function parsed(response: Awaited<ReturnType<typeof send>>) {
	if (!response.headers.has('Link')) return response.json();

	const data = await response.json();
	const links = LinkHeader.parse(response.headers.get('Link')!);
	console.log('API links:', links.rel('next'));

	data._links = {
		prev: links.rel('prev')[0]?.uri,
		next: links.rel('next')[0]?.uri
	};

	return data;
}

/**
 * Request a resource from the API, or becomes a hanging promise if `null`
 *
 * The hanging promise is intended for cascade loading, so the UI can
 * display a loading skeleton until the resource is available.
 *
 * If the resource has a `Link` header, it will be parsed and added to the
 * response as `_links.prev` and `_links.next`.
 *
 * @param path The path to the resource, null for a hanging promise
 * @param fetch The fetch function to use
 * @returns The resource from the API
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
export async function request<Resource = unknown>(path: string | null, fetch = window.fetch) {
	if (path === null) return new Promise<Resource>(() => {});

	const response = await send(path, undefined, fetch);
	return parsed(response) as Promise<Resource>;
}

/**
 * Request an authorized resource from the API, or becomes a hanging promise if `null`
 *
 * The hanging promise is intended for cascade loading, so the UI can
 * display a loading skeleton until the resource is available.
 *
 * If the resource has a `Link` header, it will be parsed and added to the
 * response as `_links.prev` and `_links.next`.
 *
 * @param path The path to the resource, null for a hanging promise
 * @param fetch The fetch function to use
 * @returns The resource from the API
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
request.auth = async function <Resource = unknown>(
	path: string | null,
	fetch = window.fetch
) {
	if (path === null) return new Promise<Resource>(() => {});

	const response = await send.auth(path, undefined, fetch);
	return parsed(response) as Promise<Resource>;
};

/**
 * Create a resource on the API (POST with Authorization)
 *
 * @param type The type of resource to create
 * @param payload The data to send to the API
 * @param fetch The fetch function to use
 * @returns The response and location of the created resource
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
export async function create<R>(type: string, payload: FormData, fetch = window.fetch) {
	const response = await send.auth(
		`/api/${type}`,
		{
			method: 'POST',
			body: payload
		},
		fetch
	);

	return [await response.json(), response.headers.get('Location')!] as [R, string];
}

/**
 * Upload an image to the API (POST with Authorization)
 *
 * @param file The image to upload
 * @returns The location of the uploaded resource, null on error
 */
export async function upload(file: File) {
	const payload = new FormData();
	payload.append('image', file);

	try {
		const [, url] = await create('images', payload);
		return url;
	} catch (e) {
		console.error('Ups', e);
		return null;
	}
}

/**
 * Create a resource on the API (PUT with Authorization)
 *
 * @param resource The path to the resource to PUT
 * @param fetch The fetch function to use
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 * @returns The location of the updated resource
 */
export async function put(resource: string, fetch = window.fetch) {
	const response = await send.auth(resource, { method: 'PUT' }, fetch);
	return response.headers.get('Location');
}

/**
 * Update a resource on the API (PATCH with Authorization)
 *
 * @param resource The path to the resource to PATCH
 * @param payload The data to send to the API
 * @param fetch The fetch function to use
 */
export async function patch(resource: string, payload?: FormData, fetch = window.fetch) {
	await send.auth(
		resource,
		{
			method: 'PATCH',
			body: payload
		},
		fetch
	);
}

/**
 * Delete a resource on the API (DELETE with Authorization)
 *
 * @param resource The path to the resource to DELETE
 * @param fetch The fetch function to use
 * @throws TypeError if the path is not a valid URL
 * @throws An {@link ApiError} if the request fails
 */
export async function del(resource: string, fetch = window.fetch) {
	await send.auth(resource, { method: 'DELETE' }, fetch);
}

/**
 * Check if a resource is available for the current user
 *
 * @param resource The path to the resource to check
 * @param fetch The fetch function to use
 * @returns True if the resource exists for the current user, false otherwise
 */
export async function exists(resource: string, fetch = window.fetch) {
	try {
		await send.auth(resource, undefined, fetch);
		return true;
	} catch (e) {
		if (e instanceof ApiError && e.status === 404) return false;
		throw e;
	}
}
