<script lang="ts">
	import type { PageData } from './$types';
	import { m } from '$lib/paraglide/messages';
	import Button from '$components/Button/Button.svelte';
	import Input from '$components/Input/Input.svelte';
	import Toast from '$components/Toast/Toast.svelte';
	import { goto } from '$app/navigation';
	import { base } from '$app/paths';
	import type { Insurance } from '$types/api';
	import { editInsurance } from '$lib/services/insurances';

	let { data }: { data: PageData } = $props();

	let insurance: Insurance = $state(data.insurance);
	let insuranceName = $state(insurance ? insurance.name : '');

	let newImage = $state<File | undefined>(undefined);

	let isSubmitting = $state(false);

	let showErrorToast = $state(false);
	let showSuccessToast = $state(false);

	const handleSubmit = async () => {
		if (!insurance) return;

		isSubmitting = true;

		editInsurance(insurance.links.self.resolved!, insuranceName, newImage, fetch)
			.then(() => {
				showSuccessToast = true;
				setTimeout(() => {
					goto(`${base}/home`);
				}, 1500);
			})
			.catch((error) => {
				console.error('Error updating insurance:', error);
				showErrorToast = true;
			})
			.finally(() => {
				isSubmitting = false;
			});
	};
</script>

<div class="flex justify-center">
	<div
		class="page-container-division bg-transparent! h-fit max-h-full! flex flex-col w-200 gap-2.5 select-none"
	>
		<div class="page-card flex flex-col gap-5 w-full h-fit max-h-full! overflow-y-auto">
			<h1 class="w-full text-center text-[24px] font-bold mb-2.5">
				{m['insurance.title']()}
			</h1>
			<div class="flex flex-col gap-5">
				<div class="flex flex-col gap-1">
					<p class="text-line text-primaryText font-bold select-none">
						{m['insurance.labels.name']()}:
					</p>
					<Input
						bind:value={insuranceName}
						placeholder={m['insurance.placeholder.name']()}
						class="w-full"
					/>
				</div>

				<div class="flex flex-col w-full items-start justify-center gap-2 rounded-lg border border-primaryBorder p-4 bg-gray-100">
					<p class="w-full text-start text-line text-primaryText font-bold select-none">
						{m['insurance.labels.logo']()}:
					</p>
					{#if newImage}
						<div class="flex w-full h-fit items-center justify-center">
							<img src={URL.createObjectURL(newImage)} alt="Logo" class="h-70 object-contain rounded-lg" />
						</div>
					{:else}
						{m['insurance.no_logo']()}
					{/if}
					<input
						id="logo-upload"
						type="file"
						accept="image/png,image/jpeg"
						onchange={(e) => {
							const file = (e.target as HTMLInputElement).files?.[0] || undefined;
							newImage = file;
						}}
						class="hidden"
					/>
					<label
						for="logo-upload"
						class="flex items-center justify-center font-semibold rounded-md px-4 py-2 cursor-pointer transition-colors text-base bg-primary text-white hover:bg-primary-hover select-none"
					>
						{m['insurance.buttons.choose_file']()}
					</label>
				</div>
				<p class="text-sm text-secondaryText -mt-4">
					{m['insurance.accepted_files']()}
				</p>
				
				<div class="flex flex-col w-full items-start justify-center gap-2 rounded-lg border border-primaryBorder p-4 bg-gray-100">
					<div class="flex flex-row w-full h-fit justify-between">
						<p class="w-full text-start text-line text-primaryText font-bold select-none">
							{m['insurance.labels.current_logo']()}:
						</p>
						<p class="text-sm text-secondaryText text-nowrap">
							{m['insurance.can_update']()}
						</p>
					</div>
					<div class="flex w-full h-fit items-center justify-center">
						<img src={insurance.links.image.resolved! || ''} alt="Logo" class="h-70 object-contain rounded-lg" />
					</div>
				</div>
				
				<div class="flex flex-row w-full gap-2">
					<Button
						variant="primary"
						class="w-full mt-5"
						onclick={handleSubmit}
						disabled={isSubmitting}
					>
						{m['insurance.buttons.submit']()}
					</Button>
					<Button
						variant="gray"
						class="w-full mt-5"
						onclick={() => goto(`${base}/home`)}
						disabled={isSubmitting}
					>
						{m['insurance.buttons.cancel']()}
					</Button>
				</div>
			</div>
		</div>
	</div>

	<Toast
		variant="success"
		title={m['insurance.toast.success.title']()}
		description={m['insurance.toast.success.message']()}
		show={showSuccessToast}
	/>

	<Toast
		variant="destructive"
		title={m['insurance.toast.error.title']()}
		description={m['insurance.toast.error.message']()}
		show={showErrorToast}
	/>
</div>

<style>
	.page-container-division {
		display: flex;
		top: 20px;
		color: var(--primaryText, #000);
		height: calc(100vh - 110px);
	}

	.page-card {
		display: flex;
		padding: 20px;
		background-color: var(--bgColor, #fff);
		border-radius: 10px;
		box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
		color: var(--primaryText, #000);
	}
</style>
