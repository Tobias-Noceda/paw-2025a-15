package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.Insurance;
import ar.edu.itba.paw.models.enums.AccessLevelEnum;
import ar.edu.itba.paw.models.enums.SpecialtyEnum;
import ar.edu.itba.paw.models.enums.WeekdayEnum;

public interface DoctorDetailDao {
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);

    public void addDoctorCoverage(long doctorId, long insuranceId);

    public boolean removeDoctorCoverage(long doctorId, long insuranceId);

    public List<Insurance> getDoctorInsurancesById(long doctorId);

    public List<DoctorView> getDoctorsPage(int page, int pageSize);

    public int getTotalDoctors();

    public List<DoctorView> findDoctorsPageByName(String name, int page, int pageSize);

    public int getTotalDoctorsByName(String name);

    public List<DoctorView> getFilteredDoctorsPage(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday, int page, int pageSize);

    public int getTotalFilteredDoctors(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday);

    public List<DoctorView> getAuthDoctorsByPatientId(long id);

    public boolean hasAuthDoctor(long patientId, long doctorId);

    public boolean hasAuthDoctorWithAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public void authDoctor(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public void unauthDoctorAllAccessLevels(long patientId, long doctorId);

    public void unauthDoctorByAccessLevel(long patientId, long doctorId, AccessLevelEnum accessLevel);

    public List<AccessLevelEnum> getAuthAccessLevelEnums(long patientId, long doctorId);
}
