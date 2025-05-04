package ar.edu.itba.paw.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Autowired
    private InsuranceService is;

    @Override
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty) {
        //TODO: same as in pds, check existent doctor
        return doctorDetailDao.create(doctorId, licence, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return doctorDetailDao.getDetailByDoctorId(doctorId);
    }

    @Override
    public List<DoctorView> getDoctorsPage(int page, int pageSize) {
        return doctorDetailDao.getDoctorsPage(page, pageSize);
    }

    @Override
    public int getTotalDoctors() {
        return doctorDetailDao.getTotalDoctors();
    }

    @Override
    public List<DoctorView> findDoctorsPageByName(String name, int page, int pageSize) {
        //TODO:maybe string input check? before it goes to query or in jdbc?
        return doctorDetailDao.findDoctorsPageByName(name, page, pageSize);
    }

    @Override
    public int getTotalDoctorsByName(String name) {
        //TODO:maybe string input check? before it goes to query or in jdbc?
        return doctorDetailDao.getTotalDoctorsByName(name);
    }

    @Override
    public List<DoctorView> getFilteredDoctorsPage(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday, int page, int pageSize) {
        if(is.getInsuranceById(insurance.getId()).isPresent()) return doctorDetailDao.getFilteredDoctorsPage(specialty, insurance, weekday, page, pageSize);
        else return Collections.emptyList();
    }

    @Override
    public int getTotalFilteredDoctors(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday) {
        if(is.getInsuranceById(insurance.getId()).isPresent()) return doctorDetailDao.getTotalFilteredDoctors(specialty, insurance, weekday);
        else return 0;
    }

    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        //TODO:check after refactor with us the existance of patientId
        return doctorDetailDao.getAuthDoctorsByPatientId(id);
    }

    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
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

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        //TODO:check after refactor with us the existance of patientId and docId
        return doctorDetailDao.hasAuthDoctor(patientId, doctorId);
    }

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

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        //TODO:check after refactor with us the existance of patientId and docId
        return doctorDetailDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
    
}
