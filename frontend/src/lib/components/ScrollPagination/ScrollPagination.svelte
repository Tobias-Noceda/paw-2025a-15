<script lang="ts" generics="T">
	import type { Paginated } from '$lib/types/api';
	import { onMount, type Snippet } from 'svelte';

	import { request } from '$modules/api.svelte';
	import { base } from '$app/paths';
	import { afterNavigate, beforeNavigate } from '$app/navigation';
	import { preserve, restore } from '$modules/statefull.svelte';

	import * as m from '$lib/paraglide/messages';

	interface Props {
		/**
		 * The URL to the first page of the paginated resources.
		 * @note origin and base must not be included.
		 */
		href: string;
		/**
		 * The start param.
		 */
		start?: number;
		/**
		 * The limit param.
		 */
		limit?: number;
		/**
		 * Wether the user must be authenticated to fetch the resources.
		 */
		auth?: boolean;
		/**
		 * The component to render per element.
		 */
		children: Snippet<[T, number]>;
		/**
		 * The loading component to render while fetching the next page.
		 */
		loading: Snippet;
	}

	let {
		href,
		start = 0,
		limit = 25,
		auth = false,
		children,
		loading
	}: Props = $props();

	let entries = $state([] as T[]);

	let load = $state(true);
	let done = $state(false);
	let erro = $state(false);

	const req = $derived(!auth ? request : request.auth);

	let next: string | undefined = `${href}?start=${start}&limit=${limit}`;

	async function get() {
		if (!next) return;

		console.log('Loading next page');

		// Prevent multiple requests
		const url = next;
		next = undefined;

		load = true;

		try {
			const response = await req<Paginated<T>>(url);

			entries.push(...response.results);
			next = response._links?.next;
		} catch (e) {
			console.error(e);
			erro = true;
			throw e;
		} finally {
			load = false;

			if (!next) {
				observer.disconnect();
				done = true;
			}
		}
	}

	let sentinel: HTMLElement | null = $state(null);
	let observer: IntersectionObserver;
	onMount(() => {
		observer = new IntersectionObserver(
			(entries) => {
				if (entries[0].isIntersecting) get();
			},
			{
				root: null,
				rootMargin: '100px',
				threshold: 1.0
			}
		);

		observer.observe(sentinel!);
	});

	let scrollY: number = $state(0);

	type State = {
		next: string | undefined;
		load: boolean;
		done: boolean;
		entries: T[];
		scrollY: number;
	};

	beforeNavigate(({ from, to }) => {
		preserve(from, to, {
			next,
			load,
			done,
			entries,
			scrollY
		} satisfies State);
	});

	afterNavigate(({ from, to }) => {
		const data = restore<State>(from, to);

		if (data) {
			next = data.next;
			load = data.load;
			done = data.done;
			entries = data.entries;

			// Nasty
			setTimeout(() => (scrollY = data.scrollY), 500);
		}
	});
</script>

<svelte:window bind:scrollY />

{#each entries as e, i}
	{@render children(e, i)}
{:else}
	{#if done}
		<div class="flex flex-col items-center">
			<h3 class="text-lg font-bold">{m.no_data_available()}</h3>
			<img
				src="https://cdn.dribbble.com/users/331307/screenshots/4688503/robot-drib.gif"
				alt="No results found"
			/>
		</div>
	{/if}
{/each}

{#if !done}
	<div bind:this={sentinel} class="h-0.5" data-testid="done-test"></div>
{/if}

{#if load}
	{@render loading()}
{/if}

{#if erro}
	<!-- TODO: Error handling -->
	<!-- {@render error()} -->
{/if}
