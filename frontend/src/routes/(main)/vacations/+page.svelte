<script lang="ts">
    import Button from '$components/Button/Button.svelte';
    import ButtonCell from '$components/Table/ButtonCell.svelte';
    import DatePicker from '$components/DatePicker/DatePicker.svelte';
    import Table, { type Column } from '$components/Table/Table.svelte';
    import Toast from '$components/Toast/Toast.svelte';
    import PopUp from '$components/PopUp/PopUp.svelte';

    import { m } from '$lib/paraglide/messages';
    import { getLocale } from '$lib/paraglide/runtime';

	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import type { Paginated, Vacations } from '$types/api';
	import type { PageData } from './$types';
	import { createVacation, deleteVacation, fetchVacations } from '$lib/services/vacations';

	let { data }: { data: PageData } = $props();

    let pastVacations: Paginated<Vacations> = $state(data.pastVacations);
    let futureVacations: Paginated<Vacations> = $state(data.futureVacations);
    let isSubmitting = $state(false);

    let isCreating = $state(false);
    let isDeleting = $state(false);

    // Form state
    let startDate = $state<Date | null>(null);
    let endDate = $state<Date | null>(null);
    let formError = $state<string | null>(null);

    // Toast state
    let showSuccessToast = $state(false);
    let showErrorToast = $state(false);
    let toastMessage = $state({ title: '', description: '' });

    // Delete confirmation popup state
    let vacationToDelete = $state<Vacations | null>(null);
    let deleteingVacationIdx = $state<number | null>(null);

    // Derived: Get minimum date for pickers (today)
    let minDate = $derived(new Date());

    // Validate the form
    const validateForm = (): boolean => {
        formError = null;

        if (!startDate) {
            formError = m['vacations.error.startRequired']();
            return false;
        }

        if (!endDate) {
            formError = m['vacations.error.endRequired']();
            return false;
        }

        // Check that end date is after start date
        if (endDate <= startDate) {
            formError = m['vacations.error.endBeforeStart']();
            return false;
        }

        // Check that start date is not in the past
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        const startDateClean = new Date(startDate);
        startDateClean.setHours(0, 0, 0, 0);

        if (startDateClean < today) {
            formError = m['vacations.error.pastDate']();
            return false;
        }

        return true;
    };

    // Handle form submission
    const handleSubmit = async (cancel: boolean = true) => {
        if (!validateForm() || !startDate || !endDate) return;

        isSubmitting = true;
        try {
            const success = await createVacation(
                data.doctor.links.self,
                startDate,
                endDate,
                cancel
            ).catch((error) => {
                console.error('Error creating vacation:', error);
                return null;
            });

            if (success) {
                showSuccess(
                    m['vacations.created.title'](),
                    m['vacations.created.message']()
                );
                // Reset form
                startDate = null;
                endDate = null;

                futureVacations = await fetchVacations(data.doctor.links.futureVacations, fetch);
            } else {
                showError(
                    m['vacations.error.create.title'](),
                    m['vacations.error.create.message']()
                );
            }
        } catch (error) {
            console.error('Error creating vacation:', error);
            showError(
                m['vacations.error.create.title'](),
                m['vacations.error.create.message']()
            );
        } finally {
            isSubmitting = false;
            isCreating = false;
        }
    };

    // Handle delete vacation
    const handleDelete = async () => {
        if (!vacationToDelete || deleteingVacationIdx === null) return;

        isDeleting = true;
        try {
            const success = await deleteVacation(vacationToDelete.links.self);

            if (success) {
                showSuccess(
                    m['vacations.deleted.title'](),
                    m['vacations.deleted.message']()
                );
                vacationToDelete = null;
                futureVacations.results.splice(deleteingVacationIdx, 1);
            } else {
                showError(
                    m['vacations.error.delete.title'](),
                    m['vacations.error.delete.message']()
                );
            }
        } catch (error) {
            console.error('Error deleting vacation:', error);
            showError(
                m['vacations.error.delete.title'](),
                m['vacations.error.delete.message']()
            );
        } finally {
            isDeleting = false;
        }
    };

    // Show success toast
    const showSuccess = (title: string, description: string) => {
        toastMessage = { title, description };
        showSuccessToast = true;
    };

    // Show error toast
    const showError = (title: string, description: string) => {
        toastMessage = { title, description };
        showErrorToast = true;
    };

    const formatDateDisplay = (date: string | Date) => {
        const parsedDate = date instanceof Date ? date : parseDateInLocalTimezone(date);
        return parsedDate.toLocaleDateString(getLocale(), {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Table columns for past vacations (no actions)
    const pastColumns: Column<Vacations>[] = [
        {
            id: 'startDate',
            label: m['vacations.table.startDate'](),
            render: (vacation: Vacations) => formatDateDisplay(vacation.startDate),
            class: 'font-medium'
        },
        {
            id: 'endDate',
            label: m['vacations.table.endDate'](),
            render: (vacation: Vacations) => formatDateDisplay(vacation.endDate),
            class: 'text-secondaryText'
        }
    ];

    // Table columns for future vacations (with delete action)
    const futureColumns: Column<Vacations>[] = [
        ...pastColumns,
        {
            id: 'actions',
            label: m['action'](),
            render: (vacation: Vacations, index: number) => {
                return {
                    component: ButtonCell,
                    props: {
                        text: 'trash',
                        icon: true,
                        variant: 'destructive',
                        onclick: (e: MouseEvent) => {
                            e.stopPropagation();
                            e.preventDefault();

                            vacationToDelete = vacation;
                            deleteingVacationIdx = index;
                        }
                    }
                };
            },
            class: 'flex justify-center items-center text-center',
            columnClass: 'w-20'
        }
    ];
</script>

<div class="flex gap-5">
    <!-- Left Panel: Create Vacation Form + Past Vacations -->
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m['vacations.create.title']()}:</p>

        <div class="flex flex-wrap items-end gap-4">
            <div class="flex-1 min-w-[180px]">
                <DatePicker
                    id="start-date"
                    label={m['vacations.table.startDate']()}
                    bind:selectedDate={startDate}
                    minDate={minDate}
                    required
                    class="w-full"
                />
            </div>
            <div class="flex-1 min-w-[180px]">
                <DatePicker
                    id="end-date"
                    label={m['vacations.table.endDate']()}
                    bind:selectedDate={endDate}
                    minDate={startDate ?? minDate}
                    required
                    class="w-full"
                />
            </div>
            <Button
                variant="primary"
                onclick={() => {
                    if (validateForm()) {
                        isCreating = true;
                    }
                }}
                disabled={isCreating || !startDate || !endDate}
                class="w-fit"
            >
                {#if isCreating}
                    {m['input_loading']()}
                {:else}
                    {m['vacations.create.submit']()}
                {/if}
            </Button>
        </div>

        {#if formError}
            <div class="text-error-text text-sm bg-error-bg px-4 py-2 rounded-md border border-red-200">
                {formError}
            </div>
        {/if}

        <p class="title text-primaryText">{m['vacations.future.title']()}:</p>
        <Table
            columns={futureColumns}
            rows={futureVacations}
            nextFetchFunction={(url) => fetchVacations(url, fetch)}
            striped
            hover
            emptyMessage={m['vacations.future.empty']()}
            class="shadow-sm rounded-lg"
        />
    </div>

    <!-- Right Panel: Past Vacations -->
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText mt-4">{m['vacations.past.title']()}:</p>
        <Table
            columns={pastColumns}
            rows={pastVacations}
            nextFetchFunction={(url) => fetchVacations(url, fetch)}
            striped
            hover
            emptyMessage={m['vacations.past.empty']()}
            class="shadow-sm rounded-lg"
        />
    </div>
</div>

<!-- Registering decision PopUp -->
{#if isCreating && startDate && endDate}
    <PopUp onClose={() => isCreating = false}>
        <div class="flex flex-col gap-2">
            <h1 class="text-primaryText text-[1.17rem] font-bold">
                {m['vacations.creating.confirm.title']()}
            </h1>
            <p class="text-primaryText">
                {m['vacations.creating.confirm.message']({
                    startDate: formatDateDisplay(startDate),
                    endDate: formatDateDisplay(endDate)
                })}
            </p>
            <div class="flex justify-end gap-4 mt-2">
                <Button
                    variant="primary"
                    onclick={() => handleSubmit(true)}
                    disabled={isSubmitting}
                >
                    {m['vacations.creating.confirm.yes']()}
                </Button>
                <Button
                    variant="destructive"
                    onclick={() => handleSubmit(false)}
                    disabled={isSubmitting}
                >
                    {#if isSubmitting}
                        {m['input_loading']()}
                    {:else}
                        {m['vacations.creating.confirm.no']()}
                    {/if}
                </Button>
            </div>
        </div>
    </PopUp>
{/if}

<!-- Delete Confirmation PopUp -->
{#if vacationToDelete}
    <PopUp onClose={() => vacationToDelete = null}>
        <div class="flex flex-col gap-2">
            <h1 class="text-primaryText text-[1.17rem] font-bold">
                {m['vacations.delete.confirm.title']()}
            </h1>
            <p class="text-primaryText">
                {m['vacations.delete.confirm.message']({
                    startDate: formatDateDisplay(vacationToDelete.startDate),
                    endDate: formatDateDisplay(vacationToDelete.endDate)
                })}
            </p>
            <div class="flex justify-end gap-4 mt-2">
                <Button
                    variant="primary"
                    onclick={handleDelete}
                >
                    {m['vacations.delete.confirm.button']()}
                </Button>
                <Button
                    variant="destructive"
                    onclick={() => vacationToDelete = null}
                    disabled={isDeleting}
                >
                    {#if isDeleting}
                        {m['input_loading']()}
                    {:else}
                        {m['vacations.delete.confirm.back']()}
                    {/if}
                </Button>
            </div>
        </div>
    </PopUp>
{/if}

<!-- Toast Notifications -->
<Toast
    bind:show={showSuccessToast}
    variant="success"
    title={toastMessage.title}
    description={toastMessage.description}
/>
<Toast
    bind:show={showErrorToast}
    variant="destructive"
    title={toastMessage.title}
    description={toastMessage.description}
/>

<style>
    .title {
        font-size: 22px;
        font-weight: 700;
    }
</style>
