<script lang="ts">
  import { cn } from '$lib/utils';
  import { clickOutside } from '$lib/actions/clickOutside';

  interface Props {
    options?: { value: string; label: string }[];
    value?: string;
    disabled?: boolean;
    skeleton?: boolean;
    class?: string;
    onchange?: (value: string) => void;
  }

  let {
    options = [],
    value = $bindable(''),
    disabled = false,
    skeleton = false,
    class: selectClass,
    onchange
  }: Props = $props();

  let isOpen = $state(false);

  const selectedOption = $derived(options.find(opt => opt.value === value));

  function handleSelect(optionValue: string) {
    value = optionValue;
    isOpen = false;
    onchange?.(optionValue);
  }

  const containerClass = cn(
    'flex w-full rounded-xl border border-primaryBorder',
    selectClass
  );

  const optionClass = (isSelected: boolean) => cn(
    'w-full justify-start flex items-center justify-center text-center',
    'px-3 py-2.5 cursor-pointer transition-colors',
    'hover:bg-bgColorHover',
    'last:rounded-r-xl first:rounded-l-xl',
    'border-r border-primaryBorder last:border-0',
    isSelected ? 'bg-primary font-semibold text-white hover:bg-primary' : ''
  );
</script>

<div class={containerClass} use:clickOutside={() => isOpen = false}>
    {#each options as option}
        <button
            class={optionClass(option.value === selectedOption?.value)}
            onclick={() => handleSelect(option.value)}
        >
            {option.label}
        </button>
    {/each}
</div>