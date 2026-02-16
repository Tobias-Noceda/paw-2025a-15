<script lang="ts">
    import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from "$components/Table/Table.svelte";
	import ButtonCell from "$components/Table/ButtonCell.svelte";
	import { m } from "$lib/paraglide/messages";
	import type { Appointment, Paginated } from "$types/api";
	import type { PageData } from './$types';
	import { onMount } from 'svelte';
	import { goto, pushState } from '$app/navigation';
	import { base } from '$app/paths';
	import Button from '$components/Button/Button.svelte';
	import { cancelAppointment, fetchFreeAppointments, fetchNonFreeAppointments, parseDateInLocalTimezone, takeAppointment } from '$lib/services/appointments';
	import { page } from '$app/stores';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Toast from '$components/Toast/Toast.svelte';
	import PopUp from '$components/PopUp/PopUp.svelte';

    let { data }: { data: PageData } = $props();

    const pastAppointments: Paginated<Appointment> | null = $state(data.pastAppointments ?? null);
    
    let freeAppointments: Paginated<Appointment> | null = $state(data.freeAppointments ?? null);
    const freeAppointmentsLink: string | null = $state(data.freeAppointmentsLink || null);
    
    let showSuccessToast = $state(false);
    let showErrorToast = $state(false);

    let canceledAppointment: Appointment | null = $state(null);
    let canceledAppointmentId: number | null = $state(null);
    let cancelReason: string | null = $state(null);

    let selectedAppointment: Appointment | null = $state(null);

    let futureAppointments: Paginated<Appointment> = $state(data.futureAppointments ?? {
        results: [],
        _links: {}
    });

    const tableColumns: Column<Appointment>[] = [
        {
            id: 'weekday',
            label: m['appointments.label.doctor'](),
            render: (appointment: Appointment) => {
                return appointment.doctor ? `${appointment.doctor.name}` : 'N/A';
            },
            class: 'font-medium'
        },
        {
            id: 'date',
            label: m['appointments.label.date'](),
            render: (appointment: Appointment) => {
                return parseDateInLocalTimezone(appointment.date).toLocaleDateString(getLocale(), {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
            },
            class: 'text-start'
        },
        {
            id: 'time-span',
            label: m['appointments.label.time'](),
            render: (appointment: Appointment) => (appointment.startTime ?? '') + ' - ' + (appointment.endTime ?? ''),
            class: 'text-start'
        },
        {
            id: 'address',
            label: m['appointments.label.address'](),
            render: (appointment: Appointment) => appointment.address ?? '',
            class: 'text-start'
        }
    ];

    const futureColumns: Column<Appointment>[] = [
        ...tableColumns,
        {
            id: 'action',
            label: m['appointments.label.action'](),
            render: (appointment: Appointment, index: number) => {
                return {
                    component: ButtonCell,
                    props: {
                        text: 'trash',
                        icon: true,
                        variant: 'destructive',
                        onclick: (e: MouseEvent, ) => {
                            e.stopPropagation();
                            e.preventDefault();
                            // Handle cancel logic
                            
                            canceledAppointment = appointment;
                            canceledAppointmentId = index;
                            cancelReason = null;
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

    const updateUrlDate = (date: Date) => {
        const dateStr = formatDateLocal(date);
        const newUrl = new URL($page.url);
        newUrl.searchParams.set('date', dateStr);
        pushState(newUrl.toString(), {});
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
                return appointment.weekday ? m[`filters.weekdays.${appointment.weekday.toLowerCase()}`]() : '';
            },
            class: 'font-medium'
        },
        {
            id: 'time-span',
            label: m['doctor.table.time'](),
            render: (appointment: Appointment) => (appointment.startTime ?? '') + ' - ' + (appointment.endTime ?? ''),
            class: 'text-start'
        },
        {
            id: 'free-action',
            label: m['appointments.label.action'](),
            render: (appointment: Appointment, index: number) => {
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
                            canceledAppointment = appointment;
                            canceledAppointmentId = index;
                            cancelReason = 'Canceled by doctor';
                        }
                    }
                };
            },
            class: 'flex justify-center items-center text-center',
            columnClass: 'w-20'
        }
    ];

    const parseSelf = (self: string) => {
		const apiIndex = self.indexOf('/api/');
		const toRet = apiIndex !== -1 ? self.substring(apiIndex + 5) : self;
		return toRet;
	};

    const handleCancelAppointment = (appointment: Appointment, index: number, reason: string | null) => {
        if (!appointment) return;

        if (reason) {
            takeAppointment(appointment.links.self.resolved!, reason)
                .then(() => {
                    showSuccessToast = true;

                    // Remove the appointment from futureAppointments
                    freeAppointments?.results.splice(index, 1);
                })
                .catch((error) => {
                    showErrorToast = true;
                    console.error('Failed to take appointment:', error);
                });
        } else {
            cancelAppointment(appointment.links.self.resolved!)
                .then(() => {
                    showSuccessToast = true;

                    futureAppointments.results.splice(index, 1)
                })
                .catch((error) => {
                    showErrorToast = true;
                    console.error('Failed to cancel appointment:', error);
                });
        }

        canceledAppointment = null;
        canceledAppointmentId = null;
        cancelReason = null;
    };

    onMount(() => {
        if (freeAppointments === null) {
            const newUrl = new URL($page.url);
            newUrl.searchParams.delete('date');
            pushState(newUrl.toString(), {});
        }
    });
</script>

<div class="flex gap-5">
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m["appointments.title.future"]()}:</p>
        <Table
            rows={futureAppointments}
            nextFetchFunction={(nextUrl: string) => fetchNonFreeAppointments(nextUrl, freeAppointmentsLink !== undefined, fetch, true)}
            columns={futureColumns}
            striped
            hover
            onRowClick={(appointment) => {
                if (freeAppointmentsLink) {
                    selectedAppointment = appointment;
                    return;
                }
                goto(`${base}/${parseSelf((appointment as Appointment).doctor!.links.self.resolved!)}`);
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
                nextFetchFunction={(nextUrl: string) => fetchNonFreeAppointments(nextUrl, false, fetch, true)}
                columns={tableColumns}
                striped
                hover
                onRowClick={(row) => goto(`${base}/${parseSelf((row as Appointment).doctor!.links.self.resolved!)}`)}
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
                    disabled={!freeAppointments!._links.prev || isFetching}
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
                    maxDate={(freeAppointments._pageInfo?.maxDate ?? new Date(new Date().setMonth(new Date().getMonth() + 3)))}
                    class="w-fit"
                />
                <Button
                    variant="secondary"
                    class="w-fit"
                    onclick={() => fetchAppointments(freeAppointments!._links.next!)}
                    disabled={!freeAppointments!._links.next || isFetching}
                >
                    {m['next']()}
                </Button>
            </div>
            <Table
                columns={freeTableColumns}
                rows={freeAppointments.results}
                hover
                striped={true}
                skeleton={false}
                emptyMessage={m['doctor.text.empty_schedule']()}
                class="shadow-sm rounded-lg"
            />
        {/if}
    </div>

    <Toast
        show={showSuccessToast}
        variant="success"
        title={m['appointments.canceled.success_title']()}
        description={m['appointments.canceled.success_message']({
            day: canceledAppointment?.date ? new Date(canceledAppointment.date).getDate() : '',
            month: canceledAppointment?.date ? new Date(canceledAppointment.date).toLocaleString(getLocale(), { month: 'long' }) : '',
            startTime: canceledAppointment?.startTime ?? ''}
        )}
    />
    <Toast
        show={showErrorToast}
        variant="destructive"
        title={m['appointments.canceled.error_title']()}
        description={m['appointments.canceled.error_message']()}
    />

    {#if canceledAppointment}
        <PopUp onClose={() => {
            canceledAppointment = null;
            canceledAppointmentId = null;
            cancelReason = null;
        }}>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">
                    {cancelReason ? m['appointments.pop_up.delete.title']() : m['appointments.pop_up.cancel.title']()}
                </h1>
                {#if cancelReason}
                    <p class="text-primaryText">{m['appointments.pop_up.delete.subtitle']()}</p>
                {/if}

                <div class="flex justify-end gap-4 mt-2">
                    <Button
                        variant="primary"
                        onclick={() => handleCancelAppointment(canceledAppointment!, canceledAppointmentId!, cancelReason)}
                    >
                        {cancelReason ? m['appointments.pop_up.delete.confirm']() : m['appointments.pop_up.cancel.confirm']()}
                    </Button>
                    <Button
                        variant="destructive"
                        onclick={() => {
                            canceledAppointment = null;
                            canceledAppointmentId = null;
                            cancelReason = null;
                        }}
                    >
                        {m['appointments.pop_up.back']()}
                    </Button>
                </div>
            </div>
        </PopUp>
    {/if}

    {#if selectedAppointment}
        <PopUp onClose={() => selectedAppointment = null}>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">
                    {selectedAppointment.patientName ?? m['appointments.pop_up.unknown_patient']()}
                    {#if selectedAppointment.patientEmail}
                        {`(${selectedAppointment.patientEmail})`}
                    {/if}
                </h1>
                <p class="text-primaryText">{selectedAppointment.detail}</p>

                <div class="flex justify-end gap-4 mt-2">
                    {#if selectedAppointment.patient}
                        <Button
                            variant="primary"
                            onclick={() => {
                                if (selectedAppointment?.patient) {
                                    goto(`${base}/${parseSelf(selectedAppointment.patient.links.self.resolved!)}`);
                                }
                            }}
                        >
                            {m['appointments.pop_up.see_patient']()}
                        </Button>
                    {/if}
                    <Button
                        variant="destructive"
                        onclick={() => {
                            selectedAppointment = null;
                        }}
                    >
                        {m['appointments.pop_up.back']()}
                    </Button>
                </div>
            </div>
        </PopUp>
    {/if}
</div>

<style>
    .title {
        font-size: 22px;
        font-weight: 700;
    }
</style>