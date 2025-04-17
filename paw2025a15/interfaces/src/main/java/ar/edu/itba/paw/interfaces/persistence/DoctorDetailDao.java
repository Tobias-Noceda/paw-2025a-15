package ar.edu.itba.paw.interfaces.persistence;

import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.DoctorDetail;
import ar.edu.itba.paw.models.DoctorView;
import ar.edu.itba.paw.models.SpecialtyEnum;

public interface DoctorDetailDao {
    public DoctorDetail create(long doctorId, String licence, SpecialtyEnum specialty);

    public Optional<DoctorDetail> getDetailByDoctorId(long doctorId);

    public List<DoctorView> getAllDoctors();

    public List<DoctorView> findDoctorsByName(String name);    

    public List<DoctorView> getAuthDoctorsByPatientId(long id);

    public boolean hasAuthDoctor(long patientId, long doctorId);

    public void authDoctor(long patientId, long doctorId);

    public void unauthDoctor(long patientId, long doctorId);
}
