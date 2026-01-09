import type { Specialties } from "./enums/specialties";
import type { Weekdays } from "./enums/weekdays";

export type Languages = 'es' | 'en';

export type Paginated<T> = {
	_links: {
        first?: string;
		prev?: string;
		next?: string;
        last?: string;
	};
	_pageInfo?: {
		currentPage: number;
		totalPages: number;
	};
	results: T[];
};

export type Session = {
	session: string;
	refresh: string;
};

export type Doctor = {
	name: string;
	image: string;
	email: string;
	phone: string;
	license: string;
	specialty: Specialties;
	schedule: string;
	insurances: string;
	scheduleDays?: Set<Weekdays>;
	insuranceNames?: string[];
}

export type Shift = {
	address: string;
	startTime: string;
	endTime: string;
	durationMinutes: number;
	weekday: string;
	self: string;
	doctor: string;
}

export type Insurance = {
	name: string;
	picture: string;
	self: string;
}