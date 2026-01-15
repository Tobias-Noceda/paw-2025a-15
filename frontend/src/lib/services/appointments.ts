import { baseApiUrl, type Appointment } from "$types/api";

export const fetchFreeAppointments = async (
    doctorId: number,
    date: string
): Promise<Appointment[]> => {
    
    let appointments: Appointment[] = [];
    try {
        let url = new URL(`${baseApiUrl}/appointments`);
        url.searchParams.append('doctorId', doctorId.toString());
        url.searchParams.append('date', date);
        url.searchParams.append('status', 'Free');

        const response = await fetch(url.toString());
        if (response.ok) {
            appointments = await response.json();
        }
    } catch (error) {
        console.error('Failed to fetch appointments:', error);
    }

    return appointments;
};