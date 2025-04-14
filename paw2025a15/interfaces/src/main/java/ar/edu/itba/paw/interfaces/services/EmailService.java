package ar.edu.itba.paw.interfaces.services;

import java.time.LocalDateTime;
import java.util.Map;

import javax.mail.MessagingException;

import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

public interface EmailService {
    public void sendSimpleMessage(String to, String subject, String text);

    public void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException;

    public void sendMessageWithFileTemplate(String to, String subject, Map<String, Object> templateModel, String templateName, byte[] file, String fileType, String fileName) throws MessagingException;

    public void sendTestEmail();

    public void sendDoctorTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);
    
    public void sendPatientTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift);

    public void sendRecievedStudyEmail(User patient, User doctor, File file, String description, LocalDateTime dateTime);
}
