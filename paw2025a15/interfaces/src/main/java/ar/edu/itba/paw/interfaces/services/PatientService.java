package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.Patient;

public interface PatientService {
    public Patient create(String email, String password, String name, long pictureId);

    public Optional<Patient> getPatientById(long id);
}
