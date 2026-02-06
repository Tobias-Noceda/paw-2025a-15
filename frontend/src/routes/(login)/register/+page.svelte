<script lang="ts">
	import Input from "$components/Input/Input.svelte";
    import Button from "$components/Button/Button.svelte";
	import { m } from "$lib/paraglide/messages";
	import { base } from "$app/paths";
	import Tabs from "$components/Tabs/Tabs.svelte";
	import DatePicker from "$components/DatePicker/DatePicker.svelte";
	import { getSpecialtyLabel, Specialty } from "$types/enums/specialties";
	import Select from "$components/Select/Select.svelte";
	import type { Insurance } from "$types/api";
	import { onMount } from "svelte";
	import RadioCheck from "$components/RadioCheck/RadioCheck.svelte";
	import { Durations, getWeekdayShortLabel, TimeSlots, Weekdays } from "$types/enums/weekdays";
	import { createPatient } from "$lib/services/patients";
	import Toast from "$components/Toast/Toast.svelte";
	import { createDoctor } from "$lib/services/doctors";
	import { fetchInsurances, fetchInsurancesPage } from "$lib/services/insurances";

    let role: string = $state('');

    let name: string = $state("");
    let surname: string = $state("");
    let email: string = $state("");
    let telephone: string = $state("");
    let password: string = $state("");
    let confirmPassword: string = $state("");
    
    let birthdate: Date | null = $state(null);
    let height: number | string = $state("");
    let weight: number | string = $state("");

    let license: string = $state("");
    let specialty: Specialty = $state(Specialty.BARIATRIC_SURGERY);
    let address: string = $state("");
    let selectedInsurances: string[] = $state([]);
    let selectedWeekdays: string[] = $state([]);
    let startTime: string = $state("N/A");
    let endTime: string = $state("N/A");
    let durationTime: string = $state("N/A");

    const specialties = [
		...Object.values(Specialty).map((spec) => ({
			value: spec,
			label: getSpecialtyLabel(spec)
		}))
	];

    const weekDays = [
        ...Object.values(Weekdays).map((day) => ({
            id: day,
            label: getWeekdayShortLabel(day),
            checked: false
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

    let insurances: Insurance[] = $state([]);

    let error: boolean = $state(false);
    let success: boolean = $state(false);

    const focusInputById = (id: string) => {
        const inputElement = document.getElementById(id) as HTMLInputElement;
        if (inputElement) {
            inputElement.focus();
        }
    };

    const canRegister = (): boolean => {
        if (role === 'patient') {
            return (
                name.trim() !== '' &&
                surname.trim() !== '' &&
                email.trim() !== '' &&
                telephone.trim() !== '' &&
                birthdate !== null &&
                height !== '' &&
                weight !== '' &&
                password.trim() !== '' &&
                confirmPassword.trim() !== '' &&
                confirmPassword === password
            );
        } else if (role === 'doctor') {
            return (
                name.trim() !== '' &&
                surname.trim() !== '' &&
                email.trim() !== '' &&
                telephone.trim() !== '' &&
                license.trim() !== '' &&
                specialty.trim() !== '' &&
                address.trim() !== '' &&
                password.trim() !== '' &&
                confirmPassword.trim() !== '' &&
                confirmPassword === password &&
                startTime !== 'N/A' &&
                endTime !== 'N/A' &&
                durationTime !== 'N/A'
            );
        }
        return false;
    };

    const redirectToLogin = () => {
        setTimeout(() => {
            // Clear form
            name = '';
            surname = '';
            email = '';
            telephone = '';
            password = '';
            confirmPassword = '';
            birthdate = null;
            height = '';
            weight = '';
            license = '';
            specialty = Specialty.BARIATRIC_SURGERY;
            address = '';
            selectedInsurances = [];
            selectedWeekdays = [];
            startTime = 'N/A';
            endTime = 'N/A';
            durationTime = 'N/A';
            role = '';

            window.location.href = `${base}/login`;
        }, 1500);
    };

    const handleRegister = () => {
        if (password !== confirmPassword) {
            error = true;
            return;
        }
        let isOk = false;
        if (role === 'patient' && canRegister()) {
            createPatient(
                {
                    name: `${name} ${surname}`,
                    email,
                    telephone,
                    height: height as number,
                    weight: weight as number,
                    birthDate: birthdate!.toISOString().split('T')[0],
                },
                password
            ).then(() => {
                success = true;
                error = false;
                redirectToLogin();
            }).catch(() => {
                error = true;
                success = false;
            });
        } else if (role === 'doctor' && canRegister()) {
            createDoctor(
                {
                    name: `${name} ${surname}`,
                    email,
                    telephone,
                    license,
                    specialty,
                    direction: address,
                    insurances: selectedInsurances
                },
                password,
                {
                    startTime,
                    endTime,
                    duration: Number(durationTime)!,
                    address,
                    weekdays: selectedWeekdays as Weekdays[],
                }
            ).then(() => {
                success = true;
                error = false;
                redirectToLogin();
            }).catch(() => {
                error = true;
                success = false;
            });
        } else {
            error = true;
        }
    };

    onMount(async () => {
		// Fetch insurances list
		let insurancesPage = await fetchInsurances(undefined, fetch);
		insurances = [...insurancesPage.results];
        while (insurancesPage._links.next) {
            insurancesPage = await fetchInsurancesPage(insurancesPage._links.next, fetch);
            insurances = [...insurances, ...insurancesPage.results];
        }
	});
</script>

<div class="flex flex-col items-center w-[420px] overflow-y-auto px-0.5">
    <h1 class="text-[1.75rem] font-semibold mb-1.5">{m['login.register.title']()}</h1>
    <Tabs
        options={[
            { value: 'patient', label: m['login.register.role.patient']() },
            { value: 'doctor', label: m['login.register.role.doctor']() }
        ]}
        bind:value={role}
        class="mb-6"
        onchange={(val) => {
            role = val;
            error = false;
        }}
    />

    <div class="flex flex-col items-center text-start gap-6 w-full">
        {#if role !== ''}
            <Input
                label={`${m["login.register.name"]()}:`}
                placeholder=""
                bind:value={name}
                required
                class="w-full"
                onsubmit={() => focusInputById('surname') }
            />

            <Input
                id="surname"
                label={`${m["login.register.surname"]()}:`}
                placeholder=""
                bind:value={surname}
                required
                class="w-full"
                onsubmit={() => focusInputById('email') }
            />

            <Input
                id="email"
                label={`${m["login.register.email"]()}:`}
                placeholder="you@example.com"
                bind:value={email}
                required
                class="w-full"
                onsubmit={() => focusInputById('telephone') }
            />

            <Input
                id="telephone"
                label={`${m["login.register.telephone"]()}:`}
                placeholder="+54 9 11 1234 5678"
                bind:value={telephone}
                required
                class="w-full"
                onsubmit={() => {
                    if (role === 'patient') {
                        const telephoneInput = document.getElementById('telephone') as HTMLInputElement;
                        if (telephoneInput) {
                            telephoneInput.blur();
                        }
                    } else if (role === 'doctor') {
                        focusInputById('license');
                    }
                }}
            />

            {#if role === 'patient'}
                <DatePicker
                    id="birthdate"
                    label={`${m["login.register.birthdate"]()}:`}
                    bind:selectedDate={birthdate}
                    maxDate={new Date()}
                    yearRange={Array.from({ length: 100 }, (_, i) => new Date().getFullYear() - i)}
                    required
                    class="w-full"
                    onSelectDate={() => focusInputById('height') }
                />

                <Input
                    id="height"
                    type="number"
                    label={`${m["login.register.height"]()}:`}
                    placeholder=""
                    bind:value={height}
                    required
                    class="w-full"
                    onsubmit={() => focusInputById('weight') }
                />

                <Input
                    id="weight"
                    type="number"
                    label={`${m["login.register.weight"]()}:`}
                    placeholder=""
                    bind:value={weight}
                    required
                    class="w-full"
                    onsubmit={() => focusInputById('password') }
                />
            {:else if role === 'doctor'}
                <Input
                    id="license"
                    label={`${m["login.register.license"]()}:`}
                    placeholder=""
                    bind:value={license}
                    required
                    class="w-full"
                    onsubmit={() => {
                        const licenseInput = document.getElementById('license') as HTMLInputElement;
                        if (licenseInput) {
                            licenseInput.blur();
                        }
                    }}
                />

                <Select
                    label={`${m["login.register.specialty"]()}:`}
                    required
                    options={specialties}
                    bind:value={specialty}
                    class="w-full"
                    onchange={() => focusInputById('address') }
                />

                <Input
                    id="address"
                    label={`${m["login.register.address"]()}:`}
                    placeholder=""
                    bind:value={address}
                    required
                    class="w-full"
                    onsubmit={() => {
                        const addressInput = document.getElementById('address') as HTMLInputElement;
                        if (addressInput) {
                            addressInput.blur();
                        }
                    }}
                />

                <RadioCheck
                    label={`${m["login.register.insurances"]()}:`}
                    options={insurances.map(insurance => ({
                        id: insurance.name,
                        label: insurance.name,
                        checked: selectedInsurances.includes(insurance.name)
                    }))}
                    onchange={(event) => {
                        if (event.checked) {
                            selectedInsurances = [...selectedInsurances, event.id];
                        } else {
                            selectedInsurances = selectedInsurances.filter(name => name !== event.id);
                        }
                    }}
                    class="flex flex-wrap w-full text-sm font-normal"
                />

                <RadioCheck
                    label={`${m["login.register.work_days"]()}:`}
                    options={weekDays}
                    onchange={(event) => {
                        if (event.checked) {
                            selectedWeekdays = [...selectedWeekdays, event.id];
                        } else {
                            selectedWeekdays = selectedWeekdays.filter(day => day !== event.id);
                        }
                    }}
                    class="flex flex-wrap w-full text-sm font-normal"
                    optionsClass="w-10"
                />

                <Select
                    label={`${m["login.register.start_time"]()}:`}
                    options={timeSpans.slice(0, -2)}
                    bind:value={startTime}
                    class="w-full"
                    onchange={() => focusInputById('endTime') }
                />

                <Select
                    label={`${m["login.register.end_time"]()}:`}
                    options={[
                        {
                            value: 'N/A',
                            label: '-'
                        },
                        ...timeSpans.slice(3)
                    ]}
                    bind:value={endTime}
                    class="w-full"
                    onchange={() => focusInputById('durationTime') }
                />

                <Select
                    label={`${m["login.register.duration"]()}:`}
                    options={durations}
                    bind:value={durationTime}
                    class="w-full"
                    onchange={() => focusInputById('password') }
                />
            {/if}

            <Input
                id="password"
                type="password"
                label={`${m["login.register.password"]()}:`}
                placeholder="••••••••••"
                bind:value={password}
                required
                class="w-full"
                onsubmit={() => focusInputById('confirmPassword') }
            />

            <Input
                id="confirmPassword"
                type="password"
                label={`${m["login.register.confirm_password"]()}:`}
                placeholder="••••••••••"
                bind:value={confirmPassword}
                required
                class="w-full"
                onsubmit={() => {
                    const confirmPasswordInput = document.getElementById('confirmPassword') as HTMLInputElement;
                    if (confirmPasswordInput) {
                        confirmPasswordInput.blur();
                    }
                    handleRegister();
                }}
            />

            {#if error}
                <p class="w-full rounded-md text-[1rem] text-error-text bg-error-bg text-sm text-start mb-5 p-3">
                    {m["login.invalid_credentials"]()}
                </p>
            {/if}

            <Button
                variant="primary"
                disabled={!canRegister()}
                onclick={handleRegister}
                class="w-full rounded-xl mb-3.5"
            >
                {m["login.register.submit_button"]()}
            </Button>
        {/if}
    </div>

    <Toast
        bind:show={success}
        variant="success"
        title={m["login.register.success.title"]()}
        description={m["login.register.success.message"]()}
        duration={3000}
    />

    <Toast
        bind:show={error}
        variant="destructive"
        title={m["login.register.error.title"]()}
        description={m["login.register.error.message"]()}
        duration={3000}
    />

    <div class="flex flex-col w-full justify-center items-center">
        <a href="{base}/login" class="font-semibold text-primary hover:underline cursor-pointer">
            {m["login.register.back_to_login"]()}
        </a>
    </div>
</div>