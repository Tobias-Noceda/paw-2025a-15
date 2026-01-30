<script lang="ts">
    import './layout.css';
    import { onMount } from 'svelte';
    import { setLocale, type Locale } from '$lib/paraglide/runtime';

    let { children } = $props();

    onMount(() => {
        // Detect browser language on client-side
        const browserLang = navigator.language.split('-')[0];
        
        // Only set if it's a supported language
        setLocale(browserLang as Locale || 'es');
    });
</script>

<div class="flex flex-col max-h-screen! min-h-screen! min-w-screen! max-w-screen! select-none!">
    {@render children()}
</div>