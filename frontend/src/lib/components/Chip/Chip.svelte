<script lang="ts">
  import { cn } from '$lib/utils';

  interface Props {
    variant: 'primary' | 'secondary' | 'tertiary' | 'success' | 'destructive';
    label: string;
    class?: string;
  }

  let {
    variant = 'primary',
    label,
    class: chipClass
  }: Props = $props();

  const finalClass = cn(
    'inline-block px-3 py-1 rounded-full text-sm font-medium',
    chipClass,
    {
      primary: 'bg-primary text-white border-1 border-primary',
      secondary: 'bg-secondary text-white border-1 border-secondary',
      tertiary: 'bg-tertiary text-primaryText border-1 border-primaryBorder',
      success: 'bg-success text-white border-1 border-success',
      destructive: 'bg-red-500 text-white border-1 border-destructive',
    }[variant],
    "cursor-default select-none"
  );
</script>

<span class={finalClass}>
  {label}
</span>