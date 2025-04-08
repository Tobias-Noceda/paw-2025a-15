package ar.edu.itba.paw.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;

@Service
public class DoctorDetailServiceImpl implements DoctorDetailService{

    private final DoctorDetailDao doctorDetailDao;

    @Autowired
    public DoctorDetailServiceImpl(final DoctorDetailDao doctorDetailDao){
        this.doctorDetailDao = doctorDetailDao;
    }

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

    private List<DoctorView> getFilteredDoctorsSpecialty(SpecialtyEnum specialty){
        List<DoctorView> doctorList = getAllDoctors();
        doctorList.stream().filter(doctorView -> doctorView.getSpecialty().getName().equals(specialty.getName()));
        return doctorList;
    }

    private List<DoctorView> getFilteredDoctorsInsurance(Insurance insurance){
        List<DoctorView> doctorList = getAllDoctors();
        doctorList.stream().filter(doctorView -> doctorView.getInsurances().contains(insurance));
        return doctorList;
    }

    private  List<DoctorView> getFilteredDoctorsWeekday(WeekdayEnum weekday){
        List<DoctorView> doctorList = getAllDoctors();
        doctorList.stream().filter(doctorView -> doctorView.getWeekdays().contains(weekday));
        return doctorList;
    }

    @Override
    public List<DoctorView> getFilteredDoctor(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday){
        List<DoctorView> specialtyFiltered = getFilteredDoctorsSpecialty(specialty);
        List<DoctorView> insuranceFiltered = getFilteredDoctorsInsurance(insurance);
        List<DoctorView> weekdayFiltered = getFilteredDoctorsWeekday(weekday);

        specialtyFiltered.retainAll(insuranceFiltered);
        specialtyFiltered.retainAll(weekdayFiltered);

        return specialtyFiltered;
    }

}
