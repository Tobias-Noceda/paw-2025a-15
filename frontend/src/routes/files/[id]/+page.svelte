<script lang="ts">
    import '../../layout.css';

	import { onDestroy } from 'svelte';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

    console.log('File data:', data.file);
	let contentType = $state<string>(data.file.type);
    console.log('File content type:', contentType);

	const isImage = $derived(contentType.startsWith('image/'));
	const isPdf = $derived(contentType === 'application/pdf');

    onDestroy(() => {
        URL.revokeObjectURL(data.fileLink);
    });
</script>

<div class="flex flex-col items-center justify-center w-full min-w-screen h-full min-h-screen p-4">
    {#if isImage}
        <div class="max-w-full max-h-full overflow-auto bg-white rounded-lg shadow-lg p-4">
            <img 
                src={data.fileLink} 
                alt="File {data.fileLink}" 
                class="max-w-full h-auto"
            />
        </div>
    {:else if isPdf}
        <div class="w-full h-screen">
            <embed 
                src={data.fileLink} 
                type="application/pdf" 
                class="w-full h-full rounded-lg shadow-lg"
            />
        </div>
    {/if}
</div>
