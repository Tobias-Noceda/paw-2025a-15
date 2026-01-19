<script lang="ts">
    import { page } from '$app/stores';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import { fetchDoctorById } from '$lib/services/doctors';
	import type { Appointment, Doctor } from '$types/api';
	import { onMount } from 'svelte';

    import { m } from '$lib/paraglide/messages';
	import Divider from '$components/Divider/Divider.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Table, { type Column } from '$components/Table/Table.svelte';
	import { fetchFreeAppointments } from '$lib/services/appointments';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Input from '$components/Input/Input.svelte';
	import { goto } from '$app/navigation';

    const doctorId = $derived(Number.parseInt($page.params.id!));

	let doctor: Doctor | null = $state(null);
    let appointments: Appointment[] = $state([]);

    let appointmentsDate: Date | null = $state(null);

    // Parse date in local timezone to avoid UTC conversion issues
    const parseDateInLocalTimezone = (dateStr: string): Date => {
        const [year, month, day] = dateStr.split('-').map(Number);
        return new Date(year, month - 1, day);
    };

    let selectedDate: Date = $state($page.url.searchParams.get('date') ? parseDateInLocalTimezone($page.url.searchParams.get('date')!) : new Date());

    let selectedAppointment: Appointment | null = $state(null);
    let reason = $state('');

    const fetchAppointments = async (doctorId: number, date: Date) => {
        if (doctor && selectedDate) {
            appointments = await fetchFreeAppointments(doctorId, date.toISOString().split('T')[0]);
            return;
        }
    };

    onMount(async () => {
        if (!doctor) {
            doctor = await fetchDoctorById($page.params.id!)
                .then((data) => {
                    return data;
                })
                .catch((error) => {
                    console.error('Failed to fetch doctor:', error);
                    return null;
                });

            if (doctor) {
                // Initialize appointmentsDate from doctor.todaysFreeAppointments URI query param
                appointmentsDate = new Date();
            }

            // Fetch appointments for today
            if (doctor && appointmentsDate) {
                await fetchAppointments(doctorId, appointmentsDate);
            }
        }
    });

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

    $effect(() => {
        if (doctor && selectedDate) {
            fetchAppointments(doctorId, selectedDate);
            return;
        }
    });
</script>

<div class="flex gap-5">
    <div class="page-division flex-col min-w-[350px]! w-[350px]! sticky">
        <div class="doctor-card-inner">
            <h1 class="text-[24px] font-bold mb-2.5">{doctor ? doctor.name : 'Loading...'}</h1>
            <div class="flex w-full h-60 justify-center items-center">
                <div class="flex w-[200px] h-[200px]">
                    <Avatar
                        size="auto"
                        src={doctor ? doctor.image : ''}
                        class="bg-primary"
                    />
                </div>
            </div>
            <p class="text-line font-bold">{doctor ? doctor.email : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.telephone']()}:</span> {doctor ? doctor.telephone : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.insurances']()}:</span> {doctor ? doctor.insuranceNames?.join(', ') : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.specialty']()}:</span> {doctor ? m[`specialties.${doctor.specialty}`]() : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.address']()}:</span> {doctor ? doctor.direction : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.license']()}:</span> {doctor ? doctor.license : 'Loading...'}</p>

            <div class="flex flex-1 flex-col">
                <p class="section-title">{m['doctor.labels.schedule']()}:</p>

                {#if doctor && doctor.scheduleDays && doctor.scheduleDays.size > 0}
                    <ul class="mt-2.5 pb-5">
                        {#each Array.from(doctor.scheduleDays.entries()) as [day, [start, end]]}
                            <li class="text-secondaryText">
                                {m['doctor.text.schedule']({ day: m[`filters.weekdays.${day.toLowerCase()}`](), startTime: start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }), endTime: end.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }) })}
                            </li>
                        {/each}
                    </ul>
                {:else}
                    <p class="text-line text-secondaryText">{m['doctor.text.no_schedule']()}</p>
                {/if}
            </div>
            
            <Divider class="my-5 bg-skeleton" size="auto" />

            <Button variant="success" class="w-fit" onclick={() => window.history.back()}>
                {m['doctor.actions.authorize']()}
            </Button>
        </div>
    </div>

    <div class="page-division flex flex-col w-full">
        <div class="flex flex-row justify-center items-center p-6 gap-10">
            <Button
                variant="secondary"
                class="w-fit"
                onclick={() => {
                    selectedDate = new Date(selectedDate.valueOf() - 24 * 60 * 60 * 1000);
                    goto($page.url.pathname + `?date=` + selectedDate.toISOString().split('T')[0]);
                }}
                disabled={selectedDate <= new Date()}
            >
                {m['previous']()}
            </Button>
            <DatePicker
                bind:selectedDate
                onSelectDate={(date) => {
                    selectedDate = date!;
                    goto($page.url.pathname + `?date=` + selectedDate.toISOString().split('T')[0]);
                }}
                minDate={new Date()}
                maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
                class="w-fit"
            />
            <Button
                variant="secondary"
                class="w-fit"
                onclick={() => {
                    selectedDate = new Date(selectedDate.valueOf() + 24 * 60 * 60 * 1000);
                    goto($page.url.pathname + `?date=` + selectedDate.toISOString().split('T')[0]);
                }}
                disabled={selectedDate >= new Date(new Date().setMonth(new Date().getMonth() + 3))}
            >
                {m['next']()}
            </Button>
        </div>
        <Table
            columns={tableColumns}
            rows={appointments}
            onRowClick={(appointment) => {
                selectedAppointment = appointment;
            }}
            hover={true}
            striped={true}
            skeleton={doctor === null}
            emptyMessage={m['doctor.text.empty_schedule']()}
        />
    </div>
    {#if selectedAppointment !== null}
        <PopUp>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">{m['doctor.pop_up.title']({month: selectedAppointment.startTime.toLocaleString('default', { month: 'long' }), day: selectedDate.getDate(), startTime: selectedAppointment.startTime, doctorName: doctor?.name})}</h1>
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
                        onclick={() => {
                            // Here you would normally call an API to book the appointment
                            alert(m['doctor.pop_up.confirm']({date: selectedDate.toDateString(), time: selectedAppointment?.startTime}));
                            selectedAppointment = null;
                            reason = '';
                        }}
                    >
                        <!-- disabled={reason.trim() === ''} -->
                        {m['doctor.pop_up.confirm']()}
                    </Button>
                </div>
            </div>
        </PopUp>
    {/if}
</div>

<style>
    .doctor-card-inner {
        display: flex;
        flex-direction: column;
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
        background-color: #2E4A7D; /* mismo azul de tu topbar */
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
