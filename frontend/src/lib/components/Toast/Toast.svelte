<script>
	import { fly } from 'svelte/transition';

	let {
		show = $bindable(false),
		title = '',
		description = '',
		variant = 'normal',
		duration = 3000,
		position = 'top-center'
	} = $props();

	// Auto-hide the toast after duration
	$effect(() => {
		if (show) {
			const timeout = setTimeout(() => {
				show = false;
			}, duration);

			return () => clearTimeout(timeout);
		}
	});

	// Compute position styles
	const positionClasses = $derived({
		'top-center': 'top-4 left-1/2 -translate-x-1/2',
		'top-right': 'top-4 right-4',
		'top-left': 'top-4 left-4',
		'bottom-center': 'bottom-4 left-1/2 -translate-x-1/2',
		'bottom-right': 'bottom-4 right-4',
		'bottom-left': 'bottom-4 left-4'
	}[position]);

	// Compute variant styles
	const variantClasses = $derived({
		normal: 'bg-white text-gray-900 border-gray-200',
		destructive: 'bg-red-50 text-red-600 border-red-600',
		success: 'bg-green-50 text-green-600 border-green-600'
	}[variant]);
</script>

{#if show}
	<div
		class="fixed z-50 {positionClasses}"
		transition:fly={{ y: -20, duration: 300 }}
		role="alert"
	>
		<div
			class="px-4 py-3 rounded-md border shadow-lg {variantClasses}"
		>
			{#if title}
				<div class="font-semibold mb-1">{title}</div>
			{/if}
			{#if description}
				<div class="text-sm">{description}</div>
			{/if}
		</div>
	</div>
{/if}
