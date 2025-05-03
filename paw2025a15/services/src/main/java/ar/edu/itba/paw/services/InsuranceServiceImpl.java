package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;

@Service
public class InsuranceServiceImpl implements InsuranceService{

    @Autowired
    private final InsuranceDao insuranceDao;

    @Autowired
    private FileService fs;

    @Autowired
    public InsuranceServiceImpl(final InsuranceDao insuranceDao){
        this.insuranceDao = insuranceDao;
    }

    @Override
    public Insurance create(String name, long pictureId) {
        return insuranceDao.create(name, pictureId);
    }

    @Override
    public void edit(long id, String name, long pictureId) {
        if(getInsuranceById(id).isPresent()){
            insuranceDao.edit(id, name, pictureId);
        }
    }

    @Override
    public Optional<Insurance> getInsuranceById(long id) {
        return insuranceDao.getInsuranceById(id);
    }

    @Override
    public List<Insurance> getAllInsurances() {
        return insuranceDao.getAllInsurances();
    }

    @Override
    public Optional<File> getInsurancePicture(long id) {
        Insurance insurance = insuranceDao.getInsuranceById(id).orElse(null);
        if (insurance == null) return Optional.empty();

        return fs.findById(insurance.getPictureId());
    }

}
