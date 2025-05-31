package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorDetailDao {
    public Doctor createDoctor(String email, String password, String name, String telephone, File picture, LocaleEnum locale, String licence, SpecialtyEnum specialty, List<Insurance> insurances);
    
    public void updateDoctor(Doctor doctor, String phoneNumber, File picture, LocaleEnum mailLanguage, List<Insurance> insurances);

    public Optional<Doctor> getDoctorById(long id);

    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize);

    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday);

    public List<Patient> searchAuthPatientsPageByDoctorAndName(Doctor doctor, String name, int page, int pageSize);

    public int searchAuthPatientsCountByDoctorAndName(Doctor doctor, String name);
}
