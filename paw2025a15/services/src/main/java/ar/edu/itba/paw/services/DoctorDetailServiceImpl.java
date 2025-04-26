package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.SpecialtyEnum;

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
            doctorDetailDao.unauthDoctor(patientId, doctorId);
        }
        else{
            doctorDetailDao.authDoctor(patientId, doctorId);
        }
    }

    @Override
    public boolean hasAuthDoctor(long patientId, long doctorId) {
        return doctorDetailDao.hasAuthDoctor(patientId, doctorId);
    }
}
