package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorShiftServiceImpl.class);

    @Autowired
    private DoctorShiftDao doctorShiftDao;

    @Autowired
    private AppointmentService as;

    @Autowired
    private DoctorService ds;

    @Transactional
    @Override
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int slot) {
        if(startTime==null || endTime==null) throw new IllegalArgumentException("Start time and end time cannot be null");
        if(!startTime.isBefore(endTime)) throw new IllegalArgumentException("Start time of a shift must be before the end time");
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        List<DoctorSingleShift> shifts = new ArrayList<>();
        for (WeekdayEnum weekday : weekdays) {
            shifts.add(new DoctorSingleShift(
                doctor,
                weekday,
                address,
                startTime,
                endTime,
                slot
            ));
        }
        doctorShiftDao.doctorSetShifts(doctor, shifts);
        LOGGER.info("Successfully created {} doctor shifts for doctorId: {}", shifts.size(), doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DoctorSingleShift> getShiftById(long id) {
        return doctorShiftDao.getShiftById(id);
    }

    @Transactional
    @Override
    public void updateShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int amount, boolean keepTurns) {
        List<DoctorSingleShift> shifts = new ArrayList<>();
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        for (WeekdayEnum weekday : weekdays) {
            shifts.add(new DoctorSingleShift(
                doctor,
                weekday,
                address,
                startTime,
                endTime,
                amount
            ));
        }
        doctorShiftDao.updateShifts(doctorId, shifts);
        LOGGER.info("Updated shifts for doctor with id: {}", doctorId);

        if(!keepTurns){
            List<AppointmentNew> appointments = as.getFutureAppointmentDataByDoctorId(doctorId);
            for(AppointmentNew ap : appointments){
                as.cancelAppointment(ap.getId().getShiftId(),ap.getDate(),ap.getId().getStartTime(), ap.getId().getEndTime(), doctorId);
            }
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date) {
        Doctor doctor = ds.getDoctorById(doctorId).orElseThrow(() -> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        return doctorShiftDao.getAvailableTurnsByDoctorByDate(doctor, date);
    }
}
