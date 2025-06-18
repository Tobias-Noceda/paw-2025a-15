package ar.edu.itba.paw.services;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import ar.edu.itba.paw.interfaces.services.EmailService;
import ar.edu.itba.paw.models.entities.AppointmentNew;
import ar.edu.itba.paw.models.entities.Doctor;
import ar.edu.itba.paw.models.entities.DoctorSingleShift;
import ar.edu.itba.paw.models.entities.File;
import ar.edu.itba.paw.models.entities.Patient;
import ar.edu.itba.paw.models.entities.Study;
import ar.edu.itba.paw.models.entities.User;

@Service
public class EmailServiceImpl implements EmailService{

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MessageSource messageSource;

    @Value("${app.base-url}")
    private String baseURL;

    @Value("${app.email.from}")
    private String emailFromString;

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

    private void sendMessageWithFilesTemplate(String to, String subject, Map<String, Object> templateModel, String templateName, List<File> files, List<String> fileNames, Locale locale) throws MessagingException {
        Context context = new Context(locale);
        context.setVariables(templateModel);
        String htmlBody = templateEngine.process(templateName, context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.setFrom(emailFromString);
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            String fileName = fileNames.get(i);
            String fileType = file.getType().getName();
            helper.addAttachment(fileName, new ByteArrayDataSource(file.getContent(), fileType));
        }

        emailSender.send(message);
    }

    @Override
    @Async
    public void sendDoctorTakenShiftEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
        templateModel.put("detail", appointment.getDetail());
        String subject = messageSource.getMessage("takenShift.subject", null, locale);

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), subject, templateModel, "doctorAppointmentConfirmationTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending doctor taking shift email to {}: {}", doctor.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPatientTakenShiftEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
    public void sendRecievedStudyEmail(Patient patient, Doctor doctor, List<File> files, Study study, String description) {
        Map<String, Object> templateModel;
        templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("description", description);
        templateModel.put("studyLink", baseURL + "view-study/" + study.getId());
        
        List<String> studyNames = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);    
            StringBuilder fileName = new StringBuilder();
            fileName.append("Study_")
                .append(patient.getName().replace(" ", "-"))
                .append("_")
                .append(study.getUploadDate().toString().replace(":", "-"))
                .append("_")
                .append(i + 1)
                .append(".")
                .append(file.getType().getName().split("/")[1]);
            
            studyNames.add(fileName.toString());
        }

        Locale locale = patient.getLocale().toLocale();
        String subject = messageSource.getMessage("recievedStudy.subject", null, locale);

        try {
            sendMessageWithFilesTemplate(patient.getEmail(), subject, templateModel, "recievedStudyTemplate", files, studyNames, locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending recieved study email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendDoctorCancelledAppointmentEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
    public void sendDoctorCancellationConfirmationEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
    public void sendPatientCancelledAppointmentEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
    public void sendPatientCancellationConfirmationEmail(Patient patient, Doctor doctor, AppointmentNew appointment, DoctorSingleShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
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
    public void sendPasswordResetEmail(User user, String token) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
        templateModel.put("userName", user.getName());

        String recoveryLink = baseURL + "change-password/" + token + "/" + user.getId();
        templateModel.put("recoveryLink", recoveryLink);

        Locale locale = user.getLocale().toLocale();
        String subject = messageSource.getMessage("passwordReset.subject", null, locale);

        try {
            sendSimpleMessageTemplate(user.getEmail(), subject, templateModel, "passwordRecoveryTemplate", locale);
        } catch (MessagingException e) {
            LOGGER.error("Error sending user reset password email to {}: {}", user.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendPatientAppointmentReminderEmail(AppointmentNew appointment) {
        User patient = appointment.getPatient();
        Doctor doctor = appointment.getShift().getDoctor();
        DoctorSingleShift shift = appointment.getShift();
        
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        templateModel.put("monthName", appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, patient.getLocale().toLocale()));
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", doctor.getEmail());
        templateModel.put("phone", doctor.getTelephone());
        templateModel.put("shiftsLink", baseURL + "appointments/");

        String subject = messageSource.getMessage("reminder.subject", null, patient.getLocale().toLocale());

        try {
            sendSimpleMessageTemplate(patient.getEmail(), subject, templateModel, "patientAppointmentReminderTemplate", patient.getLocale().toLocale());
        } catch (MessagingException e) {
            LOGGER.error("Error sending patient reminder email to {}: {}", patient.getEmail(), e.getMessage(), e);
        }
    }

    @Override
    @Async
    public void sendDoctorAppointmentReminderEmail(AppointmentNew appointment) {
        User patient = appointment.getPatient();
        Doctor doctor = appointment.getShift().getDoctor();
        DoctorSingleShift shift = appointment.getShift();

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "logo");
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("patientName", patient.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        templateModel.put("monthName", appointment.getDate().getMonth().getDisplayName(TextStyle.FULL, doctor.getLocale().toLocale()));
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", patient.getEmail());
        templateModel.put("phone", patient.getTelephone());
        templateModel.put("uploadLink", baseURL + "appointments/");

        String subject = messageSource.getMessage("reminder.subject", null, doctor.getLocale().toLocale());

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), subject, templateModel, "doctorAppointmentReminderTemplate", doctor.getLocale().toLocale());
        } catch (MessagingException e) {
            LOGGER.error("Error sending doctor reminder email to {}: {}", doctor.getEmail(), e.getMessage(), e);
        }
    }
}
