package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.Insurance;

public interface InsuranceDao {
    public Insurance create(String name, long pictureId);

    public void edit(long id, String name, long pictureId);
    
    public Optional<Insurance> getInsuranceById(long id);

    public List<Insurance> getAllInsurances();
}
