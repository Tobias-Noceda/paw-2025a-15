<script lang="ts">
	import { onMount } from 'svelte';
	import Button from '$components/Button/Button.svelte';
	import Select from '$components/Select/Select.svelte';
	import { m } from '$lib/paraglide/messages';
	import { getSpecialtyLabel, Specialties } from '$types/enums/specialties';
	import { getWeekdayLabel, Weekdays } from '$types/enums/weekdays';
	import Card from '$components/Card/Card.svelte';
	import type { Doctor, Insurance, Shift } from '$types/api';
	import Avatar from '$components/Avatar/Avatar.svelte';

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

	let ensurance = $state('all');
	let day = $state('all');
	let specialty = $state('all');
	let order = $state('');

	let doctors: Doctor[] = $state([]);

	const fetchDoctors = async () => {
		try {
			let url = new URL('http://localhost:8080/paw-2025a-15/api/doctors');
			if (ensurance !== 'all') {
				url.searchParams.append('insurance', ensurance);
			}
			if (day !== 'all') {
				url.searchParams.append('weekday', day);
			}
			if (specialty !== 'all') {
				url.searchParams.append('specialty', specialty);
			}
			if (order !== '') {
				url.searchParams.append('orderBy', order);
			}
			const response = await fetch(url.toString());
			if (response.ok) {
				doctors = await response.json();

				await Promise.all(doctors.map(async (doctor) => {
					const response = await fetch(doctor.schedule);

					if (response.ok) {
						const schedule: Shift[] = await response.json();

						const days = new Set<Weekdays>();
						schedule.forEach(shift => {
							days.add(shift.weekday as Weekdays);
						});
						doctor.scheduleDays = days;
					} else {
						throw new Error('Failed to fetch schedule');
					}

                    const responseInsurances = await fetch(doctor.insurances);
                    if (responseInsurances.ok) {
                        const insurancesData: Insurance[] = await responseInsurances.json();
                        doctor.insuranceNames = insurancesData.map(ins => ins.name);
                    } else {
                        throw new Error('Failed to fetch insurances');
                    }
				}));
			}
		} catch (error) {
			console.error('Failed to fetch doctors:', error);
		}
	};

	onMount(async () => {
		await fetchDoctors();

        const insurancesResponse = await fetch('http://localhost:8080/paw-2025a-15/api/insurances');
        if (insurancesResponse.ok) {
            insurances = await insurancesResponse.json();
        } else {
            console.error('Failed to fetch insurances');
        }

        console.log('Insurances:', insurances);
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
			bind:value={ensurance}
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
	<Button variant="primary" class="w-full mt-5" onclick={() => fetchDoctors()}>
		{m['filters.apply']()}
	</Button>
</div>
<h2 class="section-title m-0 mb-3">{m['doctors_list']()}</h2>
<div class="flex flex-wrap justify-center gap-5">
	{#each doctors as doctor}
		<Card
			variant="doctor"
			avatarSrc={doctor.image}
			userName={doctor.name}
			specialization={getSpecialtyLabel(doctor.specialty)}
            schedule={doctor.scheduleDays}
            insurances={doctor.insuranceNames}
			email={doctor.email}
			phone={doctor.phone}
		/>
	{/each}
</div>
