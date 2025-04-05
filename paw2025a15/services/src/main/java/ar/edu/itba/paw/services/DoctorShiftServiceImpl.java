package ar.edu.itba.paw.services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.AppointmentService;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private final DoctorShiftDao doctorShiftDao;

    private final AppointmentService as;

    @Autowired
    public DoctorShiftServiceImpl(final DoctorShiftDao doctorShiftDao, final AppointmentService as){
        this.doctorShiftDao = doctorShiftDao;
        this.as = as;
    }

    @Override
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, LocalTime startTime, LocalTime endTime) {
        return doctorShiftDao.create(doctorId, weekday, address, startTime, endTime);
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return doctorShiftDao.getShiftById(id);
    }

    @Override
    public List<DoctorShift> getShiftsByDoctorId(long doctorId) {
        return doctorShiftDao.getShiftsByDoctorId(doctorId);
    }
    
    @Override
    public List<DoctorShift> getShiftsByDoctorIdAndWeekday(long doctorId, WeekdayEnum weekday) {
        return doctorShiftDao.getShiftsByDoctorIdAndWeekday(doctorId, weekday);
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdAndDate(long doctorId, LocalDate date) {
        List<AvailableTurn> turns = new ArrayList<>();
        List<DoctorShift> shifts = getShiftsByDoctorIdAndWeekday(doctorId, WeekdayEnum.fromInt(date.getDayOfWeek().getValue()-1));//-1 cause our enum starts at cero
        
        for (DoctorShift shift : shifts) {
            if(as.getAppointmentsByShiftIdAndDate(doctorId, date).isEmpty()){
                turns.add(new AvailableTurn(date, shift.getStartTime(), shift.getEndTime(), shift.getAddress(), shift.getId()));
            }
        }
        return turns;
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate, LocalDate endDate) {
        List<AvailableTurn> allTurns = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<AvailableTurn> turnsForDay = getAvailableTurnsByDoctorIdAndDate(doctorId, date);
            allTurns.addAll(turnsForDay);
        }
        return allTurns;
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonth(long doctorId, Month month) {
        LocalDate now = LocalDate.now();
        int year = (now.getMonthValue() <= month.getValue()) ? now.getYear() : (now.getYear() + 1);
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = LocalDate.of(year, month, startOfMonth.lengthOfMonth());

        if(now.getMonth().equals(month)){
            return getAvailableTurnsByDoctorIdBetweenDates(doctorId, now, endOfMonth);
        }
        else{
            return getAvailableTurnsByDoctorIdBetweenDates(doctorId, startOfMonth, endOfMonth);
        }
    }


}
