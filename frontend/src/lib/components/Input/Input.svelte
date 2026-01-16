<script lang="ts">
  import { cn } from '$lib/utils';
  import Icon from '$components/Icon/Icon.svelte';
  import { m } from '$lib/paraglide/messages.js';

  interface Props {
    id?: string;
    label?: string;
    placeholder?: string;
    type?: 'text' | 'password' | 'number';
    value?: string;
    min?: number;
    errorMessage?: string;
    disabled?: boolean;
    skeleton?: boolean;
    required?: boolean;
    multiline?: boolean;
    class?: string;
    oninput?: (event: Event & { currentTarget: HTMLInputElement | HTMLTextAreaElement }) => void;
    onsubmit?: () => void;
  }

  let showPassword = $state(false);

  let {
    id,
    label,
    placeholder,
    type = 'text',
    value = $bindable(''),
    min,
    errorMessage,
    disabled = false,
    skeleton = false,
    required = false,
    multiline = false,
    class: inputClass,
    oninput,
    onsubmit
  }: Props = $props();

  const finalClass = cn(
    'w-full transition-colors',
    inputClass,
    'px-3 py-2.5 rounded-md border bg-bgColor text-primaryText',
    'disabled:opacity-50 disabled:cursor-not-allowed',
    errorMessage ? 'border-red-500' : 'border-gray-300',
    'focus:border-primary',
    skeleton ? 'bg-skeleton animate-pulse text-transparent cursor-default' : '',
    multiline ? 'resize-none overflow-hidden' : '',
    type === 'number' ? '[appearance:textfield] [&::-webkit-outer-spin-button]:appearance-none [&::-webkit-inner-spin-button]:appearance-none' : ''
  );

  let textareaElement: HTMLTextAreaElement | null = $state(null);

  function autoResize(element: HTMLTextAreaElement) {
    element.style.height = 'auto';
    element.style.height = element.scrollHeight + 'px';
  }

  $effect(() => {
    if (multiline && textareaElement && value !== undefined) {
      autoResize(textareaElement);
    }
  });

  const errorClass = cn(
    'flex p-3 rounded-md bg-error-bg mt-1',
    'text-error-text text-sm',
  );
</script>

<div class="flex flex-col gap-1 w-full">
  {#if label}
    <label class="text-sm font-medium text-text" for={id}>
      {label}
      {#if required}
        <span class="text-red-500">*</span>
      {/if}
    </label>
  {/if}
  <div class="relative">
    {#if multiline}
      <textarea
        {id}
        bind:this={textareaElement}
        class={finalClass}
        placeholder={skeleton ? m.input_loading() : placeholder}
        bind:value
        disabled={disabled || skeleton}
        {required}
        oninput={(e) => {
          autoResize(e.currentTarget);
          oninput?.(e);
        }}
        onkeydown={(event) => {
          if (event.key === 'Enter') {
            onsubmit?.();
          }
        }}
      ></textarea>
    {:else}
      <input
        {id}
        type={type === 'password' ? (showPassword ? 'text' : 'password') : type}
        class={finalClass}
        placeholder={skeleton ? m.input_loading() : placeholder}
        bind:value
        disabled={disabled || skeleton}
        {required}
        {min}
        {oninput}
        onkeydown={(event) => {
          if (event.key === 'Enter') {
            onsubmit?.();
          }
        }}
      />
    {/if}
    {#if type === 'password' && !multiline}
      <button 
        type="button" 
        class="absolute right-3 top-1/2 -translate-y-1/2 hover:opacity-70 transition-opacity flex justify-center items-center w-5 h-5 cursor-pointer" 
        onclick={() => showPassword = !showPassword}
        tabindex="-1"
      >
        <Icon name={showPassword ? 'eye-blind' : 'eye'} class="text-gray-500" />
      </button>
    {/if}
  </div>
  {#if errorMessage}
    <p class={errorClass}>{errorMessage}</p>
  {/if}
</div>