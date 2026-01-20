import { browser } from '$app/environment';
import { base } from '$app/paths';
import { PUBLIC_API_ORIGIN } from '$env/static/public';
import type { Session } from '$types/api';

let tokens = $state({
	session: browser ? (localStorage.session as string) : null,
	refresh: browser ? (localStorage.refresh as string) : null
});

let expiration = $state(null) as Date | null;

let username = $state(browser ? (localStorage.username as string) : null);

export function setSession(data: Session) {
	tokens = data;
	localStorage.session = tokens.session;
	localStorage.refresh = tokens.refresh;
	expiration = new Date(new Date().getTime() + 2 * 60 * 1000);
}

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
		const res = await get(
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
		throw error;
	}

	if (form) {
		username = user;
		localStorage.username = username;
	}

	setSession(data);
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
		await login(undefined, fetch);
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