<script lang="ts">
  import { cn } from '$lib/utils';
  import { clickOutside } from '$lib/actions/clickOutside';
	import type { Snippet } from 'svelte';
	import Avatar from '$components/Avatar/Avatar.svelte';

  interface Props {
    label?: string;
    options?: { value: string; label: string, icon?: Snippet, avatarSrc?: string }[];
    value?: string;
    disabled?: boolean;
    skeleton?: boolean;
    placeholder?: string;
    class?: string;
    onchange?: (value: string) => void;
  }

  let {
    label = '',
    options = [],
    value = $bindable(''),
    disabled = false,
    skeleton = false,
    placeholder = 'Select an option',
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
    'relative w-60',
    label ? 'mt-1' : '',
    selectClass
  );

  const buttonClass = cn(
    'bg-bgColor text-primaryText w-full',
    'disabled:opacity-50 disabled:cursor-not-allowed',
    'px-3 py-2.5 rounded-xl transition-colors cursor-pointer',
    'border border-primaryBorder hover:border-primaryBorder',
    'focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent',
    'flex items-center justify-between',
    skeleton ? 'bg-skeleton animate-pulse text-transparent cursor-default' : ''
  );

  const dropdownClass = cn(
    'absolute z-50 mt-1 w-full flex-col',
    'bg-bgColor border border-primaryBorder rounded-xl shadow-lg',
    'max-h-60 overflow-y-auto',
    // Custom scrollbar styles
    'scrollbar-thin scrollbar-thumb-gray-400 scrollbar-track-transparent',
    '[&::-webkit-scrollbar]:w-2',
    '[&::-webkit-scrollbar-track]:bg-transparent',
    '[&::-webkit-scrollbar-thumb]:bg-gray-400',
    '[&::-webkit-scrollbar-thumb]:rounded-full',
    '[&::-webkit-scrollbar-thumb]:hover:bg-gray-500'
  );

  const optionClass = (isSelected: boolean) => cn(
    'w-full justify-start flex items-center',
    'px-3 py-2.5 cursor-pointer transition-colors',
    'hover:bg-primary hover:text-white',
    isSelected ? 'bg-primary/10 font-medium' : ''
  );
</script>

<div>
  {#if label}
    <label for="select">
      {label}
    </label>
  {/if}
  <div class={containerClass} use:clickOutside={() => isOpen = false}>
    <button
      type="button"
      class={buttonClass}
      {disabled}
      onclick={() => !disabled && !skeleton && (isOpen = !isOpen)}
    >
      <span class={selectedOption ? '' : 'text-gray-400'}>
        {selectedOption ? selectedOption.label : placeholder}
      </span>
      <svg 
        class={cn('w-4 h-4 transition-transform', isOpen ? 'rotate-180' : '')}
        fill="none" 
        stroke="currentColor" 
        viewBox="0 0 24 24"
      >
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7" />
      </svg>
    </button>

    {#if isOpen && !disabled && !skeleton}
      <div class={dropdownClass}>
        {#each options as option, index}
          <button
            tabindex={index}
            class={optionClass(option.value === value)}
            onclick={() => handleSelect(option.value)}
            role="option"
            aria-selected={option.value === value}
          >
            {#if option.icon}
              {@render option.icon()}
            {:else if option.avatarSrc}
              <Avatar size="sm" src={option.avatarSrc} class="mr-2" />
            {/if}
            {option.label}
          </button>
        {/each}
      </div>
    {/if}
  </div>
</div>