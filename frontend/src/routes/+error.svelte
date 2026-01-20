<script lang="ts">
	import { base } from '$app/paths';
	import { page } from '$app/state';
	import Button from '$components/Button/Button.svelte';
	import * as m from '$lib/paraglide/messages';
</script>

<div class="flex flex-col items-center justify-center w-screen! h-screen! py-6 bg-[#a7dbfe]">
	<div class="flex flex-col items-center gap-4">
		<img src="{base}/cached/resources/errorImage.png" alt="Error" class="max-w-62.5 mb-5" />
		<h1 class="text-5xl text-[#e74c3c] font-bold">Error {page.status}</h1>
		<p class="text-lg text-[#333] mt-2.5 mb-7.5">{m[`error.${page.status}_message`]()}</p>
		<Button
			variant="primary"
			onclick={() => { window.location.href = `${base}/home` }}
		>
			{m['error.back_home']()}
		</Button>

		<span class="text-sm font-extralight text-transparent hover:text-gray-600">
			{page.status}: {page.error?.message}
		</span>
	</div>
</div>
