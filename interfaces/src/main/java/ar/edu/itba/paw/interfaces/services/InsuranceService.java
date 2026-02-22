package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;

public interface InsuranceService {
    
    public Insurance create(String name, File picture);

    public void edit(long id, String name, Long pictureId);

    public Optional<Insurance> getInsuranceById(long id);

    public Optional<Insurance> getInsuranceByName(String name);

    public int getInsurancesByDoctorIdCount(long doctorId);
    
    public List<Insurance> getInsurancesByDoctorIdPage(long doctorId, int page, int pageSize);

    public List<Insurance> searchInsurancesByNamePage(String name, int page, int pageSize);

    public int searchInsurancesByNameCount(String name);

    public void delete(long id);
}
