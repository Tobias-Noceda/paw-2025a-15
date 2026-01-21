<script lang="ts">
	import '../layout.css';
	import { m } from '$lib/paraglide/messages.js';
	import { base } from '$app/paths';
	import { apiOrigin, logout } from '$modules/api.svelte';
	import { page } from '$app/stores';

	import { setUserFromSession, user } from '$lib/stores/user';
	import Icon from '$components/Icon/Icon.svelte';
	import Avatar from '$components/Avatar/Avatar.svelte';
	import { searchQuery, insurance, day, specialty, order, getFiltersURL } from '$stores/filters';
	import { goto } from '$app/navigation';
	import { onMount } from 'svelte';

	let userDropdownOpen = $state(false);

	let { children } = $props();

	$effect(() => {
		const urlSearch = $page.url.searchParams.get('search') || '';
		// Only update if different to prevent feedback loop
		searchQuery.update(current => current !== urlSearch ? urlSearch : current);
	});

	onMount(() => {
		const handleClickOutside = (event: MouseEvent) => {
			const dropdown = document.querySelector('.user-dropdown-menu');
			const button = document.querySelector('.user-btn');
			if (dropdown && button && !dropdown.contains(event.target as Node) && !button.contains(event.target as Node)) {
				userDropdownOpen = false;
			}
		};

		document.addEventListener('click', handleClickOutside);

		// if session is not expired, set user from session
		if (localStorage.getItem('expiration') && new Date() < new Date(localStorage.getItem('expiration')!)) {
			if ($user === null && localStorage.getItem('session')) {
				setUserFromSession(localStorage.getItem('session')!);
			}
		} else {
			// session expired, clear localStorage
			localStorage.removeItem('session');
			localStorage.removeItem('refresh');
			localStorage.removeItem('expiration');
		}

		return () => {
			document.removeEventListener('click', handleClickOutside);
		};
	});
</script>

<div class="flex min-h-screen! min-w-screen! flex-col">
	<div class="topbar">
		<div class="logo">
			<a href="{base}/home" class="logo-link">
				<img src="{base}/cached/resources/icono.jpg" alt={m['topbar.logo_alt_text']()} />
			</a>
		</div>
		{#if $user}
			<nav class="nav-links">
				<!-- add "active" class when the location matches the link -->
				<a href="{base}/appointments" class="nav-item {$page.url.pathname === `${base}/appointments` ? 'active' : ''}">{m['topbar.appointments']()}</a>
				{#if $user.role === 'PATIENT'}
					<a href="{base}/studies" class="nav-item {$page.url.pathname === `${base}/studies` ? 'active' : ''}">{m['topbar.studies']()}</a>
				{:else if $user.role === 'DOCTOR'}
					<a href="{base}/vacations" class="nav-item {$page.url.pathname === `${base}/vacations` ? 'active' : ''}">{m['topbar.vacations']()}</a>
				{/if}
			</nav>
		{/if}
		<div class="search-bar-container">
			<div class="search-bar">
				<form class="search-bar-form" onsubmit={(e) => {
					e.preventDefault();
					goto(`/paw-2025a-15/home?${getFiltersURL($searchQuery, $insurance, $day, $specialty, $order)}`, { replaceState: true, noScroll: true });
				}}>
					<Icon name="search" class="w-4.5 h-4.5 text-white" />
					<input type="text" class="search-bar-text" placeholder="Search..." bind:value={$searchQuery} />
				</form>
			</div>
		</div>
		{#if $user == null}
			<a href="{base}/login">
				<button class="login-btn">
					<Icon name="login" class="w-4.5 h-4.5 text-white" />
					{m['topbar.login']()}
				</button>
			</a>
		{:else}
			<button
				class="user-btn {userDropdownOpen ? 'active' : ''}"
				onclick={() => {
					userDropdownOpen = !userDropdownOpen;
				}}
			>
				<Avatar size="md" src={$user.image} />
				<div class="user-info">
					<p class="user-name">{$user.name}</p>
					<p class="user-role">{$user.role.charAt(0).toUpperCase() + $user.role.slice(1).toLowerCase()}</p>
				</div>
			</button>
		{#if userDropdownOpen}
		<div class="user-dropdown-menu">
			<a href="{base}/profile">{m['topbar.profile']()}</a>
			<button onclick={() => logout()}>{m['topbar.logout']()}</button>
		</div>
		{/if}
		{/if}
	</div>
	<div class="page-container flex-1 w-full!">
		{@render children()}
	</div>
</div>

<style>
	.topbar {
		background-color: #256395;
		display: flex;
		align-items: center;
		padding: 5px 20px;
		/* height eliminado para que sea auto-ajustable */
		box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
		position: sticky;
		top: 0;
		z-index: 1000;
		box-sizing: border-box;
	}

	/* Logo: altura fija y ancho automático */
	.logo img {
		display: block;
		height: 60px; /* ajusta este valor a tu gusto */
		width: auto;
		object-fit: contain; /* mantiene proporción sin recortes */
	}

	.nav-links {
		display: flex;
		margin-left: 20px;
		box-sizing: border-box;
	}

	.nav-item {
		color: white;
		font-weight: 500;
		text-decoration: none;
		padding: 10px 15px;
		margin-right: 10px;
		transition: background 0.3s ease; /* Suaviza el efecto hover */
		box-sizing: border-box;
		border-radius: 5px;
	}

	.nav-item:hover,
	.nav-item.active {
		background-color: #0e3b6b;
		border-radius: 5px;
	}

	.search-bar-container {
		flex-grow: 1;
		display: flex;
		align-items: center;
		justify-content: center;
		box-sizing: border-box;
	}

	.search-bar {
		display: flex;
		align-items: center;
		gap: 8px;
		background-color: #0e3b6b;
		padding: 8px 8px 8px 16px;
		border: none;
		border-radius: 30px;
		height: 30px;
		text-align: start bottom;
		box-sizing: border-box;
	}

	.search-bar-form {
		margin: 0;
		flex: 1;
	}

	.search-bar-form input {
		box-shadow: 0 0 0 1000px #0e3b6b inset !important; /* deep blue */
		border: none !important;
		-webkit-text-fill-color: white !important; /* Chrome */
		color: white; /* Otros navegadores */
		box-sizing: border-box;
	}

	.search-bar-text {
		background-color: #0e3b6b;
		color: white;
		width: 500px;
		height: 20px;
		border: none;
		padding: 0 0 0 8px;
		text-align: left;
		box-sizing: border-box;
	}

	.search-bar-text:focus {
		outline: none;
	}

	.search-bar:focus-within .search-bar-text {
		box-shadow: 4px 2px 10px #0e3b6bff; /* Compatible en ambos */
	}

	.search-bar-text::placeholder {
		color: white;
		opacity: 0.8;
	}

	.login-btn {
		display: inline-flex;
		align-items: center;
		gap: 0.5rem;
		padding: 0.5rem 1rem;
		background: transparent; /* sin fondo */
		border: none; /* sin borde */
		color: white; /* texto en blanco */
		font-style: italic; /* cursiva */
		text-decoration: none;
		cursor: pointer;
		border-radius: 5px;
	}

	.login-btn:hover {
		background: #0e3b6b;
		border-radius: 5px;
	}

	.user-btn {
		display: flex;
		flex-direction: row;
    align-items: center;
    gap: 8px;
		background-color: #256395;
		color: white;
		border: none;
		padding: 5px;
		border-radius: 5px;
		cursor: pointer;
		margin-left: 15px;
		font-weight: normal;
		font-size: smaller;
		transition: background-color 0.3s ease;
		box-sizing: border-box;
		border-radius: 5px;
	}

	.user-btn:hover,
	.user-btn.active {
		background-color: #0e3b6b;
		border-radius: 5px;
	}

	.user-btn .user-info {
		display: flex;
		flex-direction: column;
		box-sizing: border-box;
	}

	.user-btn .user-info .user-name {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
		font-weight: bold;
		color: white;
		margin: 0;
	}

	.user-btn .user-info .user-role {
		font-size: 12px;
		color: #d9d9d9;
		margin: 0;
	}

	.user-dropdown-menu {
		display: block;
		position: absolute;
		background-color: #256395;
		border: 1px solid #0e3b6b;
		border-radius: 0 0 5px 5px;
		top: 65px;
		right: 20px;
		box-sizing: border-box;
	}

	.user-dropdown-menu button,
	.user-dropdown-menu a {
		display: block;
		padding: 8px;
		text-decoration: none;
		background-color: #256395;
		color: white;
		transition:
			background-color 0.3s ease,
			color 0.3s ease;
		box-sizing: border-box;
	}

	.user-dropdown-menu button:hover,
	.user-dropdown-menu a:hover {
		background-color: #0e3b6b;
		color: white;
		cursor: pointer;
	}
</style>
