<script lang="ts">
  import { cn } from '$lib/utils';

  interface Props {
    size?: 'sm' | 'md' | 'lg' | 'auto';
  }

  let { size = 'md' }: Props = $props();

  const sizeClasses = {
    sm: 'w-[40px] h-[4px] mt-[8px] mx-auto',
    md: 'w-[60px] h-[4px] mt-[8px] mx-auto',
    lg: 'w-[80px] h-[4px] mt-[8px] mx-auto',
    auto: 'w-full h-[4px] my-[8px]',
  };

  const finalClass = $derived(cn(
    'bg-primary rounded-full',
    sizeClasses[size]
  ));
</script>

<div class={finalClass}></div>
