package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorService {
    public void deleteDoctor(long doctorId);

    public Doctor createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum specialty, List<Long> insurances, LocaleEnum locale);

    public Optional<Doctor> getDoctorById(long id);

    public boolean licenceExists(String licence);

    public void updateDoctor(Doctor doctor, String phoneNumber, File picture, LocaleEnum mailLanguage, List<Long> insurances);
    
    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize);

    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday);

    public List<Patient> getAuthPatientsPageByDoctorIdAndName(long doctorId, String name, int page, int pageSize);

    public int getAuthPatientsCountByDoctorIdAndName(long doctorId, String name);

    public DoctorVacation createDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate);

    public void deleteDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate);

    public List<DoctorVacation> getDoctorVacationsPast(long doctorId);

    public List<DoctorVacation> getDoctorVacationsFuture(long doctorId);

    public boolean vacationExists(long doctorId, LocalDate startDate, LocalDate endDate);
}
