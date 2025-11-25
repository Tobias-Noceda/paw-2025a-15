<script lang="ts">
  import { type Snippet } from 'svelte';
  import { cn } from '$lib/utils';

  interface Params {
    variant: 'primary' | 'secondary' | 'success' | 'destructive';
    skeleton?: boolean;
    disabled?: boolean;
    children: Snippet;
    onclick: () => void;
    class?: string;
  }

  let props: Params = $props();
  const { variant, skeleton, disabled, children, onclick } = props;

  const variantClasses = {
    primary: 'bg-primary text-white hover:bg-primary-hover',
    secondary: 'bg-secondary text-white hover:bg-secondary-hover',
    success: 'bg-success text-white hover:bg-success-hover',
    destructive: 'bg-red-500 text-white hover:bg-red-600',
  }

  const baseClass = 'rounded-md px-4 py-2 cursor-pointer transition-colors text-base';

  const finalClass = cn(
    'flex items-center justify-center font-semibold',
    props.class,
    baseClass,
    variantClasses[variant],
    skeleton ? '!bg-skeleton animate-pulse text-transparent cursor-default' : '',
    disabled ? 'opacity-50 animate-none cursor-not-allowed' : '',
  );
</script>
  
<button class={finalClass} {onclick} {disabled}>
  {@render children()}
</button>