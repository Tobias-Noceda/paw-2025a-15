package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private final DoctorShiftDao doctorShiftDao;

    @Autowired
    public DoctorShiftServiceImpl(final DoctorShiftDao doctorShiftDao){
        this.doctorShiftDao = doctorShiftDao;
    }

    @Override
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, int amount, String range) {
        return doctorShiftDao.create(doctorId, weekday, address, amount, range);
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return doctorShiftDao.getShiftById(id);
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorId(long doctorId) {
        return doctorShiftDao.getShiftsByDoctorId(doctorId);
    }
    

}
