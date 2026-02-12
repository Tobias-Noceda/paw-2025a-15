<script lang="ts">
	import { page } from '$app/stores';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import type { Paginated, Patient, Study } from '$types/api';
	import type { PageData } from './$types';

	import { m } from '$lib/paraglide/messages';
	import Divider from '$components/Divider/Divider.svelte';
	import Button from '$components/Button/Button.svelte';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import { goto } from '$app/navigation';
	import Toast from '$components/Toast/Toast.svelte';
	import { base } from '$app/paths';
	import { getLocale } from '$lib/paraglide/runtime';
	import { StudyType } from '$types/enums/studyTypes';
	import Select from '$components/Select/Select.svelte';
	import { StudyOrders } from '$types/enums/studyOrders';
	import { fetchStudies, fetchStudiesPage } from '$lib/services/studies';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';

	let { data }: { data: PageData } = $props();

	let patient: Patient = $state(data.patient);
	let isFetching = $state(false);

	let showErrorToast = $state(false);

	let selectedStudyType: string = $state(data.studyType ?? 'all');
	let selectedStudyOrder: string = $state(data.order ?? 'm_recent');

	let studies: Paginated<Study> = $state(data.studies ?? { _links: {}, results: [] });

	const getAgeFromBirthdate = (birthdate: string): number => {
		const birthDateObj = new Date(birthdate);
		const today = new Date();
		let age = today.getFullYear() - birthDateObj.getFullYear();
		const monthDiff = today.getMonth() - birthDateObj.getMonth();
		if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDateObj.getDate())) {
			age--;
		}
		return age;
	};

	const parseStudyId = (studyUrl: string): string | null => {
		const keyword = '/studies/';
		const parts = studyUrl.split(keyword);
		return parts[parts.length - 1] || null;
	};

	const studyTypeOptions = Object.entries(StudyType)
		.map(([key, value]) => ({
			label: m[`studies.types.options.${key.toLowerCase()}`](),
			value: value
		}));

	const studyOrderOptions = [
		...Object.entries(StudyOrders).map(([key, value]) => ({
			label: m[`studies.orders.options.${key.toLowerCase()}`](),
			value: value
		}))
	];

	const tableColumns: Column<Study>[] = [
		{
			id: 'type',
			label: m['studies.table.type'](),
			render: (study: Study) => {
				return m[`studies.types.options.${study.type.toLowerCase()}`]();
			},
			class: 'font-medium'
		},
		{
			id: 'details',
			label: m['studies.table.details'](),
			render: (study: Study) => study.comment,
			class: 'text-start'
		},
		{
			id: 'date',
			label: m['studies.table.date'](),
			render: (study: Study) => {
				const date = parseDateInLocalTimezone(study.studyDate);
				return date.toLocaleDateString(getLocale(), {
					year: 'numeric',
					month: '2-digit',
					day: '2-digit'
				});
			},
			class: 'text-start'
		},
		{
			id: 'upload-date',
			label: m['studies.table.upload_date'](),
			render: (study: Study) => {
				const date = new Date(study.uploadDate);
				return date.toLocaleDateString(getLocale(), {
					year: 'numeric',
					month: '2-digit',
					day: '2-digit'
				});
			},
			class: 'text-start'
		}
	];

	const handleStudySearch = async () => {
		try {
			if (patient.links.resolvedStudies === undefined) {
				return;
			}

			isFetching = true;

			const pageUrl = new URL($page.url);

			if (selectedStudyType !== 'all') {
				pageUrl.searchParams.set('type', selectedStudyType);
			} else {
				pageUrl.searchParams.delete('type');
			}

			if (selectedStudyOrder === 'l_recent') {
				pageUrl.searchParams.set('order', selectedStudyOrder);
			} else {
				pageUrl.searchParams.delete('order');
			}

			studies = await fetchStudies(
				patient.links.resolvedStudies,
				selectedStudyType,
				selectedStudyOrder,
				fetch
			);

			goto(pageUrl.toString());
		} catch (error) {
			console.error('Error fetching studies:', error);
			showErrorToast = true;
		} finally {
			isFetching = false;
		}
	};
</script>

<div class="flex gap-5">
	<div class="page-division flex-col min-w-[350px]! w-[350px]! sticky">
		<div class="patient-card-inner inner-flex flex-col select-text">
			<h1 class="text-[24px] font-bold mb-1">{patient.name}</h1>
			<div class="flex w-full h-60 justify-center items-center">
				<div class="flex w-[200px] h-[200px]">
					<Avatar size="auto" src={patient.links.image} class="bg-primary" />
				</div>
			</div>

			<div class="flex w-full justify-center">
				<Button
					variant="secondary"
					class="w-fit py-1! px-4! text-md!"
					onclick={() => {
						goto($page.url.pathname.replace('patients', 'upload-study'));
					}}
				>
					{m['patient.actions.upload']()}
				</Button>
			</div>

			<h2 class="patient-section-title">{m['patient.titles.basic']()}</h2>
			<Divider class="my-4 bg-skeleton h-0.5" size="auto" />

			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none">{m['patient.labels.email']()}:</span>
				{patient.email}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none"
					>{m['patient.labels.telephone']()}:</span
				>
				{patient.telephone}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none">{m['patient.labels.age']()}:</span>
				{patient.birthdate ? getAgeFromBirthdate(patient.birthdate) : m['patient.values.unknown']()}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none"
					>{m['patient.labels.blood_type']()}:</span
				>
				{patient.bloodType ?? m['patient.values.unknown']()}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none">{m['patient.labels.height']()}:</span>
				{patient.height ?? m['patient.values.unknown']()}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none">{m['patient.labels.weight']()}:</span>
				{patient.weight ?? m['patient.values.unknown']()}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText select-none"
					>{m['patient.labels.insurance']()}:</span
				>
				{patient.insurance ?? m['patient.values.unknown']()}
			</p>
			{#if patient.insurance}
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText">{m['patient.labels.insurance_number']()}:</span>
					{patient.insuranceNumber ?? m['patient.values.unknown']()}
				</p>
			{/if}

			{#if patient.smokes != null || patient.drinks != null || patient.diet}
				<h2 class="patient-section-title">{m['patient.titles.habits']()}</h2>
				<Divider class="my-4 bg-skeleton h-0.5" size="auto" />

				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none">{m['patient.labels.smokes']()}:</span
					>
					{patient.smokes === undefined
						? m['patient.values.unknown']()
						: m[`patient.values.${patient.smokes}`]()}
				</p>
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none">{m['patient.labels.drinks']()}:</span
					>
					{patient.drinks === undefined
						? m['patient.values.unknown']()
						: m[`patient.values.${patient.drinks}`]()}
				</p>
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none">{m['patient.labels.diet']()}:</span>
					{patient.diet ?? m['patient.values.unknown']()}
				</p>
			{/if}

			{#if patient.meds || patient.conditions || patient.allergies}
				<h2 class="patient-section-title">{m['patient.titles.medical']()}</h2>
				<Divider class="my-4 bg-skeleton h-0.5" size="auto" />

				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none"
						>{m['patient.labels.medications']()}:</span
					>
					{patient.meds ?? m['patient.values.unknown']()}
				</p>
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none"
						>{m['patient.labels.conditions']()}:</span
					>
					{patient.conditions ?? m['patient.values.unknown']()}
				</p>
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none"
						>{m['patient.labels.allergies']()}:</span
					>
					{patient.allergies ?? m['patient.values.unknown']()}
				</p>
			{/if}

			{#if patient.hobbies || patient.job}
				<h2 class="patient-section-title">{m['patient.titles.social']()}</h2>
				<Divider class="my-4 bg-skeleton h-0.5" size="auto" />

				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none"
						>{m['patient.labels.hobbies']()}:</span
					>
					{patient.hobbies ?? m['patient.values.unknown']()}
				</p>
				<p class="text-line text-secondaryText">
					<span class="font-bold text-primaryText select-none">{m['patient.labels.job']()}:</span>
					{patient.job ?? m['patient.values.unknown']()}
				</p>
			{/if}
		</div>
	</div>

	<div class="page-division flex flex-col w-full gap-2.5">
		<h1 class="text-[24px] font-bold">{m['studies.title']()}:</h1>
		<div class="grid grid-cols-3 gap-5 h-fit">
			<Select
				label={`${m['studies.types.title']()}:`}
				options={[{ value: 'all', label: m['all']() }, ...studyTypeOptions]}
				bind:value={selectedStudyType}
				class="w-full"
			/>
			<Select
				label={`${m['studies.orders.title']()}:`}
				options={studyOrderOptions}
				bind:value={selectedStudyOrder}
				class="w-full"
			/>
			<div class="flex justify-start items-end">
				<Button
					variant="secondary"
					class="w-fit ml-2 px-5 py-2.5"
					onclick={handleStudySearch}
				>
					{m['patient.actions.apply']()}
				</Button>
			</div>
		</div>
		<Table
			rows={data.studies}
			nextFetchFunction={(url) => {
				return fetchStudiesPage(url, fetch);
			}}
			columns={tableColumns}
			striped
			hover
			bordered
			skeleton={isFetching}
			emptyMessage={m['studies.table.empty']()}
			onRowClick={(study) => {
				const studyId = parseStudyId(study.links.self);
				if (studyId) {
					goto(`${base}/study-info/${studyId}`);
				} else {
					console.error('Could not parse study ID from URL:', study.links.self);
				}
			}}
		/>
	</div>

	<Toast
		show={showErrorToast}
		variant="destructive"
		title={m['patient.error.fetching.title']()}
		description={m['patient.error.fetching.message']()}
	/>
</div>

<style>
	.patient-card-inner {
		height: 100%;
		overflow-y: auto;
		/* Hide scrollbar for all browsers */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE and Edge */
	}

	.patient-card-inner::-webkit-scrollbar {
		display: none; /* Chrome, Safari and Opera */
	}

	.text-line {
		line-height: 1.4;
		margin-bottom: 1rem;
	}

	.patient-section-title {
		display: block;
		user-select: none;
		margin: 0;
		margin-top: 20px;
		padding: 6px 12px;
		font-size: 1rem;
		font-weight: 600;
		color: #ffffff;
		background-color: #2e4a7d;
		border-radius: 4px;
		line-height: 1.3;
	}
</style>
