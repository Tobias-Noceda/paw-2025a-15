export type Languages = 'es' | 'en';

export type Paginated<T> = {
	_links?: {
        first?: string;
		prev?: string;
		next?: string;
        last?: string;
	};
	results: T[];
};

export type Session = {
	session: string;
	refresh: string;
};
