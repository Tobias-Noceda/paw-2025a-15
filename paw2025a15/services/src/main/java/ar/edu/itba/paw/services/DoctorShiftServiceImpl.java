package ar.edu.itba.paw.services;

import java.time.LocalDate;
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
    public DoctorShift create(long doctorId, WeekdayEnum weekday, String address, int amount, String range) {
        return doctorShiftDao.create(doctorId, weekday, address, amount, range);
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
        //TODO: for date in month
            List<DoctorShift> shifts = getShiftsByDoctorIdAndWeekday(doctorId, WeekdayEnum.fromInt(date.getDayOfWeek().getValue()));
            for (DoctorShift shift : shifts) {
                List<Integer> takenAppIdx = as.getAppointmentIdxByShiftAndDate(shift.getId(), date);
                for(int i = 0; i < shift.getAmount(); i++){
                    if(!takenAppIdx.contains(i)) turns.add(new AvailableTurn(date, shift.getRange(), shift.getAddress(), shift.getId(), i));
                }
            }

        return turns;
    }

}
