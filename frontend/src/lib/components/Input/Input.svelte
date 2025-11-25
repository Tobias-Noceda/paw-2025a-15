<script lang="ts">
  import { cn } from '$lib/utils';
  import Icon from '../Icon/Icon.svelte';

  interface Props {
    label?: string;
    placeholder?: string;
    type?: 'text' | 'password' | 'number';
    value?: string;
    min?: number;
    errorMessage?: string;
    disabled?: boolean;
    skeleton?: boolean;
    required?: boolean;
    class?: string;
    oninput?: (event: Event & { currentTarget: HTMLInputElement }) => void;
  }

  let showPassword = $state(false);

  let {
    label,
    placeholder,
    type = 'text',
    value = $bindable(''),
    min,
    errorMessage,
    disabled = false,
    skeleton = false,
    required = false,
    class: inputClass,
    oninput
  }: Props = $props();

  const finalClass = cn(
    'w-full transition-colors',
    inputClass,
    'px-3 py-2.5 rounded-md border bg-transparent',
    'disabled:opacity-50 disabled:cursor-not-allowed',
    errorMessage ? 'border-red-500' : 'border-gray-300',
    'focus:border-primary',
    skeleton ? 'bg-skeleton animate-pulse text-transparent cursor-default' : '',
    type === 'number' ? '[appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none' : ''
  );

  const errorClass = cn(
    'flex p-3 rounded-md bg-error-bg mt-1',
    'text-error-text text-sm',
  );
</script>

<div class="flex flex-col gap-1">
  {#if label}
    <label class="text-sm font-medium text-text" for={label}>
      {label}
      {#if required}
        <span class="text-red-500">*</span>
      {/if}
    </label>
  {/if}
  <div class="relative">
    <input
      type={type === 'password' ? (showPassword ? 'text' : 'password') : type}
      class={finalClass}
      {placeholder}
      bind:value
      {disabled}
      {required}
      {min}
      {oninput}
    />
    {#if type === 'password'}
      <button 
        type="button" 
        class="absolute right-3 top-1/2 -translate-y-1/2 hover:opacity-70 transition-opacity" 
        onclick={() => showPassword = !showPassword}
        tabindex="-1"
      >
        <Icon name={showPassword ? 'eye-blind' : 'eye'} class="w-5 h-5 text-gray-500" />
      </button>
    {/if}
  </div>
  {#if errorMessage}
    <p class={errorClass}>{errorMessage}</p>
  {/if}
</div>