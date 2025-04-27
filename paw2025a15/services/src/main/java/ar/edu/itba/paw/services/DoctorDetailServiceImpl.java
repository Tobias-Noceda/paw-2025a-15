package ar.edu.itba.paw.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService{

    @Autowired
    private DoctorDetailDao doctorDetailDao;

    @Override
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty) {
        return doctorDetailDao.create(doctorId, licence, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return doctorDetailDao.getDetailByDoctorId(doctorId);
    }

    @Override
    public List<DoctorView> getAllDoctors() {
        return doctorDetailDao.getAllDoctors();
    }

    @Override
    public List<DoctorView> findDoctorsByName(String name) {
        return doctorDetailDao.findDoctorsByName(name);
    }

    @Override
    public List<DoctorView> getFilteredDoctor(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday){
        return doctorDetailDao.getFilteredDoctor(specialty, insurance, weekday);
    }

    @Override
    public List<DoctorView> getAuthDoctorsByPatientId(long id) {
        return doctorDetailDao.getAuthDoctorsByPatientId(id);
    }

    @Override
    public void toggleAuthDoctor(long patientId, long doctorId) {
        if(hasAuthDoctor(patientId, doctorId)){
            doctorDetailDao.unauthDoctorAllAccessLevels(patientId, doctorId);
        }
        else{
            doctorDetailDao.authDoctor(patientId, doctorId, AccessLevelEnum.VIEW_RESTRICTED);
        }
    }

    @Override
    public void authDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        for (AccessLevelEnum accessLevel: accessLevels) {
            doctorDetailDao.authDoctor(patientId, doctorId, accessLevel);
        }
    }

    @Override
    public void unauthDoctorWithLevels(long patientId, long doctorId, List<AccessLevelEnum> accessLevels){
        if(accessLevels.contains(AccessLevelEnum.VIEW_RESTRICTED)){
            doctorDetailDao.unauthDoctorAllAccessLevels(patientId, doctorId);
            return;
        }
        for (AccessLevelEnum accessLevel: accessLevels) {
            doctorDetailDao.unauthDoctorByAccessLevel(patientId, doctorId, accessLevel);
        }
    }

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        return doctorDetailDao.hasAuthDoctor(patientId, doctorId);
    }

    @Override
    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel) {
        return doctorDetailDao.hasAuthDoctorWithAccessLevel(patientId, doctorId, accessLevel);
    }

    @Override
    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels) {
        List<AccessLevelEnum> toRemove = new ArrayList<>();
        for (AccessLevelEnum currentAccessLevel : getAuthAccessLevelEnums(patientId, doctorId)) {
            if(currentAccessLevel!=AccessLevelEnum.VIEW_RESTRICTED && !accessLevels.contains(currentAccessLevel)) toRemove.add(currentAccessLevel);
        }
        unauthDoctorWithLevels(patientId, doctorId, toRemove);
        authDoctorWithLevels(patientId, doctorId, accessLevels);
    }

    @Override
    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId) {
        return doctorDetailDao.getAuthAccessLevelEnums(patientId, doctorId);
    }
    
}
