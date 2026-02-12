<script lang="ts">
  import { clickOutside } from "$lib/actions/clickOutside";
  import { format } from "date-fns";
	import { cn } from "$lib/utils";

	import Button from "$components/Button/Button.svelte";
	import Icon from "$components/Icon/Icon.svelte";

  import { getLocale } from '$lib/paraglide/runtime'
  import * as m from '$lib/paraglide/messages.js';
	import Select from "$components/Select/Select.svelte";

  interface Props {
    id?: string;
    label?: string;
    required?: boolean;
    selectedDate: Date | null;
    onSelectDate?: (date: Date | null) => void;
    minDate?: Date;
    maxDate?: Date;
    yearRange?: number[];
    erasable?: boolean;
    class?: string;
  }

  let {
    id = 'date-picker',
    label,
    required = false,
    selectedDate = $bindable(null),
    onSelectDate,
    /**
     * Minimum selectable date (inclusive). Dates before this will be disabled.
    */
    minDate,
    /**
     * Maximum selectable date (not inclusive). Dates after this will be disabled.
    */
    maxDate,
    /**
     * Array of years to show in the year selector. If not provided, defaults to a range of 3 years centered around the current year.
    */
    yearRange,
    erasable,
    class: datePickerClass
  }: Props = $props();

  let showCalendar = $state(false);
  let showMonthYearPicker = $state(false);

  let showingMonth: Date = $derived(selectedDate ? new Date(selectedDate) : new Date());

  let currentMonth: string = $state(selectedDate ? new Date(selectedDate).toLocaleDateString(getLocale(), { month: 'numeric' }) : new Date().toLocaleDateString(getLocale(), { month: 'numeric' }));
  let currentYear: string = $derived(selectedDate ? new Date(selectedDate).getFullYear().toString() : new Date().getFullYear().toString());

  let buttonRef: HTMLButtonElement;

  const daysShort = [
    m['weekdays_short.mon'](),
    m['weekdays_short.tue'](),
    m['weekdays_short.wed'](),
    m['weekdays_short.thu'](),
    m['weekdays_short.fri'](),
    m['weekdays_short.sat'](),
    m['weekdays_short.sun']()
  ];

  function getDaysInMonth(date: Date) {
    const year = date.getFullYear();
    const month = date.getMonth();
    const firstDay = new Date(year, month, 1);
    const lastDay = new Date(year, month + 1, 0);
    const days = [];
    // Fill empty slots before first day
    for (let i = 0; i < (firstDay.getDay() + 6) % 7; i++) days.push(null);
    // Fill days of month
    for (let d = 1; d <= lastDay.getDate(); d++) days.push(new Date(year, month, d));
    return days;
  }

  function getMonthName(monthIndex: number) {
    const monthNames = [
      m['months.jan'](),
      m['months.feb'](),
      m['months.mar'](),
      m['months.apr'](),
      m['months.may'](),
      m['months.jun'](),
      m['months.jul'](),
      m['months.aug'](),
      m['months.sep'](),
      m['months.oct'](),
      m['months.nov'](),
      m['months.dec']()
    ];
    
    return monthNames[monthIndex];
  }

  function prevMonth() {
    showingMonth = new Date(showingMonth.getFullYear(), showingMonth.getMonth() - 1, 1);
    currentMonth = showingMonth.toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = showingMonth.getFullYear().toString();
  }

  function nextMonth() {
    showingMonth = new Date(showingMonth.getFullYear(), showingMonth.getMonth() + 1, 1);
    currentMonth = showingMonth.toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = showingMonth.getFullYear().toString();
  }

  function setMonthYear(month: number, year: number) {
    showingMonth = new Date(year, month, 1);
    showMonthYearPicker = false;
    currentMonth = showingMonth.toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = showingMonth.getFullYear().toString();
  }

  function selectDate(date: Date) {
    selectedDate = date;
    onSelectDate?.(date);
    showCalendar = false;
    currentMonth = date.toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = date.getFullYear().toString();
  }

  function clearDate() {
    selectedDate = null;
    onSelectDate?.(null);
    showCalendar = false;
    currentMonth = new Date().toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = new Date().getFullYear().toString();
  }

  function selectToday() {
    const today = new Date();
    selectedDate = today;
    onSelectDate?.(today);
    showingMonth = today;
    currentMonth = today.toLocaleDateString(getLocale(), { month: 'numeric' });
    currentYear = today.getFullYear().toString();
    showCalendar = false;
  }

  function isDateDisabled(date: Date) {
    // Compare dates without time
    const dateOnly = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    
    if (minDate) {
      const minDateOnly = new Date(minDate.getFullYear(), minDate.getMonth(), minDate.getDate());
      if (dateOnly < minDateOnly) return true;
    }
    
    if (maxDate) {
      const maxDateOnly = new Date(maxDate.getFullYear(), maxDate.getMonth(), maxDate.getDate());
      if (dateOnly > maxDateOnly) return true;
    }
    
    return false;
  }
</script>

<div class="relative inline-block {datePickerClass}">
  {#if label}
    <label class="text-sm font-medium text-text" for={id}>
      {label}
      {#if required}
        <span class="text-red-500">*</span>
      {/if}
    </label>
  {/if}
  <button
    bind:this={buttonRef}
    type="button"
    onclick={(e) => { e.stopPropagation(); showCalendar = !showCalendar; }}
    class={
      cn(
        "w-60",
        datePickerClass,
        "flex items-center justify-left",
        "border border-primaryBorder font-semibold rounded-md px-4 py-2",
        "text-secondaryText select-none! cursor-pointer!"
      )
    }
  >
    <Icon name={'calendar'} class="w-5 h-5 text-secondaryText mr-2" />
    {selectedDate ? format(selectedDate, getLocale() === 'es' ? "dd/MM/yyyy" : "MM/dd/yyyy") : m['date_format']().toLowerCase()}
  </button>
  {#if showCalendar}
    <div
      class="absolute bg-white border border-primaryBorder rounded-xl shadow p-2 mt-1 z-1000"
      style="width: 240px;"
      use:clickOutside={(event?: MouseEvent) => {
        if (!buttonRef.contains(event?.target as Node)) {
          showCalendar = false;
        }
      }}
    >
      <div class="flex justify-between items-center mb-2">
        <Button
          variant="secondary"
          onclick={prevMonth}
          class="p-2 rounded-full"
        >
          <Icon name="left-arrow" class="w-2 h-2" />
        </Button>
        {#if !showMonthYearPicker}
          <button
            type="button"
            onclick={() => showMonthYearPicker = true}
            class="font-semibold hover:bg-gray-100 px-2 py-1 rounded"
          >
            {getMonthName(showingMonth.getMonth())} {showingMonth.getFullYear()}
          </button>
        {:else}
          <div class="flex gap-1">
            <Select
              class="text-[10px] max-w-20! h-fit!"
              bind:value={currentMonth}
              options={Array.from({ length: 12 }, (_, i) => ({ value: (i + 1).toString(), label: getMonthName(i) }))}
              onchange={(e) => setMonthYear(Number(e) - 1, showingMonth.getFullYear())}
            />
            <Select
              class="text-[10px] max-w-fit! h-fit!"
              options={
                yearRange ? yearRange.map((year) => ({ value: year.toString(), label: year.toString() })) :
                Array.from({ length: 3 }, (_, i) => {
                  const year = new Date().getFullYear() - 1 + i;
                  return { value: year.toString(), label: year.toString() };
                })}
              bind:value={currentYear}
              onchange={(e) => setMonthYear(showingMonth.getMonth(), Number(e))}
            />
          </div>
        {/if}
        <Button
          variant="secondary"
          onclick={nextMonth}
          class="p-2 rounded-full"
        >
          <Icon name="right-arrow" class="w-2 h-2" />
        </Button>
      </div>
      <div class="grid grid-cols-7 text-center mb-1">
        {#each daysShort as d}
          <div class="font-bold">{d}</div>
        {/each}
      </div>
      <div class="grid grid-cols-7 text-center">
        {#each getDaysInMonth(showingMonth) as day}
          {#if day}
            <button
              class={
                cn(
                  "p-1 m-px rounded-lg cursor-pointer",
                  selectedDate && day.toDateString() === selectedDate.toDateString()
                    ?'bg-primary text-white hover:bg-primary-hover'
                    : 'bg-white text-black hover:bg-gray-200/60',
                  isDateDisabled(day) ? 'bg-gray-200/60 hover:bg-gray-200/60 opacity-40 cursor-not-allowed' : ''
                )
              }
              disabled={isDateDisabled(day)}
              onclick={() => selectDate(day)}
            >
              {day.getDate()}
            </button>
          {:else}
            <div></div>
          {/if}
        {/each}
      </div>
      <div class="flex {erasable ? 'justify-between' : 'justify-end'} mt-2 text-sm">
        {#if erasable}
          <Button
            onclick={clearDate}
            variant="secondary"
            class="py-1 px-2 font-normal leading-none rounded-lg"
          >
            {m.clear()}
          </Button>
        {/if}
        <Button
          onclick={selectToday}
          variant="secondary"
          class="py-1 px-2 font-normal leading-none rounded-lg"
        >
          {m.today()}
        </Button>
      </div>
    </div>
  {/if}
</div>