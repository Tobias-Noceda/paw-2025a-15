<script lang="ts">
	import { base } from "$app/paths";
	import Button from "$components/Button/Button.svelte";
	import Input from "$components/Input/Input.svelte";
	import { m } from "$lib/paraglide/messages";

    let email = $state('');
    let password = $state('');

    const handleLogin = () => {
        // Implement login logic here
        console.log(`Logging in with email: ${email} and password: ${password}`);
    };

    let error = $state(false);
</script>

<div class="flex flex-col items-center w-[420px]">
    <h1 class="text-[1.75rem] font-semibold mb-1.5">{m["login.title"]()}</h1>
    <p class="text-mutedText mb-7 font-[0.95rem]">{m["login.welcome"]()}</p>

    <div class="flex flex-col items-center text-start gap-6 w-full">
        <Input
            label={`${m["login.email_label"]()}:`}
            placeholder="you@example.com"
            bind:value={email}
            class="w-full"
            onsubmit={() => {
                const passwordInput = document.getElementById('password') as HTMLInputElement;
                if (passwordInput) {
                    passwordInput.focus();
                }
            }}
        />

        <Input
            id="password"
            type="password"
            label={`${m["login.password_label"]()}:`}
            placeholder="••••••••••"
            bind:value={password}
            class="w-full"
            onsubmit={() => {
                const passwordInput = document.getElementById('password') as HTMLInputElement;
                if (passwordInput) {
                    passwordInput.blur();
                }
                handleLogin();
            }}
        />

        <div class="flex w-full justify-end mb-[25px]">
            <!-- <label class="flex items-center gap-2 text-[0.9rem]">
                <input type="checkbox" class="w-4 h-4" />
                {m["login.remember_me"]()}
            </label>
            <div class="flex-grow"></div> -->
            
            <a href="{base}/forgot-password" class="text-[0.9rem] text-primary hover:underline cursor-pointer">
                {m["login.forgot_password"]()}
            </a>
        </div>

        {#if error}
            <p class="w-full rounded-md text-[1rem] text-error-text bg-error-bg text-sm text-start mb-5 p-3">
                {m["login.invalid_credentials"]()}
            </p>
        {/if}

        <Button
            variant="primary"
            onclick={handleLogin}
            class="w-full rounded-xl mb-3.5"
        >
            {m["login.submit_button"]()}
        </Button>

        <div class="flex flex-col w-full justify-center items-center">
            <div class="flex text-mutedText">
                <p>{m["login.no_account"]()}</p>
                <a href="{base}/register" class="ml-1 font-semibold text-primary hover:underline cursor-pointer">
                    {m["login.register"]()}
                </a>
            </div>
            <a href="{base}/home" class="font-semibold text-primary hover:underline cursor-pointer">
                {m["login.go_back"]()}
            </a>
        </div>
    </div>
</div>