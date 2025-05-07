package ar.edu.itba.paw.interfaces.services;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.DoctorOrderEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorDetailService {
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);

    public List<DoctorView> getDoctorsPageByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday, DoctorOrderEnum orderBy, int page, int pageSize);

    public int getTotalDoctorsByParams(String name, SpecialtyEnum specialty, Insurance insuranceId, WeekdayEnum weekday);

    public List<DoctorView> getAuthDoctorsByPatientId(long id);

    public void toggleAuthDoctor(long patientId, long doctorId);

    public void updateAuthDoctor(long patientId, long doctorId, List<AccessLevelEnum> accessLevels);

    public boolean hasAuthDoctor(long patientId, long doctorId);

    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId);
}
