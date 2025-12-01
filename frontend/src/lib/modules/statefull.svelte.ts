import type { NavigationTarget } from '@sveltejs/kit';

const history = $state({}) as Record<string, Record<string, unknown>>;

export function preserve(
	from: NavigationTarget | null,
	to: NavigationTarget | null,
	data: unknown
) {
	if (!from?.route.id || !to?.route.id) {
		return undefined;
	}

	history[to.route.id] ??= {};
	history[to.route.id][from.route.id] = data;
}

export function restore<T>(from: NavigationTarget | null, to: NavigationTarget | null) {
	if (!from?.route.id || !to?.route.id) {
		return undefined;
	}

	return history[from.route.id]?.[to.route.id] as T | undefined;
}
