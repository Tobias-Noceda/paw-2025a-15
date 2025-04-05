package ar.edu.itba.paw.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import ar.edu.itba.paw.interfaces.persistence.DoctorDetailDao;
import ar.edu.itba.paw.models.DoctorDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.itba.paw.interfaces.services.DoctorService;
import ar.edu.itba.paw.models.Doctor;
import ar.edu.itba.paw.models.Schedule;
import ar.edu.itba.paw.models.WeekdayEnum;

@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorDetailDao doctorDetailDao;


    @Override
    public Doctor create(final String name, final String email, final List<String> workingEnsurances, final String specialty, final Schedule schedules) {
        //doctorDetailDao.create(0, )
        return new Doctor(0, name, email, workingEnsurances, specialty, schedules);
    }

    @Override
    public Optional<Doctor> findById(long id) {
        return Optional.of(new Doctor(0, "Tobías Noceda", "tnoceda@itba.edu.ar", List.of("OSDE"), "Pediatría", null));
    }
}


//    @Override
//    public Optional<Doctor> findById(long id) {
//        final Doctor aux
//                //= new Doctor(0, "Tobías Noceda", "tnoceda@itba.edu.ar", List.of("OSDE"), "Pediatría", List.of()
////            new Schedule(WeekdayEnum.MONDAY, "08:00", "12:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.TUESDAY, "08:00", "12:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.WEDNESDAY, "08:00", "12:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.THURSDAY, "08:00", "12:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.FRIDAY, "08:00", "12:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.FRIDAY, "13:00", "15:00", "Iguazú 341", 4),
////            new Schedule(WeekdayEnum.TUESDAY, "15:00", "17:00", "Iguazú 341", 4)));
//
//        return Optional.of(aux);
//    }
//}
