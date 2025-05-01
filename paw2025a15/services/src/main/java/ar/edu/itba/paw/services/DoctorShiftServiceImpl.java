package ar.edu.itba.paw.services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.persistence.DoctorShiftDao;
import ar.edu.itba.paw.interfaces.services.DoctorShiftService;
import ar.edu.itba.paw.models.AvailableTurn;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.WeekdayEnum;

@Service
public class DoctorShiftServiceImpl implements DoctorShiftService{

    private final DoctorShiftDao doctorShiftDao;

    @Autowired
    public DoctorShiftServiceImpl(final DoctorShiftDao doctorShiftDao){
        this.doctorShiftDao = doctorShiftDao;
    }

    @Override
    public void createShifts(long doctorId, List<WeekdayEnum> weekdays, String address, LocalTime startTime, LocalTime endTime, int slot) {
        long amount =  Duration.between(startTime,endTime).toMinutes()/slot;
        //long slot = Duration.between(startTime, endTime).toMinutes() / amount;
        for (WeekdayEnum weekday : weekdays) {
            for (int i = 1; i <= amount; i++) {
                doctorShiftDao.create(doctorId, weekday, address, startTime.plusMinutes(slot * (i-1)), startTime.plusMinutes(slot * i));
            }
        }
    }

    @Override
    public Optional<DoctorShift> getShiftById(long id) {
        return doctorShiftDao.getShiftById(id);
    }

    @Override
    public List<DoctorShift> getUnifiedShiftsByDoctorId(long doctorId) {
        List<DoctorShift> aux = doctorShiftDao.getShiftsByDoctorId(doctorId);
        
        if (aux == null || aux.isEmpty() || aux.size() == 1) return List.of();
        
        // Ordenar por día y luego por hora de inicio
        List<DoctorShift> sorted = aux.stream()
            .sorted(Comparator.comparing(DoctorShift::getWeekday)
            .thenComparing(DoctorShift::getStartTime))
            .collect(Collectors.toList());
        
        List<DoctorShift> toReturn = new ArrayList<>();
        DoctorShift current = sorted.get(0);
        DoctorShift end;

        for (int i = 1; i < sorted.size(); i++) {
            end = sorted.get(i);
            // Si es el mismo día y el turno actual termina cuando el próximo empieza
            if(current.getWeekday() == end.getWeekday() && current.getEndTime().equals(end.getStartTime())) {
                current = new DoctorShift(current.getId(), current.getDoctorId(), current.getWeekday(), current.getAddress(), current.getStartTime(), end.getEndTime());
            } else {
                toReturn.add(current);
                if(i < sorted.size()) {
                    current = sorted.get(i);
                }
            }
        }
        // Agregar el último turno
        toReturn.add(new DoctorShift(current.getId(), current.getDoctorId(), current.getWeekday(), current.getAddress(), current.getStartTime(), current.getEndTime()));

        return toReturn;
    }

    private List<AvailableTurn> getAvailableTurnsByDoctorIdAndDate(long doctorId, LocalDate date) {
        List<AvailableTurn> turns = new ArrayList<>();
        List<DoctorShift> shifts;
        if(date.isBefore(LocalDate.now())) return turns;
        else if(date.isEqual(LocalDate.now())) shifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDateTime(doctorId, WeekdayEnum.fromInt(date.getDayOfWeek().getValue()-1), date, LocalTime.now());
        else shifts = doctorShiftDao.getAvailableShiftsByDoctorIdWeekdayAndDate(doctorId, WeekdayEnum.fromInt(date.getDayOfWeek().getValue()-1), date);
        for (DoctorShift shift : shifts) {
            turns.add(new AvailableTurn(date, shift.getStartTime(), shift.getEndTime(), shift.getAddress(), shift.getId()));
        }
        return turns;
    }

    private List<AvailableTurn> getAvailableTurnsByDoctorIdBetweenDates(long doctorId, LocalDate startDate, LocalDate endDate) {
        List<AvailableTurn> allTurns = new ArrayList<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            List<AvailableTurn> turnsForDay = getAvailableTurnsByDoctorIdAndDate(doctorId, date);
            allTurns.addAll(turnsForDay);
        }
        return allTurns;
    }

    @Override
    public List<AvailableTurn> getAvailableTurnsByDoctorIdByMonthAndWeekNumber(long doctorId, Month month, int weekNumber) {
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

        return getAvailableTurnsByDoctorIdBetweenDates(doctorId, startOfWeek, endOfWeek);
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
}
