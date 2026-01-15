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

            await Promise.all(appointments.map(async (appointment) => {
                await populateAppointmentData(appointment);
            }));
        }
    } catch (error) {
        console.error('Failed to fetch appointments:', error);
    }

    return appointments;
};

const populateAppointmentData = async (appointment: Appointment): Promise<void> => {
    try {
        const response = await fetch(appointment.doctor);
        if (response.ok) {
            const doctorData = await response.json();
            appointment.doctorData = doctorData;
        }
    } catch (error) {
        console.error('Failed to populate appointment data:', error);
    }
    
    return;
}