<script lang="ts">
	import { base } from "$app/paths";
	import { m } from "$lib/paraglide/messages";
    import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<div class="flex flex-col items-center w-[420px]">
    {#if data.verifiedEmail}
        <h1 class="text-[1.75rem] font-semibold mb-1.5">{m["login.verify.success.title"]()}</h1>
        <p class="text-mutedText mb-7 font-[0.95rem]">{m["login.verify.success.message"]({ email: data.verifiedEmail })}</p>
    {:else}
        <h1 class="text-[1.75rem] font-semibold mb-1.5">{m["login.verify.error.title"]()}</h1>
        <p class="text-mutedText mb-7 font-[0.95rem]">{m["login.verify.error.message"]()}</p>
    {/if}

    <div class="flex flex-col items-center text-start gap-6 w-full">
        <div class="flex flex-col w-full justify-center items-center">
            <a href="{base}/login" class="font-semibold text-primary hover:underline cursor-pointer">
                {m["login.change.back_to_login"]()}
            </a>
        </div>
    </div>
</div>