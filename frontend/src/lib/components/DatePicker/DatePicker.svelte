<script lang="ts">
  import { clickOutside } from "$lib/actions/clickOutside";
  import { format } from "date-fns";
	import { cn } from "$lib/utils";

	import Button from "$components/Button/Button.svelte";
	import Icon from "$components/Icon/Icon.svelte";

  import * as m from '$lib/paraglide/messages.js';

  interface Props {
    selectedDate: Date | null;
    onSelectDate?: (date: Date | null) => void;
    minDate?: Date;
    maxDate?: Date;
    erasable?: boolean;
    class?: string;
  }

  let {
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
    erasable,
    class: datePickerClass
  }: Props = $props();

  let showCalendar = $state(false);
  let currentMonth: Date = $state(selectedDate ? new Date(selectedDate) : new Date());

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
    currentMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1);
  }

  function nextMonth() {
    currentMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1);
  }

  function selectDate(date: Date) {
    selectedDate = date;
    onSelectDate?.(date);
    showCalendar = false;
  }

  function clearDate() {
    selectedDate = null;
    onSelectDate?.(null);
    showCalendar = false;
  }

  function selectToday() {
    const today = new Date();
    selectedDate = today;
    onSelectDate?.(today);
    currentMonth = today;
    showCalendar = false;
  }

  function isDateDisabled(date: Date) {
    if (minDate && date.getDate() < minDate.getDate()) return true;
    if (maxDate && date.getDate() >= maxDate.getDate()) return true;
    return false;
  }
</script>

<div>
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
    {selectedDate ? format(selectedDate, "dd/MM/yyyy") : "dd/mm/aaaa"}
  </button>
  {#if showCalendar}
    <div
      class="absolute bg-white border border-primaryBorder rounded-xl shadow p-2 mt-1 z-10"
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
        <span>
          {getMonthName(currentMonth.getMonth())} {currentMonth.getFullYear()}
        </span>
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
        {#each getDaysInMonth(currentMonth) as day}
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