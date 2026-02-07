<script lang="ts">
	import { page } from '$app/stores';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import type { Appointment, Doctor, Paginated, Patient } from '$types/api';
	import type { PageData } from './$types';

	import { m } from '$lib/paraglide/messages';
	import Divider from '$components/Divider/Divider.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import { fetchFreeAppointments, takeAppointment } from '$lib/services/appointments';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Input from '$components/Input/Input.svelte';
	import { goto } from '$app/navigation';
	import Toast from '$components/Toast/Toast.svelte';
	import { base } from '$app/paths';
	import { getLocale } from '$lib/paraglide/runtime';
	import { StudyType } from '$types/enums/studyTypes';
	import Select from '$components/Select/Select.svelte';
	import { StudyOrders } from '$types/enums/studyOrders';

	let { data }: { data: PageData } = $props();

	let patient: Patient = $state(data.patient);
	let isFetching = $state(false);

	let showSuccessToast = $state(false);
	let showErrorToast = $state(false);

	// Format date in local timezone (avoid UTC conversion)
	const formatDateLocal = (date: Date): string => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		return `${year}-${month}-${day}`;
	};

	let selectedStudyType: string = $state('all');
    let selectedStudyOrder: string = $state('none');

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

	const studyTypeOptions = Object.entries(StudyType)
		.sort()
		.map(([key, value]) => ({
			label: m[`studies.types.options.${key.toLowerCase()}`](),
			value: value
		}));

    const studyOrderOptions = [
        { label: "-", value: 'none' },
        ...Object.entries(StudyOrders)
            .map(([key, value]) => ({
                label: m[`studies.orders.options.${key.toLowerCase()}`](),
                value: value
            }))
    ];

	// const tableColumns: Column<Appointment>[] = [
	//     {
	//         id: 'weekday',
	//         label: m['doctor.table.day'](),
	//         render: (appointment: Appointment) => {
	//             return m[`filters.weekdays.${appointment.weekday.toLowerCase()}`]();
	//         },
	//         class: 'font-medium'
	//     },
	//     {
	//         id: 'time-span',
	//         label: m['doctor.table.time'](),
	//         render: (appointment: Appointment) => appointment.startTime + ' - ' + appointment.endTime,
	//         class: 'text-start'
	//     }
	// ];
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
					onclick={() => window.history.back()}
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

			{#if patient.gaveHabits}
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

			{#if patient.gaveMedical}
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

			{#if patient.gaveSocial}
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
                    onclick={() => console.log('Filtering studies with', selectedStudyType, selectedStudyOrder)}
                >
                    {m['patient.actions.apply']()}
                </Button>
            </div>
		</div>
	</div>
	<!-- {#if selectedAppointment !== null}
        <PopUp>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">
                    {m['doctor.pop_up.title']({
                        month: new Date(selectedAppointment.date).toLocaleString(getLocale(), { month: 'long' }),
                        day: new Date(selectedAppointment.date).getDate(),
                        startTime: selectedAppointment.startTime,
                        doctorName: doctor?.name ?? ''
                    })}
                </h1>
                <p class="text-primaryText">{m['doctor.pop_up.subtitle']()}</p>
                <div>
                    <p class="text-primaryText">{m['doctor.pop_up.reason']()}</p>
                    <Input
                        multiline
                        placeholder={m['doctor.pop_up.reason_placeholder']()}
                        bind:value={reason}
                    />
                </div>
                <div class="flex justify-end gap-4 mt-2">
                    <Button
                        variant="destructive"
                        onclick={() => {
                            selectedAppointment = null;
                            reason = '';
                        }}
                    >
                        {m['doctor.pop_up.cancel']()}
                    </Button>
                    <Button
                        variant="primary"
                        disabled={!reason || reason.trim() === ''}
                        onclick={handleTakeAppointment}
                    >
                        {m['doctor.pop_up.confirm']()}
                    </Button>
                </div>
            </div>
        </PopUp>
    {/if} -->

	<Toast
		show={showSuccessToast}
		variant="success"
		title={m['appointments.taken.success_title']()}
		description={'Hola'}
	/>
	<Toast
		show={showErrorToast}
		variant="destructive"
		title={m['appointments.taken.error_title']()}
		description={m['appointments.taken.error_message']()}
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
