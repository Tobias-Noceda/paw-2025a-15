<script lang="ts">
	import { setLocale } from '$lib/paraglide/runtime';
	import { m } from '$lib/paraglide/messages.js';
	import Icon from '$components/Icon/Icon.svelte';
</script>



<h1>{m.hello_world({ name: 'SvelteKit User' })}</h1>
<div>
	<button onclick={() => setLocale('es')}>es</button>
	<button onclick={() => setLocale('en')}>en</button>
</div><p>
If you use VSCode, install the <a href="https://marketplace.visualstudio.com/items?itemName=inlang.vs-code-extension" target="_blank">Sherlock i18n extension</a> for a better i18n experience.
</p>
<Icon name="search" class="w-6 h-6 text-blue-500" />
