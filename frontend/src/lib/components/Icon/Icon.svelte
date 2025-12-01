<script lang="ts">
  import { cn } from '$lib/utils';
  import { iconMap } from './icon-map';

  export type IconNames = keyof typeof iconMap;

  interface Props {
    name: IconNames;
    class?: string;
    strokeWidth?: number;
  }

  let { name, class: className = '', strokeWidth = 1.7 }: Props = $props();

  let svgContent = $state<string | null>(null);

  $effect(() => {
    const src = iconMap[name];
    
    if (typeof src !== 'string') {
      svgContent = null;
      return;
    }
    
    fetch(src)
      .then((res) => res.text())
      .then((raw) => {
        // 1. Remove width/height
        let updated = raw.replace(/(width|height)="[^"]*"/g, '');

        // 2. stroke="currentColor" to all elements
        updated = updated.replace(/stroke="[^"]*"/g, 'stroke="currentColor"');

        // 3. Replace ALL stroke-width occurrences
        updated = updated.replace(/stroke-width="[^"]*"/g, `stroke-width="${strokeWidth}"`);

        // 4. Make SVG fully responsive with proper scaling
        updated = updated.replace(
          /<svg([^>]*)>/,
          `<svg$1 width="100%" height="100%" style="display: block; max-width: 100%; max-height: 100%;">`
        );

        svgContent = updated;
      });
  });

  const finalClass = $derived(cn(
    'inline-block align-middle overflow-hidden',
    /\b(w-|h-)/.test(className) ? '' : 'w-4 h-4',
    className,
    "select-none"
  ));
</script>

{#if svgContent}
  <div class={finalClass} style="line-height: 0;">
    {@html svgContent}
  </div>
{/if}