<script lang="ts">
	import { base } from '$app/paths';
    import '../layout.css';

    let { children } = $props();
</script>

<div class="flex min-h-screen! min-w-screen! justify-center items-center">
    <div class="login-card flex max-w-120! max-h-[90vh] flex-col items-center">
        <img src="{base}/cached/resources/logo3.png" alt="Logo" class="mb-6 w-18 h-18" />
        {@render children()}
    </div>
</div>

<style>
    .login-card {
        background-color: white;
        padding: 40px 30px;
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
        text-align: center;
        box-sizing: border-box;
    }
</style>
