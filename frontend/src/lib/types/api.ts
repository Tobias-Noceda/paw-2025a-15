import type { insurance } from "$stores/filters";
import type { AppointmentStatus } from "./enums/AppointmentStatus";
import type { Specialty } from "./enums/specialties";
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
		currentPage?: number;
		totalPages?: number;
		currentDate?: Date;
		maxDate?: Date;
	};
	results: T[];
};

export type Session = {
	access: string;
	refresh: string;
};

export type Doctor = {
	name: string;
	email: string;
	telephone: string;
	license: string;
	specialty: Specialty;
	schedule?: Map<Weekdays, [Date, Date]>;
	direction?: string;
	insurances?: string[];
	links: {
		image: string;
		insurances: string;
		self: string;
		schedule: string;
		freeAppointments: string;
		futureAppointments: string;
		patients: string;
	}
}

export type Patient = {
	email: string;
	name: string;
	telephone: string;
	birthDate: string;
	bloodType: string;
	height: number;
	weight: number;
	insurance?: string;
	insuranceNumber: string;
	meds?: string | null;
	conditions?: string | null;
	allergies?: string | null;
	smokes?: boolean | null;
	drinks?: boolean | null;
	diet?: string | null;
	hobbies?: string | null;
	job?: string | null;
	links: {
		doctors: string;
		image: string;
		insurance?: string;
		self: string;
		medicalInfo: string;
		habitsInfo: string;
		socialInfo: string;
		pastAppointments: string;
		futureAppointments: string;
	}
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
	links: {
		image: string;
		self: string;
	}
}

export type Appointment = {
	status: AppointmentStatus;
	weekday: string;
	address: string;
	date: string;
	startTime: string;
	endTime: string;
	durationMinutes: number;
	doctor?: Doctor;
	patient?: Patient;
	patientName?: string;
	patientEmail?: string;
	detail?: string;
	links: {
		self: string;
		doctor: string;
		patient: string;
	}
};
