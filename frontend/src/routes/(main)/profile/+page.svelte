<script lang="ts">
	import { onMount } from 'svelte';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import Button from '$components/Button/Button.svelte';
	import DatePicker from '$components/DatePicker/DatePicker.svelte';
	import Icon from '$components/Icon/Icon.svelte';
	import Input from '$components/Input/Input.svelte';
	import Select from '$components/Select/Select.svelte';
	import Switch from '$components/Switch/Switch.svelte';
	import Toast from '$components/Toast/Toast.svelte';
	import { m } from '$lib/paraglide/messages';
	import { user } from '$lib/stores/user';
	import { fetchDoctorById, updateDoctorProfile, type DoctorProfileUpdateData } from '$lib/services/doctors';
	import { fetchInsurances, fetchInsurancesPage } from '$lib/services/insurances';
	import { fetchPatientById, updatePatientProfile } from '$lib/services/patients';
	import { Durations, getWeekdayShortLabel, Weekdays } from '$types/enums/weekdays';
	import { getSpecialtyLabel, Specialty } from '$types/enums/specialties';

	const languageOptions = [
		{ value: 'ES_AR', label: m['locale.ES_AR']() },
		{ value: 'ES_US', label: m['locale.ES_US']() },
		{ value: 'EN_AR', label: m['locale.EN_AR']() },
		{ value: 'EN_US', label: m['locale.EN_US']() }
	];

	const bloodTypeOptions = [
		{ value: 'A_POSITIVE', label: 'A+' },
		{ value: 'A_NEGATIVE', label: 'A-' },
		{ value: 'B_POSITIVE', label: 'B+' },
		{ value: 'B_NEGATIVE', label: 'B-' },
		{ value: 'AB_POSITIVE', label: 'AB+' },
		{ value: 'AB_NEGATIVE', label: 'AB-' },
		{ value: 'O_POSITIVE', label: 'O+' },
		{ value: 'O_NEGATIVE', label: 'O-' }
	];

	const yesNoOptions = [
		{ value: 'yes', label: m['profileInfo.yes']() },
		{ value: 'no', label: m['profileInfo.no']() }
	];

	const timeOptions = Array.from({ length: 33 }, (_, index) => {
		const totalMinutes = 6 * 60 + index * 30;
		const hours = String(Math.floor(totalMinutes / 60)).padStart(2, '0');
		const minutes = String(totalMinutes % 60).padStart(2, '0');
		const time = `${hours}:${minutes}`;
		return { value: time, label: time };
	});

	const durationOptions = Object.values(Durations).map((duration) => ({
		value: duration,
		label: duration
	}));

	const weekdayOptions = Object.values(Weekdays).map((weekday) => ({
		value: weekday,
		label: getWeekdayShortLabel(weekday)
	}));

	let fullName = $state('');
	let email = $state('');
	let role = $state('');
	let avatarSrc = $state('');
	let telephone = $state('');
	let mailLanguage = $state('EN_US');

	let loading = $state(false);
	let loaded = $state(false);
	let loadedUserId = $state<number | null>(null);
	let saveError = $state('');
	let showSaveSuccessToast = $state(false);
	let insuranceOptions = $state<{ value: string; label: string }[]>([]);

	let birthDate: Date | null = $state(null);
	let bloodType = $state('');
	let height = $state<string | number>('');
	let weight = $state<string | number>('');
	let insuranceId = $state('');
	let insuranceNumber = $state('');
	let smokes = $state('');
	let drinks = $state('');
	let diet = $state('');
	let meds = $state('');
	let conditions = $state('');
	let allergies = $state('');
	let hobbies = $state('');
	let job = $state('');

	let license = $state('');
	let specialty = $state('');
	let doctorAddress = $state('');
	let selectedDoctorInsuranceIds = $state<string[]>([]);
	let updateSchedule = $state(false);
	let selectedWeekdays = $state<string[]>([]);
	let startTime = $state('06:00');
	let endTime = $state('07:30');
	let duration = $state('15');

	const roleLabel = $derived.by(() => {
		if (!role) return '';
		if (role === 'PATIENT') return m['role.PATIENT']();
		if (role === 'DOCTOR') return m['role.DOCTOR']();
		if (role === 'LABORATORY') return m['role.LABORATORY']();
		if (role === 'ADMIN') return m['role.ADMIN']();
		return role;
	});

	const specialtyLabel = $derived.by(() => {
		if (!specialty) return '';
		if (Object.values(Specialty).includes(specialty as Specialty)) {
			return getSpecialtyLabel(specialty as Specialty);
		}
		return specialty;
	});

	const toYesNo = (value?: boolean | null) => {
		if (value === true) return 'yes';
		if (value === false) return 'no';
		return '';
	};

	const bloodTypeFromLabel = (label?: string | null) => {
		const match = bloodTypeOptions.find((option) => option.label === label);
		return match ? match.value : '';
	};

	const parseIdFromUrl = (url?: string | null) => {
		if (!url) return '';
		const parts = url.split('/');
		return parts[parts.length - 1] ?? '';
	};

	const normalizeTimeValue = (value?: string | null) => {
		if (!value) return '';
		const [hours = '00', minutes = '00'] = value.split(':');
		return `${hours.padStart(2, '0')}:${minutes.padStart(2, '0')}`;
	};

	const parseNumber = (value: string | number) => {
		if (value === null || value === undefined) return null;
		if (typeof value === 'number') {
			return Number.isNaN(value) ? null : value;
		}
		if (value.trim() === '') return null;
		const parsed = Number(value);
		return Number.isNaN(parsed) ? null : parsed;
	};

	const parseBoolean = (value: string) => {
		if (value === 'yes') return true;
		if (value === 'no') return false;
		return null;
	};

	const getInsuranceOptions = async () => {
		let page = await fetchInsurances(undefined, fetch);
		let allInsurances = [...page.results];

		while (page._links.next) {
			page = await fetchInsurancesPage(page._links.next, fetch);
			allInsurances = [...allInsurances, ...page.results];
		}

		return allInsurances.map((insurance) => ({
			value: parseIdFromUrl(insurance.links.self),
			label: insurance.name
		}));
	};

	const loadPatientProfile = async () => {
		if (!$user) {
			return;
		}

		const [patient, allInsuranceOptions] = await Promise.all([
			fetchPatientById($user.id, $user),
			getInsuranceOptions()
		]);

		insuranceOptions = allInsuranceOptions;
		fullName = patient.name ?? '';
		email = patient.email ?? '';
		role = $user.role;
		avatarSrc = patient.links?.image ?? '';
		telephone = patient.telephone ?? '';
		mailLanguage = patient.mailLanguage ?? 'EN_US';
		birthDate = patient.birthdate ? new Date(patient.birthdate) : null;
		bloodType = bloodTypeFromLabel(patient.bloodType);
		height = patient.height != null ? String(patient.height) : '';
		weight = patient.weight != null ? String(patient.weight) : '';
		smokes = toYesNo(patient.smokes);
		drinks = toYesNo(patient.drinks);
		meds = patient.meds ?? '';
		conditions = patient.conditions ?? '';
		allergies = patient.allergies ?? '';
		diet = patient.diet ?? '';
		hobbies = patient.hobbies ?? '';
		job = patient.job ?? '';
		insuranceId = parseIdFromUrl(patient.links?.insurance);
		insuranceNumber = patient.insuranceNumber ?? '';
	};

	const loadDoctorProfile = async () => {
		if (!$user) {
			return;
		}

		const [doctor, allInsuranceOptions] = await Promise.all([
			fetchDoctorById(String($user.id), $user, fetch),
			getInsuranceOptions()
		]);

		if (!doctor) {
			throw new Error('Doctor profile not found');
		}

		insuranceOptions = allInsuranceOptions;
		fullName = doctor.name ?? '';
		email = doctor.email ?? '';
		role = $user.role;
		avatarSrc = doctor.links?.image ?? '';
		telephone = doctor.telephone ?? '';
		mailLanguage = doctor.mailLanguage ?? 'EN_US';
		license = doctor.license ?? '';
		specialty = doctor.specialty ?? '';
		doctorAddress = doctor.address ?? doctor.direction ?? '';
		selectedDoctorInsuranceIds = (doctor.insuranceIds ?? []).map((id) => String(id));
		selectedWeekdays = (doctor.weekdays ?? []).map((weekday) => String(weekday));
		startTime = normalizeTimeValue(doctor.startTime) || '06:00';
		endTime = normalizeTimeValue(doctor.endTime) || '07:30';
		duration = doctor.duration != null ? String(doctor.duration) : '15';
		updateSchedule = false;
	};

	const loadProfile = async () => {
		if (!$user || loading || (loaded && loadedUserId === $user.id)) {
			return;
		}

		loading = true;
		saveError = '';

		try {
			if ($user.role === 'PATIENT') {
				await loadPatientProfile();
			} else if ($user.role === 'DOCTOR') {
				await loadDoctorProfile();
			}

			loaded = true;
			loadedUserId = $user.id;
		} catch (error) {
			console.error('Failed to fetch profile:', error);
		} finally {
			loading = false;
		}
	};

	const savePatientProfile = async () => {
		if (!$user || $user.role !== 'PATIENT') {
			return;
		}

		const payload = {
			telephone,
			mailLanguage,
			birthdate: birthDate ? birthDate.toISOString().split('T')[0] : null,
			bloodType: bloodType || null,
			height: parseNumber(height),
			weight: parseNumber(weight),
			smokes: parseBoolean(smokes),
			drinks: parseBoolean(drinks),
			diet: diet || null,
			meds: meds || null,
			conditions: conditions || null,
			allergies: allergies || null,
			hobbies: hobbies || null,
			job: job || null,
			insuranceId: insuranceId ? Number(insuranceId) : null,
			insuranceNumber: insuranceNumber || null
		};

		const updated = await updatePatientProfile($user.id, payload);
		fullName = updated.name ?? fullName;
		email = updated.email ?? email;
		telephone = updated.telephone ?? telephone;
		mailLanguage = updated.mailLanguage ?? mailLanguage;
		birthDate = updated.birthdate ? new Date(updated.birthdate) : birthDate;
		bloodType = bloodTypeFromLabel(updated.bloodType);
		height = updated.height != null ? String(updated.height) : height;
		weight = updated.weight != null ? String(updated.weight) : weight;
		insuranceId = parseIdFromUrl(updated.links?.insurance);
		insuranceNumber = updated.insuranceNumber ?? insuranceNumber;
		smokes = toYesNo(updated.smokes);
		drinks = toYesNo(updated.drinks);
		diet = updated.diet ?? diet;
		meds = updated.meds ?? meds;
		conditions = updated.conditions ?? conditions;
		allergies = updated.allergies ?? allergies;
		hobbies = updated.hobbies ?? hobbies;
		job = updated.job ?? job;
	};

	const saveDoctorProfile = async () => {
		if (!$user || $user.role !== 'DOCTOR') {
			return;
		}

		const insuranceIds = selectedDoctorInsuranceIds
			.map((value) => Number(value))
			.filter((value) => !Number.isNaN(value));

		if (updateSchedule) {
			if (
				selectedWeekdays.length === 0 ||
				doctorAddress.trim() === '' ||
				startTime.trim() === '' ||
				endTime.trim() === '' ||
				duration.trim() === ''
			) {
				saveError = m['error.400_message']();
				return;
			}

			if (startTime >= endTime) {
				saveError = m['error.400_message']();
				return;
			}
		}

		const payload: DoctorProfileUpdateData = {
			telephone,
			mailLanguage,
			insuranceIds,
			updateSchedule,
			keepTurns: true,
			shifts: updateSchedule
				? {
					startTime,
					endTime,
					duration: Number(duration),
					address: doctorAddress,
					weekdays: selectedWeekdays as Weekdays[]
				}
				: null
		};

		const updated = await updateDoctorProfile($user.id, payload);
		fullName = updated.name ?? fullName;
		email = updated.email ?? email;
		telephone = updated.telephone ?? telephone;
		mailLanguage = updated.mailLanguage ?? mailLanguage;
		license = updated.license ?? license;
		specialty = updated.specialty ?? specialty;
		doctorAddress = updated.address ?? doctorAddress;
		selectedDoctorInsuranceIds = (updated.insuranceIds ?? insuranceIds).map((id) => String(id));
		selectedWeekdays = (updated.weekdays ?? selectedWeekdays) as string[];
		startTime = normalizeTimeValue(updated.startTime) || startTime;
		endTime = normalizeTimeValue(updated.endTime) || endTime;
		duration = updated.duration != null ? String(updated.duration) : duration;
	};

	const saveProfile = async () => {
		if (!$user) {
			return;
		}

		saveError = '';
		showSaveSuccessToast = false;

		try {
			if ($user.role === 'PATIENT') {
				await savePatientProfile();
			} else if ($user.role === 'DOCTOR') {
				await saveDoctorProfile();
			}
			if (saveError) {
				return;
			}
			showSaveSuccessToast = true;
		} catch (error) {
			console.error('Failed to update profile:', error);
			saveError = m['error.500_message']();
		}
	};

	const toggleDoctorInsurance = (id: string) => {
		if (selectedDoctorInsuranceIds.includes(id)) {
			selectedDoctorInsuranceIds = selectedDoctorInsuranceIds.filter((selectedId) => selectedId !== id);
		} else {
			selectedDoctorInsuranceIds = [...selectedDoctorInsuranceIds, id];
		}
	};

	const toggleWeekday = (weekday: string) => {
		if (selectedWeekdays.includes(weekday)) {
			selectedWeekdays = selectedWeekdays.filter((selectedDay) => selectedDay !== weekday);
		} else {
			selectedWeekdays = [...selectedWeekdays, weekday];
		}
	};

	onMount(loadProfile);

	$effect(() => {
		if (!$user) {
			loaded = false;
			loadedUserId = null;
			return;
		}

		loadProfile();
	});
</script>

{#if $user && $user.role === 'PATIENT'}
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
					bind:value={insuranceId}
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
				<Button variant="primary" class="w-fit" onclick={saveProfile}>
					{m['profile.save.button']()}
				</Button>
			</div>
			{#if saveError}
				<p class="text-error-text mt-4 text-center">{saveError}</p>
			{/if}
		</div>
	</div>
{:else if $user && $user.role === 'DOCTOR'}
	<div class="profile-page">
		<div class="card bg-white profile-summary">
			<div class="profile-avatar">
				<Avatar size="xl" src={avatarSrc} class="bg-primary" />
				<button class="edit-avatar-btn" type="button" aria-label={m['admin.insurances.button.edit']()}>
					<Icon name="edit" class="w-3.5 h-3.5 text-white" />
				</button>
			</div>
			<div class="profile-info">
				<h1 class="text-[1.8rem] font-semibold text-primaryText">{fullName}</h1>
				<p class="text-secondaryText">{email}</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span> {roleLabel}
				</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['doctor.labels.license']()}:</span> {license}
				</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['doctor.labels.specialty']()}:</span> {specialtyLabel}
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
					<p class="text-sm font-medium text-text">{m['doctor.profile.insurance.label']()}</p>
					<div class="flex flex-wrap gap-2 mt-2">
						{#each insuranceOptions as option}
							<button
								type="button"
								onclick={() => toggleDoctorInsurance(option.value)}
								class={`insurance-pill ${selectedDoctorInsuranceIds.includes(option.value) ? 'selected' : ''}`}
							>
								{option.label}
							</button>
						{/each}
					</div>
				</div>

				<Input
					label={`${m['login.register.address']()}:`}
					bind:value={doctorAddress}
					class="col-span-3"
				/>

				<div class="col-span-3 flex items-center justify-between gap-4 mt-1">
					<p class="text-[2rem] font-semibold text-primaryText">{m['doctor.profile.updateSchedule']()}</p>
					<div class="w-16 min-w-[64px]">
						<Switch bind:checked={updateSchedule} />
					</div>
				</div>

				{#if updateSchedule}
					<div class="col-span-3 flex flex-col gap-5">
						<h3 class="text-[2rem] font-semibold text-primaryText">{m['doctor.labels.schedule']()}</h3>

						<div>
							<p class="text-sm font-medium text-text mb-2">{m['login.register.work_days']()}:</p>
							<div class="flex flex-wrap gap-3">
								{#each weekdayOptions as weekday}
									<button
										type="button"
										onclick={() => toggleWeekday(weekday.value)}
										class={`weekday-pill ${selectedWeekdays.includes(weekday.value) ? 'selected' : ''}`}
									>
										{weekday.label}
									</button>
								{/each}
							</div>
						</div>

						<Select
							label={`${m['login.register.start_time']()}:`}
							options={timeOptions}
							bind:value={startTime}
							class="w-full"
						/>

						<Select
							label={`${m['login.register.end_time']()}:`}
							options={timeOptions}
							bind:value={endTime}
							class="w-full"
						/>

						<Select
							label={`${m['login.register.duration']()}:`}
							options={durationOptions}
							bind:value={duration}
							class="w-full"
						/>
					</div>
				{/if}
			</div>

			<div class="flex justify-center mt-6">
				<Button variant="primary" class="w-fit" onclick={saveProfile}>
					{m['profile.save.button']()}
				</Button>
			</div>
			{#if saveError}
				<p class="text-error-text mt-4 text-center">{saveError}</p>
			{/if}
		</div>
	</div>
{:else}
	<div class="card bg-white profile-section text-center">
		<h2 class="section-title">{m['profile.title']()}</h2>
		<p class="text-secondaryText mt-4">{m['no_data_available']()}</p>
	</div>
{/if}

<Toast
	bind:show={showSaveSuccessToast}
	variant="success"
	title={m['profile.save.success_title']()}
	description={m['profile.save.success_message']()}
	duration={3000}
/>

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

	.insurance-pill {
		display: inline-flex;
		align-items: center;
		justify-content: center;
		border-radius: 999px;
		border: 1px solid var(--color-primaryBorder);
		padding: 6px 14px;
		font-weight: 500;
		background-color: var(--color-bgColor);
		cursor: pointer;
		transition: all 0.2s ease;
	}

	.insurance-pill.selected {
		background-color: var(--color-primary);
		border-color: var(--color-primary);
		color: #fff;
	}

	.weekday-pill {
		width: 44px;
		height: 44px;
		border-radius: 999px;
		border: 1px solid var(--color-primaryBorder);
		background-color: var(--color-bgColor);
		font-weight: 600;
		cursor: pointer;
		transition: all 0.2s ease;
	}

	.weekday-pill.selected {
		background-color: var(--color-primary);
		border-color: var(--color-primary);
		color: #fff;
	}

	@media (max-width: 960px) {
		.profile-summary {
			flex-direction: column;
			align-items: flex-start;
		}

		.profile-section :global(.grid) {
			grid-template-columns: 1fr;
		}

		.profile-section :global(.col-span-2),
		.profile-section :global(.col-span-3) {
			grid-column: span 1;
		}
	}
</style>
