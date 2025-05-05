package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.InsuranceService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDetailServiceImpl.class);

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Autowired
    private InsuranceService is;

    @Transactional
    @Override
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty) {
        //TODO: same as in pds, check existent doctor
        if(getDetailByDoctorId(doctorId).isPresent()) throw new IllegalArgumentException("Doctor detail for userId: " + doctorId + " already exists!");
        DoctorDetail dd = doctorDetailDao.create(doctorId, licence, specialty);
        if(dd == null){
            LOGGER.error("Failed to create doctor details for userId: {} at {}", doctorId, LocalDateTime.now());
            throw new RuntimeException("Failed to create doctor details for userId: " + doctorId);
        }
        LOGGER.info("Successfully created doctor details for userId: {}", doctorId);
        return dd;
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return doctorDetailDao.getDetailByDoctorId(doctorId);
    }

    @Transactional
    @Override
    public void createDoctorCoverages(long doctorId, List<Long> insurances) {
        if(!getDetailByDoctorId(doctorId).isPresent()) throw new IllegalArgumentException("Doctor with userId: " + doctorId + " does not exist!");
        List<Insurance> currentInsurances = doctorDetailDao.getDoctorInsurancesById(doctorId);
        if(currentInsurances != null && !currentInsurances.isEmpty()) throw new IllegalArgumentException("Doctor with userId: " + doctorId + " already has insurances created!");
        for (Long insuranceId : insurances) {
            if(!is.getInsuranceById(insuranceId).isPresent()) throw new IllegalArgumentException("Insurance with insuranceId: " + insuranceId + " does not exist!");
            doctorDetailDao.addDoctorCoverage(doctorId, insuranceId);
            LOGGER.info("Adding insurance with insuranceId:{} for doctor with userId: {}", insuranceId, doctorId);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<Insurance> getDoctorInsurancesById(long doctorId) {
        return doctorDetailDao.getDoctorInsurancesById(doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday, int page, int pageSize) {
        return doctorDetailDao.getDoctorsPageByParams(name, specialty, insuranceId, weekday, page, pageSize);
    }

    @Transactional(readOnly = true)
    @Override
    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday) {
        return doctorDetailDao.getTotalDoctorsByParams(name, specialty, insuranceId, weekday);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        //TODO:check after refactor with us the existance of patientId
        return doctorDetailDao.getAuthDoctorsByPatientId(id);
    }

    @Transactional
    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {//TODO: tests of all auth related after its in its own file
        //TODO:check after refactor with us the existance of patientId and docId
        if(hasAuthDoctor(patientId, doctorId)){
            doctorDetailDao.unauthDoctorAllAccessLevels(patientId, doctorId);
        }
        else{
            doctorDetailDao.authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_BASIC);
        }
    }

    private void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        //TODO:check after refactor with us the existance of patientId and docId
        if(accessLevels==null || accessLevels.isEmpty()) return;
        for (AccessLevelEnum accessLevel: accessLevels) {
            doctorDetailDao.authDoctor(patientId, doctorId, accessLevel);
        }
    }

    private void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        //TODO:check after refactor with us the existance of patientId and docId
        if(accessLevels==null || accessLevels.isEmpty()) return;
        if(accessLevels.contains(AccessLevelEnum.VIEW_BASIC)){
            doctorDetailDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            return;
        }
        for (AccessLevelEnum accessLevel: accessLevels) {
            doctorDetailDao.unauthDoctorByAccessLevel(patientId, doctorId, accessLevel);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        //TODO:check after refactor with us the existance of patientId and docId
        return doctorDetailDao.hasAuthDoctor(patientId, doctorId);
    }

    @Transactional
    @Override
    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        //TODO:check after refactor with us the existance of patientId and docId
        List<AccessLevelEnum> toRemove;
        if(accessLevels==null || accessLevels.isEmpty()){
            toRemove = getAuthAccessLevelEnums(patientId, doctorId);
            toRemove.remove(AccessLevelEnum.VIEW_BASIC);
        } else {
            toRemove = new ArrayList<>();
            for (AccessLevelEnum currentAccessLevel : getAuthAccessLevelEnums(patientId, doctorId)) {
                if(currentAccessLevel!=AccessLevelEnum.VIEW_BASIC && !accessLevels.contains(currentAccessLevel)) toRemove.add(currentAccessLevel);
            }
        }
        unauthDoctorWithLevels(patientId, doctorId, toRemove);
        authDoctorWithLevels(patientId, doctorId, accessLevels);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        //TODO:check after refactor with us the existance of patientId and docId
        return doctorDetailDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
    
}
