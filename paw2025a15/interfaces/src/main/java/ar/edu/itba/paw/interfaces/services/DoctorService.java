package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Schedule;

public interface DoctorService {
    
    Doctor create(final String name, final String email, final List<String> workingEnsurances, final String specialty, final Schedule schedules);

    Optional<Doctor> findById(long id);
}
