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
	todaysFreeAppointments: string;
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

export type Appointment = {
	weekday: string;
	address: string;
	date: string;
	startTime: string;
	endTime: string;
	durationMinutes: number;
	self: string;
	doctor: string;
	patient: string;
}

export type Profile = {
	name: string;
	email: string;
	role: string;
	telephone: string;
	mailLanguage: string;
	birthdate?: string;
	bloodtype?: string;
	height?: number;
	weight?: number;
	smokes?: boolean;
	drinks?: boolean;
	meds?: string;
	conditions?: string;
	allergies?: string;
	diet?: string;
	hobbies?: string;
	job?: string;
	insuranceName?: string;
	insuranceId?: number;
	insuranceNumber?: string;
	licence?: string;
	specialty?: string;
	insurances?: string[];
	address?: string;
	links?: {
		self?: string;
		image?: string;
		insurance?: string;
	};
};
