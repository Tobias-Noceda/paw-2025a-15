package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.InsuranceDao;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class InsuranceServiceImpl implements InsuranceService{

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(InsuranceServiceImpl.class);

    @Autowired
    private InsuranceDao insuranceDao;

    @Autowired
    private FileService fs;

    @Transactional
    @Override
    public Insurance create(String name, long pictureId) {
        if(getInsuranceByName(name).isPresent()) throw new AlreadyExistsException("Insurance with name: " + name + " already exists!");
        if(fs.findById(pictureId).isEmpty()) throw new NotFoundException("Logo with id: " + pictureId + " does not exist!");
        Insurance insurance = insuranceDao.create(name, pictureId);
        if(insurance == null){
            LOGGER.error("Failed to create insurance: {} at {}", name, LocalDateTime.now());
            throw new RuntimeException("Failed to create insurance: " + name);
        }
        LOGGER.info("Successfully created insurance: {}", name);
        return insurance;
    }

    @Transactional
    @Override
    public void edit(long id, String name, long pictureId) {
        if(getInsuranceById(id).isEmpty()) throw new NotFoundException("Insurance with id: " + id + " does not exist!");
        if(getInsuranceByName(name).isPresent()) throw new AlreadyExistsException("Insurance with name: " + name + " already exists!");
        if(fs.findById(pictureId).isEmpty()) throw new NotFoundException("Logo with id: " + pictureId + " does not exists!");
        insuranceDao.edit(id, name, pictureId);
        LOGGER.info("Edited insurance information for insurance with id: {}", id);
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

    @Override
    public Optional<File> getInsurancePicture(long id) {
        Insurance insurance = insuranceDao.getInsuranceById(id).orElseThrow(() -> new NotFoundException("Insurance with id: " + id + " does not exist!"));

        return fs.findById(insurance.getPictureId());
    }
}
