<script lang="ts">
	import { base } from "$app/paths";
	import { page } from "$app/stores";
	import Button from "$components/Button/Button.svelte";
	import Input from "$components/Input/Input.svelte";
	import Toast from "$components/Toast/Toast.svelte";
	import { m } from "$lib/paraglide/messages";

    const token = $derived($page.params.token);

    let newPassword = $state('');
    let confirmPassword = $state('');

    let success = $state(false);
    let error = $state(false);

    const handleSubmit = () => {
        // Implement login logic here
        console.log(`Using token: ${token}.`);
        console.log(`Submitting with new password: ${newPassword} and confirm password: ${confirmPassword}`);
        success = true;
        setTimeout(() => {
            error = true;
        }, 3000);
    };
</script>

<div class="flex flex-col items-center w-[420px]">
    <h1 class="text-[1.75rem] font-semibold mb-1.5">{m["login.change.title"]()}</h1>
    <p class="text-mutedText mb-7 font-[0.95rem]">{m["login.change.subtitle"]()}</p>

    <div class="flex flex-col items-center text-start gap-6 w-full">
        <Input
            label={`${m["login.change.new_label"]()}:`}
            type="password"
            placeholder="••••••••••"
            bind:value={newPassword}
            class="w-full"
            onsubmit={() => {
                const confirmPasswordInput = document.getElementById('confirmPassword') as HTMLInputElement;
                if (confirmPasswordInput) {
                    confirmPasswordInput.focus();
                }
            }}
        />

        <Input
            id="confirmPassword"
            label={`${m["login.change.confirm_label"]()}:`}
            type="password"
            placeholder="••••••••••"
            bind:value={confirmPassword}
            class="w-full"
            onsubmit={() => {
                const passwordInput = document.getElementById('password') as HTMLInputElement;
                if (passwordInput) {
                    passwordInput.blur();
                }
                handleSubmit();
            }}
        />

        <Button
            variant="primary"
            onclick={handleSubmit}
            class="w-full rounded-xl mb-3.5"
        >
            {m["login.change.submit_button"]()}
        </Button>

        <div class="flex flex-col w-full justify-center items-center">
            <a href="{base}/login" class="font-semibold text-primary hover:underline cursor-pointer">
                {m["login.change.back_to_login"]()}
            </a>
        </div>
    </div>

    <Toast
        bind:show={success}
        title={m["login.change.success_title"]()}
        description={m["login.change.success_message"]()}
        variant="success"
        position="top-center"
        duration={3000}
    />

    <Toast
        bind:show={error}
        title={m["login.change.error_title"]()}
        description={m["login.change.error_message"]()}
        variant="destructive"
        position="top-center"
        duration={3000}
    />
</div>