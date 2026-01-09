<script lang="ts">
	import { onMount } from 'svelte';
	import Button from '$components/Button/Button.svelte';
	import Select from '$components/Select/Select.svelte';
	import { m } from '$lib/paraglide/messages';
	import { getSpecialtyLabel, Specialties } from '$types/enums/specialties';
	import { getWeekdayLabel, Weekdays } from '$types/enums/weekdays';
	import Card from '$components/Card/Card.svelte';
	import type { Doctor, Insurance, Paginated } from '$types/api';
	import { fetchDoctors, fetchDoctorsPage } from '$lib/services/doctors';
	import ScrollPagination from '$components/ScrollPagination/ScrollPagination.svelte';
	import Pagination from '$components/Pagination/pagination.svelte';

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

	let insurance = $state('all');
	let day = $state('all');
	let specialty = $state('all');
	let order = $state('');

	let doctors: Paginated<Doctor> = $state({ results: [], _links: {} });

	onMount(async () => {
		doctors = await fetchDoctors(
			insurance,
			day,
			specialty,
			order
		);

        const insurancesResponse = await fetch('http://localhost:8080/paw-2025a-15/api/insurances');
        if (insurancesResponse.ok) {
            insurances = await insurancesResponse.json();
        } else {
            console.error('Failed to fetch insurances');
        }
	});
</script>

<div class="card bg-white mb-3">
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
			bind:value={insurance}
			class="w-full"
		/>

		<Select
			label={m['filters.label.workday']()}
			options={workDays}
			bind:value={day}
			class="w-full"
		/>

		<Select
			label={m['filters.label.specialty']()}
			options={specialties}
			bind:value={specialty}
			class="w-full"
		/>

		<div></div>

		<Select label={m['filters.label.order']()} options={orders} bind:value={order} class="w-full" />
	</div>
	<Button variant="primary" class="w-full mt-5" onclick={async () => {
		doctors = await fetchDoctors(
			insurance,
			day,
			specialty,
			order
		);
	}}>
		{m['filters.apply']()}
	</Button>
</div>
<h2 class="section-title m-0 mb-3">{m['doctors_list']()}</h2>
<div class="flex flex-wrap justify-center gap-5">
	<Pagination
		initialFetchFunction={() => fetchDoctors(insurance, day, specialty, order)}
		pageFetchFunction={(page) => fetchDoctorsPage(page)}
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
				avatarSrc={entry.image}
				userName={entry.name}
				specialization={getSpecialtyLabel(entry.specialty)}
				schedule={entry.scheduleDays}
				insurances={entry.insuranceNames}
				email={entry.email}
				phone={entry.phone}
				onclick={() => console.log(`Clicked on doctor ${entry.name}`)}
			/>
		{/snippet}
	</Pagination>
</div>
