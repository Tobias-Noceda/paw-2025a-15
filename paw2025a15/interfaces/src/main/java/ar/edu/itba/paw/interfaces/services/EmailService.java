package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDateTime;
import java.util.Locale;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

public interface EmailService {

    public void sendDoctorTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);
    
    public void sendPatientTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendRecievedStudyEmail(User patient, User doctor, File file, String description, LocalDateTime dateTime);

    public void sendDoctorCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPatientCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendDoctorCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPatientCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPasswordResetEmail(User user);
    void sendPatientAppointmentReminderEmail(User patient, User doctor, Appointment appointment, DoctorShift shift, Locale locale);
    void sendDoctorAppointmentReminderEmail(User patient, User doctor, Appointment appointment, DoctorShift shift, Locale locale);

}
