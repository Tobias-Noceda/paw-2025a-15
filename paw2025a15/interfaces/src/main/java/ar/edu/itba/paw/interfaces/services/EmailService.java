package ar.edu.itba.paw.interfaces.services;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

public interface EmailService {

    public void sendTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendPatientCancellationEmails(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendDoctorCancellationEmails(User patient, User doctor, Appointment appointment, DoctorShift shift);
}
