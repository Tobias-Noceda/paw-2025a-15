package ar.edu.itba.paw.interfaces.services;

import java.util.Optional;

import ar.edu.itba.paw.models.Insurance;

public interface InsuranceService {
    public Insurance create(String name);

    public Optional<Insurance> getInsuranceById(long id);
}
