import { post } from "$modules/api.svelte";
import { baseApiUrl, type Patient } from "$types/api";

export const createPatient = async (patient: Partial<Patient>, password: string): Promise<void> => {
    const response = await post(`${baseApiUrl}/patients`, 
        {
            name: patient.name,
            email: patient.email,
            password: password,
            telephone: patient.telephone,
            height: patient.height,
            weight: patient.weight,
            birthDate: patient.birthDate,
        },
        {
            headers: {
                'Content-Type': 'application/vnd.patients.creation.v1+json'
            }
        },
        fetch
    );
    if (!response.ok) {
        throw new Error("Failed to create patient");
    }
};
