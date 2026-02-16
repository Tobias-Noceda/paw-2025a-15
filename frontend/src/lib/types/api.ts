import type { AccessLevels } from "./enums/accessLevels";
import type { AppointmentStatus } from "./enums/appointmentStatus";
import type { FileTypes } from "./enums/fileTypes";
import type { Specialty } from "./enums/specialties";
import type { StudyType } from "./enums/studyTypes";
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

export type UriTemplate = {
	href: string;
	templated: boolean;
	resolved?: string;
};

export type Session = {
	access: string;
	refresh: string;
};

export type DoctorAuthorizations = {
	authorized: boolean;
	accessLevels: AccessLevels[];
}

export type Doctor = {
	name: string;
	email: string;
	telephone: string;
	license: string;
	specialty: Specialty;
	address?: string;
	startTime?: string;
	endTime?: string;
	duration?: number;
	schedule?: Map<Weekdays, [Date, Date]>;
	insurances?: string[];
	links: {
		image: UriTemplate;
		insurances: UriTemplate;
		self: UriTemplate;
		schedule: UriTemplate;
		freeAppointments: UriTemplate;
		futureAppointments: UriTemplate;
		patients: UriTemplate;
		authorization: UriTemplate;
		pastVacations: UriTemplate;
		futureVacations: UriTemplate;
	}
}

export type Patient = {
	email: string;
	name: string;
	telephone: string;
	birthdate: string;
	bloodType: string;
	height: number;
	weight: number;
	insurance?: string;
	insuranceNumber: string;
	gaveHabits?: boolean;
	smokes?: boolean;
	drinks?: boolean;
	diet?: string;
	gaveMedical?: boolean;
	meds?: string;
	conditions?: string;
	allergies?: string;
	gaveSocial?: boolean;
	hobbies?: string;
	job?: string;
	studiesPage?: Paginated<Study>;
	links: {
		doctors: UriTemplate;
		image: UriTemplate;
		insurance?: UriTemplate;
		self: UriTemplate;
		habitsInfo: UriTemplate;
		medicalInfo: UriTemplate;
		socialInfo: UriTemplate;
		pastAppointments: UriTemplate;
		futureAppointments: UriTemplate;
		studies: UriTemplate;
	}
}

export type Shift = {
	address: string;
	startTime: string;
	endTime: string;
	duration: number;
	weekday: string;
	self: string;
	doctor: string;
}

export type Insurance = {
	name: string;
	links: {
		image: UriTemplate;
		self: UriTemplate;
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
		self: UriTemplate;
		doctor: UriTemplate;
		patient: UriTemplate;
	}
};

export type File = {
	type: FileTypes;
	links: {
		self: UriTemplate;
	}
};
export type Study = {
	comment: string;
	studyDate: string;
	type: StudyType;
	uploadDate: string;
	uploaderName?: string;
	files?: Paginated<File>;
	links: {
		authDoctors: UriTemplate;
		files: UriTemplate;
		patient: UriTemplate;
		self: UriTemplate;
		uploader: UriTemplate;
	}
};

export type Vacations = {
	startDate: string;
	endDate: string;
	links: {
		self: UriTemplate;
		doctor: UriTemplate;
	}
};
