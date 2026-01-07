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
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
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
    public Insurance create(String name, File picture) {
        if(getInsuranceByName(name).isPresent()) throw new AlreadyExistsException("Insurance with name: " + name + " already exists!");
        if(fs.findById(picture.getId()).isEmpty()) throw new NotFoundException("Logo with id: " + picture.getId() + " does not exist!");
        Insurance insurance = insuranceDao.create(name, picture);
        if(insurance == null){
            LOGGER.error("Failed to create insurance: {} at {}", name, LocalDateTime.now());
            throw new RuntimeException("Failed to create insurance: " + name);
        }
        LOGGER.info("Successfully created insurance: {}", name);
        return insurance;
    }

    @Transactional
    @Override
    public void edit(long id, String name, File picture) {
        if(name==null) throw new IllegalArgumentException("Insurance name cannot be null");
        Insurance insurance = getInsuranceById(id).orElseThrow(() -> new NotFoundException("Insurance with id: " + id + " does not exist!"));
        if((!name.equals(insurance.getName())) && (getInsuranceByName(name).isPresent())) throw new AlreadyExistsException("Insurance with name: " + name + " already exists!");
        fs.findById(picture.getId()).orElseThrow(() -> new NotFoundException("Logo with id: " + picture.getId() + " does not exist!"));
        insurance.setName(name);
        insurance.setPicture(picture);
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

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getInsurancesByDoctorId(long doctorId) {
        return insuranceDao.getInsurancesByDoctorId(doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public int getInsurancesCount() {
        return insuranceDao.getInsurancesCount();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getInsurancesPage(int page, int pageSize) {
        return insuranceDao.getInsurancesPage(page, pageSize);
    }

    @Transactional
    @Override
    public void delete(long id) {
        Optional<Insurance> insurance = getInsuranceById(id);
        if(insurance.isEmpty()) return;
        insuranceDao.delete(insurance.get());
        LOGGER.info("Deleted insurance with id: {}", id);
    }
}
