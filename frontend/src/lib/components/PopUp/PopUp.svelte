<script lang="ts">
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
    }

    let { onClose = undefined, children }: Props = $props();
</script>

<div class="justify-center items-center flex fixed inset-0 w-full z-1001" style="background-color: rgba(0, 0, 0, 0.5);">
    <div class="flex flex-col bg-white rounded-lg p-4 max-w-2xl max-h-lg w-full h-fit mx-4 shadow-lg overflow-y-auto">
        {#if onClose}
            <div class="flex justify-end text-xl mb-2">
                <button class="rounded-full w-8 h-8 text-gray-500 hover:text-gray-700 hover:bg-gray-100" onclick={onClose}>
                    &times;
                </button>
            </div>
        {/if}
        <div>
            {@render children()}
        </div>
    </div>
</div>