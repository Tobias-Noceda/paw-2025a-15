<script lang="ts">
    import { page } from '$app/stores';
    import Button from '$components/Button/Button.svelte';
    import ButtonCell from '$components/Table/ButtonCell.svelte';
    import DatePicker from '$components/DatePicker/DatePicker.svelte';
    import Icon from '$components/Icon/Icon.svelte';
    import Table, { type Column } from '$components/Table/Table.svelte';
    import Toast from '$components/Toast/Toast.svelte';
    import PopUp from '$components/PopUp/PopUp.svelte';

    import { m } from '$lib/paraglide/messages';
    import { getLocale } from '$lib/paraglide/runtime';
    import { 
        fetchVacations, 
        createVacation, 
        deleteVacation,
        type Vacation,
        type VacationsResponse 
    } from '$lib/services/doctors';

    // Get doctor ID from route params
    let doctorId = $derived($page.params.id);

    // State management with Svelte 5 Runes
    let vacations = $state<VacationsResponse>({ past: [], future: [] });
    let isLoading = $state(true);
    let isSubmitting = $state(false);

    // Form state
    let startDate = $state<Date | null>(null);
    let endDate = $state<Date | null>(null);
    let formError = $state<string | null>(null);

    // Toast state
    let showSuccessToast = $state(false);
    let showErrorToast = $state(false);
    let toastMessage = $state({ title: '', description: '' });

    // Delete confirmation popup state
    let vacationToDelete = $state<Vacation | null>(null);

    // Derived: Get minimum date for pickers (today)
    let minDate = $derived(new Date());

    // Derived: Get minimum end date (start date + 1 day)
    let minEndDate = $derived(() => {
        if (!startDate) return minDate;
        const nextDay = new Date(startDate);
        nextDay.setDate(nextDay.getDate() + 1);
        return nextDay;
    });

    // Format date to ISO string (YYYY-MM-DD)
    const formatDateISO = (date: Date): string => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
    };

    // Format date for display based on locale
    const formatDateDisplay = (dateStr: string): string => {
        const date = new Date(dateStr + 'T00:00:00');
        const locale = getLocale();
        return date.toLocaleDateString(locale === 'es' ? 'es-ES' : 'en-US', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Load vacations on mount
    $effect(() => {
        loadVacations();
    });

    // Load vacations data
    const loadVacations = async () => {
        isLoading = true;
        try {
            vacations = await fetchVacations(doctorId);
        } catch (error) {
            console.error('Error loading vacations:', error);
            showError(m['vacations.error.load.title'](), m['vacations.error.load.message']());
        } finally {
            isLoading = false;
        }
    };

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
    const handleSubmit = async () => {
        if (!validateForm()) return;

        isSubmitting = true;
        try {
            const success = await createVacation(
                doctorId,
                formatDateISO(startDate!),
                formatDateISO(endDate!)
            );

            if (success) {
                showSuccess(
                    m['vacations.created.title'](),
                    m['vacations.created.message']()
                );
                // Reset form
                startDate = null;
                endDate = null;
                // Reload vacations
                await loadVacations();
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
        }
    };

    // Handle delete vacation
    const handleDelete = async () => {
        if (!vacationToDelete) return;

        isSubmitting = true;
        try {
            const success = await deleteVacation(
                doctorId,
                vacationToDelete.startDate,
                vacationToDelete.endDate
            );

            if (success) {
                showSuccess(
                    m['vacations.deleted.title'](),
                    m['vacations.deleted.message']()
                );
                vacationToDelete = null;
                await loadVacations();
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
            isSubmitting = false;
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

    // Table columns for past vacations (no actions)
    const pastColumns: Column<Vacation>[] = [
        {
            id: 'startDate',
            label: m['vacations.table.startDate'](),
            render: (vacation: Vacation) => formatDateDisplay(vacation.startDate),
            class: 'font-medium'
        },
        {
            id: 'endDate',
            label: m['vacations.table.endDate'](),
            render: (vacation: Vacation) => formatDateDisplay(vacation.endDate),
            class: 'text-secondaryText'
        }
    ];

    // Table columns for future vacations (with delete action)
    const futureColumns: Column<Vacation>[] = [
        {
            id: 'startDate',
            label: m['vacations.table.startDate'](),
            render: (vacation: Vacation) => formatDateDisplay(vacation.startDate),
            class: 'font-medium'
        },
        {
            id: 'endDate',
            label: m['vacations.table.endDate'](),
            render: (vacation: Vacation) => formatDateDisplay(vacation.endDate),
            class: 'text-secondaryText'
        },
        {
            id: 'actions',
            label: m['vacations.table.actions'](),
            columnClass: 'w-20 text-center',
            class: 'text-center',
            render: (vacation: Vacation) => {
                return {
                    component: Button,
                    props: {
                        variant: 'destructive',
                        class: 'p-2 rounded-full',
                        onclick: (e: MouseEvent) => {
                            e.stopPropagation();
                            vacationToDelete = vacation;
                        },
                        children: () => ({})
                    }
                };
            }
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
                    minDate={startDate ? minEndDate() : minDate}
                    required
                    class="w-full"
                />
            </div>
            <Button
                variant="primary"
                onclick={handleSubmit}
                disabled={isSubmitting || !startDate || !endDate}
                class="w-fit"
            >
                {#if isSubmitting}
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

        <p class="title text-primaryText mt-4">{m['vacations.past.title']()}:</p>
        <Table
            columns={pastColumns}
            rows={vacations.past}
            skeleton={isLoading}
            striped={true}
            emptyMessage={m['vacations.past.empty']()}
            class="shadow-sm rounded-lg"
        />
    </div>

    <!-- Right Panel: Future Vacations -->
    <div class="page-division flex flex-col h-full w-full gap-2.5">
        <p class="title text-primaryText">{m['vacations.future.title']()}:</p>

        {#if isLoading}
            <Table
                columns={futureColumns}
                rows={[]}
                skeleton={true}
                striped={true}
                class="shadow-sm rounded-lg"
            />
        {:else}
            <table class="w-full table-fixed border-collapse">
                <thead class="bg-table-header text-white sticky top-0 z-10 cursor-default select-none">
                    <tr>
                        <th class="text-left px-3 py-2 font-semibold first:rounded-tl-lg">
                            {m['vacations.table.startDate']()}
                        </th>
                        <th class="text-left px-3 py-2 font-semibold">
                            {m['vacations.table.endDate']()}
                        </th>
                        <th class="text-center px-3 py-2 font-semibold last:rounded-tr-lg w-20">
                            {m['vacations.table.actions']()}
                        </th>
                    </tr>
                </thead>
                <tbody>
                    {#if vacations.future.length === 0}
                        <tr>
                            <td class="px-3 py-1 text-center font-semibold text-lg text-secondaryText" colspan="3">
                                {m['vacations.future.empty']()}
                            </td>
                        </tr>
                    {:else}
                        {#each vacations.future as vacation, i}
                            <tr class="border-b border-gray-200 last:border-0 {i % 2 === 1 ? 'bg-gray-50' : ''} select-none">
                                <td class="px-3 py-2 font-medium">
                                    {formatDateDisplay(vacation.startDate)}
                                </td>
                                <td class="px-3 py-2 text-secondaryText">
                                    {formatDateDisplay(vacation.endDate)}
                                </td>
                                <td class="px-3 py-2 flex justify-center items-center">
                                    <ButtonCell
                                        text="trash"
                                        icon={true}
                                        variant="destructive"
                                        onclick={(e) => {
                                            e.stopPropagation();
                                            vacationToDelete = vacation;
                                        }}
                                    />
                                </td>
                            </tr>
                        {/each}
                    {/if}
                </tbody>
            </table>
        {/if}
    </div>
</div>

<!-- Delete Confirmation PopUp -->
{#if vacationToDelete}
    <PopUp>
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
                    variant="secondary"
                    onclick={() => vacationToDelete = null}
                >
                    {m['appointments.pop_up.back']()}
                </Button>
                <Button
                    variant="destructive"
                    onclick={handleDelete}
                    disabled={isSubmitting}
                >
                    {#if isSubmitting}
                        {m['input_loading']()}
                    {:else}
                        {m['vacations.delete.confirm.button']()}
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
