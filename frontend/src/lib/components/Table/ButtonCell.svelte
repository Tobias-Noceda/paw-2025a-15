<script lang="ts">
	import Button from '$components/Button/Button.svelte';
	import Icon from '$components/Icon/Icon.svelte';

	interface Props {
		text: string;
		icon?: boolean;
		variant?: 'primary' | 'secondary' | 'tertiary' | 'success' | 'destructive';
		onclick: (event: MouseEvent) => void;
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
</script>

<Button {variant} {onclick} {disabled} class={className}>
	{#if icon}
		<Icon name={text} class="w-4 h-4" />
    {:else}
		{text}
	{/if}
</Button>
