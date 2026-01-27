<script lang="ts">
	import { base } from "$app/paths";
	import Button from "$components/Button/Button.svelte";
	import Input from "$components/Input/Input.svelte";
	import Toast from "$components/Toast/Toast.svelte";
	import { m } from "$lib/paraglide/messages";
	import { requestPasswordReset } from "$lib/services/users";

    let email = $state('');

    let success = $state(false);
    let error = $state(false);

    const handleSubmit = () => {
        requestPasswordReset(email)
            .then(() => {
                success = true;
            })
            .catch(() => {
                error = true;
            });
    };
</script>

<div class="flex flex-col items-center w-[420px]">
    <h1 class="text-[1.75rem] font-semibold mb-1.5">{m["login.recover.title"]()}</h1>
    <p class="text-mutedText mb-7 font-[0.95rem]">{m["login.recover.subtitle"]()}</p>

    <div class="flex flex-col items-center text-start gap-6 w-full">
        <Input
            label={`${m["login.email_label"]()}:`}
            placeholder="you@example.com"
            bind:value={email}
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
            {m["login.recover.submit_button"]()}
        </Button>

        <div class="flex flex-col w-full justify-center items-center">
            <div class="flex text-mutedText">
                <p>{m["login.recover.remember"]()}</p>
                <a href="{base}/login" class="ml-1 font-semibold text-primary hover:underline cursor-pointer">
                    {m["login.recover.back_to_login"]()}
                </a>
            </div>
            <div class="flex text-mutedText">
                <p>{m["login.no_account"]()}</p>
                <a href="{base}/register" class="ml-1 font-semibold text-primary hover:underline cursor-pointer">
                    {m["login.register_link"]()}
                </a>
            </div>
        </div>
    </div>

    <Toast
        bind:show={success}
        title={m["login.recover.success_title"]()}
        description={m["login.recover.success_message"]()}
        variant="success"
        position="top-center"
        duration={3000}
    />

    <Toast
        bind:show={error}
        title={m["login.recover.error_title"]()}
        description={m["login.recover.error_message"]()}
        variant="destructive"
        position="top-center"
        duration={3000}
    />
</div>