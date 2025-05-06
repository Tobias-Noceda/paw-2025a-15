package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
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
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.UserRoleEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorDetailServiceImpl.class);

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Autowired
    private InsuranceService is;

    @Autowired
    private UserService us;

    @Transactional
    @Override
    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum specialty, LocaleEnum locale) {
        if(us.getUserByEmail(email).isPresent()) throw new IllegalArgumentException("User with email: " + email + " already exists!");
        User doc = us.create(email, password, name, telephone, UserRoleEnum.DOCTOR, locale);
        long doctorId = doc.getId();
        DoctorDetail dd = doctorDetailDao.create(doctorId, licence, specialty);
        if(dd == null){
            LOGGER.error("Failed to create doctor details for userId: {} at {}", doctorId, LocalDateTime.now());
            throw new RuntimeException("Failed to create doctor details for userId: " + doctorId);
        }
        LOGGER.info("Successfully created doctor details for userId: {}", doctorId);
        LOGGER.info("Successfully created doctor user with email: {}", email);
        return doc;
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
    
}
