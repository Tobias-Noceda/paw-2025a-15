<script lang="ts">
	import { cn } from "$lib/utils";

	interface Props {
		index?: number;
		selected?: boolean;
		onclick: (selected: boolean, index?: number) => void;
		disabled?: boolean;
		class?: string;
	}

	let {
		index,
		selected = $bindable(false),
		onclick,
		disabled = false,
		class: className = ''
	}: Props = $props();
</script>

<input
	type="checkbox"
	class={cn('form-checkbox mt-0.75 w-4! h-4! rounded-sm text-primary', className, 'cursor-pointer!')}
	onchange={(event) => onclick((event.target as HTMLInputElement).checked, index)}
	bind:checked={selected}
	disabled={disabled}
/>

<style>
	/* Custom styles for the toggle switch */
	.form-checkbox:checked {
		background-color: var(--color-primary);
		border-color: var(--color-primary);
	}

	.form-checkbox:focus {
		box-shadow: none !important;
	}
</style>
