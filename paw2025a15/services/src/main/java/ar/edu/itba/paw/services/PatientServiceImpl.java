package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.PatientDao;
import ar.edu.itba.paw.interfaces.services.PatientService;
import ar.edu.itba.paw.models.Patient;

@Service
public class PatientServiceImpl implements PatientService{

    private final PatientDao patientDao;

    @Autowired
    public PatientServiceImpl(final PatientDao patientDao){
        this.patientDao = patientDao;
    }

    @Override
    public Patient create(String email, String password, String name, long pictureId) {
        return patientDao.create(email, password, name, pictureId);
    }

    @Override
    public Optional<Patient> getPatientById(long id) {
        return patientDao.getPatientById(id);
    }

}
