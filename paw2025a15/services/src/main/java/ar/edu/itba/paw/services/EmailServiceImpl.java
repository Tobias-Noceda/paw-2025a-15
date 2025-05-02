package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Map;
import java.util.Locale;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.Appointment;
import ar.edu.itba.paw.models.DoctorShift;
import ar.edu.itba.paw.models.File;
import ar.edu.itba.paw.models.User;

@Service
public class EmailServiceImpl implements EmailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    private final String emailFromString = "caretracehealth@gmail.com";

    private final String baseURL = "http://pawserver.it.itba.edu.ar/paw-2025a-15/";

    private void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName, Locale locale) throws MessagingException{
        Context context = new Context(locale);
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process(templateName, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom(emailFromString);

        emailSender.send(message);
    }

    private void sendMessageWithFileTemplate(String to, String subject, Map<String, Object> templateModel, String templateName, byte[] file, String fileType, String fileName, Locale locale) throws MessagingException {
        Context context = new Context(locale);
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process(templateName, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom(emailFromString);
        helper.addAttachment(fileName, new ByteArrayDataSource(file, fileType));

        emailSender.send(message);
    }

    @Override
    @Async
    public void sendDoctorTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = doctor.getLocale().toLocale();
        
        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", patient.getEmail());
        templateModel.put("phone", patient.getTelephone());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("uploadLink", baseURL + "appointments/");

        String subject = messageSource.getMessage("takenShift.subject", null, locale);

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), subject, templateModel, "doctorAppointmentConfirmationTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending doctor taking shift email to {}: {}", doctor.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPatientTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = patient.getLocale().toLocale();

        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", doctor.getEmail());
        templateModel.put("phone", doctor.getTelephone());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "appointments/");
        
        String subject = messageSource.getMessage("takenShift.subject", null, locale);

        try {
            sendSimpleMessageTemplate(patient.getEmail(), subject, templateModel, "patientAppointmentConfirmationTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending patient taking shift email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendRecievedStudyEmail(User patient, User doctor, File file, String description, LocalDateTime dateTime) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("description", description);
        // TODO: replace with real watch file link
        templateModel.put("watchLink", baseURL);
        
        StringBuilder fileName = new StringBuilder();
        fileName.append("Study_")
            .append(patient.getName().replace(" ", "-"))
            .append("_")
            .append(dateTime.toString().replace(":", "-"))
            .append(".")
            .append(file.getType().getName().split("/")[1]);

        Locale locale = patient.getLocale().toLocale();
        String subject = messageSource.getMessage("receivedStudy.subject", null, locale);

        try {
            sendMessageWithFileTemplate(patient.getEmail(), subject, templateModel, "recievedStudyTemplate", file.getContent(), file.getType().getName(), fileName.toString(), locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending received study email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendDoctorCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = doctor.getLocale().toLocale();
        
        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", patient.getEmail());
        templateModel.put("phone", patient.getTelephone());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "appointments/");
        
        String subject = messageSource.getMessage("cancelledTurn.doctor.subject", null, locale);

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), subject, templateModel, "doctorCancelledAppointmentTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending doctor cancelled appointment email to {}: {}", doctor.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendDoctorCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = doctor.getLocale().toLocale();
        
        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "appointments/");
        
        String subject = messageSource.getMessage("cancelledConfirmation.doctor.subject", null, locale);

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), subject, templateModel, "doctorCancelledAppointmentConfirmationTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending doctor cancellation confirmation email to {}: {}", doctor.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPatientCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = patient.getLocale().toLocale();
        
        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", doctor.getEmail());
        templateModel.put("phone", doctor.getTelephone());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctors/" + doctor.getId());
        
        String subject = messageSource.getMessage("cancelledTurn.patient.subject", null, locale);

        try {
            sendSimpleMessageTemplate(patient.getEmail(), subject, templateModel, "patientCancelledAppointmentTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending patient cancelled appointment email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPatientCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());

        Locale locale = patient.getLocale().toLocale();
        
        String month = appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        templateModel.put("monthName", month);
        templateModel.put("address", shift.getAddress());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctors/" + doctor.getId());
        
        String subject = messageSource.getMessage("cancelledConfirmation.patient.subject", null, locale);

        try {
            sendSimpleMessageTemplate(patient.getEmail(), subject, templateModel, "patientCancelledAppointmentConfirmationTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending patient cancellation confirmation email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "supersecret/files/logo");
        templateModel.put("userName", user.getName());

        String token = UUID.randomUUID().toString(); // o algo más complejo
        //passwordRecoveryTokenService.saveTokenForUser(user.getId(), token); // persistir en DB con expiración opcional

        String recoveryLink = baseURL + "changePassword/" + token + "/" + user.getId();
        templateModel.put("resetLink", recoveryLink);

        Locale locale = user.getLocale().toLocale();
        String subject = messageSource.getMessage("passwordReset.subject", null, locale);


        try {
            sendSimpleMessageTemplate(user.getEmail(), subject, templateModel, "passwordRecoveryTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending user reset password email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }
}
