package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorVacation;
import ar.edu.itba.paw.models.entities.DoctorVacationId;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorDao {
    
    public Doctor createDoctor(String email, String password, String name, String telephone, long pictureId, LocaleEnum locale, String licence, SpecialtyEnum specialty, List<Insurance> insurances);
    
    public void updateDoctor(long doctorId, String telephone, long pictureId, LocaleEnum mailLanguage, List<Insurance> insurances);

    public Optional<Doctor> getDoctorById(long id);

    public List<Doctor> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize);

    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Long insuranceId, WeekdayEnum weekday);

    public List<Patient> searchAuthPatientsPageByDoctorAndName(long doctorId, String name, int page, int pageSize);

    public int searchAuthPatientsCountByDoctorAndName(long doctorId, String name);

    public boolean licenceExists(String licence);

    public DoctorVacation createDoctorVacation(long doctorId, LocalDate startDate, LocalDate endDate);

    public void deleteDoctorVacation(DoctorVacationId dvId);

    public List<DoctorVacation> getDoctorVacationsPast(long doctorId);

    public List<DoctorVacation> getDoctorVacationsFuture(long doctorId);

    public boolean vacationExists(long doctorId, LocalDate startDate, LocalDate endDate);
}
