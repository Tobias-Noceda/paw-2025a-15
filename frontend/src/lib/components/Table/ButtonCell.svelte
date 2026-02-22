<script lang="ts">
	import Button from '$components/Button/Button.svelte';
	import Icon from '$components/Icon/Icon.svelte';

	type IconVariants = 'primary' | 'secondary' | 'tertiary' | 'success' | 'destructive';

	interface Props {
		text: string;
		icon?: boolean;
		variant?: IconVariants | IconVariants[];
		onclick: (event: MouseEvent, index?: number) => void;
		disabled?: boolean;
		class?: string;
	}

	let {
		text,
		icon = false,
		variant = 'primary',
		onclick,
		disabled = false,
		class: className = ''
	}: Props = $props();

	const icons = icon && text ? text.split(' ') : [];

	if (
		(!icon && Array.isArray(variant)) ||
		(icon && Array.isArray(variant) && icons.length !== variant.length)
	) {
		throw new Error('If icon is true and variant is an array, the length of the variant array must match the number of icons.');
	}
</script>

{#if !Array.isArray(variant)}
	<Button {variant} {onclick} {disabled} class={className}>
		{#if icon}
			{#each icons as iconName, index}
				<Icon name={iconName} class="w-4 h-4" />
			{/each}
		{:else}
			{text}
		{/if}
	</Button>
{:else}
	{#each icons as iconName, index}
		<Button variant={variant[index]} onclick={(event) => onclick(event, index)} disabled={disabled} class={className}>
			<Icon name={iconName} class="w-4 h-4" />
		</Button>
	{/each}
{/if}