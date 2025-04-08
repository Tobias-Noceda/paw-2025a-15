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

    private List<DoctorView> getFilteredDoctorsSpecialty(SpecialtyEnum specialty){
        List<DoctorView> doctorList = getAllDoctors();

        List<DoctorView> filteredDoctors = new ArrayList<>();
        for (DoctorView doctor : doctorList) {
            if (doctor.getSpecialty().equals(specialty) ){
                filteredDoctors.add(doctor);
            }
        }

        return filteredDoctors;
    }

    private List<DoctorView> getFilteredDoctorsInsurance(Insurance insurance){
        List<DoctorView> doctorList = getAllDoctors();
        List<DoctorView> filteredDoctors = new ArrayList<>();
        for (DoctorView doctor : doctorList) {
            if (doctor.getInsurances().contains(insurance)){
                filteredDoctors.add(doctor);
            }
        }
        return filteredDoctors;
    }

    private  List<DoctorView> getFilteredDoctorsWeekday(WeekdayEnum weekday){
        List<DoctorView> doctorList = getAllDoctors();
        List<DoctorView> filteredDoctors = new ArrayList<>();
        for (DoctorView doctor : doctorList) {
            if (doctor.getWeekdays().contains(weekday)){
                filteredDoctors.add(doctor);
            }
        }
        return filteredDoctors;
    }

    @Override
    public List<DoctorView> getFilteredDoctor(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday){
        List<DoctorView> specialtyFiltered;
        List<DoctorView> insuranceFiltered;
        List<DoctorView> weekdayFiltered;

        if(specialty == null){
            specialtyFiltered = getAllDoctors();
        }else{
            specialtyFiltered = getFilteredDoctorsSpecialty(specialty);
        }

        System.out.println(specialtyFiltered);

        if(insurance == null){
            insuranceFiltered = getAllDoctors();
        }else{
            insuranceFiltered = getFilteredDoctorsInsurance(insurance);
        }

        System.out.println(insuranceFiltered);

        if(weekday == null){
            weekdayFiltered = getAllDoctors();
        }else{
            weekdayFiltered = getFilteredDoctorsWeekday(weekday);
        }

        System.out.println(weekdayFiltered);

        specialtyFiltered.retainAll(insuranceFiltered);
        specialtyFiltered.retainAll(weekdayFiltered);

        return specialtyFiltered;
    }

}
