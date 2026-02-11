<script lang="ts">
	import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import ButtonCell from '$components/Table/ButtonCell.svelte';
	import { m } from '$lib/paraglide/messages';
	import type { Doctor, Paginated, Study } from '$types/api';
	import type { PageData } from './$types';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import { page } from '$app/stores';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Select from '$components/Select/Select.svelte';
	import { StudyType } from '$types/enums/studyTypes';
	import { StudyOrders } from '$types/enums/studyOrders';
	import { fetchStudies, fetchStudiesPage } from '$lib/services/studies';
	import { fetchDoctorsPage, putAuthorizations } from '$lib/services/doctors';
	import { onMount } from 'svelte';

	let { data }: { data: PageData } = $props();

	let isFetching = $state(false);

	let showErrorToast = $state(false);

	let selectedStudyType: string = $state(data.studyType ?? 'all');
	let selectedStudyOrder: string = $state(data.order ?? 'm_recent');

	let studies: Paginated<Study> = $state(data.studies ?? { _links: {}, results: [] });
	const studiesLink: string | undefined = $state(data.studiesLink);

	let doctors: Paginated<Doctor> = $state(data.doctors ?? { _links: {}, results: [] });

	const parseStudyId = (studyUrl: string): string | null => {
		const keyword = '/studies/';
		const parts = studyUrl.split(keyword);
		return parts[parts.length - 1] || null;
	};

	// parse self (cut after /api/)
	const parseDoctor = (self: string) => {
		const limitString = '/api/';
		const apiIndex = self.indexOf(limitString);
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + limitString.length) : self;
		return toRet;
	};

	const studyTypeOptions = Object.entries(StudyType).map(([key, value]) => ({
		label: m[`studies.types.options.${key.toLowerCase()}`](),
		value: value
	}));

	const studyOrderOptions = [
		...Object.entries(StudyOrders).map(([key, value]) => ({
			label: m[`studies.orders.options.${key.toLowerCase()}`](),
			value: value
		}))
	];

	const studiesColumns: Column<Study>[] = [
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
		},
		{
			id: 'study-action',
			label: m['action'](),
			render: (study: Study, index: number) => {
				return {
					component: ButtonCell,
					props: {
						text: 'trash',
						icon: true,
						variant: 'destructive',
						onclick: (e: MouseEvent) => {
							e.stopPropagation();
							e.preventDefault();

							console.log('Delete action for study:', study);
						}
					}
				};
			},
			class: 'flex justify-center items-center text-center',
			columnClass: 'w-20'
		}
	];

	const doctorColumns: Column<Doctor>[] = [
		{
			id: 'name',
			label: m['studies.doctors.table.name'](),
			render: (doctor: Doctor) => doctor.name,
			class: 'font-medium'
		},
		{
			id: 'specialty',
			label: m['studies.doctors.table.specialty'](),
			render: (doctor: Doctor) => m[`specialties.${doctor.specialty}`](),
			class: 'text-start'
		},
		{
			id: 'doctor-action',
			label: m['action'](),
			render: (doctor: Doctor, index: number) => {
				return {
					component: ButtonCell,
					props: {
						text: m['studies.doctors.actions.revoke'](),
						variant: 'destructive',
						onclick: (e: MouseEvent) => {
							e.stopPropagation();
							e.preventDefault();

							putAuthorizations(doctor.links.authorization.resolved!, false, [])
                                .catch((error) => {
                                    console.error(`Error deauthorizing doctor ${doctor.name}:`, error);
                                });

                            doctors.results.splice(index, 1);
						},
						class: 'px-2 py-1 text-sm font-semibold'
					}
				};
			},
			class: 'flex justify-center items-center text-center',
			columnClass: 'w-34'
		}
	];

	const handleStudySearch = async () => {
		try {
			if (studiesLink === undefined) {
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

			studies = await fetchStudies(studiesLink, selectedStudyType, selectedStudyOrder, fetch);

			goto(pageUrl.toString());
		} catch (error) {
			console.error('Error fetching studies:', error);
			showErrorToast = true;
		} finally {
			isFetching = false;
		}
	};

	const handleDeauthorizeAll = async () => {
        let doctorsList = doctors.results;

        while (doctors._links.next) {
            doctors = await fetchDoctorsPage(doctors._links.next, data.currentUser, fetch);
            doctorsList = [...doctorsList, ...doctors.results];
        }

		for (const doctor of doctorsList) {
			putAuthorizations(doctor.links.authorization.resolved!, false, [])
                .catch((error) => {
                    console.error(`Error deauthorizing doctor ${doctor.name}:`, error);
                });
		}

		doctors = {
			results: [],
			_links: {}
		};
	};
</script>

<div class="flex gap-5">
	<div class="page-division flex flex-col h-full w-full gap-2.5">
		<div class="flex flex-row justify-between items-center w-full h-fit">
			<p class="title text-primaryText">{m['studies.title']()}:</p>
			<Button variant="secondary" class="w-fit" onclick={() => goto(`${base}/studies/upload/${data.currentUser.id}`)}>
				{m['studies.actions.upload']()}
			</Button>
		</div>
		<p class="text-[#555] text-[14px]">
			{m['studies.description']()}
		</p>
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
				<Button variant="secondary" class="w-fit ml-2 px-5 py-2.5" onclick={handleStudySearch}>
					{m['patient.actions.apply']()}
				</Button>
			</div>
		</div>
		<Table
			rows={studies}
			nextFetchFunction={(url) => {
				return fetchStudiesPage(url, fetch);
			}}
			columns={studiesColumns}
			striped
			hover
			bordered
			skeleton={isFetching}
			emptyMessage={m['studies.table.empty']()}
			onRowClick={(study) => {
				const studyId = parseStudyId(study.links.self);
				if (studyId) {
					goto(`${base}/study-info/${studyId}-${data.currentUser.id}`);
				} else {
					console.error('Could not parse study ID from URL:', study.links.self);
				}
			}}
		/>
	</div>

	<div class="page-division flex flex-col h-full w-full gap-2.5">
		<div class="flex flex-row justify-between items-center w-full h-fit">
			<p class="title text-primaryText">{m['studies.doctors.title']()}:</p>
			<Button variant="destructive" class="w-fit" onclick={handleDeauthorizeAll}>
				{m['studies.doctors.actions.revoke_all']()}
			</Button>
		</div>
		<p class="text-[#555] text-[14px]">
			{m['studies.doctors.description']()}
		</p>
		<Table
			rows={doctors}
			nextFetchFunction={(url) => {
				return fetchDoctorsPage(url, data.currentUser, fetch);
			}}
			columns={doctorColumns}
			striped
			hover
			bordered
			emptyMessage={m['studies.doctors.table.empty']()}
			onRowClick={(doctor) => {
				const doctorPath = parseDoctor(doctor.links.self);
				if (doctorPath) {
					goto(`${base}/${doctorPath}`);
				}
			}}
		/>
	</div>
</div>

<style>
	.title {
		font-size: 22px;
		font-weight: 700;
	}
</style>
