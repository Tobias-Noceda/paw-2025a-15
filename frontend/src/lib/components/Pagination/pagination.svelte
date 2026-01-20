<script lang="ts" generics="T">
	import type { Paginated } from '$lib/types/api';
	import { onMount, type Snippet } from 'svelte';

	import { afterNavigate, beforeNavigate } from '$app/navigation';
	import { preserve, restore } from '$modules/statefull.svelte';

	import Button from '$components/Button/Button.svelte';
	import { cn } from '$lib/utils';

	interface Props {
		/**
		 * The function to fetch data from at the start.
		 */
		initialFetchFunction: () => Promise<Paginated<T>>;
		/**
		 * The function to fetch the next page.
		 */
		pageFetchFunction: (nextUrl: string) => Promise<Paginated<T>>;
		/**
		 * The component to render per element.
		 */
		children: Snippet<[T, number]>;
		/**
		 * The loading component to render while fetching the next page.
		 */
		loading: Snippet;
		/**
		 * The error component to render if an error occurs.
		 */
		error?: Snippet;
	}

	let {
		initialFetchFunction,
		pageFetchFunction,
		children,
		loading,
		error,
	}: Props = $props();

    const ammountClass = cn(
        'flex items-center justify-center font-semibold rounded-md px-4 py-2',
        'cursor-pointer transition-colors text-base',
        'bg-bgColor text-primary border-1 border-primaryBorder',
    );

	let entries = $state([] as T[]);

	let initialLoadComplete = $state(false);
	let load = $state(true);
	let done = $state(false);
	let erro = $state(false);

    let first: string | undefined = $state(undefined as string | undefined);
    let prev: string | undefined = $state(undefined as string | undefined);
	let next: string | undefined = $state(undefined as string | undefined);
    let last: string | undefined = $state(undefined as string | undefined);

	let totalPages: number = $state(0);
	let currentPage: number = $state(0);

	async function get(pageUrl: string) {
        load = true;
        entries = [];

		await pageFetchFunction(pageUrl)
			.then((response) => {
				entries = [...response.results];

                first = response._links?.first;
                prev = response._links?.prev;
				next = response._links?.next;
                last = response._links?.last;

				if (response._pageInfo) {
					currentPage = response._pageInfo.currentPage!;
					totalPages = response._pageInfo.totalPages!;
				}
			})
			.catch((e) => {
				console.error('Error in fetch:', e);
				erro = true;
				throw e;
			})
			.finally(() => {
				load = false;
			});
	}

	onMount(() => {
		const doctorsPage = initialFetchFunction();
		doctorsPage.then((data) => {
			entries = data.results;

            first = data._links?.first;
            prev = data._links?.prev;
			next = data._links?.next;
            last = data._links?.last;

			if (data._pageInfo) {
				currentPage = data._pageInfo.currentPage!;
				totalPages = data._pageInfo.totalPages!;
			}
			
            load = false;
			initialLoadComplete = true;
		});
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
{/each}

{#if load}
	{@render loading()}
{/if}

{#if erro && error}
	{@render error()}
{/if}

{#if totalPages > 0}
	<div class="flex w-full h-fit justify-center items-center my-2 gap-2">
		<Button variant="secondary" class="text-sm!" disabled={first === undefined} onclick={() => get(first!)}>&laquo;</Button>
		<Button variant="secondary" class="text-sm!" disabled={prev === undefined} onclick={() => get(prev!)}>&lt;</Button>
		<span class={ammountClass}>{currentPage} / {totalPages}</span>
		<Button variant="secondary" class="text-sm!" disabled={next === undefined} onclick={() => get(next!)}>&gt;</Button>
		<Button variant="secondary" class="text-sm!" disabled={last === undefined} onclick={() => get(last!)}>&raquo;</Button>
	</div>
{/if}
