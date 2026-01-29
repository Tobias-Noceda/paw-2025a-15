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
	import { fetchFreeAppointments } from '$lib/services/appointments';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import Input from '$components/Input/Input.svelte';
	import { goto } from '$app/navigation';

    let { data }: { data: PageData } = $props();

	let doctor: Doctor = $state(data.doctor);
    let appointments: Paginated<Appointment> = $state(data.appointments);
    let selectedDate: Date = $state(data.selectedDate);
    let isFetching = $state(false);

    // Format date in local timezone (avoid UTC conversion)
    const formatDateLocal = (date: Date): string => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    let selectedAppointment: Appointment | null = $state(null);
    let reason = $state('');

    const updateUrlDate = (date: Date) => {
        const dateStr = formatDateLocal(date);
        const newUrl = new URL($page.url);
        newUrl.searchParams.set('date', dateStr);
        goto(newUrl, { replaceState: true, noScroll: true, keepFocus: true });
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
        <div class="doctor-card-inner">
            <h1 class="text-[24px] font-bold mb-2.5">{doctor ? doctor.name : 'Loading...'}</h1>
            <div class="flex w-full h-60 justify-center items-center">
                <div class="flex w-[200px] h-[200px]">
                    <Avatar
                        size="auto"
                        src={doctor ? doctor.links.image : ''}
                        class="bg-primary"
                    />
                </div>
            </div>
            <p class="text-line font-bold">{doctor ? doctor.email : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.telephone']()}:</span> {doctor ? doctor.telephone : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.insurances']()}:</span> {doctor ? doctor.insurances?.join(', ') : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.specialty']()}:</span> {doctor ? m[`specialties.${doctor.specialty}`]() : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.address']()}:</span> {doctor ? doctor.direction : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">{m['doctor.labels.license']()}:</span> {doctor ? doctor.license : 'Loading...'}</p>

            <div class="flex flex-1 flex-col">
                <p class="section-title">{m['doctor.labels.schedule']()}:</p>

                {#if doctor && doctor.schedule && doctor.schedule.size > 0}
                    <ul class="mt-2.5 pb-5">
                        {#each Array.from(doctor.schedule.entries()) as [day, [start, end]]}
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
                onclick={() => fetchAppointments(appointments._links.prev!)}
                disabled={selectedDate <= new Date() || isFetching}
            >
                {m['previous']()}
            </Button>
            <DatePicker
                selectedDate={selectedDate}
                onSelectDate={(date) => {
                    selectedDate = date ?? new Date();
                    fetchAppointments(doctor!.links.freeAppointments, selectedDate);
                }}
                minDate={new Date()}
                maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
                class="w-fit"
            />
            <Button
                variant="secondary"
                class="w-fit"
                onclick={() => fetchAppointments(appointments._links.next!)}
                disabled={selectedDate >= new Date(new Date().setMonth(new Date().getMonth() + 3)) || isFetching}
            >
                {m['next']()}
            </Button>
        </div>
        <Table
            columns={tableColumns}
            rows={appointments.results}
            onRowClick={(appointment) => {
                selectedAppointment = appointment;
            }}
            hover={true}
            striped={true}
            skeleton={false}
            emptyMessage={m['doctor.text.empty_schedule']()}
        />
    </div>
    {#if selectedAppointment !== null}
        <PopUp>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">{m['doctor.pop_up.title']({month: selectedAppointment.startTime.toString(), day: selectedDate.getDate(), startTime: selectedAppointment.startTime, doctorName: doctor?.name ?? ''})}</h1>
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
