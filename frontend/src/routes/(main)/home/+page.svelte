<script lang="ts">
	import { onMount } from 'svelte';
	import Button from '$components/Button/Button.svelte';
	import Select from '$components/Select/Select.svelte';
	import { m } from '$lib/paraglide/messages';
	import { getSpecialtyLabel, Specialties } from '$types/enums/specialties';
	import { getWeekdayLabel, Weekdays } from '$types/enums/weekdays';
	import Card from '$components/Card/Card.svelte';
	import { type Doctor, type Insurance, type Paginated } from '$types/api';
	import { fetchDoctors, fetchDoctorsPage } from '$lib/services/doctors';
	import Pagination from '$components/Pagination/Pagination.svelte';
	import { page } from '$app/stores';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import { searchQuery, insurance, day, specialty, order, getFiltersURL } from '$stores/filters';

    let insurances: Insurance[] = $state([]);

	const workDays = [
		{ value: 'all', label: m['all']() },
		...Object.values(Weekdays).map((day) => ({
			value: day,
			label: getWeekdayLabel(day)
		}))
	];

	const specialties = [
		{ value: 'all', label: m['all']() },
		...Object.values(Specialties).map((spec) => ({
			value: spec,
			label: getSpecialtyLabel(spec)
		}))
	];

	const orders = [
		{ value: '', label: '-' },
		{ value: 'M_RECENT', label: m['filters.order.m_recent']() },
		{ value: 'L_RECENT', label: m['filters.order.l_recent']() },
		{ value: 'M_POPULAR', label: m['filters.order.m_popular']() },
		{ value: 'L_POPULAR', label: m['filters.order.l_popular']() }
	];

	let doctors: Paginated<Doctor> = $state({ results: [], _links: {} });
	let filterKey = $state(0);

	async function applyFilters() {
		goto(`?${getFiltersURL($searchQuery, $insurance, $day, $specialty, $order)}`, { replaceState: true, noScroll: true });
		
		doctors = await fetchDoctors($searchQuery, $insurance, $day, $specialty, $order);
		filterKey++; // Force Pagination to remount
	}

	// Watch URL params and refetch when they change
	$effect(() => {
		const urlSearchQuery = $page.url.searchParams.get('search') || '';
		const insuranceParam = $page.url.searchParams.get('insurance') || 'all';
		const dayParam = $page.url.searchParams.get('day') || 'all';
		const specialtyParam = $page.url.searchParams.get('specialty') || 'all';
		const orderParam = $page.url.searchParams.get('order') || '';

		// Sync URL params to stores
		insurance.set(insuranceParam);
		day.set(dayParam);
		specialty.set(specialtyParam);
		order.set(orderParam);

		// Fetch doctors with current URL params
		fetchDoctors(urlSearchQuery, insuranceParam, dayParam, specialtyParam, orderParam).then(result => {
			doctors = result;
			filterKey++;
		});
	});

	// parse self (cut after /api/)
	const parseSelf = (self: string) => {
		const apiIndex = self.indexOf('/api/');
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + 5) : self;
		return toRet;
	};

	onMount(async () => {
		// Fetch insurances list
		const insurancesResponse = await fetch('http://localhost:8080/paw-2025a-15/api/insurances');
		if (insurancesResponse.ok) {
			insurances = await insurancesResponse.json();
		} else {
			console.error('Failed to fetch insurances');
		}
	});
</script>

<div class="flex flex-col max-w-full! gap-3">
	<div class="flex flex-col card w-full bg-white">
		<h2 class="section-title m-0 mb-5">{m['filters.title']()}</h2>
		<div class="grid grid-cols-3 gap-5 items-end">
			<Select
				label={m['filters.label.ensurance']()}
				options={[
					{ value: 'all', label: m['all']() },
					...insurances.map((ins) => ({ 
						value: ins.name, 
						label: ins.name,
						avatarSrc: ins.picture
					}))
				]}
				bind:value={$insurance}
				class="w-full"
			/>

			<Select
				label={m['filters.label.workday']()}
				options={workDays}
				bind:value={$day}
				class="w-full"
			/>

			<Select
				label={m['filters.label.specialty']()}
				options={specialties}
				bind:value={$specialty}
				class="w-full"
			/>

			<div></div>

			<Select label={m['filters.label.order']()} options={orders} bind:value={$order} class="w-full" />
		</div>
		<Button variant="primary" class="w-full mt-5" onclick={applyFilters}>
			{m['filters.apply']()}
		</Button>
	</div>
	<h2 class="section-title m-0">{m['doctors_list']()}</h2>
	<div class="flex flex-col justify-center items-center">
		{#key filterKey}
			<Pagination
				initialFetchFunction={() => fetchDoctors($searchQuery, $insurance, $day, $specialty, $order)}
				pageFetchFunction={(page) => fetchDoctorsPage(page)}
				class="flex flex-wrap justify-center gap-5 mb-3"
			>
				{#snippet loading()}
					{#each Array(10) as _, i}
						<Card 
							variant="doctor"
							avatarSrc=""
							userName=""
							specialization=""
							schedule={new Set<Weekdays>()}
							insurances={[]}
							email=""
							phone=""
							skeleton={true}
						/>
					{/each}
				{/snippet}

				{#snippet children(entry: Doctor, i: number)}
					<Card
						variant="doctor"
						avatarSrc={entry.links.image}
						userName={entry.name}
						specialization={getSpecialtyLabel(entry.specialty)}
						schedule={entry.schedule ? new Set(entry.schedule.keys()) : new Set<Weekdays>()}
						insurances={entry.insurances}
						email={entry.email}
						phone={entry.telephone}
						onclick={() => goto(`${base}/${parseSelf(entry.links.self)}`)}
					/>
				{/snippet}
			</Pagination>
		{/key}
	</div>
</div>
