<script lang="ts">
    import { page } from '$app/stores';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import { fetchDoctorById } from '$lib/services/doctors';
	import type { Doctor } from '$types/api';
	import { onMount } from 'svelte';

    import { m } from '$lib/paraglide/messages';
	import Divider from '$components/Divider/Divider.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Table from '$components/Table/Table.svelte';

	let doctor: Doctor | null = $state(null);

    let selectedDate: Date = $state(new Date());

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
                    />
                </div>
            </div>
            <p class="text-line font-bold">{doctor ? doctor.email : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">Telefono:</span> {doctor ? doctor.telephone : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">Obras sociales:</span> {doctor ? doctor.insuranceNames?.join(', ') : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">Especialidad:</span> {doctor ? m[`specialties.${doctor.specialty}`]() : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">Dirección:</span> {doctor ? doctor.direction : 'Loading...'}</p>
            <p class="text-line text-secondaryText"><span class="font-bold text-primaryText">Matrícula:</span> {doctor ? doctor.license : 'Loading...'}</p>

            <div class="flex flex-1 flex-col">
                <p class="section-title">Atiende:</p>

                {#if doctor && doctor.scheduleDays && doctor.scheduleDays.size > 0}
                    <ul class="mt-2.5 pb-5">
                        {#each Array.from(doctor.scheduleDays.entries()) as [day, [start, end]]}
                            <li class="text-secondaryText">
                                {m[`filters.weekdays.${day.toLowerCase()}`]()}
                                {' de '}
                                {start.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                                {' a '}
                                {end.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                            </li>
                        {/each}
                    </ul>
                {:else}
                    <p class="text-line text-secondaryText">No schedule available.</p>
                {/if}
            </div>
            
            <Divider class="my-5 bg-skeleton" size="auto" />

            <Button variant="success" class="w-fit" onclick={() => window.history.back()}>
                Autorizar
            </Button>
        </div>
    </div>

    <div class="page-division flex flex-col w-full">
        <div class="flex flex-row justify-center items-center p-2.5 gap-10">
            <Button
                variant="secondary"
                class="w-fit"
                onclick={() => console.log('Previous clicked')}
                disabled={selectedDate <= new Date()}
            >
                Anterior
            </Button>
            <DatePicker
                bind:selectedDate
                onSelectDate={(date) => console.log('Selected date:', date)}
                minDate={new Date()}
                maxDate={new Date(new Date().setMonth(new Date().getMonth() + 3))}
                class="w-fit"
            />
            <Button
                variant="secondary"
                class="w-fit"
                onclick={() => console.log('Next clicked')}
                disabled={selectedDate >= new Date(new Date().setMonth(new Date().getMonth() + 3))}
            >
                Siguiente
            </Button>
        </div>
    </div>
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
