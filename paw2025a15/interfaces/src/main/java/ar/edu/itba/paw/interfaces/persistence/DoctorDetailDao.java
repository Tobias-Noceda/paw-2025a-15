package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.SpecialtyEnum;
import ar.edu.itba.paw.models.WeekdayEnum;
import ar.edu.itba.paw.models.Insurance;

public interface DoctorDetailDao {
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);

    public List<DoctorView> getAllDoctors();

    public List<DoctorView> findDoctorsByName(String name);    

    List<DoctorView> getFilteredDoctor(SpecialtyEnum specialty, Insurance insurance, WeekdayEnum weekday);

    public List<DoctorView> getAuthDoctorsByPatientId(long id);

    public boolean hasAuthDoctor(long patientId, long doctorId);

    public void authDoctor(long patientId, long doctorId);

    public void unauthDoctor(long patientId, long doctorId);
}
