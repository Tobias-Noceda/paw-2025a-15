<script lang="ts">
	import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import ButtonCell from '$components/Table/ButtonCell.svelte';
	import { m } from '$lib/paraglide/messages';
	import type { Doctor, Paginated, Study } from '$types/api';
	import type { PageData } from './$types';
	import { goto, pushState } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import { page } from '$app/stores';
	import Select from '$components/Select/Select.svelte';
	import { StudyType } from '$types/enums/studyTypes';
	import { StudyOrders } from '$types/enums/studyOrders';
	import { deleteStudy, fetchStudies, fetchStudiesPage } from '$lib/services/studies';
	import { fetchDoctorsPage, putAuthorizations } from '$lib/services/doctors';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Toast from '$components/Toast/Toast.svelte';

	let { data }: { data: PageData } = $props();

	let isFetching = $state(false);

	let toastTitle: string = $state('');
	let toastMessage: string = $state('');
	let showErrorToast = $state(false);
	let showSuccessToast = $state(false);

	$effect(() => {
		if (!showErrorToast && !showSuccessToast) {
			toastTitle = '';
			toastMessage = '';
			return;
		}
	});

	let deleteStudyData: { study: Study; idx: number } | null = $state(null);

	let selectedStudyType: string = $state(data.studyType ?? 'all');
	let selectedStudyOrder: string = $state(data.order ?? 'm_recent');

	let studies: Paginated<Study> = $state(data.studies ?? { _links: {}, results: [] });
	const studiesLink: string | undefined = $state(data.studiesLink);

	let doctors: Paginated<Doctor> = $state(data.doctors ?? { _links: {}, results: [] });

	const parseStudyId = (studyUrl: string): string | null => {
		if (!studyUrl) {
			return null;
		}
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
		label: key ? m[`studies.types.options.${key.toLowerCase()}`]() : '',
		value: value
	}));

	const studyOrderOptions = [
		...Object.entries(StudyOrders).map(([key, value]) => ({
			label: key ? m[`studies.orders.options.${key.toLowerCase()}`]() : '',
			value: value
		}))
	];

	const studiesColumns: Column<Study>[] = [
		{
			id: 'type',
			label: m['studies.table.type'](),
			render: (study: Study) => {
				return study.type
					? m[`studies.types.options.${study.type.replace(/\s/g, '_').toLowerCase()}`]()
					: '';
			},
			class: 'font-medium',
			columnClass: 'w-56'
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
				const date = parseDateInLocalTimezone(study.uploadDate);
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

							deleteStudyData = { study, idx: index };
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
			render: (doctor: Doctor) => doctor.name || '',
			class: 'font-medium'
		},
		{
			id: 'specialty',
			label: m['studies.doctors.table.specialty'](),
			render: (doctor: Doctor) => (doctor.specialty ? m[`specialties.${doctor.specialty}`]() : ''),
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

							putAuthorizations(doctor.links.authorization.resolved!, false, []).catch((error) => {
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
				pageUrl.searchParams.set('type', selectedStudyType.replace(/\s/g, '_'));
			} else {
				pageUrl.searchParams.delete('type');
			}

			if (selectedStudyOrder === 'l_recent') {
				pageUrl.searchParams.set('order', selectedStudyOrder);
			} else {
				pageUrl.searchParams.delete('order');
			}

			studies = await fetchStudies(studiesLink, selectedStudyType, selectedStudyOrder, fetch);

			pushState(pageUrl.toString(), {});
		} catch (error) {
			console.error('Error fetching studies:', error);
			toastTitle = m['studies.messages.error.fetching.title']();
			toastMessage = m['studies.messages.error.fetching.message']();
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
			putAuthorizations(doctor.links.authorization.resolved!, false, []).catch((error) => {
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
			<Button
				variant="secondary"
				class="w-fit"
				onclick={() => goto(`${base}/studies/${data.currentUser.id}`)}
			>
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
				const studyId = parseStudyId(study.links.self.resolved!);
				if (studyId) {
					goto(`${base}/study-info/${studyId}-${data.currentUser.id}`);
				} else {
					console.error('Could not parse study ID from URL:', study.links.self.resolved);
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
				const doctorPath = parseDoctor(doctor.links.self.resolved!);
				if (doctorPath) {
					goto(`${base}/${doctorPath}`);
				}
			}}
		/>
	</div>

	{#if deleteStudyData}
		<PopUp onClose={() => (deleteStudyData = null)}>
			<div class="flex flex-col gap-4">
				<h2 class="text-xl font-bold">{m['studies.pop_up.delete.title']()}</h2>
				<div class="flex flex-col gap-1.5">
					<p>
						{m['studies.pop_up.delete.date_info']({
							uploadDate: parseDateInLocalTimezone(deleteStudyData.study.uploadDate).toLocaleDateString(
								getLocale(),
								{
									year: 'numeric',
									month: '2-digit',
									day: '2-digit'
								}
							)
						})}
					</p>
					<p class="font-semibold">
						{m['studies.pop_up.delete.comment_label']()}
						<span class="font-normal select-text"> {deleteStudyData.study.comment}</span>
					</p>
				</div>
				<p class="text-red-600 font-semibold">{m['studies.pop_up.delete.subtitle']()}</p>
				<div class="flex justify-end gap-2 mt-4">
					<Button
						variant="primary"
						onclick={async () => {
							try {
								await deleteStudy(deleteStudyData!.study.links.self.resolved!, fetch);
								studies.results.splice(deleteStudyData!.idx, 1);
								toastTitle = m['studies.messages.success.deleting.title']();
								toastMessage = m['studies.messages.success.deleting.message']();
								showSuccessToast = true;
							} catch (error) {
								console.error('Error deleting study:', error);
								toastTitle = m['studies.messages.error.deleting.title']();
								toastMessage = m['studies.messages.error.deleting.message']();
								showErrorToast = true;
							} finally {
								deleteStudyData = null;
							}
						}}
					>
						{m['studies.pop_up.delete.confirm']()}
					</Button>
					<Button variant="destructive" onclick={() => (deleteStudyData = null)}>
						{m['studies.pop_up.delete.cancel']()}
					</Button>
				</div>
			</div>
		</PopUp>
	{/if}

	{#if toastTitle.trim() !== '' && toastMessage.trim() !== ''}
		<Toast
			bind:show={showErrorToast}
			title={toastTitle}
			description={toastMessage}
			variant={'destructive'}
		/>
		<Toast
			bind:show={showSuccessToast}
			title={toastTitle}
			description={toastMessage}
			variant={'success'}
		/>
	{/if}
</div>

<style>
	.title {
		font-size: 22px;
		font-weight: 700;
	}
</style>
