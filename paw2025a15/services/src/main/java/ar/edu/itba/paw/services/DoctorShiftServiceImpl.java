package ar.edu.itba.paw.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.DoctorDetailService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.interfaces.services.UserService;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;
import ar.edu.itba.paw.models.enums.WeekdayEnum;
import ar.edu.itba.paw.models.exceptions.NotFoundException;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private static final Logger LOGGER = LoggerFactory.getLogger(DoctorShiftServiceImpl.class);

    @Autowired
    private DoctorShiftDao doctorShiftDao;

    @Autowired
    private DoctorDetailService dds;

    @Autowired
    private UserService us;

    @Transactional
    @Override
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int slot) {
        if(!startTime.isBefore(endTime)) throw new IllegalArgumentException("Start time of a shift must be before the end time");
        User doctor = us.getUserById(doctorId).orElseThrow(()-> new NotFoundException("User with id: " + doctorId + " does not exist!"));//TODO:changed for hibernate check
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
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
    public List<DoctorShift> getUnifiedShiftsByDoctorId(long doctorId) {
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
        List<DoctorShift> shifts = doctorShiftDao.getShiftsByDoctorId(doctorId);
        
        if (shifts == null || shifts.isEmpty()) return Collections.emptyList();
        if(shifts.size() == 1) return shifts;
        
        List<DoctorShift> toReturn = new ArrayList<>();
        DoctorShift current = shifts.get(0);
        DoctorShift end;

        for (int i = 1; i < shifts.size(); i++) {
            end = shifts.get(i);
            // Si es el mismo día y el turno actual termina cuando el próximo empieza
            if(current.getWeekday() == end.getWeekday() && current.getEndTime().equals(end.getStartTime())) {
                current = new DoctorShift(current.getDoctor(), current.getWeekday(), current.getAddress(), current.getStartTime(), end.getEndTime());
            } else {
                toReturn.add(current);
                if(i < shifts.size()) {
                    current = shifts.get(i);
                }
            }
        }
        // Agregar el último turno
        toReturn.add(new DoctorShift(current.getDoctor(), current.getWeekday(), current.getAddress(), current.getStartTime(), current.getEndTime()));

        return toReturn;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonthAndWeekNumber(long doctorId, Month month, int weekNumber) {
        if(dds.getDetailByDoctorId(doctorId).isEmpty()) throw new NotFoundException("Doctor with id: " + doctorId + " does not exist!");
        LocalDate now = LocalDate.now();
        int year = (now.getMonthValue() <= month.getValue()) ? now.getYear() : (now.getYear() + 1);
        LocalDate startOfWeek;
        LocalDate endOfWeek;
        switch (weekNumber) {
            case 0 -> {
                startOfWeek = LocalDate.of(year, month, 1);
                endOfWeek = LocalDate.of(year, month, 7);
            }
            case 1 -> {
                startOfWeek = LocalDate.of(year, month, 8);
                endOfWeek = LocalDate.of(year, month, 14);
            }
            case 2 -> {
                startOfWeek = LocalDate.of(year, month, 15);
                endOfWeek = LocalDate.of(year, month, 21);
            }
            case 3 -> {
                startOfWeek = LocalDate.of(year, month, 22);
                endOfWeek = LocalDate.of(year, month, month.length(isLeapYear(year)));
            }
            default -> throw new IllegalArgumentException("Invalid week number: " + weekNumber);
        }
        if (now.getMonth().equals(month) && now.getDayOfMonth() > endOfWeek.getDayOfMonth()) {
            return List.of();
        }
        if (now.getMonth().equals(month) && now.getDayOfMonth() > startOfWeek.getDayOfMonth()) {
            startOfWeek = now;
        }

        return doctorShiftDao.getAvailableTurnsByDoctorIdBetweenDates(doctorId, startOfWeek, endOfWeek);
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
