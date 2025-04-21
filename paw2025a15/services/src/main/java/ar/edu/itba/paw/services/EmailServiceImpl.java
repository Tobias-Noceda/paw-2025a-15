package ar.edu.itba.paw.services;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
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

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private final String emailFromString = "caretracehealth@gmail.com";

    //private final String baseURL = "http://pawserver.it.itba.edu.ar/paw-2025a-15/";
    //private final String baseURL = "http://localhost:8080/webapp/";
    private final String baseURL = "http://localhost:8080/";

    private void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(emailFromString);
        message.setText(text);
        emailSender.send(message);
    }

    private void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException{
        Context context = new Context();
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

    private void sendMessageWithFileTemplate(String to, String subject, Map<String, Object> templateModel, String templateName, byte[] file, String fileType, String fileName) throws MessagingException {
        Context context = new Context();
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
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", patient.getEmail());
        // TODO: get phone
        templateModel.put("phone", "11 1234-5678");
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("uploadLink", baseURL + "supersecret/upload/" + patient.getId() + "/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), "Appointment Confirmation", templateModel, "doctorAppointmentConfirmationTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendPatientTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", doctor.getEmail());
        // TODO: get phone
        templateModel.put("phone", "11 1234-5678");
        templateModel.put("startTime", shift.getStartTime().toString());
        // TODO: real shifts list link
        templateModel.put("shiftsLink", baseURL);

        try {
            sendSimpleMessageTemplate(patient.getEmail(), "Appointment Confirmation", templateModel, "patientAppointmentConfirmationTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendRecievedStudyEmail(User patient, User doctor, File file, String description, LocalDateTime dateTime) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
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

        try {
            sendMessageWithFileTemplate(patient.getEmail(), "Recieved Study", templateModel, "recievedStudyTemplate", file.getContent(), file.getType().getName(), fileName.toString());
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendDoctorCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", patient.getEmail());
        // TODO: get phone
        templateModel.put("phone", "11 1234-5678");
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctorProfile/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), "Appointment Cancellation", templateModel, "doctorCancelledAppointmentTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendDoctorCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctorProfile/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), "Appointment Cancellation", templateModel, "doctorCancelledAppointmentConfirmationTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendPatientCancelledAppointmentEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();

        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("email", doctor.getEmail());
        // TODO: get phone
        templateModel.put("phone", "11 1234-5678");
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctorProfile/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(patient.getEmail(), "Appointment Cancellation", templateModel, "patientCancelledAppointmentTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendPatientCancellationConfirmationEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", "http://pawserver.it.itba.edu.ar/paw-2025a-15/resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        // TODO: get month name
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("shiftsLink", baseURL + "doctorProfile/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(patient.getEmail(), "Appointment Cancellation", templateModel, "patientCancelledAppointmentConfirmationTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(User user) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "/resources/icono.jpg");
        templateModel.put("userName", user.getName());

        String token = UUID.randomUUID().toString(); // o algo más complejo
        //passwordRecoveryTokenService.saveTokenForUser(user.getId(), token); // persistir en DB con expiración opcional

        String recoveryLink = baseURL + "changePassword/" + token + "/" + user.getId();
        templateModel.put("resetLink", recoveryLink);


        try {
            sendSimpleMessageTemplate(user.getEmail(), "Password Recovery", templateModel, "passwordRecoveryTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }
}
