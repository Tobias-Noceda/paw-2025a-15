package ar.edu.itba.paw.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.models.DoctorDetail;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService{

    private final DoctorDetailDao doctorDetailDao;

    @Autowired
    public DoctorDetailServiceImpl(final DoctorDetailDao doctorDetailDao){
        this.doctorDetailDao = doctorDetailDao;
    }

    @Override
    public DoctorDetail create(long doctorId, String licence, String specialty) {
        return doctorDetailDao.create(doctorId, licence, specialty);
    }

    @Override
    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId) {
        return doctorDetailDao.getDetailByDoctorId(doctorId);
    }

}
