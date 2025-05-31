package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;

public interface InsuranceDao {
    public Insurance create(String name, File picture);
    
    public Optional<Insurance> getInsuranceById(long id);

    public Optional<Insurance> getInsuranceByName(String name);

    public List<Insurance> getAllInsurances();
}
