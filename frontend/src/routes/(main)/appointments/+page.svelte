<script lang="ts">
    import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from "$components/Table/Table.svelte";
	import ButtonCell from "$components/Table/ButtonCell.svelte";
	import { m } from "$lib/paraglide/messages";
	import type { Appointment, Paginated } from "$types/api";
	import type { PageData } from './$types';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { fetchFreeAppointments, fetchNonFreeAppointments } from '$lib/services/appointments';
	import { page } from '$app/stores';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';

    let { data }: { data: PageData } = $props();

    const pastAppointments: Paginated<Appointment> | null = data.pastAppointments ?? null;
    let freeAppointments: Paginated<Appointment> | null = $state(data.freeAppointments ?? null);
    const freeAppointmentsLink: string | null = data.freeAppointmentsLink || null;
    
    const futureAppointments: Paginated<Appointment> = data.futureAppointments ?? {
        results: [],
        _links: {}
    };

    const tableColumns: Column<Appointment>[] = [
        {
            id: 'weekday',
            label: m['appointments.label.doctor'](),
            render: (appointment: Appointment) => {
                return appointment.doctorData ? `${appointment.doctorData.name}` : 'N/A';
            },
            class: 'font-medium'
        },
        {
            id: 'date',
            label: m['appointments.label.date'](),
            render: (appointment: Appointment) => {
                return new Date(appointment.date).toLocaleDateString(getLocale());
            },
            class: 'text-start'
        },
        {
            id: 'time-span',
            label: m['appointments.label.time'](),
            render: (appointment: Appointment) => appointment.startTime + ' - ' + appointment.endTime,
            class: 'text-start'
        },
        {
            id: 'address',
            label: m['appointments.label.address'](),
            render: (appointment: Appointment) => appointment.address,
            class: 'text-start'
        }
    ];

    const futureColumns: Column<Appointment>[] = [
        ...tableColumns,
        {
            id: 'action',
            label: m['appointments.label.action'](),
            render: (appointment: Appointment) => {
                return {
                    component: ButtonCell,
                    props: {
                        text: 'trash',
                        icon: true,
                        variant: 'destructive',
                        onclick: (e: MouseEvent) => {
                            e.stopPropagation();
                            e.preventDefault();
                            // Handle cancel logic
                            console.log('Cancel appointment: ', appointment.date);
                        }
                    }
                };
            },
            class: 'flex justify-center items-center text-center',
            columnClass: 'w-20'
        }
    ];

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

    const updateUrlDate = (date: Date) => {
        const dateStr = formatDateLocal(date);
        const newUrl = new URL($page.url);
        newUrl.searchParams.set('date', dateStr);
        goto(newUrl, { replaceState: true, noScroll: true, keepFocus: true });
    };

    const fetchAppointments = async (url: string, date?: Date | null, updateUrl = true) => {
        if (isFetching || !freeAppointmentsLink) return;
        
        isFetching = true;
        try {
            freeAppointments = await fetchFreeAppointments(url, date ? formatDateLocal(date) : undefined, fetch);
            const newDate = freeAppointments._pageInfo?.currentDate || date || new Date();
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

    const freeTableColumns: Column<Appointment>[] = [
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

    const parseDoctorSelf = (self: string) => {
		const apiIndex = self.indexOf('/api/');
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + 5) : self;
		return toRet;
	};

    onMount(() => {
        if (freeAppointments === null) {
            const newUrl = new URL($page.url);
            newUrl.searchParams.delete('date');
            goto(newUrl, { replaceState: true, noScroll: true, keepFocus: true });
        }
    });
</script>

<div class="flex gap-5">
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m["appointments.title.future"]()}:</p>
        <Table
            rows={futureAppointments}
            nextFetchFunction={(nextUrl: string) => fetchNonFreeAppointments(nextUrl, fetch, true)}
            columns={futureColumns}
            striped
            hover={freeAppointmentsLink !== null}
            onRowClick={(appointment) => {
                if (freeAppointmentsLink) {
                    return;
                }
                console.log('Clicked future appointment:', appointment);
            }}
            emptyMessage={m['appointments.empty.future']()}
            class="shadow-sm rounded-lg"
        />
    </div>
    
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        {#if pastAppointments}
            <p class="title text-primaryText">{m["appointments.title.past"]()}:</p>
            <Table
                rows={pastAppointments}
                nextFetchFunction={(nextUrl: string) => fetchNonFreeAppointments(nextUrl, fetch, true)}
                columns={tableColumns}
                striped
                hover
                onRowClick={(row) => goto(`${base}/${parseDoctorSelf((row as Appointment).doctorData!.links.self)}`)}
                emptyMessage={m['appointments.empty.past']()}
                class="shadow-sm rounded-lg"
            />
        {:else if freeAppointments && freeAppointmentsLink}
            <p class="title text-primaryText">{m["appointments.title.free"]()}:</p>
            <div class="flex flex-row justify-center items-center p-6 pt-0! gap-10">
                <Button
                    variant="secondary"
                    class="w-fit"
                    onclick={() => fetchAppointments(freeAppointments!._links.prev!)}
                    disabled={selectedDate <= new Date() || isFetching}
                >
                    {m['previous']()}
                </Button>
                <DatePicker
                    selectedDate={selectedDate}
                    onSelectDate={(date) => {
                        selectedDate = date ?? new Date();
                        fetchAppointments(freeAppointmentsLink, selectedDate);
                    }}
                    minDate={new Date()}
                    maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
                    class="w-fit"
                />
                <Button
                    variant="secondary"
                    class="w-fit"
                    onclick={() => fetchAppointments(freeAppointments!._links.next!)}
                    disabled={selectedDate >= new Date(new Date().setMonth(new Date().getMonth() + 3)) || isFetching}
                >
                    {m['next']()}
                </Button>
            </div>
            <Table
                columns={freeTableColumns}
                rows={freeAppointments.results}
                onRowClick={(appointment) => {
                    selectedAppointment = appointment;
                }}
                hover={true}
                striped={true}
                skeleton={false}
                emptyMessage={m['doctor.text.empty_schedule']()}
                class="shadow-sm rounded-lg"
            />
        {/if}
    </div>
</div>

<style>
    .title {
        font-size: 22px;
        font-weight: 700;
    }
</style>