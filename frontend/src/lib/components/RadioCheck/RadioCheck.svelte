<script lang="ts">
	import { cn } from "$lib/utils";

  interface RadioCheckOption {
    id: string;
    label: string;
    checked: boolean;
  }

  interface RadioCheckProps {
    label?: string;
    required?: boolean;
    options: RadioCheckOption[];
    disabled?: boolean;
    skeleton?: boolean;
    class?: string;
    optionsClass?: string;
    onchange?: (event: { id: string; checked: boolean }) => void;
  }

  // Read props
  let {
    label,
    required = false,
    options = $bindable([]),
    disabled = false,
    class: radioClass = '',
    optionsClass,
    onchange
  }: RadioCheckProps = $props();

  function emitChange(id: string, checked: boolean) {
    options = options.map(option =>
      option.id === id ? { ...option, checked } : option
    );
    onchange?.({ id, checked });
  }

  const optionClass = (checked: boolean) =>
    cn(
      'flex items-center justify-center rounded-3xl p-2.5 border-1 border-primaryBorder',
      optionsClass,
      checked
        ? 'bg-primary border-primary text-white hover:bg-primary-hover'
        : 'bg-bgColor border-primaryBorder text-primaryText hover:bg-bgColorHover',
      disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer',
    );
</script>

<div class="w-full">
  {#if label}
    <label class="text-sm font-medium text-text" for="select">
      {label}
      {#if required}
        <span class="text-red-500">*</span>
      {/if}
    </label>
  {/if}
  <div class={"flex gap-2 " + radioClass + (label ? ' mt-1' : '')}>
    {#each options as option}
      <button
        class={optionClass(option.checked)}
        onclick={() => {
          if (!disabled) {
            emitChange(option.id, !option.checked);
          }
        }}
      >
        {option.label}
      </button>
    {/each}
  </div>
</div>