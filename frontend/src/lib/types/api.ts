import type { Specialties } from "./enums/specialties";
import type { Weekdays } from "./enums/weekdays";

export const baseApiUrl = 'http://localhost:8080/paw-2025a-15/api'

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
	telephone: string;
	license: string;
	specialty: Specialties;
	schedule: string;
	insurances: string;
	self: string;
	scheduleDays?: Map<Weekdays, [Date, Date]>;
	direction?: string;
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
