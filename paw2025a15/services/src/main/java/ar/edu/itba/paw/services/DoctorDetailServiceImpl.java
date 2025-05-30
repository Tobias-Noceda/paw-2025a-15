package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.FileService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;
import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDetailServiceImpl.class);

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Autowired
    private UserService us;

    @Autowired
    private FileService fs;

    @Transactional//TODO: no llama al dao, medio raro que este aca
    @Override
    public Doctor createDoctor(String email, String password, String name, String telephone, String doctorLicense, SpecialtyEnum specialty, LocaleEnum locale) {
        if(us.getUserByEmail(email).isPresent()) throw new AlreadyExistsException("User with email: " + email + " already exists!");
        File picture = fs.findById(1).orElseThrow(() -> new NotFoundException("Default picture not found!"));
        return us.createDoctor(email, password, name, telephone, picture, locale, doctorLicense, specialty);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return doctorDetailDao.getDetailByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public void createDoctorCoverages(long doctorId, List<Long> insurances) {
        if(getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with userId: " + doctorId + " does not exist!");
        List<Insurance> currentInsurances = doctorDetailDao.getDoctorInsurancesById(doctorId);
        if(currentInsurances != null && !currentInsurances.isEmpty()) throw new AlreadyExistsException("Doctor with userId: " + doctorId + " already has insurances created!");
        int[] added = doctorDetailDao.addDoctorCoverages(doctorId, insurances);
        for (int i = 0; i < added.length; i++) {
            if (added[i] > 0) {
                LOGGER.info("Added insuranceId: {} for doctorId: {}", insurances.get(i), doctorId);
            } else {
                LOGGER.error("Failed to add insuranceId: {} for doctorId: {} at {}", insurances.get(i), doctorId, LocalDateTime.now());
                throw new RuntimeException("Failed to add insuranceId: " + insurances.get(i) + "as doctor coverage for userId: " + doctorId);
            }
        }
    }

    @Transactional
    @Override
    public void updateDoctorCoverages(long doctorId, final List<Long> insurancesIds) {
        if(getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with userId: " + doctorId + " does not exist!");
        if(insurancesIds == null || insurancesIds.isEmpty()) {
            doctorDetailDao.removeAllCoveragesForDoctorId(doctorId);
            LOGGER.info("Removed all insurances for doctorId: {}", doctorId);
            return;
        }
        List<Insurance> currentInsurances = doctorDetailDao.getDoctorInsurancesById(doctorId);
        if(currentInsurances == null || currentInsurances.isEmpty()){
            int[] added = doctorDetailDao.addDoctorCoverages(doctorId, insurancesIds);
            for (int i = 0; i < added.length; i++) {
                if (added[i] > 0) {
                    LOGGER.info("Added insuranceId: {} for doctorId: {}", insurancesIds.get(i), doctorId);
                } else {
                    LOGGER.error("Failed to add insuranceId: {} for doctorId: {} at {}", insurancesIds.get(i), doctorId, LocalDateTime.now());
                    throw new RuntimeException("Failed to add insuranceId: " + insurancesIds.get(i) + "as doctor coverage for userId: " + doctorId);
                }
            }
            return;
        }
        List<Long> toRemove = new ArrayList<>();
        List<Long> newInsurances = new ArrayList<>(insurancesIds);
        for (Insurance currentInsurance : currentInsurances) {
            if(insurancesIds.contains(currentInsurance.getId())) newInsurances.remove(currentInsurance.getId());
            else toRemove.add(currentInsurance.getId());
        }
        doctorDetailDao.removeDoctorCoverages(doctorId, toRemove);
        int[] added = doctorDetailDao.addDoctorCoverages(doctorId, newInsurances);
        for (int i = 0; i < added.length; i++) {
            if (added[i] > 0) {
                LOGGER.info("Added insuranceId: {} for doctorId: {}", newInsurances.get(i), doctorId);
            } else {
                LOGGER.error("Failed to add insuranceId: {} for doctorId: {} at {}", newInsurances.get(i), doctorId, LocalDateTime.now());
                throw new RuntimeException("Failed to add insuranceId: " + newInsurances.get(i) + "as doctor coverage for userId: " + doctorId);
            }
        }
        LOGGER.info("Updated insurances for doctorId: {}", doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getDoctorInsurancesById(long doctorId) {
        return doctorDetailDao.getDoctorInsurancesById(doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize) {
        return doctorDetailDao.getDoctorsPageByParams(name, specialty, insuranceId, weekday, orderBy, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday) {
        return doctorDetailDao.getTotalDoctorsByParams(name, specialty, insuranceId, weekday);
    }
}