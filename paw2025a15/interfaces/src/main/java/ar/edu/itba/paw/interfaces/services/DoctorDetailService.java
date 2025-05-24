package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.entities.DoctorDetail;
import ar.edu.itba.paw.models.entities.Insurance;
import ar.edu.itba.paw.models.entities.User;
import ar.edu.itba.paw.models.enums.LocaleEnum;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorDetailService {

    public User createDoctor(String email, String password, String name, String telephone, String licence, SpecialtyEnum specialty, LocaleEnum locale);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);

    public void createDoctorCoverages(long doctorId, List<Long> insurances);

    public void updateDoctorCoverages(long doctorId, final List<Long> insurances);

    public List<Insurance> getDoctorInsurancesById(long doctorId);

    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize);

    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday);

}
