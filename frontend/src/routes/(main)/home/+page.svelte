<script lang="ts">
	import Button from '$components/Button/Button.svelte';
	import Select from '$components/Select/Select.svelte';
	import { m } from '$lib/paraglide/messages';
	import { getSpecialtyLabel, Specialty } from '$types/enums/specialties';
	import { getWeekdayLabel, Weekdays } from '$types/enums/weekdays';
	import Card from '$components/Card/Card.svelte';
	import { type Doctor, type Insurance, type Paginated, type Patient } from '$types/api';
	import { fetchDoctors, fetchDoctorsPage } from '$lib/services/doctors';
	import Pagination from '$components/Pagination/Pagination.svelte';
	import { goto, pushState } from '$app/navigation';
	import { base } from '$app/paths';
	import { searchQuery, insurance, day, specialty, order, getFiltersURL } from '$stores/filters';
	import { type PageData } from './$types';
	import { page } from '$app/stores';
	import { fetchPatients } from '$lib/services/patients';
	import { loggedOut } from '$stores/user';
	import { fetchInsurances, fetchInsurancesPage } from '$lib/services/insurances';
	import Divider from '$components/Divider/Divider.svelte';
	import Icon from '$components/Icon/Icon.svelte';

	let { data }: { data: PageData } = $props();
	let firstLoad = true;

	let userRole = $state(data.userRole);
    let insurances: Paginated<Insurance> = $state({ results: [], _links: {} });
	let doctors: Paginated<Doctor> = $state({ results: [], _links: {} });
	let patients: Paginated<Patient> = $state({ results: [], _links: {} });

	const workDays = [
		{ value: 'all', label: m['all']() },
		...Object.values(Weekdays).map((day) => ({
			value: day,
			label: getWeekdayLabel(day)
		}))
	];

	const specialties = [
		{ value: 'all', label: m['all']() },
		...Object.values(Specialty).map((spec) => ({
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

	let filterKey = $state(0);

	async function applyFilters() {
		pushState(`${base}/home?${getFiltersURL($searchQuery, $insurance, $day, $specialty, $order)}`, { replaceState: true, noScroll: true });
		
		doctors = await fetchDoctors($searchQuery, $insurance, $day, $specialty, $order);
		filterKey++; // Force Pagination to remount
	}

	// Watch URL params and refetch when they change
	$effect(() => {
		if ($loggedOut) {
			return;
		}
		const urlSearchQuery = $page.url.searchParams.get('search') || '';
		let insuranceSearch: string | undefined = undefined;

		if (userRole === 'DOCTOR' && data.patientsLink) {
			fetchPatients(urlSearchQuery, data.patientsLink, fetch).then(result => {
				patients = result;
				console.log('Fetched patients with query:', urlSearchQuery, 'Result:', result);
				filterKey++;
			});
		} else if (userRole === 'ADMIN') {
			insuranceSearch = urlSearchQuery;
		} else {
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
		}

		if (firstLoad || insuranceSearch) {
			fetchInsurances(insuranceSearch, fetch).then(result => {
				insurances = result;
				
				firstLoad = false;
				filterKey++;
			});
		}
	});

	// parse self (cut after /api/)
	const parseSelf = (self: string) => {
		const limitString = '/api/';
		const apiIndex = self.indexOf(limitString);
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + limitString.length) : self;
		return toRet;
	};
</script>

<div class="flex flex-col max-w-full! gap-3">
	{#if userRole === 'DOCTOR'}
		<h2 class="section-title m-0 mb-5">{m['patients_list']()}</h2>
		<div class="flex flex-col justify-center items-center">
			{#key filterKey}
				<Pagination
					initialFetchFunction={() => Promise.resolve(patients)}
					pageFetchFunction={(page) => fetchPatients($searchQuery, page)}
					class="flex flex-wrap justify-center gap-5 mb-3 w-[90%]"
				>
					{#snippet loading()}
						{#each Array(10) as _, i}
							<Card 
								variant="patient"
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

					{#snippet children(entry: Patient, i: number)}
						<Card
							variant="patient"
							avatarSrc={entry.links.image}
							userName={entry.name}
							email={entry.email}
							phone={entry.telephone}
							onclick={() => goto(`${base}/${parseSelf(entry.links.self)}`)}
						/>
					{/snippet}
				</Pagination>
			{/key}
		</div>
	{:else if userRole === 'ADMIN'}
		<div class="flex justify-between items-center w-full">
			<h2 class="section-title m-0 text-start after:w-[80%]!">{m['insurances.title']()}</h2>
			<Button
				variant="primary"
				onclick={() => goto(`${base}/admin/insurances/new`)}
			>
				<Icon name="plus" class="w-4 h-4 mr-2" />
				{m['insurances.new']()}
			</Button>
		</div>
		<Divider class="w-full bg-[#D0D5DD] h-px" />
		<div class="flex flex-col justify-center items-center">
			{#key filterKey}
				<Pagination
					initialFetchFunction={() => {
						return Promise.resolve(insurances)
					}}
					pageFetchFunction={(page) => fetchInsurancesPage(page, fetch)}
					class="flex flex-wrap justify-center gap-5 mb-3 w-[90%]"
				>
					{#snippet loading()}
						{#each Array(10) as _, i}
							<Card 
								variant="insurance"
								avatarSrc=""
								userName=""
								skeleton={true}
							/>
						{/each}
					{/snippet}

					{#snippet children(entry: Insurance, _)}
						<Card
							variant="insurance"
							avatarSrc={entry.links.image}
							userName={entry.name}
						>
							{#snippet buttons()}
								<div class="flex w-full justify-center gap-4 mt-0 mx-4">
									<Button
										variant="primary"
										class="flex w-full"
										onclick={() => goto(`${base}/admin/${parseSelf(entry.links.self)}`)}
									>
										{m['insurances.buttons.edit']()}
									</Button>
									<Button
										variant="destructive"
										class="flex w-full"
										onclick={() => goto(`${base}/admin/${parseSelf(entry.links.self)}`)}
									>
										{m['insurances.buttons.delete']()}
									</Button>
								</div>
							{/snippet}
						</Card>
					{/snippet}
				</Pagination>
			{/key}
		</div>
	{:else}
		<div class="flex flex-col card w-full bg-white">
			<h2 class="section-title m-0 mb-5">{m['filters.title']()}</h2>
			<div class="grid grid-cols-3 gap-5 items-end">
				<Select
					label={m['filters.label.ensurance']()}
					options={{
						...insurances,
						results: [
							{ value: 'all', label: m['all']() },
							...insurances.results.map((ins) => ({
								value: ins.name,
								label: ins.name,
								avatarSrc: ins.links.image
							}))
						],
					}}
					fetchNextOptions={async () => {
						if (insurances._links.next) {
							const newInsurances = fetchInsurancesPage(insurances._links.next, fetch);

							return newInsurances.then((data) => {
								return {
									results: [...data.results.map((ins) => ({ 
										value: ins.name, 
										label: ins.name,
										avatarSrc: ins.links.image
									}))],
									_links: data._links
								};
							});
						}
						return Promise.resolve({ results: [], _links: {} });
					}}
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
					initialFetchFunction={() => Promise.resolve(doctors)}
					pageFetchFunction={(page) => fetchDoctorsPage(page, undefined, fetch)}
					class="flex flex-wrap justify-center gap-5 mb-3 w-[90%]"
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
	{/if}
</div>
