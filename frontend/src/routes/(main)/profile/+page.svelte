<script lang="ts">
	import Avatar from '$components/Avatar/Avatar.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Icon from '$components/Icon/Icon.svelte';
	import Input from '$components/Input/Input.svelte';
	import Select from '$components/Select/Select.svelte';
	import { m } from '$lib/paraglide/messages';

	const languageOptions = [
		{ value: 'es-AR', label: m['locale.ES_AR']() },
		{ value: 'en-US', label: m['locale.EN_US']() }
	];

	const bloodTypeOptions = [
		{ value: 'A+', label: 'A+' },
		{ value: 'A-', label: 'A-' },
		{ value: 'B+', label: 'B+' },
		{ value: 'B-', label: 'B-' },
		{ value: 'AB+', label: 'AB+' },
		{ value: 'AB-', label: 'AB-' },
		{ value: 'O+', label: 'O+' },
		{ value: 'O-', label: 'O-' }
	];

	const yesNoOptions = [
		{ value: 'yes', label: m['profileInfo.yes']() },
		{ value: 'no', label: m['profileInfo.no']() }
	];

	const insuranceOptions = [
		{ value: 'OSDE', label: 'OSDE' },
		{ value: 'Swiss Medical', label: 'Swiss Medical' },
		{ value: 'Galeno', label: 'Galeno' }
	];

	let fullName = $state('Manuel Santamarina');
	let email = $state('manuelsantamarina03@gmail.com');
	let role = $state('PATIENT');

	let telephone = $state('01157407765');
	let mailLanguage = $state('en-US');
	let birthDate: Date | null = $state(new Date('2003-07-27'));
	let bloodType = $state('AB-');
	let height = $state('1.75');
	let weight = $state('79.12');
	let insurance = $state('OSDE');
	let insuranceNumber = $state('311133341');

	let smokes = $state('yes');
	let drinks = $state('no');
	let diet = $state('como sano');

	let meds = $state('paracetamol todos los dias');
	let conditions = $state('');
	let allergies = $state('');

	let hobbies = $state('juego al futbol');
	let job = $state('ingeniero informatico (ojala)');
</script>

<div class="profile-page">
	<div class="card bg-white profile-summary">
		<div class="profile-avatar">
			<Avatar size="xl" class="bg-primary" />
			<button class="edit-avatar-btn" type="button" aria-label={m['admin.insurances.button.edit']()}>
				<Icon name="edit" class="w-3.5 h-3.5 text-white" />
			</button>
		</div>
		<div class="profile-info">
			<h1 class="text-[1.4rem] font-semibold text-primaryText">{fullName}</h1>
			<p class="text-secondaryText">{email}</p>
			<p class="text-secondaryText">
				<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span> {m[`role.${role}`]()}
			</p>
		</div>
	</div>

	<div class="card bg-white profile-section">
		<h2 class="section-title">{m['profileInfo.basic']()}</h2>
		<div class="grid grid-cols-3 gap-5 mt-6">
			<Input label={m['profile.phone.label']()} bind:value={telephone} class="col-span-3" />
			<Select
				label={m['profile.email.lang.label']()}
				options={languageOptions}
				bind:value={mailLanguage}
				placeholder={m['profileInfo.select']()}
				class="col-span-3"
			/>
			<DatePicker label={m['form.birthDate']()} bind:selectedDate={birthDate} class="col-span-1" />
			<Select
				label={m['profileInfo.bloodType']()}
				options={bloodTypeOptions}
				bind:value={bloodType}
				placeholder={m['profileInfo.select']()}
				class="col-span-1"
			/>
			<Input label={m['profileInfo.height']()} type="number" bind:value={height} class="col-span-1" />
			<Input label={m['profileInfo.weight']()} type="number" bind:value={weight} class="col-span-1" />
			<Select
				label={m['profile.insurance.label']()}
				options={insuranceOptions}
				bind:value={insurance}
				placeholder={m['profile.insurance.placeholder']()}
				class="col-span-1"
			/>
			<Input
				label={m['profile.insuranceNumber.label']()}
				placeholder={m['profile.insuranceNumber.placeholder']()}
				bind:value={insuranceNumber}
				class="col-span-2"
			/>
		</div>
	</div>

	<div class="card bg-white profile-section">
		<h2 class="section-title">{m['profileInfo.habits']()}</h2>
		<div class="grid grid-cols-2 gap-5 mt-6">
			<Select label={m['profileInfo.smokes']()} options={yesNoOptions} bind:value={smokes} class="col-span-1" />
			<Select label={m['profileInfo.drinks']()} options={yesNoOptions} bind:value={drinks} class="col-span-1" />
			<Input label={m['profileInfo.diet']()} bind:value={diet} class="col-span-2" />
		</div>
	</div>

	<div class="card bg-white profile-section">
		<h2 class="section-title">{m['profileInfo.medical']()}</h2>
		<div class="flex flex-col gap-5 mt-6">
			<Input label={m['profileInfo.meds']()} bind:value={meds} multiline />
			<Input label={m['profileInfo.conditions']()} bind:value={conditions} multiline />
			<Input label={m['profileInfo.allergies']()} bind:value={allergies} multiline />
		</div>
	</div>

	<div class="card bg-white profile-section">
		<h2 class="section-title">{m['profileInfo.social']()}</h2>
		<div class="flex flex-col gap-5 mt-6">
			<Input label={m['profileInfo.hobbies']()} bind:value={hobbies} />
			<Input label={m['profileInfo.job']()} bind:value={job} />
		</div>
		<div class="flex justify-center mt-6">
			<Button variant="primary" class="w-fit" onclick={() => {}}>
				{m['profile.save.button']()}
			</Button>
		</div>
	</div>
</div>

<style>
	.profile-page {
		display: flex;
		flex-direction: column;
		gap: 20px;
		align-items: center;
		width: 100%;
	}

	.profile-summary {
		display: flex;
		gap: 24px;
		align-items: center;
		width: min(1100px, 100%);
	}

	.profile-section {
		width: min(1100px, 100%);
	}

	.profile-avatar {
		position: relative;
		width: 96px;
		height: 96px;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	.edit-avatar-btn {
		position: absolute;
		right: -4px;
		bottom: -4px;
		width: 28px;
		height: 28px;
		border-radius: 999px;
		background: var(--color-primary);
		display: flex;
		align-items: center;
		justify-content: center;
		border: 2px solid #fff;
		box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
	}

	.profile-info {
		display: flex;
		flex-direction: column;
		gap: 4px;
	}
</style>
