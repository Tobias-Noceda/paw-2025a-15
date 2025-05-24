package ar.edu.itba.paw.interfaces.services;

import java.util.Locale;

import ar.edu.itba.paw.models.entities.Appointment;
import ar.edu.itba.paw.models.entities.DoctorShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;

public interface EmailService {

    public void sendDoctorTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);
    
    public void sendPatientTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendRecievedStudyEmail(User patient, User doctor, File file, Study study, String description);

    public void sendDoctorCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPatientCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendDoctorCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPatientCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPasswordResetEmail(User user, String token);

    public void sendPatientAppointmentReminderEmail(User patient, User doctor, Appointment appointment, DoctorShift shift, Locale locale);
    
    public void sendDoctorAppointmentReminderEmail(User patient, User doctor, Appointment appointment, DoctorShift shift, Locale locale);
}
