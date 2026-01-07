package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;

public interface InsuranceService {
    public Insurance create(String name, File picture);

    public void edit(long id, String name, File picture);

    public Optional<Insurance> getInsuranceById(long id);

    public Optional<Insurance> getInsuranceByName(String name);

    public List<Insurance> getAllInsurances();
    
    public List<Insurance> getInsurancesByDoctorId(long doctorId);

    public int getInsurancesCount();

    public List<Insurance> getInsurancesPage(int page, int pageSize);

    public void delete(long id);
}
