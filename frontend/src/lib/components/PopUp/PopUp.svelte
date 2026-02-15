<script lang="ts">
	import { cn } from "$lib/utils";
	import type { Snippet } from "svelte";

    interface Props {
        /**
         * Function to be called when the popup is requested to close
        */
        onClose?: () => void;
        /**
         * The content to be displayed inside the popup
        */
        children: Snippet;
        class?: string;
    }

    let { onClose = undefined, children, class: customClass }: Props = $props();
</script>

<div class={cn("justify-center items-center flex fixed inset-0 w-full bg-black/50", customClass, "z-1001!")}>
    <div class="flex flex-col bg-white rounded-lg p-4 max-w-2xl max-h-lg w-full h-fit mx-4 shadow-lg overflow-y-auto">
        {#if onClose}
            <div class="flex justify-end text-xl mb-2">
                <button class="rounded-full w-8 h-8 text-gray-500 hover:text-gray-700 hover:bg-gray-100 cursor-pointer!" onclick={onClose}>
                    &times;
                </button>
            </div>
        {/if}
        <div>
            {@render children()}
        </div>
    </div>
</div>