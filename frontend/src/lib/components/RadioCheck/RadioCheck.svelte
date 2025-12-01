<script lang="ts">
  import Button from "$components/Button/Button.svelte";

  interface RadioCheckOption {
    id: string;
    label: string;
    checked: boolean;
  }

  interface RadioCheckProps {
    options: RadioCheckOption[];
    disabled?: boolean;
    skeleton?: boolean;
    containerClass?: string;
    onchange?: (event: { id: string; checked: boolean }) => void;
  }

  // Read props
  let {
    options = $bindable([]),
    disabled = false,
    skeleton = false,
    containerClass = "",
    onchange
  }: RadioCheckProps = $props();

  function emitChange(id: string, checked: boolean) {
    options = options.map(option =>
      option.id === id ? { ...option, checked } : option
    );
    onchange?.({ id, checked });
  }
</script>

<div class={"flex gap-2 " + containerClass}>
  {#each options as option}
    <Button
      variant={option.checked ? "primary" : "tertiary"}
      class="flex items-center focus:outline-none rounded-full"
      skeleton={skeleton}
      disabled={disabled}
      onclick={() => {
        if (!disabled) {
          emitChange(option.id, !option.checked);
        }
      }}
    >
      {option.label}
    </Button>
  {/each}
</div>