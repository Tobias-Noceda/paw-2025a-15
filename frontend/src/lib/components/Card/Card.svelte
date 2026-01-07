<script lang="ts">
	import Avatar from "$components/Avatar/Avatar.svelte";
	import Chip from "$components/Chip/Chip.svelte";

	import { cn } from "$lib/utils";

  import * as m from '$lib/paraglide/messages.js';
	import { Weekdays } from "$types/enums/weekdays";

  interface Props {
    variant: 'doctor' | 'patient';
    avatarSrc: string;
    userName: string;
    specialization?: string;
    insurances?: string[];
    schedule?: Set<Weekdays>;
    email?: string;
    phone?: string;
    class?: string;
  }

  let {
    variant = 'doctor',
    avatarSrc,
    userName,
    specialization,
    insurances = [],
    schedule,
    email,
    phone,
    class: cardClass
  }: Props = $props();

  const daysShort = [
    m['weekdays_short.mon'](),
    m['weekdays_short.tue'](),
    m['weekdays_short.wed'](),
    m['weekdays_short.thu'](),
    m['weekdays_short.fri'](),
    m['weekdays_short.sat'](),
    m['weekdays_short.sun']()
  ];
</script>

<div
  class={
    cn(
      cardClass,
      "bg-white rounded-xl w-fit justify-center",
      variant === 'doctor' ? "px-[25px] py-[30px]" : "px-4 py-6 gap-4"
    )}
  style="box-shadow: 0 8px 24px rgba(0,0,0,0.1);"
>
  <div
    class={
      cn(
        "flex flex-col items-center gap-4",
        variant === 'doctor' ? "px-4 py-6 w-[210px]" : "w-[260px]"
      )
    }
  >
    <Avatar src={avatarSrc} size="xl" class="bg-primary" />
    <div class="gap-0 text-center">
      <h3 class="text-[1.2rem] font-semibold m-0 mb-2 text-primary leading-[1.146]">{userName}</h3>
      {#if variant === 'doctor' && specialization}
        <p class="text-[0.9rem] text-secondaryText mb-1 leading-[1.1112]">{specialization}</p>
      {/if}
      {#if variant === 'patient' && email}
        <p class="text-[0.9rem] text-secondaryText mb-1 leading-[1.294]">
          <strong>Email: </strong>
          {email}
        </p>
      {/if}
      {#if variant === 'patient' && phone}
        <p class="text-[0.9rem] text-secondaryText mb-1 leading-[1.294]">
          <strong>Tel: </strong>
          {phone}
        </p>
      {/if}
      <span class="text-[0.8rem] text-secondaryText leading-[1.1112]">{insurances.join(", ")}</span>
    </div>
    <div class="flex flex-wrap gap-2 justify-center m-0">
      {#if variant === 'doctor' && schedule}
        <!-- Loop over the Weekdays Enum -->
        {#each Object.values(Weekdays) as day, index}
          <Chip
            label={daysShort[index]}
            variant={schedule.has(day) ? 'primary' : 'tertiary'}
            class="text-[0.8rem] px-2.5 py-[5px] font-bold"
          />
        {/each}
      {/if}
    </div>
  </div>
</div>