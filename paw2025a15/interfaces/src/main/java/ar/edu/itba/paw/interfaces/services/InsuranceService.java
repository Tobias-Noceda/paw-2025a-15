package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Insurance;

public interface InsuranceService {
    public Insurance create(String name, File picture);

    public void edit(long id, String name, File picture);

    public Optional<Insurance> getInsuranceById(long id);

    public Optional<Insurance> getInsuranceByName(String name);

    public List<Insurance> getAllInsurances();

    public Optional<File> getInsurancePicture(long id);
}
