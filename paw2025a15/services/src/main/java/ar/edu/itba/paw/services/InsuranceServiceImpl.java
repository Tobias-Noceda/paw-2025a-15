package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.Insurance;

@Service
public class InsuranceServiceImpl implements InsuranceService{

    @Autowired
    private InsuranceDao insuranceDao;

    @Autowired
    private FileService fs;

    @Transactional
    @Override
    public Insurance create(String name, long pictureId) {
        if(!getInsuranceByName(name).isPresent() && fs.findById(pictureId).isPresent()) return insuranceDao.create(name, pictureId);
        return null;
    }

    @Transactional
    @Override
    public void edit(long id, String name, long pictureId) {
        if(getInsuranceById(id).isPresent() && !getInsuranceByName(name).isPresent() && fs.findById(pictureId).isPresent()) insuranceDao.edit(id, name, pictureId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Insurance> getInsuranceById(long id) {
        return insuranceDao.getInsuranceById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Insurance> getInsuranceByName(String name) {
        return insuranceDao.getInsuranceByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getAllInsurances() {
        return insuranceDao.getAllInsurances();
    }

}
