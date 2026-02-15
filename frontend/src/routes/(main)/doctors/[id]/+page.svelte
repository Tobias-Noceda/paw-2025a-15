<script lang="ts">
	import { page } from '$app/stores';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import type { Appointment, Doctor, Paginated } from '$types/api';
	import type { PageData } from './$types';

	import { m } from '$lib/paraglide/messages';
	import Divider from '$components/Divider/Divider.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import { fetchFreeAppointments, takeAppointment } from '$lib/services/appointments';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Input from '$components/Input/Input.svelte';
	import { goto, pushState } from '$app/navigation';
	import Toast from '$components/Toast/Toast.svelte';
	import { base } from '$app/paths';
	import { getLocale } from '$lib/paraglide/runtime';
	import { AccessLevels } from '$types/enums/accessLevels';
	import { putAuthorizations } from '$lib/services/doctors';
	import RadioCheck from '$components/RadioCheck/RadioCheck.svelte';

	let { data }: { data: PageData } = $props();

	let doctor: Doctor = $state(data.doctor!);
	let appointments: Paginated<Appointment> = $state(data.appointments!);
	let selectedDate: Date = $state(data.selectedDate!);
	let isFetching = $state(false);

	let isAuthorized = $state(data.doctorAuthorizations?.authorized ?? false);
	let authorizationLevels: AccessLevels[] = $state(data.doctorAuthorizations?.accessLevels ?? []);

	let showSuccessToast = $state(false);
	let showErrorToast = $state(false);

	const handleAuthorize = () => {
		if (!isAuthorized) {
			putAuthorizations(doctor.links.authorization.resolved!, true, [AccessLevels.VIEW_BASIC])
				.then(() => {
					isAuthorized = true;
					authorizationLevels = [AccessLevels.VIEW_BASIC];
				})
				.catch((error) => {
					console.error('Failed to authorize doctor:', error);
				});
		} else {
			putAuthorizations(
				doctor.links.authorization.resolved!,
				isAuthorized,
				authorizationLevels
			).catch((error) => {
				console.error('Failed to update doctor authorizations:', error);
			});
		}
	};

    const handleDeauthorize = () => {
        putAuthorizations(doctor.links.authorization.resolved!, false, [])
            .then(() => {
                isAuthorized = false;
                authorizationLevels = [];
            })
            .catch((error) => {
                console.error('Failed to deauthorize doctor:', error);
            });
	};

	// Format date in local timezone (avoid UTC conversion)
	const formatDateLocal = (date: Date): string => {
		const year = date.getFullYear();
		const month = String(date.getMonth() + 1).padStart(2, '0');
		const day = String(date.getDate()).padStart(2, '0');
		return `${year}-${month}-${day}`;
	};

	let selectedAppointment: Appointment | null = $state(null);
	let selectedAppointmentId: number | null = $state(null);
	let reason = $state('');

	const updateUrlDate = (date: Date) => {
		const dateStr = formatDateLocal(date);
		const newUrl = new URL($page.url);
		newUrl.searchParams.set('date', dateStr);
		pushState(newUrl.toString(), {});
	};

	const fetchAppointments = async (url: string, date?: Date | null, updateUrl = true) => {
		if (isFetching) return;

		isFetching = true;
		try {
			appointments = await fetchFreeAppointments(url, date ? formatDateLocal(date) : undefined);
			const newDate = appointments._pageInfo?.currentDate || date || new Date();
			selectedDate = newDate;
			if (updateUrl) {
				updateUrlDate(newDate);
			}
		} catch (error) {
			console.error('Failed to fetch appointments:', error);
		} finally {
			isFetching = false;
		}
	};

	const handleTakeAppointment = async () => {
		if (!selectedAppointment || !reason || reason.trim() === '') return;

		takeAppointment(selectedAppointment.links.self, reason)
			.then(() => {
				showSuccessToast = true;
				appointments.results.splice(selectedAppointmentId!, 1);
				selectedAppointment = null;
				reason = '';

				setTimeout(() => {
					goto(`${base}/appointments`);
				}, 3000);
			})
			.catch((error) => {
				showErrorToast = true;
				console.error('Failed to take appointment:', error);
			});
	};

	const tableColumns: Column<Appointment>[] = [
		{
			id: 'weekday',
			label: m['doctor.table.day'](),
			render: (appointment: Appointment) => {
				return m[`filters.weekdays.${appointment.weekday.toLowerCase()}`]();
			},
			class: 'font-medium'
		},
		{
			id: 'time-span',
			label: m['doctor.table.time'](),
			render: (appointment: Appointment) => appointment.startTime + ' - ' + appointment.endTime,
			class: 'text-start'
		}
	];
</script>

<div class="flex gap-5">
	<div class="page-division flex-col min-w-[350px]! w-[350px]! sticky">
		<div class="doctor-card-inner inner-flex flex-col select-text">
			<h1 class="text-[24px] font-bold mb-2.5">{doctor.name}</h1>
			<div class="flex w-full h-60 justify-center items-center">
				<div class="flex w-[200px] h-[200px]">
					<Avatar size="auto" src={doctor.links.image} class="bg-primary" />
				</div>
			</div>
			<p class="text-line font-bold">{doctor.email}</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText">{m['doctor.labels.telephone']()}:</span>
				{doctor.telephone}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText">{m['doctor.labels.insurances']()}:</span>
				{doctor.insurances?.join(', ')}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText">{m['doctor.labels.specialty']()}:</span>
				{m[`specialties.${doctor.specialty}`]()}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText">{m['doctor.labels.address']()}:</span>
				{doctor.direction}
			</p>
			<p class="text-line text-secondaryText">
				<span class="font-bold text-primaryText">{m['doctor.labels.license']()}:</span>
				{doctor.license}
			</p>

			<div class="flex flex-1 flex-col">
				<p class="section-title">{m['doctor.labels.schedule']()}:</p>

				{#if doctor.schedule && doctor.schedule.size > 0}
					<ul class="mt-2.5 pb-5">
						{#each Array.from(doctor.schedule.entries()) as [day, [start, end]]}
							<li class="text-secondaryText">
								{m['doctor.text.schedule']({
									day: m[`filters.weekdays.${day.toLowerCase()}`](),
									startTime: start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
									endTime: end.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
								})}
							</li>
						{/each}
					</ul>
				{:else}
					<p class="text-line text-secondaryText">{m['doctor.text.no_schedule']()}</p>
				{/if}
			</div>

			<Divider class="my-5 bg-skeleton h-0.5" size="auto" />

            <div class="flex flex-col mt-3">
                {#if isAuthorized}
                    <p class="section-title">{m['doctor.labels.accesses']()}:</p>
                    <RadioCheck
                        optionsClass="w-full mb-5"
                        options={Object.entries(AccessLevels).splice(1, 3).map(([key, value]) => {
                            return {
                                id: key,
                                label: m[`doctor.authorizations.options.${value}`](),
                                checked: authorizationLevels.includes(key as AccessLevels)
                            };
                        })}
                        onchange={({ id, checked }) => {
                            if (checked) {
                                authorizationLevels = [...authorizationLevels, id as AccessLevels];
                            } else {
                                authorizationLevels = authorizationLevels.filter(level => level !== id);
                            }
                        }}
                    />
                {/if}

                <Button variant="success" class={isAuthorized ? 'w-full' : 'w-fit'} onclick={handleAuthorize}>
                    {isAuthorized ? m['doctor.actions.update']() : m['doctor.actions.authorize']()}
                </Button>

                {#if isAuthorized}
                    <Button variant="destructive" class="w-full mt-2" onclick={handleDeauthorize}>
                        {m['doctor.actions.de_authorize']()}
                    </Button>
                {/if}

            </div>

		</div>
	</div>

	<div class="page-division flex flex-col w-full">
		<div class="flex flex-row justify-center items-center p-6 gap-10">
			<Button
				variant="secondary"
				class="w-fit"
				onclick={() => fetchAppointments(appointments._links.prev!)}
				disabled={!appointments._links.prev || isFetching}
			>
				{m['previous']()}
			</Button>
			<DatePicker
				{selectedDate}
				onSelectDate={(date) => {
					selectedDate = date ?? new Date();
					fetchAppointments(doctor.links.freeAppointments, selectedDate);
				}}
				minDate={new Date()}
				maxDate={(appointments._pageInfo?.maxDate ?? new Date(new Date().setMonth(new Date().getMonth() + 3)))}
				class="w-fit"
			/>
			<Button
				variant="secondary"
				class="w-fit"
				onclick={() => fetchAppointments(appointments._links.next!)}
				disabled={!appointments._links.next || isFetching}
			>
				{m['next']()}
			</Button>
		</div>
		<Table
			columns={tableColumns}
			rows={appointments.results}
			onRowClick={(appointment, index) => {
				selectedAppointment = appointment;
				selectedAppointmentId = index;
			}}
			hover={true}
			striped={true}
			skeleton={false}
			emptyMessage={m['doctor.text.empty_schedule']()}
			class="shadow-sm rounded-lg"
		/>
	</div>
	{#if selectedAppointment !== null}
		<PopUp onClose={() => {
			selectedAppointment = null;
			reason = '';
		}}>
			<div class="flex flex-col gap-2">
				<h1 class="text-primaryText text-[1.17rem] font-bold">
					{m['doctor.pop_up.title']({
						month: new Date(selectedAppointment.date).toLocaleString(getLocale(), {
							month: 'long'
						}),
						day: new Date(selectedAppointment.date).getDate(),
						startTime: selectedAppointment.startTime,
						doctorName: doctor.name
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
	{/if}

	<Toast
		show={showSuccessToast}
		variant="success"
		title={m['appointments.taken.success_title']()}
		description={m['appointments.taken.success_message']({
			doctorName: doctor.name ?? '',
			day: selectedDate.getDate(),
			month: selectedDate.toLocaleString(getLocale(), { month: 'long' }),
			startTime: selectedAppointment?.startTime ?? ''
		})}
	/>
	<Toast
		show={showErrorToast}
		variant="destructive"
		title={m['appointments.taken.error_title']()}
		description={m['appointments.taken.error_message']()}
	/>
</div>

<style>
	.doctor-card-inner {
		height: 100%;
		overflow-y: auto;
		/* Hide scrollbar for all browsers */
		scrollbar-width: none; /* Firefox */
		-ms-overflow-style: none; /* IE and Edge */
	}

	.doctor-card-inner::-webkit-scrollbar {
		display: none; /* Chrome, Safari and Opera */
	}

	.text-line {
		line-height: 1.4;
		margin-bottom: 1rem;
	}

	.section-title {
		display: block;
		margin: 20px 0 8px;
		padding: 6px 12px;
		font-size: 1rem;
		font-weight: 600;
		color: #fff;
		background-color: #2e4a7d; /* mismo azul de tu topbar */
		border-radius: 4px;
		line-height: 1.3;
	}

	ul {
		padding: 0 0 0 20px;
		margin: 10px 0 0 0;
		list-style-type: disc;
	}

	li {
		font-size: 14px;
		color: #777;
		margin: 0 0 2px 0;
		display: list-item;
	}

	li:last-child {
		margin: 0;
	}
</style>
