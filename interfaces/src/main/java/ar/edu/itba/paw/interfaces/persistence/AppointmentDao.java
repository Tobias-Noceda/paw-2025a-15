package ar.edu.itba.paw.interfaces.persistence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.Patient;

public interface AppointmentDao { //TODO check deprecated
    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail);

    public Optional<AppointmentNew> getAppointmentByShiftDateAndTime(DoctorSingleShift shift, LocalDate date, LocalTime startTime, LocalTime endTime);

    @Deprecated
    public List<AppointmentNew> getFutureAppointmentDataByPatient(Patient patient);

    public List<AppointmentNew> getFutureAppointmentDataPageByPatient(Patient patient, int page, int pageSize);

    public Integer getFutureAppointmentTotalByPatient(Patient patient);

    public List<AppointmentNew> getOldAppointmentDataByPatient(Patient patient);

    public List<AppointmentNew> getOldAppointmentDataPageByPatient(Patient patient, int page, int pageSize);

    public Integer getOldAppointmentTotalByPatient(Patient patient);

    public List<AppointmentNew> getFutureAppointmentDataByDoctor(Doctor doctor);

    public List<AppointmentNew> getFutureAppointmentDataPageByDoctor(Doctor doctor, int page, int pageSize);

    public Integer getFutureAppointmentTotalByDoctor(Doctor doctor);

    public boolean cancelAppointment(DoctorSingleShift shift, LocalDate date, LocalTime startTime, LocalTime endTime);
    
    public List<AppointmentNew> getAppointmentsForDate(LocalDate date);

    public void clearRemovedAppointmentBeforeDate(LocalDate date);

    public void cancelAppointmentRange(long doctorId, LocalDate startDate, LocalDate endDate);

    public List <AppointmentNew> getAvailableTurnsByDoctorByDate(Doctor doctor, LocalDate date);
}
