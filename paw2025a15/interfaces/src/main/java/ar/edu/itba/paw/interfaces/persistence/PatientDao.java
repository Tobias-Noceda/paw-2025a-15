package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.Patient;

public interface PatientDao {
    public Patient create(String email, String password, String name, long pictureId);

    public Optional<Patient> getPatientById(long id);
}