<script lang="ts">
  import { cn } from "$lib/utils";

  interface Props {
    checked?: boolean;
    disabled?: boolean;
    skeleton?: boolean;
    onchange?: (checked: boolean) => void;
    class?: string;
  }

  let {
    checked = $bindable(false),
    disabled = false,
    skeleton = false,
    onchange = () => {},
    class: switchClass
  }: Props = $props();

  function toggle() {
    if (disabled || skeleton) return;
    checked = !checked;
    onchange?.(checked);
  }
    
  const rootClasses = cn(
    "flex w-full h-fit bg-primary",
    "items-center rounded-full transition-colors cursor-pointer p-1",
    disabled ? "opacity-50 cursor-not-allowed" : "",
    skeleton ? "bg-skeleton animate-pulse cursor-default" : "",
  );

  const knobClasses = $derived(
    cn(
      "w-4 h-4 rounded-full shadow transform transition-transform z-10",
      skeleton ? "bg-skeleton animate-pulse" : ""
    )
  );
</script>

<div class={switchClass}>
  <div
    class={rootClasses}
    style={"justify-content: " + (checked ? "flex-end" : "flex-start") + "; background-color: " + (checked ? "var(--color-primary)" : "var(--color-primaryBorder)") + " !important;"}
    onclick={toggle}
    role="switch"
    aria-checked={checked}
    tabindex={disabled ? undefined : 0}
    onkeydown={(e) => e.key === " " || e.key === "Enter" ? toggle() : null}
  >
    <div class={knobClasses} style="background-color: white !important;"></div>
  </div>
</div>
