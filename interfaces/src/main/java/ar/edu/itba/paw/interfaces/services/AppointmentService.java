package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.enums.AppointmentStatusEnum;
import ar.edu.itba.paw.models.utils.Pair;

public interface AppointmentService {//TODO check deprecated

    public AppointmentNew addAppointment(long shiftId, long patientId, LocalDate date, LocalTime startTime, LocalTime endTime, String detail);

    public Pair<AppointmentNew, AppointmentStatusEnum> getAppointmentByShiftIdDateAndTime(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime);

    @Deprecated
    public List<AppointmentNew> getOldAppointmentDataByPatientId(long patientId);

    public List<AppointmentNew> getOldAppointmentDataPageByPatientId(long patientId, int page, int pageSize);

    public Integer getOldAppointmentTotalByPatientId(long patientId);

    public List<AppointmentNew> getFutureAppointmentDataByDoctorId(long doctorId);

    public List<AppointmentNew> getFutureAppointmentDataPageByUserId(long userId, int page, int pageSize);

    public Integer getFutureAppointmentTotalByUserId(long userId);

    public AppointmentNew cancelAppointment(long shiftId, LocalDate date, LocalTime startTime, LocalTime endTime, long cancellerId);

    @Deprecated
    public void removeAppointment(long shiftId, LocalDate date, long doctorId, LocalTime startTime, LocalTime endTime);

    @Deprecated
    public  void cancelAppointmentRange(long doctorId, LocalDate startDate, LocalDate endDate);

    public List<AppointmentNew> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date);
}
