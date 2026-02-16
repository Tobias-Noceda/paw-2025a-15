<script lang="ts">
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
	import { updateDoctorProfile, type DoctorProfileUpdateData } from '$lib/services/doctors';
	import { updatePatientProfile, type PatientProfileUpdateData } from '$lib/services/patients';
	import { Durations, getWeekdayShortLabel, TimeSlots, Weekdays } from '$types/enums/weekdays';
	import { getSpecialtyLabel, Specialty } from '$types/enums/specialties';
	import { Locales } from '$types/enums/locales';
	import { BloodType } from '$types/enums/bloodTypes';
	import RadioCheck from '$components/RadioCheck/RadioCheck.svelte';
	import type { PageData } from './$types';
	import { parseDateInLocalTimezone } from '$lib/services/appointments';
	import PopUp from '$components/PopUp/PopUp.svelte';
	import { refreshToken } from '$modules/api.svelte';

	let { data }: { data: PageData } = $props();

	const languageOptions = Object.entries(Locales).map(([key]) => ({
		value: key,
		label: `${m[`locale.${key}`]()}`
	}));

	const bloodTypeOptions = [
		{ value: 'N/A', label: '-' },
		...Object.entries(BloodType).map(([_, value]) => ({
			value: value,
			label: value
		}))
	];

	const yesNoOptions = [
		{ value: 'N/A', label: '-' },
		{ value: 'true', label: m['profileInfo.yes']() },
		{ value: 'false', label: m['profileInfo.no']() }
	];

	const weekDays = [
		...Object.values(Weekdays).map((day) => ({
			id: day,
			label: getWeekdayShortLabel(day),
			checked: data.doctor && data.doctor.schedule ? data.doctor.schedule.has(day) : false
		}))
	];

	const timeSpans = [
		{
			value: 'N/A',
			label: '-'
		},
		...Object.values(TimeSlots).map((time) => ({
			value: time,
			label: time
		}))
	];

	const durations = [
		{
			value: 'N/A',
			label: '-'
		},
		...Object.values(Durations).map((duration) => ({
			value: duration,
			label: duration
		}))
	];

	let fullName = $state(data.currentUser.name || '');
	let email = $state(
		data.doctor ? data.doctor.email || '' : data.patient ? data.patient.email || '' : ''
	);
	let role = $state(data.currentUser.role || '');
	let avatarSrc = $state(data.currentUser.image || '');
	let telephone = $state(
		data.doctor ? data.doctor.telephone || '' : data.patient ? data.patient.telephone || '' : ''
	);
	let mailLanguage = $state(data.currentUser.language || 'EN_US');
	let newImage: File | null = $state(null);

	let saveError = $state('');
	let showSaveSuccessToast = $state(false);

	let birthDate: Date | null = $state(
		data.patient && data.patient.birthdate ? parseDateInLocalTimezone(data.patient.birthdate) : null
	);
	let bloodType = $state(data.patient && data.patient.bloodType ? data.patient.bloodType : '');
	let height = $state<number | undefined>(
		data.patient && data.patient.height != null ? data.patient.height : undefined
	);
	let weight = $state<number | undefined>(
		data.patient && data.patient.weight != null ? data.patient.weight : undefined
	);
	let insuranceName: string = $state(
		data.patient && data.patient.insurance ? data.patient.insurance : ''
	);
	let insuranceNumber = $state(
		data.patient && data.patient.insuranceNumber ? data.patient.insuranceNumber : ''
	);
	let smokes: string = $state(
		data.patient
			? data.patient.smokes === undefined
				? 'N/A'
				: data.patient.smokes
					? 'true'
					: 'false'
			: 'N/A'
	);
	let drinks: string = $state(
		data.patient
			? data.patient.drinks === undefined
				? 'N/A'
				: data.patient.drinks
					? 'true'
					: 'false'
			: 'N/A'
	);
	let diet = $state(data.patient && data.patient.diet ? data.patient.diet : '');
	let meds = $state(data.patient && data.patient.meds ? data.patient.meds : '');
	let conditions = $state(data.patient && data.patient.conditions ? data.patient.conditions : '');
	let allergies = $state(data.patient && data.patient.allergies ? data.patient.allergies : '');
	let hobbies = $state(data.patient && data.patient.hobbies ? data.patient.hobbies : '');
	let job = $state(data.patient && data.patient.job ? data.patient.job : '');

	let license = $state(data.doctor && data.doctor.license ? data.doctor.license : '');
	let specialty = $state(data.doctor && data.doctor.specialty ? data.doctor.specialty : '');
	let doctorAddress = $state(data.doctor && data.doctor.address ? data.doctor.address : '');
	let selectedInsurances = $state<string[]>(
		data.doctor && data.doctor.insurances ? data.doctor.insurances : []
	);
	let updateSchedule = $state(false);
	let keepTurnDeciding = $state(false);
	let selectedWeekdays = $state<Weekdays[]>(
		data.doctor && data.doctor.schedule ? Array.from(data.doctor.schedule.keys()) : []
	);
	let startTime = $state(data.doctor && data.doctor.startTime ? data.doctor.startTime : 'N/A');
	let endTime = $state(data.doctor && data.doctor.endTime ? data.doctor.endTime : 'N/A');
	let duration = $state(data.doctor && data.doctor.duration ? String(data.doctor.duration) : 'N/A');

	const parseBoolean = (value: string) => {
		if (value === 'true') return true;
		if (value === 'false') return false;
		return undefined;
	};

	const roleLabel = $derived.by(() => {
		if (!role) return '';
		if (role === 'PATIENT') return m['role.PATIENT']();
		if (role === 'DOCTOR') return m['role.DOCTOR']();
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

	const savePatientProfile = async () => {
		if (!$user || $user.role !== 'PATIENT' || !data.patient) {
			return;
		}

		const payload: PatientProfileUpdateData = {
			telephone,
			mailLanguage,
			birthdate: birthDate,
			bloodType: bloodType || undefined,
			height: height,
			weight: weight,
			smokes: parseBoolean(smokes),
			drinks: parseBoolean(drinks),
			diet: diet || undefined,
			meds: meds || undefined,
			conditions: conditions || undefined,
			allergies: allergies || undefined,
			hobbies: hobbies || undefined,
			job: job || undefined,
			insuranceSelf:
				data.insurances.find((ins) => ins.name === insuranceName)?.links.self || undefined,
			insuranceNumber: insuranceNumber || undefined
		};

		await updatePatientProfile(data.currentUser, payload, data.patient!, newImage || undefined, fetch);
	};

	const saveDoctorProfile = async (keepTurns: boolean) => {
		if (!$user || $user.role !== 'DOCTOR') {
			return;
		}

		const insuranceSelfs = data.insurances
			.filter((ins) => selectedInsurances.includes(ins.name))
			.map((ins) => ins.links.self);

		if (updateSchedule) {
			if (
				selectedWeekdays.length === 0 ||
				doctorAddress.trim() === 'N/A' ||
				startTime.trim() === 'N/A' ||
				endTime.trim() === 'N/A' ||
				duration.trim() === 'N/A'
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
			insuranceSelfs,
			updateSchedule,
			keepTurns: keepTurns,
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

		await updateDoctorProfile(data.currentUser, payload, data.doctor!, newImage || undefined, fetch);
	};

	const saveProfile = async (keep?: boolean) => {
		if (!$user) {
			return;
		}

		saveError = '';
		showSaveSuccessToast = false;

		try {
			if ($user.role === 'PATIENT') {
				await savePatientProfile();
			} else if ($user.role === 'DOCTOR') {
				await saveDoctorProfile(keep === undefined ? false : keep);
			}
			if (saveError) {
				return;
			}
			showSaveSuccessToast = true;
		} catch (error) {
			console.error('Failed to update profile:', error);
			saveError = m['error.500_message']();
		} finally {
			refreshToken();
			newImage = null;
		}
	};
</script>

{#if $user && $user.role === 'PATIENT'}
	<div class="profile-page">
		<div class="card bg-white profile-summary">
			<div class="profile-avatar">
				<Avatar size="xl" src={avatarSrc} class="bg-primary" />
				<button
					class="edit-avatar-btn"
					type="button"
					aria-label={m['admin.insurances.button.edit']()}
					onclick={() => {
						const input = document.createElement('input');
						input.type = 'file';
						input.accept = '.jpeg,.png';
						input.multiple = false;
						input.onchange = (event) => {
							const target = event.target as HTMLInputElement;
							if (target.files && target.files.length > 0) {
								if (target.files[0].type === 'image/jpeg' || target.files[0].type === 'image/png') {
									newImage = target.files[0];
								}
							}
						};
						input.click();
					}}
				>
					<Icon name="edit" class="w-3.5 h-3.5 text-white" />
				</button>
			</div>
			<div class="profile-info">
				<h1 class="text-[1.4rem] font-semibold text-primaryText">{fullName}</h1>
				<p class="text-secondaryText">{email}</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span>
					{roleLabel}
				</p>
			</div>
		</div>

		<div class="card bg-white profile-section">
			<h2 class="section-title">{m['profileInfo.basic']()}</h2>
			<div class="grid grid-cols-3 gap-5 mt-6">
				<Input
					label={m['profile.phone.label']()}
					bind:value={telephone}
					class="col-span-3 w-full"
				/>
				<Select
					label={m['profile.email.lang.label']()}
					options={languageOptions}
					bind:value={mailLanguage}
					placeholder={m['profileInfo.select']()}
					class="col-span-3 w-full"
				/>
				<DatePicker
					label={m['form.birthDate']()}
					bind:selectedDate={birthDate}
					maxDate={new Date()}
					yearRange={Array.from({ length: 100 }, (_, i) => new Date().getFullYear() - i)}
					required
					class="w-full"
				/>
				<Select
					label={m['profileInfo.bloodType']()}
					options={bloodTypeOptions}
					bind:value={bloodType}
					placeholder={m['profileInfo.select']()}
					class="col-span-1 w-full"
				/>
				<Input
					label={m['profileInfo.height']()}
					type="number"
					bind:value={height}
					class="col-span-1 w-full"
				/>
				<Input
					label={m['profileInfo.weight']()}
					type="number"
					bind:value={weight}
					class="col-span-1 w-full"
				/>
				<Select
					label={m['profile.insurance.label']()}
					options={data.insurances.map((insurance) => ({
						value: insurance.name,
						label: insurance.name
					}))}
					bind:value={insuranceName}
					placeholder={m['profile.insurance.placeholder']()}
					class="col-span-1 w-full"
				/>
				<Input
					label={m['profile.insuranceNumber.label']()}
					placeholder={m['profile.insuranceNumber.placeholder']()}
					bind:value={insuranceNumber}
					class="col-span-2 w-full"
				/>
			</div>
		</div>

		<div class="card bg-white profile-section">
			<h2 class="section-title">{m['profileInfo.habits']()}</h2>
			<div class="grid grid-cols-2 gap-5 mt-6">
				<Select
					label={m['profileInfo.smokes']()}
					options={yesNoOptions}
					bind:value={smokes}
					class="col-span-1 w-full"
				/>
				<Select
					label={m['profileInfo.drinks']()}
					options={yesNoOptions}
					bind:value={drinks}
					class="col-span-1 w-full"
				/>
				<Input label={m['profileInfo.diet']()} bind:value={diet} class="col-span-2 w-full" />
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
				<Button variant="primary" class="w-fit" onclick={() => saveProfile()}>
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
				<button
					class="edit-avatar-btn"
					type="button"
					aria-label={m['admin.insurances.button.edit']()}
					onclick={() => {
						const input = document.createElement('input');
						input.type = 'file';
						input.accept = '.jpeg,.png';
						input.multiple = false;
						input.onchange = (event) => {
							const target = event.target as HTMLInputElement;
							if (target.files && target.files.length > 0) {
								if (target.files[0].type === 'image/jpeg' || target.files[0].type === 'image/png') {
									newImage = target.files[0];
								}
							}
						};
						input.click();
					}}
				>
					<Icon name="edit" class="w-3.5 h-3.5 text-white" />
				</button>
			</div>
			<div class="profile-info">
				<h1 class="text-[1.8rem] font-semibold text-primaryText">{fullName}</h1>
				<p class="text-secondaryText">{email}</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['profile.role.label']()}:</span>
					{roleLabel}
				</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['doctor.labels.license']()}:</span>
					{license}
				</p>
				<p class="text-secondaryText">
					<span class="font-semibold text-primaryText">{m['doctor.labels.specialty']()}:</span>
					{specialtyLabel}
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
						<RadioCheck
							options={data.insurances.map((insurance) => ({
								id: insurance.name,
								label: insurance.name,
								checked: selectedInsurances.includes(insurance.name)
							}))}
							onchange={(event) => {
								if (event.checked) {
									selectedInsurances = [...selectedInsurances, event.id];
								} else {
									selectedInsurances = selectedInsurances.filter((name) => name !== event.id);
								}
							}}
							class="flex flex-wrap w-full text-sm font-normal"
						/>
					</div>
				</div>

				<div class="col-span-3 flex items-center justify-between gap-4 mt-1">
					<p class="text-[2rem] font-semibold text-primaryText">
						{m['doctor.profile.updateSchedule']()}
					</p>
					<div class="w-16 min-w-16">
						<Switch
							bind:checked={updateSchedule}
							class="flex flex-row w-full justify-start items-center gap-2"
							innerClass="w-12"
						/>
					</div>
				</div>

				{#if updateSchedule}
					<div class="col-span-3 flex flex-col gap-5">
						<h3 class="text-[2rem] font-semibold text-primaryText">
							{m['doctor.labels.schedule']()}
						</h3>

						<Input
							label={`${m['login.register.address']()}:`}
							bind:value={doctorAddress}
							class="col-span-3"
						/>

						<div>
							<p class="text-sm font-medium text-text mb-2">{m['login.register.work_days']()}:</p>
							<RadioCheck
								options={weekDays}
								onchange={(event) => {
									if (event.checked) {
										selectedWeekdays = [...selectedWeekdays, event.id as Weekdays];
									} else {
										selectedWeekdays = selectedWeekdays.filter((day) => day !== event.id);
									}
								}}
								class="flex flex-wrap w-full text-sm font-normal"
								optionsClass="w-10"
							/>
						</div>

						<Select
							label={`${m['login.register.start_time']()}:`}
							options={timeSpans}
							bind:value={startTime}
							class="w-full"
						/>

						<Select
							label={`${m['login.register.end_time']()}:`}
							options={timeSpans}
							bind:value={endTime}
							class="w-full"
						/>

						<Select
							label={`${m['login.register.duration']()}:`}
							options={durations}
							bind:value={duration}
							class="w-full"
						/>
					</div>
				{/if}
			</div>

			<div class="flex justify-center mt-6">
				<Button variant="primary" class="w-fit" onclick={() => {
					if (updateSchedule) {
						keepTurnDeciding = true;
					} else {
						saveProfile();
					}
				}}>
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

{#if keepTurnDeciding}
        <PopUp onClose={() => keepTurnDeciding = false}>
            <div class="flex flex-col gap-2">
                <h1 class="text-primaryText text-[1.17rem] font-bold">
                    {m['profile.pop_up.keep.title']()}
                </h1>

                <div class="flex justify-end gap-4 mt-2">
                    <Button
                        variant="primary"
                        onclick={() => {
							keepTurnDeciding = false;
							saveProfile(true);
						}}
                    >
                        {m['profile.pop_up.keep.confirm']()}
                    </Button>
                    <Button
                        variant="destructive"
                        onclick={() => {
							keepTurnDeciding = false;
							saveProfile(false);
						}}
                    >
                        {m['profile.pop_up.keep.cancel']()}
                    </Button>
                </div>
            </div>
        </PopUp>
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
		cursor: pointer;
		transition: transform 0.2s;
	}

	.edit-avatar-btn:hover {
		transform: scale(1.1);
	}

	:global(.hidden) {
		display: none;
	}

	.profile-info {
		display: flex;
		flex-direction: column;
		gap: 4px;
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
