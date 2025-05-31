package ar.edu.itba.paw.services;

import java.time.Duration;
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
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorShiftServiceImpl.class);

    @Autowired
    private DoctorShiftDao doctorShiftDao;

    @Autowired
    private UserService us;

    @Transactional
    @Override
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int slot) {
        if(!startTime.isBefore(endTime)) throw new IllegalArgumentException("Start time of a shift must be before the end time");
        Doctor doctor = us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));
        long amount =  Duration.between(startTime,endTime).toMinutes()/slot;
        //long slot = Duration.between(startTime, endTime).toMinutes() / amount;
        List<DoctorShift> shifts = new ArrayList<>();
        for (WeekdayEnum weekday : weekdays) {
            for (int i = 1; i <= amount; i++) {
                shifts.add(new DoctorShift(doctor, weekday, address, startTime.plusMinutes(slot * (i-1)), startTime.plusMinutes(slot * i)));
            }
        }
        int[] results = doctorShiftDao.batchCreate(shifts);
        for (int i = 0; i < results.length; i++) {
            if (results[i] == 0) {
                LOGGER.error("Failed to create doctor shift: {}", shifts.get(i));
                throw new RuntimeException("Batch insert failed for shift: " + shifts.get(i));
            }
        }
        LOGGER.info("Successfully created {} doctor shifts for doctorId: {}", shifts.size(), doctorId);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return doctorShiftDao.getShiftById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByDate(long doctorId, LocalDate date) {
        us.getDoctorById(doctorId).orElseThrow(()-> new NotFoundException("Doctor with id: " + doctorId + " does not exist!"));

        return doctorShiftDao.getAvailableTurnsByDoctorIdByDate(doctorId, date);
    }
}
