<script lang="ts">
	import type { PageData } from './$types';
	import { m } from '$lib/paraglide/messages';
	import Button from '$components/Button/Button.svelte';
	import Select from '$components/Select/Select.svelte';
	import { StudyType } from '$types/enums/studyTypes';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Input from '$components/Input/Input.svelte';
	import type { Doctor } from '$types/api';
	import ToggleCell from '$components/Table/ToggleCell.svelte';
	import type { Column } from '$components/Table/Table.svelte';
	import Table from '$components/Table/Table.svelte';
	import { createStudy } from '$lib/services/studies';
	import Toast from '$components/Toast/Toast.svelte';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';

	let { data }: { data: PageData } = $props();

	let files: File[] = $state([]);

	const studyTypeOptions = Object.entries(StudyType).map(([key, value]) => {
        return {
		label: m[`studies.types.options.${key.replace(/\s/g, '_').toLowerCase()}`](),
		value: value
	}});
	let selectedStudyType: string = $state('OTHER');

	let fileDate = $state(new Date());

	let extraData: string = $state('');

	let selectedDoctorsSelfs: string[] = $state([]);

	let showErrorToast = $state(false);
	let showSuccessToast = $state(false);

	const parseStudyId = (url: string): string | null => {
		const match = url.match(/\/studies\/(\d+)/);
		return match ? match[1] : null;
	};

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
			render: (doctor: Doctor) => {
				return {
					component: ToggleCell,
					props: {
						selected: selectedDoctorsSelfs.includes(doctor.links.self),
						onclick: (selected: boolean) => {
							if (selected) {
								selectedDoctorsSelfs = [...selectedDoctorsSelfs, doctor.links.self];
							} else {
								selectedDoctorsSelfs = selectedDoctorsSelfs.filter(
									(self) => self !== doctor.links.self
								);
							}
						},
						class: 'px-2 py-1 text-xs! font-semibold'
					}
				};
			},
			class: 'flex justify-center items-center text-center',
			columnClass: 'w-20'
		}
	];

	const handleSelectAll = () => {
		if (data.doctors) {
			selectedDoctorsSelfs = data.doctors.map((doctor: Doctor) => doctor.links.self);
		}
	};

	const handleDeselectAll = () => {
		selectedDoctorsSelfs = [];
	};

	const handleSubmit = async () => {
		if (files.length === 0) {
			return;
		}

		const location = await createStudy(data.patient.links.studies.resolved!, {
			comment: extraData,
			studyDate: fileDate,
			type: selectedStudyType as StudyType,
			authDoctors: selectedDoctorsSelfs,
			files
		});

		if (!location) {
			showErrorToast = true;
			return;
		}
		showSuccessToast = true;

		setTimeout(() => {
			goto(`${base}/study-info/${parseStudyId(location)}-${data.patientId}`);
		}, 1500);
	};
</script>

<div class="flex justify-center">
	<div
		class="page-container-division bg-transparent! h-fit max-h-full! flex flex-col w-160 gap-2.5 select-text"
	>
		<div class="page-card flex flex-col gap-5 w-full h-fit max-h-full! overflow-y-auto">
			<h1 class="w-full text-center text-[24px] font-bold mb-2.5">
				{m['new_file.title']({ patientName: data.patient.name })}
			</h1>
			<div class="flex flex-col gap-3">
				<p class="text-line text-primaryText font-bold select-none">
					{m['new_file.labels.files']()}:
				</p>
				<div class="flex gap-5 items-center">
					<Button
						variant="primary"
						class="min-w-fit!"
						onclick={() => {
							const input = document.createElement('input');
							input.type = 'file';
							input.accept = '.jpeg,.png,.pdf';
							input.multiple = true;
							input.onchange = (event) => {
								const target = event.target as HTMLInputElement;
								if (target.files) {
									files = Array.from(target.files);
								}
							};
							input.click();
						}}
					>
						{m['new_file.buttons.files']()}
					</Button>
					{#if files.length === 0}
						<p>{m['new_file.no_files']()}</p>
					{:else}
						<ul class="list-disc list-inside text-ellipsis overflow-hidden">
							{#each files.slice(0, 2) as file}
								<li class="truncate">{file.name}</li>
							{/each}
							{#if files.length > 2}
								<p class="truncate text-gray-400">
									{m['new_file.more_files']({ count: files.length - 2 })}
								</p>
							{/if}
						</ul>
					{/if}
				</div>
				<Select
					options={studyTypeOptions}
					label={m['new_file.labels.type']() + ':'}
					value={selectedStudyType}
					onchange={(value: string) => {
						selectedStudyType = value;
					}}
					class="w-full"
				/>
				<DatePicker
					label={m['new_file.labels.date']() + ':'}
					selectedDate={fileDate}
					onSelectDate={(date: Date | null) => {
						if (date) {
							fileDate = date;
						} else {
							fileDate = new Date();
						}
					}}
					maxDate={new Date()}
					class="w-full"
				/>
				<Input
					label={m['new_file.labels.data']() + ':'}
					placeholder={m['new_file.data_placeholder']()}
					bind:value={extraData}
					class="w-full max-h-22!"
					multiline
					required
				/>
				{#if data.doctors && data.doctors.length > 0}
					<div class="flex flex-col gap-1">
						<p class="font-medium text-sm">
							{m['new_file.labels.auth_doctors']()}:
						</p>
						<div class="flex flex-row h-fit w-full pt-1 pb-4 gap-2">
							{#if selectedDoctorsSelfs.length < data.doctors.length}
								<Button variant="primary" onclick={handleSelectAll}>
									{m['new_file.buttons.select_all']()}
								</Button>
							{/if}
							{#if selectedDoctorsSelfs.length > 0}
								<Button variant="gray" onclick={handleDeselectAll}>
									{m['new_file.buttons.deselect_all']()}
								</Button>
							{/if}
						</div>
						<Table columns={doctorColumns} rows={data.doctors} striped bordered hover />
					</div>
				{/if}
				<Button
					variant="primary"
					class="w-full mt-5"
					onclick={handleSubmit}
					disabled={files.length === 0 || extraData.trim() === ''}
				>
					{m['new_file.buttons.submit']()}
				</Button>
			</div>
		</div>
	</div>

	<Toast
		variant="success"
		title={m['new_file.messages.upload_success_title']()}
		description={m['new_file.messages.upload_success_message']({ patientName: data.patient.name })}
		show={showSuccessToast}
	/>

	<Toast
		variant="destructive"
		title={m['new_file.messages.upload_error_title']()}
		description={m['new_file.messages.upload_error_message']()}
		show={showErrorToast}
	/>
</div>

<style>
	.page-container-division {
		display: flex;
		top: 20px;
		color: var(--primaryText, #000);
		height: calc(100vh - 110px);
	}

	.page-card {
		display: flex;
		padding: 20px;
		background-color: var(--bgColor, #fff);
		border-radius: 10px;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
		color: var(--primaryText, #000);
	}
</style>
