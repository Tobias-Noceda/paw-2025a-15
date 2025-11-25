package ar.edu.itba.paw.interfaces.services;

import java.util.List;

import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;

public interface EmailService {

    public void sendDoctorTakenShiftEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);
    
    public void sendPatientTakenShiftEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);

    public void sendRecievedStudyEmail(Patient patient, Doctor doctor, List<File> files, Study study, String description);

    public void sendDoctorCancelledAppointmentEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);

    public void sendPatientCancelledAppointmentEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);

    public void sendDoctorCancellationConfirmationEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);

    public void sendPatientCancellationConfirmationEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift);

    public void sendPasswordResetEmail(User user, String token);

    public void sendPatientAppointmentReminderEmail(AppointmentNew appointment);
    
    public void sendDoctorAppointmentReminderEmail(AppointmentNew appointment);
}
