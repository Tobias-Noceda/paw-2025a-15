<script lang="ts">
  import { cn } from '$lib/utils';
	import type { Snippet } from 'svelte';

  interface Params {
    variant: 'primary' | 'secondary' | 'tertiary' | 'success' | 'destructive';
    skeleton?: boolean;
    disabled?: boolean;
    children: Snippet;
    onclick: (event: MouseEvent) => void;
    class?: string;
  }

  // Keep reactive object — DO NOT DESTRUCTURE
  let props: Params = $props();

  // reactive derived values
  const variant = $derived(props.variant);
  const skeleton = $derived(props.skeleton ?? false);
  const disabled = $derived(props.disabled ?? false);
  const children = $derived(props.children);
  const onclick = $derived(props.onclick);

  const finalClass = $derived(
    cn(
      'flex items-center justify-center font-semibold rounded-md px-4 py-2',
      props.class,
      'cursor-pointer transition-colors text-base',
      {
        primary: 'bg-primary text-white hover:bg-primary-hover border-1 border-primary disabled:opacity-50',
        secondary: 'bg-secondary text-white hover:bg-secondary-hover border-1 border-secondary disabled:bg-skeleton disabled:border-skeleton disabled:text-gray-500',
        tertiary: 'bg-tertiary text-primaryText hover:bg-primary-hover hover:text-white border-1 border-primaryBorder disabled:opacity-50',
        success: 'bg-success text-white hover:bg-success-hover border-1 border-success disabled:opacity-50',
        destructive: 'bg-red-500 text-white hover:bg-red-600 border-1 border-destructive disabled:opacity-50',
      }[variant],
      skeleton ? '!bg-skeleton animate-pulse !text-transparent cursor-default border-1 border-skeleton' : '',
      disabled ? 'animate-none cursor-not-allowed' : '',
      "select-none"
    )
  );
</script>

<button class={finalClass} {onclick} {disabled}>
  {@render children()}
</button>