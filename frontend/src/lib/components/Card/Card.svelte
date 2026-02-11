<script lang="ts">
	import Avatar from '$components/Avatar/Avatar.svelte';
	import Chip from '$components/Chip/Chip.svelte';

	import { cn } from '$lib/utils';

	import * as m from '$lib/paraglide/messages.js';
	import { Weekdays } from '$types/enums/weekdays';
	import type { Snippet } from 'svelte';

	interface Props {
		variant: 'doctor' | 'patient' | 'insurance';
		avatarSrc: string;
		userName: string;
		specialization?: string;
		insurances?: string[];
		schedule?: Set<Weekdays>;
		email?: string;
		phone?: string;
		skeleton?: boolean;
		onclick?: () => void;
		buttons?: Snippet;
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
		skeleton = false,
		onclick,
    buttons,
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

<button
	class={cn(
		cardClass,
		'bg-white rounded-xl w-fit justify-center',
		variant === 'doctor' ? 'px-[25px] py-[30px]' : 'px-4 py-6 gap-4',
		skeleton
			? 'animate-pulse'
			: onclick
				? 'cursor-pointer hover:shadow-lg transition duration-300 hover:bg-bgColorHover'
				: ''
	)}
	style="box-shadow: 0 8px 24px rgba(0,0,0,0.1);"
	{onclick}
>
	<div
		class={cn(
			'flex flex-col items-center gap-4',
			variant === 'doctor' ? 'px-4 py-6 w-[220px]' : 'w-[260px]'
		)}
	>
		<Avatar src={avatarSrc} size="xl" {skeleton} class="bg-primary" />
		{#if skeleton}
			<div class="gap-0 text-center">
				<div class="h-6 w-32 bg-skeleton rounded mb-2 mx-auto"></div>
				{#if variant === 'doctor'}
					<div class="h-4 w-24 bg-skeleton rounded mb-1 mx-auto"></div>
				{/if}
				{#if variant === 'patient' && email}
					<div class="h-4 w-40 bg-skeleton rounded mb-1 mx-auto"></div>
				{/if}
				{#if variant === 'patient' && phone}
					<div class="h-4 w-32 bg-skeleton rounded mb-1 mx-auto"></div>
				{/if}
				<div class="h-4 w-48 bg-skeleton rounded mx-auto"></div>
			</div>
		{:else}
			<div class="gap-0 text-center">
				<h3 class="text-[1.2rem] font-semibold m-0 mb-2 text-primary leading-[1.146]">
					{userName}
				</h3>
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
        {#if variant !== 'insurance' && insurances.length > 0}
          <span class="text-[0.8rem] text-secondaryText leading-[1.1112]"
            >{insurances.join(', ')}</span
          >
        {/if}
			</div>
			<div class="flex flex-wrap gap-2 justify-center m-0">
				{#if variant === 'doctor' && schedule}
					<!-- Loop over the Weekdays Enum -->
					{#each Object.values(Weekdays) as day, index}
						<Chip
							label={daysShort[index]}
							variant={schedule.has(day) ? 'primary' : 'tertiary'}
							class="text-[0.8rem] px-2.5 py-[5px] font-bold cursor-pointer"
						/>
					{/each}
				{/if}
			</div>
      {#if buttons}
        {@render buttons()}
      {/if}
		{/if}
	</div>
</button>
