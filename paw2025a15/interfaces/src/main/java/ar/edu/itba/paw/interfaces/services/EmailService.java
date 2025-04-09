package ar.edu.itba.paw.interfaces.services;

import java.util.Map;

import javax.mail.MessagingException;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.User;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);

    public void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException;

    public void sendTestEmail();

    public void sendTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);
}
