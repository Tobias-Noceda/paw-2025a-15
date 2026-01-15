<script lang="ts">
    import { getLocale } from '$lib/paraglide/runtime';
	import Table, { type Column } from "$components/Table/Table.svelte";
	import ButtonCell from "$components/Table/ButtonCell.svelte";
	import { m } from "$lib/paraglide/messages";
	import type { Appointment } from "$types/api";
	import { Specialties } from "$types/enums/specialties";

    const appointments: Appointment[] = [];
    const futureAppointments: Appointment[] = [
        {
            weekday: 'Friday',
            address: '123 Main St',
            date: '2026-01-16',
            startTime: '10:00',
            endTime: '10:30',
            durationMinutes: 30,
            self: '/appointments/1',
            doctor: '/doctors/1',
            patient: '/patients/1',
            doctorData: {
                name: 'Dr. Smith',
                image: '/files/1',
                email: 'dr.smith@example.com',
                telephone: '555-1234',
                license: 'ABC123',
                specialty: Specialties.BARIATRIC_SURGERY,
                schedule: '/shifts?doctorId=1',
                todaysFreeAppointments: 'trash',
                insurances: 'trash',
                self: '/doctors/1'
            }
        }
    ];

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
            id: 'status',
            label: m['appointments.label.action'](),
            render: (appointment: Appointment) => {
                return {
                    component: ButtonCell,
                    props: {
                        text: 'trash',
                        icon: true,
                        variant: 'destructive',
                        onclick: () => {
                            // Handle cancel logic
                            console.log('Cancel appointment: ', appointment.date);
                        }
                    }
                };
            },
            class: 'flex justify-center items-center text-center'
        }
    ];
</script>

<div class="flex gap-5">
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m["appointments.title.future"]()}:</p>
        <Table
            rows={futureAppointments}
            columns={futureColumns}
            striped
            emptyMessage={m['appointments.empty.future']()}
        />
    </div>
    
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m["appointments.title.past"]()}:</p>
        <Table
            rows={appointments}
            columns={tableColumns}
            striped
            emptyMessage={m['appointments.empty.past']()}
        />
    </div>
</div>

<style>
    .title {
        font-size: 22px;
        font-weight: 700;
    }
</style>