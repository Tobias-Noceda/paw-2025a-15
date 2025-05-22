package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.AppointmentDao;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.AppointmentData;
import ar.edu.itba.paw.models.AppointmentId;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class AppointmentJpaDao implements AppointmentDao{

    @PersistenceContext
    private EntityManager em;

    @Override
    public Appointment addAppointment(DoctorShift shift, User patient, LocalDate date) {
        final Appointment app = new Appointment(shift, patient, date);
        em.persist(app);
        return app;
    }

    @Override
    public Optional<Appointment> getAppointmentsByShiftIdAndDate(long shiftId, LocalDate date) {
        return Optional.ofNullable(em.find(Appointment.class, new AppointmentId(shiftId, date)));
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByPatientId(long patientId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFutureAppointmentDataByPatientId'");
    }

    @Override
    public List<AppointmentData> getOldAppointmentDataByPatientId(long patientId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOldAppointmentDataByPatientId'");
    }

    @Override
    public List<AppointmentData> getFutureAppointmentDataByDoctorId(long doctorId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFutureAppointmentDataByDoctorId'");
    }

    @Override
    public List<Appointment> getAppointmentsForDate(LocalDate date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAppointmentsForDate'");
    }

    @Override
    public boolean removeAppointment(long shiftId, LocalDate date) {
        Appointment app = em.find(Appointment.class, new AppointmentId(shiftId, date));
        if(app==null) return false;
        em.remove(app);
        return true;
    }

    @Override
    public void clearRemovedAppointmentBeforeDate(LocalDate date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'clearRemovedAppointmentBeforeDate'");
    }
    
}
