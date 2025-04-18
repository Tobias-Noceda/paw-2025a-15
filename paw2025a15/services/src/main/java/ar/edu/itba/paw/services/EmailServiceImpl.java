package ar.edu.itba.paw.services;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

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
import ar.edu.itba.paw.models.User;

@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    private final String emailFromString = "caretracehealth@gmail.com";

    private final String baseURL = "http://pawserver.it.itba.edu.ar/paw-2025a-15/";
    // private final String baseURL = "http://localhost:8080/webapp/";

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setFrom(emailFromString);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendSimpleMessageTemplate(String to, String subject, Map<String, Object> templateModel, String templateName) throws MessagingException{
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

    @Override
    @Async
    public void sendTestEmail() {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", "testName");
        templateModel.put("link", "http://pawserver.it.itba.edu.ar/paw-2025a-15/");
        try {
            sendSimpleMessageTemplate("testMail@gmail.com", "Test", templateModel, "testTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }





    @Override
    @Async
    public void sendTakenShiftEmail(User patient, User doctor, Appointment appointment, DoctorShift shift) {
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("homeLink", baseURL);
        templateModel.put("imageSource", baseURL + "resources/icono.jpg");
        templateModel.put("patientName", patient.getName());
        templateModel.put("doctorName", doctor.getName());
        templateModel.put("dateNumber", appointment.getDateNumber());
        templateModel.put("monthName", "April");
        templateModel.put("address", shift.getAddress());
        templateModel.put("startTime", shift.getStartTime().toString());
        templateModel.put("uploadLink", baseURL + "supersecret/upload/" + patient.getId() + "/" + doctor.getId());

        try {
            sendSimpleMessageTemplate(doctor.getEmail(), "Appointment Confirmation", templateModel, "appointmentConfirmationTemplate");
        } catch (MessagingException e) {
            // TODO catch
        }
    }
}
