package ar.edu.itba.paw.interfaces.persistence;

import java.util.Optional;

import ar.edu.itba.paw.models.Insurance;

public interface InsuranceDao {
    public Insurance create(String name);

    public Optional<Insurance> getInsuranceById(long id);
}
