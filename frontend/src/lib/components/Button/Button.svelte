<script lang="ts">
  import { cn } from '$lib/utils';
	import type { Snippet } from 'svelte';

  interface Params {
    ariaLabel?: string;
    variant: 'primary' | 'secondary' | 'tertiary' | 'success' | 'destructive';
    skeleton?: boolean;
    disabled?: boolean;
    children: Snippet;
    onclick: () => void;
    class?: string;
  }

  // Keep reactive object — DO NOT DESTRUCTURE
  let props: Params = $props();

  // reactive derived values
  const ariaLabel = $derived(props.ariaLabel);
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
        primary: 'bg-primary text-white hover:bg-primary-hover border-1 border-primary',
        secondary: 'bg-secondary text-white hover:bg-secondary-hover border-1 border-secondary',
        tertiary: 'bg-tertiary text-primaryText hover:bg-primary-hover hover:text-white border-1 border-primaryBorder',
        success: 'bg-success text-white hover:bg-success-hover border-1 border-success',
        destructive: 'bg-red-500 text-white hover:bg-red-600 border-1 border-destructive',
      }[variant],
      skeleton ? '!bg-skeleton animate-pulse !text-transparent cursor-default border-1 border-skeleton' : '',
      disabled ? 'opacity-50 animate-none cursor-not-allowed' : '',
      "select-none"
    )
  );
</script>

<button class={finalClass} {onclick} {disabled}>
  {@render children()}
</button>