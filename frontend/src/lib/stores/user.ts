import { writable } from "svelte/store";

let user = writable<string | null>("doctor");

export { user };