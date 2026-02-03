<script lang="ts">
	import { onMount } from 'svelte';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import Button from '$components/Button/Button.svelte';
	import Chip from '$components/Chip/Chip.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Icon from '$components/Icon/Icon.svelte';
	import Input from '$components/Input/Input.svelte';
	import Select from '$components/Select/Select.svelte';
	import Switch from '$components/Switch/Switch.svelte';
	import { m } from '$lib/paraglide/messages';
	import { user } from '$lib/stores/user';
	import { baseApiUrl, type Profile } from '$types/api';
	import { getSpecialtyLabel, Specialties } from '$types/enums/specialties';

	const languageOptions = [
		{ value: 'ES_AR', label: m['locale.ES_AR']() },
		{ value: 'ES_US', label: m['locale.ES_US']() },
		{ value: 'EN_AR', label: m['locale.EN_AR']() },
		{ value: 'EN_US', label: m['locale.EN_US']() }
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

	let fullName = $state('');
	let email = $state('');
	let role = $state('');
	let avatarSrc = $state('');

	let telephone = $state('');
	let mailLanguage = $state('EN_US');
	let birthDate: Date | null = $state(null);
	let bloodType = $state('');
	let height = $state('');
	let weight = $state('');
	let insurance = $state('');
	let insuranceNumber = $state('');

	let smokes = $state('');
	let drinks = $state('');
	let diet = $state('');

	let meds = $state('');
	let conditions = $state('');
	let allergies = $state('');

	let hobbies = $state('');
	let job = $state('');
	let licence = $state('');
	let specialty = $state('');
	let doctorInsurances = $state<string[]>([]);
	let address = $state('');
	let updateSchedule = $state(false);

	const message = (key: string) => {
		const fn = (m as Record<string, (() => string) | undefined>)[key];
		return typeof fn === 'function' ? fn() : '';
	};

	const roleLabel = $derived(role ? message(`role.${role}`) : '');

	const toYesNo = (value?: boolean | null) => {
		if (value === true) return 'yes';
		if (value === false) return 'no';
		return '';
	};

	onMount(async () => {
		if ($user !== 'patient' && $user !== 'doctor') {
			return;
		}

		try {
			const response = await fetch(`${baseApiUrl}/profile`);
			if (!response.ok) return;
			const data: Profile = await response.json();

			fullName = data.name ?? '';
			email = data.email ?? '';
			role = data.role ?? '';
			avatarSrc = data.links?.image ?? '';
			telephone = data.telephone ?? '';
			mailLanguage = data.mailLanguage ?? 'EN_US';
			birthDate = data.birthdate ? new Date(data.birthdate) : null;
			bloodType = data.bloodtype ?? '';
			height = data.height != null ? String(data.height) : '';
			weight = data.weight != null ? String(data.weight) : '';
			smokes = toYesNo(data.smokes);
			drinks = toYesNo(data.drinks);
			meds = data.meds ?? '';
			conditions = data.conditions ?? '';
			allergies = data.allergies ?? '';
			diet = data.diet ?? '';
			hobbies = data.hobbies ?? '';
			job = data.job ?? '';
			insurance = data.insuranceName ?? '';
			insuranceNumber = data.insuranceNumber ?? '';
			licence = data.licence ?? '';
			specialty = data.specialty ?? '';
			doctorInsurances = data.insurances ?? [];
			address = data.address ?? '';
		} catch (error) {
			console.error('Failed to fetch profile:', error);
		}
	});
</script>

{#if $user === 'patient'}
<div class="profile-page">
	<div class="card bg-white profile-summary">
		<div class="profile-avatar">
			<Avatar size="xl" src={avatarSrc} class="bg-primary" />
			<button class="edit-avatar-btn" type="button" aria-label={m['admin.insurances.button.edit']()}>
				<Icon name="edit" class="w-3.5 h-3.5 text-white" />
			</button>
		</div>
		<div class="profile-info">
			<h1 class="text-[1.4rem] font-semibold text-primaryText">{fullName}</h1>
			<p class="text-secondaryText">{email}</p>
			<p class="text-secondaryText">
				<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span> {roleLabel}
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
{:else if $user === 'doctor'}
<div class="profile-page">
	<div class="card bg-white profile-summary">
		<div class="profile-avatar">
			<Avatar size="xl" src={avatarSrc} class="bg-primary" />
			<button class="edit-avatar-btn" type="button" aria-label={m['admin.insurances.button.edit']()}>
				<Icon name="edit" class="w-3.5 h-3.5 text-white" />
			</button>
		</div>
		<div class="profile-info">
			<h1 class="text-[1.4rem] font-semibold text-primaryText">{fullName}</h1>
			<p class="text-secondaryText">{email}</p>
			<p class="text-secondaryText">
				<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span> {roleLabel}
			</p>
			<p class="text-secondaryText">
				<span class="font-semibold text-primaryText">{m['doctor.labels.license']()}:</span> {licence}
			</p>
			<p class="text-secondaryText">
				<span class="font-semibold text-primaryText">{m['doctor.details.specialty.label']()}</span>{' '}
				{getSpecialtyLabel(specialty as Specialties) ?? specialty}
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
			<div class="col-span-3">
				<label class="text-sm font-medium text-text">{m['doctor.profile.insurance.label']()}</label>
				<div class="flex flex-wrap gap-2 mt-2">
					{#each doctorInsurances as insuranceName, idx}
						<Chip label={insuranceName} variant={idx === 1 ? 'primary' : 'tertiary'} />
					{/each}
				</div>
			</div>
			<Input label={m['doctor.labels.address']()} bind:value={address} class="col-span-3" />
			<div class="col-span-3 flex items-center gap-4">
				<span class="text-primaryText font-semibold">{m['doctor.profile.updateSchedule']()}</span>
				<div class="w-14">
					<Switch bind:checked={updateSchedule} />
				</div>
			</div>
		</div>
		<div class="flex justify-center mt-6">
			<Button variant="primary" class="w-fit" onclick={() => {}}>
				{m['profile.save.button']()}
			</Button>
		</div>
	</div>
</div>
{:else}
<div class="card bg-white profile-section text-center">
	<h2 class="section-title">{m['profile.title']()}</h2>
	<p class="text-secondaryText mt-4">{m['no_data_available']()}</p>
</div>
{/if}

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
